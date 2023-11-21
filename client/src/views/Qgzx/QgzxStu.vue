<template>
  <div>
    <div style="margin-bottom: 10px">
      <el-button style="margin-right: 10px" @click="confirmMenu()" >刷新状态</el-button>
      <el-button style="margin-right: 10px" type="primary" @click="showWarning('未开放申请')" v-if="terminfo.dqzt === '0'">暂无申请权限：未开放申请</el-button>
      <el-button style="margin-right: 10px" type="primary" @click="showWarning('非资助认定对象')" v-if="terminfo.dqzt === '1' && stuType === '0' ">暂无申请权限：非资助认定对象</el-button>
      <el-button style="margin-right: 10px" type="primary" @click="add()" v-if="terminfo.dqzt === '1' && stuType !== '0'">新增申请</el-button>
      <el-button style="margin-right: 10px" type="primary" @click="add()" v-if="terminfo.dqzt === '2'">新增申请</el-button>
      当前学生资助认定状况：
      <el-tag v-if="stuType === '3'" >特别困难</el-tag>
      <el-tag v-if="stuType === '2'" >困难</el-tag>
      <el-tag v-if="stuType === '1'" >一般困难</el-tag>
      <el-tag v-if="stuType === '0'" >未认定</el-tag>，
      当前学年批次：<el-tag>{{terminfo.pcmc}}</el-tag>，
      面向学生群体：
      <el-tag v-if="terminfo.dqzt === '0'" type="info">未开放申请</el-tag>
      <el-tag v-if="terminfo.dqzt === '1'" type="warning">资助认定对象</el-tag>
      <el-tag v-if="terminfo.dqzt === '2'" type="success">全部学生</el-tag>
      <br>注意事项：<span v-html="terminfo.zssm"/>
    </div>
    <el-table :data="tableData" style="z-index: 0" @selection-change="tableSelected">
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column label="申请学年和批次" prop="sqxn" width="150"/>
      <el-table-column label="校区" prop="campus" width="100"/>
      <el-table-column label="姓名" prop="name" width="100"/>
      <el-table-column label="资助认定" prop="type" width="120">
        <template #default="scope">
          <el-tag v-if="scope.row.type === '3'" type="danger">特别困难</el-tag>
          <el-tag v-if="scope.row.type === '2'" type="warning">困难</el-tag>
          <el-tag v-if="scope.row.type === '1'" type="normal">一般困难</el-tag>
          <el-tag v-if="scope.row.type === ''" type="info">未认定</el-tag>
          <el-tag v-if="scope.row.type === null" type="info">未认定</el-tag>
          <el-tag v-if="scope.row.type === '0'" type="info">未认定</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="学号" prop="username" width="120"/>
      <el-table-column label="学院" prop="college" width="170"/>
      <el-table-column label="班级" prop="classname" width="120"/>
      <el-table-column label="申请岗位">
        <el-table-column label="岗位1" prop="depart" width="auto"/>
        <el-table-column label="岗位2" prop="departb" width="auto"/>
        <el-table-column label="调剂" width="70">
          <template #default="scope">
            <el-tag v-if="scope.row.choosetj === 'false'" type="danger">否</el-tag>
            <el-tag v-if="scope.row.choosetj === 'true'" type="success">是</el-tag>
          </template>
        </el-table-column>
      </el-table-column>
      <el-table-column label="审批进度" width="100">
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
          <el-button size="mini" type="text" @click="getPDF(scope.row.id)">导出申请表</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div>

      <el-dialog :title="dialogPDF.title" :visible.sync="dialogPDF.show" >
        <el-button size="mini" type="primary" @click="downPDF()">下载申请表</el-button>
        <iframe id="pdfContainer" :src="`/static/pdfjs/web/viewer.html?file=` + dialogPDF.path" width="100%" height="800px" frameborder="0" />
      </el-dialog>


      <el-dialog :title="dialogSetting.title" :visible.sync="dialogSetting.show">
        <el-form ref="dataForm" :model="dataForm" label-width="100px">
          <el-form-item label="申请人">{{ dataForm.name }}（{{ dataForm.username }}）{{ dataForm.college }}
            {{ dataForm.classname }} {{dataForm.campus}}
          </el-form-item>
        </el-form>

        <el-form ref="dataForm" :disabled="dialogSetting.dis || dataForm.speed !== 'save'" :model="dataForm" :rules="formRules" inline label-width="100px">
          <el-form-item label="申请学年" prop="sqxn">
            <el-input v-model="dataForm.sqxn" :style="{width: '100%'}" clearable placeholder="" disabled>
            </el-input>
          </el-form-item>
          <el-form-item label="资质认定" prop="type">
            <el-select v-model="dataForm.type" placeholder="" disabled>
              <el-option label="特别困难" value="3"></el-option>
              <el-option label="困难" value="2"></el-option>
              <el-option label="一般困难" value="1"></el-option>
              <el-option label="未认定" value="0"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="审批进度" prop="speed">
            <el-tag v-if="dataForm.speed === 'save'" type="info">学生暂存</el-tag>
            <el-tag v-if="dataForm.speed === 'remain'" type="normal">确认留任</el-tag>
            <el-tag v-if="dataForm.speed === 'doing'" type="warning">待审批</el-tag>
            <el-tag v-if="dataForm.speed === 'refuse'" type="danger">审批驳回</el-tag>
            <el-tag v-if="dataForm.speed === 'finish'" type="success">审批通过</el-tag>
          </el-form-item>
          <el-form-item label="联系电话" prop="phone">
            <el-input v-model="dataForm.phone" :style="{width: '100%'}" clearable placeholder="">
            </el-input>
          </el-form-item>
          <el-form-item label="QQ号" prop="qq">
            <el-input v-model="dataForm.qq" :style="{width: '100%'}" clearable placeholder="">
            </el-input>
          </el-form-item>
          <el-form-item label="勤工经历" prop="lastdepart">
            <el-select v-model="dataForm.lastdepart" placeholder="" filterable clearable multiple>
              <el-option
                  v-for="item in departFull"
                  :key="item.id"
                  :label="item.departname"
                  :value="item.departname"
              />
            </el-select>
          </el-form-item>
          <div><el-form-item style="font-weight: bold" label=" ">本学年勤工助学申请信息</el-form-item></div>
          <el-form-item label="申请岗位1" prop="depart">
            <el-select v-model="dataForm.depart" placeholder="" filterable clearable>
              <el-option v-for="item in departInfo" v-if="item.detail === '1'" :key="item.id" :label="item.name" :value="item.name">
                <span style="float: left">{{ item.name }}</span>
                <span style="float: right; color: #8492a6; font-size: 13px" v-if="dataForm.campus === '钱塘校区'">　钱塘:{{item.qtrs}}</span>
                <span style="float: right; color: #8492a6; font-size: 13px" v-if="dataForm.campus === '南浔校区'">　南浔:{{item.nxrs}}</span>
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="申请岗位2" prop="departb">
            <el-select v-model="dataForm.departb" placeholder="" filterable clearable>
              <el-option
                  v-for="item in departInfo"
                  v-if="item.detail === '1'"
                  :key="item.id"
                  :label="item.name"
                  :value="item.name">
                <span style="float: left">{{ item.name }}</span>
                <span style="float: right; color: #8492a6; font-size: 13px" v-if="dataForm.campus === '钱塘校区'">　钱塘:{{item.qtrs}}</span>
                <span style="float: right; color: #8492a6; font-size: 13px" v-if="dataForm.campus === '南浔校区'">　南浔:{{item.nxrs}}</span>
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="调剂" prop="choosetj">
            <el-select v-model="dataForm.choosetj" placeholder="">
              <el-option label="是" value="true"></el-option>
              <el-option label="否" value="false"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="可用工作时长">单位：小时，填写整数，单日工作时长最长为8小时
            <p>
              周一　
              <el-input-number v-model="dataForm.timea" :max="8" :min="0"></el-input-number>
              　周二　
              <el-input-number v-model="dataForm.timeb" :max="8" :min="0"></el-input-number>
              　周三　
              <el-input-number v-model="dataForm.timec" :max="8" :min="0"></el-input-number>
            </p>
            <p>
              周四　
              <el-input-number v-model="dataForm.timed" :max="8" :min="0"></el-input-number>
              　周五　
              <el-input-number v-model="dataForm.timee" :max="8" :min="0"></el-input-number>
              　周六　
              <el-input-number v-model="dataForm.timef" :max="8" :min="0"></el-input-number>
            </p>
            <p>
              周日　
              <el-input-number v-model="dataForm.timeg" :max="8" :min="0"></el-input-number>
            </p>
          </el-form-item>
        </el-form>
        <el-form :disabled="dialogSetting.dis || dataForm.speed !== 'save'" :model="dataForm" label-width="100px">
          <el-form-item label="工作技能" prop="skill">
            <el-input v-model="dataForm.skill" :autosize="{minRows: 4, maxRows: 4}" :style="{width: '100%'}"
                      placeholder="" type="textarea"></el-input>
          </el-form-item>
          <el-form-item label="注意事项" v-if="terminfo.pcdm === dataForm.sqxn" style="font-weight: bold" >
            <span v-html="terminfo.zssm" />
          </el-form-item>
        </el-form>
        <template #footer>
            <span class="dialog-footer">
              <el-button @click="dialogSetting.show = false">取消</el-button>
              <span v-if="!dialogSetting.dis">
                <span v-if="dataForm.speed === 'save'">
                  <el-popconfirm title="暂存申请只能自己查看，确认申请请点击“提交”" @confirm="save('dataForm', 'save')">
                      <template #reference>
                        <el-button style="margin: 0 10px" type="warning">暂存</el-button>
                      </template>
                    </el-popconfirm>
                    <el-popconfirm title="提交后将无法修改，是否提交？" @confirm="save('dataForm', 'doing')">
                      <template #reference>
                        <el-button type="primary">提交</el-button>
                      </template>
                    </el-popconfirm>
                </span>
                <span v-if="dataForm.speed === 'refuse' || dataForm.speed === 'doing' || dataForm.speed === 'remain'" >
                    <el-popconfirm title="撤回后只能自己查看，是否撤回" @confirm="save('dataForm', 'save')">
                      <template #reference>
                        <el-button style="margin: 0 10px" type="primary">撤回</el-button>
                      </template>
                    </el-popconfirm>
                </span>
              </span>
            </span>
        </template>
      </el-dialog>

    </div>
  </div>
