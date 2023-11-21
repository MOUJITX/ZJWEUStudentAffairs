<template>
  <div>
    <div style="margin-bottom: 10px">
      <el-button style="margin-right: 10px" type="primary" @click="exportData()">导出数据</el-button>
      <el-upload :headers="myHeaders"
                 :on-progress="handleExcelImporting" :on-success="handleExcelImportSuccess"
                 :show-file-list="false"
                 accept="xlsx"
                 action="/api/stuMajor/import"
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
      <el-select v-model="searchForm.xyh" placeholder="请选择学院" class="topInput" clearable @change="load">
        <el-option v-for="item in department" :key="item.departid" :label="item.departname" :value="item.departid"></el-option>
      </el-select>
      <el-input v-model="searchForm.zyh" class="topInput" clearable placeholder="请输入专业号"
                @input="load()"></el-input>
      <el-input v-model="searchForm.zym" class="topInput" clearable placeholder="请输入专业名"
                @input="load()"></el-input>
      <el-select v-model="searchForm.cc" placeholder="请选择层次" class="topInput" clearable @change="load">
        <el-option key="本科" label="本科" value="本科"></el-option>
        <el-option key="专科" label="专科" value="专科"></el-option>
        <el-option key="预科" label="预科" value="预科"></el-option>
        <el-option key="成教" label="成教" value="成教"></el-option>
      </el-select>
      <el-select v-model="searchForm.xz" placeholder="请选择学制" class="topInput" clearable @change="load">
        <el-option key="4" label="四年制" value="4"></el-option>
        <el-option key="3" label="三年制" value="3"></el-option>
        <el-option key="2" label="二年制" value="2"></el-option>
        <el-option key="1" label="一年制" value="1"></el-option>
        <el-option key="0" label="0" value="0"></el-option>
      </el-select>
      <el-select v-model="searchForm.lx" placeholder="请选择类型" clearable @change="load">
        <el-option key="普通本科" label="普通本科" value="普通本科"></el-option>
        <el-option key="中本一体化" label="中本一体化" value="中本一体化"></el-option>
        <el-option key="专升本" label="专升本" value="专升本"></el-option>
        <el-option key="专升本（联合培养）" label="专升本（联合培养）" value="专升本（联合培养）"></el-option>
        <el-option key="专科" label="专科" value="专科"></el-option>
        <el-option key="预科" label="预科" value="预科"></el-option>
        <el-option key="成教" label="成教" value="成教"></el-option>
        <el-option key="第二学位" label="第二学位" value="第二学位"></el-option>
        <el-option key="留学生" label="留学生" value="留学生"></el-option>
        <el-option key="中外合作" label="中外合作" value="中外合作"></el-option>
      </el-select>
      <el-select v-model="searchForm.zt" placeholder="请选择状态" class="topInput" clearable @change="load">
        <el-option key="1" label="在招" value="1"></el-option>
        <el-option key="0" label="停招" value="0"></el-option>
      </el-select>
      <el-button type="primary" @click="load()">搜索</el-button>
    </div>
    <el-table :data="tableData" style="z-index: 0" @selection-change="tableSelected">
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column label="学院号" prop="xyh" width="60"></el-table-column>
      <el-table-column label="学院名" prop="xym" width="auto"></el-table-column>

      <el-table-column label="专业号" prop="zyh" width="70"></el-table-column>
      <el-table-column label="专业名" prop="zym" width="auto"></el-table-column>
      <el-table-column label="层次" prop="cc" width="70"></el-table-column>
      <el-table-column label="类型" prop="lx" width="100"></el-table-column>
      <el-table-column label="学制" prop="xz" width="60"></el-table-column>
      <el-table-column label="状态" prop="zt" width="70">
        <template #default="scope">
          <el-tag v-if="scope.row.zt === '0'" type="danger">停招</el-tag>
          <el-tag v-if="scope.row.zt === '1'" >在招</el-tag>
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
            <el-select v-model="dataForm.xyh" placeholder="请选择学院" clearable>
              <el-option v-for="item in department" :key="item.departid" :label="item.departname" :value="item.departid"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="专业号" prop="zyh">
            <el-input v-model="dataForm.zyh" placeholder="请输入专业号"></el-input>
          </el-form-item>
          <el-form-item label="专业名" prop="zym">
            <el-input v-model="dataForm.zym" placeholder="请输入专业名"></el-input>
          </el-form-item>
          <el-form-item label="层次" prop="cc">
            <el-select v-model="dataForm.cc" placeholder="请选择层次" clearable>
              <el-option key="本科" label="本科" value="本科"></el-option>
              <el-option key="专科" label="专科" value="专科"></el-option>
              <el-option key="预科" label="预科" value="预科"></el-option>
              <el-option key="成教" label="成教" value="成教"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="类型" prop="lx">
            <el-select v-model="dataForm.lx" placeholder="请选择类型" clearable>
              <el-option key="普通本科" label="普通本科" value="普通本科"></el-option>
              <el-option key="中本一体化" label="中本一体化" value="中本一体化"></el-option>
              <el-option key="专升本" label="专升本" value="专升本"></el-option>
              <el-option key="专升本（联合培养）" label="专升本（联合培养）" value="专升本（联合培养）"></el-option>
              <el-option key="专科" label="专科" value="专科"></el-option>
              <el-option key="预科" label="预科" value="预科"></el-option>
              <el-option key="成教" label="成教" value="成教"></el-option>
              <el-option key="第二学位" label="第二学位" value="第二学位"></el-option>
              <el-option key="留学生" label="留学生" value="留学生"></el-option>
              <el-option key="中外合作" label="中外合作" value="中外合作"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="学制" prop="xz">
            <el-select v-model="dataForm.xz" placeholder="请选择学制" clearable>
              <el-option key="4" label="四年制" value="4"></el-option>
              <el-option key="3" label="三年制" value="3"></el-option>
              <el-option key="2" label="二年制" value="2"></el-option>
              <el-option key="1" label="一年制" value="1"></el-option>
              <el-option key="0" label="0" value="0"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="状态" prop="xz">
            <el-switch v-model="dataForm.zt" active-value="1" inactive-value="0"></el-switch>
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
        "xyh": "",
        "xym": "",
        "zyh": "",
        "zym": "",
        "cc": "",
        "lx": "",
        "xz": "",
        "zt": "1",
      },
      myHeaders: {token: JSON.parse(localStorage.getItem("UserInfo")).token},
      selectedIds: [],
      delButtonDisable: true,
      dialogSetting: {"title": "新增", "show": false},
      dataForm: {
        "xyh": "",
        "zyh": "",
        "zym": "",
        "cc": "",
        "lx": "",
        "xz": "",
        "zt": "",
      },
      formRules: {
        xyh: [
          {required: true, message: '请输入学院号', trigger: 'blur'}
        ],
        zyh: [
          {required: true, message: '请输入专业号', trigger: 'blur'}
        ],
        zym: [
          {required: true, message: '请输入专业名', trigger: 'blur'}
        ],
        cc: [
          {required: true, message: '请输入层次', trigger: 'blur'}
        ],
        lx: [
          {required: false, message: '请输入类型', trigger: 'blur'}
        ],
        xz: [
          {required: true, message: '请输入学制', trigger: 'blur'}
        ],
        zt: [
          {required: true, message: '请输入状态', trigger: 'blur'}
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
      this.$request.post("/stuMajor/pageSelect?pageNum=" + this.pageNum + "&pageSize=" + this.pageSize + "", this.searchForm)
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
      this.$request.get("/department/selectCollege").then(res => {
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
      this.$request.post("/stuMajor/export", this.searchForm).then(res => {
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
      this.$request.get("/stuMajor/selectById/" + id).then(res => {
        if (res.code === '200') {
          this.dataForm = {
            "xyh": "",
            "zyh": "",
            "zym": "",
            "cc": "",
            "lx": "",
            "xz": "",
            "zt": "1",
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
        "xyh": "",
        "zyh": "",
        "zym": "",
        "cc": "",
        "lx": "",
        "xz": "",
        "zt": "",
      }
      this.dialogSetting.title = "新增"
      this.dialogSetting.show = true
    },
    save(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          if (this.dialogSetting.title === "新增") {
            this.$request.post("/stuMajor/add", this.dataForm).then(res => {
              if (res.code === '200') {
                this.$notify.success({title: res.data})
                this.dialogSetting.show = false
                this.load()
              } else {
                this.$notify.error({title: res.msg, message: res.data})
              }
            })
          } else {
            this.$request.put("/stuMajor/update", this.dataForm).then(res => {
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
      this.$request.delete("/stuMajor/del/" + id).then(res => {
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
      this.$request.delete("/stuMajor/del/batch", {data: this.selectedIds}).then(res => {
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
