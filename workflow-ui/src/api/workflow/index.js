export * from './workflow'
export * from './version'
export * from './instance'
export * from './activity'
export * from './transition'
export * from './system'

// 如果需要保持原有的 workflowApi 对象兼容性
import * as workflow from './workflow'
import * as version from './version'
import * as instance from './instance'
import * as system from './system'

export const workflowApi = {
  ...workflow,
  ...version,
  ...instance,
  ...system
} 