import { PageEnum } from '@/enums/pageEnum'
import { createRouter, createWebHashHistory } from 'vue-router'
import { createRouterGuards } from './router-guards'
const modules = import.meta.globEager('./modules/**/*.js')

const routeModuleList = []

Object.keys(modules).forEach((key) => {
  const mod = modules[key].default || {}
  const modList = Array.isArray(mod) ? [...mod] : [mod]
  routeModuleList.push(...modList)
})

function sortRoute(a, b) {
  return (a.meta?.sort || 0) - (b.meta?.sort || 0)
}

routeModuleList.sort(sortRoute)

// 根路由
export const RootRoute = {
  path: '/',
  name: 'Root',
  redirect: PageEnum.BASE_HOME,
  meta: {
    title: 'Root'
  }
}

// 登录路由
export const LoginRoute = {
  path: '/login',
  name: 'Login',
  component: () => import('@/views/Login.vue'),
  meta: {
    title: '登录'
  }
}

export const menuRoute = [...routeModuleList]

export const constantRouter = [LoginRoute, RootRoute, ...routeModuleList]

const router = createRouter({
  history: createWebHashHistory(''),
  routes: constantRouter,
  strict: true,
  scrollBehavior: () => ({ left: 0, top: 0 })
})

export function setupRouter(app) {
  app.use(router)
  createRouterGuards(router)
}

export default router
