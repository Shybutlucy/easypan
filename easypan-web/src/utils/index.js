import { isObject } from './is'
import { PageEnum } from '@/enums/pageEnum'

/**
 * 递归组装菜单格式
 */

export function generatorMenu(routerMap) {
  return filterRouter(routerMap).map((item) => {
    const isRoot = isRootRouter(item)
    const info = isRoot ? item.children[0] : item
    const currentMenu = {
      ...info,
      label: info.meta?.title,
      key: info.name,
      icon: isRoot ? item.meta?.icon : info.meta?.icon,
      src: isRoot ? item.meta?.src : info.meta?.src
    }
    // 是否有子菜单，并递归处理
    if (info.children && info.children.length > 0) {
      // 递归
      currentMenu.children = generatorMenu(info.children)
    }
    return currentMenu
  })
}

/**
 * 判断根路由 Router
 * */
export function isRootRouter(item) {
  return (
    item.meta?.alwaysShow != true &&
    item?.children?.filter((item) => !item?.meta?.hidden)?.length === 1
  )
}

/**
 * 排除Router
 */
export function filterRouter(routerMap) {
  return routerMap.filter((item) => {
    return (
      (item.meta?.hidden || false) != true &&
      !['/:path(.*)*', '/', PageEnum.REDIRECT, PageEnum.BASE_LOGIN].includes(item.path)
    )
  })
}

/**
 *  找到对应的节点
 * */
let result = null
export function getTreeItem(data, key) {
  data.map((item) => {
    if (item.key === key) {
      result = item
    } else {
      if (item.children && item.children.length) {
        getTreeItem(item.children, key)
      }
    }
  })
  return result
}

export function deepMerge(src = {}, target = {}) {
  let key
  for (key in target) {
    src[key] = isObject(src[key]) ? deepMerge(src[key], target[key]) : (src[key] = target[key])
  }
  return src
}

export function size2Str(limit) {
  var size = ''
  if (limit < 1024) {
    // 小于 1KB 转为B
    size = limit.toFixed(2) + 'B'
  } else if (limit < 1024 * 1024) {
    // 小于 1MB 转为KB
    size = (limit / 1024).toFixed(2) + 'KB'
  } else if (limit < 1024 * 1024 * 1024) {
    // 小于 1GB 转为MB
    size = (limit / (1024 * 1024)).toFixed(2) + 'MB'
  } else {
    // 小于 1TB 转为GB
    size = (limit / (1024 * 1024 * 1024)).toFixed(2) + 'GB'
  }
  var sizeStr = size + ''
  var index = sizeStr.indexOf('.')
  var dou = sizeStr.substring(index + 1, index + 3)
  if (dou == '00') {
    return sizeStr.substring(0, index) + sizeStr.substring(index + 3, sizeStr.length)
  }
  return size
}

export function dateFormat(fmt, date) {
  let ret = ''
  date = new Date(date)
  const opt = {
    'Y+': date.getFullYear().toString(), // 年
    'm+': (date.getMonth() + 1).toString(), // 月
    'd+': date.getDate().toString(), // 日
    'H+': date.getHours().toString(), // 时
    'M+': date.getMinutes().toString(), // 分
    'S+': date.getSeconds().toString() // 秒
  }
  for (let k in opt) {
    ret = new RegExp('(' + k + ')').exec(fmt)
    if (ret) {
      fmt = fmt.replace(ret[1], ret[1].length == 1 ? opt[k] : opt[k].padStart(ret[1].length, '0'))
    }
  }
  return fmt
}
