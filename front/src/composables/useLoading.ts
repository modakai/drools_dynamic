import { ref, type Ref } from 'vue'

/**
 * 加载状态管理组合式函数
 */
export function useLoading(initialState = false) {
  const loading: Ref<boolean> = ref(initialState)
  
  const setLoading = (state: boolean) => {
    loading.value = state
  }
  
  const withLoading = async <T>(fn: () => Promise<T>): Promise<T> => {
    try {
      setLoading(true)
      return await fn()
    } finally {
      setLoading(false)
    }
  }
  
  return {
    loading,
    setLoading,
    withLoading
  }
}