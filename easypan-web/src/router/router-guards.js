import { PageEnum } from '@/enums/pageEnum'
import VueCookies from 'vue-cookies'

const LOGIN_PATH = PageEnum.BASE_LOGIN

// 路由守卫白名单，即不进行重定向
const whitePathList = [LOGIN_PATH]

// 创建路由首位
export function createRouterGuards(router) {
  router.beforeEach((to, from, next) => {
    if (from.path === LOGIN_PATH && to.name === 'errorPage') {
      next(PageEnum.BASE_HOME)
      return
    }

    // 白名单直接进入
    if (whitePathList.includes(to.path)) {
      next()
      return
    }

    // 获取用户信息
    const userInfo = VueCookies.get('userInfo')
    if (!userInfo) {
      if (to.meta.ignoreAuth) {
        next()
        return
      }
      // 重定向到登录页，带跳转前的路径
      const redirectData = {
        path: LOGIN_PATH,
        replace: true
      }
      if (to.path) {
        redirectData.query = {
          ...redirectData.query,
          redirect: to.path
        }
      }
      next(redirectData)
      return
    }
    // const redirectPath = from.query.redirect || to.path
    // const redirect = decodeURIComponent(redirectPath)
    // const nextData = to.path === redirect ? { ...to, replace: true } : { path: redirect }
    // console.log(nextData);
    // next(nextData)
    next()
  })
}
