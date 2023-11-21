<template>
  <div>
    <el-carousel height="230px">
      <el-carousel-item v-for="item in imageData" :key="item">
        <div class="carousel-item-content">
          <a :href="item.url" target="_blank">
            <img :src="item.img" class="carousel-item-image" width="100%"/>
          </a>
        </div>
      </el-carousel-item>
    </el-carousel>
    <el-card header="欢迎登录大学生事务管理与服务平台">
      <div style="font-size: 15px;line-height: 30px">系统功能：</div>
      <el-button style="margin-right: 5px" type="primary" @click="$router.push('/Phone/inform')">修改个人信息
      </el-button>
      <el-button style="margin-right: 5px" type="primary" @click="$router.push('/Phone/PassChange')">修改密码
      </el-button>
      <el-button style="margin-right: 5px" type="danger" @click="logout()">退出登录
      </el-button>
      <div v-if="role === '2' || role === '1'">
        <div style="font-size: 15px;line-height: 30px">勤工助学岗位申请：</div>
        <el-button style="margin-right: 5px" type="primary" @click="$router.push('/Phone/QgzxDepart')">查看岗位
        </el-button>
        <el-button style="margin-right: 5px" type="primary" @click="$router.push('/Phone/QgzxStu')">提交申请
        </el-button>
        <el-button style="margin-right: 5px" type="primary" @click="$router.push('/Phone/QgzxOfferStu')">录用结果
        </el-button>
      </div>
      <div v-if="role === '11' || role === '1'">
        <div style="font-size: 15px;line-height: 30px">勤工助学申请审核：</div>
        <div>
          <el-button style="margin-right: 5px" type="primary" @click="$router.push('/Phone/QgzxDepart')">查看岗位
          </el-button>
          <el-button style="margin-right: 5px" type="primary" @click="$router.push('/Phone/QgzxCollege')">学院审核
          </el-button>
        </div>
        <div style="padding-top: 5px">
          <el-button style="margin-right: 5px" type="primary" @click="$router.push('/Phone/QgzxOfferDepart')">学生录用（用工部门）
          </el-button>
          <el-button style="margin-right: 5px" type="primary" @click="$router.push('/Phone/QgzxOfferCollege')">录用结果（学院）
          </el-button>
        </div>
      </div>
      <div v-if="role === '13' || role === '1'">
        <div style="font-size: 15px;line-height: 30px">勤工助学申请审核：</div>
        <el-button style="margin-right: 5px" type="primary" @click="$router.push('/Phone/QgzxDepart')">查看岗位
        </el-button>
        <el-button style="margin-right: 5px" type="primary" @click="$router.push('/Phone/QgzxOfferDepart')">学生录用
        </el-button>
      </div>
    </el-card>
    <el-card header="通知公告" style="margin: 20px 0">
      <el-collapse v-for="item in noticesData" :key="item">
        <el-collapse-item :title="item.title">
          <div v-html="item.detail"></div>
        </el-collapse-item>
      </el-collapse>
    </el-card>
  </div>
</template>

<style>
.carousel-item-content {
  display: flex;
  justify-content: center; /* 水平居中 */
  align-items: center; /* 垂直居中 */
  height: 100%; /* 确保内容占满高度 */
}
</style>
<script>
export default {
  data() {
    return {
      role: JSON.parse(localStorage.getItem("UserInfo")).userGroup,
      noticesData: [],
      imageData: []
    }
  },
  created() {
    this.getNotices()
    this.getImage()
  },
  methods: {
    getNotices() {
      //创建页面时运行
      this.$request.get("/notices/selectAll")
          .then(res => {
            if (res.code === '200') {
              this.noticesData = res.data
            }
          })
    },
    getImage() {
      //创建页面时运行
      this.$request.get("/image/selectAll")
          .then(res => {
            if (res.code === '200') {
              this.imageData = res.data
            }
          })
    },
    logout() {
      localStorage.removeItem('UserInfo')
      this.$router.push('/login')
    }
  }
}
</script>