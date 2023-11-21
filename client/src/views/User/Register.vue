<template>
  <div class="login">
    <el-form ref="registerForm" :model="registerForm" :rules="loginRules" class="login-form">
      <img class="login-logo" src="../../assets/zjweu0.png">
      <h3 class="title">大学生事务管理与服务平台</h3>
      <el-form-item prop="username">
        <template #label><i class="el-icon-user"/> 账号　　</template>
        <el-input v-model="registerForm.username" placeholder="请输入账号" style="width: 70%"></el-input>
      </el-form-item>
      <el-form-item prop="phone">
        <template #label><i class="el-icon-mobile"/> 联系电话</template>
        <el-input v-model="registerForm.phone" maxlength="11" placeholder="请输入联系电话"
                  style="width: 70%"></el-input>
      </el-form-item>
      <el-form-item prop="password">
        <template #label><i class="el-icon-lock"/> 设置密码</template>
        <el-input v-model="registerForm.password" placeholder="请输入密码" show-password style="width: 70%"></el-input>
      </el-form-item>
      <el-form-item prop="passwordConfirm">
        <template #label><i class="el-icon-lock"/> 确认密码</template>
        <el-input v-model="registerForm.passwordConfirm" placeholder="请输入确认密码" show-password
                  style="width: 70%"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button size="medium" style="font-size: 16px;width: 100%" type="primary" @click="register('registerForm')">
          激活账号
        </el-button>
      </el-form-item>
      <div style="text-align: right">
        <el-button style="font-size: 14px;" type="text" @click="$router.push('/login')">返回登录</el-button>
      </div>
    </el-form>
    <!--  底部  -->
    <div class="el-login-footer">
      <div>Copyright © 1953-2023 浙江水利水电学院党委学工部 版权所有</div>
      <div>校大学生事务管理与服务中心 技术支持</div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    var validatePass = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入密码'));
      } else {
        if (this.registerForm.password !== '') {
          this.$refs.registerForm.validateField('checkPass');
        }
        callback();
      }
    };
    var validatePass2 = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入密码'));
      } else if (value !== this.registerForm.password) {
        callback(new Error('两次输入密码不一致!'));
      } else {
        callback();
      }
    };
    return {
      registerForm: {
        username: '',
        password: '',
        passwordConfirm: '',
        phone: '',
      },
      loginRules: {
        username: [
          {required: true, message: '请输入账号', trigger: 'blur'},
          {min: 5, max: 10, message: '账号格式错误', trigger: 'blur'}
        ],
        phone: [
          {required: true, message: '请输入联系电话', trigger: 'blur'},
          {min: 11, max: 11, message: '联系电话格式错误', trigger: 'blur'}
        ],
        password: [
          {required: true, message: '请输入密码', trigger: 'blur'},
          {min: 6, max: 50, message: '密码格式错误', trigger: 'blur'},
          {validator: validatePass, trigger: 'blur'}
        ],
        passwordConfirm: [
          {required: true, message: '请输入确认密码', trigger: 'blur'},
          {min: 6, max: 50, message: '密码格式错误', trigger: 'blur'},
          {validator: validatePass2, trigger: 'blur'}
        ],
      }
    }
  },
  methods: {
    register(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.$request.post("/register", this.registerForm).then(res => {
            //console.log(res)
            if (res.code === "200") {
              this.$notify.success({title: res.msg, message: res.data});
              this.$router.push('/login')
            } else {
              this.$notify.error({title: res.msg, message: res.data});
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
  color: rgb(0, 175, 170);
}

.login-logo {
  width: 70%;
}

.login-form {
  border-radius: 6px;
  background: #ffffff;
  width: 400px;
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