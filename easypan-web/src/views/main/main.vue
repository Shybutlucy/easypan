<template>
  <div class="main">
    <div class="main-header">
      <div class="toolbar">
        <div class="toolbar-action">
          <div class="toolbar-group">
            <div class="toolbar-group-item" v-show="selectedRow.length == 0">
              <div class="action-main">
                <el-upload
                  :show-file-list="false"
                  :with-credentials="true"
                  :multiple="true"
                  :http-request="addFile"
                  :accept="fileAccept"
                >
                  <el-button type="primary" round style="padding: 8px 24px">
                    <el-icon size="16">
                      <UploadOutlined />
                    </el-icon>
                    上传</el-button
                  >
                </el-upload>
              </div>
            </div>
            <div class="toolbar-group-item" v-show="selectedRow.length == 0">
              <div class="action-list">
                <el-button-group>
                  <el-button
                    text
                    round
                    type="primary"
                    bg
                    @click="newFolder"
                    v-show="params.category == 'all'"
                  >
                    <el-icon size="16"> <FolderAddOutlined /> </el-icon>新建文件夹
                  </el-button>
                  <!-- <el-button text round bg type="primary" class="need-left-step">
                  <el-icon class="el-icon--right"><FileAddOutlined /></el-icon>新建在线文档
                </el-button>
                <el-button text round bg type="primary" class="need-left-step">
                  <el-icon class="el-icon--right"><DownloadOutlined /></el-icon>离线下载
                </el-button> -->
                </el-button-group>
              </div>
            </div>
            <div class="toolbar-group-item" v-show="selectedRow.length > 0">
              <FileAction :value="selectedRow" :actions="actions" />
            </div>
          </div>
        </div>
        <div class="toolbar-customize">
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
      <div class="file-list overflow-visible">
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
              <!-- 编辑框 -->
              <div class="edit-panel" v-if="row.showEdit" @click.stop="">
                <el-input
                  v-model.trim="row.filenameReal"
                  ref="editNameRef"
                  size="small"
                  :maxLength="190"
                  @keyup.enter="saveNameEdit(index)"
                >
                  <template #suffix>
                    {{ row.fileSuffix }}
                  </template>
                </el-input>
                <div class="edit-action is-confirm">
                  <span class="iconfont icon-check" @click="saveNameEdit(index)"></span>
                </div>
                <div class="edit-action is-cancel">
                  <span class="iconfont icon-close" @click="cancelNameEdit(index)"></span>
                </div>
              </div>
            </div>
          </template>
          <template #RowAction="{ index, row }">
            <FileAction
              mode="row"
              :value="row"
              field="updateTime"
              :key="row.showEdit"
              :actions="actions"
              :offset="-130"
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
                    <div class="empty-action">
                      <el-upload
                        :show-file-list="false"
                        :with-credentials="true"
                        :multiple="true"
                        :http-request="addFile"
                        :accept="fileAccept"
                      >
                        <div class="action-item">
                          <div class="action-item-image">
                            <Icon icon-name="empty_upload" :width="42" />
                          </div>
                          <div class="action-item-text">上传文件</div>
                        </div>
                      </el-upload>

                      <div class="action-item" @click="newFolder" v-show="params.category == 'all'">
                        <div class="action-item-image">
                          <Icon icon-name="empty_newFolder" :width="42" />
                        </div>
                        <div class="action-item-text">新建文件夹</div>
                      </div>
                    </div>
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
    <FolderSelect ref="moveFileRef" @move-success="reloadTable" />
    <ShareFile ref="shareFileRef" />
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
import { FolderAddOutlined, UploadOutlined } from '@vicons/antd'
import { computed, nextTick, reactive, ref } from 'vue'
import { size2Str, dateFormat } from '@/utils'
import { ElMessage } from 'element-plus'
import Navigation from '@/components/Navigation.vue'
import { loadDataList, newFolderApi, renameApi, createDownloadUrl } from '@/api/file'
import CategorySetting from '@/settings/categorySetting'
import Preview from '@/components/preview/Preview.vue'
import { FileAction, ShareFile, FolderSelect, DeleteFile } from '@/components/FileAction'
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
    label: '修改时间',
    prop: 'updateTime',
    minWidth: '25%',
    scopedSlots: 'RowAction'
  },
  {
    label: '大小',
    prop: 'fileSize',
    scopedSlots: 'fileSize',
    minWidth: '23%'
  }
]

const emit = defineEmits(['addFile'])
const { fileUrlMap } = fileSetting
const dataTableRef = ref()
const editNameRef = ref()
const navigationRef = ref()
const previewRef = ref()
const deleteFileRef = ref()
const moveFileRef = ref()
const shareFileRef = ref()
const editing = ref(false)
const filenameFuzzy = ref()
const selectedRow = ref([])
const currentFolder = ref({ id: '0' })
const canEditColumns = ref(columns)
const params = reactive({
  page: 1,
  pageSize: 20,
  filePid: '0',
  category: 'all'
})

