<template>
  <div class="course-container">
    <!-- 搜索区域 -->
    <div class="search-card">
      <div class="search-title">
        <el-icon><Notebook /></el-icon>
        <span>课程管理</span>
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
          <el-icon><List /></el-icon>
          <span>课程列表</span>
        </div>
        <el-button type="primary" @click="handleAdd" v-if="data.user.role === 'ADMIN'">
          <el-icon><Plus /></el-icon>
          <span>新增课程</span>
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
        <el-table-column label="序号" type="index" width="60" align="center" />
        <el-table-column label="课程名称" prop="name" min-width="150">
          <template #default="scope">
            <div class="course-name">
              <el-icon><Notebook /></el-icon>
              <span>{{ scope.row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="课程介绍" prop="content" min-width="200" show-overflow-tooltip>
          <template #default="scope">
            <el-tooltip
              class="box-item"
              effect="dark"
              :content="scope.row.content"
              placement="top-start"
              :show-after="500"
            >
              <div class="course-content">{{ scope.row.content }}</div>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column label="课程学分" prop="score" width="90" align="center">
          <template #default="scope">
            <el-tag type="success">{{ scope.row.score }}分</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="授课教师" prop="teacherName" width="100" align="center">
          <template #default="scope">
            <div class="teacher-name">
              <el-icon><User /></el-icon>
              <span>{{ scope.row.teacherName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="开班人数" prop="num" width="90" align="center">
          <template #default="scope">
            <el-tag type="info">{{ scope.row.num }}人</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="上课时间" prop="time" width="140" align="center">
          <template #default="scope">
            <div class="time-info">
              <el-icon><Timer /></el-icon>
              <span>{{ scope.row.time }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="上课地点" prop="location" width="100" align="center">
          <template #default="scope">
            <div class="location-info">
              <el-icon><Location /></el-icon>
              <span>{{ scope.row.location }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="所属学院" prop="collegeName" width="140" align="center">
          <template #default="scope">
            <div class="college-name">
              <el-icon><School /></el-icon>
              <span>{{ scope.row.collegeName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="已选人数" prop="alreadyNum" width="90" align="center">
          <template #default="scope">
            <el-progress 
              :percentage="Math.round((scope.row.alreadyNum / scope.row.num) * 100)" 
              :format="format"
              :status="scope.row.alreadyNum >= scope.row.num ? 'success' : ''"
            ></el-progress>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="180" v-if="data.user.role !== 'TEACHER'">
          <template #default="scope" v-if="data.user.role === 'ADMIN'">
            <div class="operation-buttons">
              <el-button type="primary" size="small" @click="handleEdit(scope.row)">
                <el-icon><Edit /></el-icon>
                <span>编辑</span>
              </el-button>
              <el-button type="danger" size="small" @click="handleDelete(scope.row.id)">
                <el-icon><Delete /></el-icon>
                <span>删除</span>
              </el-button>
            </div>
          </template>
          <template #default="scope" v-else>
            <el-button 
              type="primary" 
              size="small" 
              @click="choiceCourse(scope.row)" 
              :disabled="scope.row.num === scope.row.alreadyNum"
            >
              <el-icon><Check /></el-icon>
              <span>选课</span>
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
      :title="data.form.id ? '编辑课程' : '新增课程'" 
      width="60%" 
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
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="课程名称" prop="name">
              <el-input 
                v-model="data.form.name" 
                placeholder="请输入课程名称"
                clearable
                maxlength="50"
                show-word-limit
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="课程学分" prop="score">
              <el-input-number 
                v-model="data.form.score" 
                :min="1" 
                :max="10" 
                placeholder="请输入课程学分"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="课程介绍" prop="content">
          <el-input 
            type="textarea" 
            :rows="4" 
            placeholder="请输入课程介绍"
            v-model="data.form.content" 
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="授课教师" prop="teacherId">
              <el-select 
                v-model="data.form.teacherId" 
                placeholder="请选择教师"
                filterable
                style="width: 100%"
              >
                <el-option
                  v-for="item in data.teacherData"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                >
                  <div class="option-item">
                    <el-icon><User /></el-icon>
                    <span>{{ item.name }}</span>
                  </div>
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属学院" prop="collegeId">
              <el-select 
                v-model="data.form.collegeId" 
                placeholder="请选择学院"
                filterable
                style="width: 100%"
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
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="开班人数" prop="num">
              <el-input-number 
                v-model="data.form.num" 
                :min="1" 
                :max="200"
                placeholder="请输入开班人数"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="上课时间" prop="time">
              <el-input 
                v-model="data.form.time" 
                placeholder="如：周二第1-2节"
                clearable
                prefix-icon="Timer"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="上课地点" prop="location">
              <el-input 
                v-model="data.form.location" 
                placeholder="如：教学楼A101"
                clearable
                prefix-icon="Location"
              />
            </el-form-item>
          </el-col>
        </el-row>
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
  Notebook, 
  List, 
  School, 
  User, 
  Timer,
  Location,
  Plus, 
  Edit, 
  Delete,
  Check
} from '@element-plus/icons-vue';

const formRef = ref(null);

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入课程名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入课程介绍', trigger: 'blur' },
    { min: 5, max: 500, message: '长度在 5 到 500 个字符', trigger: 'blur' }
  ],
  score: [
    { required: true, message: '请输入课程学分', trigger: 'blur' }
  ],
  teacherId: [
    { required: true, message: '请选择授课教师', trigger: 'change' }
  ],
  num: [
    { required: true, message: '请输入开班人数', trigger: 'blur' }
  ],
  time: [
    { required: true, message: '请输入上课时间', trigger: 'blur' }
  ],
  location: [
    { required: true, message: '请输入上课地点', trigger: 'blur' }
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
  collegeData: [],
  teacherData: []
});

// 自定义进度条显示格式
const format = (percentage) => {
  const row = data.tableData.find(r => Math.round((r.alreadyNum / r.num) * 100) === percentage);
  return row ? `${row.alreadyNum}/${row.num}` : '';
};

// 分页查询
const load = () => {
  let teacherId = null;
  if (data.user.role === 'TEACHER') {
    teacherId = data.user.id;
  }
  request.get('/course/selectPage', {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
      name: data.name,
      teacherId: teacherId,
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
  data.form = {
    score: 3,  // 默认值
    num: 30    // 默认值
  };
  data.formVisible = true;
};

// 编辑
const handleEdit = (row) => {
  data.form = JSON.parse(JSON.stringify(row));
  data.formVisible = true;
};

// 新增保存
const add = () => {
  if (!data.form.name || !data.form.content || !data.form.score || 
      !data.form.teacherId || !data.form.num || !data.form.time || 
      !data.form.location || !data.form.collegeId) {
    ElMessage.warning('请填写完整的课程信息！');
    return;
  }
  
  request.post('/course/add', data.form).then(res => {
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
  if (!data.form.name || !data.form.content || !data.form.score || 
      !data.form.teacherId || !data.form.num || !data.form.time || 
      !data.form.location || !data.form.collegeId) {
    ElMessage.warning('请填写完整的课程信息！');
    return;
  }
  
  request.put('/course/update', data.form).then(res => {
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
    request.delete('/course/deleteById/' + id).then(res => {
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

// 加载教师数据
const loadTeacher = () => {
  request.get('/teacher/selectAll').then(res => {
    if (res.code === '200') {
      data.teacherData = res.data;
    } else {
      ElMessage.error(res.msg || '获取教师数据失败');
    }
  });
};

// 选课
const choiceCourse = (row) => {
  let courseData = JSON.parse(JSON.stringify(row));
  courseData.studentId = data.user.id;
  
  ElMessageBox.confirm(`确定要选择"${row.name}"课程吗?`, '选课确认', { 
    type: 'info',
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    icon: 'InfoFilled'
  }).then(() => {
    request.post('/choice/add', courseData).then(res => {
      if (res.code === '200') {
        ElMessage.success('恭喜你选课成功！');
        load();
      } else {
        ElMessage.error(res.msg || '选课失败');
      }
    });
  }).catch(() => {});
};

// 初始化加载
load();
loadCollege();
loadTeacher();
</script>

<style scoped>
.course-container {
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

.course-name {
  display: flex;
  align-items: center;
  gap: 5px;
  font-weight: 500;
}

.course-content {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 200px;
  color: #606266;
}

.teacher-name,
.time-info,
.location-info,
.college-name {
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

.option-item {
  display: flex;
  align-items: center;
  gap: 5px;
}

.operation-buttons {
  display: flex;
  gap: 5px;
  justify-content: center;
}

:deep(.el-table) {
  border-radius: 5px;
  overflow: hidden;
}

:deep(.el-table th) {
  height: 45px;
}

:deep(.el-table td) {
  padding: 8px 0;
}

:deep(.el-table .el-button) {
  padding: 6px 12px;
}

:deep(.el-progress) {
  margin: 0;
  width: 80px;
}
</style>