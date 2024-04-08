<template>
  <div class="code-previewer">
    <el-scrollbar>
      <div class="code">
        <div ref="editorRef"></div>
        <div class="right-bar"></div>
      </div>
    </el-scrollbar>
    <div class="bottom-tools">
      <div class="left"></div>
      <div class="right">
        <div :class="['item encoder', isSelected ? 'selected' : '']">
          <Popover :visible="visible" content="设置编码">
            <el-dropdown trigger="click" @command="itemSelected" @visible-change="onVisibleChange">
              <div
                class="encoder-text"
                @mouseenter="visible = !isSelected"
                @mouseleave="visible = false"
              >
                {{ encode }}
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="UTF-8">UTF-8</el-dropdown-item>
                  <el-dropdown-item command="GBK">GBK</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </Popover>
        </div>
        <div class="item copyclip" @click="copy2Clipboard">
          <Popover content="复制">
            <div class="icon-btn"><span class="icons iconfont icon-copy-thin"></span></div>
          </Popover>
        </div>
        <div class="item full-screen">
          <Popover content="全屏">
            <div class="icon-btn"><span class="icons icons-full_screen"></span></div>
          </Popover>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'PreviewCode'
}
</script>
<script setup>
import { basicSetup, EditorView } from 'codemirror'
import { nextTick, onMounted, ref } from 'vue'
import { getFileBolb } from '@/api/file'
import Popover from './Popover.vue'
import { html } from '@codemirror/lang-html'
import { vue } from '@codemirror/lang-vue'
import { json } from '@codemirror/lang-json'
import { sql } from '@codemirror/lang-sql'
import { xml } from '@codemirror/lang-xml'
import useClipboard from 'vue-clipboard3'
import { ElMessage } from 'element-plus'
const props = defineProps({
  url: {
    type: String
  },
  language: {
    type: String
  }
})
const modes = { html: html, vue: vue, json: json, sql: sql, xml: xml }
let editor = null
const editorRef = ref()
const encode = ref('UTF-8')
const arrayBufferResult = ref()
const codeContent = ref('')
const isSelected = ref(false)
const visible = ref(false)
const mode = ref()
const { toClipboard } = useClipboard()

function onVisibleChange(visiable) {
  isSelected.value = visiable
  if (visiable) {
    visible.value = false
  }
}

const readCode = async () => {
  let result = await getFileBolb(props.url)
  if (!result) return
  arrayBufferResult.value = result
  arrayBuffer2String()
}
// 复制到剪切板
const copy2Clipboard = async () => {
  await toClipboard(codeContent.value)
  ElMessage.success('复制成功')
}

function arrayBuffer2String() {
  const encoder = new TextDecoder(encode.value)
  codeContent.value = encoder.decode(new Uint8Array(arrayBufferResult.value))
}

// 获取当前语法类型
function getLanguage() {
  // 尝试从父容器获取语法类型
  if (props.language) {
    let modeObj = modes[props.language.toLowerCase()]
    if (modeObj) {
      return modeObj
    }
    return null
  }
}

function initCodeContent() {
  setTimeout(() => {
    if (editor) {
      editor.destroy()
    }
    let mode = getLanguage()
    let extensions = [basicSetup]
    if (mode) {
      extensions = [basicSetup, mode()]
    }
    editor = new EditorView({
      doc: codeContent.value || 'Press Ctrl-Space in here...\n',
      extensions: extensions,
      parent: editorRef.value,
      options: {
        lineNumber: true,
        line: true,
        //括号匹配
        matchBrackets: true,
        readOnly: true // 只读
      }
    })
  }, 500)
}
function insertCommandContent() {
  editor.dispatch({
    changes: {
      from: 0,
      to: editor.state.doc.length,
      insert: codeContent.value || 'Press Ctrl-Space in here...\n'
    }
  })
}

function itemSelected(item) {
  encode.value = item
  arrayBuffer2String()
  insertCommandContent()
}

onMounted(() => {
  readCode()
  nextTick(() => {
    initCodeContent()
  })
})
</script>
<style lang="scss" scoped>
.code-previewer {
  padding: 60px 0 0 0;
  height: 100%;
  display: flex;
  flex-direction: column;
  :deep(.ͼ2 .cm-gutters) {
    background-color: #ffffff;
    border-right: none;
  }
  .code {
    display: flex;
    flex-direction: column;
    .right-bar {
      flex: 1;
    }
  }
  .bottom-tools {
    width: 100%;
    display: flex;
    justify-content: space-between;
    height: 30px;
    background: #f2f4f7;
    box-shadow: 0 -1px 0 0 #e2e6ed;
    z-index: 9;
    overflow: hidden;
    .right {
      padding-right: 8px;
      display: flex;
      align-items: center;
      .item {
        display: flex;
        align-items: center;
        justify-content: center;
      }
      .encoder {
        width: 55px;
        height: 30px;
        margin: 0 auto;
        text-align: center;
        transition: background-color 0.3s ease;
        border-radius: 5px;
        &:hover {
          background-color: rgba(25, 55, 88, 0.04);
        }
        .encoder-text {
          font-size: 12px;
          padding: 6px 8px;
        }
        .encoder-text.selected {
          background-color: rgba(25, 55, 88, 0.1);
        }
      }
      .icon-btn {
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 4px;
        border-radius: 2px;
        white-space: nowrap;
        flex-shrink: 0;
        transition: background-color 0.3s ease;
        border-radius: 5px;
        &:hover {
          background-color: rgba(25, 55, 88, 0.04);
        }
        .icons {
          width: 16px;
          height: 16px;
          color: #000000;
          vertical-align: middle;
          display: inline-block;
          background-repeat: no-repeat;
          background-size: 100% 100%;
        }
      }
    }
  }
}
</style>
