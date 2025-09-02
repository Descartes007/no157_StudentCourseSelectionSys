<template>
  <div class="notice-container">
    <!-- 搜索区域 -->
    <div class="search-card">
      <div class="search-title">
        <el-icon><InfoFilled /></el-icon>
        <span>公告管理</span>
      </div>
      <div class="search-content">
        <el-input 
          v-model="data.title" 
          placeholder="请输入公告标题关键词" 
          prefix-icon="Search"
          clearable
          class="search-input"
        />
        <div class="search-buttons">
          <el-button type="primary" @click="load">
            <el-icon><Search /></el-icon>
            <span>查询</span>
          </el-button>
          <el-button @click="reset">
            <el-icon><RefreshRight /></el-icon>
            <span>重置</span>
          </el-button>
        </div>
      </div>
    </div>

    <!-- 表格区域 -->
    <div class="table-card">
      <div class="table-header">
        <div class="table-title">
          <el-icon><Bell /></el-icon>
          <span>公告列表</span>
        </div>
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          <span>新增公告</span>
        </el-button>
      </div>
      
      <el-table 
        :data="data.tableData" 
        stripe 
        border 
        highlight-current-row
        style="width: 100%"
        :header-cell-style="{
          background: '#f5f7fa',
          color: '#606266',
          fontWeight: 'bold',
          textAlign: 'center'
        }"
      >
        <el-table-column label="序号" type="index" width="70" align="center" />
        <el-table-column label="公告标题" prop="title" min-width="220">
          <template #default="scope">
            <div class="notice-title">
              <el-icon><Document /></el-icon>
              <span>{{ scope.row.title }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="公告内容" prop="content" min-width="350">
          <template #default="scope">
            <el-tooltip
              class="box-item"
              effect="dark"
              :content="scope.row.content"
              placement="top-start"
              :show-after="500"
            >
              <div class="notice-content">{{ scope.row.content }}</div>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column label="发布时间" prop="time" min-width="180" align="center">
          <template #default="scope">
            <div class="notice-time">
              <el-icon><Calendar /></el-icon>
              <span>{{ scope.row.time }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="180">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEdit(scope.row)">
              <el-icon><Edit /></el-icon>
              <span>编辑</span>
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row.id)">
              <el-icon><Delete /></el-icon>
              <span>删除</span>
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          background
          layout="total, sizes, prev, pager, next, jumper"
          v-model:page-size="data.pageSize"
          v-model:current-page="data.pageNum"
          :page-sizes="[5, 10, 20, 50]"
          :total="data.total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog 
      :title="data.form.id ? '编辑公告' : '新增公告'" 
      width="50%" 
      v-model="data.formVisible" 
      :close-on-click-modal="false" 
      destroy-on-close
    >
      <el-form 
        :model="data.form" 
        label-width="100px" 
        style="padding: 0 20px"
        ref="formRef"
        :rules="rules"
      >
        <el-form-item label="公告标题" prop="title">
          <el-input 
            v-model="data.form.title" 
            placeholder="请输入公告标题"
            clearable
            maxlength="50"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="公告内容" prop="content">
          <el-input 
            type="textarea" 
            :rows="6" 
            placeholder="请输入公告内容"
            v-model="data.form.content" 
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="data.formVisible = false">取 消</el-button>
          <el-button type="primary" @click="save">保 存</el-button>
        </span>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import request from "@/utils/request";
import { reactive, ref } from "vue";
import { ElMessageBox, ElMessage } from "element-plus";
import { 
  Search, 
  RefreshRight, 
  InfoFilled, 
  Bell, 
  Plus, 
  Document, 
  Calendar, 
  Edit, 
  Delete 
} from '@element-plus/icons-vue';

const formRef = ref(null);

// 表单验证规则
const rules = {
  title: [
    { required: true, message: '请输入公告标题', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入公告内容', trigger: 'blur' },
    { min: 5, max: 500, message: '长度在 5 到 500 个字符', trigger: 'blur' }
  ]
};

const data = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0,
  formVisible: false,
  form: {},
  tableData: [],
  title: null
});

// 分页查询
const load = () => {
  request.get('/notice/selectPage', {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
      title: data.title
    }
  }).then(res => {
    data.tableData = res.data?.list
    data.total = res.data?.total
  })
};

// 页码变化处理
const handleCurrentChange = (val) => {
  data.pageNum = val;
  load();
};

// 页大小变化处理
const handleSizeChange = (val) => {
  data.pageSize = val;
  data.pageNum = 1;
  load();
};

// 新增
const handleAdd = () => {
  data.form = {};
  data.formVisible = true;
};

// 编辑
const handleEdit = (row) => {
  data.form = JSON.parse(JSON.stringify(row));
  data.formVisible = true;
};

// 新增保存
const add = () => {
  if (!data.form.title || !data.form.content) {
    ElMessage.warning('标题和内容不能为空！');
    return;
  }
  
  // 给表单添加时间
  data.form.time = new Date().toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    hour12: false
  }).replace(/\//g, '-');
  
  request.post('/notice/add', data.form).then(res => {
    if (res.code === '200') {
      load();
      ElMessage.success('新增成功');
      data.formVisible = false;
    } else {
      ElMessage.error(res.msg || '操作失败');
    }
  });
};

// 编辑保存
const update = () => {
  if (!data.form.title || !data.form.content) {
    ElMessage.warning('标题和内容不能为空！');
    return;
  }
  
  request.put('/notice/update', data.form).then(res => {
    if (res.code === '200') {
      load();
      ElMessage.success('更新成功');
      data.formVisible = false;
    } else {
      ElMessage.error(res.msg || '操作失败');
    }
  });
};

// 弹窗保存
const save = () => {
  // data.form有id就是更新，没有就是新增
  data.form.id ? update() : add();
};

// 删除
const handleDelete = (id) => {
  ElMessageBox.confirm('删除后数据无法恢复，您确定删除吗?', '删除确认', { 
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    icon: 'Warning'
  }).then(() => {
    request.delete('/notice/deleteById/' + id).then(res => {
      if (res.code === '200') {
        load();
        ElMessage.success('删除成功');
      } else {
        ElMessage.error(res.msg || '删除失败');
      }
    });
  }).catch(() => {});
};

// 重置
const reset = () => {
  data.title = null;
  data.pageNum = 1;
  load();
};

// 初始化加载
load();
</script>

<style scoped>
.notice-container {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.search-card {
  background: white;
  border-radius: 8px;
  padding: 15px 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.search-title {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 15px;
  display: flex;
  align-items: center;
  gap: 8px;
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 10px;
}

.search-content {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 15px;
}

.search-input {
  width: 350px;
}

.search-buttons {
  display: flex;
  gap: 10px;
}

.table-card {
  background: white;
  border-radius: 8px;
  padding: 15px 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 10px;
}

.table-title {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 8px;
}

.notice-title {
  display: flex;
  align-items: center;
  gap: 5px;
  font-weight: 500;
}

.notice-content {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 350px;
  color: #606266;
}

.notice-time {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
  color: #606266;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  padding-top: 15px;
  border-top: 1px solid #f0f0f0;
}

:deep(.el-table) {
  border-radius: 5px;
  overflow: hidden;
}

:deep(.el-table th) {
  height: 45px;
}

:deep(.el-table td) {
  padding: 12px 0;
}

:deep(.el-table .el-button) {
  padding: 6px 12px;
}
</style>