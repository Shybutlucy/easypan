import { isArray } from '@/utils/is'

export const createActions = ({
  shareFile,
  downloadFile,
  deleteFile,
  renameFile,
  copyFile,
  moveFile
}) => {
  return [
    {
      title: '分享',
      label: 'share',
      icon: 'share',
      onClick: (data) => {
        shareFile(data)
      }
    },
    {
      title: '下载',
      label: 'download',
      icon: 'download',
      isShow: (data) => {
        return (
          !isArray(data) ||
          (isArray(data) && data.filter((item) => item.folderType === 1).length === 0)
        )
      },
      onClick: (data) => {
        downloadFile(data)
      }
    },
    {
      title: '删除',
      label: 'delete',
      icon: 'delete',
      onClick: (data) => {
        deleteFile(data)
      }
    },
    {
      title: '重命名',
      label: 'rename',
      icon: 'rename',
      onClick: (data) => {
        renameFile(data)
      },
      isShow: (data) => {
        return !isArray(data) || (isArray(data) && data.length == 1)
      }
    },
    {
      title: '复制',
      label: 'copy',
      icon: 'copy',
      onClick: (data) => {
        copyFile(data)
      }
    },
    {
      title: '移动',
      label: 'move',
      icon: 'move',
      onClick: (data) => {
        moveFile(data)
      }
    }
  ]
}
