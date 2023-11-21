<template>
  <div>
    <div style="margin-bottom: 10px">
      <el-select v-model="searchForm.pc" class="topInput" clearable filterable placeholder="查看历史学年申请"
                 @change="load">
        <el-option
            v-for="item in terminfo"
            :key="item.id"
            :label="item.pcmc"
            :value="item.pcdm"
        />
      </el-select>
      <el-select v-model="searchForm.xq" class="topInput" clearable placeholder="请输入校区" @input="load()">
        <el-option label="钱塘校区" value="钱塘校区"></el-option>
        <el-option label="南浔校区" value="南浔校区"></el-option>
      </el-select>
      <el-input v-model="searchForm.nj" class="topInput" clearable placeholder="请输入年级" @input="load()"></el-input>
      <el-input v-model="searchForm.bj" class="topInput" clearable placeholder="请输入班级" @input="load()"></el-input>
      <el-input v-model="searchForm.xh" class="topInput" clearable placeholder="请输入学号" @input="load()"></el-input>
      <el-input v-model="searchForm.xm" class="topInput" clearable placeholder="请输入姓名" @input="load()"></el-input>
      <el-input v-model="searchForm.bm" class="topInput" clearable placeholder="请输入录用部门" @input="load()"></el-input>
      <el-button type="primary" @click="load()">搜索</el-button>
      <el-button style="margin-right: 10px" type="primary" @click="exportData()">导出数据</el-button>
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
    <div style="margin-top: 10px">
      <el-pagination
          :currentPage="pageNum"
          :page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
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
      dataForm: {
        "xhs": [],
        "bm": "",
        "pc": "",
      },
      terminfo: [],
      stuList: [],
      importDis: ''
    }
  },
  created() {
    this.confirmMenu()
  },
  methods: {
    load() {
      this.importDis = true
      if (this.searchForm.pc !== '' && this.searchForm.bm !== '') {
        this.importDis = false
      }
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
    getTerminfo() {
      this.$request.get("/qgzxTerm/selectAll")
          .then(res => {
            if (res.code === '200') {
              this.terminfo = res.data
              this.searchForm.pc = res.data[0].pcdm
              this.dataForm.pc = res.data[0].pcdm
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
</style>
