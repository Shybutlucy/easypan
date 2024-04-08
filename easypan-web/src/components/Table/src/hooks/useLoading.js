import { computed, ref, unref, watch } from 'vue'

/**
 * 表格加载动画钩子函数
 * @param {Prop} props
 */
export function useLoading(props) {
  const loadingRef = ref(unref(props).loading)

  watch(
    () => unref(props).loading,
    (loading) => {
      loadingRef.value = loading
    }
  )

  const getLoading = computed(() => unref(loadingRef))

  function setLoading(loading) {
    loadingRef.value = loading
  }

  return { getLoading, setLoading }
}
