import { Modal } from 'ant-design-vue'
import { ExclamationCircleOutlined } from '@ant-design/icons-vue'
import { createVNode } from 'vue'

/**
 * 确认对话框组合式函数
 */
export function useConfirm() {
  const confirm = (options: {
    title: string
    content?: string
    onOk?: () => void | Promise<void>
    onCancel?: () => void
    okText?: string
    cancelText?: string
    type?: 'info' | 'success' | 'error' | 'warning' | 'confirm'
  }) => {
    const {
      title,
      content,
      onOk,
      onCancel,
      okText = '确定',
      cancelText = '取消',
      type = 'confirm'
    } = options
    
    Modal.confirm({
      title,
      content,
      icon: createVNode(ExclamationCircleOutlined),
      okText,
      cancelText,
      onOk,
      onCancel,
      centered: true
    })
  }
  
  const confirmDelete = (options: {
    title?: string
    content?: string
    onOk?: () => void | Promise<void>
    onCancel?: () => void
  }) => {
    const {
      title = '确认删除',
      content = '删除后无法恢复，确定要删除吗？',
      onOk,
      onCancel
    } = options
    
    confirm({
      title,
      content,
      onOk,
      onCancel,
      okText: '删除',
      cancelText: '取消',
      type: 'error'
    })
  }
  
  return {
    confirm,
    confirmDelete
  }
}