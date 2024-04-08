import { isArray } from '@/utils/is'

export const createActions = ({ deleteFile, downloadFile }) => {
  return [
    {
      title: '下载',
      label: 'download',
      icon: 'download',
      showLabel: true,
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
      showLabel: true,
      onClick: (data) => {
        deleteFile(data)
      }
    }
  ]
}
