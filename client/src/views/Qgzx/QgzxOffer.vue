<template>
  <div>
    <div style="margin: 10px 0">
      <div>
        当前学年批次：<el-tag>{{ searchForm.pc }}</el-tag>，
        用工单位：<el-tag>{{ searchForm.bm }}</el-tag>
      </div>
      <div>
        当前学年可录用人数：【钱塘校区】<el-tag>已录{{ departNum.qtly }} / 余{{ departNum.qtsy }} / 总{{ departNum.qtrs }}</el-tag>，
        【南浔校区】<el-tag>已录{{ departNum.nxly }} / 余{{ departNum.nxsy }} / 总{{ departNum.nxrs }}</el-tag>
        （已录用人数不含“未通过审核”和“被其他部门录用”人数）
      </div>
      <span>勤工助学录用名单录入：</span>
      <el-button :disabled="importDis" style="margin-right: 10px" type="primary" @click="add()">手动添加</el-button>
      <el-upload :action="'/api/qgzxOffer/import/' + dataForm.pc + '/' + dataForm.bm"
                 :headers="myHeaders" :on-progress="handleExcelImporting"
                 :on-success="handleExcelImportSuccess"
                 :show-file-list="false"
                 accept="xlsx"
                 style="display: inline-block;margin-right: 10px">
        <el-button :disabled="importDis" type="primary">批量导入</el-button>
      </el-upload>
      <el-link href="/api/file/download/importDemo.xls" style="margin-right: 10px" target="_blank" type="primary">
        (导入模板下载)
      </el-link>
      <el-button type="primary" >扫码录入（请从手机端登录）</el-button>
    </div>
    <el-divider></el-divider>
    <div style="margin-bottom: 10px">
      <el-select v-model="searchForm.pc" class="topInput" clearable filterable placeholder="查看历史学年申请"
                 @change="load">
        <el-option
            v-for="item in terminfo"
            :key="item.id"
            :label="item.pcmc"
            :value="item.pcdm"
        />
      </el-select>
      <el-select v-model="searchForm.xq" class="topInput" clearable placeholder="请输入校区" @input="load()">
        <el-option label="钱塘校区" value="钱塘校区"></el-option>
        <el-option label="南浔校区" value="南浔校区"></el-option>
      </el-select>
      <el-input v-model="searchForm.nj" class="topInput" clearable placeholder="请输入年级" @input="load()"></el-input>
      <el-input v-model="searchForm.xy" class="topInput" clearable placeholder="请输入学院" @input="load()"></el-input>
      <el-input v-model="searchForm.bj" class="topInput" clearable placeholder="请输入班级" @input="load()"></el-input>
    </div>
    <div style="margin: 10px 0">
      <el-input v-model="searchForm.xh" class="topInput" clearable placeholder="请输入学号" @input="load()"></el-input>
      <el-input v-model="searchForm.xm" class="topInput" clearable placeholder="请输入姓名" @input="load()"></el-input>
      <el-input v-model="searchForm.bm" class="topInput" clearable placeholder="请输入录用部门" @input="load()"></el-input>
      <el-select v-model="searchForm.zt" class="topInput" clearable placeholder="请选择录用状态" @input="load()">
        <el-option label="确认录用" value="确认录用"></el-option>
        <el-option label="未通过审核" value="未通过"></el-option>
        <el-option label="已被其他部门录用" value="已被"></el-option>
      </el-select>
      <el-button type="primary" @click="load()">搜索</el-button>
      <el-button style="margin-right: 10px" type="primary" @click="exportData()">导出数据</el-button>
      <el-popconfirm style="margin-right: 10px" title="确认删除?" @confirm="delBatch()">
        <template #reference>
          <el-button :disabled="delButtonDisable" type="danger">删除选中</el-button>
        </template>
      </el-popconfirm>
    </div>
    <el-table :data="tableData" style="z-index: 0" @selection-change="tableSelected">
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column label="招聘批次" prop="pc" width="130"></el-table-column>
      <el-table-column label="校区" prop="xq" width="80"></el-table-column>
      <el-table-column label="年级" prop="nj" width="60"></el-table-column>
      <el-table-column label="学院" prop="xy" width="120"></el-table-column>
      <el-table-column label="班级" prop="bj" width="120"></el-table-column>
      <el-table-column label="学号" prop="xh" width="120"></el-table-column>
      <el-table-column label="姓名" prop="xm" width="120"></el-table-column>
      <el-table-column label="录用部门" prop="bm" width="120"></el-table-column>
      <el-table-column label="录用结果" prop="zt" width="auto"></el-table-column>
      <el-table-column label="确认时间" prop="time" width="150"></el-table-column>
      <el-table-column label="申请表id" prop="applyid" width="80"></el-table-column>
      <el-table-column align="center" fixed="right" label="操作" width="80">
        <template slot-scope="scope">
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
        <el-form ref="dataForm" :model="dataForm" label-width="100px">
          <el-form-item label="录用岗位">
            学年批次：
            <el-tag>{{ dataForm.pc }}</el-tag>
            用工单位：
            <el-tag>{{ dataForm.bm }}</el-tag>
          </el-form-item>
          <el-form-item label="录用学生">
            <el-button type="primary" @click="getStuList()">全部学生</el-button>
            <el-button type="primary" @click="getStuListByBM()">报名本部门学生</el-button>
            <el-transfer
                v-model="dataForm.xhs"
                :data="stuList"
                :props="{key: 'username', label: 'name'}"
                :titles="dialogSetting.trTitle"
                filter-placeholder="请输入学生姓名或学号"
                filterable>
            </el-transfer>
          </el-form-item>
          <el-form-item label="说明">
            <div>1.待选列表默认仅显示报名本部门学生名单。如需录用未报名本部门的学生，请先点击相应按钮切换。列表中所有学生均已通过学院审核。</div>
            <div>2.列表中所有学生均已通过学院审核。</div>
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
        "xq": "",
        "nj": "",
        "xy": "",
        "bj": "",
        "xh": "",
        "xm": "",
        "bm": "",
        "pc": "",
        "zt": "",
      },
      myHeaders: {token: JSON.parse(localStorage.getItem("UserInfo")).token},
      selectedIds: [],
      delButtonDisable: true,
      dialogSetting: {"title": "新增", "show": false,"trTitle":['报名本部门', '预录用'] },
      dataForm: {
        "xhs": [],
        "bm": "",
        "pc": "",
      },
      terminfo: [],
      stuList: [],
      importDis: '',
      departNum:'',
    }
  },
  created() {
    this.confirmMenu()
  },
  methods: {
    load() {
      this.importDis = true
      if (this.searchForm.pc !== '' && this.searchForm.bm !== '') {
        this.importDis = false
      }
      //创建页面时运行
      this.$request.post("/qgzxOffer/pageSelect?pageNum=" + this.pageNum + "&pageSize=" + this.pageSize + "", this.searchForm)
          .then(res => {
            if (res.code === '200') {
              //console.log(res.data)
              this.tableData = res.data.list
              this.pageNum = res.data.pageNum
              this.total = res.data.total
              this.pageSize = res.data.pageSize
              this.getDepartNumber()
            }
          })
    },
    getDepartNumber(){
      this.$request.get("/qgzxOffer/queryNumber?pc=" + this.searchForm.pc + "&bm=" + this.searchForm.bm)
          .then(res => {
            if (res.code === '200') {
              this.departNum = res.data
              if (this.searchForm.pc === this.terminfo[0].pcdm) this.overMax()
            } else {
              this.$notify.error({title: res.msg})
            }
          })
    },
    overMax(){
      let msg1 = "【钱塘校区】申报数量【" + this.departNum.qtrs +"】人，录用成功【" + this.departNum.qtly +"】人，已超【" + -this.departNum.qtsy +"】人！"
      let msg2 = "【南浔校区】申报数量【" + this.departNum.nxrs +"】人，录用成功【" + this.departNum.nxly +"】人，已超【" + -this.departNum.nxsy +"】人！"
      if (this.departNum.qtsy < 0 &&  this.departNum.nxsy < 0){
        this.$alert(msg1 + "\n" + msg2,"【钱塘校区】【南浔校区】超限警报", {
          confirmButtonText: '确定'
        });
      } else if (this.departNum.nxsy < 0){
        this.$alert(msg2,"【南浔校区】超限警报", {
          confirmButtonText: '确定'
        });
      } else if (this.departNum.qtsy < 0){
        this.$alert(msg1,"【钱塘校区】超限警报", {
          confirmButtonText: '确定'
        });
      }
    },
    confirmMenu() {
      this.$request.post('/menu/ConfirmAccess', this.$route.path).then(res => {
        if (res.code === '403') {
          this.$notify.error({title: res.code, message: "当前页面暂无访问权限"})
          this.$router.push("/noPermission")
        } else {
          this.getTerminfo()
        }
      })
    },
    getTerminfo() {
      this.$request.get("/qgzxTerm/selectAll")
          .then(res => {
            if (res.code === '200') {
              this.terminfo = res.data
              this.searchForm.pc = res.data[0].pcdm
              this.dataForm.pc = res.data[0].pcdm
              this.load()
            } else {
              this.$notify.error({title: res.msg})
            }
          })
    },
    exportData() {
      this.$request.post("/qgzxOffer/export", this.searchForm).then(res => {
        if (res.code === '200') {
          this.$notify.success({title: res.msg})
          window.open("/api/file/export/" + res.data)
        }
      })
    },
    handleExcelImportSuccess(res) {
      //console.log(res)
      if (res.code === '200') {
        this.$alert(res.data,res.msg, {
          confirmButtonText: '确定'
        });
      }
      this.load()
    },
    handleExcelImporting() {
      this.$notify.warning("导入中……")
      this.load()
    },
    add() {
      this.dataForm = {
        "xhs": [],
        "bm": this.searchForm.bm,
        "pc": this.searchForm.pc,
      }
      this.getStuListByBM()
      this.dialogSetting.title = "新增"
      this.dialogSetting.show = true
    },
    getStuList() {
      this.dialogSetting.trTitle = ['全部学生', '预录用']
      this.$request.get("/qgzxApply/selectFinish/" + this.searchForm.pc).then(res => {
        if (res.code === '200') {
          this.stuList = res.data
          this.stuList.forEach((item) => {
            item.name = item.name + " (" + item.username + ")"
          })
          this.dataForm.xhs = []
        } else this.$notify.error(res.msg)
      })
    },
    getStuListByBM() {
      this.dialogSetting.trTitle = ['报名本部门', '预录用']
      this.$request.get("/qgzxApply/selectFinishByBM?pcdm=" + this.searchForm.pc + "&bm=" + this.searchForm.bm).then(res => {
        if (res.code === '200') {
          this.stuList = res.data
          this.stuList.forEach((item) => {
            item.name = item.name + " (" + item.username + ")"
          })
          this.dataForm.xhs = []
        } else this.$notify.error(res.msg)
      })
    },
    save(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.$request.post("/qgzxOffer/insertBatch?xn=" + this.dataForm.pc + "&bm=" + this.dataForm.bm, this.dataForm.xhs).then(res => {
            if (res.code === '200') {
              this.$alert(res.data,res.msg, {
                confirmButtonText: '确定'
              });
              this.dialogSetting.show = false
              this.load()
            } else {
              this.$notify.error({title: res.msg, message: res.data})
            }
          })
        } else {
          //console.log('error submit!!');
          return false;
        }
      });
    },
    del(id) {
      this.$request.delete("/qgzxOffer/del/" + id).then(res => {
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
      this.$request.delete("/qgzxOffer/del/batch", {data: this.selectedIds}).then(res => {
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
