<template>
  <el-drawer v-model="visible" :append-to-body="true" :before-close="handleClose" :close-on-click-modal="true" :destroy-on-close="true" :show-close="true" :with-header="true" size="400px" title="活动配置">
    <div class="drawer-container">
      <el-form ref="formRef" :model="form" :rules="rules" class="activity-form" label-width="100px">
        <el-form-item label="活动名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入活动名称"></el-input>
        </el-form-item>

        <el-form-item label="活动编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入活动编码"></el-input>
        </el-form-item>

        <el-form-item label="跳转页面" prop="pageUrl">
          <el-input v-model="form.pageUrl" placeholder="请输入页面路径，例如：/form/apply"></el-input>
        </el-form-item>

        <el-form-item label="执行后class">
          <el-input v-model="form.afterClass" :autosize="{ minRows: 1, maxRows: 2 }" placeholder="类名(全路径).方法名" type="textarea"></el-input>
        </el-form-item>

        <el-form-item label="审核人员">
          <div class="select-container">
            <el-select v-model="form.approvers" :popper-append-to-body="true" :popper-options="{
                modifiers: [
                  {
                    name: 'computeStyles',
                    options: {
                      adaptive: false,
                      gpuAcceleration: false
                    }
                  }
                ]
              }" :reserve-keyword="true" :teleported="true" filterable multiple placeholder="请选择审核人员">
              <el-option v-for="item in userOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
            </el-select>
          </div>
        </el-form-item>

        <el-form-item label="审核部门">
          <div class="select-container">
            <el-select v-model="form.departments" :popper-append-to-body="true" :popper-options="{
                modifiers: [
                  {
                    name: 'computeStyles',
                    options: {
                      adaptive: false,
                      gpuAcceleration: false
                    }
                  }
                ]
              }" :reserve-keyword="true" :teleported="true" filterable multiple placeholder="请选择审核部门">
              <el-option v-for="item in departmentOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
            </el-select>
          </div>
        </el-form-item>

        <el-form-item label="操作按钮" prop="operations">
          <el-checkbox-group v-model="form.operations">
            <el-checkbox label="APPROVED">通过</el-checkbox>
            <el-checkbox label="RETURN_APPLICANT">退回申请人</el-checkbox>
            <el-checkbox label="RETURN_PREVIOUS">退回上一步</el-checkbox>
            <el-checkbox label="DISAPPROVED">不通过</el-checkbox>
          </el-checkbox-group>
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

import {ElMessage} from "element-plus";

export default {
  name: 'ActivityDrawer',
  props: {
    modelValue: Boolean,
    activityData: {
      type: Object,
      default: () => ({})
    },
    userOptions: {
      type: Array,
      default: () => []
    },
    departmentOptions: {
      type: Array,
      default: () => []
    },
    workflowVersionId: {
      type: String,
      default: ''
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
        pageUrl: '',
        approvers: [],
        departments: [],
        operations: ['APPROVED', 'RETURN_APPLICANT', 'RETURN_PREVIOUS', 'DISAPPROVED']
      },
      rules: {
        name: [
          {required: true, message: '请输入活动名称', trigger: 'blur'}
        ],
        code: [
          {required: true, message: '请输入活动编码', trigger: 'blur'}
        ],
        pageUrl: [
          {required: true, message: '请输入跳转页面路径', trigger: 'blur'}
        ],
        operations: [
          {type: 'array', required: true, message: '请至少选择一个操作按钮', trigger: 'change'}
        ]
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
      if (this.activityData) {
        this.form = {...this.activityData}
      } else {
        this.form = {
          name: '',
          code: '',
          pageUrl: '',
          afterClass: '',
          approvers: [],
          departments: [],
          operations: []
        }
      }
    },
    handleClose(done) {
      this.form = {
        name: '',
        code: '',
        pageUrl: '',
        afterClass: '',
        approvers: [],
        departments: [],
        operations: []
      }
      done()
    },
    async handleSave() {
      try {
        await this.$refs.formRef.validate()
        const activityData = {
          id: this.activityData?.id,
          name: this.form.name,
          code: this.form.code,
          pageUrl: this.form.pageUrl,
          afterClass: this.form.afterClass,
          approvers: this.form.approvers,
          departments: this.form.departments,
          operations: this.form.operations,
          type: 'activity'
        }

        this.$emit('save', activityData)
        this.visible = false
      } catch (error) {
        console.error('保存活动失败:', error)
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

.select-container {
  width: 100%;
}

:deep(.el-select) {
  width: 100%;
}
</style> 