import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

const routes = [
  {path: '/login', name: 'login', component: ()=>import('../views/User/Login.vue')},
  {path: '/loginDepart', name: 'login', component: ()=>import('../views/User/LoginDepart.vue')},
  {path: '/register', name: 'register', component: ()=>import('../views/User/Register.vue')},
  {path: '/', name: 'Layout', component: ()=>import('../views/Layout.vue'), redirect:"/home",
    children:[
        //系统功能
      {path: 'home', name: 'home', component: ()=>import('../views/User/Home.vue')},
      {path: 'noPermission', name: 'noPermission', component: ()=>import('../views/NoPermission.vue')},
      {path: 'inform', name: 'inform', component: ()=>import('../views/User/Inform.vue')},
      {path: 'PassChange', name: 'PassChange', component: ()=>import('../views/User/PassChange.vue')},
        //基础数据
      {path: 'StuInfo', name: 'StuInfo', component: ()=>import('../views/BasicData/StuInfo.vue')},
      {path: 'TchInfo', name: 'TchInfo', component: ()=>import('../views/BasicData/TchInfo.vue')},
      {path: 'Department', name: 'Department', component: ()=>import('../views/BasicData/Department.vue')},
      {path: 'StuMajor', name: 'StuMajor', component: ()=>import('../views/BasicData/StuMajor.vue')},
      {path: 'StuClass', name: 'StuClass', component: ()=>import('../views/BasicData/StuClass.vue')},
        //资助认定
      {path: 'Stuhelper', name: 'Stuhelper', component: ()=>import('../views/Helper/Stuhelper.vue')},
        //勤工助学
      {path: 'QgzxApply', name: 'QgzxApply', component: ()=>import('../views/Qgzx/QgzxApply.vue')},
      {path: 'QgzxCollege', name: 'QgzxCollege', component: ()=>import('../views/Qgzx/QgzxCollege.vue')},
      {path: 'QgzxStu', name: 'QgzxStu', component: ()=>import('../views/Qgzx/QgzxStu.vue')},
      {path: 'QgzxDepartSet', name: 'QgzxDepart', component: ()=>import('../views/Qgzx/QgzxDepart.vue')},
      {path: 'QgzxDepartShow', name: 'QgzxDepartShow', component: ()=>import('../views/Qgzx/QgzxDepartShow.vue')},
      {path: 'QgzxTerm', name: 'QgzxTerm', component: ()=>import('../views/Qgzx/QgzxTerm.vue')},
      {path: 'QgzxOffer', name: 'QgzxOffer', component: ()=>import('../views/Qgzx/QgzxOffer.vue')},
      {path: 'QgzxOfferDepart', name: 'QgzxOfferDepart', component: ()=>import('../views/Qgzx/QgzxOfferDepart.vue')},
      {path: 'QgzxOfferCollege', name: 'QgzxOfferCollege', component: ()=>import('../views/Qgzx/QgzxOfferCollege.vue')},
      {path: 'QgzxOfferStu', name: 'QgzxOfferStu', component: ()=>import('../views/Qgzx/QgzxOfferStu.vue')},
      {path: 'QgzxOfferCount', name: 'QgzxOfferCount', component: ()=>import('../views/Qgzx/QgzxOfferCount.vue')},
      // 系统设置
      {path: 'Menu' , name: 'Menu', component:() => import('../views/System/Menu.vue')},
      {path: 'UserGroup' , name: 'UserGroup', component:() => import('../views/System/UserGroup.vue')},
      {path: 'Users' , name: 'Users', component:() => import('../views/System/Users.vue')},
      {path: 'Semesters' , name: 'Semesters', component:() => import('../views/System/Semesters.vue')},
      {path: 'Notices' , name: 'Notices', component:() => import('../views/System/Notices.vue')},
      {path: 'Image' , name: 'Image', component:() => import('../views/System/Image.vue')},
    ]},

  {path: '/Phone/', name: 'LayoutPhone', component: ()=>import('../views/LayoutPhone.vue'), redirect:"/home",
    children:[
      //移动端
      {path: 'mhome', name: 'mhome', component: ()=>import('../views/User/mhome.vue')},
      {path: 'noPermission', name: 'noPermission', component: ()=>import('../views/NoPermission.vue')},
      {path: 'inform', name: 'inform', component: ()=>import('../views/User/Inform.vue')},
      {path: 'PassChange', name: 'PassChange', component: ()=>import('../views/User/PassChange.vue')},
      {path: 'QgzxDepart', name: 'QgzxDepartPhone', component: ()=>import('../views/MobilePhone/QgzxDepart.vue')},
      {path: 'QgzxStu', name: 'QgzxStuPhone', component: ()=>import('../views/MobilePhone/QgzxStu.vue')},
      {path: 'QgzxCollege', name: 'QgzxStuPhone', component: ()=>import('../views/MobilePhone/QgzxCollege.vue')},
      {path: 'QgzxOfferDepart', name: 'QgzxOfferDepart', component: ()=>import('../views/MobilePhone/QgzxOfferDepart.vue')},
      {path: 'QgzxOfferCollege', name: 'QgzxOfferCollege', component: ()=>import('../views/MobilePhone/QgzxOfferCollege.vue')},
      {path: 'QgzxOfferStu', name: 'QgzxOfferStu', component: ()=>import('../views/MobilePhone/QgzxOfferStu.vue')},
    ]},
  {path: '*', name: 'notFound', component: ()=>import('../views/NotFound.vue')},
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes,
})

export default router
