<template>
  <div class="login-body">
    <div class="bg"></div>
    <div class="login-panel">
      <el-form :model="formData" :rules="rules" ref="loginFormRef" class="login-register">
        <div class="login-title">Easy云盘</div>
        <el-form-item prop="email">
          <el-input
            clearable
            placeholder="请输入邮箱"
            v-model.trim="formData.email"
            :prefix-icon="MailOutlined"
            maxLength="150"
          />
        </el-form-item>
        <el-form-item prop="password" v-if="opType == 1">
          <el-input
            show-password
            type="password"
            placeholder="请输入密码"
            v-model.trim="formData.password"
            :prefix-icon="LockOutlined"
            maxLength="20"
          />
        </el-form-item>
        <!-- 注册 -->
        <div v-if="opType == 0 || opType == 2">
          <el-form-item prop="emailCode">
            <div class="send-email-panel">
              <el-input
                placeholder="请输入邮箱验证码"
                v-model.trim="formData.emailCode"
                :prefix-icon="SafetyCertificateOutlined"
              />
              <el-button class="send-mail-btn" type="primary" @click="getEmailCode"
                >获取邮箱验证码</el-button
              >
            </div>
            <el-popover placement="left" :width="500" trigger="click">
              <div>
                <p>1、在垃圾箱中查找邮箱验证码</p>
                <p>2、在邮箱中头像->设置->反垃圾->白名单->设置邮箱地址白名单</p>
                <p>3、将邮箱【2627.311935@qq.com】添加到白名单</p>
              </div>
              <template #reference>
                <el-link type="primary" :underline="false">未收到邮箱验证码？</el-link>
              </template>
            </el-popover>
          </el-form-item>
          <el-form-item prop="nickname" v-if="opType == 0">
            <el-input
              placeholder="请输入昵称"
              v-model.trim="formData.nickname"
              :prefix-icon="UserOutlined"
              maxLength="20"
            />
          </el-form-item>
          <!-- 注册密码，找回密码 -->
          <el-form-item prop="registerPassword">
            <el-input
              show-password
              type="password"
              placeholder="请输入密码"
              v-model.trim="formData.registerPassword"
              :prefix-icon="LockOutlined"
              maxLength="20"
            />
          </el-form-item>
          <el-form-item prop="reRegisterPassword">
            <el-input
              show-password
              type="password"
              placeholder="请再次输入密码"
              v-model.trim="formData.reRegisterPassword"
              :prefix-icon="LockOutlined"
              maxLength="20"
            />
          </el-form-item>
        </div>
        <el-form-item prop="checkCode">
          <div class="check-code-panel">
            <el-input
              placeholder="请输入验证码"
              v-model.trim="formData.checkCode"
              :prefix-icon="SafetyCertificateOutlined"
              @keyup.enter="doSubmit"
            />
            <img :src="checkCodeUrl" class="check-code" @click="changeCheckCode(0)" />
          </div>
        </el-form-item>
        <!-- 登陆 -->
        <el-form-item v-if="opType == 1">
          <!-- <div class="rememberme-panel">
            <el-checkbox v-model="formData.rememberMe" label="记住我" />
          </div> -->
          <div class="no-account">
            <el-link type="primary" :underline="false" @click="showPanel(2)">忘记密码？</el-link>
            <el-link type="primary" :underline="false" @click="showPanel(0)">没有账号？</el-link>
          </div>
        </el-form-item>
        <!-- 找回密码 -->
        <el-form-item v-if="opType == 2">
          <div class="no-account">
            <el-link type="primary" :underline="false" @click="showPanel(1)">去登陆</el-link>
          </div>
        </el-form-item>
        <!-- 注册 -->
        <el-form-item v-if="opType == 0">
          <div class="no-account">
            <el-link type="primary" :underline="false" @click="showPanel(1)">已有账号？</el-link>
          </div>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" style="width: 100%" @click="doSubmit">
            <span v-if="opType == 0">注册</span>
            <span v-if="opType == 1">登陆</span>
            <span v-if="opType == 2">重置密码</span>
          </el-button>
        </el-form-item>
        <div class="login-btn-qq" v-if="opType == 1">
          快捷登陆 <img src="https://passport.baidu.com/passApi/img/bd-acc-qzone.png" />
        </div>
      </el-form>
    </div>
    <Dialog
      :show="dialogConfig4SendMailCode.show"
      :title="dialogConfig4SendMailCode.title"
      :buttons="dialogConfig4SendMailCode.buttons"
      width="500px"
      :showCancel="false"
      @close="dialogConfig4SendMailCode.show = false"
    >
      <el-form
        :model="formData4SendMailCode"
        :rules="rules"
        label-width="80px"
        ref="formRef4SendMailCode"
      >
        <el-form-item label="邮箱">
          {{ formData.email }}
        </el-form-item>
        <el-form-item label="验证码" prop="checkCode">
          <div class="check-code-panel">
            <el-input
              placeholder="请输入验证码"
              v-model.trim="formData4SendMailCode.checkCode"
              :prefix-icon="SafetyCertificateOutlined"
            />
            <img :src="checkCodeUrl4SendMailCode" class="check-code" @click="changeCheckCode(1)" />
          </div>
        </el-form-item>
      </el-form>
    </Dialog>
  </div>
