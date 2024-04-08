const routeName = 'index'
const Layout = () => import('@/layout/index.vue')
const src = [
  'https://nd-static.bdstatic.com/m-static/v20-main/home/img/icon-home-active.158e32eb.png',
  'data:image/png;base64,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'
]

const routes = [
  {
    path: '/index',
    name: routeName,
    redirect: '/index/all',
    component: Layout,
    meta: {
      title: '首页',
      src: src,
      sort: 0,
      alwaysShow: true
    },
    children: [
      {
        path: '/index/:category',
        name: `${routeName}_category`,
        meta: {
          title: '我的文件',
          children: [
            {
              icon: 'cate-all',
              name: '全部',
              category: 'all',
              path: '/index/all',
              isShow: false
            },
            {
              icon: 'cate-doc',
              name: '文档',
              category: 'doc',
              path: '/index/doc'
            },
            {
              icon: 'cate-video',
              name: '视频',
              category: 'video',
              path: '/index/video'
            },
            {
              icon: 'cate-music',
              name: '音频',
              category: 'music',
              path: '/index/music'
            },
            {
              icon: 'cate-pic',
              name: '图片',
              category: 'image',
              path: '/index/image'
            },
            {
              icon: 'cate-other',
              name: '其他',
              category: 'other',
              path: '/index/other'
            }
          ]
        },
        component: () => import('@/views/main/main.vue')
      },
      {
        path: '/index/recycle',
        name: `${routeName}_recycle`,
        meta: {
          title: '回收站'
        },
        component: () => import('@/views/recycle/recycle.vue')
      }
    ]
  }
]

export default routes
