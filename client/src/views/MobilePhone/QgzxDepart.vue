<template>
  <div>
<!--    功能区-->
    <div style="margin-bottom: 10px">
      <el-input v-model="searchForm.name" placeholder="请输入部门名称" style="width: 40%;" clearable @input="load()"></el-input>
        <el-button type="primary" style="margin-left: 5px" @click="load">搜索</el-button>
        <el-button type="primary" style="margin-left: 5px" @click="$router.push('/Phone/QgzxStu')" v-if="role === '2'">勤工助学申请提交</el-button>
    </div>
<!--    数据区-->
    <el-table :data="tableData" border stripe style="width:auto"  :header-cell-style="{'text-align':'center'}">
        <el-table-column prop="name" label="部门名称" width="auto" />
        <el-table-column label="录用人数">
            <el-table-column prop="qtrs" align="center" label="钱塘" width="50px" />
            <el-table-column prop="nxrs" align="center" label="南浔" width="50px" />
        </el-table-column>
      <el-table-column align="center" label="操作" width="80px">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="handleEdit(scope.row.id)">查看更多</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div style="margin: 10px;">
      <el-pagination
          :currentPage="pageNum"
          :page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :background=true
          :total="total"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange">
      </el-pagination>
    </div>

    <div>
      <el-dialog :title="dialogSetting.title" :visible.sync="dialogSetting.show" width="100%">

        <el-form  label-width="auto" ref="dataForm" :model="dataForm" :disabled="true">
          <el-form-item label="部门名称" prop="name">
            {{dataForm.name}}
          </el-form-item>
          <el-form-item label="岗位说明" prop="gwsm">
            {{dataForm.gwsm}}
          </el-form-item>
          <el-form-item label="聘用条件" prop="pytj">
            {{dataForm.pytj}}
          </el-form-item>
          <el-form-item style="font-weight: bolder" >钱塘校区</el-form-item>
            <el-form-item label="联系人" prop="qtlxr">
              {{dataForm.qtlxr}}
            </el-form-item>
            <el-form-item label="联系电话" prop="qtdh">
              {{dataForm.qtdh}}
            </el-form-item>
            <el-form-item label="岗位人数" prop="qtrs">
              {{dataForm.qtrs}}
            </el-form-item>
          <el-form-item label="办公地址" prop="qtbgdz">
            {{dataForm.qtbgdz}}
          </el-form-item>
          <el-form-item style="font-weight: bolder" >南浔校区</el-form-item>
            <el-form-item label="联系人" prop="nxlxr">
              {{dataForm.nxlxr}}
            </el-form-item>
            <el-form-item label="联系电话" prop="nxdh">
              {{dataForm.nxdh}}
            </el-form-item>
            <el-form-item label="岗位人数" prop="nxrs">
              {{dataForm.nxrs}}
            </el-form-item>
          <el-form-item label="办公地址" prop="nxbgdz">
            {{dataForm.nxbgdz}}
          </el-form-item>
        </el-form>
      </el-dialog>
    </div>
  </div>
</template>

<script>

export default {
  name: 'HomeView',
  components: {

  },
  data(){
    return{
      role: JSON.parse(localStorage.getItem("UserInfo")).userGroup,
      searchForm: {
        "id": "",
        "name": "",
        "detail": "1",
      },
      dataForm: {},
      pageNum:1,
      total:0,
      pageSize: 20,
      tableData:[],
      dialogSetting: {"title": "新增", "show": false},
    }

  },
  created() {
    this.confirmMenu()
  },
  methods: {
    load() {
      //创建页面时运行
      this.$request.post("/qgzxDepart/pageSelect?pageNum=" + this.pageNum + "&pageSize=" + this.pageSize + "", this.searchForm)
          .then(res => {
            if (res.code === '200') {
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
          this.load()
        }
      })
    },
    handleEdit(id) {
      this.$request.get("/qgzxDepart/selectById/" + id).then(res => {
        if (res.code === '200') {
          this.dataForm = res.data
          this.dialogSetting.title = "查看更多"
          this.dialogSetting.show = true
        } else {
          this.$notify.error({title: res.msg})
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
</style>