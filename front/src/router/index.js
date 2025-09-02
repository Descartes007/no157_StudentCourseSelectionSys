import {createRouter, createWebHistory} from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      component: () => import('@/views/Manager.vue'),
      redirect: '/home',
      children: [
        { path: 'person', component: () => import('@/views/manager/Person.vue')},
        { path: 'tPerson', component: () => import('@/views/manager/TPerson.vue')},
        { path: 'sPerson', component: () => import('@/views/manager/SPerson.vue')},
        { path: 'password', component: () => import('@/views/manager/Password.vue')},
        { path: 'home', component: () => import('@/views/manager/Home.vue')},
        { path: 'admin', component: () => import('@/views/manager/Admin.vue')},
        { path: 'teacher', component: () => import('@/views/manager/Teacher.vue')},
        { path: 'student', component: () => import('@/views/manager/Student.vue')},
        { path: 'notice', component: () => import('@/views/manager/Notice.vue')},
        { path: 'college', component: () => import('@/views/manager/College.vue')},
        { path: 'speciality', component: () => import('@/views/manager/Speciality.vue')},
        { path: 'course', component: () => import('@/views/manager/Course.vue')},
        { path: 'choice', component: () => import('@/views/manager/Choice.vue')},
      ]
    },
    { path: '/login', component: () => import('@/views/Login.vue')},
    { path: '/register', component: () => import('@/views/Register.vue')},
  ]
})

export default router
