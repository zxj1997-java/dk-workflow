<template>
  <div class="instance-list">
    <div class="header">
      <h2>{{ workflow?.name }} - 流程实例列表</h2>
    </div>
    
    <div class="table-container">
      <table>
        <thead>
          <tr>
            <th>实例ID</th>
            <th>业务关联</th>
            <th>当前节点</th>
            <th>状态</th>
            <th>创建时间</th>
            <th>更新时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in instanceList" :key="item.id">
            <td>{{ item.id }}</td>
            <td>{{ item.businessKey }}</td>
            <td>{{ item.currentNode }}</td>
            <td>
              <el-tag :type="getStatusType(item.status)">
                {{ getStatusText(item.status) }}
              </el-tag>
            </td>
            <td>{{ formatDate(item.createTime) }}</td>
            <td>{{ formatDate(item.updateTime) }}</td>
            <td>
              <button class="view-btn" @click="viewInstance(item.id)">查看</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template> 