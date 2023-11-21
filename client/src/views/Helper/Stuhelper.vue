<template>
  <div>
    <div style="margin-bottom: 10px">
      <el-button style="margin-right: 10px" type="primary" @click="exportData()">导出数据</el-button>
      <el-upload :headers="myHeaders"
                 :on-progress="handleExcelImporting" :on-success="handleExcelImportSuccess"
                 :show-file-list="false"
                 accept="xlsx"
                 action="/api/vStuhelper/import"
                 style="display: inline-block;margin-right: 10px">
        <el-button type="primary">批量导入</el-button>
      </el-upload>
      <el-button style="margin-right: 10px" type="primary" @click="add()">新增数据</el-button>
      <el-popconfirm style="margin-right: 10px" title="确认删除?" @confirm="delBatch()">
        <template #reference>
          <el-button :disabled="delButtonDisable" type="danger">批量删除</el-button>
        </template>
      </el-popconfirm>
    </div>
    <div>
      <el-input v-model="searchForm.nj" class="topInput" clearable placeholder="请输入年级" @input="load()"></el-input>
      <el-input v-model="searchForm.xym" class="topInput" clearable placeholder="请输入部门名称"
                @input="load()"></el-input>
      <el-input v-model="searchForm.bjm" class="topInput" clearable placeholder="请输入班级名"
                @input="load()"></el-input>
      <el-input v-model="searchForm.xm" class="topInput" clearable placeholder="请输入姓名" @input="load()"></el-input>
      <el-input v-model="searchForm.xh" class="topInput" clearable placeholder="请输入学工号"
                @input="load()"></el-input>
      <el-select v-model="searchForm.lx" class="topInput" placeholder="资助认定状态" filterable @change="load" clearable>
        <el-option label="特别困难" value="3"></el-option>
        <el-option label="困难" value="2"></el-option>
        <el-option label="一般困难" value="1"></el-option>
        <el-option label="未认定/不予认定" value="0"></el-option>
      </el-select>
      <el-select v-model="searchForm.xn" class="topInput" placeholder="请选择认定学年" filterable clearable @change="load">
        <el-option
            v-for="item in schoolYear"
            :key="item.schoolyear"
            :label="item.schoolyear"
            :value="item.schoolyear"
        />
      </el-select>
      <el-button type="primary" @click="load()">搜索</el-button>
    </div>
    <el-table :data="tableData" style="z-index: 0" @selection-change="tableSelected">
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column label="年级" prop="nj" width="120"></el-table-column>
      <el-table-column label="部门名称" prop="xym" width="120"></el-table-column>
      <el-table-column label="班级名" prop="bjm" width="120"></el-table-column>
      <el-table-column label="姓名" prop="xm" width="120"></el-table-column>
      <el-table-column label="学工号" prop="xh" width="120"></el-table-column>
      <el-table-column label="认定类别" prop="lx" width="120">
        <template #default="scope">
          <el-tag v-if="scope.row.lx === '3'" type="danger">特别困难</el-tag>
          <el-tag v-if="scope.row.lx === '2'" type="warning">困难</el-tag>
          <el-tag v-if="scope.row.lx === '1'" type="normal">一般困难</el-tag>
          <el-tag v-if="scope.row.lx === '0'" type="info">未认定</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="认定学年" prop="xn" width="120"></el-table-column>
      <el-table-column align="center" fixed="right" label="操作" width="auto">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="handleEdit(scope.row.id)">修改</el-button>
          <el-popconfirm style="margin-left: 10px" title="确认删除?" @confirm="del(scope.row.id)">
            <template #reference>
              <el-button size="mini" style="color: red" type="text">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
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
    <div>
      <el-dialog :title="dialogSetting.title" :visible.sync="dialogSetting.show">
        <el-form label-width="100px" ref="dataForm" :model="dataForm" :rules="formRules">
          <el-form-item label="学工号" prop="xh">
            <el-input v-model="dataForm.xh" placeholder="请输入学工号"></el-input>
          </el-form-item>
          <el-form-item label="认定类别" prop="lx">
            <el-select v-model="dataForm.lx" placeholder="资助认定状态" filterable clearable>
              <el-option label="特别困难" value="3"></el-option>
              <el-option label="困难" value="2"></el-option>
              <el-option label="一般困难" value="1"></el-option>
              <el-option label="未认定/不予认定" value="0"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="认定学年" prop="xn">
            <el-select v-model="dataForm.xn" placeholder="请选择认定学年" filterable clearable>
              <el-option
                  v-for="item in schoolYear"
                  :key="item.schoolyear"
                  :label="item.schoolyear"
                  :value="item.schoolyear"
              />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="save('dataForm')">确定</el-button>
          </el-form-item>
        </el-form>
      </el-dialog>
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
        "nj": "",
        "xym": "",
        "bjm": "",
        "xm": "",
        "xh": "",
        "lx": "",
        "xn": "",
      },
      myHeaders: {token: JSON.parse(localStorage.getItem("UserInfo")).token},
      selectedIds: [],
      delButtonDisable: true,
      dialogSetting: {"title": "新增", "show": false},
      dataForm: {
        "nj": "",
        "xym": "",
        "bjm": "",
        "xm": "",
        "xh": "",
        "lx": "",
        "xn": "",
      },
      formRules: {
        nj: [
          {required: true, message: '请输入年级', trigger: 'blur'}
        ],
        xym: [
          {required: true, message: '请输入部门名称', trigger: 'blur'}
        ],
        bjm: [
          {required: true, message: '请输入班级名', trigger: 'blur'}
        ],
        xm: [
          {required: true, message: '请输入姓名', trigger: 'blur'}
        ],
        xh: [
          {required: true, message: '请输入学工号', trigger: 'blur'}
        ],
        lx: [
          {required: true, message: '请输入认定类别', trigger: 'blur'}
        ],
        xn: [
          {required: true, message: '请输入认定学年', trigger: 'blur'}
        ],
      },

      schoolYear:[],
    }
  },
  created() {
    this.confirmMenu()
  },
  methods: {
    load() {
      //创建页面时运行
      this.$request.post("/vStuhelper/pageSelect?pageNum=" + this.pageNum + "&pageSize=" + this.pageSize + "", this.searchForm)
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
          this.getSchoolYear()
          this.load()
        }
      })
    },
    getSchoolYear(){
      this.$request.get("/semesters/selectAllYear").then(res => {
        if (res.code === '200') {
          this.schoolYear = res.data
        } else {
          this.$notify.error({title: res.msg})
        }
      })
    },
    exportData() {
      this.$request.post("/vStuhelper/export", this.searchForm).then(res => {
        if (res.code === '200') {
          this.$notify.success({title: res.msg})
          window.open("/api/file/export/" + res.data)
        }
      })
    },
    handleExcelImportSuccess(res) {
      //console.log(res)
      if (res.code === '200') {
        this.$notify.success({title: res.msg, message: "新增" + res.data.add + "条，更新" + res.data.updata + "条"})
      }
      this.load()
    },
    handleExcelImporting() {
      this.$notify.warning("导入中……")
      this.load()
    },
    handleEdit(id) {
      this.$request.get("/vStuhelper/selectById/" + id).then(res => {
        if (res.code === '200') {
          this.dataForm = {
            "nj": "",
            "xym": "",
            "bjm": "",
            "xm": "",
            "xh": "",
            "lx": "",
            "xn": "",
          }
          this.dataForm = res.data
          this.dialogSetting.title = "修改"
          this.dialogSetting.show = true
        } else {
          this.$notify.error({title: res.msg})
        }
      })
    },
    add() {
      this.dataForm = {
        "nj": "",
        "xym": "",
        "bjm": "",
        "xm": "",
        "xh": "",
        "lx": "",
        "xn": "",
      }
      this.dialogSetting.title = "新增"
      this.dialogSetting.show = true
    },
    save(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          if (this.dialogSetting.title === "新增") {
            this.$request.post("/vStuhelper/add", this.dataForm).then(res => {
              if (res.code === '200') {
                this.$notify.success({title: res.data})
                this.dialogSetting.show = false
                this.load()
              } else {
                this.$notify.error({title: res.msg, message: res.data})
              }
            })
          } else {
            this.$request.put("/vStuhelper/update", this.dataForm).then(res => {
              if (res.code === '200') {
                this.$notify.success({title: res.data})
                this.dialogSetting.show = false
                this.load()
              } else {
                this.$notify.error({title: res.msg, message: res.data})
              }
            })
          }
        } else {
          //console.log('error submit!!');
          return false;
        }
      });
    },
    del(id) {
      this.$request.delete("/vStuhelper/del/" + id).then(res => {
        if (res.code === '200') {
          this.$notify.success({title: res.data})
          this.load()
        } else {
          this.$notify.error({title: res.msg, message: res.data})
        }
      })
    },
    delBatch() {
      if (!this.selectedIds.length) {
        this.$notify.error({title: "删除失败", message: "请选择需要删除的数据"})
        this.delButtonDisable = true
        return
      }
      this.$request.delete("/vStuhelper/del/batch", {data: this.selectedIds}).then(res => {
        if (res.code === '200') {
          this.$notify.success({title: res.data})
          this.load()
          this.delButtonDisable = true
        } else {
          this.$notify.error({title: res.msg, message: res.data})
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
    tableSelected(rows) {
      this.selectedIds = []
      this.selectedIds = rows.map(v => v.id)
      if (!this.selectedIds.length) {
        this.delButtonDisable = true
      } else {
        this.delButtonDisable = false
      }
    }
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
