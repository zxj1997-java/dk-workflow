<template>
  <el-dialog :title="dialogTitle" v-model="visible" width="500px" @close="closeDialog">
    <el-form ref="formRef" :model="processForm" :rules="processRules" label-width="100px">
      <el-form-item label="处理结果" prop="action">
        <el-radio-group v-model="processForm.action">
          <el-radio v-for="item in operationOptions" :key="item.value" :label="item.value">
            {{ item.label }}
          </el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="处理意见" prop="comment">
        <el-input v-model="processForm.comment" type="textarea" :rows="4" placeholder="请输入处理意见"/>
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="closeDialog">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script>
// 动态加载 taskApi 模块
import {taskApi} from '@/api/workflow';

export default {
  name: 'ProcessDialog',
  props: {
    modelValue: {
      type: Boolean,
      required: true
    },
    // 业务ID，用于加载对应待处理任务
    businessId: {
      type: [String, Number],
      required: true
    },
    dialogTitle: {
      type: String,
      default: '处理申请'
    }
  },
  data() {
    return {
      processForm: {
        id: null,
        action: '',
        comment: ''
      },
      operationOptions: [],
      processRules: {
        action: [
          {required: true, message: '请选择处理结果', trigger: 'change'}
        ],
        comment: [
          {required: true, message: '请输入处理意见', trigger: 'blur'}
        ]
      }
    }
  },
  computed: {
    visible: {
      get() {
        return this.modelValue;
      },
      set(val) {
        this.$emit('update:modelValue', val);
      }
    }
  },
  watch: {
    // 当对话框显示时，根据业务ID加载任务记录及对应操作配置
    visible(newVal) {
      if (newVal) {
        this.loadTaskData();
      }
    }
  },
  methods: {
    async loadTaskData() {
      try {

        // 获取运行时任务列表（接口返回的是任务数组）
        const tasks = await taskApi.getRuntimeTasks(this.businessId);
        if (!tasks || tasks.length === 0) {
          this.$message.error('未找到待处理的任务记录');
          this.closeDialog();
          return;
        }
        // 从任务中选择状态为 PENDING 的任务
        const pendingTask = tasks.find(task => task.status === "PENDING");
        if (!pendingTask) {
          this.$message.error('未找到待处理的任务记录');
          this.closeDialog();
          return;
        }
        // 将任务ID赋值到处理表单
        this.processForm.id = pendingTask.id;
        // 根据任务ID获取操作配置
        const opRes = await taskApi.getTaskOperations(pendingTask.id);
        this.operationOptions = opRes || [];
        console.log(this.operationOptions)
        if (this.operationOptions.length > 0) {
          // 默认选择第一个操作项
          this.processForm.action = this.operationOptions[0].value;
        }
      } catch (error) {
        this.$message.error('获取任务记录失败');
        this.closeDialog();
      }
    },
    closeDialog() {
      this.visible = false;
    },
    handleSubmit() {
      this.$refs.formRef.validate(async valid => {
        if (!valid) return;
        try {
          await taskApi.processTasks(this.processForm);
          this.$message.success('处理成功');
          this.closeDialog();
          this.$emit('submit', this.processForm);
        } catch (error) {
          this.$message.error('处理失败');
        }
      });
    }
  }
};
</script>

<style scoped>
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style> 