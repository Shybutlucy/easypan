<template>
  <div class="framework">
    <div class="header">
      <div class="logo">
        <div class="name">Easy云盘</div>
      </div>
      <div class="right-panel">
        <el-popover
          placement="bottom"
          width="auto"
          popper-style="padding:0px;border-radius:8px;"
          trigger="click"
          :offset="30"
          :hide-after="10"
          v-model:visible="transferVisible"
        >
          <template #reference>
            <span class="btn-radius">
              <el-icon class="rotate" color="#454d5a"><SwapOutlined /></el-icon>
            </span>
          </template>
          <template #default>
            <FileUploader ref="uploaderRef" @upload-callback="uploadCallbackHandler" />
          </template>
        </el-popover>
        <el-dropdown>
          <div class="user-info">
            <div class="avatar">
              <Avatar :userId="userInfo.id" :avatar="userInfo.avatar" :timestamp="timestamp" />
            </div>
            <span class="nickname">{{ userInfo.nickname }}</span>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="updateAvatar">修改头像</el-dropdown-item>
              <el-dropdown-item @click="updatePassword">修改密码</el-dropdown-item>
              <el-dropdown-item @click="handleLogout">退出</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
    <div class="body">
      <div class="left-sider">
        <AsideMenu ref="asideMenuRef" />
      </div>
      <div class="body-content">
        <router-view v-slot="{ Component }">
          <component ref="routerViewRef" :is="Component" @addFile="addFile" />
        </router-view>
      </div>
    </div>
    <UploadAvatar ref="uploadAvatarRef" @upload-success="uploadAvatarSuccess" />
    <UploadPassword ref="uploadPasswordRef" />
  </div>
</template>
<script>
export default {
  name: 'Layout',
  components: { FileUploader }
}
</script>
<script setup>
import { nextTick, ref } from 'vue'
import { AsideMenu } from './components/Menu'
import Avatar from '@/components/Avatar.vue'
import { SwapOutlined } from '@vicons/antd'
import VueCookie from 'vue-cookies'
import UploadAvatar from '@/components/UploadAvatar.vue'
import UploadPassword from '@/components/UploadPassword.vue'
import { logout } from '@/api/user'
import { useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import FileUploader from '@/components/FileUploader.vue'
import { useUserStore } from '@/store/modules/user'

const router = useRouter()
const userSore = useUserStore()
const timestamp = ref(0)
const uploadAvatarRef = ref()
const uploadPasswordRef = ref()
const uploaderRef = ref()
const asideMenuRef = ref()
const routerViewRef = ref()
const transferVisible = ref(false)
const userInfo = ref(VueCookie.get('userInfo'))

const addFile = (data) => {
  const { file, filePid } = data
  transferVisible.value = true
  uploaderRef.value.addFile(file, filePid)
}

// 上传文件回调
const uploadCallbackHandler = () => {
  nextTick(async () => {
    // 刷新表格数据
    routerViewRef.value?.reloadTable()
    // 更新用户空间
    await userSore.fetchUserSpace()
  })
}

const updateAvatar = () => {
  uploadAvatarRef.value.show(userInfo.value)
}
const updatePassword = () => {
  uploadPasswordRef.value.show()
}
const uploadAvatarSuccess = () => {
  delete userInfo.value.avatar
  VueCookie.set('userInfo', userInfo.value, 0)
  timestamp.value = new Date().getTime()
}
const handleLogout = () => {
  ElMessageBox.confirm('你确定要退出吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    draggable: true,
    type: 'info'
  })
    .then(async () => {
      await logout()
      VueCookie.remove('userInfo')
      router.push('login')
    })
    .catch(() => {})
}
</script>

<style lang="scss" scoped>
.framework {
  height: 100%;
  min-width: 1080px;
}
.header {
  box-shadow: 0 3px 10px 0 rgb(0 0 0 / 6%);
  height: 56px;
  padding-left: 24px;
  padding-right: 24px;
  position: relative;
  z-index: 200;
  display: flex;
  align-items: center;
  justify-content: space-between;
  .logo {
    display: flex;
    align-items: center;
    .icon-pan {
      font-size: 40px;
      color: #1296db;
    }
    .name {
      font-weight: bold;
      margin-left: 5px;
      font-size: 25px;
      color: #05a1f5;
    }
  }
  .right-panel {
    display: flex;
    align-items: center;
    .icon-transfer {
      cursor: pointer;
    }
    .btn-radius {
      border-radius: 32px;
      width: 28px;
      height: 28px;
      background: #f1f3f8;
      display: flex;
      justify-content: center;
      align-items: center;
      cursor: pointer;
      font-weight: bold;
      .rotate {
        transform: rotate(90deg);
      }
    }
    .user-info {
      margin-right: 10px;
      display: flex;
      align-items: center;
      cursor: pointer;
      .avatar {
        margin: 0px 5px 0px 15px;
      }
      .nick-name {
        color: #05a1f5;
      }
    }
  }
}
.body {
  height: calc(100% - 56px);
  display: flex;
  .body-content {
    flex: 1;
    width: 0;
    // padding: 20px 0 0;
  }
}
</style>
