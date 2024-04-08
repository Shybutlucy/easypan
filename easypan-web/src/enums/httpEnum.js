/**
 * @description: 请求结果集
 */
export const ResultEnum = {
  SUCCESS: 2000,
  BUSSINESS_ERROR: 1000,
  PARAM_IS_INVALID: 1001,
  SYSTEM_ERROR: -1,
  TIMEOUT: 2004,
  TOKEN_TIMEOUT: 2004,
  TYPE: 'success',
  PARAM_INVALID: 1001,
  OUT_OF_SPACE: 2006
}

/**
 * @description: 请求方法
 */
export const RequestEnum = {
  GET: 'GET',
  POST: 'POST',
  PATCH: 'PATCH',
  PUT: 'PUT',
  DELETE: 'DELETE'
}

/**
 * @description:  常用的contentTyp类型
 */
export const ContentTypeEnum = {
  // json
  JSON: 'application/json;charset=UTF-8',
  // json
  TEXT: 'text/plain;charset=UTF-8',
  // form-data 一般配合qs
  FORM_URLENCODED: 'application/x-www-form-urlencoded;charset=UTF-8',
  // form-data  上传
  FORM_DATA: 'multipart/form-data;charset=UTF-8'
}
