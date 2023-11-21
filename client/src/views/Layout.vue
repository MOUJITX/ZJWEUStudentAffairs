<template>
  <el-container>
    <!--区域左：侧边栏-->
    <el-aside :width="asideWidth" class="aside" style="position: fixed">
      <div class="logo">
        <img :src="logoImg" height="50%">
      </div>
      <el-menu :collapse="isCollapse" :collapse-transition="false" :default-active="$route.path"
               active-text-color="#ffd04b"
               background-color="#334157"
               router style="border: none;" text-color="white" unique-opened>
        <el-menu-item index="/home">
          <i class="el-icon-s-home"/><span>首页</span>
        </el-menu-item>
        <el-submenu v-for="(item,id) in menuList" v-if="item.father === '0' && item.show === '1'" :key="id" :index="item.path">
          <template slot="title"><i :class="item.icon"/><span>{{ item.title }}</span></template>
          <div v-for="list in menuList">
            <el-menu-item v-if="list.show === '1' && list.father === item.id.toString()" :index="list.path">
              <i :class="list.icon"/><span>{{ list.title }}</span>
            </el-menu-item>
          </div>
        </el-submenu>
      </el-menu>
    </el-aside>
    <!--区域右：侧边栏-->
    <el-container>
      <!--头部-->
      <el-header :style="{ marginLeft: mainLeft, width: 'calc(100% - ' + asideWidth + ')' }">
        <i :class="collapseIcon" style="font-size: 20px" @click="handleCollapse()"></i>
        <el-breadcrumb separator-class="el-icon-arrow-right" style="margin-left: 20px">
          大学生事务管理与服务平台
        </el-breadcrumb>

        <div style="flex: 1;display: flex;justify-content: flex-end;">
          <el-dropdown placement="bottom">
              <span class="el-dropdown-link" style="display: flex;align-items: center;cursor: default">
                <el-avatar :src="avatar"
                           style="box-shadow: 0px 0px 10px 1px rgba(0,21,41,.25);"></el-avatar>
                <span style="margin-left: 10px">{{ nickname }}</span>
                <i class="el-icon-arrow-down el-icon--right"></i>
              </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item @click.native="$router.push('/inform')">个人信息</el-dropdown-item>
              <el-dropdown-item @click.native="$router.push('/PassChange')">修改密码</el-dropdown-item>
              <el-dropdown-item @click.native="logout()">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </el-header>
      <!--主体-->
      <el-main v-loading="loading" :style="{ marginLeft: mainLeft }" style="margin-top: 60px">
        <div v-if="showMain"><router-view/></div>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>


import router from "@/router";

export default {
  name: 'HomeView',
  components: {},
  data() {
    return {
      //数据
      loading: false,
      mainLeft: '200px',
      isCollapse: false,//收缩
      collapseIcon: 'el-icon-s-fold',
      asideWidth: '200px',
      logoImg: require('@/assets/zjweu.png'),
      nickname: '',
      token: '',
      menuList: [],
      user: JSON.parse(localStorage.getItem("UserInfo")),
      showMain: true,
      avatar:require("@/assets/zjweuer.png")
    }
  },
  created() {
    if (this.user != null) {
      this.nickname = this.user.nickname
      this.token = this.user.token
      const flag = navigator.userAgent.match(/(phone|pad|pod|iPhone|iPod|ios|iPad|Android|Mobile|BlackBerry|IEMobile|MQQBrowser|JUC|Fennec|wOSBrowser|BrowserNG|WebOS|Symbian|Windows Phone)/i)
      if (flag) {
        this.$router.push("/Phone/mhome")
      }
    }
    this.loadMenu()
    this.confirmMenu()
  },
  methods: {
    router() {
      return router
    },
    loadMenu() {
      //console.log('loadMenu')
      this.$request.get('/menu/getMenu').then(res => {
        //console.log('loadMenu', res)
        if (res.code === '200') {
          this.menuList = res.data
        }
      })
    },
    confirmMenu(){
      this.$request.post('/menu/ConfirmAccess',this.$route.path).then(res => {
        if (res.code === '403') {
          this.$notify.error({title:res.code,message:"当前页面暂无访问权限"})
          this.$router.push("/noPermission")
        }
      })
    },
    //点击收缩
    handleCollapse() {
      this.loading = true;
      this.isCollapse = !this.isCollapse;
      this.mainLeft = this.isCollapse ? '64px' : '200px';
      this.asideWidth = this.isCollapse ? '64px' : '200px';
      this.logoImg = this.isCollapse ? require('@/assets/zjweuLogo.png') : require('@/assets/zjweu.png');
      setTimeout(() => {
        this.collapseIcon = this.isCollapse ? 'el-icon-s-unfold' : 'el-icon-s-fold';
        this.loading = false;
      }, 350);
    },
    logout() {
      localStorage.removeItem('UserInfo')
      this.$router.push('/login')
    }
  }
}
</script>

<style>
.aside {
  min-height: 100vh;
  background-color: #334157;
  box-shadow: 2px 0 6px rgba(0, 21, 41, .35);
}

.logo {
  height: 60px;
  line-height: 60px;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
}

.el-menu-item:hover, .el-submenu__title:hover {
  color: #ffd04b !important;
  background-color: #001529 !important;
}

.el-menu-item:hover i, .el-submenu__title:hover i {
  color: #ffd04b !important;
}

.el-menu-item.is-active {
  background-color: #001529 !important;
}

.el-aside {
  transition: width .3s;
}

.el-header {
  position: fixed;
  margin-left: 200px;
  box-shadow: 2px 0 6px rgba(0, 21, 41, .35);
  background: white;
  z-index: 5;
  display: flex;
  align-items: center;
}

</style>