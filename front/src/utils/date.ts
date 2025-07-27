// 日期工具函数

/**
 * 格式化日期
 * @param date 日期对象、字符串或时间戳
 * @param format 格式字符串，默认为 'YYYY-MM-DD HH:mm:ss'
 * @returns 格式化后的日期字符串
 */
export function formatDate(date: Date | string | number, format: string = 'YYYY-MM-DD HH:mm:ss'): string {
  if (!date) return ''
  
  const d = new Date(date)
  
  if (isNaN(d.getTime())) {
    return ''
  }
  
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hours = String(d.getHours()).padStart(2, '0')
  const minutes = String(d.getMinutes()).padStart(2, '0')
  const seconds = String(d.getSeconds()).padStart(2, '0')
  
  return format
    .replace('YYYY', String(year))
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hours)
    .replace('mm', minutes)
    .replace('ss', seconds)
}

/**
 * 格式化相对时间
 * @param date 日期对象、字符串或时间戳
 * @returns 相对时间字符串，如 "2小时前"、"3天前"
 */
export function formatRelativeTime(date: Date | string | number): string {
  if (!date) return ''
  
  const d = new Date(date)
  const now = new Date()
  const diff = now.getTime() - d.getTime()
  
  if (isNaN(d.getTime())) {
    return ''
  }
  
  const seconds = Math.floor(diff / 1000)
  const minutes = Math.floor(seconds / 60)
  const hours = Math.floor(minutes / 60)
  const days = Math.floor(hours / 24)
  const months = Math.floor(days / 30)
  const years = Math.floor(months / 12)
  
  if (years > 0) {
    return `${years}年前`
  } else if (months > 0) {
    return `${months}个月前`
  } else if (days > 0) {
    return `${days}天前`
  } else if (hours > 0) {
    return `${hours}小时前`
  } else if (minutes > 0) {
    return `${minutes}分钟前`
  } else {
    return '刚刚'
  }
}

/**
 * 判断是否为今天
 * @param date 日期对象、字符串或时间戳
 * @returns 是否为今天
 */
export function isToday(date: Date | string | number): boolean {
  if (!date) return false
  
  const d = new Date(date)
  const today = new Date()
  
  if (isNaN(d.getTime())) {
    return false
  }
  
  return d.getFullYear() === today.getFullYear() &&
         d.getMonth() === today.getMonth() &&
         d.getDate() === today.getDate()
}

/**
 * 判断是否为本周
 * @param date 日期对象、字符串或时间戳
 * @returns 是否为本周
 */
export function isThisWeek(date: Date | string | number): boolean {
  if (!date) return false
  
  const d = new Date(date)
  const today = new Date()
  
  if (isNaN(d.getTime())) {
    return false
  }
  
  const startOfWeek = new Date(today)
  startOfWeek.setDate(today.getDate() - today.getDay())
  startOfWeek.setHours(0, 0, 0, 0)
  
  const endOfWeek = new Date(startOfWeek)
  endOfWeek.setDate(startOfWeek.getDate() + 6)
  endOfWeek.setHours(23, 59, 59, 999)
  
  return d >= startOfWeek && d <= endOfWeek
}

/**
 * 获取日期范围描述
 * @param startDate 开始日期
 * @param endDate 结束日期
 * @returns 日期范围描述字符串
 */
export function getDateRangeDescription(startDate: Date | string | number, endDate: Date | string | number): string {
  if (!startDate || !endDate) return ''
  
  const start = formatDate(startDate, 'YYYY-MM-DD')
  const end = formatDate(endDate, 'YYYY-MM-DD')
  
  if (start === end) {
    return start
  }
  
  return `${start} 至 ${end}`
}