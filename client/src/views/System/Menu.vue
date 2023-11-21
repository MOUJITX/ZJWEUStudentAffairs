<template>
  <div>
    <div style="margin-bottom: 10px">
      <el-button style="margin-right: 10px" type="primary" @click="exportData()">导出数据</el-button>
      <el-upload :headers="myHeaders"
                 :on-progress="handleExcelImporting" :on-success="handleExcelImportSuccess"
                 :show-file-list="false"
                 accept="xlsx"
                 action="/api/menu/import"
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
      <el-input v-model="searchForm.title" class="topInput" clearable placeholder="请输入名称"
                @input="load()"></el-input>
      <el-input v-model="searchForm.path" class="topInput" clearable placeholder="请输入路径"
                @input="load()"></el-input>
      <el-input v-model="searchForm.icon" class="topInput" clearable placeholder="请输入图标"
                @input="load()"></el-input>
      <el-select v-model="searchForm.father" placeholder="请选择父级目录" class="topInput" clearable @change="load">
        <el-option
            label="根目录"
            value="0">
        </el-option>
        <el-option
            v-for="item in fatherList"
            :key="item"
            :label="item.title"
            :value="item.id">
        </el-option>
      </el-select>
      <el-input v-model="searchForm.note" class="topInput" clearable placeholder="请输入说明"
                @input="load()"></el-input>
      <el-input v-model="searchForm.show" class="topInput" clearable placeholder="请输入是否显示"
                @input="load()"></el-input>
      <el-button type="primary" @click="load()">搜索</el-button>
    </div>

    <el-table :data="tableData" style="z-index: 0" @selection-change="tableSelected">
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column label="名称" width="120" prop="title">
      </el-table-column>
      <el-table-column label="路径" prop="path" width="120"></el-table-column>
      <el-table-column label="图标" width="120">
        <template #default="scope">
          <i :class="scope.row.icon"></i>
        </template>
      </el-table-column>
      <el-table-column label="父级目录id" width="120">
        <template slot-scope="scope">
          {{ getFatherLabel(scope.row.father) }}
        </template>
      </el-table-column>
      <el-table-column label="说明" prop="note" width="120"></el-table-column>
      <el-table-column label="显示顺序" prop="ranknum" width="120"></el-table-column>
      <el-table-column label="是否显示" prop="show" wwidth="120"></el-table-column>
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
        <el-form ref="dataForm" :model="dataForm" :rules="formRules" label-width="100px">
          <el-form-item label="名称" prop="title">
            <el-input v-model="dataForm.title" placeholder="请输入名称"></el-input>
          </el-form-item>
          <el-form-item label="路径" prop="path">
            <el-input v-model="dataForm.path" placeholder="请输入路径"></el-input>
          </el-form-item>
          <el-form-item label="图标" prop="icon">
            <div>
              <el-popover placement="top-start" :append-to-body="false" width="30vw" trigger="click">
                <div slot="reference" class="iconDiv">
                  <el-input placeholder="请选择图标" v-model="dataForm.icon">
                    <template slot="prepend"><i :class="dataForm.icon"></i></template>
                  </el-input>
                </div>
                <div class="iconList">
                  <i
                      v-for="item in iconList"
                      :key="item"
                      :class="[item, 'icon']"
                      @click="setIcon(item)"
                      style="font-size: 20px"
                  ></i>
                </div>
              </el-popover>
            </div>
          </el-form-item>
          <el-form-item label="父级目录id" prop="father">
            <el-select v-model="dataForm.father" placeholder="请选择父级目录id" clearable>
              <el-option
                  label="根目录"
                  value="0">
              </el-option>
              <el-option
                  v-for="item in fatherList"
                  :key="item"
                  :label="item.title"
                  :value="item.id">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="说明" prop="note">
            <el-input v-model="dataForm.note" placeholder="请输入说明"></el-input>
          </el-form-item>
          <el-form-item label="显示顺序" prop="ranknum">
            <el-input v-model="dataForm.ranknum" placeholder="请输入显示顺序"></el-input>
          </el-form-item>
          <el-form-item label="是否显示" prop="show">
            <el-input v-model="dataForm.show" placeholder="请输入是否显示"></el-input>
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
import {elementIcons} from '@/utils/icon'
import {Menu} from "element-ui";
export default {
  computed: {
    Menu() {
      return Menu
    }
  },
  data() {
    return {
      pageNum: 1, //当前页面
      pageSize: 10, //页面大小
      total: 0,  //总数
      tableData: [],
      searchForm: {
        "title": "",
        "path": "",
        "icon": "",
        "father": "0",
        "note": "",
        "ranknum": 0,
        "show": 1,
      },
      myHeaders: {token: JSON.parse(localStorage.getItem("UserInfo")).token},
      selectedIds: [],
      delButtonDisable: true,
      dialogSetting: {"title": "新增", "show": false},
      dataForm: {
        "title": "",
        "path": "",
        "icon": "",
        "father": "",
        "note": "",
        "ranknum": 0,
        "show": 1,
      },
      fatherList:'',
      iconList:'',
      formRules: {
        title: [
          {required: true, message: '请输入名称', trigger: 'blur'}
        ],
        path: [
          {required: true, message: '请输入路径', trigger: 'blur'}
        ],
        icon: [
          {required: true, message: '请输入图标', trigger: 'blur'}
        ],
        father: [
          {required: true, message: '请输入父级目录id', trigger: 'blur'}
        ],
        note: [
          {required: true, message: '请输入说明', trigger: 'blur'}
        ],
        ranknum: [
          {required: true, message: '请输入显示顺序', trigger: 'blur'}
        ],
        show: [
          {required: true, message: '请输入是否显示', trigger: 'blur'}
        ],
      },
    }
  },
  created() {
    this.iconList = elementIcons
    this.confirmMenu()

  },
  methods: {
    load() {
      //创建页面时运行
      this.$request.post("/menu/pageSelect?pageNum=" + this.pageNum + "&pageSize=" + this.pageSize + "", this.searchForm)
          .then(res => {
            if (res.code === '200') {
              //console.log(res.data)
              this.tableData = res.data.list
              this.pageNum = res.data.pageNum
              this.total = res.data.total
              this.pageSize = res.data.pageSize
            }
          })
      this.getFatherList(0)
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
    exportData() {
      this.$request.post("/menu/export", this.searchForm).then(res => {
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
    getFatherList(id){
      this.$request.get("/menu/selectByFather/" + id).then(res =>{
        if (res.code === '200') {
          res.data.map((item) => {
            item.id = String(item.id)
          })
          this.fatherList = res.data
        } else {
          this.$notify.error({title:res.msg})
        }
      })
    },
    handleEdit(id) {
      this.$request.get("/menu/selectById/" + id).then(res => {
        if (res.code === '200') {
          this.dataForm = {
            "title": "",
            "path": "",
            "icon": "",
            "father": "",
            "note": "",
            "ranknum": 0,
            "show": 1,
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
        "path": "",
        "icon": "",
        "father": "",
        "note": "",
        "ranknum": 0,
        "show": 1,
      }
      this.dialogSetting.title = "新增"
      this.dialogSetting.show = true
    },
    save(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          if (this.dialogSetting.title === "新增") {
            this.$request.post("/menu/add", this.dataForm).then(res => {
              if (res.code === '200') {
                this.$notify.success({title: res.data})
                this.dialogSetting.show = false
                this.load()
              } else {
                this.$notify.error({title: res.msg, message: res.data})
              }
            })
          } else {
            this.$request.put("/menu/update", this.dataForm).then(res => {
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
      this.$request.delete("/menu/del/" + id).then(res => {
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
      this.$request.delete("/menu/del/batch", {data: this.selectedIds}).then(res => {
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
    },
    setIcon(icon){
      this.dataForm.icon = icon
    },
    getFatherLabel(id){
      if (id === '0'){
        return "/"
      } else {
        let returnTitle = ""
        this.fatherList.forEach((item) => {
          if (id === item.id) returnTitle = item.title
        })
        return returnTitle
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

.iconList {
  width: 30vw;
  height: 300px;
  overflow-y: scroll;
  overflow-x: hidden;
  display: flex;
  justify-content: space-around;
  flex-wrap: wrap;
}
.icon {
  display: inline-block;
  width: 60px;
  height: 45px;
  color: #000000;
  font-size: 20px;
  border: 1px solid #e6e6e6;
  border-radius: 4px;
  cursor: pointer;
  text-align: center;
  line-height: 45px;
  margin: 5px;
}
.icon:hover {
  color: red;
  border-color: red;
}
.iconDiv {
  width: 100%;
  height: 50%;

  display: flex;
  flex-direction: row;
  align-items: center;
}
</style>
