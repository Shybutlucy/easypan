<template>
  <transition name="showModel">
    <div class="model" v-if="show">
      <div class="container">
        <div class="content">
          <div class="header">
            <div class="header-left">
              <div class="close" @click="close">
                <i class="iconfont icon-close-thin"></i>
              </div>
            </div>
            <div class="header-center">
              <div class="filename">{{ title }}</div>
            </div>
          </div>
          <div class="previewer">
            <slot></slot>
          </div>
        </div>
      </div>
    </div>
  </transition>
</template>

<script>
export default {
  name: 'Window',
  components: { CloseOutlined }
}
</script>
<script setup>
import { CloseOutlined } from '@vicons/antd'

const props = defineProps({
  show: {
    type: Boolean
  },
  title: {
    type: String
  },
  align: {
    type: String,
    default: 'top'
  }
})
const emit = defineEmits(['close'])

const close = () => {
  emit('close')
}
</script>
<style lang="scss" scoped>
@keyframes modal-fade-in {
  0% {
    opacity: 0;
    transform: scale(0.9);
  }

  100% {
    opacity: 1;
    transform: scale(1);
  }
}
@keyframes modal-fade-out {
  0% {
    opacity: 1;
    transform: scale(1);
  }

  100% {
    opacity: 0;
    transform: scale(0.9);
  }
}
.showModel-enter-active {
  -webkit-animation: modal-fade-in 0.3s ease;
  animation: modal-fade-in 0.3s ease;
  -webkit-transform: scale(1);
  transform: scale(1);
}
.showModel-leave-active {
  opacity: 0;
  pointer-events: none;
  animation: modal-fade-out 0.2s ease;
  transform: scale(0.9);
}
.showModel-enter-active .previewer {
  transition-delay: 1s;
}
.model {
  position: absolute;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 200;
  opacity: 1;
  .container {
    height: 100%;
    width: 100%;
    background-color: #fff;
    display: -ms-flexbox;
    display: flex;
    .content {
      -ms-flex: 1 1;
      flex: 1 1;
      height: 100%;
      display: -ms-flexbox;
      display: flex;
      -ms-flex-direction: column;
      flex-direction: column;
      position: relative;
      -webkit-transition: padding 0.66s cubic-bezier(0.66, 0, 0.01, 1);
      -o-transition: padding 0.66s cubic-bezier(0.66, 0, 0.01, 1);
      transition: padding 0.66s cubic-bezier(0.66, 0, 0.01, 1);
      overflow: hidden;
      .header {
        height: 60px;
        width: 100%;
        position: absolute;
        top: 0;
        left: 0;
        z-index: 1;
        background: #fff;
        -webkit-backdrop-filter: blur(30px);
        backdrop-filter: blur(30px);
        .header-left {
          position: absolute;
          top: 50%;
          -webkit-transform: translateY(-50%);
          transform: translateY(-50%);
          left: 20px;
          .close {
            width: 28px;
            height: 28px;
            display: flex;
            align-items: center;
            justify-content: center;
            transition: background-color 0.3s ease;
            border-radius: 5px;
            &:hover {
              background-color: rgba(132, 133, 141, 0.12);
            }
            .iconfont {
              font-size: 20px;
            }
          }
        }
        .header-center {
          position: absolute;
          top: 50%;
          left: 50%;
          -webkit-transform: translate(-50%, -50%);
          transform: translate(-50%, -50%);
          width: calc(100% - 500px);
          .filename {
            width: 100%;
            text-align: center;
            font-size: 16px;
            line-height: 1.5;
            font-weight: 600;
            max-width: 100%;
            overflow: hidden;
            white-space: nowrap;
            -o-text-overflow: ellipsis;
            text-overflow: ellipsis;
          }
        }
      }
      .previewer {
        width: 100%;
        height: 100%;
        -ms-flex: 1 1;
        flex: 1 1;
      }
    }
  }
}
</style>
