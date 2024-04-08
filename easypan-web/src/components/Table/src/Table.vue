<template>
  <div class="table">
    <div class="file-table" v-show="getLoading || tableData.length > 0">
      <el-table
        ref="tableElRef"
        v-loading="getLoading"
        v-bind="getBindValues"
        @row-click="handleRowClick"
        @selection-change="handleSelectionChange"
        :row-class-name="tableRowClassName"
      >
        <!-- select选择框 -->
        <el-table-column
          v-if="selection"
          type="selection"
          width="48px"
          align="center"
          class-name="checkbox"
        >
        </el-table-column>
        <!-- 数据列 -->
        <template v-for="(column, index) in columns">
          <template v-if="column.scopedSlots">
            <el-table-column
              :key="index"
              :prop="column.prop"
              :label="column.label"
              :align="column.align || 'left'"
              :width="column.width"
              :min-width="column.minWidth"
              :show-overflow-tooltip="column.ellipsis"
            >
              <template #default="scope">
                <slot :name="column.scopedSlots" :index="scope.$index" :row="scope.row"></slot>
              </template>
            </el-table-column>
          </template>
          <template v-else>
            <el-table-column
              :key="index"
              :prop="column.prop"
              :label="column.label"
              :align="column.align || 'left'"
              :width="column.width"
              :min-width="column.minWidth"
              :show-overflow-tooltip="column.ellipsis"
            >
            </el-table-column>
          </template>
        </template>
      </el-table>
    </div>
    <div class="empty" v-show="!getLoading && tableData.length === 0">
      <slot name="empty"></slot>
    </div>
  </div>
</template>

<script>
export default {
  name: 'BasicTable'
}
</script>
<script setup>
import { computed, nextTick, onMounted, ref, unref, watch } from 'vue'
import { basicProps } from './props'
import { useLoading } from './hooks/useLoading'
import { usePagination } from './hooks/usePagination'
import { useDataSource } from './hooks/useDataSource'
import { getViewportOffset } from '@/utils/domUtils'
// import { createTableContext } from './hooks/useTableContext';
import { useWindowSizeFn } from '@/utils/useWindowSizeFn'

const emit = defineEmits(['fetch-success', 'fetch-error', 'select-change'])
const props = defineProps({ ...basicProps })

const tableHeight = ref(150)
const tableData = ref([])
const tableElRef = ref()
const innerPropsRef = ref()

const getProps = computed(() => {
  return { ...props, ...unref(innerPropsRef) }
})

// 获取表格加载动画的钩子函数
const { getLoading, setLoading } = useLoading(getProps)
// 获取表格的分页信息，里面会读取默认值, setPagination，设置分页信息的钩子函数
const { getPaginationInfo, setPagination } = usePagination(getProps)
// 获取一些表格的操作，并传入一些钩子函数，方便调用
const { getDataSourceRef, getRowKey, reload, setRowFieldValue, unshiftRow, setRowValue } =
  useDataSource(
    getProps,
    {
      getPaginationInfo,
      setPagination,
      tableData,
      setLoading
    },
    emit
  )

// 页码切换
function uploadPage(page) {
  setPagination({ page: page })
  reload()
}

//每页显示数量切换
function updatePageSize(size) {
  setPagination({ page: 1, pageSize: size })
  reload()
}

// 组装表格信息
const getBindValues = computed(() => {
  const tableData = unref(getDataSourceRef)
  const maxHeight = tableData.length ? `${unref(tableHeight)}px` : 'auto'
  return {
    ...unref(getProps),
    data: tableData,
    rowKey: unref(getRowKey),
    maxHeight: maxHeight
  }
})

function setProps(props) {
  innerPropsRef.value = { ...unref(innerPropsRef), ...props }
}

let numbers = []
function handleRowClick(row) {
  let isSelect =
    tableElRef.value.getSelectionRows().includes(row) &&
    tableElRef.value.getSelectionRows().length == 1
  tableElRef.value.clearSelection()
  tableElRef.value.toggleRowSelection(row, !isSelect)
}
function handleSelectionChange(selection) {
  const ids = selection.map((item) => item.id)
  numbers = ids
  emit('select-change', selection)
}
// 选中背景色
function tableRowClassName({ row, _ }) {
  let color = ''
  numbers.forEach((id) => {
    if (id === row.id) {
      return (color = 'is-selected')
    }
  })
  return color
}

// 动态计算高度
async function computeTableHeight() {
  const table = unref(tableElRef)
  if (!table) return
  const tableEl = table?.$el
  const headerEl = tableEl.querySelector('.el-table__header')
  const { bottomIncludeBody } = getViewportOffset(headerEl)
  tableHeight.value = bottomIncludeBody
}

useWindowSizeFn(computeTableHeight, 280)

watch(
  () => tableData.value.length,
  () => {
    computeTableHeight()
  }
)

onMounted(() => {
  nextTick(() => {
    computeTableHeight()
  })
})

defineExpose({
  reload,
  tableElRef,
  setRowFieldValue,
  unshiftRow,
  setRowValue,
  setLoading
})
</script>
<style lang="scss" scoped>
.table {
  height: 100%;
}
.file-table {
  .checkbox {
    width: 80px;
  }
  :deep(.el-table__inner-wrapper) {
    &::before {
      background-color: transparent;
    }
  }
  :deep(.el-table) {
    .el-table__cell,
    .cell {
      // overflow: visible;
      padding: 0px;
    }
  }
  :deep(.el-table__header > thead > tr) {
    height: 50px;
    line-height: 1;
    color: #818999;
    text-align: left;
    font-size: 12px;
    .el-table__cell {
      border-bottom: 1px solid #f7f8fa;
    }
    .el-table-column--selection {
      border-bottom: 1px solid transparent;
    }
  }
  :deep(.el-table__row.is-selected) {
    background-color: #f2faff;
  }
  :deep(.el-table__row) {
    height: 50px;
    color: #03081a;
    font-size: 12px;
    line-height: 1;
    cursor: pointer;
    .el-table__cell {
      border-bottom: 1px solid #f7f8fa;
      background-color: transparent !important;
    }
    .el-table-column--selection {
      border-bottom: 1px solid transparent;
      .el-checkbox {
        visibility: hidden;
      }
      .el-checkbox.is-checked {
        visibility: visible;
      }
    }
    &:hover {
      background-color: #f7f9fc;
      border-color: #f7f9fc;
      .el-table-column--selection .el-checkbox {
        visibility: visible;
      }
    }
  }
}
</style>
