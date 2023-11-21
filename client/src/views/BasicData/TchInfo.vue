<template>
  <div>
    <div style="margin-bottom: 10px">
      <el-button style="margin-right: 10px" type="primary" @click="exportData()">导出数据</el-button>
      <el-upload :headers="myHeaders"
                 :on-progress="handleExcelImporting" :on-success="handleExcelImportSuccess"
                 :show-file-list="false"
                 accept="xlsx"
                 action="/api/tchInfo/import"
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
      <el-input v-model="searchForm.number" class="topInput" clearable placeholder="请输入工号"
                @input="load()"></el-input>
      <el-input v-model="searchForm.name" class="topInput" clearable placeholder="请输入姓名"
                @input="load()"></el-input>
      <el-select v-model="searchForm.sex" placeholder="请选择性别" class="topInput" clearable @change="load">
        <el-option key="1" label="男" value="1"></el-option>
        <el-option key="0" label="女" value="0"></el-option>
      </el-select>
      <el-input v-model="searchForm.phone" class="topInput" clearable placeholder="请输入联系电话"
                @input="load()"></el-input>
      <el-select v-model="searchForm.department" placeholder="请选择学院" class="topInput" clearable @change="load" filterable>
        <el-option v-for="item in department" :key="item.departid" :label="item.departname" :value="item.departid"></el-option>
      </el-select>
      <el-input v-model="searchForm.avatar" class="topInput" clearable placeholder="请输入照片"
                @input="load()"></el-input>
      <el-button type="primary" @click="load()">搜索</el-button>
    </div>
    <el-table :data="tableData" style="z-index: 0" @selection-change="tableSelected">
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column label="工号" prop="number" width="100"></el-table-column>
      <el-table-column label="姓名" prop="name" width="150"></el-table-column>
      <el-table-column label="性别" prop="sex" width="70">
        <template #default="scope">
          <el-tag v-if="scope.row.sex === '1'">男</el-tag>
          <el-tag v-if="scope.row.sex === '0'">女</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="联系电话" prop="phone" width="120"></el-table-column>
      <el-table-column label="学院名称" prop="departname" width="auto"></el-table-column>
      <el-table-column label="照片" prop="avatar" width="120"></el-table-column>
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
          <el-form-item label="工号" prop="number">
            <el-input v-model="dataForm.number" placeholder="请输入工号"></el-input>
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
          <el-form-item label="学院" prop="department">
            <el-select v-model="dataForm.department" placeholder="请选择学院" clearable filterable>
              <el-option v-for="item in department" :key="item.departid" :label="item.departname" :value="item.departid"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="照片" prop="avatar">
            <el-input v-model="dataForm.avatar" placeholder="请输入照片"></el-input>
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
        "department": "",
        "departname": "",
        "avatar": "",
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
        "department": "",
        "avatar": "",
      },
      formRules: {
        number: [
          {required: true, message: '请输入工号', trigger: 'blur'}
        ],
        name: [
          {required: true, message: '请输入姓名', trigger: 'blur'}
        ],
        sex: [
          {required: true, message: '请输入性别', trigger: 'blur'}
        ],
        phone: [
          {required: true, message: '请输入联系电话', trigger: 'blur'}
        ],
        department: [
          {required: true, message: '请输入学院', trigger: 'blur'}
        ],
        departname: [
          {required: true, message: '请输入学院名称', trigger: 'blur'}
        ],
        avatar: [
          {required: true, message: '请输入照片', trigger: 'blur'}
        ],
      },
      department:[],
    }
  },
  created() {
    this.confirmMenu()
  },
  methods: {
    load() {
      //创建页面时运行
      this.$request.post("/tchInfo/pageSelect?pageNum=" + this.pageNum + "&pageSize=" + this.pageSize + "", this.searchForm)
          .then(res => {
            if (res.code === '200') {
              //console.log(res.data)
              this.tableData = res.data.list
              this.pageNum = res.data.pageNum
              this.total = res.data.total
              this.pageSize = res.data.pageSize
              this.getDepartment()
            }
          })
    },
    getDepartment(){
      this.$request.get("/department/selectAllAvailable").then(res => {
        if (res.code === '200') {
          this.department = res.data
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
        }
      })
    },
    exportData() {
      this.$request.post("/tchInfo/export", this.searchForm).then(res => {
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
      this.$request.get("/tchInfo/selectById/" + id).then(res => {
        if (res.code === '200') {
          this.dataForm = {
            "number": "",
            "name": "",
            "sex": "",
            "phone": "",
            "department": "",
            "avatar": "",
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
        "number": "",
        "name": "",
        "sex": "",
        "phone": "",
        "department": "",
        "avatar": "",
      }
      this.dialogSetting.title = "新增"
      this.dialogSetting.show = true
    },
    save(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          if (this.dialogSetting.title === "新增") {
            this.$request.post("/tchInfo/add", this.dataForm).then(res => {
              if (res.code === '200') {
                this.$notify.success({title: res.data})
                this.dialogSetting.show = false
                this.load()
              } else {
                this.$notify.error({title: res.msg, message: res.data})
              }
            })
          } else {
            this.$request.put("/tchInfo/update", this.dataForm).then(res => {
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
      this.$request.delete("/tchInfo/del/" + id).then(res => {
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
      this.$request.delete("/tchInfo/del/batch", {data: this.selectedIds}).then(res => {
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
