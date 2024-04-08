<template>
  <el-popover
    placement="top"
    :visible="visible"
    :content="content"
    effect="light"
    :offset="5"
    :show-arrow="false"
    :hide-after="0"
    popper-style="font-size: 12px;min-width: auto;width:auto;padding: 8px 10px"
  >
    <template #reference>
      <slot></slot>
    </template>
  </el-popover>
</template>

<script>
export default {
  name: 'Popover'
}
</script>
<script setup>
const props = defineProps({
  visible: {
    type: [Boolean, null],
    default: null
  },
  content: {
    type: String
  }
})
</script>
<style lang="scss" scoped></style>
