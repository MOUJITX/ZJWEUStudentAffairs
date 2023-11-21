<template>
  <div>
    <div style="margin-bottom: 10px">
      <el-button style="margin-right: 10px" type="primary" @click="exportData()">导出数据</el-button>
      <el-upload :headers="myHeaders"
                 :on-progress="handleExcelImporting" :on-success="handleExcelImportSuccess"
                 :show-file-list="false"
                 accept="xlsx"
                 action="/api/qgzxDepart/import"
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
      <el-input v-model="searchForm.name" class="topInput" clearable placeholder="请输入部门名称"
                @input="load()"></el-input>
      <el-button type="primary" @click="load()">搜索</el-button>
    </div>
    <el-table :data="tableData" style="z-index: 0" @selection-change="tableSelected">
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column prop="name" label="部门名称" width="200" />
      <el-table-column prop="gwsm" label="岗位说明" width="auto" />
      <el-table-column prop="pytj" label="用工需求" width="auto" />
      <el-table-column label="录用人数">
        <el-table-column prop="qtrs" label="钱塘校区" width="100" />
        <el-table-column prop="nxrs" label="南浔校区" width="100" />
      </el-table-column>
      <el-table-column align="center" fixed="right" label="操作" width="100">
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
                <el-form-item label="部门名称" prop="name">
                  <el-input v-model="dataForm.name" placeholder="请输入部门名称"  clearable
                            :style="{width: '100%'}"></el-input>
                </el-form-item>
                <el-form-item label="开放状态" prop="detail">
                  <el-select v-model="dataForm.detail">
                    <el-option label="开放报名" value="1"></el-option>
                    <el-option label="关闭报名" value="0"></el-option>
                  </el-select>
                </el-form-item>
                <el-form-item label="岗位说明" prop="gwsm">
                  <el-input v-model="dataForm.gwsm" type="textarea" placeholder="请输入岗位说明"
                            :autosize="{minRows: 4, maxRows: 4}" :style="{width: '100%'}"></el-input>
                </el-form-item>
                <el-form-item label="聘用条件" prop="pytj">
                  <el-input v-model="dataForm.pytj" type="textarea" placeholder="请输入聘用条件"
                            :autosize="{minRows: 4, maxRows: 4}" :style="{width: '100%'}"></el-input>
                </el-form-item>
                <el-form-item style="font-weight: bold" >钱塘校区</el-form-item>
                <el-form label-width="100px" :disabled="false" inline>
                  <el-form-item label="联系人" prop="qtlxr">
                    <el-input v-model="dataForm.qtlxr" placeholder="请输入联系人" clearable :style="{width: '100%'}">
                    </el-input>
                  </el-form-item>
                  <el-form-item label="联系电话" prop="qtdh">
                    <el-input v-model="dataForm.qtdh" placeholder="请输入联系电话" clearable :style="{width: '100%'}">
                    </el-input>
                  </el-form-item>
                  <el-form-item label="岗位人数" prop="qtrs">
                    <el-input-number v-model="dataForm.qtrs" placeholder="岗位人数"></el-input-number>
                  </el-form-item>
                </el-form>
                <el-form-item label="办公地址" prop="qtbgdz">
                  <el-input v-model="dataForm.qtbgdz" placeholder="请输入办公地址" clearable :style="{width: '100%'}">
                  </el-input>
                </el-form-item>
                <el-form-item style="font-weight: bold" >南浔校区</el-form-item>
                <el-form label-width="100px" :disabled="false" inline>
                  <el-form-item label="联系人" prop="nxlxr">
                    <el-input v-model="dataForm.nxlxr" placeholder="请输入联系人" clearable :style="{width: '100%'}">
                    </el-input>
                  </el-form-item>
                  <el-form-item label="联系电话" prop="nxdh">
                    <el-input v-model="dataForm.nxdh" placeholder="请输入联系电话" clearable>
                    </el-input>
                  </el-form-item>
                  <el-form-item label="岗位人数" prop="nxrs">
                    <el-input-number v-model="dataForm.nxrs" placeholder="岗位人数"></el-input-number>
                  </el-form-item>
                </el-form>
                <el-form-item label="办公地址" prop="nxbgdz">
                  <el-input v-model="dataForm.nxbgdz" placeholder="请输入办公地址" clearable>
                  </el-input>
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
        "id": "",
        "name": "",
        "number": "",
        "detail": "",
        "qtlxr": "",
        "qtdh": "",
        "qtbgdz": "",
        "qtrs": "",
        "nxlxr": "",
        "nxdh": "",
        "nxbgdz": "",
        "nxrs": "",
        "gwsm": "",
        "pytj": "",
      },
      myHeaders: {token: JSON.parse(localStorage.getItem("UserInfo")).token},
      selectedIds: [],
      delButtonDisable: true,
      dialogSetting: {"title": "新增", "show": false},
      dataForm: {
        "id": "",
        "name": "",
        "number": "",
        "detail": "",
        "qtlxr": "",
        "qtdh": "",
        "qtbgdz": "",
        "qtrs": "",
        "nxlxr": "",
        "nxdh": "",
        "nxbgdz": "",
        "nxrs": "",
        "gwsm": "",
        "pytj": "",
      },
      formRules: {
        id: [
          {required: true, message: '请输入id', trigger: 'blur'}
        ],
        name: [
          {required: true, message: '请输入部门名称', trigger: 'blur'}
        ],
        number: [
          {required: true, message: '请输入部门编号', trigger: 'blur'}
        ],
        detail: [
          {required: true, message: '请选择部门报名状态', trigger: 'blur'}
        ],
        qtlxr: [
          {required: false, message: '请输入钱塘校区联系人', trigger: 'blur'}
        ],
        qtdh: [
          {required: false, message: '请输入钱塘校区联系电话', trigger: 'blur'}
        ],
        qtbgdz: [
          {required: false, message: '请输入钱塘校区办公地址', trigger: 'blur'}
        ],
        qtrs: [
          {required: true, message: '请输入钱塘校区岗位人数', trigger: 'blur'}
        ],
        nxlxr: [
          {required: false, message: '请输入南浔校区联系人', trigger: 'blur'}
        ],
        nxdh: [
          {required: false, message: '请输入南浔校区联系电话', trigger: 'blur'}
        ],
        nxbgdz: [
          {required: false, message: '请输入南浔校区办公地址', trigger: 'blur'}
        ],
        nxrs: [
          {required: true, message: '请输入南浔校区岗位人数', trigger: 'blur'}
        ],
        gwsm: [
          {required: false, message: '请输入岗位说明', trigger: 'blur'}
        ],
        pytj: [
          {required: false, message: '请输入聘用条件', trigger: 'blur'}
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
      this.$request.post("/qgzxDepart/pageSelect?pageNum=" + this.pageNum + "&pageSize=" + this.pageSize + "", this.searchForm)
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
      this.$request.post("/qgzxDepart/export", this.searchForm).then(res => {
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
      this.$request.get("/qgzxDepart/selectById/" + id).then(res => {
        if (res.code === '200') {
          this.dataForm = {
            "id": "",
            "name": "",
            "number": "",
            "detail": "",
            "qtlxr": "",
            "qtdh": "",
            "qtbgdz": "",
            "qtrs": "",
            "nxlxr": "",
            "nxdh": "",
            "nxbgdz": "",
            "nxrs": "",
            "gwsm": "",
            "pytj": "",
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
        "id": "",
        "name": "",
        "number": "",
        "detail": "",
        "qtlxr": "",
        "qtdh": "",
        "qtbgdz": "",
        "qtrs": "",
        "nxlxr": "",
        "nxdh": "",
        "nxbgdz": "",
        "nxrs": "",
        "gwsm": "",
        "pytj": "",
      }
      this.dialogSetting.title = "新增"
      this.dialogSetting.show = true
    },
    save(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          if (this.dialogSetting.title === "新增") {
            this.$request.post("/qgzxDepart/add", this.dataForm).then(res => {
              if (res.code === '200') {
                this.$notify.success({title: res.data})
                this.dialogSetting.show = false
                this.load()
              } else {
                this.$notify.error({title: res.msg, message: res.data})
              }
            })
          } else {
            this.$request.put("/qgzxDepart/update", this.dataForm).then(res => {
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
      this.$request.delete("/qgzxDepart/del/" + id).then(res => {
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
      this.$request.delete("/qgzxDepart/del/batch", {data: this.selectedIds}).then(res => {
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
