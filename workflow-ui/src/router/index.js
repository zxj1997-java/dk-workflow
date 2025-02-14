import {createRouter, createWebHistory} from 'vue-router'
import AppLayout from '@/layout'
import WorkflowList from '@/views/workflow/List.vue'
import WorkflowEditor from '@/components/WorkflowEditor.vue'
import WorkflowViewer from '@/components/WorkflowViewer.vue'
import InstanceDetail from '@/views/workflow/InstanceDetail.vue'

const routes = [
    {
        path: '/',
        redirect: '/workflow/list'
    },
    {
        path: '/workflow/list',
        name: 'WorkflowList',
        component: WorkflowList
    },
    {
        path: '/workflow/editor/:id?',
        name: 'WorkflowEditor',
        component: WorkflowEditor,
        props: true
    },
    {
        path: '/workflow/viewer/:id',
        name: 'WorkflowViewer',
        component: WorkflowViewer,
        props: route => ({
            id: route.params.id,
            version: route.query.version
        })
    },
    {
        path: '/workflow/instance/detail/:instanceId',
        name: 'InstanceDetail',
        component: InstanceDetail,
        props: true
    },
    {
        path: '/leave',
        component: AppLayout,
        meta: {title: '请假申请', icon: 'document'},
        children: [
            {
                path: 'index',
                name: 'Leave',
                component: () => import('@/views/leave/index.vue'),
                meta: {title: '请假申请'}
            }
        ]
    },
    {
        path: '/workflow',
        component: AppLayout,
        meta: {title: '工作流管理', icon: 'setting'},
        children: [
            {
                path: 'list',
                name: 'WorkflowList',
                component: WorkflowList,
                meta: {title: '流程列表'}
            }
        ]
    }
]

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes
})

export default router 