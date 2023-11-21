<template>
  <div>
    <div style="margin-bottom: 10px">
      <el-button style="margin-right: 10px" type="primary" @click="exportData()">导出数据</el-button>
      <el-upload :headers="myHeaders"
                 :on-progress="handleExcelImporting" :on-success="handleExcelImportSuccess"
                 :show-file-list="false"
                 accept="xlsx"
                 action="/api/userGroup/import"
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
      <el-input v-model="searchForm.name" class="topInput" clearable placeholder="请输入用户组名称"
                @input="load()"></el-input>
      <el-input v-model="searchForm.note" class="topInput" clearable placeholder="请输入说明"
                @input="load()"></el-input>
      <el-button type="primary" @click="load()">搜索</el-button>
    </div>
    <el-table :data="tableData" style="z-index: 0" @selection-change="tableSelected">
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column label="用户组名称" prop="name" width="120"></el-table-column>
      <el-table-column label="说明" prop="note" width="120"></el-table-column>
      <el-table-column label="可访问目录" prop="accessmenu" width="auto"></el-table-column>
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
          <el-form-item label="用户组名称" prop="name">
            <el-input v-model="dataForm.name" placeholder="请输入用户组名称"></el-input>
          </el-form-item>
          <el-form-item label="说明" prop="note">
            <el-input v-model="dataForm.note" placeholder="请输入说明"></el-input>
          </el-form-item>
          <el-form-item label="可访问目录" prop="accessmenu">
            <el-tree
                default-expand-all
                ref="tree"
                :load="loadNode"
                :props="{label: 'title'}"
                node-key="id"
                lazy
                @check="getSelectNode"
                show-checkbox >
            </el-tree>
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
        "name": "",
        "note": "",
        "accessmenu": "",
      },
      myHeaders: {token: JSON.parse(localStorage.getItem("UserInfo")).token},
      selectedIds: [],
      delButtonDisable: true,
      dialogSetting: {"title": "新增", "show": false},
      dataForm: {
        "name": "",
        "note": "",
        "accessmenu": "",
      },
      formRules: {
        name: [
          {required: true, message: '请输入用户组名称', trigger: 'blur'}
        ],
        note: [
          {required: true, message: '请输入说明', trigger: 'blur'}
        ],
        accessmenu: [
          {required: true, message: '请输入可访问目录', trigger: 'blur'}
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
      this.$request.post("/userGroup/pageSelect?pageNum=" + this.pageNum + "&pageSize=" + this.pageSize + "", this.searchForm)
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


    loadNode(node, resolve){
      //console.log(node,resolve)
      //如果展开第一级节点，从后台加载一级节点列表
      if (node.level === 0) {
        this.loadFirstNode(resolve);
      }
      //如果展开其他级节点，动态从后台加载下一级节点列表
      if (node.level >= 1) {
        this.loadChildNode(node, resolve);
      }
    },
    //加载第一级节点
    async loadFirstNode(resolve) {
      this.$request.get('/menu/selectByFather/0').then(res => {
        return resolve(res.data);
      })
    },
    //加载节点的子节点集合
    async loadChildNode(node, resolve) {
      this.$request.get('/menu/selectByFather/' + node.key).then(res => {
        return resolve(res.data);
      })
    },
    getSelectNode(){
      let getNodes = this.$refs.tree.getCheckedNodes(false,true);
      let accessmenuTemp = [];
      getNodes.forEach((item, index) => {
        accessmenuTemp.push(item.id)
      })
      //console.log(accessmenuTemp)
      this.dataForm.accessmenu = accessmenuTemp.toString()
    },
    async setCheckedKeys(ids) {
      //console.log('ids',ids)
      this.$refs.tree.setCheckedKeys([]);
      let arr = ids.split(',')
      //console.log('arr',arr)
      arr.forEach((item,index) => {
        this.$refs.tree.setChecked(item,true,false)
      })
      // this.$refs.tree.setCheckedKeys(arr,true);
    },


    exportData() {
      this.$request.post("/userGroup/export", this.searchForm).then(res => {
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
      this.$request.get("/userGroup/selectById/" + id).then(res => {
        if (res.code === '200') {
          this.dataForm = {
            "name": "",
            "note": "",
            "accessmenu": "",
          }
          this.dataForm = res.data
          this.$nextTick(() => {this.setCheckedKeys(res.data.accessmenu)})
          this.dialogSetting.title = "修改"
          this.dialogSetting.show = true
        } else {
          this.$notify.error({title: res.msg})
        }
      })
    },
    add() {
      this.dataForm = {
        "name": "",
        "note": "",
        "accessmenu": "",
      }
      this.$nextTick(() => {this.setCheckedKeys(this.dataForm.accessmenu)})
      this.dialogSetting.title = "新增"
      this.dialogSetting.show = true
    },
    save(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          if (this.dialogSetting.title === "新增") {
            this.$request.post("/userGroup/add", this.dataForm).then(res => {
              if (res.code === '200') {
                this.$notify.success({title: res.data})
                this.dialogSetting.show = false
                this.load()
              } else {
                this.$notify.error({title: res.msg, message: res.data})
              }
            })
          } else {
            this.$request.put("/userGroup/update", this.dataForm).then(res => {
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
      this.$request.delete("/userGroup/del/" + id).then(res => {
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
      this.$request.delete("/userGroup/del/batch", {data: this.selectedIds}).then(res => {
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
