<template>
  <div class="main-user">
    <div class="main-header">
      <div class="actions">
        <div class="select">
          <el-select
            v-model="searchParam.select"
            class="status-select"
            placeholder="状态"
            :teleported="false"
            style="width: 80px"
          >
            <el-option label="启用" value="1" />
            <el-option label="禁用" value="0" />
          </el-select>
        </div>
        <div class="search">
          <el-input
            v-model.trim="searchParam.filename"
            size="small"
            clearable
            placeholder="搜索我的文件"
            input-style="height: 32px"
            @keyup.enter="searchFile"
            @clear="searchFile"
            class="input-with-select"
          >
            <template #suffix>
              <span class="text" @click="searchFile">搜索</span>
            </template>
          </el-input>
        </div>
      </div>
    </div>
    <div class="main-body">
      <div class="file-list">
        <BasicTable
          ref="userTableRef"
          :columns="canEditColumns"
          :row-key="(row) => row.id"
          :request="loadDataTable"
          :loading="true"
          @select-change="handleTableRowSelect"
        >
          <template #avatar="{ index, row}">
            <Avatar :userId="row.id" :avatar="row.qqAvatar" />
          </template>
          <template #space="{ index, row}">
            {{ size2Str(row.useSpace) }} / {{ size2Str(row.totalSpace) }}
          </template>
          <template #status="{ index, row}">
            <span v-if="row.status == 1" style="color: #529b2e">启用</span>
            <span v-if="row.status == 0" style="color: #f56c62">禁用</span>
          </template>
          <template #op="{ index, row }">
            <el-button link type="primary" style="font-size: 12px;" @click.stop="updateSpace(row)">分配空间</el-button>
            <el-divider direction="vertical" />
            
            <el-button link :type="row.status == 0 ? 'success' : 'danger'" style="font-size: 12px;" @click.stop="updateUserStatus(row)">
              {{ row.status == 0 ? '启用': '禁用' }}
            </el-button>
          </template>
        </BasicTable>
      </div>
    </div>
  </div>
</template>

<script>
export default {
    name: "UserSetting",
}
</script>
<script setup>
import { reactive, ref } from 'vue'
import { columns } from './columns'
import { BasicTable } from '@/components/Table';
import Avatar from '@/components/Avatar.vue';
import { size2Str } from '@/utils';

const userTableRef = ref()
const selectedRow = ref([])
const canEditColumns = ref(columns)
const searchParam = reactive({
  select: '',
  filename: ''
})

const loadDataTable = (res) => {
  const params = { ...res, ...searchParam }
  return {
    pages: 2,
    records: [
      {
        id: '1',
        avatar: 'test',
        nickname: 'test',
        email: 'test@qq.com',
        useSpace: 10240000,
        totalSpace: 20480000,
        createTime: '2023-04-20 09:57',
        lastLoginTime: '2023-04-20 09:57',
        status: '1'
      },
      {
        id: '2',
        avatar: 'swcode',
        nickname: 'swcode',
        email: 'swcode@qq.com',
        useSpace: 10240000,
        totalSpace: 20480000,
        createTime: '2023-07-02 20:24',
        lastLoginTime: '2023-07-02 20:24',
        status: '0'
      },
    ]
  }
}

function reloadTable() {
  userTableRef.value?.reload()
}


const handleTableRowSelect = (rows) => {
  if (rows.length > 0) {
    canEditColumns.value[0].label = `已选中${rows.length}个用户`
  } else {
    canEditColumns.value[0].label = '昵称'
  }
  const newRows = rows.map((item) => {
    item.id = item.userId + '_' + item.id
    return item
  })
  selectedRow.value = newRows
}

const updateSpace = (row) => {}
const updateUserStatus = (row) => {}

const searchFile = () => {}
</script>
<style lang="scss" scoped>
@import '@/assets/filelist.scss';
.main-user {
  padding-top: 20px;
  .main-header {
    height: 40px;
    padding: 4px 24px;
    .actions {
      display: flex;
    }
    .select {
      :deep(.el-input__wrapper) {
        border-radius: 18px;
        padding-left: 15px;
      }
      .status-select :deep(.el-popper) {
        border-radius: 8px;
      }
    }
    .search {
      margin-left: 15px;
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
}
</style>
