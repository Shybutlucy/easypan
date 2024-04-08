<template>
  <div class="menu">
    <div class="menu-list">
      <div
        :class="['menu-item', item.key === currentMenu.key ? 'active' : '']"
        v-for="item in menus"
        @click="jump(item)"
      >
        <div v-if="item.icon" :class="['iconfont', 'icon-' + item.icon]"></div>
        <div v-else-if="item.src" class="item-img">
          <img v-show="item.key === currentMenu.key" :src="item.src[0]" alt="" />
          <img v-show="item.key !== currentMenu.key" :src="item.src[1]" alt="" />
        </div>
        <div class="text">{{ item.label }}</div>
      </div>
    </div>
    <div class="menu-sub-list">
      <template v-for="sub in currentMenu.children">
        <div v-if="sub.meta && sub.meta.children">
          <!-- 带子选项的路由菜单 -->
          <ExpandMenu
            :title="sub.label"
            v-model="sub.meta.children"
            :activePath="currentPath"
            @item-click="jump"
          ></ExpandMenu>
        </div>
        <!-- 普通的路由菜单 -->
        <div
          v-else-if="sub"
          :class="['menu-sub-item', sub.path == currentPath ? 'active' : '']"
          @click="jump(sub)"
        >
          <div class="body">
            <span class="text">{{ sub.label }}</span>
          </div>
        </div>
      </template>
      <div class="tips" v-if="currentMenu && currentMenu.tips">{{ currentMenu.tips }}</div>
      <div class="space-info">
        <div class="percent">
          <el-progress :percentage="percentage" :show-text="false" color="#ffd821"></el-progress>
        </div>
        <div class="space-use">
          <div class="use">
            {{ size2Str(useSpaceInfo.useSpace) }} / {{ size2Str(useSpaceInfo.totalSpace) }}
          </div>
          <el-icon class="refresh" :size="14" @click="getUseSpace"><SyncOutlined /></el-icon>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  name: 'Menu'
}
</script>
<script setup>
import { computed, onMounted, ref, watchEffect } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { generatorMenu, size2Str } from '@/utils'
import { menuRoute } from '@/router'
import ExpandMenu from './ExpandMenu.vue'
import { SyncOutlined } from '@vicons/antd'
import { useUserStore } from '@/store/modules/user'
// import { menus } from './menus'
const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const menus = ref([])
const currentMenu = ref({})
const currentPath = ref()

const useSpaceInfo = computed(() => {
  return userStore?.userSpace || { useSpace: 0, totalSpace: 1 }
})

const percentage = computed(() => {
  return Math.floor((useSpaceInfo.value.useSpace / useSpaceInfo.value.totalSpace) * 10000) / 100
})

function jump(menu) {
  if (!menu.path || menu.key == currentMenu.value.key) {
    return
  }
  router.push(menu.path)
}

function updateMenu(path) {
  if (menus.value.length === 0) {
    menus.value = generatorMenu(menuRoute)
  }
  const parentPath = path.substring(0, path.lastIndexOf('/'))
  const menu = menus.value.find((item) => {
    return item.path === parentPath
  })
  currentMenu.value = menu ? menu : {}
  currentPath.value = path
}

const getUseSpace = async () => {
  await userStore.fetchUserSpace()
}

watchEffect(() => {
  updateMenu(route.path)
})

onMounted(() => {
  getUseSpace()
})
</script>
<style lang="scss" scoped>
.menu {
  border-right: 1px solid #f1f2f4;
  display: flex;
  .menu-list {
    height: calc(100vh - 56px);
    width: 80px;
    padding-top: 40px;
    padding-bottom: 30px;
    color: #636d7e;
    box-shadow: 0 3px 10px 0 rgba(0, 0, 0, 0.06);
    border-right: 1px solid #f1f2f4;
    .menu-item {
      text-align: center;
      font-size: 12px;
      cursor: pointer;
      margin-top: 30px;
      &:first-child {
        margin-top: 0px;
      }
      &:hover {
        background: #f3f3f3;
      }
      .iconfont {
        font-weight: normal;
        font-size: 28px;
      }
      .item-img img {
        width: 36px;
        height: auto;
      }
      .text {
        margin-top: 6px;
        max-width: 80px;
      }
    }
    .active {
      color: #06a7ff;
      font-weight: 700;
    }
  }
  .menu-sub-list {
    width: 200px;
    padding: 20px 0px;
    position: relative;
    .menu-sub-item {
      height: 40px;
      line-height: 40px;
      display: inline-block;
      margin-left: 12px;
      width: 177px;
      font-size: 14px;
      font-weight: 700;
      border-radius: 12px;
      cursor: pointer;
      &:hover {
        background: #fafafa;
      }
      .body {
        padding-left: 50px;
        .text {
          color: #636d7e;
          font-size: 14px;
        }
      }
    }
    .active {
      background-color: #eef9fe;
      .body .text {
        color: #06a7ff;
      }
    }
    .tip {
      margin-top: 10px;
      color: #888888;
      font-size: 13px;
    }

    .space-info {
      position: absolute;
      bottom: 25px;
      width: 100%;
      padding: 0px 20px;
      font-size: 14px;
      .space-use {
        margin-top: 5px;
        color: #afb3bf;
        font-size: 12px;
        display: flex;
        justify-content: space-around;
        .use {
          flex: 1;
        }
        .refresh {
          cursor: pointer;
          color: #05a1f5;
        }
      }
    }
  }
}
</style>
