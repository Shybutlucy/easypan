// axios配置  可自行根据项目进行更改，只需更改该文件即可，其他文件可以不动
import { ContentTypeEnum, RequestEnum, ResultEnum } from '@/enums/httpEnum'
import { PageEnum } from '@/enums/pageEnum'
import router from '@/router'
import VueCookie from 'vue-cookies'
import { isString, isUrl } from '@/utils/is'
import { formatRequestDate, joinTimestamp } from './helper'
import { setObjToUrlParams } from '@/utils/urlUtils'
// import { useUserStoreWidthOut } from '@/store/modules/user';
import axios from 'axios'
import { VAxios } from './Axios'
import { deepMerge } from '@/utils'
import { checkStatus } from './checkStatus'
import { ElMessageBox, ElMessage } from 'element-plus'
import { useGlobalSetting } from '@/hooks/setting'

const globSetting = useGlobalSetting()
const urlPrefix = globSetting.urlPrefix || ''

/**
 * @description: 数据处理，方便区分多种处理方式
 */
const transform = {
  /**
   * @description: 处理请求数据
   */
  transformRequestData: (res, options) => {
    const {
      isShowMessage = true,
      isShowErrorMessage,
      isShowSuccessMessage,
      successMessageText,
      errorMessageText,
      isTransformResponse,
      isReturnNativeResponse
    } = options
    // 是否返回原生响应头 比如：需要获取响应头时使用该属性
    if (isReturnNativeResponse) {
      return res
    }

    // 不进行任何处理，直接返回
    // 用于页面代码可能需要直接获取code，data，message这些信息时开启
    if (!isTransformResponse) {
      return res.data
    }

    const { data } = res

    const $dialog = ElMessageBox
    const $message = ElMessage

    if (!data) {
      // return '[HTTP] Request has no return value';
      throw new Error('请求出错，请稍候重试')
    }
    //  这里 code，data，message为 后台统一的字段，需要修改为项目自己的接口返回格式
    const { code, data: result, message } = data
    // 请求成功
    const hasSuccess = data && Reflect.has(data, 'code') && code === ResultEnum.SUCCESS
    // 是否显示提示信息
    if (isShowMessage) {
      if (hasSuccess && (successMessageText || isShowSuccessMessage)) {
        // 是否显示自定义信息提示
        $message.success({
          type: 'success',
          message: successMessageText || message || '操作成功！'
        })
      } else if (!hasSuccess && (errorMessageText || isShowErrorMessage)) {
        // 是否显示自定义信息提示
        $message.error(message || errorMessageText || '操作失败！')
      } else if (!hasSuccess && options.errorMessageMode === 'modal') {
        // errorMessageMode=‘custom-modal’的时候会显示modal错误弹窗，而不是消息提示，用于一些比较重要的错误
        $dialog.alert(message, '提示', {
          confirmButtonText: '确定'
        })
      }
    }

    // 接口请求成功，直接返回结果
    if (code === ResultEnum.SUCCESS) {
      return result
    }
    // 接口请求错误，统一提示错误信息 这里逻辑可以根据项目进行修改
    let errMsg = message
    switch (code) {
      // 请求失败
      case ResultEnum.BUSSINESS_ERROR:
      case ResultEnum.SYSTEM_ERROR:
      case ResultEnum.PARAM_INVALID:
      case ResultEnum.OUT_OF_SPACE:
      case RequestEnum.PARAM_INVALID:
        $message.error(errMsg)
        break
      case ResultEnum.TOKEN_TIMEOUT: {
        const LoginName = PageEnum.BASE_LOGIN_NAME
        const LoginPath = PageEnum.BASE_LOGIN
        if (router.currentRoute.value?.name === LoginName) return
        // 到登陆页
        errMsg = '登录超时，请重新登录!'
        $dialog.alert('登录身份已失效，请重新登录!', '提示', {
          showClose: false,
          confirmButtonText: '确定',
          callback: () => {
            VueCookie.remove('userInfo')
            window.location.href = LoginPath
          }
        })
        break
      }
    }
    throw new Error(errMsg)
  },

  // 请求之前处理config
  beforeRequestHook: (config, options) => {
    const { apiUrl, joinPrefix, joinParamsToUrl, formatDate, joinTime = true, urlPrefix } = options

    const isUrlStr = isUrl(config.url)

    if (!isUrlStr && joinPrefix) {
      config.url = `${urlPrefix}${config.url}`
    }

    if (!isUrlStr && apiUrl && isString(apiUrl)) {
      config.url = `${apiUrl}${config.url}`
    }
    const params = config.params || {}
    const data = config.data || false
    if (config.method?.toUpperCase() === RequestEnum.GET) {
      if (!isString(params)) {
        // 给 get 请求加上时间戳参数，避免从缓存中拿数据。
        config.params = Object.assign(params || {}, joinTimestamp(joinTime, false))
      } else {
        // 兼容restful风格
        config.url = config.url + params + `${joinTimestamp(joinTime, true)}`
        config.params = undefined
      }
    } else {
      if (!isString(params)) {
        formatDate && formatRequestDate(params)
        if (Reflect.has(config, 'data') && config.data && Object.keys(config.data).length > 0) {
          config.data = data
          config.params = params
        } else {
          config.data = params
          config.params = undefined
        }
        if (joinParamsToUrl) {
          config.url = setObjToUrlParams(config.url, Object.assign({}, config.params, config.data))
        }
      } else {
        // 兼容restful风格
        config.url = config.url + params
        config.params = undefined
      }
    }
    return config
  },

  /**
   * @description: 请求拦截器处理
   */
  requestInterceptors: (config, options) => {
    // 请求之前处理config
    // const userStore = useUserStoreWidthOut();
    // const token = userStore.getToken;
    const token = ''
    if (token && config?.requestOptions?.withToken !== false) {
      // jwt token
      config.headers.Authorization = options.authenticationScheme
        ? `${options.authenticationScheme} ${token}`
        : token
    }
    return config
  },

  /**
   * @description: 响应错误处理
   */
  responseInterceptorsCatch: (error) => {
    const $dialog = ElMessageBox
    const $message = ElMessage
    const { response, code, message } = error || {}
    // TODO 此处要根据后端接口返回格式修改
    const msg = response && response.data && response.data.message ? response.data.message : ''
    const err = error.toString()
    try {
      if (code === 'ECONNABORTED' && message.indexOf('timeout') !== -1) {
        $message.error('接口请求超时，请刷新页面重试!')
        return
      }
      if (err && err.includes('Network Error')) {
        $dialog.info({
          title: '网络异常',
          message: '请检查您的网络连接是否正常',
          positiveText: '确定',
          //negativeText: '取消',
          closable: false,
          maskClosable: false,
          onPositiveClick: () => {},
          onNegativeClick: () => {}
        })
        return Promise.reject(error)
      }
    } catch (error) {
      throw new Error(error)
    }
    // 请求是否被取消
    const isCancel = axios.isCancel(error)
    if (!isCancel) {
      checkStatus(error.response && error.response.status, msg)
    } else {
      console.log(error, '请求被取消！')
    }
    //return Promise.reject(error);
    return Promise.reject(response?.data)
  }
}

function createAxios(opt) {
  return new VAxios(
    deepMerge(
      {
        timeout: 10 * 1000,
        authenticationScheme: '',
        // 接口前缀
        prefixUrl: urlPrefix,
        headers: { 'Content-Type': ContentTypeEnum.JSON },
        // 数据处理方式
        transform,
        // 配置项，下面的选项都可以在独立的接口请求中覆盖
        requestOptions: {
          // 默认将prefix 添加到url
          joinPrefix: true,
          // 是否返回原生响应头 比如：需要获取响应头时使用该属性
          isReturnNativeResponse: false,
          // 需要对返回数据进行处理
          isTransformResponse: true,
          // post请求的时候添加参数到url
          joinParamsToUrl: false,
          // 格式化提交参数时间
          formatDate: true,
          // 消息提示类型
          errorMessageMode: 'none',
          // 接口地址
          apiUrl: '',
          // 接口拼接地址
          urlPrefix: urlPrefix,
          //  是否加入时间戳
          joinTime: true,
          // 忽略重复请求
          ignoreCancelToken: true,
          // 是否携带token
          withToken: false
        },
        withCredentials: false
      },
      opt || {}
    )
  )
}

export const http = createAxios()
