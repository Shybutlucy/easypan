import { isBoolean } from '@/utils/is'
import { computed, ref, unref } from 'vue'
import { APISETTING, DEFAULTPAGESIZE, PAGESIZES } from '../const'

// 分页组件钩子函数
export function usePagination(refProps) {
  const configRef = ref({})
  const show = ref(true)

  const getPaginationInfo = computed(() => {
    // 从父传子值中查看是否开启了分页
    const { pagination } = unref(refProps)
    if (!unref(show) || (isBoolean(pagination) && !pagination)) {
      // 未开启，直接返回false
      return false
    }
    // 获取接口返回总页数字段名
    const { totalField, itemCountField } = APISETTING
    // 组装分页组件的信息
    return {
      pageSize: DEFAULTPAGESIZE, // 默认每页数量
      pageSizes: PAGESIZES, // 默认可切换每页数量
      showSizePicker: true,
      showQuickJumper: true,
      ...(isBoolean(pagination) ? {} : pagination),
      ...unref(configRef),
      pageCount: unref(configRef)[totalField],
      itemCount: unref(configRef)[itemCountField] || 0
    }
  })

  /**
   * 分页信息钩子函数，设置分页的当前页和总页数
   * @param {*} info
   */
  function setPagination(info) {
    const paginationInfo = unref(getPaginationInfo)
    configRef.value = {
      ...(!isBoolean(paginationInfo) ? paginationInfo : {}),
      ...info
    }
  }

  function getPagination() {
    return unref(getPaginationInfo)
  }

  function getShowPagination() {
    return unref(show)
  }

  async function setShowPagination(flag) {
    show.value = flag
  }

  return {
    getPagination,
    getPaginationInfo,
    setShowPagination,
    getShowPagination,
    setPagination
  }
}
