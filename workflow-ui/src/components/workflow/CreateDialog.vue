<template>
  <el-dialog
      v-model="visible"
      title="新建工作流"
      width="500px"
      :close-on-click-modal="false"
  >
    <el-form
        ref="form"
        :model="flowForm"
        :rules="rules"
        label-width="100px"
    >
      <el-form-item label="工作流名称" prop="name">
        <el-input v-model="flowForm.name" placeholder="请输入工作流名称"/>
      </el-form-item>
      <el-form-item label="工作流编码" prop="code">
        <el-input v-model="flowForm.code" placeholder="请输入工作流编码"/>
      </el-form-item>
      <el-form-item label="导入配置">
        <el-upload
            class="upload-demo"
            action="#"
            :auto-upload="false"
            :show-file-list="false"
            accept=".json"
            :on-change="handleFileChange"
        >
          <el-button type="primary">选择文件</el-button>
          <template #tip>
            <div class="el-upload__tip">
              支持导入 .json 格式的工作流配置文件
            </div>
          </template>
        </el-upload>
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script>
import {workflowApi} from '@/api/workflow'
import {ElMessage} from 'element-plus'

export default {
  name: 'CreateDialog',
  data() {
    return {
      visible: false,
      flowForm: {
        name: '',
        code: '',
        flowData: null
      },
      rules: {
        name: [
          { required: true, message: '请输入工作流名称', trigger: 'blur' }
        ],
        code: [
          { required: true, message: '请输入工作流编码', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    show() {
      this.visible = true
      this.flowForm = {
        name: '',
        code: '',
        flowData: null
      }
    },
    handleFileChange(file) {
      if (file && file.raw) {
        const reader = new FileReader()
        reader.onload = (e) => {
          try {
            const jsonData = JSON.parse(e.target.result)
            this.flowForm.flowData = jsonData.flowData
            this.flowForm.name = jsonData.name || this.flowForm.name
            this.flowForm.code = jsonData.code || this.flowForm.code
            ElMessage.success('文件解析成功')
          } catch (error) {
            console.error('文件解析失败:', error)
            ElMessage.error('文件格式不正确')
            this.flowForm.flowData = null
          }
        }
        reader.readAsText(file.raw)
      }
    },
    async handleSubmit() {
      try {
        await this.$refs.form.validate()
        
        // 创建提交的数据对象
        const submitData = {
          name: this.flowForm.name,
          code: this.flowForm.code,
          flowData: this.flowForm.flowData || ''
        }
        
        // 调用API创建工作流
        await workflowApi.createWorkflow(submitData)
        
        ElMessage.success('创建成功')
        this.visible = false
        this.$emit('created')
      } catch (error) {
        console.error('创建失败:', error)
        ElMessage.error('创建失败')
      }
    }
  }
}
</script>

<style scoped>
.upload-demo {
  margin-bottom: 10px;
}

.el-upload__tip {
  color: #909399;
  font-size: 12px;
  margin-top: 4px;
}
</style> 