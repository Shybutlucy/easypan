<template>
  <div class="main">
    <div class="main-header">
      <div class="actions">
        <div class="search">
          <el-input
            v-model="filenameFuzzy"
            size="small"
            clearable
            placeholder="搜索我的文件"
            input-style="height: 32px"
            @keyup.enter="searchFile"
            @clear="searchFile"
          >
            <template #suffix>
              <span class="text" @click="searchFile">搜索</span>
            </template>
          </el-input>
        </div>
        <div class="file-action" v-show="selectedRow.length > 0">
          <FileAction :value="selectedRow" :actions="actions" />
        </div>
      </div>
    </div>
    <div class="main-body">
      <div class="nav">
        <div class="nav-list">
          <div class="nav-left">
            <Navigation ref="navigationRef" @nav-change="navChange" />
          </div>
        </div>
      </div>
      <div class="file-list">
        <BasicTable
          ref="dataTableRef"
          :columns="canEditColumns"
          :row-key="(row) => row.id"
          :request="loadDataTable"
          :loading="true"
          @select-change="handleTableRowSelect"
        >
          <template #filename="{ index, row }">
            <div class="file-item">
              <template v-if="(row.fileType == 3 || row.fileType == 1) && row.status == 2">
                <!-- 视频，图片，且转码成功才有封面 -->
                <Icon :cover="row.fileCover" :width="32" />
              </template>
              <template v-else>
                <Icon v-if="row.folderType == 0" :file-type="row.fileType" />
                <!-- 文件夹 -->
                <Icon v-else="row.folderType == 1" :file-type="0" />
              </template>
              <span class="file-name" v-if="!row.showEdit">
                <span class="text" @click.stop="openFile(row)">{{ row.filename }}</span>
              </span>
            </div>
          </template>
          <template #RowAction="{ index, row }">
            <FileAction
              mode="row"
              :value="row"
              field="updateTime"
              :key="row.showEdit"
              :actions="actions"
            />
          </template>
          <template #fileSize="{ index, row }">
            <div class="file-size">
              <span v-if="row.fileSize">{{ size2Str(row.fileSize) }}</span>
              <span v-else> - </span>
            </div>
          </template>
          <template #empty>
            <div class="table-empty">
              <div class="main-empty">
                <div class="u-empty">
                  <div class="empty-image">
                    <Icon icon-name="empty" :width="120" height="auto" />
                  </div>
                  <div class="empty-description">
                    <span class="text">当前列表为空，上传你的第一个文件吧</span>
                  </div>
                  <div class="empty-bottom">
                    <div class="empty-action"></div>
                  </div>
                </div>
              </div>
            </div>
          </template>
        </BasicTable>
      </div>
    </div>
    <Preview ref="previewRef" />
    <DeleteFile ref="deleteFileRef" @delete-success="reloadTable" />
  </div>
</template>

<script>
export default {
  name: 'Main',
  components: { Preview }
}
</script>
<script setup>
import Icon from '@/components/Icon.vue'
import { BasicTable } from '@/components/Table'
import { reactive, ref } from 'vue'
import { size2Str } from '@/utils'
import { ElMessage } from 'element-plus'
import Navigation from '@/components/Navigation.vue'
import { loadDataList, createDownloadUrl } from '@/api/file'
import Preview from '@/components/preview/Preview.vue'
import { FileAction, DeleteFile } from '@/components/FileAction'
import { createActions } from './actions'
import fileSetting from '@/settings/fileSetting'
import { isArray } from '@/utils/is'

const columns = [
  {
    label: '文件名',
    prop: 'filename',
    scopedSlots: 'filename',
    ellipsis: true,
    minWidth: '40%'
  },
  {
    label: '发布人',
    prop: 'nickname',
    minWidth: '20%'
  },
  {
    label: '修改时间',
    prop: 'updateTime',
    minWidth: '20%',
    scopedSlots: 'RowAction'
  },
  {
    label: '大小',
    prop: 'fileSize',
    scopedSlots: 'fileSize',
    minWidth: '20%'
  }
]

