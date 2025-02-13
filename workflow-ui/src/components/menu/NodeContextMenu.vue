<template>
  <div v-show="visible" class="edge-context-menu" :style="position">
    <div class="menu-item" @click="$emit('configure')">
      <el-icon><Setting /></el-icon>
      配置活动属性
    </div>
    <div class="menu-item delete" @click="$emit('delete')">
      <el-icon><Delete /></el-icon>
      删除
    </div>
  </div>
</template>

<script>
import { Setting, Delete } from '@element-plus/icons-vue'

export default {
  name: 'NodeContextMenu',
  components: {
    Setting,
    Delete
  },
  props: {
    visible: Boolean,
    x: Number,
    y: Number
  },
  computed: {
    position() {
      return {
        left: this.x + 'px',
        top: this.y + 'px'
      }
    }
  }
}
</script>

<style scoped>
.edge-context-menu {
  position: fixed;
  background: white;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
  z-index: 1000;
}

.menu-item {
  padding: 8px 16px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
}

.menu-item:hover {
  background-color: #f5f7fa;
}

.menu-item.delete {
  color: #f56c6c;
}

.menu-item.delete:hover {
  background-color: #fef0f0;
}

.menu-item i {
  margin-right: 8px;
  font-size: 16px;
}
</style> 