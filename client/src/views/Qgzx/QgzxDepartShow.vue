<template>
  <div>
    <div>
      <el-input v-model="searchForm.name" class="topInput" clearable placeholder="部门名称"
                @input="load()"></el-input>
      <el-button type="primary" @click="load()">搜索</el-button>
    </div>
    <el-table :data="tableData" style="z-index: 0" @selection-change="tableSelected">
      <el-table-column prop="name" label="部门名称" width="200" />
      <el-table-column prop="gwsm" label="岗位说明" width="auto" />
      <el-table-column prop="pytj" label="用工需求" width="auto" />
      <el-table-column label="录用人数">
        <el-table-column prop="qtrs" label="钱塘校区" width="100" />
        <el-table-column prop="nxrs" label="南浔校区" width="100" />
      </el-table-column>
      <el-table-column align="center" fixed="right" label="操作" width="100">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="handleEdit(scope.row.id)">查看更多</el-button>
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

              <el-form label-width="100px" ref="dataForm" :model="dataForm" :rules="formRules" :disabled="true">
                <el-form-item label="部门名称" prop="name">
                  <el-input v-model="dataForm.name" placeholder="请输入部门名称"  clearable
                            :style="{width: '100%'}"></el-input>
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
                <el-form label-width="100px" :disabled="true" inline>
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
                <el-form label-width="100px" :disabled="true" inline>
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
        "detail": "1",
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
          {required: false, message: '请输入部门说明', trigger: 'blur'}
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
          this.dialogSetting.title = "查看更多"
          this.dialogSetting.show = true
        } else {
          this.$notify.error({title: res.msg})
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
