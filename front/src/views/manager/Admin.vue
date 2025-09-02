<template>
  <div class="admin-container">
    <!-- 搜索区域 -->
    <div class="search-card">
      <el-input 
        v-model="data.name" 
        placeholder="请输入名称查询" 
        prefix-icon="Search"
        clearable
        class="search-input"
      />
      <div class="search-buttons">
        <el-button type="primary" @click="load" :icon="Search">查询</el-button>
        <el-button @click="reset" :icon="RefreshRight">重置</el-button>
      </div>
    </div>

    <!-- 表格区域 -->
    <div class="content-card">
      <div class="table-header">
        <h3 class="table-title">管理员信息列表</h3>
        <el-button type="primary" @click="handleAdd" :icon="Plus" class="add-button">新增管理员</el-button>
      </div>
      
      <el-table 
        :data="data.tableData" 
        stripe 
        border
        highlight-current-row
        class="admin-table"
      >
        <el-table-column label="用户名" prop="username" width="180"></el-table-column>
        <el-table-column label="名称" prop="name" width="180"></el-table-column>
        <el-table-column label="头像" width="100" align="center">
          <template #default="scope">
            <el-avatar 
              :src="scope.row.avatar" 
              :size="40"
            ></el-avatar>
          </template>
        </el-table-column>
        <el-table-column label="角色" prop="role" width="120" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row.role === 'ADMIN'" type="success">管理员</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center">
          <template #default="scope">
            <el-button type="primary" size="small" :icon="Edit" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="danger" size="small" :icon="Delete" @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          background
          layout="total, prev, pager, next, jumper"
          v-model:page-size="data.pageSize"
          v-model:current-page="data.pageNum"
          :total="data.total"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 表单对话框 -->
    <el-dialog 
      v-model="data.formVisible" 
      :title="data.form.id ? '编辑管理员信息' : '新增管理员信息'" 
      width="500px" 
      :close-on-click-modal="false"
      destroy-on-close
      center
    >
      <el-form :model="data.form" label-width="80px" class="admin-form">
        <el-form-item label="头像" prop="avatar">
          <div class="avatar-uploader">
            <el-upload 
              :action="uploadUrl" 
              list-type="picture-card" 
              :on-success="handleImgSuccess"
              :show-file-list="false"
              class="avatar-upload"
            >
              <img v-if="data.form.avatar" :src="data.form.avatar" class="avatar" />
              <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
            </el-upload>
            <div class="upload-text">点击上传头像</div>
          </div>
        </el-form-item>
        <el-form-item label="账号" prop="username">
          <el-input v-model="data.form.username" placeholder="请输入账号" />
        </el-form-item>
        <el-form-item label="名称" prop="name">
          <el-input v-model="data.form.name" placeholder="请输入名称" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="data.formVisible = false">取 消</el-button>
          <el-button type="primary" @click="save">保 存</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import request from "@/utils/request";
import { reactive } from "vue";
import { ElMessageBox, ElMessage } from "element-plus";
import { Search, RefreshRight, Plus, Edit, Delete } from '@element-plus/icons-vue';

// 文件上传的接口地址
const uploadUrl = import.meta.env.VITE_BASE_URL + '/files/upload'

const data = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0,
  formVisible: false,
  form: {},
  tableData: [],
  name: null
})

// 分页查询
const load = () => {
  request.get('/admin/selectPage', {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
      name: data.name
    }
  }).then(res => {
    data.tableData = res.data?.list
    data.total = res.data?.total
  })
}

// 处理页码变化
const handleCurrentChange = () => {
  load()
}

// 新增
const handleAdd = () => {
  data.form = {}
  data.formVisible = true
}

// 编辑
const handleEdit = (row) => {
  data.form = JSON.parse(JSON.stringify(row))
  data.formVisible = true
}

// 新增保存
const add = () => {
  request.post('/admin/add', data.form).then(res => {
    if (res.code === '200') {
      load()
      ElMessage.success('操作成功')
      data.formVisible = false
    } else {
      ElMessage.error(res.msg)
    }
  })
}

// 编辑保存
const update = () => {
  request.put('/admin/update', data.form).then(res => {
    if (res.code === '200') {
      load()
      ElMessage.success('操作成功')
      data.formVisible = false
    } else {
      ElMessage.error(res.msg)
    }
  })
}

// 弹窗保存
const save = () => {
  // data.form有id就是更新，没有就是新增
  data.form.id ? update() : add()
}

// 删除
const handleDelete = (id) => {
  ElMessageBox.confirm('删除后数据无法恢复，您确定删除吗?', '删除确认', { 
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  }).then(res => {
    request.delete('/admin/delete/' + id).then(res => {
      if (res.code === '200') {
        load()
        ElMessage.success('操作成功')
      } else {
        ElMessage.error(res.msg)
      }
    })
  }).catch(() => {})
}

// 重置
const reset = () => {
  data.name = null
  load()
}

// 处理文件上传的钩子
const handleImgSuccess = (res) => {
  data.form.avatar = res.data  // res.data就是文件上传返回的文件路径，获取到路径后赋值表单的属性
}

load()
</script>

<style scoped>
.admin-container {
  padding: 15px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 100px);
}

.search-card {
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  display: flex;
  align-items: center;
}

.search-input {
  width: 300px;
  margin-right: 20px;
}

.search-buttons .el-button {
  margin-right: 10px;
}

.content-card {
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #ebeef5;
}

.table-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.add-button {
  font-weight: 500;
}

.admin-table {
  margin-bottom: 20px;
  border-radius: 6px;
  overflow: hidden;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.avatar-uploader {
  text-align: center;
}

.avatar-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  overflow: hidden;
  display: inline-block;
}

.avatar-upload:hover {
  border-color: #409eff;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  line-height: 100px;
  text-align: center;
}

.avatar {
  width: 100px;
  height: 100px;
  display: block;
  object-fit: cover;
}

.upload-text {
  margin-top: 8px;
  color: #606266;
  font-size: 14px;
}

.admin-form {
  margin: 0 auto;
  max-width: 400px;
}

.dialog-footer {
  text-align: right;
  margin-top: 20px;
}
</style>