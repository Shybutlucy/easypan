const toString = Object.prototype.toString

/**
 * @description: 判断值是否未某个类型
 */
export function is(val, type) {
  return toString.call(val) === `[object ${type}]`
}

/**
 * @description:  是否为函数
 */
export function isFunction(val) {
  return is(val, 'Function') || is(val, 'AsyncFunction')
}

/**
 * @description: 是否为对象
 */
export const isObject = (val) => {
  return val !== null && is(val, 'Object')
}

/**
 * @description:  是否为字符串
 */
export function isString(val) {
  return is(val, 'String')
}

/**
 * 判断是否 url
 * */
export function isUrl(url) {
  return /^(http|https):\/\//g.test(url)
}

/**
 * @description:  是否为boolean类型
 */
export function isBoolean(val) {
  return is(val, 'Boolean')
}

/**
 * @description:  是否为数组
 */
export function isArray(val) {
  return val && Array.isArray(val)
}

/**
 * @description:  是否为数值
 */
export function isNumber(val) {
  return is(val, 'Number')
}
