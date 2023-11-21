<template>
  <div>
    <div style="margin-bottom: 10px">
      <el-button style="margin-right: 10px" type="primary" @click="exportData()">导出数据</el-button>
      <el-button style="margin-right: 10px" type="primary" @click="add()" v-show="false">新增数据</el-button>
      <el-button style="margin-right: 10px" type="success" @click="changeBatch('finish')" :disabled="changeButtonDisable">批量通过</el-button>
      <el-button style="margin-right: 10px" type="danger" @click="changeBatch('refuse')" :disabled="changeButtonDisable">批量驳回</el-button>
    </div>
    <div style="margin: 10px 0">
      <el-select v-model="searchForm.sqxn" placeholder="查看历史学年申请" filterable @change="load" style="width: 10%;" clearable>
        <el-option
            v-for="item in terminfo"
            :key="item.id"
            :label="item.pcmc"
            :value="item.pcdm"
        />
      </el-select>
      <el-select v-model="searchForm.campus" placeholder="校区" filterable style="width: 10%;margin-left: 20px" @change="load" clearable>
        <el-option label="钱塘校区" value="钱塘校区"></el-option>
        <el-option label="南浔校区" value="南浔校区"></el-option>
      </el-select>
      <el-input v-model="searchForm.grade" placeholder="请输入年级" style="width: 10%;margin-left: 20px" clearable @input="load" ></el-input>
      <el-input v-model="searchForm.username" placeholder="请输入学号" style="width: 10%;margin-left: 20px" clearable @input="load" ></el-input>
      <el-input v-model="searchForm.name" placeholder="请输入姓名" style="width: 10%;margin-left: 20px" clearable @input="load"></el-input>
      <el-select v-model="searchForm.speed" placeholder="当前审批状态" filterable style="width: 10%;margin-left: 20px" @change="load" clearable>
        <el-option label="确认留任" value="remain"></el-option>
        <el-option label="学生暂存" value="save"></el-option>
        <el-option label="待审批" value="doing"></el-option>
        <el-option label="审批通过" value="finish"></el-option>
        <el-option label="审批驳回" value="refuse"></el-option>
      </el-select>
      <el-select v-model="searchForm.type" placeholder="资助认定状态" filterable style="width: 10%;margin-left: 20px" @change="load" clearable>
        <el-option label="特别困难" value="3"></el-option>
        <el-option label="困难" value="2"></el-option>
        <el-option label="一般困难" value="1"></el-option>
        <el-option label="未认定" value="0"></el-option>
      </el-select>
      <el-button type="primary" style="margin-left: 5px" @click="load">搜索</el-button>
    </div>
    <el-table :data="tableData" style="z-index: 0" @selection-change="tableSelected">
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column prop="campus" label="校区" width="100" />
      <el-table-column prop="name" label="姓名" width="100" />
      <el-table-column prop="type" label="资助认定" width="120" >
        <template #default="scope">
          <el-tag v-if="scope.row.type === '3'" type="danger">特别困难</el-tag>
          <el-tag v-if="scope.row.type === '2'" type="warning">困难</el-tag>
          <el-tag v-if="scope.row.type === '1'" type="normal">一般困难</el-tag>
          <el-tag v-if="scope.row.type === ''" type="info">未认定</el-tag>
          <el-tag v-if="scope.row.type === null" type="info">未认定</el-tag>
          <el-tag v-if="scope.row.type === '0'" type="info">未认定</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="username" label="学号" width="120" />
      <el-table-column prop="college" label="学院" width="170" />
      <el-table-column prop="classname" label="班级" width="120" />
      <el-table-column label="申请岗位" >
        <el-table-column prop="depart" label="岗位1" width="170" />
        <el-table-column prop="departb" label="岗位2" width="170" />
        <el-table-column label="调剂"  width="70" >
          <template #default="scope">
            <el-tag v-if="scope.row.choosetj === 'false'" type="danger">否</el-tag>
            <el-tag v-if="scope.row.choosetj === 'true'" type="success">是</el-tag>
          </template>
        </el-table-column>
      </el-table-column>
      <el-table-column label="审批进度"  width="100" >
        <template #default="scope">
          <el-tag v-if="scope.row.speed === 'save'" type="info">学生暂存</el-tag>
          <el-tag v-if="scope.row.speed === 'remain'" type="normal">确认留任</el-tag>
          <el-tag v-if="scope.row.speed === 'doing'" type="warning">待审批</el-tag>
          <el-tag v-if="scope.row.speed === 'refuse'" type="danger">审批驳回</el-tag>
          <el-tag v-if="scope.row.speed === 'finish'" type="success">审批通过</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" fixed="right" label="操作" width="auto">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="handleEdit(scope.row.id)">申请查看</el-button>
          <el-button size="mini" type="text" style="color: green" @click="saveInList(scope.row.id, 'finish')" v-if="scope.row.speed !== 'save'">通过</el-button>
          <el-button size="mini" type="text" style="color: red" @click="saveInList(scope.row.id, 'refuse')" v-if="scope.row.speed !== 'save'">驳回</el-button>
          <el-button size="mini" type="text" @click="getPDF(scope.row.id)">导出申请表</el-button>
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


      <el-dialog :title="dialogPDF.title" :visible.sync="dialogPDF.show" >
        <el-button size="mini" type="primary" @click="downPDF()">下载申请表</el-button>
        <iframe id="pdfContainer" :src="`/static/pdfjs/web/viewer.html?file=` + dialogPDF.path" width="100%" height="800px" frameborder="0" />
      </el-dialog>


      <el-dialog :title="dialogSetting.title" :visible.sync="dialogSetting.show">
        <el-form label-width="100px" ref="dataForm" :model="dataForm" :rules="formRules" :disabled="true">
          <el-form-item label="申请人">{{dataForm.name}}（{{dataForm.username}}）{{dataForm.college}} {{dataForm.classname}} {{dataForm.campus}}</el-form-item>
          <el-form label-width="100px" :disabled="true" inline>
            <el-form-item label="申请学年" prop="field122">
              <el-input v-model="dataForm.sqxn" placeholder="" clearable :style="{width: '100%'}">
              </el-input>
            </el-form-item>
            <el-form-item label="资质认定" prop="field123">
              <el-select v-model="dataForm.type" placeholder="">
                <el-option label="特别困难" value="3"></el-option>
                <el-option label="困难" value="2"></el-option>
                <el-option label="一般困难" value="1"></el-option>
                <el-option label="未认定" value="0"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="审批进度" prop="field123">
              <el-tag v-if="dataForm.speed === 'save'" type="info">学生暂存</el-tag>
              <el-tag v-if="dataForm.speed === 'remain'" type="normal">确认留任</el-tag>
              <el-tag v-if="dataForm.speed === 'doing'" type="warning">待审批</el-tag>
              <el-tag v-if="dataForm.speed === 'refuse'" type="danger">审批驳回</el-tag>
              <el-tag v-if="dataForm.speed === 'finish'" type="success">审批通过</el-tag>
            </el-form-item>
          </el-form>
          <el-form label-width="100px" :disabled="true" inline style="width: 100%">
            <el-form-item label="联系电话" prop="field126">
              <el-input v-model="dataForm.phone" placeholder="" clearable :style="{width: '100%'}">
              </el-input>
            </el-form-item>
            <el-form-item label="QQ号" prop="field127">
              <el-input v-model="dataForm.qq" placeholder="" clearable :style="{width: '100%'}">
              </el-input>
            </el-form-item>
          </el-form>
          <el-form-item label="历年勤工经历" prop="field137">
            <el-input v-model="dataForm.lastdepart" placeholder="" clearable
                      :style="{width: '100%'}"></el-input>
          </el-form-item>
          <el-form-item label="工作技能" prop="field133">
            <el-input v-model="dataForm.skill" type="textarea" placeholder=""
                      :autosize="{minRows: 4, maxRows: 4}" :style="{width: '100%'}"></el-input>
          </el-form-item>
          <el-form label-width="100px" :disabled="true" inline>
            <el-form-item label="申请岗位1" prop="field134">
              <el-input v-model="dataForm.depart" placeholder="" clearable :style="{width: '100%'}">
              </el-input>
            </el-form-item>
            <el-form-item label="申请岗位2" prop="field135">
              <el-input v-model="dataForm.departb" placeholder="" clearable :style="{width: '100%'}">
              </el-input>
            </el-form-item>
            <el-form-item label="调剂" prop="field136">
              <el-select v-model="dataForm.choosetj" placeholder="" >
                <el-option label="是" value="true"></el-option>
                <el-option label="否" value="false"></el-option>
              </el-select>
            </el-form-item>
          </el-form>
          <el-form-item label="可用工作时长" >单位：小时，填写整数，单日工作时长最长为8小时
            <p>
              周一　<el-input-number v-model="dataForm.timea" :min="0" :max="8"></el-input-number>
              　周二　<el-input-number v-model="dataForm.timeb" :min="0" :max="8"></el-input-number>
              　周三　<el-input-number v-model="dataForm.timec" :min="0" :max="8"></el-input-number>
            </p>
            <p>
              周四　<el-input-number v-model="dataForm.timed" :min="0" :max="8"></el-input-number>
              　周五　<el-input-number v-model="dataForm.timee" :min="0" :max="8"></el-input-number>
              　周六　<el-input-number v-model="dataForm.timef" :min="0" :max="8"></el-input-number>
            </p>
            <p>
              周日　<el-input-number v-model="dataForm.timeg" :min="0" :max="8"></el-input-number>
            </p>
          </el-form-item>
        </el-form>
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="dialogSetting.show = false">取消</el-button>
                <el-button type="primary" @click="saveInList(dataForm.id, 'finish')">通过</el-button>
                <el-button type="danger" @click="saveInList(dataForm.id, 'refuse')">驳回</el-button>
                <el-button @click="getPDF(dataForm.id)">导出申请表</el-button>
            </span>
      </template>
      </el-dialog>

    </div>
  </div>
