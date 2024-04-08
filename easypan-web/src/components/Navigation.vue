<template>
  <div class="nav-list">
    <span v-if="folderList.length == 0" class="all-file">全部文件</span>
    <template v-if="folderList.length > 0">
      <span class="back nav-item">
        <span class="item-title text-ellip" @click="backParent">返回上一级</span>
        <span class="item-sep">|</span>
      </span>
      <span class="nav-item">
        <span class="item-title text-ellip" @click="folderSwitch(-1)">全部文件</span>
        <span class="item-sep">></span>
      </span>
      <span
        v-for="(item, index) in folderList"
        :class="['nav-item', index < folderList.length - 1 ? '' : 'is-disable-nav']"
      >
        <span class="item-title text-ellip" @click="folderSwitch(index)">{{ item.filename }}</span>
        <span class="item-sep">></span>
      </span>
    </template>
  </div>
</template>

<script>
export default {
  name: 'Navigation'
}
</script>
<script setup>
import { ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getFolderInfo } from '@/api/file'

const props = defineProps({
  watchPath: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(['nav-change'])
const route = useRoute()
const router = useRouter()
const folderList = ref([])
let currentFolder = { id: '0' }
let category = 'all'

function openFolder(data) {
  const { id, filename } = data
  const folder = {
    filename,
    id
  }
  folderList.value.push(folder)
  currentFolder = folder
  setPath()
}

function setPath() {
  if (!props.watchPath) {
    // 设置不监听路由回调方法
    navChange()
    return
  }
  let pathArray = []
  folderList.value.forEach((item) => {
    pathArray.push(item.id)
  })
  router.push({
    path: route.path,
    query: pathArray.length == 0 ? '' : { path: pathArray.join('/') }
  })
}
// 返回上一级
function backParent() {
  folderSwitch(folderList.value.length - 2)
}
// 点击导航，切换目录
function folderSwitch(index) {
  if (index == -1) {
    // 返回全部
    ;(currentFolder = { id: '0' }), (folderList.value = [])
  } else {
    currentFolder = folderList.value[index]
    folderList.value.splice(index + 1, folderList.value.length)
  }
  setPath()
}
// 获取当前路径的目录
const getNavigationFolder = async (path) => {
  const res = await getFolderInfo({ path })
  if (!res) return
  folderList.value = res
}

function init() {
  folderList.value = []
  ;(currentFolder = { id: '0' }), navChange()
}
function navChange() {
  emit('nav-change', {
    category,
    curFolder: currentFolder
  })
}
watch(
  () => route,
  (newVal, oldValue) => {
    if (!props.watchPath) {
      return
    }
    if (newVal.path.indexOf('/index') === -1 && newVal.path.indexOf('/settings/files') === -1) {
      return
    }
    const path = newVal.query.path
    category = newVal.params.category
    if (path == undefined) {
      init()
    } else {
      if (folderList.value.length === 0) {
        // 没有目录信息，发请求获取
        getNavigationFolder(path)
      }
      let pathArray = path.split('/')
      currentFolder = {
        id: pathArray[pathArray.length - 1]
      }
      navChange()
    }
  },
  { immediate: true, deep: true }
)

defineExpose({ openFolder, init })
</script>
<style lang="scss" scoped>
.nav-list {
  .all-file {
    font-size: 12px;
    font-weight: 700;
  }
  .nav-item {
    font-size: 14px;
    .item-title {
      color: #06a7ff;
      max-width: 120px;
      cursor: pointer;
      display: inline-block;
      vertical-align: middle;
      font-size: 12px;
    }
    .item-sep {
      margin: 0 5px;
      color: #c4d8f4;
      display: inline-block;
      vertical-align: middle;
    }
  }
  .nav-item.is-disable-nav {
    .item-title {
      color: #818999;
      cursor: auto;
    }
    .item-sep {
      display: none;
    }
  }
}
</style>
