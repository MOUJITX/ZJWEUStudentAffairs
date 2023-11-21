<template>
  <div>
    <div style="margin-bottom: 10px">
      <el-button style="margin-right: 10px" type="primary" @click="exportData()">导出数据</el-button>
      <el-upload :headers="myHeaders"
                 :on-progress="handleExcelImporting" :on-success="handleExcelImportSuccess"
                 :show-file-list="false"
                 accept="xlsx"
                 action="/api/users/import"
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
      <el-input v-model="searchForm.nickname" class="topInput" clearable placeholder="请输入昵称"
                @input="load()"></el-input>
      <el-input v-model="searchForm.username" class="topInput" clearable placeholder="请输入账号"
                @input="load()"></el-input>
      <el-input v-model="searchForm.password" class="topInput" clearable placeholder="请输入密码"
                @input="load()"></el-input>
      <el-input v-model="searchForm.note" class="topInput" clearable placeholder="请输入说明"
                @input="load()"></el-input>
      <el-select v-model="searchForm.usergroup" placeholder="请选择用户组" @change="load()" class="topInput" clearable>
        <el-option
            v-for="item in groupList"
            :key="item"
            :label="item.name"
            :value="item.id">
        </el-option>
      </el-select>
      <el-button type="primary" @click="load()">搜索</el-button>
    </div>
    <el-table :data="tableData" style="z-index: 0" @selection-change="tableSelected">
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column label="昵称" prop="nickname" width="120"></el-table-column>
      <el-table-column label="账号" prop="username" width="120"></el-table-column>
      <el-table-column label="密码" prop="password" width="120"></el-table-column>
      <el-table-column label="说明" prop="note" width="120"></el-table-column>
      <el-table-column label="用户组" width="120">
        <template slot-scope="scope">
          {{ getGroupLabel(scope.row.usergroup) }}
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
          <el-form-item label="昵称" prop="nickname">
            <el-input v-model="dataForm.nickname" placeholder="请输入昵称"></el-input>
          </el-form-item>
          <el-form-item label="账号" prop="username">
            <el-input v-model="dataForm.username" placeholder="请输入账号"></el-input>
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="dataForm.password" placeholder="请输入密码"></el-input>
          </el-form-item>
          <el-form-item label="说明" prop="note">
            <el-input v-model="dataForm.note" placeholder="请输入说明"></el-input>
          </el-form-item>
          <el-form-item label="用户组" prop="usergroup">
            <el-select v-model="dataForm.usergroup" placeholder="请选择用户组名称" clearable >
              <el-option
                  v-for="item in groupList"
                  :key="item"
                  :label="item.name"
                  :value="item.id">
              </el-option>
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
        "nickname": "",
        "username": "",
        "password": "",
        "note": "",
        "usergroup": "",
      },
      myHeaders: {token: JSON.parse(localStorage.getItem("UserInfo")).token},
      selectedIds: [],
      delButtonDisable: true,
      dialogSetting: {"title": "新增", "show": false},
      dataForm: {
        "nickname": "",
        "username": "",
        "password": "",
        "note": "",
        "usergroup": "",
      },
      formRules: {
        nickname: [
          {required: true, message: '请输入昵称', trigger: 'blur'}
        ],
        username: [
          {required: true, message: '请输入账号', trigger: 'blur'}
        ],
        password: [
          {required: true, message: '请输入密码', trigger: 'blur'}
        ],
        note: [
          {required: true, message: '请输入说明', trigger: 'blur'}
        ],
        usergroup: [
          {required: true, message: '请输入用户组', trigger: 'blur'}
        ],
      },
      groupList:[]
    }
  },
  created() {
    this.confirmMenu()
  },
  methods: {
    load() {
      //创建页面时运行
      this.$request.post("/users/pageSelect?pageNum=" + this.pageNum + "&pageSize=" + this.pageSize + "", this.searchForm)
          .then(res => {
            if (res.code === '200') {
              //console.log(res.data)
              this.tableData = res.data.list
              this.pageNum = res.data.pageNum
              this.total = res.data.total
              this.pageSize = res.data.pageSize
            }
          })
      this.getGroupList()
    },
    confirmMenu(){
      this.$request.post('/menu/ConfirmAccess',this.$route.path).then(res => {
        if (res.code === '403') {
          this.$notify.error({title:res.code,message:"当前页面暂无访问权限"})
          this.$router.push("/noPermission")
        } else {
          this.load()
        }
      })
    },
    getGroupList(){
      this.$request.get("/userGroup/selectAll").then(res =>{
        if (res.code === '200') {
          res.data.map((item) => {
            item.id = String(item.id)
          })
          this.groupList = res.data
        } else {
          this.$notify.error({title:res.msg})
        }
      })
    },
    getGroupLabel(id){
      let returnTitle = ""
      this.groupList.forEach((item) => {
        if (id === item.id) returnTitle = item.name
      })
      return returnTitle
    },
    exportData() {
      this.$request.post("/users/export", this.searchForm).then(res => {
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
      this.$request.get("/users/selectById/" + id).then(res => {
        if (res.code === '200') {
          this.dataForm = {
            "nickname": "",
            "username": "",
            "password": "",
            "note": "",
            "usergroup": "",
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
        "nickname": "",
        "username": "",
        "password": "",
        "note": "",
        "usergroup": "",
      }
      this.dialogSetting.title = "新增"
      this.dialogSetting.show = true
    },
    save(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          if (this.dialogSetting.title === "新增") {
            this.$request.post("/users/add", this.dataForm).then(res => {
              if (res.code === '200') {
                this.$notify.success({title: res.data})
                this.dialogSetting.show = false
                this.load()
              } else {
                this.$notify.error({title: res.msg, message: res.data})
              }
            })
          } else {
            this.$request.put("/users/update", this.dataForm).then(res => {
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
      this.$request.delete("/users/del/" + id).then(res => {
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
      this.$request.delete("/users/del/batch", {data: this.selectedIds}).then(res => {
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
