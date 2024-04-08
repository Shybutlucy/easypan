const routeName = 'settings'
const Layout = () => import('@/layout/index.vue')
const src = [
  'https://nd-static.bdstatic.com/m-static/v20-main/home/img/icon-util-active.d799bb4e.png',
  'data:image/png;base64,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'
]
const routes = [
  {
    path: '/settings',
    name: routeName,
    redirect: '/settings/files',
    component: Layout,
    meta: {
      title: '设置',
      sort: 3,
      src: src,
      alwaysShow: true
    },
    children: [
      {
        path: '/settings/files',
        name: `${routeName}_files`,
        meta: {
          title: '用户文件'
        },
        component: () => import('@/views/settings/file/index.vue')
      },
      {
        path: '/settings/users',
        name: `${routeName}_users`,
        meta: {
          title: '用户管理'
        },
        component: () => import('@/views/settings/user/index.vue')
      },
      {
        path: '/settings/system',
        name: `${routeName}_system`,
        meta: {
          title: '系统设置'
        },
        component: () => import('@/views/settings/system/index.vue')
      }
    ]
  }
]

export default routes
