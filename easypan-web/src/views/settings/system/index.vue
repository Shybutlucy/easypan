<template>
  <div class="main-setting">
    <el-form
      :model="formData"
      :rules="rules"
      ref="formDataRef"
      label-width="100px"
      @submit.prevent
    >
      <el-form-item label="注册邮箱标题" props="registerEmailTitle">
        <el-input
          clearable
          placeholder="注册邮箱验证码邮件标题"
          v-model.trim="formData.registerEmailTitle"
        />
      </el-form-item>
      <el-form-item label="注册邮箱内容" props="registerEmailContent">
        <el-input
          clearable
          placeholder="注册邮箱验证码邮件内容"
          v-model.trim="formData.registerEmailContent"
        />
      </el-form-item>
      <el-form-item label="初始空间大小" props="useInitUserSpace">
        <el-input
          clearable
          placeholder="初始空间大小"
          v-model.trim="formData.userInitUserSpace"
        >
        <template #suffix>MB</template>
      </el-input>
    </el-form-item>
    <el-form-item style="float: right;">
      <el-button type="primary" @click="saveSettings">保存</el-button>
    </el-form-item>
    </el-form>
  </div>
</template>

<script>
export default {
  name: 'SystemSetting'
}
</script>
<script setup>
import Verify from '@/utils/Verify';
import { ref } from 'vue';

const rules = {
  registerEmailTitle: { required: true, message: '请输入注册邮箱验证码邮件标题' },
  registerEmailContent: { required: true, message: '请输入注册邮箱验证码邮件内容' },
  userInitUserSpace: [{ required: true, message: '请输入初始空间大小' },
  { validator: Verify.number, message: '空间大小只能是数字' }]
}

const formData = ref({})
const formDataRef = ref()


const saveSettings = () => {}
</script>
<style lang="scss" scoped>
.main-setting {
  padding: 20px 24px;
  width: 600px;
}

</style>
