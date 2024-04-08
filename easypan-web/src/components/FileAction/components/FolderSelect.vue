<template>
  <div>
    <Dialog
      :show="dialogConfig.show"
      :title="dialogConfig.title"
      :buttons="dialogConfig.buttons"
      :show-cancel="true"
      width="720px"
      @close="dialogConfig.show = false"
      :padding="0"
      :paddinglr="0"
    >
      <div class="selector-body">
        <div class="navigation-panel">
          <Navigation ref="navigationRef" :watch-path="false" @nav-change="navChagne" />
        </div>
        <div class="table">
          <el-scrollbar view-style="height:100%">
            <div class="folder-list" v-if="folderList.length > 0">
              <div class="folder-item" v-for="folder in folderList" @click="selectFolder(folder)">
                <Icon icon-name="folder_2" :width="40" />
                <span class="folder-name">{{ folder.filename }}</span>
              </div>
            </div>
            <div class="empty" v-else>
              <Icon icon-name="folder_empty" :width="90" />
              <p class="text">移动到{{ currentFolder.filename }}文件夹</p>
            </div>
          </el-scrollbar>
        </div>
      </div>
    </Dialog>
  </div>
</template>

<script>
export default {
  name: 'FolderSelect',
  components: { Navigation }
}
</script>
<script setup>
import { nextTick, reactive, ref } from 'vue'
import Dialog from '@/components/Dialog.vue'
import Icon from '@/components/Icon.vue'
import { isArray } from '@/utils/is'
import Navigation from '@/components/Navigation.vue'
import { loadAllFolderApi, changeFileFolderApi } from '@/api/file'
import { ElMessage } from 'element-plus'

const emit = defineEmits(['move-success'])
const navigationRef = ref()
const folderList = ref([])
const currentFolder = ref({})
let fileIds = []
let filePids = []
let params = {
  filePid: '0'
}
const dialogConfig = reactive({
  show: false,
  title: '移动到',
  showCancel: true,
  buttons: [
    {
      type: 'primary',
      text: '移动到此',
      round: true,
      click: (e) => {
        fileRemove()
      }
    }
  ]
})

const loadAllFolder = async () => {
  try {
    const res = await loadAllFolderApi(params)
    if (!res) return
    folderList.value = res
  } catch (error) {
    dialogConfig.show = false
  }
}

const navChagne = (data) => {
  const { curFolder } = data
  currentFolder.value = curFolder
  params.filePid = curFolder.id
  loadAllFolder()
}

const selectFolder = (data) => {
  navigationRef.value.openFolder(data)
}

const fileRemove = async () => {
  if (filePids.includes(params.filePid)) {
    return ElMessage.info('文件正在当前目录，无需移动')
  }
  await changeFileFolderApi({ filePid: params.filePid, ids: fileIds.join(',') })
  emit('move-success')
  dialogConfig.show = false
}

function show(data) {
  let _data = data
  if (!isArray(data)) {
    _data = [data]
  }
  fileIds = _data.map((item) => item.id)
  params = {
    filePid: '0'
  }
  filePids = [...new Set(_data.map((item) => item.filePid))]
  dialogConfig.show = true
  nextTick(() => {
    navigationRef.value.init()
  })
}

defineExpose({ show })
</script>
<style lang="scss" scoped>
.selector-body {
  height: 340px;
  overflow: hidden;
  .navigation-panel {
    background-color: #fafafc;
    border-radius: 4px 4px 0 0;
    height: 40px;
    line-height: 40px;
    border-bottom-width: 0;
    overflow: hidden;
    padding: 0 14px 0 24px;
    color: #afb3bf;
  }
  .table {
    height: calc(100% - 40px);
    .folder-list {
      .folder-item {
        cursor: pointer;
        padding-left: 24px;
        border-bottom: 1px solid #f9f9f9;
        height: 50px;
        line-height: 50px;
        color: #03081a;
        font-size: 12px;
        &:hover {
          background-color: #f7f9fc;
          border-color: #f7f9fc;
        }
        .folder-name {
          color: #03081a;
          padding-left: 5px;
          line-height: 40px;
          overflow: hidden;
          white-space: nowrap;
          text-overflow: ellipsis;
          &:hover {
            color: #06a7ff;
          }
        }
      }
    }
  }
  .empty {
    height: 100%;
    display: -webkit-box;
    display: -ms-flexbox;
    display: flex;
    flex-direction: column;
    -webkit-box-pack: center;
    -ms-flex-pack: center;
    justify-content: center;
    -webkit-box-align: center;
    -ms-flex-align: center;
    align-items: center;
    overflow: auto;
    text-align: center;

    .text {
      font-size: 14px;
      color: #afb3bf;
      line-height: 18px;
      margin-top: 8px;
    }
  }
}
</style>
