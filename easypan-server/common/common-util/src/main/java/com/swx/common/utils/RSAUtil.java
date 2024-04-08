package com.swx.common.utils;

import com.swx.common.pojo.BizException;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class RSAUtil {

    public static final String UTF_8 = "UTF-8";
    public static final String RSA_ALGORITHM_NO_PADDING = "RSA";
    private static final String PUBLIC_KEY = "RSAPublicKey";
    private static final String PRIVATE_KEY = "RSAPrivateKey";

    /**
     * 密文解密
     *
     * @param privateKeyStr 私钥
     * @param data          秘文
     * @return 明文
     */
    public static String decrypt(String privateKeyStr, String data) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM_NO_PADDING);
            byte[] privateKeyArray = privateKeyStr.getBytes();
            byte[] dataArray = data.getBytes();
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKeyArray));
            PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM_NO_PADDING);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new String(cipher.doFinal(Base64.decodeBase64(dataArray)), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new BizException("秘文解析失败");
        }
    }

    //获得公钥
    public static String getPublicKey(Map<String, Object> keyMap) throws Exception {
        //获得map中的公钥对象 转为key对象
        Key key = (Key) keyMap.get(PUBLIC_KEY);
        //byte[] publicKey = key.getEncoded();
        //编码返回字符串
        return encryptBASE64(key.getEncoded());
    }

    //获得私钥
    public static String getPrivateKey(Map<String, Object> keyMap) throws Exception {
        //获得map中的私钥对象 转为key对象
        Key key = (Key) keyMap.get(PRIVATE_KEY);
        //byte[] privateKey = key.getEncoded();
        //编码返回字符串
        return encryptBASE64(key.getEncoded());
    }

    //解码返回byte
    public static byte[] decryptBASE64(String key) throws Exception {
        return Base64.decodeBase64(key);
    }

    //编码返回字符串
    public static String encryptBASE64(byte[] key) throws Exception {
        return Base64.encodeBase64String(key);
    }

    //map对象中存放公私钥
    public static Map<String, Object> initKey() {
        try {
            //获得对象 KeyPairGenerator 参数 RSA 1024个字节
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(RSA_ALGORITHM_NO_PADDING);
            keyPairGen.initialize(1024);
            //通过对象 KeyPairGenerator 获取对象KeyPair
            KeyPair keyPair = keyPairGen.generateKeyPair();

            //通过对象 KeyPair 获取RSA公私钥对象RSAPublicKey RSAPrivateKey
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
            //公私钥对象存入map中
            Map<String, Object> keyMap = new HashMap<String, Object>(2);
            keyMap.put(PUBLIC_KEY, publicKey);
            keyMap.put(PRIVATE_KEY, privateKey);
            return keyMap;
        } catch (NoSuchAlgorithmException e) {
            throw new BizException("密钥生成错误");
        }
    }

    public static void main(String[] args) {
        Map<String, Object> keyMap;
        try {
            keyMap = initKey();
            String publicKey = getPublicKey(keyMap);
            System.out.println(publicKey);
            String privateKey = getPrivateKey(keyMap);
            System.out.println(privateKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}