</template>

<script setup>
import { nextTick, reactive, ref, onMounted } from 'vue'
import Dialog from '@/components/Dialog.vue'
import Verify from '@/utils/Verify'
import { sendEmailCode, register, login, resetPwd } from '@/api/user.js'
import { ElMessage } from 'element-plus'
import VueCookie from 'vue-cookies'
import { PageEnum } from '@/enums/pageEnum'
import { UserOutlined, MailOutlined, LockOutlined, SafetyCertificateOutlined } from '@vicons/antd'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const LOGIN_NAME = PageEnum.BASE_LOGIN_NAME
const api = {
  checkCode: '/api/checkCode'
}
const checkRePassword = (rule, value, callback) => {
  if (value !== formData.value.registerPassword) {
    callback(new Error(rule.message))
  } else {
    callback()
  }
}
// 操作类型：0:注册，1:登陆，2:重置密码
const opType = ref(1)
const loginFormRef = ref()
const formData = ref({
  email: ''
})
const rules = reactive({
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { validator: Verify.email, message: '请输入正确的邮箱', trigger: 'blur' }
  ],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  registerPassword: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { validator: Verify.password, message: '密码只能是数字, 字母, 特殊字符8-18位', trigger: 'blur' }
  ],
  reRegisterPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: checkRePassword, message: '两次密码不一致', trigger: 'blur' }
  ],
  checkCode: [{ required: true, message: '请输入图片验证码' }],
  emailCode: [{ required: true, message: '请输入邮箱验证码' }]
})
// 发送邮箱验证码
const formData4SendMailCode = ref({})
const formRef4SendMailCode = ref()
const dialogConfig4SendMailCode = reactive({
  show: false,
  title: '发送邮箱验证码',
  buttons: [
    {
      type: 'primary',
      text: '发送验证码',
      click: (e) => {
        sendMailCode()
      }
    }
  ]
})

const checkCodeUrl = ref(api.checkCode)
const checkCodeUrl4SendMailCode = ref(api.checkCode)

onMounted(() => {
  showPanel(1)
  formData.value = {
    email: '2627311935@qq.com',
    password: 'Song@Wu.code06'
  }
})

