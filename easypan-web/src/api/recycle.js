import { http } from '@/utils/axios'
const prefix = '/recycle'

/**
 * @description: 回收站文件列表
 */
export function loadDataList(params) {
  return http.request({
    url: `${prefix}/loadRecycleList/`,
    method: 'GET',
    params
  })
}

/**
 * @description: 恢复文件
 */
export function recoveryFileApi(params) {
  return http.request(
    {
      url: `${prefix}/recoverFile/${params}`,
      method: 'PUT'
    },
    {
      successMessageText: '恢复成功'
    }
  )
}

/**
 * @description: 删除文件
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