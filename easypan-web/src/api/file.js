import { http } from '@/utils/axios'
import { ContentTypeEnum } from '@/enums/httpEnum'
const prefix = '/file'

/**
 * @description: 文件列表
 */
export function loadDataList(params) {
  return http.request({
    url: `${prefix}/loadDataList/`,
    method: 'GET',
    params
  })
}

/**
 * @description: 上传文件
 */
export function uploadFile(formData, config) {
  return http.request({
    url: `${prefix}/uploadFile`,
    method: 'POST',
    data: formData,
    timeout: 50 * 1000,
    ...config,
    headers: {
      'Content-Type': ContentTypeEnum.FORM_DATA,
      ignoreCancelToken: true
    }
  })
}

/**
 * @description: 新建目录
 */
export function newFolderApi(data) {
  return http.request(
    {
      url: `${prefix}/newFolder`,
      method: 'POST',
      data
    },
    {
      successMessageText: '创建文件夹成功'
    }
  )
}

/**
 * @description: 获取目录信息
 */
export function getFolderInfo(params) {
  return http.request({
    url: `${prefix}/getFolderInfo`,
    method: 'GET',
    params
  })
}

/**
 * @description: 修改文件名
 */
export function renameApi(data) {
  return http.request(
    {
      url: `${prefix}/rename`,
      method: 'PUT',
      data
    },
    {
      successMessageText: '重命名成功～'
    }
  )
}

/**
 * @description: 获取所有目录
 */
export function loadAllFolderApi(params) {
  return http.request({
    url: `${prefix}/loadAllFolder`,
    method: 'GET',
    params
  })
}

/**
 * @description: 移动文件
 */
export function changeFileFolderApi(data) {
  return http.request(
    {
      url: `${prefix}/changeFileFolder`,
      method: 'PUT',
      data
    },
    {
      successMessageText: '移动成功'
    }
  )
}

/**
 * @description: 回收文件
 */
export function delFileApi(params) {
  return http.request(
    {
      url: `${prefix}/delFile/${params}`,
      method: 'DELETE'
    },
    {
      successMessageText: '删除成功'
    }
  )
}

/**
 * @description: 活动文件流
 */
export function getFileBolb(url) {
  return http.request(
    {
      url,
      responseType: 'arraybuffer', // 因为是流文件，所以要指定blob类型
      method: 'GET'
    },
    {
      isTransformResponse: false
    }
  )
}

/**
 * @description: 创建下载链接
 */
export function createDownloadUrl(url) {
  return http.request({
    url,
    method: 'GET'
  })
}
