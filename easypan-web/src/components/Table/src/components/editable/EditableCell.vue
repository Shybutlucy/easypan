<template>
  <div class="editable-cell">
    <div v-show="!isEdit" class="editable-cell-content">
      {{ getValues }}
      <div class="flex editable-cell-content" v-show="isEdit" v-click-outside="onClickOutside">
        <div class="editable-cell-content">
          <ElInput ref="elRef" v-model="value" />
        </div>
        <!-- 可编辑cell的提交和取消按钮-->
        <div class="editable-cell-action">
          <n-icon class="mx-2 cursor-pointer">
            <CheckOutlined @click="handleSubmit" />
          </n-icon>
          <n-icon class="mx-2 cursor-pointer">
            <CloseOutlined @click="handleCancel" />
          </n-icon>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'EditableCell'
}
</script>
<script setup>
import { isBoolean } from '@/utils/is'
import { ElInput } from 'element-plus'
import { computed, nextTick, ref, unref, watchEffect } from 'vue'
import { useTableContext } from '../../hooks/useTableContext'

const props = {
  value: {
    type: String,
    default: ''
  },
  record: {
    type: Object
  },
  column: {
    type: Object,
    default: () => ({})
  },
  index: {
    type: Number
  }
}
const isEdit = ref(false)
const elRef = ref()
const table = useTableContext()
const currentValueRef = ref(props.value)
const defaultValueRef = ref(props.value)

const getValues = computed(() => {
  return unref(currentValueRef)
})

watchEffect(() => {
  const { editable } = props.column
  if (isBoolean(editable)) {
    isEdit.value = !!editable
  }
})
// 处理单元格的编辑，处理获取焦点
function handleEdit() {
  isEdit.value = true
  nextTick(() => {
    const el = unref(elRef)
    el?.focus?.()
  })
}
// 处理提交事件
function handleSubmit(needEmit = true, valid = true) {
  if (valid) {
    const isPass = true
    if (!isPass) return false
  }

  const { column, index, record } = props
  if (!record) return false
  const { prop } = column
  const value = unref(currentValueRef)
  if (!prop) return

  needEmit && table.emit?.('edit-end', { record, index, prop, value })
  isEdit.value = false
}

// 处理回车Enter事件
async function handleEnter() {
  handleSubmit()
}

// 取消事件处理
function handleCancel() {
  isEdit.value = false
  // 恢复原始数据
  currentValueRef.value = defaultValueRef.value
  const { column, index, record } = props
  const { prop } = column
  // 触发父组件的edit-cancel函数
  table.emit?.('edit-cancel', {
    record,
    index,
    prop: prop,
    value: unref(currentValueRef)
  })
}

if (props.record) {
  props.record['cancelCbs'] = handleCancel
  props.record['submitCbs'] = handleSubmit
  // 给 record 绑定 onCancelEdit 函数
  props.record.onCancelEdit = () => {
    props?.cancelCbs()
  }
  props.record.onSubmitEdit = () => {
    props.record?.submitCbs()
    return true
  }
}
</script>
<style lang="scss" scoped></style>