</template>

<script>
import moment from "moment"
export default {
  data() {
    return {
      pageNum: 1, //当前页面
      pageSize: 10, //页面大小
      total: 0,  //总数
      tableData: [],
      searchForm: {
        "username": JSON.parse(localStorage.getItem("UserInfo")).username,
      },
      myHeaders: {token: JSON.parse(localStorage.getItem("UserInfo")).token},
      selectedIds: [],
      delButtonDisable: true,
      dialogPDF: {"title": "校内勤工助学岗位个人申请表（" + JSON.parse(localStorage.getItem("UserInfo")).username + "）",
        "show": false,
        "path": ""},
      dialogSetting: {"title": "新增", "show": false, "dis": true},
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
        "timea": 0,
        "timeb": 0,
        "timec": 0,
        "timed": 0,
        "timee": 0,
        "timef": 0,
        "timeg": 0,
        "uptime": "",
        "campus": "",
        "grade": "",
        "replytime": "",
        "replyname": "",
        "replynum": "",
      },
      formRules: {
        sqxn: [
          {required: true, message: '请输入申请学年', trigger: 'blur'}
        ],
        speed: [
          {required: true, message: '请输入当前进度', trigger: 'blur'}
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
          {required: true, message: '请输入联系电话', trigger: 'blur'},
          { min: 11, max: 11, message:'联系电话格式错误', trigger: 'blur'}
        ],
        qq: [
          {required: true, message: '请输入qq', trigger: 'blur'},
          { min: 5, max: 11, message:'qq号格式错误', trigger: 'blur'}
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
          {required: false, message: '请输入申请岗位2', trigger: 'blur'}
        ],
        choosetj: [
          {required: true, message: '请输入是否调剂', trigger: 'blur'}
        ],
        lastdepart: [
          {required: true, message: '请输入上学年勤工经历', trigger: 'blur'}
        ],
      },

      terminfo: [],
      departInfo:[],
      departFull:[],
      stuType:"",
    }
  },
  created() {
    this.confirmMenu()
  },
  methods: {
    load() {
      //创建页面时运行
      this.$request.get("/qgzxApply/selectByNum/" + this.searchForm.username)
          .then(res => {
            if (res.code === '200') {
              this.tableData = res.data
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
          this.getDepartInfo()
          this.getDepartFull()
        }
      })
    },
    showWarning(words){
      this.$notify.error({title: "暂无申请权限",message:words})
    },
    getStuType(){
      this.$request.get("/vStuhelper/getType?xh="+this.searchForm.username+"&xn="+this.terminfo.xmbz)
          .then(res => {
            if (res.code === '200') {
              this.stuType = res.data
              this.load()
            } else {
              this.$notify.error({title: res.msg})
            }
          })
    },
    getTerminfo() {
      this.$request.get("/qgzxTerm/selectAll")
          .then(res => {
            if (res.code === '200') {
              this.terminfo = res.data[0]
              this.dataForm.sqxn = res.data[0].pcdm
              this.getStuType()
            } else {
              this.$notify.error({title: res.msg})
            }
          })
    },
    getDepartFull(){
      this.$request.get("/department/selectAllAvailable").then(res => {
        if (res.code === '200') {
          this.departFull = res.data
        } else {
          this.$notify.error({title: res.msg})
        }
      })
    },
    getDepartInfo(){
      this.$request.get("/qgzxDepart/selectAll").then(res => {
        if (res.code === '200') {
          this.departInfo = res.data
        } else {
          this.$notify.error({title: res.msg})
        }
      })
    },
    getStuInfo(){
      this.$request.get("/stuInfo/selectByNumber/" + this.searchForm.username).then(res => {
        if (res.code === '200') {
          //console.log(res.data)
          this.dataForm.sqxn = this.terminfo.pcdm
          this.dataForm.speed = 'save'
          this.dataForm.name = res.data.name
          if (res.data.sex === '1') this.dataForm.male = '男'
          else this.dataForm.male = '女'
          this.dataForm.college = res.data.collegename
          this.dataForm.classname = res.data.classname
          this.dataForm.phone = res.data.phone
          this.dataForm.type = this.stuType
          this.dataForm.campus = res.data.campus
          this.dataForm.grade = res.data.nj
        } else {
          this.$notify.error({title: res.msg})
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
            "timea": 0,
            "timeb": 0,
            "timec": 0,
            "timed": 0,
            "timee": 0,
            "timef": 0,
            "timeg": 0,
            "uptime": "",
            "campus": "",
            "grade": "",
            "replytime": "",
            "replyname": "",
            "replynum": "",
          }
          this.dataForm = res.data
          this.dataForm.lastdepart = this.dataForm.lastdepart === null ? "" : res.data.lastdepart.split(',')
          this.dialogSetting.title = "申请详情"
          this.dialogSetting.show = true
          if (res.data.sqxn === this.terminfo.pcdm) {
            if(this.terminfo.dqzt === '2'){
              this.dialogSetting.dis = false
            } else if (this.terminfo.dqzt === '1' && this.stuType !== '0') {
              this.dialogSetting.dis = false
            } else {
              this.dialogSetting.dis = true
            }
          } else {
            this.dialogSetting.dis = true
          }
        } else {
          this.$notify.error({title: res.msg})
        }
      })
    },
    add() {
      if (this.tableData.length !== 0 && this.tableData[0].sqxn === this.terminfo.pcdm) {
        this.$notify.error({title:"新增申请失败",message:"当前批次已存在一条申请记录，每轮申请批次最多仅可存在一条记录。"})
      } else {
        this.dataForm = {
          "id": "",
          "sqxn": "",
          "speed": "",
          "name": "",
          "username": JSON.parse(localStorage.getItem("UserInfo")).username,
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
          "timea": 0,
          "timeb": 0,
          "timec": 0,
          "timed": 0,
          "timee": 0,
          "timef": 0,
          "timeg": 0,
          "uptime": "",
          "campus": "",
          "grade": "",
          "replytime": "",
          "replyname": "",
          "replynum": "",
        }
        this.getStuInfo()
        this.dialogSetting.title = "新增"
        this.dialogSetting.show = true
        this.dialogSetting.dis = false
      }
    },
    save(formName, speedType) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.dataForm.speed = speedType
          this.dataForm.lastdepart = this.dataForm.lastdepart.toString()
          this.dataForm.uptime = moment(new Date()).format("YYYY-MM-DD hh:mm:ss")
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
