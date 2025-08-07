<template>
  <div>
    <div class="card" style="margin-bottom: 5px">
      <el-input style="width: 240px; margin-right: 10px" v-model="data.name" placeholder="请输入名称搜索" prefix-icon="Search"></el-input>
      <el-button type="primary" @click="load">查 询</el-button>
      <el-button type="primary" @click="reset">重 置</el-button>
    </div>

    <div class="card" style="margin-bottom: 5px">
      <el-button type="primary" @click="handleAdd">新 增</el-button>
      <el-button type="danger" @click="delBatch">批量删除</el-button>
      <el-upload
          style="display: inline-block; margin-left: 10px; margin-right: 10px"
          action="http://localhost:9090/employee/import"
          :show-file-list="false"
          :on-success="importSuccess"
      >
        <el-button type="info" >导 入</el-button>
      </el-upload>
      <el-button type="success" @click="exportData">导 出</el-button>
    </div>

    <div class="card" style="margin-bottom: 5px">
      <el-table :data="data.tableData" stripe @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" />
        <el-table-column label="账号" prop="username"></el-table-column>
        <el-table-column label="头像">
          <template #default="scope">
            <img v-if="scope.row.avatar" :src="scope.row.avatar" alt="" style="display: block; width: 40px; height: 40px; border-radius: 50%"/>
          </template>
        </el-table-column>
        <el-table-column label="名称" prop="name"></el-table-column>
        <el-table-column label="角色" prop="role"></el-table-column>
        <el-table-column label="性别" prop="sex"></el-table-column>
        <el-table-column label="工号" prop="no"></el-table-column>
        <el-table-column label="年龄" prop="age"></el-table-column>
        <el-table-column label="个人介绍" prop="description" show-overflow-tooltip></el-table-column>
        <el-table-column label="部门" prop="departmentName"></el-table-column>
        <el-table-column label="操作">
          <template #default="scope">
            <el-button type="primary" :icon="Edit" circle @click="handleUpdate(scope.row)"></el-button>
            <el-button type="danger" :icon="Delete" circle @click="del(scope.row.id)"></el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top: 15px">
        <el-pagination
            @size-change="load"
            @current-change="load"
            v-model:current-page="data.pageNum"
            v-model:page-size="data.pageSize"
            :page-size="[5,10,15,20]"
            background
            layout="total, sizes, prev, pager, next, jumper"
            :total="data.total"
        />
      </div>
    </div>

    <el-dialog v-model="data.formVisible" title="员工信息" width="500" destroy-on-close>
      <el-form ref="formRef" :rules="data.rules" :model="data.form" style="padding-right: 50px; padding-top: 20px">
        <el-form-item label="账号" prop="username">
          <el-input v-model="data.form.username" autocomplete="off" placeholder="请输入账号"/>
        </el-form-item>
        <el-form-item label="部门">
          <el-select style="width: 100%" v-model="data.form.departmentId">
            <el-option v-for="item in data.departmentList" :key="item.id" :label="item.name" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="头像">
          <el-upload
              action="http://localhost:9090/files/upload"
              list-type="picture"
              :on-success="handleAvatarSuccess"
          >
            <el-button type="primary">上传头像</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item label="名称" prop="name">
          <el-input v-model="data.form.name" autocomplete="off" placeholder="请输入名称"/>
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="data.form.sex">
            <el-radio value="男" label="男"></el-radio>
            <el-radio value="女" label="女"></el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="工号" prop="no">
          <el-input v-model="data.form.no" autocomplete="off" placeholder="请输入工号"/>
        </el-form-item>
        <el-form-item label="年龄">
          <el-input-number style="width: 180px" :min="18" v-model="data.form.age" autocomplete="off" placeholder="请输入年龄"/>
        </el-form-item>
        <el-form-item label="个人介绍">
          <el-input :rows="3" type="textarea" v-model="data.form.description" autocomplete="off" placeholder="请输入个人介绍"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="data.formVisible = false">取消</el-button>
          <el-button type="primary" @click="save">
            保存
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref } from "vue";
import {Delete, Edit, Search} from "@element-plus/icons-vue";
import request from "@/utils/request.js";
import {ElMessage, ElMessageBox} from "element-plus";

