<template>
  <div>
    <div style="margin-bottom: 10px">
      <el-button style="margin-right: 10px" type="primary" @click="exportData()">导出数据</el-button>
      <el-upload :headers="myHeaders"
                 :on-progress="handleExcelImporting" :on-success="handleExcelImportSuccess"
                 :show-file-list="false"
                 accept="xlsx"
                 action="/api/stuInfo/import"
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
      <div>
        <span>学院班级：</span>
        <el-select v-model="searchForm.college" placeholder="请选择学院" class="topInput" clearable @change="loadList(1)" filterable>
          <el-option v-for="item in deparmentList" :key="item.departid" :label="item.departname" :value="item.departid"></el-option>
        </el-select>
        <el-select v-model="searchForm.major" placeholder="请选择专业" class="topInput" clearable @change="loadList(2)" filterable>
          <el-option v-for="item in majorList" :key="item.zyh" :label="item.zym" :value="item.zyh"></el-option>
        </el-select>
        <el-select v-model="searchForm.classnum" placeholder="请选择班级" class="topInput" clearable @change="load" filterable>
          <el-option v-for="item in classList" :key="item.bjh" :label="item.bm" :value="item.bjh"></el-option>
        </el-select>
      </div>
      <div>
        <span>学生信息：</span>
        <el-input v-model="searchForm.number" class="topInput" clearable placeholder="请输入学号"
                  @input="load()"></el-input>
        <el-input v-model="searchForm.name" class="topInput" clearable placeholder="请输入姓名"
                  @input="load()"></el-input>
        <el-select v-model="searchForm.sex" placeholder="请选择性别" class="topInput" clearable @change="load">
          <el-option key="1" label="男" value="1"></el-option>
          <el-option key="0" label="女" value="0"></el-option>
        </el-select>
        <el-input v-model="searchForm.phone" class="topInput" clearable placeholder="请输入联系电话"
                  @input="load()"></el-input>
        <el-input v-model="searchForm.idcard" class="topInput" clearable placeholder="请输入身份证号"
                  @input="load()"></el-input>
        <el-select v-model="searchForm.campus" placeholder="请选择校区" class="topInput" clearable @change="load">
          <el-option key="钱塘校区" label="钱塘校区" value="钱塘校区"></el-option>
          <el-option key="南浔校区" label="南浔校区" value="南浔校区"></el-option>
        </el-select>
      </div>
      <el-button type="primary" @click="load()">搜索</el-button>
    </div>
    <el-table :data="tableData" style="z-index: 0" @selection-change="tableSelected">
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column label="学院" prop="collegename" width="auto"></el-table-column>
      <el-table-column label="专业" prop="majorname" width="auto"></el-table-column>
      <el-table-column label="班级号" prop="classname" width="120"></el-table-column>
      <el-table-column label="学号" prop="number" width="120"></el-table-column>
      <el-table-column label="姓名" prop="name" width="120"></el-table-column>
      <el-table-column label="性别" prop="sex" width="120">
        <template #default="scope">
          <el-tag v-if="scope.row.sex === '1'">男</el-tag>
          <el-tag v-if="scope.row.sex === '0'">女</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="联系电话" prop="phone" width="120"></el-table-column>
      <el-table-column label="身份证号" prop="idcard" width="auto"></el-table-column>
      <el-table-column label="校区" prop="campus" width="80"></el-table-column>
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
          <el-form-item label="所在班级" prop="college">
            <el-select v-model="dataForm.college" placeholder="请选择学院" clearable @change="loadList(11)" filterable style="padding-right: 10px">
              <el-option v-for="item in deparmentListInForm" :key="item.departid" :label="item.departname" :value="item.departid"></el-option>
            </el-select>
            <el-select v-model="dataForm.major" placeholder="请选择专业" clearable @change="loadList(12)" filterable style="padding-right: 10px">
              <el-option v-for="item in majorListInForm" :key="item.zyh" :label="item.zym" :value="item.zyh"></el-option>
            </el-select>
            <el-select v-model="dataForm.classnum" placeholder="请选择班级" clearable filterable style="padding-right: 10px">
              <el-option v-for="item in classListInForm" :key="item.bjh" :label="item.bm" :value="item.bjh"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="学工号" prop="number">
            <el-input v-model="dataForm.number" placeholder="请输入学工号"></el-input>
          </el-form-item>
          <el-form-item label="姓名" prop="name">
            <el-input v-model="dataForm.name" placeholder="请输入姓名"></el-input>
          </el-form-item>
          <el-form-item label="性别" prop="sex">
            <el-select v-model="dataForm.sex" placeholder="请选择性别" clearable >
              <el-option key="1" label="男" value="1"></el-option>
              <el-option key="0" label="女" value="0"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="联系电话" prop="phone">
            <el-input v-model="dataForm.phone" placeholder="请输入联系电话"></el-input>
          </el-form-item>
          <el-form-item label="身份证号" prop="idcard">
            <el-input v-model="dataForm.idcard" placeholder="请输入身份证号"></el-input>
          </el-form-item>
          <el-form-item label="校区" prop="campus">
            <el-select v-model="dataForm.campus" placeholder="请选择校区" clearable >
              <el-option key="钱塘校区" label="钱塘校区" value="钱塘校区"></el-option>
              <el-option key="南浔校区" label="南浔校区" value="南浔校区"></el-option>
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
        "number": "",
        "name": "",
        "sex": "",
        "phone": "",
        "idcard": "",
        "classnum": "",
        "classname": "",
        "major": "",
        "majorname": "",
        "college": "",
        "collegename": "",
        "campus": "",
      },
      myHeaders: {token: JSON.parse(localStorage.getItem("UserInfo")).token},
      selectedIds: [],
      delButtonDisable: true,
      dialogSetting: {"title": "新增", "show": false},
      dataForm: {
        "number": "",
        "name": "",
        "sex": "",
        "phone": "",
        "idcard": "",
        "classnum": "",
        "major": "",
        "campus": "",
        "college": "",
      },
      formRules: {
        number: [
          {required: true, message: '请输入学工号', trigger: 'blur'}
        ],
        name: [
          {required: true, message: '请输入姓名', trigger: 'blur'}
        ],
        sex: [
          {required: false, message: '请输入性别', trigger: 'blur'}
        ],
        phone: [
          {required: false, message: '请输入联系电话', trigger: 'blur'}
        ],
        idcard: [
          {required: false, message: '请输入身份证号', trigger: 'blur'}
        ],
        classnum: [
          {required: false, message: '请输入班级号', trigger: 'blur'}
        ],
        major: [
          {required: false, message: '请输入专业号', trigger: 'blur'}
        ],
        campus: [
          {required: false, message: '请输入校区', trigger: 'blur'}
        ],
        college: [
          {required: false, message: '请输入学院号', trigger: 'blur'}
        ],
      },
      deparmentList:[],
      majorList:[],
      classList:[],
      deparmentListInForm:[],
      majorListInForm:[],
      classListInForm:[],
    }
  },
  created() {
    this.confirmMenu()
  },
  methods: {
    load() {
      //创建页面时运行
      this.$request.post("/stuInfo/pageSelect?pageNum=" + this.pageNum + "&pageSize=" + this.pageSize + "", this.searchForm)
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
    loadList(typeid){
      if (typeid === 0) {this.getDepartment(0);this.getMajor("00",0);this.getClass("","",0);}
      else if (typeid === 1) {this.getMajor(this.searchForm.college,0);this.getClass(this.searchForm.college,"",0);this.load()}
      else if (typeid === 2) {this.getClass(this.searchForm.college,this.searchForm.major,0);this.load()}
      else if (typeid === 10) {this.getDepartment(1);this.getMajor("00",1);this.getClass("","",1);}
      else if (typeid === 11) {this.getMajor(this.dataForm.college,1);this.getClass(this.dataForm.college,"",1)}
      else if (typeid === 12) this.getClass(this.dataForm.college,this.dataForm.major,1)
    },
    getDepartment(place){
      this.$request.get("/department/selectCollege").then(res => {
        if (res.code === '200') {
          if (place === 0) this.deparmentList = res.data
          else this.deparmentListInForm = res.data
        } else this.$notify.error(res.msg)
      })
    },
    getMajor(departID,place){
      if (departID === "00" || departID === "") {
        this.$request.get("/stuMajor/selectAll").then(res => {
          if (res.code === '200') {
            if (place === 0) this.majorList = res.data
            else this.majorListInForm = res.data
          } else this.$notify.error(res.msg)
        })
      } else {
        this.$request.get("/stuMajor/selectByCollege/" + departID).then(res => {
          if (res.code === '200') {
            if (place === 0) this.majorList = res.data
            else this.majorListInForm = res.data
          } else this.$notify.error(res.msg)
        })
      }
    },
    getClass(departID,majorID,place){
      this.$request.get("/stuClass/selectByCM?collegeNum=" + departID + "&majorNum=" + majorID).then(res => {
        if (res.code === '200'){
          if (place === 0) this.classList = res.data
          else this.classListInForm = res.data
        } else this.$notify.error(res.msg)
      })
    },
    confirmMenu() {
      this.$request.post('/menu/ConfirmAccess', this.$route.path).then(res => {
        if (res.code === '403') {
          this.$notify.error({title: res.code, message: "当前页面暂无访问权限"})
          this.$router.push("/noPermission")
        } else {
          this.load()
          this.loadList(0)
        }
      })
    },
    exportData() {
      this.$request.post("/stuInfo/export", this.searchForm).then(res => {
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
      this.$request.get("/stuInfo/selectById/" + id).then(res => {
        if (res.code === '200') {
          this.dataForm = {
            "number": "",
            "name": "",
            "sex": "",
            "phone": "",
            "idcard": "",
            "classnum": "",
            "major": "",
            "campus": "",
            "college": "",
          }
          this.dataForm = res.data
          this.loadList(10)
          this.dialogSetting.title = "修改"
          this.dialogSetting.show = true
        } else {
          this.$notify.error({title: res.msg})
        }
      })
    },
    add() {
      this.dataForm = {
        "number": "",
        "name": "",
        "sex": "",
        "phone": "",
        "idcard": "",
        "classnum": "",
        "major": "",
        "campus": "",
        "college": "",
      }
      this.loadList(10)
      this.dialogSetting.title = "新增"
      this.dialogSetting.show = true
    },
    save(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          if (this.dialogSetting.title === "新增") {
            this.$request.post("/stuInfo/add", this.dataForm).then(res => {
              if (res.code === '200') {
                this.$notify.success({title: res.data})
                this.dialogSetting.show = false
                this.load()
              } else {
                this.$notify.error({title: res.msg, message: res.data})
              }
            })
          } else {
            this.$request.put("/stuInfo/update", this.dataForm).then(res => {
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
      this.$request.delete("/stuInfo/del/" + id).then(res => {
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
      this.$request.delete("/stuInfo/del/batch", {data: this.selectedIds}).then(res => {
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
