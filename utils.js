// 公共字典与工具

export const CATEGORY_LIST = ['水电', '家具', '门窗', '网络', '家电', '电料', '五金', '其他']

export const STATUS_MAP = {
  PENDING: { label: '待派单', type: 'info' },
  ASSIGNED: { label: '已派单', type: 'warning' },
  PROCESSING: { label: '维修中', type: 'primary' },
  DONE: { label: '待审核', type: 'warning' },
  REWORK: { label: '需返修', type: 'danger' },
  CONFIRMED: { label: '已完成', type: 'success' },
  CANCELED: { label: '已取消', type: 'info' }
}

export const MATERIAL_STATUS = {
  PENDING: { label: '待审批', type: 'warning' },
  APPROVED: { label: '已批准', type: 'success' },
  REJECTED: { label: '已驳回', type: 'danger' }
}

export function statusOf(code) {
  return STATUS_MAP[code] || { label: code, type: 'info' }
}

export function fmtDate(s) {
  if (!s) return '-'
  return String(s).replace('T', ' ').slice(0, 16)
}
