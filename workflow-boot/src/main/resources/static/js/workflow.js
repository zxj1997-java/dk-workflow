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

        // 1. 创建状态和监控按钮容器
        const headerContainer = document.createElement('div');
        headerContainer.className = 'workflow-header';

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

        // 创建流程监控按钮
        const monitorButton = document.createElement('button');
        monitorButton.textContent = '流程监控';
        monitorButton.className = 'workflow-monitor-button';
        monitorButton.onclick = () => this.openProcessMonitor();

        // 将状态和监控按钮添加到头部容器
        headerContainer.appendChild(statusContainer);
        headerContainer.appendChild(monitorButton);

        // 2. 创建意见输入框
        const commentInput = document.createElement('textarea');
        commentInput.className = 'workflow-comment';
        commentInput.placeholder = '请输入处理意见...';

        // 3. 创建操作按钮组
        const buttonContainer = document.createElement('div');
        buttonContainer.className = 'workflow-buttons';
        if (data.operations && Array.isArray(data.operations)) {
            data.operations.forEach(op => {
                const button = document.createElement('button');
                button.textContent = op.label;
                button.className = 'workflow-button';
                button.onclick = () => this.handleOperation(op.value);
                buttonContainer.appendChild(button);
            });
        }

        // 按顺序添加到主容器
        mainContainer.appendChild(headerContainer);
        mainContainer.appendChild(commentInput);
        mainContainer.appendChild(buttonContainer);
        container.appendChild(mainContainer);

        // 添加样式
        this.addStyles();
    }

    async handleOperation(operation) {
        const commentInput = document.querySelector('.workflow-comment');
        const comment = commentInput ? commentInput.value.trim() : '';

        if (!comment) {
            this.showMessage('请输入处理意见', 'warning');
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
                this.showMessage('处理成功', 'success');
                window.location.reload();
            } else {
                this.showMessage(result.msg || '处理失败', 'error');
            }
        } catch (error) {
            console.error('处理失败:', error);
            this.showMessage('处理失败', 'error');
        }
    }

    openProcessMonitor() {
        // 创建遮罩层
        const overlay = document.createElement('div');
        overlay.className = 'workflow-overlay';
        
        // 创建弹框容器
        const modal = document.createElement('div');
        modal.className = 'workflow-modal';
        
        // 创建弹框头部
        const header = document.createElement('div');
        header.className = 'workflow-modal-header';
        header.innerHTML = `
            <span>流程监控</span>
            <button class="workflow-modal-close">&times;</button>
        `;
        
        // 创建弹框内容
        const content = document.createElement('div');
        content.className = 'workflow-modal-content';
        
        // 创建iframe加载流程监控页面
        const iframe = document.createElement('iframe');
        iframe.src = `${this.monitorUrl}workflow/instance/detail/${this.businessId}`;
        iframe.className = 'workflow-modal-iframe';
        
        // 组装弹框
        content.appendChild(iframe);
        modal.appendChild(header);
        modal.appendChild(content);
        overlay.appendChild(modal);
        document.body.appendChild(overlay);
        
        // 绑定关闭事件
        const closeBtn = modal.querySelector('.workflow-modal-close');
        closeBtn.onclick = () => document.body.removeChild(overlay);
        overlay.onclick = (e) => {
            if (e.target === overlay) {
                document.body.removeChild(overlay);
            }
        };
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

    showMessage(message, type = 'info') {
        const messageDiv = document.createElement('div');
        messageDiv.className = `workflow-message ${type}`;
        messageDiv.textContent = message;
        document.body.appendChild(messageDiv);

        // 2秒后自动消失
        setTimeout(() => {
            messageDiv.classList.add('fade-out');
            setTimeout(() => {
                document.body.removeChild(messageDiv);
            }, 300);
        }, 2000);
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
            ${this.getBaseStyles()}
            
            .workflow-message {
                position: fixed;
                top: 20px;
                left: 50%;
                transform: translateX(-50%);
                padding: 10px 20px;
                border-radius: 4px;
                font-size: 14px;
                z-index: 9999;
                transition: opacity 0.3s, transform 0.3s;
                box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
            }
            .workflow-message.success {
                background-color: #f0f9eb;
                color: #67c23a;
                border: 1px solid #e1f3d8;
            }
            .workflow-message.warning {
                background-color: #fdf6ec;
                color: #e6a23c;
                border: 1px solid #faecd8;
            }
            .workflow-message.error {
                background-color: #fef0f0;
                color: #f56c6c;
                border: 1px solid #fde2e2;
            }
            .workflow-message.info {
                background-color: #f4f4f5;
                color: #909399;
                border: 1px solid #e9e9eb;
            }
            .workflow-message.fade-out {
                opacity: 0;
                transform: translateX(-50%) translateY(-20px);
            }
        `;
        document.head.appendChild(style);
    }

    // 将原有的样式移到单独的方法中
    getBaseStyles() {
        return `
            ${this.getOriginalStyles()}

            .workflow-overlay {
                position: fixed;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                background: rgba(0, 0, 0, 0.5);
                display: flex;
                justify-content: center;
                align-items: center;
                z-index: 9999;
            }
            
            .workflow-modal {
                background: white;
                border-radius: 4px;
                width: 90%;
                height: 90%;
                display: flex;
                flex-direction: column;
                box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
            }
            
            .workflow-modal-header {
                padding: 15px 20px;
                border-bottom: 1px solid #e4e7ed;
                display: flex;
                justify-content: space-between;
                align-items: center;
            }
            
            .workflow-modal-header span {
                font-size: 16px;
                font-weight: 500;
                color: #303133;
            }
            
            .workflow-modal-close {
                border: none;
                background: none;
                font-size: 20px;
                color: #909399;
                cursor: pointer;
                padding: 0;
                line-height: 1;
            }
            
            .workflow-modal-close:hover {
                color: #409eff;
            }
            
            .workflow-modal-content {
                flex: 1;
                padding: 20px;
                overflow: hidden;
            }
            
            .workflow-modal-iframe {
                width: 100%;
                height: 100%;
                border: none;
            }
        `;
    }

    // 将原有的基础样式移到单独的方法中
    getOriginalStyles() {
        return `
            .workflow-main {
                padding: 20px;
                height: 100vh;
                display: flex;
                flex-direction: column;
                box-sizing: border-box;
            }
            .workflow-header {
                height: 10%;
                min-height: 50px;
                display: flex;
                justify-content: space-between;
                align-items: center;
                gap: 10px;
                padding-bottom: 10px;
            }
            .workflow-status {
                flex-shrink: 0;
            }
            .status-tag {
                display: inline-block;
                padding: 6px 12px;
                border-radius: 4px;
                font-size: 14px;
                font-weight: bold;
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
            .workflow-monitor-button {
                padding: 6px 12px;
                border: 1px solid #409eff;
                border-radius: 4px;
                background: #ecf5ff;
                color: #409eff;
                cursor: pointer;
                font-size: 14px;
                white-space: nowrap;
            }
            .workflow-monitor-button:hover {
                background: #409eff;
                color: #fff;
            }
            .workflow-comment {
                height: 70%;
                min-height: 200px;
                padding: 10px;
                border: 1px solid #dcdfe6;
                border-radius: 4px;
                resize: none;
                font-size: 14px;
                box-sizing: border-box;
                margin: 10px 0;
                transition: border-color 0.2s ease-in-out;
                outline: none;
            }
            .workflow-comment:hover {
                border-color: #c0c4cc;
            }
            .workflow-comment:focus {
                border-color: #409eff80;
                box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1);
            }
            .workflow-buttons {
                height: 20%;
                min-height: 60px;
                display: flex;
                gap: 10px;
                justify-content: center;
                align-items: flex-start;
                padding-top: 10px;
            }
            .workflow-button {
                padding: 10px 20px;
                border: 1px solid #dcdfe6;
                border-radius: 4px;
                background: #fff;
                cursor: pointer;
                font-size: 14px;
                white-space: nowrap;
            }
            .workflow-button:hover {
                background: #f5f7fa;
            }
            .workflow-error {
                color: #f56c6c;
                padding: 8px;
                border: 1px solid #fde2e2;
                background-color: #fef0f0;
                border-radius: 4px;
            }
        `;
    }
} 