const emit = defineEmits(['addFile'])
const { fileUrlMap } = fileSetting
const dataTableRef = ref()
const navigationRef = ref()
const previewRef = ref()
const deleteFileRef = ref()
const filenameFuzzy = ref()
const selectedRow = ref([])
const currentFolder = ref({ id: '0' })
const canEditColumns = ref(columns)
const params = reactive({
  page: 1,
  pageSize: 20,
  filePid: '0'
})

// 表格数据的操作
const actions = createActions({
  deleteFile: (data) => {
    // 批量删除
    deleteFileRef.value.show(data)
  },
  downloadFile: async (data) => {
    handleDownloadFile(data)
  }
})

function reloadTable() {
  dataTableRef.value?.reload()
}

function navChange(data) {
  const { curFolder } = data
  params.filePid = curFolder.id
  currentFolder.value = curFolder
  reloadTable()
}

const loadDataTable = async (res) => {
  const param = { ...res, ...params }
  try {
    return loadDataList(param)
  } catch (error) {
    return []
  }
}

const handleTableRowSelect = (rows) => {
  if (rows.length > 0) {
    canEditColumns.value[0].label = `已选中${rows.length}个文件/文件夹`
  } else {
    canEditColumns.value[0].label = '文件名'
  }
  const newRows = rows.map((item) => {
    item.id = item.userId + '_' + item.id
    return item
  })
  selectedRow.value = newRows
}

// 搜索文件
const searchFile = () => {
  params.filename = filenameFuzzy.value
  reloadTable()
}
// 打开文件夹或者预览文件
const openFile = (row) => {
  if (row.folderType == 1) {
    navigationRef.value.openFolder(row)
    return
  }
  // 文件
  if (row.status != 2) {
    ElMessage.warning('文件未完成转码，无法预览')
    return
  }
  previewRef.value.showPreview(row, 1)
}

// 下载链接
const handleDownloadFile = async (data) => {
  if (isArray(data)) return
  const url = fileUrlMap[1].createDownloadUrl + '/' + data.userId + '/' + data.id
  const res = await createDownloadUrl(url)
  if (!res) return
  window.location.href = fileUrlMap[1].downloadUrl + '/' + res.code
}

defineExpose({ reloadTable })
</script>
<style lang="scss" scoped>
@import '@/assets/filelist.scss';
.main {
  padding-top: 20px;
}
.main-header {
  height: 40px;
  padding: 4px 24px;
  .actions {
    display: flex;
    .el-icon {
      margin-right: 6px;
    }
    .search {
      width: 270px;
      margin-right: 16px;
      :deep(.el-input__wrapper) {
        border-radius: 18px;
        padding: 1px 15px;
      }

      .text {
        cursor: pointer;
      }
    }
  }
  .toolbar {
    display: flex;
    align-items: center;
    .toolbar-action {
      width: 100%;

      .toolbar-group {
        display: flex;
        color: #fff;

        .action-list {
          :deep(button) {
            background-color: #f0faff;
            padding: 0 12px;
          }

          :deep(button.need-left-step) {
            &::after {
              height: 12px;
              width: 1px;
              position: absolute;
              background-color: rgba(6, 167, 255, 0.23);
              content: ' ';
              left: 0;
              top: 50%;
              margin-top: -6px;
            }
          }
        }
      }

      .action-main {
        margin-right: 16px;
      }
    }
  }
}

.main-body {
  height: calc(100% - 40px);
  .nav {
    width: 100%;
    padding: 0 0 0 24px;
    box-sizing: border-box;
    height: 40px;

    .nav-list {
      height: 40px;
      line-height: 40px;
      position: relative;
      border-radius: 4px 4px 0 0;
      overflow: hidden;

      .nav-left {
        float: left;
        color: #03081a;
      }
    }
  }
  .file-list {
    padding-left: 8px;
    height: calc(100% - 40px);
  }
  .table-empty {
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    overflow: auto;
  }
}
</style>
