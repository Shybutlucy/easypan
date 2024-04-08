export const columns = [
  {
    label: '昵称',
    prop: 'nickname'
  },
  {
    label: '头像',
    prop: 'avatar',
    scopedSlots: 'avatar',
    width: 80
  },
  {
    label: '邮箱',
    prop: 'email',
    ellipsis: true,
  },
  {
    label: '空间使用',
    prop: 'space',
    scopedSlots: 'space',
    ellipsis: true,
  },
  {
    label: '加入时间',
    prop: 'createTime',
    ellipsis: true,
  },
  {
    label: '最后登陆时间',
    prop: 'lastLoginTime',
    ellipsis: true,
  },
  {
    label: '状态',
    prop: 'status',
    scopedSlots: 'status',
    width: 80
  },
  {
    label: '操作',
    prop: 'op',
    scopedSlots: 'op',
    width: 150
  }
]