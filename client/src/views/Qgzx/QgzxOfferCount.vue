<template>
  <div>
    <div>
      <el-select v-model="searchForm.pc" class="topInput" placeholder="查看历史学年申请"
                 @change="load">
        <el-option
            v-for="item in terminfo"
            :key="item.id"
            :label="item.pcmc"
            :value="item.pcdm"
        />
      </el-select>
      <el-button type="primary" @click="load()">搜索</el-button>
    </div>
    <el-table :data="tableData" style="z-index: 0" show-summary height="calc(80vh)">
      <el-table-column align="center" label="录用部门" prop="name" width="auto"></el-table-column>
      <el-table-column align="center" label="招聘计划" v-if="tableDataShow">
        <el-table-column align="center" label="钱塘校区" prop="qtrs" width="auto" sortable></el-table-column>
        <el-table-column align="center" label="南浔校区" prop="nxrs" width="auto" sortable></el-table-column>
      </el-table-column>
      <el-table-column align="center" label="录用人数">
        <el-table-column align="center" label="钱塘校区" prop="qtlq" width="auto" sortable></el-table-column>
        <el-table-column align="center" label="南浔校区" prop="nxlq" width="auto" sortable></el-table-column>
      </el-table-column>
      <el-table-column align="center" label="剩余人数" v-if="tableDataShow">
        <el-table-column align="center" label="钱塘校区" prop="qtsy" width="auto" sortable></el-table-column>
        <el-table-column align="center" label="南浔校区" prop="nxsy" width="auto" sortable></el-table-column>
      </el-table-column>
      <el-table-column align="center" label="招聘进度" v-if="tableDataShow">
        <el-table-column align="center" label="钱塘校区" width="auto">
          <template #default="scope">
            <el-tag v-if="scope.row.qtsy > 0" type="normal">未录满</el-tag>
            <el-tag v-if="scope.row.qtsy === 0" type="success">已录满</el-tag>
            <el-tag v-if="scope.row.qtsy < 0" type="danger">超标</el-tag>
          </template>
        </el-table-column>
        <el-table-column align="center" label="南浔校区" width="auto">
          <template #default="scope">
            <el-tag v-if="scope.row.nxsy > 0" type="normal">未录满</el-tag>
            <el-tag v-if="scope.row.nxsy === 0" type="success">已录满</el-tag>
            <el-tag v-if="scope.row.nxsy < 0" type="danger">超标</el-tag>
          </template>
        </el-table-column>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>

export default {
  data() {
    return {
      tableData: [],
      searchForm: {
        "pc": "",
      },
      myHeaders: {token: JSON.parse(localStorage.getItem("UserInfo")).token},
      terminfo: [],
      tableDataShow:false,
    }
  },
  created() {
    this.confirmMenu()
  },
  methods: {
    load() {
      //创建页面时运行
      this.$request.get("/qgzxOffer/count/" + this.searchForm.pc)
          .then(res => {
            if (res.code === '200') {
              this.tableData = res.data
              if (this.searchForm.pc === this.terminfo[0].pcdm) this.tableDataShow = true
              else this.tableDataShow = false
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
              this.terminfo = res.data
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
