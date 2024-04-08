import { http } from '@/utils/axios'
const prefix = '/share'

/**
 * @description: 回收站文件列表
 */
export function loadShareList(params) {
  return http.request({
    url: `${prefix}/loadShareList/`,
    method: 'GET',
    params
  })
}

/**
 * @description: 分享文件
 */
export function shareFileApi(data) {
  return http.request(
    {
      url: `${prefix}/shareFile`,
      method: 'POST',
      data
    },
    {
      successMessageText: '分享成功'
    }
  )
}

/**
 * @description: 取消分享
 */
export function cancelShareApi(ids) {
  return http.request(
    {
      url: `${prefix}/cancelShare/${ids}`,
      method: 'DELETE'
    },
    {
      successMessageText: '取消成功'
    }
  )
}