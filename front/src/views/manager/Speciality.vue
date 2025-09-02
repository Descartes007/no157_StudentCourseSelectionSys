<template>
  <div class="speciality-container">
    <!-- 搜索区域 -->
    <div class="search-card">
      <div class="search-title">
        <el-icon><Collection /></el-icon>
        <span>专业管理</span>
      </div>
      <div class="search-content">
        <el-input 
          v-model="data.name" 
          placeholder="请输入专业名称查询" 
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
          <el-icon><Reading /></el-icon>
          <span>专业列表</span>
        </div>
        <el-button type="primary" @click="handleAdd" v-if="data.user.role === 'ADMIN'">
          <el-icon><Plus /></el-icon>
          <span>新增专业</span>
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
        <el-table-column label="专业名称" prop="name" min-width="220">
          <template #default="scope">
            <div class="speciality-name">
              <el-icon><Collection /></el-icon>
              <span>{{ scope.row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="所属学院" prop="collegeName" min-width="180">
          <template #default="scope">
            <div class="college-name">
              <el-icon><School /></el-icon>
              <span>{{ scope.row.collegeName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="180" v-if="data.user.role === 'ADMIN'">
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
      :title="data.form.id ? '编辑专业' : '新增专业'" 
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
        <el-form-item label="专业名称" prop="name">
          <el-input 
            v-model="data.form.name" 
            placeholder="请输入专业名称"
            clearable
            maxlength="50"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="所属学院" prop="collegeId">
          <el-select 
            v-model="data.form.collegeId" 
            placeholder="请选择学院" 
            style="width: 100%"
            filterable
          >
            <el-option
              v-for="item in data.collegeData"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            >
              <div class="option-item">
                <el-icon><School /></el-icon>
                <span>{{ item.name }}</span>
              </div>
            </el-option>
          </el-select>
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
  Collection, 
  Reading, 
  School, 
  Plus, 
  Edit, 
  Delete 
} from '@element-plus/icons-vue';

const formRef = ref(null);

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入专业名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  collegeId: [
    { required: true, message: '请选择所属学院', trigger: 'change' }
  ]
};

const data = reactive({
  user: JSON.parse(localStorage.getItem('system-user') || '{}'),
  pageNum: 1,
  pageSize: 10,
  total: 0,
  formVisible: false,
  form: {},
  tableData: [],
  name: null,
  collegeData: []
});

// 分页查询
const load = () => {
  request.get('/speciality/selectPage', {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
      name: data.name
    }
  }).then(res => {
    data.tableData = res.data?.list;
    data.total = res.data?.total;
  });
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
  if (!data.form.name || !data.form.collegeId) {
    ElMessage.warning('专业名称和所属学院不能为空！');
    return;
  }
  
  request.post('/speciality/add', data.form).then(res => {
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
  if (!data.form.name || !data.form.collegeId) {
    ElMessage.warning('专业名称和所属学院不能为空！');
    return;
  }
  
  request.put('/speciality/update', data.form).then(res => {
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
    request.delete('/speciality/deleteById/' + id).then(res => {
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
  data.name = null;
  data.pageNum = 1;
  load();
};

// 加载学院数据
const loadCollege = () => {
  request.get('/college/selectAll').then(res => {
    if (res.code === '200') {
      data.collegeData = res.data;
    } else {
      ElMessage.error(res.msg || '获取学院数据失败');
    }
  });
};

// 初始化加载
load();
loadCollege();
</script>

<style scoped>
.speciality-container {
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

.speciality-name {
  display: flex;
  align-items: center;
  gap: 5px;
  font-weight: 500;
}

.college-name {
  display: flex;
  align-items: center;
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

.option-item {
  display: flex;
  align-items: center;
  gap: 5px;
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