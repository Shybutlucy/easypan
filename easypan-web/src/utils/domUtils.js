export function getBoundingClientRect(element) {
  if (!element || !element.getBoundingClientRect) {
    return 0
  }
  return element.getBoundingClientRect()
}

export function getViewportOffset(element) {
  const doc = document.documentElement

  const docScrollLeft = doc.scrollLeft
  const docScrollTop = doc.scrollTop
  const docClientLeft = doc.clientLeft
  const docClientTop = doc.clientTop

  const pageXOffset = window.pageXOffset
  const pageYOffset = window.pageYOffset

  const box = getBoundingClientRect(element)

  const { left: retLeft, top: rectTop, width: rectWidth, height: rectHeight } = box

  const scrollLeft = (pageXOffset || docScrollLeft) - (docClientLeft || 0)
  const scrollTop = (pageYOffset || docScrollTop) - (docClientTop || 0)
  const offsetLeft = retLeft + pageXOffset
  const offsetTop = rectTop + pageYOffset

  const left = offsetLeft - scrollLeft
  const top = offsetTop - scrollTop

  const clientWidth = window.document.documentElement.clientWidth
  const clientHeight = window.document.documentElement.clientHeight
  // debugger
  return {
    left: left,
    top: top,
    right: clientHeight - rectWidth - left,
    bottom: clientHeight - rectHeight - top,
    rightIncludeBody: clientWidth - left,
    bottomIncludeBody: clientHeight - top
  }
}

/* istanbul ignore next */
export function on(element, event, handler) {
  if (element && event && handler) {
    element.addEventListener(event, handler, false)
  }
}
