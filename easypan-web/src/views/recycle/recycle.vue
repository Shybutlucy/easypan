<template>
  <div class="recycle">
    <div class="recycle-header">
      <div class="actions">
        <el-button type="primary" round style="padding: 8px 24px">
          <el-icon size="16"><RestOutlined /></el-icon>
          清空回收站</el-button
        >
        <div class="action-more" v-show="selectFiles.length > 0">
          <FileAction :value="selectFiles" :actions="actions" />
        </div>
      </div>
    </div>
    <div class="nav">
      <span class="txt">回收站</span>
      <span class="count">已加载{{ tableDataNum }}个</span>
    </div>
    <div class="recycle-body">
      <div class="file-list">
        <BasicTable
          ref="recycleTableRef"
          :columns="canEditColumns"
          :row-key="(row) => row.id"
          :request="loadDataTable"
          :loading="true"
          @select-change="handleTableRowSelect"
          @fetch-success="fetchSuccess"
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
          <template #fileSize="{ index, row }">
            <div class="file-size">
              <span v-if="row.fileSize">{{ size2Str(row.fileSize) }}</span>
              <span v-else> - </span>
            </div>
          </template>
          <template #RowAction="{ index, row }">
            <FileAction mode="row" :value="row" field="recoveryTime" :actions="actions" />
          </template>
        </BasicTable>
      </div>
    </div>
    <Dialog
      :show="dialogConfig.show"
      :title="dialogTitle"
      :buttons="dialogConfig.buttons"
      :show-cancel="true"
      width="400px"
      @close="dialogConfig.show = false"
    >
      <div class="delete-info">
        <div class="info">
          <span class="text">{{ dialogContent }}</span>
        </div>
      </div>
    </Dialog>
  </div>
</template>
<script>
export default {
  name: 'Recycle'
}
</script>
<script setup>
import { computed, ref, reactive } from 'vue'
import { columns } from './columns'
import { BasicTable } from '@/components/Table'
import { FileAction } from '@/components/FileAction'
import Icon from '@/components/Icon.vue'
import { size2Str } from '@/utils'
import { RestOutlined } from '@vicons/antd'
import { isArray } from '@/utils/is'
import { ElMessage } from 'element-plus'
import Dialog from '@/components/Dialog.vue'
import { loadDataList, recoveryFileApi, delFileApi } from '@/api/recycle'

const recycleTableRef = ref()
const selectFiles = ref([])
const canEditColumns = ref(columns)
const tableDataNum = ref(0)
const flag = ref(1) // 0恢复，1彻底删除
const ids = ref()

const dialogTitle = computed(() => {
  return flag.value ? '彻底删除' : '确认还原'
})
const dialogContent = computed(() => {
  return flag.value ? '文件删除后将无法恢复，您确认要彻底删除所选文件吗？' : '确认还原选中的文件？'
})

const actions = [
  {
    title: '还原',
    label: 'copyLink',
    showLabel: true,
    icon: 'recovery',
    onClick: (data) => {
      showDialog(data, 0)
    }
  },
  {
    title: '删除',
    label: 'delete',
    showLabel: true,
    icon: 'delete',
    onClick: (data) => {
      showDialog(data, 1)
    }
  }
]

const dialogConfig = reactive({
  show: false,
  showCancel: true,
  buttons: [
    {
      type: 'primary',
      text: '确认',
      click: () => {
        confirmDothing()
      }
    }
  ]
})

function showDialog(data, f) {
  flag.value = f
  if (!isArray(data)) {
    ids.value = data.id
  } else {
    ids.value = data.map((item) => item.id).join(',')
  }
  dialogConfig.show = true
}

function reloadTable() {
  recycleTableRef.value?.reload()
}

const loadDataTable = (res) => {
  const param = { ...res }
  try {
    return loadDataList(param)
  } catch (error) {
    return []
  }
}

function fetchSuccess({ totalData }) {
  tableDataNum.value = totalData
}

function handleTableRowSelect(rows) {
  if (rows.length > 0) {
    canEditColumns.value[0].label = `已选中${rows.length}个文件/文件夹`
  } else {
    canEditColumns.value[0].label = '文件名'
  }
  selectFiles.value = rows
}

const confirmDothing = async () => {
  if (flag.value) {
    try {
      await delFileApi(ids.value)
      ElMessage.success('文件已彻底删除')
    } catch (error) {
      ElMessage.error('操作失败')
    }
  } else {
    try {
      await recoveryFileApi(ids.value)
      ElMessage.success('文件已恢复')
    } catch (error) {
      ElMessage.error('操作失败')
    }
  }
  dialogConfig.show = false
  reloadTable()
}
</script>
<style lang="scss" scoped>
@import '@/assets/filelist.scss';
.recycle {
  height: 100%;
  width: 100%;
  min-width: 750px;
  .recycle-header {
    padding: 10px 20px;
    .actions {
      display: flex;
      align-items: center;
      .action-more {
        margin-left: 16px;
      }
    }
  }
  .nav {
    font-size: 12px;
    color: #03081a;
    padding: 0px 20px;
    display: flex;
    justify-content: space-between;
    .txt {
      font-weight: 700;
    }
    .count {
      color: #afb3bf;
    }
  }
  .recycle-body {
    height: calc(100% - 75px);
    .file-list {
      padding-left: 4px;
      padding-right: 20px;
    }
  }
}
</style>