// 表格数据的操作
const actions = createActions({
  deleteFile: (data) => {
    // 批量删除
    deleteFileRef.value.show(data)
  },
  renameFile: (data) => {
    renameFile(data)
  },
  moveFile: (data) => {
    moveFileRef.value.show(data)
  },
  downloadFile: async (data) => {
    handleDownloadFile(data)
  },
  shareFile: (data) => {
    shareFileRef.value.show(data)
  }
})

const fileAccept = computed(() => {
  const categoryItem = CategorySetting[params.category]
  return categoryItem ? categoryItem.accept : '*'
})

function reloadTable() {
  dataTableRef.value?.reload()
}

function navChange(data) {
  const { category, curFolder } = data
  params.category = category
  params.filePid = curFolder.id
  currentFolder.value = curFolder
  reloadTable()
}

const loadDataTable = async (res) => {
  const param = { ...res, ...params }
  if (param.category !== 'all') {
    delete param.filePid
  }
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
  selectedRow.value = rows
}

const addFile = (fileData) => {
  emit('addFile', { file: fileData.file, filePid: currentFolder.value.id })
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
  previewRef.value.showPreview(row, 0)
}

// 新建文件夹
const newFolder = () => {
  if (editing.value) {
    return
  }
  dataTableRef.value.setRowFieldValue('showEdit', false)
  editing.value = true
  dataTableRef.value.unshiftRow({
    showEdit: true,
    folderType: 1,
    id: '',
    filePid: currentFolder.value.id,
    filenameReal: '',
    updateTime: dateFormat('YY-mm-dd HH:MM', new Date())
  })
  nextTick(() => {
    editNameRef.value.focus()
  })
}
// 重命名文件
const renameFile = (row) => {
  if (isArray(row)) return
  if (dataTableRef.value.tableElRef.data[0].id == '') {
    dataTableRef.value.tableElRef.data.splice(0, 1)
  }
  dataTableRef.value.setRowFieldValue('showEdit', false)
  row.showEdit = true
  editing.value = true
  // 编辑文件
  if (row.folderType == 0) {
    // 拿到文件名
    row.filenameReal = row.filename?.substring(0, row.filename.lastIndexOf('.'))
    row.fileSuffix = row.filename?.substring(row.filename.lastIndexOf('.'))
  } else {
    row.filenameReal = row.filename
    row.fileSuffix = ''
  }
  nextTick(() => {
    editNameRef.value = true
  })
}
// 取消重命名
const cancelNameEdit = (index) => {
  const fileData = dataTableRef.value.tableElRef.data[index]
  if (fileData.id) {
    fileData.showEdit = false
  } else {
    dataTableRef.value.tableElRef.data.splice(index, 1)
  }
  editing.value = false
}

// 保存文件名
const saveNameEdit = async (index) => {
  dataTableRef.value.setLoading(true)
  const { id, filePid, folderType, filenameReal } = dataTableRef.value.tableElRef.data[index]
  if (filenameReal == '' || filenameReal.indexOf('/') != -1) {
    ElMessage.warning(`文件${folderType == 1 ? '夹' : ''}名不能为空，且不能含有 '/'`)
    return
  }
  if (id == '') {
    // 新建文件
    try {
      const res = await newFolderApi({ filePid, filename: filenameReal })
      dataTableRef.value.setRowValue(res, 0)
    } catch (error) {
      cancelNameEdit(0)
    }
  } else {
    // 重命名
    try {
      const res = await renameApi({ id, filename: filenameReal })
      dataTableRef.value.setRowValue(res, index)
    } catch (error) {
      cancelNameEdit(0)
    }
  }
  editing.value = false
  dataTableRef.value.setLoading(false)
}

// 下载链接
const handleDownloadFile = async (data) => {
  if (isArray(data)) return
  const url = fileUrlMap[0].createDownloadUrl + '/' + data.id
  const res = await createDownloadUrl(url)
  if (!res) return
  window.location.href = fileUrlMap[0].downloadUrl + '/' + res.code
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
  .toolbar {
    display: flex;
    align-items: center;
    .toolbar-action {
      width: 100%;

      .toolbar-group {
        display: flex;
        color: #fff;

        .el-icon {
          margin-right: 6px;
        }

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

    .toolbar-customize {
      .search {
        width: 270px;

        :deep(.el-input__wrapper) {
          border-radius: 18px;
          padding: 1px 15px;
        }

        .text {
          cursor: pointer;
        }
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
