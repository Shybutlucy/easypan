export const columns = [
  {
    label: '文件名',
    prop: 'filename',
    scopedSlots: 'filename',
    ellipsis: true,
    minWidth: '60%'
  },
  {
    label: '大小',
    prop: 'fileSize',
    scopedSlots: 'fileSize',
    minWidth: '20%'
  },
  {
    label: '删除时间',
    prop: 'createTime',
    scopedSlots: 'RowAction',
    minWidth: '20%'
  }
]
