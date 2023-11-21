<template>
  <div>
    <div style="margin-bottom: 10px">
      <el-button style="margin-right: 10px" type="primary" @click="exportData()">导出数据</el-button>
      <el-upload :headers="myHeaders"
                 :on-progress="handleExcelImporting" :on-success="handleExcelImportSuccess"
                 :show-file-list="false"
                 accept="xlsx"
                 action="/api/department/import"
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
      <el-input v-model="searchForm.departid" class="topInput" clearable placeholder="请输入部门编号"
                @input="load()"></el-input>
      <el-input v-model="searchForm.departname" class="topInput" clearable placeholder="请输入部门名称"
                @input="load()"></el-input>
      <el-select v-model="searchForm.type" placeholder="请选择类型" @change="load" clearable class="topInput" >
        <el-option key="管理服务机构" label="管理服务机构" value="管理服务机构"></el-option>
        <el-option key="教学、科研及教辅机构" label="教学、科研及教辅机构" value="教学、科研及教辅机构"></el-option>
        <el-option key="群团组织" label="群团组织" value="群团组织"></el-option>
        <el-option key="其他" label="其他" value="其他"></el-option>
      </el-select>
      <span class="topInput">启用状态
      <el-switch v-model="searchForm.state" @change="load" active-value="1" inactive-value="0"></el-switch></span>
      <el-select v-model="searchForm.college" placeholder="是否教学机构" @change="load" clearable class="topInput" >
        <el-option key="1" label="教学机构" value="1"></el-option>
        <el-option key="0" label="非教学机构" value="0"></el-option>
      </el-select>
      <el-button type="primary" @click="load()">搜索</el-button>
    </div>
    <el-table :data="tableData" style="z-index: 0" @selection-change="tableSelected">
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column label="部门编号" prop="departid" width="120"></el-table-column>
      <el-table-column label="部门名称" prop="departname" width="auto"></el-table-column>
      <el-table-column label="类型" prop="type" width="200"></el-table-column>
      <el-table-column label="状态" prop="state" width="120">
        <template #default="scope">
          <el-tag v-if="scope.row.state === '0'" type="danger">封存</el-tag>
          <el-tag v-if="scope.row.state === '1'" >启用</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="教学机构" prop="college" width="120">
        <template #default="scope">
          <el-tag v-if="scope.row.college === '0'" type="danger">否</el-tag>
          <el-tag v-if="scope.row.college === '1'" >是</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="显示顺序" prop="ranknum" width="120"></el-table-column>
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
          <el-form-item label="部门编号" prop="departid">
            <el-input v-model="dataForm.departid" placeholder="请输入部门编号"></el-input>
          </el-form-item>
          <el-form-item label="部门名称" prop="departname">
            <el-input v-model="dataForm.departname" placeholder="请输入部门名称"></el-input>
          </el-form-item>
          <el-form-item label="类型" prop="type">
            <el-select v-model="dataForm.type" placeholder="请选择类型" clearable>
              <el-option key="管理服务机构" label="管理服务机构" value="管理服务机构"></el-option>
              <el-option key="教学、科研及教辅机构" label="教学、科研及教辅机构" value="教学、科研及教辅机构"></el-option>
              <el-option key="群团组织" label="群团组织" value="群团组织"></el-option>
              <el-option key="其他" label="其他" value="其他"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="状态" prop="state">
            <el-switch v-model="dataForm.state" active-value="1" inactive-value="0"></el-switch>
          </el-form-item>
          <el-form-item label="教学机构" prop="college">
            <el-switch v-model="dataForm.college" active-value="1" inactive-value="0"></el-switch>
          </el-form-item>
          <el-form-item label="显示顺序" prop="ranknum">
            <el-input-number v-model="dataForm.ranknum" :min="0" :max="999" label="请输入显示顺序"></el-input-number>
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
        "departid": "",
        "departname": "",
        "type": "",
        "state": "1",
        "college": "",
      },
      myHeaders: {token: JSON.parse(localStorage.getItem("UserInfo")).token},
      selectedIds: [],
      delButtonDisable: true,
      dialogSetting: {"title": "新增", "show": false},
      dataForm: {
        "departid": "",
        "departname": "",
        "type": "",
        "state": "",
        "college": "",
        "ranknum": "",
      },
      formRules: {
        departid: [
          {required: true, message: '请输入部门编号', trigger: 'blur'}
        ],
        departname: [
          {required: true, message: '请输入部门名称', trigger: 'blur'}
        ],
        type: [
          {required: true, message: '请输入类型', trigger: 'blur'}
        ],
        state: [
          {required: true, message: '请输入状态', trigger: 'blur'}
        ],
        college: [
          {required: true, message: '请输入教学机构', trigger: 'blur'}
        ],
        ranknum: [
          {required: true, message: '请输入显示顺序', trigger: 'blur'}
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
      this.$request.post("/department/pageSelect?pageNum=" + this.pageNum + "&pageSize=" + this.pageSize + "", this.searchForm)
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
          this.load()
        }
      })
    },
    exportData() {
      this.$request.post("/department/export", this.searchForm).then(res => {
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
      this.$request.get("/department/selectById/" + id).then(res => {
        if (res.code === '200') {
          this.dataForm = {
            "departid": "",
            "departname": "",
            "type": "",
            "state": "",
            "college": "",
            "ranknum": "",
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
        "departid": "",
        "departname": "",
        "type": "",
        "state": "1",
        "college": "",
        "ranknum": "0",
      }
      this.dialogSetting.title = "新增"
      this.dialogSetting.show = true
    },
    save(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          if (this.dialogSetting.title === "新增") {
            this.$request.post("/department/add", this.dataForm).then(res => {
              if (res.code === '200') {
                this.$notify.success({title: res.data})
                this.dialogSetting.show = false
                this.load()
              } else {
                this.$notify.error({title: res.msg, message: res.data})
              }
            })
          } else {
            this.$request.put("/department/update", this.dataForm).then(res => {
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
      this.$request.delete("/department/del/" + id).then(res => {
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
      this.$request.delete("/department/del/batch", {data: this.selectedIds}).then(res => {
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
