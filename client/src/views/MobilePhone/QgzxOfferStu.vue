<template>
  <div>
    <div style="margin: 5px 0">
      <div>
        当前学年批次：<el-tag>{{ searchForm.pc }}</el-tag>
      </div>
    </div>
    <el-table :data="tableData" style="z-index: 0">
      <el-table-column  prop="pc" label="学年批次" width="auto" align="center">
      </el-table-column>
      <el-table-column label="姓名" width="auto" align="center">
        <template #default="scope">
          {{scope.row.xm}}
          <br>({{scope.row.xh}})
        </template>
      </el-table-column>
      <el-table-column label="录用部门" prop="bm" width="auto"></el-table-column>
    </el-table>
    <div style="padding-top: 20px">往年录用结果历史数据：</div>
    <el-table :data="tableData2" style="z-index: 0">
      <el-table-column  prop="pc" label="学年批次" width="auto" align="center">
      </el-table-column>
      <el-table-column label="姓名" width="auto" align="center">
        <template #default="scope">
          {{scope.row.xm}}
          <br>({{scope.row.xh}})
        </template>
      </el-table-column>
      <el-table-column label="录用部门" prop="bm" width="auto"></el-table-column>
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
        "xh": JSON.parse(localStorage.getItem("UserInfo")).username,
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
