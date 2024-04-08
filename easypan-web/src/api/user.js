import { http } from '@/utils/axios'
import { ContentTypeEnum } from '@/enums/httpEnum'

/**
 * @description: 发送邮箱验证码
 */
export function sendEmailCode(param) {
  return http.request({
    url: '/sendEmailCode',
    method: 'post',
    data: param,
    headers: { 'Content-Type': ContentTypeEnum.FORM_URLENCODED }
  })
}

/**
 * @description: 注册
 */
export function register(data) {
  return http.request({
    url: '/register',
    method: 'post',
    data
  })
}

/**
 * @description: 登陆
 */
export function login(data) {
  return http.request({
    url: '/login',
    method: 'post',
    data
  })
}

/**
 * @description: 获取用户网盘空间
 */
export function getUseSpaceApi() {
  return http.request({
    url: '/getUseSpace',
    method: 'GET'
  })
}

/**
 * @description: 找回密码
 */
export function resetPwd(data) {
  return http.request({
    url: '/resetPwd',
    method: 'post',
    data
  })
}

/**
 * @description: 修改头像
 */
export function updateUserAvatar(formData) {
  return http.request(
    {
      url: '/updateUserAvatar',
      method: 'post',
      params: formData,
      headers: { 'Content-Type': ContentTypeEnum.FORM_DATA }
    },
    {
      successMessageText: '修改成功'
    }
  )
}

/**
 * @description: 修改密码
 */
export function updatePassword(param) {
  return http.request(
    {
      url: '/updatePassword',
      method: 'post',
      data: param,
      headers: { 'Content-Type': ContentTypeEnum.FORM_URLENCODED }
    },
    {
      successMessageText: '密码修改成功'
    }
  )
}

/**
 * @description: 登出
 */
export function logout() {
  return http.request({
    url: '/logout',
    method: 'post'
  })
}
