import axios from 'axios'
import { cloneDeep } from 'lodash-es'
import { isFunction } from '@/utils/is'
import { ContentTypeEnum } from '@/enums/httpEnum'
import { AxiosCanceler } from './axiosCancel'

/**
 * @description:  axios模块
 */
export class VAxios {
  #axiosInstance
  #options
  constructor(options) {
    this.#axiosInstance = axios.create(options)
    this.#options = options
    this.#setupInterceptors()
  }

  getAxios() {
    return this.#axiosInstance
  }

  /**
   * @description: 重新配置axios
   */
  configAxios(config) {
    if (!this.#axiosInstance) {
      return
    }
    this.#createAxios(config)
  }

  /**
   * @description: 设置通用header
   */
  setHeader(headers) {
    if (!this.#axiosInstance) {
      return
    }
    Object.assign(this.#axiosInstance.defaults.headers, headers)
  }

  /**
   * @description:  创建axios实例
   */
  #createAxios(config) {
    this.#axiosInstance = axios.create(config)
  }

  #getTransform() {
    const { transform } = this.#options
    return transform
  }

  /**
   * @description: 请求方法
   */
  request(config, options) {
    let conf = cloneDeep(config)
    const transform = this.#getTransform()

    const { requestOptions } = this.#options

    // 合并请求配置项
    const opt = Object.assign({}, requestOptions, options)

    const { beforeRequestHook, requestCatch, transformRequestData } = transform || {}
    if (beforeRequestHook && isFunction(beforeRequestHook)) {
      // 请求之前处理config
      conf = beforeRequestHook(conf, opt)
    }

    // 重新赋值 赋值成最新的配置
    conf.requestOptions = opt

    return new Promise((resolve, reject) => {
      this.#axiosInstance
        .request(conf)
        .then((res) => {
          // 请求是否被取消
          const isCancel = axios.isCancel(res)
          if (transformRequestData && isFunction(transformRequestData) && !isCancel) {
            try {
              const ret = transformRequestData(res, opt)
              resolve(ret)
            } catch (err) {
              reject(err || new Error('request error!'))
            }
            return
          }
          resolve(res)
        })
        .catch((e) => {
          if (requestCatch && isFunction(requestCatch)) {
            reject(requestCatch(e))
            return
          }
          reject(e)
        })
    })
  }

  /**
   * @description:  文件上传
   */
  uploadFile(config, params) {
    const formData = new window.FormData()
    const customFilename = params.name || 'file'

    if (params.filename) {
      formData.append(customFilename, params.file, params.filename)
    } else {
      formData.append(customFilename, params.file)
    }

    if (params.data) {
      Object.keys(params.data).forEach((key) => {
        const value = params.data[key]
        if (Array.isArray(value)) {
          value.forEach((item) => {
            formData.append(`${key}[]`, item)
          })
          return
        }

        formData.append(key, params.data[key])
      })
    }

    return this.#axiosInstance.request({
      method: 'POST',
      data: formData,
      headers: {
        'Content-Type': ContentTypeEnum.FORM_DATA,
        ignoreCancelToken: true
      },
      ...config
    })
  }

  /**
   * @description: 拦截器配置
   */
  #setupInterceptors() {
    // transform会在创建Axios时，被封装进options被传进来
    const transform = this.#getTransform()
    if (!transform) {
      return
    }
    const {
      requestInterceptors,
      requestInterceptorsCatch,
      responseInterceptors,
      responseInterceptorsCatch
    } = transform

    const axiosCanceler = new AxiosCanceler()

    // 请求拦截器配置处理
    this.#axiosInstance.interceptors.request.use((config) => {
      const {
        headers: { ignoreCancelToken }
      } = config
      const ignoreCancel =
        ignoreCancelToken !== undefined
          ? ignoreCancelToken
          : this.#options.requestOptions?.ignoreCancelToken
      // 仅当配置中ignoreCancelToken为false时执行，即不忽略重复请求
      !ignoreCancel && axiosCanceler.addPending(config)
      if (requestInterceptors && isFunction(requestInterceptors)) {
        config = requestInterceptors(config, this.#options)
      }
      return config
    }, undefined)

    // 请求拦截器错误捕获
    requestInterceptorsCatch &&
      isFunction(requestInterceptorsCatch) &&
      this.#axiosInstance.interceptors.request.use(undefined, requestInterceptorsCatch)

    // 相应结果拦截处理
    this.#axiosInstance.interceptors.response.use((res) => {
      res && axiosCanceler.removePending(res.config)
      if (responseInterceptors && isFunction(responseInterceptors)) {
        res = responseInterceptors(res)
      }
      return res
    }, undefined)

    // 相应结果拦截器错误捕获
    responseInterceptorsCatch &&
      isFunction(responseInterceptorsCatch) &&
      this.#axiosInstance.interceptors.response.use(undefined, responseInterceptorsCatch)
  }
}
