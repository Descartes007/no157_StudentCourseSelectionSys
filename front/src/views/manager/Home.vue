<template>
  <div class="home-container">
    <!-- 欢迎卡片 -->
    <div class="welcome-card">
      <div class="welcome-avatar">
        <img :src="data.user.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" alt="头像">
      </div>
      <div class="welcome-info">
        <h2>欢迎您，{{ data.user.name }}</h2>
        <p>{{ getCurrentTime() }} · {{ getWeekDay() }} · {{ getGreeting() }}</p>
      </div>
    </div>

    <!-- 统计卡片区域 -->
    <div class="stats-container">
      <div class="stat-card" v-for="(item, index) in data.statsCards" :key="index">
        <div class="stat-icon" :style="{ backgroundColor: item.bgColor }">
          <el-icon :size="24" :color="'white'">
            <component :is="item.icon"></component>
          </el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ item.value }}</div>
          <div class="stat-title">{{ item.title }}</div>
        </div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="charts-container">
      <!-- 学院人数统计图 -->
      <div class="chart-card">
        <div class="card-header">
          <div class="card-title"><el-icon><PieChart /></el-icon> 学院学生人数分布</div>
          <el-button type="primary" link @click="loadCollegeStats">
            <el-icon><Refresh /></el-icon> 刷新
          </el-button>
        </div>
        <div class="chart-container" ref="collegeChartRef"></div>
      </div>

      <!-- 课程选课人数统计 -->
      <div class="chart-card">
        <div class="card-header">
          <div class="card-title"><el-icon><Histogram /></el-icon> 热门课程选课统计</div>
          <el-button type="primary" link @click="loadCourseStats">
            <el-icon><Refresh /></el-icon> 刷新
          </el-button>
        </div>
        <div class="chart-container" ref="courseChartRef"></div>
      </div>
    </div>

    <!-- 公告区域 -->
    <div class="notice-card">
      <div class="card-header">
        <div class="card-title"><el-icon><Bell /></el-icon> 系统公告</div>
        <el-button type="primary" link @click="loadNotice">
          <el-icon><Refresh /></el-icon> 刷新
        </el-button>
      </div>
      <div class="notice-scroll-container">
        <el-timeline>
          <el-timeline-item
            v-for="(item, index) in data.noticeData"
            :key="index"
            :timestamp="item.time"
            :color="getTimelineColor(index)"
          >
            <div class="notice-content">{{ item.content }}</div>
          </el-timeline-item>
        </el-timeline>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted, onBeforeUnmount } from "vue";
import request from "@/utils/request";
import { ElMessage } from "element-plus";
import * as echarts from 'echarts';
import { 
  User, Bell, PieChart, Histogram, Refresh, 
  Collection, Reading, School, Medal 
} from '@element-plus/icons-vue';

// 定义图表实例引用
const collegeChartRef = ref(null);
const courseChartRef = ref(null);
let collegeChart = null;
let courseChart = null;

// 定义响应式数据
const data = reactive({
  user: JSON.parse(localStorage.getItem('system-user') || '{}'),
  noticeData: [],
  collegeStats: [],
  courseStats: [],
  statsCards: [
    { title: '学生总数', value: 0, icon: 'User', bgColor: '#1e88e5' },
    { title: '课程总数', value: 0, icon: 'Reading', bgColor: '#43a047' },
    { title: '学院总数', value: 0, icon: 'School', bgColor: '#fb8c00' },
    { title: '选课总数', value: 0, icon: 'Collection', bgColor: '#e53935' }
  ]
});

// 获取问候语
const getGreeting = () => {
  const hour = new Date().getHours();
  if (hour < 6) return '夜深了，注意休息';
  if (hour < 9) return '早上好，新的一天';
  if (hour < 12) return '上午好，状态很棒';
  if (hour < 14) return '中午好，注意休息';
  if (hour < 17) return '下午好，继续加油';
  if (hour < 19) return '傍晚好，辛苦了';
  if (hour < 22) return '晚上好，放松一下';
  return '夜深了，注意休息';
};

