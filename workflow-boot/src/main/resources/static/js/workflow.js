class WorkflowPlugin {
    constructor(options = {}) {
        this.baseUrl = options.baseUrl || '/';
        this.monitorUrl = options.monitorUrl || '/';
        this.container = options.container;
        this.businessId = options.businessId;
        this.code = 'leave'; // 默认为请假流程

        this.init();
    }

    async init() {
        try {
            const response = await this.fetchActivityInfo();
            if (response.code === 0 && response.data) {
                this.renderUI(response.data);
            } else {
                console.error('获取活动信息失败:', response.msg);
                this.showError(response.msg || '获取活动信息失败');
            }
        } catch (error) {
            console.error('初始化失败:', error);
            this.showError('初始化失败');
        }
    }

    async fetchActivityInfo() {
        try {
            const url = `${this.baseUrl}api/workflow/activity-info?code=${this.code}&businessId=${this.businessId}`;
            const response = await fetch(url);
            return await response.json();
        } catch (error) {
            console.error('请求失败:', error);
            throw error;
        }
    }

    renderUI(data) {
        const container = document.getElementById(this.container);
        if (!container) return;

        container.innerHTML = '';

        // 创建主容器
        const mainContainer = document.createElement('div');
        mainContainer.className = 'workflow-main';

        // 创建按钮和意见框容器
        const actionContainer = document.createElement('div');
        actionContainer.className = 'workflow-action';

        // 创建操作按钮
        if (data.operations && Array.isArray(data.operations)) {
            data.operations.forEach(op => {
                const button = document.createElement('button');
                button.textContent = op.label;
                button.className = 'workflow-button';
                button.onclick = () => this.handleOperation(op.value);
                actionContainer.appendChild(button);
            });
        }

        // 创建意见输入框
        const commentInput = document.createElement('textarea');
        commentInput.className = 'workflow-comment';
        commentInput.placeholder = '请输入处理意见...';
        actionContainer.appendChild(commentInput);

        // 创建流程监控按钮
        const monitorButton = document.createElement('button');
        monitorButton.textContent = '流程监控';
        monitorButton.className = 'workflow-monitor-button';
        monitorButton.onclick = () => this.openProcessMonitor();

        // 创建状态显示
        const statusContainer = document.createElement('div');
        statusContainer.className = 'workflow-status';
        if (data.status) {
            statusContainer.innerHTML = `
                <div class="status-tag ${data.status.toLowerCase()}">
                    ${this.getStatusText(data.status)}
                </div>
            `;
        }

        // 添加到主容器
        mainContainer.appendChild(actionContainer);
        mainContainer.appendChild(statusContainer);
        mainContainer.appendChild(monitorButton);
        container.appendChild(mainContainer);

        // 添加样式
        this.addStyles();
    }

    async handleOperation(operation) {
        const commentInput = document.querySelector('.workflow-comment');
        const comment = commentInput ? commentInput.value.trim() : '';

        if (!comment) {
            alert('请输入处理意见');
            return;
        }

        try {
            const response = await fetch(`${this.baseUrl}api/workflow/runtime-tasks/process/${this.businessId}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    decision: operation,
                    comment: comment
                })
            });
            const result = await response.json();
            if (result.code === 0) {
                alert('处理成功');
                window.location.reload();
            } else {
                alert('处理失败: ' + result.msg);
            }
        } catch (error) {
            console.error('处理失败:', error);
            alert('处理失败');
        }
    }

    openProcessMonitor() {
        window.open(`${this.monitorUrl}workflow/instance/detail/${this.businessId}`);
    }

    showError(message) {
        const container = document.getElementById(this.container);
        if (container) {
            container.innerHTML = `
                <div class="workflow-error">
                    ${message}
                </div>
            `;
        }
    }

    getStatusText(status) {
        const statusMap = {
            'PENDING': '待处理',
            'COMPLETED': '已完成',
            'TERMINATED': '已终止',
            'RETURNED': '已退回'
        };
        return statusMap[status] || status;
    }

    addStyles() {
        if (document.getElementById('workflow-styles')) return;

        const style = document.createElement('style');
        style.id = 'workflow-styles';
        style.textContent = `
            .workflow-main {
                padding: 20px;
                border: 1px solid #ebeef5;
                border-radius: 4px;
            }
            .workflow-action {
                display: flex;
                gap: 10px;
                margin-bottom: 16px;
            }
            .workflow-button {
                padding: 8px 16px;
                border: 1px solid #dcdfe6;
                border-radius: 4px;
                background: #fff;
                cursor: pointer;
            }
            .workflow-button:hover {
                background: #f5f7fa;
            }
            .workflow-comment {
                flex: 1;
                min-height: 60px;
                padding: 8px;
                border: 1px solid #dcdfe6;
                border-radius: 4px;
                resize: vertical;
            }
            .workflow-monitor-button {
                margin-top: 16px;
                padding: 8px 16px;
                border: 1px solid #409eff;
                border-radius: 4px;
                background: #ecf5ff;
                color: #409eff;
                cursor: pointer;
            }
            .workflow-monitor-button:hover {
                background: #409eff;
                color: #fff;
            }
            .workflow-status {
                margin-top: 16px;
            }
            .status-tag {
                display: inline-block;
                padding: 4px 8px;
                border-radius: 4px;
                font-size: 12px;
            }
            .status-tag.pending {
                background: #e6a23c;
                color: #fff;
            }
            .status-tag.completed {
                background: #67c23a;
                color: #fff;
            }
            .status-tag.terminated {
                background: #f56c6c;
                color: #fff;
            }
            .status-tag.returned {
                background: #909399;
                color: #fff;
            }
            .workflow-error {
                color: #f56c6c;
                padding: 8px;
                border: 1px solid #fde2e2;
                background-color: #fef0f0;
                border-radius: 4px;
            }
        `;
        document.head.appendChild(style);
    }
} 