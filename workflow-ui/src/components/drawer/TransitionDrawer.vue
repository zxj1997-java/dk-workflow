<template>
  <el-drawer
    v-model="visible"
    title="变迁配置"
    size="400px"
    :destroy-on-close="true"
    :append-to-body="true"
    :with-header="true"
    :close-on-click-modal="true"
    :show-close="true"
    :before-close="handleClose"
  >
    <div class="drawer-container">
      <el-form :model="form" label-width="100px" class="transition-form">
        <el-form-item label="变迁名称" required>
          <el-input v-model="form.name" placeholder="请输入变迁名称"></el-input>
        </el-form-item>
        
        <el-form-item label="变迁编码" required>
          <el-input v-model="form.code" placeholder="请输入变迁编码"></el-input>
        </el-form-item>

        <el-form-item label="条件class" required>
          <el-input v-model="form.conditionClass" placeholder="类名.方法名"></el-input>
        </el-form-item>
      </el-form>
    </div>

    <template #footer>
      <div class="drawer-footer">
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">确定</el-button>
      </div>
    </template>
  </el-drawer>
</template>

<script>
import { updateTransition, createTransition } from '@/api/workflow'

export default {
  name: 'TransitionDrawer',
  props: {
    modelValue: Boolean,
    transitionData: {
      type: Object,
      default: () => ({})
    },
    workflowVersionId: {
      type: String,
      required: true
    },
    isEdit: {
      type: Boolean,
      default: false
    }
  },
  emits: ['update:modelValue', 'save', 'success'],
  data() {
    return {
      form: {
        name: '',
        code: '',
        conditionClass: ''
      }
    }
  },
  computed: {
    visible: {
      get() {
        return this.modelValue
      },
      set(value) {
        this.$emit('update:modelValue', value)
      }
    }
  },
  watch: {
    modelValue(val) {
      if (val) {
        this.initForm()
      }
    }
  },
  methods: {
    initForm() {
      if (this.transitionData) {
        this.form = { ...this.transitionData }
      } else {
        this.form = {
          name: '',
          code: '',
          conditionClass: ''
        }
      }
    },
    handleClose(done) {
      this.form = {
        name: '',
        code: '',
        conditionClass: ''
      }
      done()
    },
    async handleSave() {
      try {
        if (!this.form.name || !this.form.code || !this.form.conditionClass) {
          this.$message.warning('请填写完整信息')
          return
        }

        if (!this.workflowVersionId) {
          this.$message.warning('请先保存工作流')
          return
        }

        const transitionData = {
          id: this.transitionData?.id,
          name: this.form.name,
          code: this.form.code,
          from: this.transitionData?.from,
          to: this.transitionData?.to,
          conditionClass: this.form.conditionClass,
          workflowVersionId: this.workflowVersionId
        }

        if (this.isEdit) {
          await updateTransition(transitionData)
        } else {
          await createTransition(transitionData)
        }

        this.$message.success('保存成功')
        this.$emit('success')
        this.visible = false
      } catch (error) {
        console.error('保存变迁失败:', error)
        this.$message.error('保存失败: ' + (error.response?.data?.message || error.message))
      }
    }
  }
}
</script>

<style scoped>
.drawer-container {
  height: 100%;
  overflow-y: auto;
  padding: 20px;
}

.drawer-footer {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 16px 20px;
  background: #fff;
  border-top: 1px solid #dcdfe6;
  text-align: right;
}

:deep(.el-drawer__body) {
  height: calc(100% - 120px);
  padding: 0;
  overflow: hidden;
}

:deep(.el-drawer__header) {
  margin-bottom: 0;
  padding: 16px 20px;
  border-bottom: 1px solid #dcdfe6;
}
</style> 