</template>

<script>

import moment from "moment/moment";

export default {
  data() {
    return {
      pageNum: 1, //当前页面
      pageSize: 10, //页面大小
      total: 0,  //总数
      tableData: [],
      searchForm: {
        "id": "",
        "sqxn": "",
        "speed": "",
        "name": "",
        "username": "",
        "male": "",
        "college": JSON.parse(localStorage.getItem("UserInfo")).note,
        "classname": "",
        "phone": "",
        "qq": "",
        "type": "",
        "skill": "",
        "depart": "",
        "departb": "",
        "choosetj": "",
        "lastdepart": "",
        "timea": "",
        "timeb": "",
        "timec": "",
        "timed": "",
        "timee": "",
        "timef": "",
        "timeg": "",
        "uptime": "",
        "campus": "",
        "grade": "",
        "replytime": "",
        "replyname": "",
        "replynum": "",
      },
      myHeaders: {token: JSON.parse(localStorage.getItem("UserInfo")).token},
      selectedIds: [],
      changeButtonDisable: true,
      dialogSetting: {"title": "新增", "show": false},
      dialogPDF: {"title": "校内勤工助学岗位个人申请表",
        "show": false,
        "path": ""},
      dataForm: {
        "id": "",
        "sqxn": "",
        "speed": "",
        "name": "",
        "username": "",
        "male": "",
        "college": "",
        "classname": "",
        "phone": "",
        "qq": "",
        "type": "",
        "skill": "",
        "depart": "",
        "departb": "",
        "choosetj": "",
        "lastdepart": "",
        "timea": "",
        "timeb": "",
        "timec": "",
        "timed": "",
        "timee": "",
        "timef": "",
        "timeg": "",
        "uptime": "",
        "campus": "",
        "grade": "",
        "replytime": "",
        "replyname": "",
        "replynum": "",
      },
      formRules: {
        id: [
          {required: true, message: '请输入id', trigger: 'blur'}
        ],
        sqxn: [
          {required: true, message: '请输入申请学年', trigger: 'blur'}
        ],
        speed: [
          {
            required: true,
            message: '请输入当前进度',
            trigger: 'blur'
          }
        ],
        name: [
          {required: true, message: '请输入姓名', trigger: 'blur'}
        ],
        username: [
          {required: true, message: '请输入学号', trigger: 'blur'}
        ],
        male: [
          {required: true, message: '请输入性别', trigger: 'blur'}
        ],
        college: [
          {required: true, message: '请输入学院', trigger: 'blur'}
        ],
        classname: [
          {required: true, message: '请输入班级', trigger: 'blur'}
        ],
        phone: [
          {required: true, message: '请输入联系电话', trigger: 'blur'}
        ],
        qq: [
          {required: true, message: '请输入qq', trigger: 'blur'}
        ],
        type: [
          {required: true, message: '请输入资助对象', trigger: 'blur'}
        ],
        skill: [
          {required: true, message: '请输入个人技能', trigger: 'blur'}
        ],
        depart: [
          {required: true, message: '请输入申请岗位', trigger: 'blur'}
        ],
        departb: [
          {required: true, message: '请输入申请岗位2', trigger: 'blur'}
        ],
        choosetj: [
          {required: true, message: '请输入是否调剂', trigger: 'blur'}
        ],
        lastdepart: [
          {required: true, message: '请输入上学年勤工经历', trigger: 'blur'}
        ],
        timea: [
          {required: true, message: '请输入课余时间', trigger: 'blur'}
        ],
        uptime: [
          {required: true, message: '请输入提交时间', trigger: 'blur'}
        ],
        campus: [
          {required: true, message: '请输入年级', trigger: 'blur'}
        ],
        grade: [
          {required: true, message: '请输入校区', trigger: 'blur'}
        ],
        replytime: [
          {required: true, message: '请输入审核时间', trigger: 'blur'}
        ],
        replyname: [
          {required: true, message: '请输入审核人', trigger: 'blur'}
        ],
        replynum: [
          {required: true, message: '请输入审核人工号', trigger: 'blur'}
        ],
      },

      terminfo:[],
    }
  },
  created() {
    this.confirmMenu()
  },
  methods: {
    load() {
      //创建页面时运行
      this.$request.post("/qgzxApply/pageSelect?pageNum=" + this.pageNum + "&pageSize=" + this.pageSize + "", this.searchForm)
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
          this.getTerminfo()
        }
      })
    },
    getTerminfo(){
      this.$request.get("/qgzxTerm/selectAll")
          .then(res => {
            if (res.code === '200') {
              this.terminfo = res.data
              this.searchForm.sqxn = this.terminfo[0].pcdm
              this.load()
            }
          })
    },
    exportData() {
      this.$request.post("/qgzxApply/export", this.searchForm).then(res => {
        if (res.code === '200') {
          this.$notify.success({title: res.msg})
          window.open("/api/file/export/" + res.data)
        }
      })
    },
    handleEdit(id) {
      this.$request.get("/qgzxApply/selectById/" + id).then(res => {
        if (res.code === '200') {
          this.dataForm = {
            "id": "",
            "sqxn": "",
            "speed": "",
            "name": "",
            "username": "",
            "male": "",
            "college": "",
            "classname": "",
            "phone": "",
            "qq": "",
            "type": "",
            "skill": "",
            "depart": "",
            "departb": "",
            "choosetj": "",
            "lastdepart": "",
            "timea": "",
            "uptime": "",
            "campus": "",
            "grade": "",
            "replytime": "",
            "replyname": "",
            "replynum": "",
          }
          this.dataForm = res.data
          this.dialogSetting.title = "申请详情"
          this.dialogSetting.show = true
        } else {
          this.$notify.error({title: res.msg})
        }
      })
    },
    add() {
      this.dataForm = {
        "id": "",
        "sqxn": "",
        "speed": "",
        "name": "",
        "username": "",
        "male": "",
        "college": "",
        "classname": "",
        "phone": "",
        "qq": "",
        "type": "",
        "skill": "",
        "depart": "",
        "departb": "",
        "choosetj": "",
        "lastdepart": "",
        "timea": "",
        "uptime": "",
        "campus": "",
        "grade": "",
        "replytime": "",
        "replyname": "",
        "replynum": "",
      }
      this.dialogSetting.title = "新增"
      this.dialogSetting.show = true
    },
    getPDF(id){
      this.$request.post("/qgzxApply/pdf",id).then(res => {
        if (res.code === '200') {
          //this.$notify.success({title: res.msg})
          this.dialogPDF.path = "/api/file/export/" + res.data
          this.dialogPDF.show = true
          //window.open("/api/file/export/" + res.data)
        } else {
          this.$notify.error({title:res.msg, message:res.data})
        }
      })
    },
    downPDF(){
      window.open(this.dialogPDF.path)
    },
    saveInList(formId, speedType) {
      let rtime = moment(new Date()).format("YYYY-MM-DD hh:mm:ss")
      let rname = JSON.parse(localStorage.getItem("UserInfo")).nickname
      let rnum = JSON.parse(localStorage.getItem("UserInfo")).username
      this.$request.get("/qgzxApply/setSpeed?id=" + formId + "&speed=" + speedType + "&rtime=" + rtime + "&rname=" + rname + "&rnum=" + rnum).then(res => {
        if (res.code === '200'){
          this.dialogSetting.show = false
          this.load()
        }
      })

    },
    save(formName, speedType) {
      this.dataForm.speed = speedType
      this.$refs[formName].validate((valid) => {
        if (valid) {
          if (this.dialogSetting.title === "新增") {
            this.$request.post("/qgzxApply/add", this.dataForm).then(res => {
              if (res.code === '200') {
                this.$notify.success({title: res.data})
                this.dialogSetting.show = false
                this.load()
              } else {
                this.$notify.error({title: res.msg, message: res.data})
              }
            })
          } else {
            this.$request.put("/qgzxApply/update", this.dataForm).then(res => {
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
    changeBatch(type) {
      if (!this.selectedIds.length) {
        this.$notify.error({title: "操作失败", message: "请选择需要操作的数据"})
        this.changeButtonDisable = true
        return
      }
      let rtime = moment(new Date()).format("YYYY-MM-DD hh:mm:ss")
      let rname = JSON.parse(localStorage.getItem("UserInfo")).nickname
      let rnum = JSON.parse(localStorage.getItem("UserInfo")).username
      this.$request.post("/qgzxApply/setSpeedBatch?speed=" + type + "&rtime=" + rtime + "&rname=" + rname + "&rnum=" + rnum, this.selectedIds).then(res => {
        if (res.code === '200') {
          this.$notify.success({title: res.data})
          this.load()
          this.changeButtonDisable = true
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
        this.changeButtonDisable = true
      } else {
        this.changeButtonDisable = false
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
