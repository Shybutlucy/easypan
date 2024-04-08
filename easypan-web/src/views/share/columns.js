export const columns = [
  {
    label: '分享文件',
    prop: 'filename',
    scopedSlots: 'filename',
    ellipsis: true,
    minWidth: '48%'
  },
  {
    label: '分享时间',
    prop: 'createTime',
    minWidth: '25%'
  },
  {
    label: '状态',
    prop: 'expireTime',
    scopedSlots: 'RowAction',
    minWidth: '25%'
  },
  {
    label: '浏览次数',
    prop: 'browseCount',
    scopedSlots: 'BrowseCount',
    minWidth: '12%'
  }
]