const data = reactive({
  name: null,
  tableData: [],
  pageNum: 1,
  pageSize: 10,
  total: 0,
  formVisible: false,
  form: {},
  ids: [],
  departmentList: [],
  rules: {
    username: [
      { required: true, message: '请输入账号', trigger: 'blur' }
    ],
    name: [
      { required: true, message: '请输入名称', trigger: 'blur' }
    ],
    no: [
      { required: true, message: '请输入工号', trigger: 'blur' }
    ]
  }
})

const formRef = ref()

request.get('/department/selectAll').then(res => {
  data.departmentList = res.data
})

const exportData = () => {
  //导出数据，是通过流的形式下载excel   打开流的链接，浏览器会自动帮我们下载文件
  window.open('http://localhost:9090/employee/export')
}

const load = () => {
  request.get('/employee/selectPage', {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
      name: data.name
    }
  }).then(res => {
    data.tableData = res.data.list
    data.total = res.data.total
  }) //?pageNum=1&pageSize=10
}

load()

const reset = () => {
  data.name = null
  load()
}

const handleAdd = () => {
  data.formVisible = true
  data.form = {}
}

const save = () => {
  formRef.value.validate((valid) => {
    if(valid) {
      data.form.id ? update() : add()
    }
  })
}

const add = () => {
  request.post('/employee/add', data.form).then(res => { //新增的对象没有id
    if ( res.code === '200') {
      data.formVisible = false
      ElMessage.success('操作成功')
      load() //新增后一定要重新加载最新的数据
    } else {
      ElMessage.error(res.msg)
    }
  })
}

const update = () => {
  request.put('/employee/update', data.form).then(res => { //修改的对象有id
    if ( res.code === '200') {
      data.formVisible = false
      ElMessage.success('操作成功')
      load() //更新后一定要重新加载最新的数据
    } else {
      ElMessage.error(res.msg)
    }
  })
}

const handleUpdate = (row) => {
  data.form = JSON.parse(JSON.stringify(row)) //深拷贝一个新的对象用于编辑，这样就不会影响行对象
  data.formVisible = true
}

const del = (id) => {
  ElMessageBox.confirm('删除数据后无法恢复，您确认删除吗', '删除确认', { type: 'warning' }).then(() =>{
    request.delete('/employee/deleteById/' + id).then(res => {
      if ( res.code === '200') {
        ElMessage.success('操作成功')
        load() //删除后一定要重新加载最新的数据
      } else {
        ElMessage.error(res.msg)
      }
    })
  }).catch()
}

const handleSelectionChange = (rows) => { //返回所有选中的行对象数组
  console.log(rows)
  // 从选中的行数组里面取出所有行的id组成一个新的数组
  data.ids = rows.map(row => row.id)
  console.log(data.ids)
}

const delBatch = () => {
  if(data.ids.length === 0) {
    ElMessage.warning('请选择数据')
    return
  }
  ElMessageBox.confirm('删除数据后无法恢复，您确认删除吗', '删除确认', { type: 'warning' }).then(() => {
    request.delete('/employee/deleteBatch', { data: data.ids }).then(res => {
      if ( res.code === '200') {
        ElMessage.success('操作成功')
        load() //删除后一定要重新加载最新的数据
      } else {
        ElMessage.error(res.msg)
      }
    })
  })
}

const handleAvatarSuccess = (res) => {
  data.form.avatar = res.data
}

const importSuccess = (res) => {
  if(res.code == '200') {
    ElMessage.success('导入成功')
    load()
  } else {
    ElMessage.error(res.msg)
  }
}
</script>

<style scoped>
.avatar-uploader .avatar {
  width: 178px;
  height: 178px;
  display: block;
}
</style>


<style>
.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  cursor: pointer;
  position: relative;
  overflow: hidden;
  background-size: cover;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  text-align: center;
}
</style>