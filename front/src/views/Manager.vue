<template>
  <div>
    <!-- 头部导航栏 -->
    <div style="height: 60px; background: linear-gradient(90deg, #1565c0, #1976d2); display: flex; align-items: center; border-bottom: 1px solid #0d47a1; box-shadow: 0 2px 6px rgba(0,0,0,0.2);">
      <div style="flex: 1">
        <div style="padding-left: 20px; display: flex; align-items: center">
          <div class="logo-container">
            <el-icon class="logo-icon"><School /></el-icon>
          </div>
          <div style="font-weight: bold; font-size: 22px; margin-left: 10px; color: white; text-shadow: 0 1px 2px rgba(0,0,0,0.2);">学生选课管理系统</div>
        </div>
      </div>
      <div style="width: fit-content; padding-right: 20px; display: flex; align-items: center;">
        <el-dropdown trigger="click">
          <div style="display: flex; align-items: center; cursor: pointer;">
            <div class="avatar-box">
              <img style="width: 40px; height: 40px; border-radius: 8px; object-fit: cover;" 
                :src="data.user.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" alt="">
            </div>
            <span style="margin-left: 8px; color: white; font-weight: 500; text-shadow: 0 1px 1px rgba(0,0,0,0.2);">{{ data.user.name }}</span>
            <el-icon style="margin-left: 5px; color: white;"><arrow-down /></el-icon>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item v-if="data.user.role === 'ADMIN'" @click="router.push('/person')">
                <el-icon><UserFilled /></el-icon><span>个人资料</span>
              </el-dropdown-item>
              <el-dropdown-item v-if="data.user.role === 'TEACHER'" @click="router.push('/tPerson')">
                <el-icon><UserFilled /></el-icon><span>个人资料</span>
              </el-dropdown-item>
              <el-dropdown-item v-if="data.user.role === 'STUDENT'" @click="router.push('/sPerson')">
                <el-icon><UserFilled /></el-icon><span>个人资料</span>
              </el-dropdown-item>
              <el-dropdown-item @click="router.push('/password')">
                <el-icon><Key /></el-icon><span>修改密码</span>
              </el-dropdown-item>
              <el-dropdown-item @click="logout">
                <el-icon><SwitchButton /></el-icon><span>退出系统</span>
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>

    <div style="display: flex">
      <!-- 侧边导航栏 -->
      <div style="width: 220px; background-color: #263238; min-height: calc(100vh - 60px); box-shadow: 2px 0 5px rgba(0,0,0,0.1);">
        <el-menu
            router
            style="border: none; background-color: transparent;"
            :default-active="router.currentRoute.value.path"
            class="custom-menu"
            text-color="#b0bec5"
            active-text-color="#ffffff"
        >
          <el-menu-item index="/home" class="menu-item">
            <el-icon><HomeFilled /></el-icon>
            <span>系统首页</span>
          </el-menu-item>
          
          <el-menu-item index="/notice" v-if="data.user.role === 'ADMIN'" class="menu-item">
            <el-icon><Bell /></el-icon>
            <span>公告信息</span>
          </el-menu-item>
          
          <el-menu-item index="/college" class="menu-item">
            <el-icon><OfficeBuilding /></el-icon>
            <span>学院信息</span>
          </el-menu-item>
          
          <el-menu-item index="/speciality" class="menu-item">
            <el-icon><Briefcase /></el-icon>
            <span>专业信息</span>
          </el-menu-item>
          
          <el-menu-item index="/course" class="menu-item">
            <el-icon><Calendar /></el-icon>
            <span>课程信息</span>
          </el-menu-item>
          
          <el-menu-item index="/choice" class="menu-item">
            <el-icon><List /></el-icon>
            <span>选课信息</span>
          </el-menu-item>
          
          <div class="menu-divider"></div>
          
          <el-menu-item index="/admin" v-if="data.user.role === 'ADMIN'" class="menu-item">
            <el-icon><Medal /></el-icon>
            <span>管理员信息</span>
          </el-menu-item>
          
          <el-menu-item index="/teacher" v-if="data.user.role === 'ADMIN'" class="menu-item">
            <el-icon><UserFilled /></el-icon>
            <span>教师信息</span>
          </el-menu-item>
          
          <el-menu-item index="/student" v-if="data.user.role === 'ADMIN'" class="menu-item">
            <el-icon><Avatar /></el-icon>
            <span>学生信息</span>
          </el-menu-item>
        </el-menu>
      </div>

      <!-- 主要内容区 -->
      <div style="flex: 1; width: 0; background-color: #f5f5f5; padding: 15px; overflow: auto;">
        <router-view @updateUser="updateUser" />
      </div>
    </div>

  </div>
</template>

<script setup>
import { reactive } from "vue";
import router from "@/router";
import {ElMessage} from "element-plus";
import { ArrowDown } from '@element-plus/icons-vue';

const data = reactive({
  user: JSON.parse(localStorage.getItem('system-user') || '{}')
})

if (!data.user?.id) {
  ElMessage.error('请登录！')
  router.push('/login')
}

const updateUser = () => {
  data.user = JSON.parse(localStorage.getItem('system-user') || '{}')
}

const logout = () => {
  ElMessage.success('退出成功')
  localStorage.removeItem('system-user')
  router.push('/login')
}
</script>

<style scoped>
.logo-container {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.2);
}

.logo-icon {
  font-size: 24px;
  color: #1565c0;
}

.avatar-box {
  border: 2px solid rgba(255, 255, 255, 0.9);
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0,0,0,0.2);
}

.menu-divider {
  height: 1px;
  background-color: rgba(176, 190, 197, 0.2);
  margin: 10px 15px;
}

.menu-item {
  margin: 4px 8px;
  border-radius: 6px;
  transition: all 0.3s;
  height: 50px;
}

.custom-menu {
  padding: 5px;
}

:deep(.el-menu) {
  background-color: transparent !important;
}

.el-menu-item.is-active {
  background-color: #1e88e5 !important;
  color: white !important;
  font-weight: bold;
  box-shadow: 0 2px 5px rgba(0,0,0,0.2);
}

.el-menu-item:hover {
  background-color: rgba(30, 136, 229, 0.15) !important;
  color: white !important;
}

:deep(.el-menu-item) {
  height: 50px;
  line-height: 50px;
}

:deep(.el-menu-item .el-icon) {
  font-size: 18px;
  margin-right: 5px;
  color: inherit;
}

:deep(th) {
  color: #333;
}
</style>