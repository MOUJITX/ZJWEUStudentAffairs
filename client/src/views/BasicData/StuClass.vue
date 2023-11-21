<template>
  <div>
    <div style="margin-bottom: 10px">
      <el-button style="margin-right: 10px" type="primary" @click="exportData()">导出数据</el-button>
      <el-upload :headers="myHeaders"
                 :on-progress="handleExcelImporting" :on-success="handleExcelImportSuccess"
                 :show-file-list="false"
                 accept="xlsx"
                 action="/api/stuClass/import"
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
      <el-select v-model="searchForm.xyh" placeholder="请选择学院" class="topInput" clearable @change="load" filterable>
        <el-option v-for="item in department" :key="item.departid" :label="item.departname" :value="item.departid"></el-option>
      </el-select>
      <el-select v-model="searchForm.zyh" placeholder="请选择专业" class="topInput" clearable @change="load" filterable>
        <el-option v-for="item in majorlist" :key="item.zyh" :label="item.zym" :value="item.zyh">
          <span style="float: left">{{ item.zym }}</span>
          <span style="float: right;  font-size: 13px">
            <span v-if="item.zt === '1'" style="color: #409eff;font-weight: bold">在招</span>
            <span v-if="item.zt === '0'" style="color: #8492a6;">停招</span>
          </span>
        </el-option>
      </el-select>
      <el-select v-model="searchForm.nj" placeholder="请选择年级" class="topInput" clearable @change="load">
        <el-option v-for="i in (new Date().getFullYear()-2000)"
                   :key="new Date().getFullYear()+1-i"
                   :label="new Date().getFullYear()+1-i"
                   :value="new Date().getFullYear()+1-i"></el-option>
      </el-select>
      <el-input v-model="searchForm.bjh" class="topInput" clearable placeholder="请输入班级号"
                @input="load()"></el-input>
      <el-input v-model="searchForm.bm" class="topInput" clearable placeholder="请输入班级名"
                @input="load()"></el-input>
      <el-select v-model="searchForm.by" placeholder="是否毕业" class="topInput" clearable @change="load">
        <el-option key="1" label="已毕业" value="1"></el-option>
        <el-option key="0" label="未毕业" value="0"></el-option>
      </el-select>
      <el-button type="primary" @click="load()">搜索</el-button>
    </div>
    <el-table :data="tableData" style="z-index: 0" @selection-change="tableSelected">
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column label="学院号" prop="xyh" width="70"></el-table-column>
      <el-table-column label="学院名" prop="xym" width="auto"></el-table-column>
      <el-table-column label="专业号" prop="zyh" width="70"></el-table-column>
      <el-table-column label="专业名" prop="zym" width="auto"></el-table-column>
      <el-table-column label="年级" prop="nj" width="70"></el-table-column>
      <el-table-column label="班级号" prop="bjh" width="100"></el-table-column>
      <el-table-column label="班级名" prop="bm" width="120"></el-table-column>
      <el-table-column label="毕业" prop="by" width="80">
        <template #default="scope">
          <el-tag v-if="scope.row.by === '0'">未毕业</el-tag>
          <el-tag v-if="scope.row.by === '1'" type="warning" >已毕业</el-tag>
        </template>
      </el-table-column>
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
          <el-form-item label="学院号" prop="xyh">
            <el-select v-model="dataForm.xyh" placeholder="请选择学院" clearable filterable @change="getMajorForDataForm()">
              <el-option v-for="item in department" :key="item.departid" :label="item.departname" :value="item.departid"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="专业号" prop="zyh">
            <el-select v-model="dataForm.zyh" placeholder="请选择专业" clearable filterable>
              <el-option v-for="item in majorlist1" :key="item.zyh" :label="item.zym" :value="item.zyh">
                <span style="float: left">{{ item.zym }}</span>
                <span style="float: right;  font-size: 13px">
            <span v-if="item.zt === '1'" style="color: #409eff;font-weight: bold">在招</span>
            <span v-if="item.zt === '0'" style="color: #8492a6;">停招</span>
          </span>
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="年级" prop="nj">
            <el-select v-model="dataForm.nj" placeholder="请选择年级" clearable>
              <el-option v-for="i in (new Date().getFullYear()-2000)"
                         :key="(new Date().getFullYear()+1-i).toString()"
                         :label="(new Date().getFullYear()+1-i).toString()"
                         :value="(new Date().getFullYear()+1-i).toString()"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="班级号" prop="bjh">
            <el-input v-model="dataForm.bjh" placeholder="请输入班级号"></el-input>
          </el-form-item>
          <el-form-item label="班级名" prop="bm">
            <el-input v-model="dataForm.bm" placeholder="请输入班级名"></el-input>
          </el-form-item>
          <el-form-item label="毕业" prop="by">
            <el-select v-model="dataForm.by" placeholder="是否毕业" clearable>
              <el-option key="1" label="已毕业" value="1"></el-option>
              <el-option key="0" label="未毕业" value="0"></el-option>
            </el-select>          </el-form-item>
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
        "xyh": "",
        "xym": "",
        "zyh": "",
        "nj": "",
        "bjh": "",
        "bm": "",
        "zym": "",
        "by": "0",
      },
      myHeaders: {token: JSON.parse(localStorage.getItem("UserInfo")).token},
      selectedIds: [],
      delButtonDisable: true,
      dialogSetting: {"title": "新增", "show": false},
      dataForm: {
        "xyh": "",
        "zyh": "",
        "nj": "",
        "bjh": "",
        "bm": "",
        "by": "",
      },
      formRules: {
        xyh: [
          {required: true, message: '请输入学院号', trigger: 'blur'}
        ],
        zyh: [
          {required: true, message: '请输入专业号', trigger: 'blur'}
        ],
        nj: [
          {required: true, message: '请输入年级', trigger: 'blur'}
        ],
        bjh: [
          {required: true, message: '请输入班级号', trigger: 'blur'}
        ],
        bm: [
          {required: true, message: '请输入班级名', trigger: 'blur'}
        ],
        by: [
          {required: true, message: '请输入毕业', trigger: 'blur'}
        ],
      },
      department:[],
      majorlist:[],
      majorlist1:[],
    }
  },
  created() {
    this.confirmMenu()
  },
  methods: {
    load() {
      //创建页面时运行
      this.$request.post("/stuClass/pageSelect?pageNum=" + this.pageNum + "&pageSize=" + this.pageSize + "", this.searchForm)
          .then(res => {
            if (res.code === '200') {
              //console.log(res.data)
              this.tableData = res.data.list
              this.pageNum = res.data.pageNum
              this.total = res.data.total
              this.pageSize = res.data.pageSize
              this.getDepartment()
              this.getMajor()
            }
          })
    },
    getDepartment(){
      this.$request.get("/department/selectCollege").then(res => {
        if (res.code === '200') {
          this.department = res.data
        } else this.$notify.error(res.msg)
      })
    },
    getMajor(){
      if (this.searchForm.xyh === "") {
        this.$request.get("/stuMajor/selectAll").then(res => {
          if (res.code === '200') {
            this.majorlist = res.data
          } else this.$notify.error(res.msg)
        })
      } else {
        this.$request.get("/stuMajor/selectByCollege/" + this.searchForm.xyh).then(res => {
          if (res.code === '200') {
            this.majorlist = res.data
          } else this.$notify.error(res.msg)
        })
      }
    },
    getMajorForDataForm(){
      if (this.dataForm.xyh === "") {
        this.$request.get("/stuMajor/selectAll").then(res => {
          if (res.code === '200') {
            this.majorlist1 = res.data
          } else this.$notify.error(res.msg)
        })
      } else {
        this.$request.get("/stuMajor/selectByCollege/" + this.dataForm.xyh).then(res => {
          if (res.code === '200') {
            this.majorlist1 = res.data
          } else this.$notify.error(res.msg)
        })
      }
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
    exportData() {
      this.$request.post("/stuClass/export", this.searchForm).then(res => {
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
      this.$request.get("/stuClass/selectById/" + id).then(res => {
        if (res.code === '200') {
          this.dataForm = {
            "xyh": "",
            "zyh": "",
            "nj": "",
            "bjh": "",
            "bm": "",
            "by": "",
          }
          this.dataForm = res.data
          this.getMajorForDataForm()
          this.dialogSetting.title = "修改"
          this.dialogSetting.show = true
        } else {
          this.$notify.error({title: res.msg})
        }
      })
    },
    add() {
      this.dataForm = {
        "xyh": "",
        "zyh": "",
        "nj": "",
        "bjh": "",
        "bm": "",
        "by": "",
      }
      this.getMajorForDataForm()
      this.dialogSetting.title = "新增"
      this.dialogSetting.show = true
    },
    save(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          if (this.dialogSetting.title === "新增") {
            this.$request.post("/stuClass/add", this.dataForm).then(res => {
              if (res.code === '200') {
                this.$notify.success({title: res.data})
                this.dialogSetting.show = false
                this.load()
              } else {
                this.$notify.error({title: res.msg, message: res.data})
              }
            })
          } else {
            this.$request.put("/stuClass/update", this.dataForm).then(res => {
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
      this.$request.delete("/stuClass/del/" + id).then(res => {
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
      this.$request.delete("/stuClass/del/batch", {data: this.selectedIds}).then(res => {
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
  margin: 0 5px;
  padding-top: 5px;
}
</style>
