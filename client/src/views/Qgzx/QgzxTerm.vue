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
    <el-table :data="tableData" style="z-index: 0" @selection-change="tableSelected">
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column prop="pcmc" label="名称" width="150" />
      <el-table-column prop="pcdm" label="代码" width="120" />
      <el-table-column label="状态" width="100" >
        <template #default="scope">
          <el-tag v-if="scope.row.dqzt === '0'" type="info">关闭</el-tag>
          <el-tag v-if="scope.row.dqzt === '1'" type="normal">资助可填</el-tag>
          <el-tag v-if="scope.row.dqzt === '2'" type="success">全部可填</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="zssm" label="显示说明" width="auto" >
        <template #default="scope">
          <span v-html="scope.row.zssm" />
        </template>
      </el-table-column>
      <el-table-column prop="xmbz" label="绑定学年" width="200" />
      <el-table-column align="center" fixed="right" label="操作" width="120">
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
          <el-form-item label="批次名称" prop="pcmc">
            <el-input v-model="dataForm.pcmc" placeholder="请输入批次名称"></el-input>
          </el-form-item>
          <el-form-item label="批次代码" prop="pcdm">
            <el-input v-model="dataForm.pcdm" placeholder="请输入批次代码"></el-input>
          </el-form-item>
          <el-form-item label="批次状态" prop="dqzt">
            <el-select v-model="dataForm.dqzt" placeholder="请选择批次状态" >
              <el-option label="关闭" value="0"></el-option>
              <el-option label="资助可填" value="1"></el-option>
              <el-option label="全部可填" value="2"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="说明" prop="zssm">
            <el-input v-model="dataForm.zssm" placeholder="请输入说明" type="textarea" autosize></el-input>
          </el-form-item>
          <el-form-item label="绑定学年" prop="xmbz">
            <el-select v-model="dataForm.xmbz" placeholder="请绑定学年" filterable clearable>
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
        "pcmc": "",
        "pcdm": "",
        "dqzt": "",
        "zssm": "",
        "xmbz": "",
      },
      myHeaders: {token: JSON.parse(localStorage.getItem("UserInfo")).token},
      selectedIds: [],
      delButtonDisable: true,
      dialogSetting: {"title": "新增", "show": false},
      dataForm: {
        "pcmc": "",
        "pcdm": "",
        "dqzt": "",
        "zssm": "",
        "xmbz": "",
      },
      formRules: {
        pcmc: [
          {required: true, message: '请输入批次名称', trigger: 'blur'}
        ],
        pcdm: [
          {required: true, message: '请输入批次代码', trigger: 'blur'}
        ],
        dqzt: [
          {required: true, message: '请输入批次状态', trigger: 'blur'}
        ],
        zssm: [
          {required: false, message: '请输入说明', trigger: 'blur'}
        ],
        xmbz: [
          {required: true, message: '请绑定学年', trigger: 'blur'}
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
      this.$request.post("/qgzxTerm/pageSelect?pageNum=" + this.pageNum + "&pageSize=" + this.pageSize + "", this.searchForm)
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
    handleEdit(id) {
      this.$request.get("/qgzxTerm/selectById/" + id).then(res => {
        if (res.code === '200') {
          this.dataForm = {
            "pcmc": "",
            "pcdm": "",
            "dqzt": "",
            "zssm": "",
            "xmbz": "",
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
        "pcmc": "",
        "pcdm": "",
        "dqzt": "",
        "zssm": "",
        "xmbz": "",
      }
      this.dialogSetting.title = "新增"
      this.dialogSetting.show = true
    },
    save(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          if (this.dialogSetting.title === "新增") {
            this.$request.post("/qgzxTerm/add", this.dataForm).then(res => {
              if (res.code === '200') {
                this.$notify.success({title: res.data})
                this.dialogSetting.show = false
                this.load()
              } else {
                this.$notify.error({title: res.msg, message: res.data})
              }
            })
          } else {
            this.$request.put("/qgzxTerm/update", this.dataForm).then(res => {
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
      this.$request.delete("/qgzxTerm/del/" + id).then(res => {
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
      this.$request.delete("/qgzxTerm/del/batch", {data: this.selectedIds}).then(res => {
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
