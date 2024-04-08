<template>
  <div class="avatar-upload">
    <div class="avatar-show">
      <template v-if="localFile">
        <img :src="localFile" />
      </template>
      <template v-else>
        <img :src="`${modelValue.qqAvatar}`" v-if="modelValue && modelValue.qqAvatar" />
        <img :src="`${proxy.globalInfo.avatarUrl}${modelValue.userId}?${timestamp}`" v-else />
      </template>
    </div>
    <div class="select-btn">
      <el-upload
        name="file"
        :show-file-list="false"
        accept=".png,.PNG,.jpg,.JPG,.jpeg,.gif,.GIF,.bmp,.MP"
        :multiple="false"
        :http-request="handleUploadFile"
      >
        <el-button type="primary">选择</el-button>
      </el-upload>
    </div>
  </div>
</template>
<script>
export default {
  name: 'AvatarUpload'
}
</script>
<script setup>
import { getCurrentInstance, ref } from 'vue'

const { proxy } = getCurrentInstance()
const props = defineProps({
  modelValue: {
    type: Object,
    default: null
  }
})

const timestamp = ref(new Date())
const localFile = ref(null)
const emit = defineEmits()
const handleUploadFile = async (file) => {
  file = file.file
  let img = new FileReader()
  img.readAsDataURL(file)
  img.onload = ({ target }) => {
    localFile.value = target.result
  }
  emit('update:modelValue', file)
}
</script>
<style lang="scss">
.avatar-upload {
  display: flex;
  justify-content: center;
  align-items: end;
  .avatar-show {
    background-color: rgb(245, 245, 245);
    width: 120px;
    height: 120px;
    display: flex;
    align-items: center;
    justify-content: center;
    overflow: hidden;
    position: relative;
    .iconfont {
      font-size: 50px;
      color: #ddd;
    }
    img {
      width: 100%;
      height: 100%;
    }
    .op {
      position: absolute;
      color: #0e8aef;
      top: 80px;
    }
  }
  .select-btn {
    margin-left: 10px;
    vertical-align: bottom;
  }
}
</style>
