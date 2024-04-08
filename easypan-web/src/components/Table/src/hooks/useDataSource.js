import { computed, ref, unref, watch, watchEffect, onMounted } from 'vue'
import { APISETTING } from '../const'
import { isBoolean } from '@/utils/is'
/**
 *
 * @param {Object} propsRef 父组件传过来的值
 * @param {*} param1
 * @param {*} emit
 */
export function useDataSource(
  propsRef,
  { getPaginationInfo, setPagination, setLoading, tableData },
  emit
) {
  // 表格数据的Ref
  const dataSourceRef = ref([])
  watchEffect(() => {
    tableData.value = unref(dataSourceRef)
  })

  // 数据变动，更新tableData
  watch(
    () => unref(propsRef).dataSource,
    () => {
      const { dataSource } = unref(propsRef)
      dataSource && (dataSourceRef.value = dataSource)
    },
    {
      immediate: true
    }
  )

  const getRowKey = computed(() => {
    const { rowKey } = unref(propsRef)
    return rowKey
      ? rowKey
      : () => {
          return 'key'
        }
  })

  const getDataSourceRef = computed(() => {
    const dataSource = unref(dataSourceRef)
    if (!dataSource || dataSource.length === 0) {
      return unref(dataSourceRef)
    }
    return unref(dataSourceRef)
  })

  async function fetch(opt) {
    try {
      setLoading(true)
      const { request, pagination } = unref(propsRef)
      if (!request) return
      const pageField = APISETTING.pageField
      const sizeField = APISETTING.sizeField
      const totalField = APISETTING.totalField
      const listField = APISETTING.listField
      const itemCountField = APISETTING.itemCountField
      // 分页参数，当前页以及每页数量
      let pageParams = {}
      // 获取分页信息，缺省则赋予1和10
      const { page = 1, pageSize = 10 } = unref(getPaginationInfo)
      if ((isBoolean(pagination) && !pagination) || isBoolean(getPaginationInfo)) {
        // 没有开启分页
        pageParams = {}
      } else {
        // page，即当前页，如果opt参数中有的话，从opt中赋值/
        pageParams[pageField] = (opt && opt[pageField]) || page
        pageParams[sizeField] = pageSize
      }

      let params = {
        ...pageParams
      }

      const res = await request(params)

      // 请根据自己项目实际情况修改字段名
      // 从结果中拿到总页数，缺省值为0。
      const resultTotal = res[totalField] || 0
      // 从结果中拿到当前页
      const currentPage = res[pageField]
      // 从结果拿到总数据数
      const totalData = res[itemCountField]
      // 如果数据异常，需获取正确的页码再次执行
      if (resultTotal) {
        if (page > resultTotal) {
          setPagination({
            [pageField]: resultTotal
          })
          fetch(opt)
        }
      }
      let resultInfo = res[listField] ? res[listField] : []
      // 赋予表格数据
      dataSourceRef.value = resultInfo
      // 赋予分页信息
      setPagination({
        [pageField]: currentPage,
        [totalField]: resultTotal,
        [itemCountField]: totalData
      })
      // 如果传入了page参数，则从opt获取
      if (opt && opt[pageField]) {
        setPagination({
          [pageField]: opt[pageField] || 1
        })
      }
      emit('fetch-success', {
        items: unref(resultInfo),
        resultTotal,
        totalData
      })
    } catch (error) {
      console.error(error)
      emit('fetch-error', error)
      // 失败，数据清空
      dataSourceRef.value = []
    } finally {
      // 数据获取成功与否，都取消加载动画
      setLoading(false)
    }
  }

  onMounted(() => {
    setTimeout(() => {
      fetch()
    }, 16)
  })

  // 设置表格数据
  function setTableData(values) {
    dataSourceRef.value = values
  }

  // 获取表格数据
  function getDataSource() {
    return getDataSourceRef.value
  }

  // 重新加载数据
  async function reload(opt) {
    await fetch(opt)
  }

  // 设置一行数据值
  function setRowFieldValue(field, value) {
    dataSourceRef.value.forEach((item) => {
      item[field] = value
    })
  }

  function unshiftRow(row) {
    dataSourceRef.value.unshift(row)
  }

  function setRowValue(row, index) {
    dataSourceRef.value.splice(index, 1)
    dataSourceRef.value.splice(index, 0, row)
  }

  return {
    fetch,
    getRowKey,
    getDataSourceRef,
    getDataSource,
    setTableData,
    reload,
    setRowFieldValue,
    unshiftRow,
    setRowValue
  }
}
