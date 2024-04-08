<template>
  <div class="office-previewer">
    <el-scrollbar>
      <div class="doc-content" ref="docRef">
        <vue-office-pdf :options="options" :src="data" v-if="type === 4" />
        <vue-office-docx :src="data" v-if="type === 5" />
        <vue-office-excel
          style="height: calc(100vh - 60px)"
          :options="options"
          :src="data"
          v-if="type == 6"
        />
      </div>
    </el-scrollbar>
  </div>
</template>

<script>
export default {
  name: 'PreviewDoc'
}
</script>
<script setup>
import VueOfficeDocx from '@vue-office/docx'
import VueOfficeExcel from '@vue-office/excel'
import VueOfficePdf from '@vue-office/pdf'
//引入相关样式
import '@vue-office/excel/lib/index.css'
import { onMounted, ref } from 'vue'
import { getFileBolb } from '@/api/file'
const props = defineProps({
  url: {
    type: String
  },
  type: {
    type: Number
  }
})

const data = ref()
const options = ref({
  width: 794,
  minColLength: 10 // 有几列，就渲染几列
})
const initDoc = async () => {
  let result = await getFileBolb(props.url)
  if (!result) return
  data.value = result
}

onMounted(() => {
  initDoc()
})
</script>
<style lang="scss" scoped>
.office-previewer {
  padding: 60px 0 0 0;
  height: 100%;
  .doc-content {
    margin: 0 auto;
    :deep(.docx-wrapper),
    :deep(.vue-office-pdf-wrapper) {
      background: #f2f4f7 !important;
    }
    :deep(.docx-wrapper > section.docx) {
      box-shadow: rgba(158, 161, 165, 0.4) 0px 2px 12px 0px;
      margin-bottom: 19px;
    }
    :deep(.vue-office-pdf-wrapper canvas) {
      box-shadow: rgba(158, 161, 165, 0.4) 0px 2px 12px 0px;
      margin-bottom: 19px;
    }
  }
}
</style>
