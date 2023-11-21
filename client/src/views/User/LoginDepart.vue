<template>
  <div class="login">
    <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form">
      <img src="../../assets/zjweu0.png" class="login-logo">
      <h3 class="title">大学生事务管理与服务平台</h3>
      <el-form-item prop="username">
        <template #label><i class="el-icon-user" /> 部门</template>
        <el-select v-model="loginForm.username" placeholder="请选择用户" clearable style="width: 100%">
          <el-option
              v-for="item in userList"
              :key="item"
              :label="item.username"
              :value="item.username">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item prop="password">
        <template #label><i class="el-icon-lock" /> 密码</template>
        <el-input v-model="loginForm.password" type="password" auto-complete="off" placeholder="请输入密码"></el-input>
      </el-form-item>
      <el-form-item style="width:100%;">
        <el-button @click="login('loginForm')" size="medium" type="primary" style="width:100%;">
          <span>登 录</span>
        </el-button>
      </el-form-item>
      <div style="text-align: right" >
        <el-button type="text" @click="$router.push('/login')" style="font-size: 14px;">返回帐号密码登录</el-button>
      </div>
    </el-form>
    <!--  底部  -->
    <div class="el-login-footer">
      <div>Copyright © 1953-2023 浙江水利水电学院党委学工部 版权所有</div>
      <div>校大学生事务管理与服务中心 技术支持 系统版本：V2.0</div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      loginForm: {
        username: '',
        password: '',
      },
      loginRules:{
        username: [
          { required: true, message: '请输入账号', trigger: 'blur' },
          { min: 0, max: 50, message:'账号格式错误', trigger: 'blur'}
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, max: 50, message:'密码格式错误', trigger: 'blur'}
        ],
      },
      userList:[],
    }
  },
  created() {
    this.$request.get("/users/selectDepartName").then( res => {
      if (res.code === '200'){
        this.userList = res.data
      }
    })
  },
  methods: {
    login(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.$request.post('/login', this.loginForm).then(res => {
            if (res.code === '200'){
              this.$notify.success({title:"登录成功"});
              this.$router.push('/')
              localStorage.setItem("UserInfo",JSON.stringify(res.data))
            } else {
              this.$notify.error({title:"登录失败", message:res.msg});
            }
          })
        } else {
          //console.log('error submit!!');
          return false;
        }
      });
    }
  }
}
</script>

<style>
.login {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-image: url("../../assets/loginBG.jpg");
  background-size: cover;
}
.title {
  line-height: 30px;
  text-align: center;
  font-size: 20px;
  color: rgb(0,175,170);
}

.login-logo{
  width: 70%;
}

.login-form {
  border-radius: 6px;
  background: #ffffff;
  width:400px;
  padding: 40px 30px 10px 30px;
  box-shadow: gray 0 0 50px 5px;
}
.el-login-footer {
  line-height: 20px;
  position: fixed;
  bottom: 0;
  width: 100%;
  text-align: center;
  color: #fff;
  font-size: 12px;
  padding-bottom: 10px;
}

</style>