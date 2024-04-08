import { storage } from '@/utils/Storage'
import { defineStore } from 'pinia'
import { USER_SPACE } from '../mutation-types'
import { getUseSpaceApi } from '@/api/user'
import { store } from '@/store'

export const useUserStore = defineStore({
  id: 'app-user',
  state: () => ({
    userSpace: storage.get(USER_SPACE, { useSpace: 0, totalSpace: 1 })
  }),
  getters: {
    getUserSapce() {
      return this.userSpace
    }
  },
  actions: {
    setUserSapce(userSpace) {
      this.userSpace = userSpace
    },
    // 获取用户使用空间
    async fetchUserSpace() {
      try {
        const res = await getUseSpaceApi()
        const ex = 7 * 24 * 60 * 60
        storage.set(USER_SPACE, res, ex)
        this.setUserSapce(res)
        return Promise.resolve(res)
      } catch (e) {
        return Promise.reject(e)
      }
    }
  }
})

// Need to be used outside the setup
export function useUserStoreWidthOut() {
  return useUserStore(store)
}
