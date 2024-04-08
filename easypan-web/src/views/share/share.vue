<template>
  <div class="share-container">
    <div class="share-header">
      <div class="top" v-if="selectFiles.length == 0">
        <span class="title">链接分享</span>
        <span class="content">(分享失败超过1年以上的链接记录将被自动清理)</span>
      </div>
      <div class="actions" v-else="selectFiles.length > 0">
        <FileAction :value="selectFiles" mode="btns" :actions="actions" />
      </div>
    </div>
    <div class="share-body">
      <!-- 左侧分享列表 -->
      <div class="left-container">
        <div class="nav">
          <span class="txt">全部文件</span>
          <span class="count">已加载{{ tableDataNum }}个</span>
        </div>
        <div class="file-list">
          <BasicTable
            ref="shareTableRef"
            :columns="columns"
            :row-key="(row) => row.id"
            :request="loadDataTable"
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
                  <Icon v-else="row.folderType == 1" icon-name="folder_2" :width="40" />
                </template>
                <span class="filename">
                  <span class="text" style="padding-left: 5px">{{ row.filename }}</span>
                </span>
              </div>
            </template>
            <template #RowAction="{ index, row }">
              <FileAction :value="row" mode="row" :actions="actions" />
            </template>
            <template #BrowseCount="{ index, row }">
              <span class="hover-hidden">{{ row.browseCount }}</span>
            </template>
          </BasicTable>
        </div>
      </div>
      <!-- 右侧分享文件详情 -->
      <div class="right-detail">
        <el-scrollbar wrap-style="position: absolute;left:0;right:0;">
          <ShareDetail :value="selectFiles" />
        </el-scrollbar>
      </div>
    </div>
  </div>
  <Dialog
    :show="dialogConfig.show"
    :title="dialogConfig.title"
    :buttons="dialogConfig.buttons"
    :show-cancel="true"
    width="400px"
    @close="dialogConfig.show = false"
  >
    <div class="delete-info">
      <div class="info">
        <span class="text"
          >取消分享后，该条分享记录将被删除，好友将无法再访问此分享链接。 您确认要取消分享吗？</span
        >
      </div>
    </div>
  </Dialog>
</template>

<script>
export default {
  name: 'Share'
}
</script>
<script setup>
import { ref, reactive } from 'vue'
import { columns } from './columns'
import Icon from '@/components/Icon.vue'
import { BasicTable } from '@/components/Table'
import ShareDetail from './detail.vue'
import { FileAction } from '@/components/FileAction'
import useClipboard from 'vue-clipboard3'
import Dialog from '@/components/Dialog.vue'
import { ElMessage } from 'element-plus'
import { isArray } from '@/utils/is'
import { loadShareList } from '@/api/share'

const emit = defineEmits(['addFile'])
const shareTableRef = ref()
const selectFiles = ref([])
const shareIds = ref([])
const tableDataNum = ref(0)
const { toClipboard } = useClipboard()
const shareUrl = ref(document.location.origin + '/share/')

const actions = [
  {
    title: '复制链接',
    label: 'copyLink',
    showLabel: true,
    icon: 'link',
    isShow: (data) => {
      return !isArray(data) || (isArray(data) && data.length == 1)
    },
    onClick: (data) => {
      copyLink2Clipboard(data)
    }
  },
  {
    title: '取消分享',
    label: 'cancelShare',
    showLabel: true,
    icon: 'stop',
    onClick: (data) => {
      cancelShare(data)
    }
  }
]

const dialogConfig = reactive({
  show: false,
  title: '确认取消分享',
  showCancel: true,
  buttons: [
    {
      type: 'primary',
      text: '确认',
      click: () => {
        deleteShare()
      }
    }
  ]
})

const loadDataTable = (res) => {
  return loadShareList(res)
}

function fetchSuccess({ resultTotal }) {
  tableDataNum.value = resultTotal
}

function reloadTable() {
  shareTableRef.value?.reload()
}

function handleTableRowSelect(rows) {
  selectFiles.value = rows
}

const cancelShare = (data) => {
  if (isArray(data)) {
    shareIds.value = data.map((item) => item.id).join(',')
  } else {
    shareIds.value = data.id
  }
  dialogConfig.show = true
}

const deleteShare = () => {
  ElMessage.success('取消外链分享成功')
  reloadTable()
  dialogConfig.show = false
}

const copyLink2Clipboard = async (data) => {
  if (isArray(data)) {
    data = data[0]
  }
  // 链接: https://pan.baidu.com/s/1OtdlYyBNGL0NPpIiLgyG3A 提取码: 93vd
  await toClipboard(`链接: ${shareUrl.value}${data.shareId} 提取码: ${data.code}`)
  ElMessage.success('复制成功')
}
</script>

<style lang="scss" scoped>
@import '@/assets/filelist.scss';
.share-container {
  height: 100%;
  padding: 10px 20px 0px 0px;
}
.share-header {
  height: 50px;
  padding-left: 20px;
  font-size: 12px;
  .top {
    display: inline-block;
    line-height: 36px;
  }
}

.share-body {
  height: calc(100% - 60px);

  .left-container {
    height: 100%;
    display: inline-block;
    width: calc(100% - 288px);
    .nav {
      font-size: 12px;
      color: #03081a;
      padding-left: 20px;
      display: flex;
      justify-content: space-between;

      .txt {
        font-weight: 700;
      }
      .count {
        color: #afb3bf;
      }
    }

    .file-list {
      padding-left: 4px;
    }
  }
  .right-detail {
    display: inline-block;
    position: relative;
    font-size: 12px;
    overflow: hidden;
    width: 272px;
    height: 100%;
    margin-left: 16px;
    background-color: #f5f6fa;
    border-radius: 8px;
  }
}
</style>