const showPanel = (type) => {
  opType.value = type
  resetForm()
}
// 刷新验证码
function changeCheckCode(type) {
  const url = api.checkCode + '?type=' + type + '&time=' + new Date().getTime()
  if (type == 0) {
    checkCodeUrl.value = url
  } else {
    checkCodeUrl4SendMailCode.value = url
  }
}
// 获取邮箱验证码
function getEmailCode() {
  loginFormRef.value.validateField('email', (valid) => {
    if (!valid) {
      return
    }
    dialogConfig4SendMailCode.show = true
    nextTick(() => {
      changeCheckCode(1)
      formRef4SendMailCode.value.resetFields()
      formData4SendMailCode.value = {
        email: formData.value.email
      }
    })
  })
}
// 重置表单
const resetForm = () => {
  // 刷新验证码
  changeCheckCode(0)
  loginFormRef.value.resetFields()
  formData.value = {}
}
// 发送邮箱验证码
const sendMailCode = () => {
  formRef4SendMailCode.value.validate(async (valid) => {
    if (!valid) {
      return
    }
    const param = Object.assign({}, formData4SendMailCode.value)
    param.type = opType.value == 0 ? 0 : 1 // 0:注册 1:找回密码
    try {
      await sendEmailCode(param)
      ElMessage.success('验证码发送成功，请登陆邮箱查看')
      dialogConfig4SendMailCode.show = false
    } catch (error) {
      // 刷新验证码
      changeCheckCode(1)
    }
  })
}
// 登陆，注册，重置密码提交表单
const doSubmit = () => {
  loginFormRef.value.validate(async (valid) => {
    if (!valid) {
      return
    }
    let params = {}
    Object.assign(params, formData.value)
    // 注册 找回密码
    if (opType.value == 0 || opType.value == 2) {
      params.password = params.reRegisterPassword
      delete params.reRegisterPassword
      delete params.registerPassword
    }
    try {
      switch (opType.value) {
        case 0: {
          await register(params)
          ElMessage.success('注册成功，请登陆')
          showPanel(1)
          break
        }
        case 1: {
          const res = await login(params)
          ElMessage.success('登陆成功')
          // 存储cookie
          VueCookie.set('userInfo', JSON.stringify(res), 0)
          // 重定向到原始页面
          const toPath = decodeURIComponent(route.query?.redirect || '/')
          if (route.name == LOGIN_NAME) {
            router.replace('/')
          } else router.replace(toPath)
          break
        }
        case 2: {
          await resetPwd(params)
          ElMessage.success('密码重置成功，请登陆')
          showPanel(1)
          break
        }
      }
    } catch (error) {
      changeCheckCode(0)
    }
  })
}
</script>

<style lang="scss" scoped>
.login-body {
  height: calc(100vh);
  background-size: cover;
  // background-color: rgb(231, 226, 226);
  background-color: rgb(234, 241, 254);
  display: flex;
  .bg {
    flex: 1;
    background-size: 800px;
    background-position: center;
    background-repeat: no-repeat;
    background-image: url('../assets/login_img_02.jpg');
    // margin-bottom: 100px;
  }

  .login-panel {
    width: 360px;
    margin-right: 15%;
    margin-top: calc((100vh - 500px) / 2);

    .login-register {
      padding: 25px;
      background: #fff;
      border-radius: 5px;

      .login-title {
        text-align: center;
        font-size: 18px;
        font-weight: bold;
        margin-bottom: 20px;
      }
      .send-email-panel {
        display: flex;
        width: 100%;
        justify-content: space-between;
        .send-mail-btn {
          margin-left: 5px;
        }
      }
      .rememberme-panel {
        width: 100%;
      }
      .no-account {
        width: 100%;
        display: flex;
        justify-content: space-between;
      }
      .login-btn-qq {
        width: 100%;
        text-align: center;
        font-size: 14px;
        color: gray;
        display: flex;
        align-items: center;
        justify-content: center;
        img {
          height: 24px;
          margin-left: 10px;
        }
      }
    }
  }
  .check-code-panel {
    width: 100%;
    display: flex;
    .check-code {
      margin-left: 5px;
      cursor: pointer;
      height: 30px;
    }
  }
}
.el-link .el-icon--right.el-icon {
  vertical-align: text-bottom;
}
</style>