// 获取当前时间
const getCurrentTime = () => {
  const now = new Date();
  const year = now.getFullYear();
  const month = (now.getMonth() + 1).toString().padStart(2, '0');
  const day = now.getDate().toString().padStart(2, '0');
  return `${year}-${month}-${day}`;
};

// 获取星期
const getWeekDay = () => {
  const weekdays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'];
  return weekdays[new Date().getDay()];
};

// 获取时间线颜色
const getTimelineColor = (index) => {
  const colors = ['#1e88e5', '#43a047', '#fb8c00', '#e53935', '#5e35b1'];
  return colors[index % colors.length];
};

// 初始化学院图表
const initCollegeChart = () => {
  if (collegeChart) {
    collegeChart.dispose();
  }
  
  collegeChart = echarts.init(collegeChartRef.value);
  collegeChart.setOption({
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      right: 10,
      top: 'center',
      data: data.collegeStats.map(item => item.name)
    },
    series: [
      {
        name: '学院人数',
        type: 'pie',
        radius: ['50%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 16,
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: data.collegeStats.map(item => ({
          name: item.name,
          value: item.count
        }))
      }
    ]
  });
  
  window.addEventListener('resize', () => {
    collegeChart.resize();
  });
};

// 初始化课程图表
const initCourseChart = () => {
  if (courseChart) {
    courseChart.dispose();
  }
  
  courseChart = echarts.init(courseChartRef.value);
  courseChart.setOption({
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'value',
      boundaryGap: [0, 0.01]
    },
    yAxis: {
      type: 'category',
      data: data.courseStats.map(item => item.name)
    },
    series: [
      {
        name: '选课人数',
        type: 'bar',
        data: data.courseStats.map(item => item.count),
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
            { offset: 0, color: '#83bff6' },
            { offset: 0.5, color: '#188df0' },
            { offset: 1, color: '#188df0' }
          ])
        }
      }
    ]
  });
  
  window.addEventListener('resize', () => {
    courseChart.resize();
  });
};

// 加载公告数据
const loadNotice = () => {
  request.get('/notice/selectAll').then(res => {
    if (res.code === '200') {
      data.noticeData = res.data;
    } else {
      ElMessage.error(res.msg);
    }
  });
};

// 加载学院统计数据
const loadCollegeStats = () => {
  request.get('/student/collegeCount').then(res => {
    if (res.code === '200') {
      data.collegeStats = res.data;
      initCollegeChart();
    } else {
      ElMessage.error(res.msg || '获取学院统计数据失败');
    }
  }).catch(() => {
    // 如果接口不存在，使用模拟数据
    data.collegeStats = [
      { name: '计算机学院', count: 120 },
      { name: '电子信息学院', count: 80 },
      { name: '数学学院', count: 65 },
      { name: '物理学院', count: 50 },
      { name: '化学学院', count: 45 }
    ];
    initCollegeChart();
  });
};

// 加载课程统计数据
const loadCourseStats = () => {
  request.get('/course/choiceCount').then(res => {
    if (res.code === '200') {
      data.courseStats = res.data.slice(0, 7); // 只取前7条
      initCourseChart();
    } else {
      ElMessage.error(res.msg || '获取课程统计数据失败');
    }
  }).catch(() => {
    // 如果接口不存在，使用模拟数据
    data.courseStats = [
      { name: '高等数学', count: 96 },
      { name: '大学英语', count: 85 },
      { name: '计算机基础', count: 75 },
      { name: '数据结构', count: 68 },
      { name: '操作系统', count: 45 },
      { name: '计算机网络', count: 42 },
      { name: '软件工程', count: 32 }
    ];
    initCourseChart();
  });
};

