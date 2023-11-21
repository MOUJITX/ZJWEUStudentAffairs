<template>
  <div>
    <div style="margin-bottom: 10px">
      <el-button style="margin-right: 10px" type="primary" @click="add()">新增数据</el-button>
      <el-popconfirm style="margin-right: 10px" title="确认删除?" @confirm="delBatch()">
        <template #reference>
          <el-button :disabled="delButtonDisable" type="danger">批量删除</el-button>
        </template>
      </el-popconfirm>
    </div>
    <!--
    <div>
      <el-input v-model="searchForm.title" class="topInput" clearable placeholder="请输入标题"
                @input="load()"></el-input>
      <el-input v-model="searchForm.top" class="topInput" clearable placeholder="请输入置顶" @input="load()"></el-input>
      <el-input v-model="searchForm.publish" class="topInput" clearable placeholder="请输入发布状态"
                @input="load()"></el-input>
      <el-input v-model="searchForm.date" class="topInput" clearable placeholder="请输入发布日期"
                @input="load()"></el-input>
      <el-input v-model="searchForm.source" class="topInput" clearable placeholder="请输入发布位置"
                @input="load()"></el-input>
      <el-input v-model="searchForm.author" class="topInput" clearable placeholder="请输入作者"
                @input="load()"></el-input>
      <el-input v-model="searchForm.detail" class="topInput" clearable placeholder="请输入内容"
                @input="load()"></el-input>
      <el-input v-model="searchForm.simple" class="topInput" clearable placeholder="请输入概要"
                @input="load()"></el-input>
      <el-input v-model="searchForm.adduser" class="topInput" clearable placeholder="请输入发布人"
                @input="load()"></el-input>
      <el-input v-model="searchForm.edituser" class="topInput" clearable placeholder="请输入最后修改人"
                @input="load()"></el-input>
      <el-button type="primary" @click="load()">搜索</el-button>
    </div>
    -->
    <el-table :data="tableData" style="z-index: 0" @selection-change="tableSelected">
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column label="标题" prop="title" width="120"></el-table-column>
      <el-table-column label="置顶" prop="top" width="120"></el-table-column>
      <el-table-column label="发布状态" prop="publish" width="120"></el-table-column>
      <el-table-column label="发布日期" prop="date" width="120"></el-table-column>
      <el-table-column label="发布位置" prop="source" width="120"></el-table-column>
      <el-table-column label="作者" prop="author" width="120"></el-table-column>
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
          <el-form-item label="标题" prop="title">
            <el-input v-model="dataForm.title" placeholder="请输入标题"></el-input>
          </el-form-item>
          <el-form-item label="置顶" prop="top">
            <el-input v-model="dataForm.top" placeholder="请输入置顶"></el-input>
          </el-form-item>
          <el-form-item label="发布状态" prop="publish">
            <el-input v-model="dataForm.publish" placeholder="请输入发布状态"></el-input>
          </el-form-item>
          <el-form-item label="发布日期" prop="date">
            <el-input v-model="dataForm.date" placeholder="请输入发布日期"></el-input>
          </el-form-item>
          <el-form-item label="发布位置" prop="source">
            <el-input v-model="dataForm.source" placeholder="请输入发布位置"></el-input>
          </el-form-item>
          <el-form-item label="作者" prop="author">
            <el-input v-model="dataForm.author" placeholder="请输入作者"></el-input>
          </el-form-item>
          <el-form-item label="内容" prop="detail">
            <el-input v-model="dataForm.detail" placeholder="请输入内容"></el-input>
          </el-form-item>
          <el-form-item label="概要" prop="simple">
            <el-input v-model="dataForm.simple" placeholder="请输入概要"></el-input>
          </el-form-item>
          <el-form-item label="发布人" prop="adduser">
            <el-input v-model="dataForm.adduser" placeholder="请输入发布人"></el-input>
          </el-form-item>
          <el-form-item label="最后修改人" prop="edituser">
            <el-input v-model="dataForm.edituser" placeholder="请输入最后修改人"></el-input>
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
        "title": "",
        "top": "",
        "publish": "",
        "date": "",
        "source": "",
        "author": "",
        "detail": "",
        "simple": "",
        "adduser": "",
        "edituser": "",
      },
      myHeaders: {token: JSON.parse(localStorage.getItem("UserInfo")).token},
      selectedIds: [],
      delButtonDisable: true,
      dialogSetting: {"title": "新增", "show": false},
      dataForm: {
        "title": "",
        "top": "",
        "publish": "",
        "date": "",
        "source": "",
        "author": "",
        "detail": "",
        "simple": "",
        "adduser": "",
        "edituser": "",
      },
      formRules: {
        title: [
          {required: true, message: '请输入标题', trigger: 'blur'}
        ],
        top: [
          {required: true, message: '请输入置顶', trigger: 'blur'}
        ],
        publish: [
          {required: true, message: '请输入发布状态', trigger: 'blur'}
        ],
        date: [
          {required: true, message: '请输入发布日期', trigger: 'blur'}
        ],
        source: [
          {required: true, message: '请输入发布位置', trigger: 'blur'}
        ],
        author: [
          {required: true, message: '请输入作者', trigger: 'blur'}
        ],
        detail: [
          {required: true, message: '请输入内容', trigger: 'blur'}
        ],
        simple: [
          {required: true, message: '请输入概要', trigger: 'blur'}
        ],
        adduser: [
          {required: true, message: '请输入发布人', trigger: 'blur'}
        ],
        edituser: [
          {required: true, message: '请输入最后修改人', trigger: 'blur'}
        ],
      }
    }
  },
  created() {
    this.confirmMenu()
  },
  methods: {
    load() {
      //创建页面时运行
      this.$request.get("/notices/selectAll")
          .then(res => {
            if (res.code === '200') {
              //console.log(res.data)
              this.tableData = res.data
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
    exportData() {
      this.$request.post("/notices/export", this.searchForm).then(res => {
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
      this.$request.get("/notices/selectById/" + id).then(res => {
        if (res.code === '200') {
          this.dataForm = {
            "title": "",
            "top": "",
            "publish": "",
            "date": "",
            "source": "",
            "author": "",
            "detail": "",
            "simple": "",
            "adduser": "",
            "edituser": "",
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
        "title": "",
        "top": "",
        "publish": "",
        "date": "",
        "source": "",
        "author": "",
        "detail": "",
        "simple": "",
        "adduser": "",
        "edituser": "",
      }
      this.dialogSetting.title = "新增"
      this.dialogSetting.show = true
    },
    save(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          if (this.dialogSetting.title === "新增") {
            this.$request.post("/notices/add", this.dataForm).then(res => {
              if (res.code === '200') {
                this.$notify.success({title: res.data})
                this.dialogSetting.show = false
                this.load()
              } else {
                this.$notify.error({title: res.msg, message: res.data})
              }
            })
          } else {
            this.$request.put("/notices/update", this.dataForm).then(res => {
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
      this.$request.delete("/notices/del/" + id).then(res => {
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
      this.$request.delete("/notices/del/batch", {data: this.selectedIds}).then(res => {
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
