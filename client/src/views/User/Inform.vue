<template>
  <div>
    <el-row :gutter="24" >
      <el-col :xs="24" :md="12" :span="12">
        <el-card header="用户个人信息">
          <div style="text-align: center">
            <el-avatar :src="avatar" :size="100"
                       style="box-shadow: 0px 0px 10px 1px rgba(0,21,41,.25); margin: 20px"></el-avatar>
          </div>
          <el-form :model="userInfo" label-width="80px">
            <el-form-item label="昵称"><el-input v-model="userInfo.nickname"></el-input></el-form-item>
            <el-form-item label="用户名">{{userInfo.username}}</el-form-item>
            <el-form-item label="用户组">{{userInfo.usergroup}}</el-form-item>
            <el-form-item><el-button type="primary" @click="changeUserInfo()">保存修改</el-button></el-form-item>
          </el-form>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="12" :span="12">
        <el-card header="学生信息" v-if="userInfo.usergroup === '2'">
          <el-form :model="userInfo" label-width="80px">
            <el-form-item label="学院">{{stuInfo.collegename}}</el-form-item>
            <el-form-item label="专业">{{stuInfo.majorname}}</el-form-item>
            <el-form-item label="班级">{{stuInfo.classname}}</el-form-item>
            <el-form-item label="姓名">{{stuInfo.name}}</el-form-item>
            <el-form-item label="学号">{{stuInfo.number}}</el-form-item>
            <el-form-item label="性别">
              <span v-if="stuInfo.sex === '1'">男</span>
              <span v-if="stuInfo.sex === '0'">女</span>
            </el-form-item>
            <el-form-item label="联系电话"><el-input v-model="stuInfo.phone"></el-input></el-form-item>
            <el-form-item><el-button type="primary" @click="changeStuInfo()">保存修改</el-button></el-form-item>
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
      userInfo: {
        id: JSON.parse(localStorage.getItem("UserInfo")).id,
        username: "",
        nickname: "",
        usergroup: "",
        note: "",
        password: "",
      },
      stuInfo:{},
      avatar:require("@/assets/zjweuer.png")
    };
  },
  mounted() {
    this.getUserInfo()
  },
  methods: {
    getUserInfo(){
      this.$request.get("/users/selectById/" + this.userInfo.id).then( res => {
        if (res.code === '200') {
          this.userInfo.username = res.data.username
          this.userInfo.nickname = res.data.nickname
          this.userInfo.usergroup = res.data.usergroup
          this.userInfo.note = res.data.note
          this.userInfo.password = res.data.password
          this.getStuInfo()
        }
      })
    },

    getStuInfo(){
      this.$request.get("/stuInfo/selectByNumber/" + this.userInfo.username).then( res => {
        if (res.code === '200') {
          this.stuInfo = res.data
        }
      })
    },

    changeUserInfo(){
      this.$request.put("/users/update",this.userInfo).then(res => {
        if (res.code === '200') {
          this.$notify.success({title: res.data})
          this.getUserInfo()
        } else {
          this.$notify.error({title: res.msg, message: res.data})
        }
      })
    },

    changeStuInfo(){
      this.$request.put("/stuInfo/update",this.stuInfo).then(res => {
        if (res.code === '200') {
          this.$notify.success({title: res.data})
          this.getStuInfo()
        } else {
          this.$notify.error({title: res.msg, message: res.data})
        }
      })
    },
  },
};
</script>
