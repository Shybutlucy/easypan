<template>
  <div class="expand-menu">
    <div
      :class="['expand-menu-title', activePath == modelValue[0].path ? 'active' : '']"
      @click="jumpDefault"
    >
      <i
        :class="['iconfont icon-arrow-down animation', collapse ? '' : 'rotate']"
        @click="() => (collapse = !collapse)"
      ></i>
      <span class="text">{{ title }}</span>
      <div class="edit">
        <el-popover
          placement="right-start"
          trigger="click"
          :show-arrow="false"
          popper-style="padding: 14px 0px; width: 120px; min-width:120px;border-radius:8px"
        >
          <template #reference>
            <i class="iconfont icon-cate-edit"></i>
          </template>
          <template #default>
            <el-checkbox-group v-model="options" @change="handleCheckedChange">
              <template v-for="item in modelValue">
                <el-checkbox
                  size="large"
                  class="check-item"
                  :label="item.name"
                  v-if="item.isShow == undefined ? true : item.isShow"
                />
              </template>
            </el-checkbox-group>
          </template>
        </el-popover>
      </div>
    </div>
    <div class="expand-menu-list" v-show="collapse">
      <template v-for="sub in subMenu">
        <div
          v-if="sub.isShow == undefined ? true : sub.isShow"
          :class="['expand-menu-item', sub.path == activePath ? 'active' : '']"
          @click="jump(sub)"
        >
          <div class="body">
            <span :class="['iconfont', 'icon-' + sub.icon]" v-if="sub.icon"></span>
            <span class="text">{{ sub.name }}</span>
          </div>
        </div>
      </template>
    </div>
  </div>
</template>
<script>
export default {
  name: 'ExpandMenu'
}
</script>

<script setup>
import { onMounted, ref } from 'vue'

const props = defineProps({
  modelValue: {
    type: Array
  },
  title: {
    type: String
  },
  activePath: {
    type: String
  }
})
// 拷贝一份，方便增删
const subMenu = ref(props.modelValue)
const collapse = ref(true)
const options = ref([])
const emit = defineEmits()

onMounted(() => {
  options.value = props.modelValue.map((item) => {
    if (item.isShow == undefined || item.isShow) {
      return item.name
    }
  })
})

function handleCheckedChange(checked) {
  // 过滤掉menus
  const newList = props.modelValue.filter((item) => checked.includes(item.name))
  subMenu.value = newList
}

function jumpDefault() {
  if (props.modelValue && props.modelValue.length > 0) {
    jump(props.modelValue[0])
  }
}

function jump(sub) {
  emit('item-click', { path: sub.path, key: sub.name })
}
</script>
<style lang="scss" scoped>
.check-item {
  width: 120px;
  margin-right: 0;
  padding: 8px 0px 8px 25px;
}
.expand-menu {
  .expand-menu-title {
    height: 40px;
    line-height: 40px;
    display: inline-block;
    width: 177px;
    border-radius: 10px;
    padding-left: 24px;
    padding-right: 18px;
    margin-left: 12px;
    font-weight: 700;
    cursor: pointer;
    color: #636d7e;
    .iconfont {
      display: inline-block;
      vertical-align: middle;
    }
    .animation {
      transition: transform 0.1s ease-in-out;
    }
    .rotate {
      transform: rotate(-90deg);
    }
    .edit {
      float: right;
    }
    .text {
      margin-left: 10px;
      font-size: 14px;
    }
  }
  .active {
    background-color: #eef9fe;
    color: #06a7ff;
  }

  .expand-menu-list {
    .expand-menu-item {
      height: 40px;
      line-height: 40px;
      border-radius: 12px;
      margin-left: 12px;
      width: 177px;
      cursor: pointer;
      color: #636d7e;
      &:hover {
        background: #fafafa;
      }
      .body {
        padding-left: 50px;
        .iconfont {
          font-size: 14px;
          margin-right: 13px;
          font-weight: 600;
        }
        .text {
          font-size: 12px;
        }
      }
    }
    .active {
      background-color: #eef9fe;
      .body {
        padding-left: 50px;
        .iconfont,
        .text {
          color: #06a7ff;
        }
      }
    }
  }
}
</style>
