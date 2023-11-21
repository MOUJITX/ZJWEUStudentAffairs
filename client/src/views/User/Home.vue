<template>
  <div>
    <template slot-scope="scope"></template>
    <el-row :gutter="24" >
      <el-col :xs="24" :md="14" :span="14">
        <el-card header="欢迎登录大学生事务管理与服务平台" style="height: 40vh">
          <div style="font-size: 15px;line-height: 30px">系统功能：</div>
          <el-button style="margin-right: 5px" type="primary" @click="$router.push('/Phone/mhome')" v-if="role === '2'">切换至移动端
          </el-button>
          <el-button style="margin-right: 5px" type="primary" @click="$router.push('/inform')">修改个人信息
          </el-button>
          <el-button style="margin-right: 5px" type="primary" @click="$router.push('/PassChange')">修改密码
          </el-button>
          <el-button style="margin-right: 5px" type="danger" @click="logout()">退出登录
          </el-button>
          <div v-if="role === '2' || role === '1'">
            <div style="font-size: 15px;line-height: 30px">勤工助学岗位申请：</div>
            <el-button style="margin-right: 5px" type="primary" @click="$router.push('/QgzxDepartShow')">查看岗位
            </el-button>
            <el-button style="margin-right: 5px" type="primary" @click="$router.push('/QgzxStu')">提交申请
            </el-button>
            <el-button style="margin-right: 5px" type="primary" @click="$router.push('/QgzxOfferStu')">录用结果
            </el-button>
          </div>
          <div v-if="role === '11' || role === '1'">
            <div style="font-size: 15px;line-height: 30px">勤工助学申请审核：</div>
            <div>
              <el-button style="margin-right: 5px" type="primary" @click="$router.push('/QgzxDepartShow')">查看岗位
              </el-button>
              <el-button style="margin-right: 5px" type="primary" @click="$router.push('/QgzxCollege')">学院审核
              </el-button>
              <el-button style="margin-right: 5px" type="primary" @click="$router.push('/QgzxOfferDepart')">学生录用（用工部门）
              </el-button>
              <el-button style="margin-right: 5px" type="primary" @click="$router.push('/QgzxOfferCollege')">录用结果（学院）
              </el-button>
            </div>
          </div>
          <div v-if="role === '13' || role === '1'">
            <div style="font-size: 15px;line-height: 30px">勤工助学申请审核：</div>
            <el-button style="margin-right: 5px" type="primary" @click="$router.push('/QgzxDepartShow')">查看岗位
            </el-button>
            <el-button style="margin-right: 5px" type="primary" @click="$router.push('/QgzxOfferDepart')">学生录用
            </el-button>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="10" :span="10" :push="0">
        <el-card style="height: 40vh">
          <el-carousel indicator-position="outside">
            <el-carousel-item v-for="item in imageData" :key="item">
              <div class="carousel-item-content">
                <a :href="item.url" target="_blank">
                  <img :src="item.img" class="carousel-item-image" width="100%"/>
                </a>
              </div>
            </el-carousel-item>
          </el-carousel>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="24" style="padding-top: 15px">
      <el-col :xs="24" :md="12" :span="14">
        <el-card header="通知公告">
          <el-collapse v-for="item in noticesData" :key="item">
            <el-collapse-item :title="item.title">
              <div v-html="item.detail"></div>
            </el-collapse-item>
          </el-collapse>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="12" :span="10" :push="0">
        <el-card>
          <el-calendar style="width: 100%;" v-model="calendar"></el-calendar>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style>
.el-calendar-day{
  height: auto !important;
  font-size: 12px;
}
.el-carousel__item h3 {
  color: #475669;
  font-size: 18px;
  opacity: 0.75;
  line-height: 300px;
  margin: 0;
}
.carousel-item-content {
  display: flex;
  justify-content: center; /* 水平居中 */
  align-items: center; /* 垂直居中 */
  height: 100%; /* 确保内容占满高度 */
}

.carousel-item-image {
  max-width: 100%; /* 图片宽度不超过容器 */
  max-height: 100%; /* 图片高度不超过容器 */
}
</style>
<script>
export default {
  data() {
    return {
      noticesData: [],
      imageData:[],
      role: JSON.parse(localStorage.getItem("UserInfo")).userGroup,
    }
  },
  created() {
    this.getNotices()
    this.getImage()
  },
  methods:{
    logout() {
      localStorage.removeItem('UserInfo')
      this.$router.push('/login')
    },
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
  }
}
</script>