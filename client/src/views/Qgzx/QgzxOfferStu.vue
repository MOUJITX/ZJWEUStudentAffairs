<template>
  <div>
    <div>
      当前学年批次：<el-tag>{{ searchForm.pc }}</el-tag>
    </div>
    <el-table :data="tableData" style="z-index: 0">
      <el-table-column label="招聘批次" prop="pc" width="130"></el-table-column>
      <el-table-column label="校区" prop="xq" width="80"></el-table-column>
      <el-table-column label="年级" prop="nj" width="60"></el-table-column>
      <el-table-column label="学院" prop="xy" width="120"></el-table-column>
      <el-table-column label="班级" prop="bj" width="120"></el-table-column>
      <el-table-column label="学号" prop="xh" width="120"></el-table-column>
      <el-table-column label="姓名" prop="xm" width="120"></el-table-column>
      <el-table-column label="录用部门" prop="bm" width="120"></el-table-column>
      <el-table-column label="录用结果" prop="zt" width="auto"></el-table-column>
      <el-table-column label="确认时间" prop="time" width="150"></el-table-column>
      <el-table-column label="申请表id" prop="applyid" width="80"></el-table-column>
    </el-table>
    <div style="padding-top: 20px">往年录用结果历史数据：</div>
    <el-table :data="tableData2" style="z-index: 0">
      <el-table-column label="招聘批次" prop="pc" width="130"></el-table-column>
      <el-table-column label="校区" prop="xq" width="80"></el-table-column>
      <el-table-column label="年级" prop="nj" width="60"></el-table-column>
      <el-table-column label="学院" prop="xy" width="120"></el-table-column>
      <el-table-column label="班级" prop="bj" width="120"></el-table-column>
      <el-table-column label="学号" prop="xh" width="120"></el-table-column>
      <el-table-column label="姓名" prop="xm" width="120"></el-table-column>
      <el-table-column label="录用部门" prop="bm" width="120"></el-table-column>
      <el-table-column label="录用结果" prop="zt" width="auto"></el-table-column>
      <el-table-column label="确认时间" prop="time" width="150"></el-table-column>
      <el-table-column label="申请表id" prop="applyid" width="80"></el-table-column>
    </el-table>
  </div>
</template>

<script>

export default {
  data() {
    return {
      tableData: [],
      tableData2: [],
      searchForm: {
        "xq": "",
        "nj": "",
        "xy": "",
        "bj": "",
        "xh": JSON.parse(localStorage.getItem("UserInfo")).username,
        "xm": "",
        "bm": "",
        "pc": "",
        "zt": "确认录用",
      },
      myHeaders: {token: JSON.parse(localStorage.getItem("UserInfo")).token},
    }
  },
  created() {
    this.confirmMenu()
  },
  methods: {
    load() {
      //创建页面时运行
      this.$request.get("/qgzxOffer/selectByXh/" + this.searchForm.xh)
          .then(res => {
            if (res.code === '200') {
              res.data.forEach(item => {
                if (item.pc === this.searchForm.pc) this.tableData.push(item)
                else this.tableData2.push(item)
              })
            }
          })
    },
    confirmMenu() {
      this.$request.post('/menu/ConfirmAccess', this.$route.path).then(res => {
        if (res.code === '403') {
          this.$notify.error({title: res.code, message: "当前页面暂无访问权限"})
          this.$router.push("/noPermission")
        } else {
          this.getTerminfo()
        }
      })
    },
    getTerminfo() {
      this.$request.get("/qgzxTerm/selectAll")
          .then(res => {
            if (res.code === '200') {
              this.searchForm.pc = res.data[0].pcdm
              this.load()
            } else {
              this.$notify.error({title: res.msg})
            }
          })
    },
  }
}
</script>
<style>
</style>
