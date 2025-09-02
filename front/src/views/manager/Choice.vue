<template>
  <div class="choice-container">
    <!-- 搜索区域 -->
    <div class="search-card">
      <div class="search-title">
        <el-icon><Collection /></el-icon>
        <span>我的选课</span>
      </div>
      <div class="search-content">
        <el-input 
          v-model="data.name" 
          placeholder="请输入课程名称查询" 
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
          <el-icon><Menu /></el-icon>
          <span>选课列表</span>
          <el-tag type="info" effect="plain" size="small">共 {{ data.total }} 条记录</el-tag>
        </div>
      </div>
      
      <!-- 表格组件 -->
      <div class="table-responsive">
        <el-table 
          :data="data.tableData" 
          stripe 
          border 
          highlight-current-row
          style="width: 100%"
          table-layout="fixed"
          :header-cell-style="{
            background: '#f5f7fa',
            color: '#606266',
            fontWeight: 'bold',
            textAlign: 'center'
          }"
          v-loading="data.loading"
          empty-text="暂无选课记录"
        >
          <el-table-column label="序号" type="index" width="60" align="center" />
          <el-table-column label="课程名称" prop="name" min-width="140">
            <template #default="scope">
              <el-tooltip :content="scope.row.name" placement="top" :show-after="300">
                <div class="course-name">
                  <el-icon><Notebook /></el-icon>
                  <span class="course-name-text">{{ scope.row.name }}</span>
                </div>
              </el-tooltip>
            </template>
          </el-table-column>
          <el-table-column label="授课教师" prop="teacherName" width="120" align="center" show-overflow-tooltip>
            <template #default="scope">
              <el-tag type="success" effect="light">
                <div class="teacher-name">
                  <el-icon><User /></el-icon>
                  <span>{{ scope.row.teacherName }}</span>
                </div>
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="选课学生" prop="studentName" width="120" align="center" show-overflow-tooltip>
            <template #default="scope">
              <el-tag type="primary" effect="light">
                <div class="student-name">
                  <el-icon><Avatar /></el-icon>
                  <span>{{ scope.row.studentName }}</span>
                </div>
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center" width="105" v-if="data.user.role === 'STUDENT'">
            <template #default="scope">
              <el-tooltip content="取消选课" placement="top" :show-after="300">
                <el-button type="danger" size="small" @click="handleDelete(scope.row.id)">
                  <el-icon><Delete /></el-icon>
                  <span>取消</span>
                </el-button>
              </el-tooltip>
            </template>
          </el-table-column>
        </el-table>
      </div>
      
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

    <!-- 空状态提示 -->
    <el-empty 
      v-if="data.tableData.length === 0 && !data.loading" 
      description="暂无选课记录" 
      :image-size="200"
      class="empty-data"
    >
      <el-button type="primary" @click="goToCourseList">去选课</el-button>
    </el-empty>
  </div>
</template>

<script setup>
import request from "@/utils/request";
import { reactive, onMounted } from "vue";
import { ElMessageBox, ElMessage } from "element-plus";
import { useRouter } from 'vue-router';
import { 
  Search, 
  RefreshRight,
  Collection,
  Menu,
  Notebook, 
  User,
  Avatar,
  Delete 
} from '@element-plus/icons-vue';

const router = useRouter();

const data = reactive({
  user: JSON.parse(localStorage.getItem('system-user') || '{}'),
  pageNum: 1,
  pageSize: 10,
  total: 0,
  form: {},
  tableData: [],
  name: null,
  loading: false
});

// 分页查询
const load = () => {
  data.loading = true;
  let teacherId = null;
  let studentId = null;
  if (data.user.role === 'TEACHER') {
    teacherId = data.user.id;
  }
  if (data.user.role === 'STUDENT') {
    studentId = data.user.id;
  }
  request.get('/choice/selectPage', {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
      name: data.name,
      teacherId: teacherId,
      studentId: studentId
    }
  }).then(res => {
    data.tableData = res.data?.list || [];
    data.total = res.data?.total || 0;
  }).finally(() => {
    data.loading = false;
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

// 删除
const handleDelete = (id) => {
  ElMessageBox.confirm('取消后可能再也选不到这门课了哦?', '取消选课', { 
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    icon: 'Warning'
  }).then(() => {
    request.delete('/choice/deleteById/' + id).then(res => {
      if (res.code === '200') {
        load();
        ElMessage.success('取消选课成功');
      } else {
        ElMessage.error(res.msg || '操作失败');
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

// 跳转到课程列表
const goToCourseList = () => {
  router.push('/course');
};

// 初始化加载
onMounted(() => {
  load();
});
</script>

<style scoped>
.choice-container {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.search-card {
  background: white;
  border-radius: 10px;
  padding: 18px 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.search-card:hover {
  box-shadow: 0 4px 18px rgba(0, 0, 0, 0.08);
}

.search-title {
  font-size: 17px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 18px;
  display: flex;
  align-items: center;
  gap: 10px;
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 12px;
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
  border-radius: 10px;
  padding: 18px 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.table-card:hover {
  box-shadow: 0 4px 18px rgba(0, 0, 0, 0.08);
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 18px;
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 12px;
}

.table-title {
  font-size: 17px;
  font-weight: bold;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 10px;
}

.table-responsive {
  width: 100%;
  overflow-x: auto;
}

.course-name {
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 500;
  width: 100%;
}

.course-name-text {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  display: inline-block;
  max-width: 100px;
}

.teacher-name,
.student-name {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  padding-top: 15px;
  border-top: 1px solid #f0f0f0;
}

.empty-data {
  margin-top: 30px;
}

:deep(.el-table) {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.el-table th) {
  height: 48px;
}

:deep(.el-table td) {
  padding: 10px 0;
}

:deep(.el-table .el-button) {
  padding: 6px 12px;
}

:deep(.el-tag) {
  display: inline-flex;
  align-items: center;
  padding: 4px 8px;
}

:deep(.el-input__wrapper) {
  box-shadow: 0 0 0 1px #dcdfe6 inset;
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #c0c4cc inset;
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #409eff inset;
}

@media (max-width: 768px) {
  .search-input {
    width: 100%;
  }
  
  .search-content {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .search-buttons {
    width: 100%;
  }
  
  .course-name-text {
    max-width: 80px;
  }
}
</style>