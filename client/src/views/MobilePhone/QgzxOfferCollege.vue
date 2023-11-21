<template>
  <div>
    <div style="margin: 5px 0">
      <div>
        当前学年批次：<el-tag>{{ searchForm.pc }}</el-tag>
      </div>
    </div>

    <el-drawer
        :title="drawerSetting.title"
        :visible.sync="drawerSetting.show"
        direction="btt">
      <el-form label-width="100px">
        <el-form-item label="申请学年">
          <el-select v-model="searchForm.pc" style="width: 90%;" clearable filterable placeholder="查看历史学年申请"
                     @change="load">
            <el-option
                v-for="item in terminfo"
                :key="item.id"
                :label="item.pcmc"
                :value="item.pcdm"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="校区">
          <el-select v-model="searchForm.xq" style="width: 90%;" clearable placeholder="请输入校区" @input="load()">
            <el-option label="钱塘校区" value="钱塘校区"></el-option>
            <el-option label="南浔校区" value="南浔校区"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="年级">
          <el-input v-model="searchForm.nj" style="width: 90%;" clearable placeholder="请输入年级" @input="load()"></el-input>
        </el-form-item>
        <el-form-item label="学号">
          <el-input v-model="searchForm.xh" style="width: 90%;" clearable placeholder="请输入学号" @input="load()"></el-input>
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="searchForm.xm" style="width: 90%;" clearable placeholder="请输入姓名" @input="load()"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="load()">搜索</el-button>
        </el-form-item>
      </el-form>
    </el-drawer>


    <div>
      <el-button type="primary" @click="drawerSet()">筛选</el-button>
      <el-button style="margin-right: 10px" type="primary" @click="exportData()">导出数据</el-button>
    </div>
    <el-table :data="tableData" style="z-index: 0">
      <el-table-column  prop="bj" label="班级" width="100" align="center">
      </el-table-column>
      <el-table-column label="姓名" width="100" align="center">
        <template #default="scope">
          {{scope.row.xm}}
          <br>({{scope.row.xh}})
        </template>
      </el-table-column>
      <el-table-column label="录用部门" prop="bm" width="auto"></el-table-column>
    </el-table>
    <div style="margin-top: 10px">
      <el-pagination
          :currentPage="pageNum"
          :page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange">
      </el-pagination>
    </div>
  </div>
</template>

<script>

export default {
  data() {
    return {
      pageNum: 1, //当前页面
      pageSize: 10, //页面大小
      total: 0,  //总数
      tableData: [],
      searchForm: {
        "xq": "",
        "nj": "",
        "xy": JSON.parse(localStorage.getItem("UserInfo")).note,
        "bj": "",
        "xh": "",
        "xm": "",
        "bm": "",
        "pc": "",
        "zt": "确认录用",
      },
      myHeaders: {token: JSON.parse(localStorage.getItem("UserInfo")).token},
      terminfo: [],
      drawerSetting: {"title": "设置筛选条件", "show": false},
    }
  },
  created() {
    this.confirmMenu()
  },
  methods: {
    load() {
      //创建页面时运行
      this.$request.post("/qgzxOffer/pageSelect?pageNum=" + this.pageNum + "&pageSize=" + this.pageSize + "", this.searchForm)
          .then(res => {
            if (res.code === '200') {
              //console.log(res.data)
              this.tableData = res.data.list
              this.pageNum = res.data.pageNum
              this.total = res.data.total
              this.pageSize = res.data.pageSize
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
    drawerSet(){
      this.drawerSetting.show = true
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
    exportData() {
      this.$request.post("/qgzxOffer/export", this.searchForm).then(res => {
        if (res.code === '200') {
          this.$notify.success({title: res.msg})
          window.open("/api/file/export/" + res.data)
        }
      })
    },
    handleSizeChange(pageSize) {
      this.pageSize = pageSize
      this.load()
    },
    handleCurrentChange(pageNum) {
      this.pageNum = pageNum
      this.load()
    },
  }
}
</script>
<style>
.topInput {
  width: 10%;
  margin: 0 5px
}

.topInput {
  margin: 0 5px
}

.el-drawer {
  height: auto !important;
}


</style>
