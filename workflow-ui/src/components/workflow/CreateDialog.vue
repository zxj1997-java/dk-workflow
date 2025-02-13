<template>
  <el-dialog
    title="新建工作流"
    v-model="dialogVisible"
    width="30%"
    :close-on-click-modal="false"
    :destroy-on-close="true"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="工作流名称" prop="name">
        <el-input v-model="form.name" placeholder="请输入工作流名称"></el-input>
      </el-form-item>
      <el-form-item label="工作流编码" prop="code">
        <el-input v-model="form.code" placeholder="请输入工作流编码"></el-input>
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="loading" @click="handleSubmit">确定</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script>
import workflowApi from '@/api/workflow/workflow'
import { ElMessage } from 'element-plus'

export default {
  name: 'CreateDialog',
  data() {
    return {
      dialogVisible: false,
      loading: false,
      form: {
        name: '',
        code: ''
      },
      rules: {
        name: [
          { required: true, message: '请输入工作流名称', trigger: 'blur' },
          { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
        ],
        code: [
          { required: true, message: '请输入工作流编码', trigger: 'blur' },
          { pattern: /^[A-Za-z0-9_]+$/, message: '编码只能包含字母、数字和下划线', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    show() {
      console.log('Show method called')
      this.dialogVisible = true
      this.form.name = ''
      this.form.code = ''
      if (this.$refs.formRef) {
        this.$refs.formRef.resetFields()
      }
    },
    async handleSubmit() {
      try {
        await this.$refs.formRef.validate()
        this.loading = true
        const res = await workflowApi.createWorkflow(this.form)
        ElMessage.success('创建成功')
        this.dialogVisible = false
        this.$emit('created', res)
      } catch (error) {
        if (error.message) {
          ElMessage.error(error.message)
        }
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.dialog-footer {
  text-align: right;
}

.dialog-footer button {
  margin-left: 10px;
}
</style> 