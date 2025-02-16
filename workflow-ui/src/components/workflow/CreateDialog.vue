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
            :show-file-list="true"
            :limit="1"
            accept=".json"
            :on-change="handleFileChange"
            :on-remove="handleFileRemove"
            :file-list="fileList"
        >
          <el-button type="primary">选择文件</el-button>
          <template #tip>
            <div class="el-upload__tip">
              支持导入 .json 格式的工作流配置文件
            </div>
            <div v-if="importStatus" :class="['import-status', importStatus.type]">
              {{ importStatus.message }}
            </div>
          </template>
        </el-upload>
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleCancel">取消</el-button>
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
          {required: true, message: '请输入工作流名称', trigger: 'blur'}
        ],
        code: [
          {required: true, message: '请输入工作流编码', trigger: 'blur'}
        ]
      },
      fileList: [],
      importStatus: null
    }
  },
  methods: {
    show() {
      this.visible = true
      this.resetForm()
    },
    resetForm() {
      this.flowForm = {
        name: '',
        code: '',
        flowData: null
      }
      this.fileList = []
      this.importStatus = null
    },
    handleFileChange(file) {
      if (file && file.raw) {
        const reader = new FileReader()
        reader.onload = (e) => {
          try {
            const jsonData = JSON.parse(e.target.result)
            this.flowForm.flowData = jsonData.flowData
            
            // 如果导入的文件包含名称和编码，则自动填充
            if (jsonData.name && !this.flowForm.name) {
              this.flowForm.name = jsonData.name
            }
            if (jsonData.code && !this.flowForm.code) {
              this.flowForm.code = jsonData.code
            }
            
            this.importStatus = {
              type: 'success',
              message: '文件解析成功，已自动填充相关信息'
            }
            ElMessage.success('文件解析成功')
          } catch (error) {
            console.error('文件解析失败:', error)
            this.importStatus = {
              type: 'error',
              message: '文件格式不正确，请检查文件内容'
            }
            ElMessage.error('文件格式不正确')
            this.flowForm.flowData = null
          }
        }
        reader.readAsText(file.raw)
      }
    },
    handleFileRemove() {
      this.flowForm.flowData = null
      this.importStatus = null
    },
    handleCancel() {
      this.visible = false
      this.resetForm()
    },
    async handleSubmit() {
      try {
        await this.$refs.form.validate()
        
        const submitData = {
          name: this.flowForm.name,
          code: this.flowForm.code,
          flowData: this.flowForm.flowData || ''
        }
        
        await workflowApi.createWorkflow(submitData)
        
        ElMessage.success('创建成功')
        this.visible = false
        this.resetForm()
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

.import-status {
  margin-top: 8px;
  font-size: 12px;
  line-height: 1.4;
}

.import-status.success {
  color: #67C23A;
}

.import-status.error {
  color: #F56C6C;
}

:deep(.el-upload-list) {
  margin-top: 8px;
}
</style> 