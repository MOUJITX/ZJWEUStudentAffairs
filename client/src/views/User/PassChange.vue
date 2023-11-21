<template>
  <div>
    <el-row :gutter="24" >
      <el-col :xs="24" :md="12" :span="12">
        <el-card header="修改密码">
          <el-form ref="pswForm" :rules="pswRules" :model="pswForm">
            <el-form-item label="旧密码"  prop="oldPassword">
              <el-input type="password" v-model="pswForm.oldPassword"></el-input>
            </el-form-item>
            <el-form-item label="新密码"  prop="newPassword">
              <el-input type="password" v-model="pswForm.newPassword"></el-input>
            </el-form-item>
            <el-form-item label="确认密码"  prop="checkPassword">
              <el-input type="password" v-model="pswForm.checkPassword"></el-input>
            </el-form-item>
            <el-form-item label="">
              <el-button type="primary" @click="changePassword">确认修改</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>
<script>
export default {
  data() {
    return {
      pswForm:{
        oldPassword: '',
        newPassword: '',
        checkPassword: '',
      },
      userID: JSON.parse(localStorage.getItem("UserInfo")).id,
      pswRules:{
        oldPassword: [
          { required: true, message: '请输入旧密码', trigger: 'blur' },
          { min: 6, max: 50, message:'密码格式错误', trigger: 'blur'},
        ],
        newPassword: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          { min: 6, max: 50, message:'密码格式错误', trigger: 'blur'},
        ],
        checkPassword: [
          { required: true, message: '请确认新密码', trigger: 'blur' },
          { min: 6, max: 50, message:'密码格式错误', trigger: 'blur'},
        ],
      }
    };
  },
  methods: {
    changePassword() {

      this.$refs['pswForm'].validate((valid) => {
        if (valid) {
          if(this.pswForm.newPassword!==this.pswForm.checkPassword){
            this.$notify({
              title: '两次输入的密码不一致',
              type: 'error'
            });
          }else {
            this.$request.put("/users/changePSW",[this.userID,this.pswForm.oldPassword,this.pswForm.newPassword]).then( res => {
              if (res.code === '200') {
                this.$notify.success({title:res.data})
                this.$router.push("/Login")
              } else {
                this.$notify.error({title:res.msg})
              }
            })
          }
        } else {
          //console.log('error submit!!');
          return false;
        }
      });
    },
  },
};
</script>