// 加载统计卡片数据
const loadStatsCardData = () => {
  // 学生总数
  request.get('/student/count').then(res => {
    if (res.code === '200') {
      data.statsCards[0].value = res.data || 0;
    }
  }).catch(() => {
    data.statsCards[0].value = 328;  // 模拟数据
  });
  
  // 课程总数
  request.get('/course/count').then(res => {
    if (res.code === '200') {
      data.statsCards[1].value = res.data || 0;
    }
  }).catch(() => {
    data.statsCards[1].value = 42;  // 模拟数据
  });
  
  // 学院总数
  request.get('/college/count').then(res => {
    if (res.code === '200') {
      data.statsCards[2].value = res.data || 0;
    }
  }).catch(() => {
    data.statsCards[2].value = 8;  // 模拟数据
  });
  
  // 选课总数
  request.get('/choice/count').then(res => {
    if (res.code === '200') {
      data.statsCards[3].value = res.data || 0;
    }
  }).catch(() => {
    data.statsCards[3].value = 645;  // 模拟数据
  });
};

// 生命周期钩子：挂载后
onMounted(() => {
  loadNotice();
  loadCollegeStats();
  loadCourseStats();
  loadStatsCardData();
});

// 生命周期钩子：卸载前
onBeforeUnmount(() => {
  if (collegeChart) {
    collegeChart.dispose();
  }
  if (courseChart) {
    courseChart.dispose();
  }
  window.removeEventListener('resize', () => {
    if (collegeChart) collegeChart.resize();
    if (courseChart) courseChart.resize();
  });
});
</script>

<style scoped>
.home-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding: 15px;
  height: calc(100vh - 80px);
  overflow-y: auto;
  overflow-x: hidden;
  /* 隐藏主容器滚动条 */
  scrollbar-width: none; /* Firefox */
  -ms-overflow-style: none; /* IE and Edge */
}

/* 隐藏主容器滚动条 - Chrome, Safari and Opera */
.home-container::-webkit-scrollbar {
  display: none;
}

/* 欢迎卡片样式 */
.welcome-card {
  background: linear-gradient(135deg, #1976d2, #64b5f6);
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  color: white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.welcome-avatar {
  width: 70px;
  height: 70px;
  border-radius: 50%;
  overflow: hidden;
  margin-right: 20px;
  border: 3px solid rgba(255, 255, 255, 0.7);
  background-color: white;
}

.welcome-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.welcome-info h2 {
  margin: 0;
  font-size: 24px;
  margin-bottom: 5px;
}

.welcome-info p {
  margin: 0;
  opacity: 0.85;
  font-size: 14px;
}

/* 统计卡片样式 */
.stats-container {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 15px;
}

.stat-card {
  background: white;
  border-radius: 10px;
  padding: 20px;
  display: flex;
  align-items: center;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  transition: transform 0.3s, box-shadow 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 5px;
}

.stat-title {
  font-size: 14px;
  color: #666;
}

/* 图表区域样式 */
.charts-container {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.chart-card {
  background: white;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.card-title {
  font-size: 16px;
  font-weight: bold;
  display: flex;
  align-items: center;
}

.card-title .el-icon {
  margin-right: 6px;
}

.chart-container {
  width: 100%;
  height: 300px;
}

/* 公告卡片样式 */
.notice-card {
  background: white;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  display: flex;
  flex-direction: column;
}

.notice-scroll-container {
  max-height: 400px;
  overflow-y: auto;
  padding-right: 10px;
  /* 隐藏公告容器滚动条 */
  scrollbar-width: none; /* Firefox */
  -ms-overflow-style: none; /* IE and Edge */
}

/* 隐藏公告容器滚动条 - Chrome, Safari and Opera */
.notice-scroll-container::-webkit-scrollbar {
  display: none;
}

.notice-content {
  background-color: #f9f9f9;
  padding: 12px;
  border-radius: 8px;
  line-height: 1.5;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  transition: transform 0.2s;
}

.notice-content:hover {
  transform: translateX(5px);
  background-color: #f0f7ff;
}

/* 适配小屏幕 */
@media (max-width: 1200px) {
  .stats-container {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .charts-container {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .stats-container {
    grid-template-columns: 1fr;
  }
}
</style>