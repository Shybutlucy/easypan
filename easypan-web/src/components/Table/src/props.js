import { ElTable } from 'element-plus'
export const basicProps = {
  ...ElTable.props, // 这里继承原 UI 组件的 props
  size: {
    type: String,
    default: 'default'
  },
  dataSource: {
    type: [Object],
    default: () => []
  },
  columns: {
    type: [Array],
    default: () => [],
    required: true
  },
  request: {
    type: Function,
    default: null
  },
  rowKey: {
    type: [String, Function],
    default: undefined
  },
  pagination: {
    type: [Object, Boolean],
    default: () => {}
  },
  extHeight: {
    type: Number,
    default: 0
  },
  tableHeight: {
    type: Number,
    default: 0
  },
  loading: {
    type: Boolean,
    default: false
  },
  selection: {
    type: Boolean,
    default: true
  }
}
