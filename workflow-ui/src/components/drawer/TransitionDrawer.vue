<template>
  <el-drawer v-model="visible" :append-to-body="true" :before-close="handleClose" :close-on-click-modal="true" :destroy-on-close="true" :show-close="true" :with-header="true" size="400px" title="变迁配置">
    <div class="drawer-container">
      <el-form :model="form" class="transition-form" label-width="100px">
        <el-form-item label="变迁名称" required>
          <el-input v-model="form.name" placeholder="请输入变迁名称"></el-input>
        </el-form-item>

        <el-form-item label="变迁编码" required>
          <el-input v-model="form.code" placeholder="请输入变迁编码"></el-input>
        </el-form-item>

        <el-form-item label="条件类方法" required>
          <el-input v-model="form.conditionClass" :autosize="{ minRows: 1, maxRows: 2 }" placeholder="类名(全路径)#方法名" type="textarea"></el-input>
          <div class="method-tip">
            <p>实现接口: ConditionHandler</p>
            <p>方法签名: boolean evaluate(String businessId,Transition transition)</p>
            <p>示例: com.example.handler.LeaveConditionHandler#evaluate</p>
          </div>
        </el-form-item>

        <el-form-item label="执行后类方法">
          <el-input v-model="form.afterClass" :autosize="{ minRows: 1, maxRows: 2 }" placeholder="类名(全路径)#方法名" type="textarea"></el-input>
          <div class="method-tip">
            <p>实现接口: TransitionHandler</p>
            <p>方法签名: void handle(String businessId,Transition transition)</p>
            <p>示例: com.example.handler.LeaveTransitionHandler#handle</p>
          </div>
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
import {ElMessage} from 'element-plus'

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
        conditionClass: '',
        afterClass: ''
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
        this.form = {...this.transitionData}
      } else {
        this.form = {
          name: '',
          code: '',
          conditionClass: '',
          afterClass: ''
        }
      }
    },
    handleClose(done) {
      this.form = {
        name: '',
        code: '',
        conditionClass: '',
        afterClass: ''
      }
      done()
    },
    async handleSave() {
      try {
        if (!this.form.name || !this.form.code || !this.form.conditionClass) {
          this.$message.warning('请填写完整信息')
          return
        }

        const transitionData = {
          id: this.transitionData?.id,
          name: this.form.name,
          code: this.form.code,
          conditionClass: this.form.conditionClass,
          afterClass: this.form.afterClass,
          from: this.transitionData?.from,
          to: this.transitionData?.to,
          type: 'transition'
        }

        this.$emit('save', transitionData)
        this.visible = false
      } catch (error) {
        console.error('保存变迁失败:', error)
        ElMessage.error('保存失败: ' + error.message)
      }
    }
  }
}
</script>

<style scoped>
.drawer-container {
  height: 100%;
  overflow-y: auto;
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

.method-tip {
  margin-top: 8px;
  padding: 8px;
  background-color: #f5f7fa;
  border-radius: 4px;
  font-size: 12px;
  color: #606266;
  line-height: 1.4;
}

.method-tip p {
  margin: 4px 0;
}
</style> 