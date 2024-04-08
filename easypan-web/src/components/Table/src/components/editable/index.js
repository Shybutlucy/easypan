import { h } from 'vue'
import EditableCell from './EditableCell.vue'
/**
 * 渲染为可编辑单元格，并挂载属性
 * @param {Array} column
 */
export function renderEditCell(column) {
  return (record, index) => {
    const _prop = column.prop
    const value = record[_prop]
    record.onEdit = (edit, submit = false) => {
      if (!submit) {
        record.editable = edit
      }
      // 提交编辑
      if (!edit && submit) {
        const red = record.onSubmitEdit?.()
        if (res) {
          record.editable = false
          return true
        }
        return false
      }
      // 取消编辑
      if (!edit && !submit) {
        record.onCancelEdit?.()
      }
      return true
    }
    return h(EditableCell, {
      value,
      record,
      column,
      index
    })
  }
}
