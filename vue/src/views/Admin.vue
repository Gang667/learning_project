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
    </div>

    <div class="card" style="margin-bottom: 5px">
      <el-table :data="data.tableData" stripe @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55"></el-table-column>
        <el-table-column label="账号" prop="username"></el-table-column>
        <el-table-column label="头像">
          <template #default="scope">
            <img v-if="scope.row.avatar" :src="scope.row.avatar" alt="" style="display: block; width: 40px; height: 40px; border-radius: 50%"/>
          </template>
        </el-table-column>
        <el-table-column label="名称" prop="name"></el-table-column>
        <el-table-column label="角色" prop="role"></el-table-column>
        <el-table-column label="操作">
          <template #default="scope">
            <el-button type="primary" :icon="Edit" circle @click="handleUpdate(scope.row)"></el-button>
            <el-button type="danger" :icon="Delete" circle @click="del(scope.row.id)"></el-button>
          </template>
        </el-table-column>
      </el-table>

      <div style="margin-top:15px">
        <el-pagination
          @size-change="load"
          @current-change="load"
          v-model:current-page="data.pageNum"
          v-model:page-size="data.pageSize"
          :page-size="[5,10,15,20]"
          background
          layout="total, sizes, prev, pager. next, jumper"
          :total="data.total"
        />
      </div>
    </div>

    <el-dialog v-model="data.formVisible" title="管理员信息" width="500" destroy-on-close>
      <el-form ref="formRef" :rules="data.rules" :model="data.form" style="padding-right: 50px; padding-top: 20px">
        <el-form-item label="账号" prop="username">
          <el-input v-model="data.form.username" autocomplete="off" placeholder="请输入账号"></el-input>
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
          <el-input v-model="data.form.name" autocomplete="off" placeholder="请输入名称"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="data.formVisible = false">取消</el-button>
          <el-button type="primary" @click="save">保存</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref } from "vue";
import {Delete, Edit} from "@element-plus/icons-vue"
import request from "@/utils/request.js";
import {ElMessage, ElMessageBox} from "element-plus";

const data = reactive({
  name: null,
  pageNum: 1,
  pageSize: 10,
  total: 1,
  formVisible: false,
  tableData: [],
  form: {},
  rules: {
    username: [
      { required: true, message: '请输入账号', trigger: 'blur' }
    ],
    name: [
      { required: true, message: '请输入名称', trigger: 'blur' }
    ]
  },
  ids: []
})

const formRef = ref()

const load = () => {
  request.get('/admin/selectByPage', {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
      name: data.name,
    }
  }).then(res => {
    data.tableData = res.data.list
    data.total = res.data.total
  })
}

load()

const reset = () => {
  data.name = null
  load()
}

const save = () => {
  formRef.value.validate((valid) => {
    if(valid) {
      data.form.id ? update() : add()
    }
  })
}

const handleAdd = () => {
  data.formVisible = true
  data.form = {}
}

const delBatch = () => {
  if(data.ids.length === 0) {
    ElMessage.warning('请选择数据')
    return
  }
  ElMessageBox.confirm('删除数据无法恢复，您确认删除吗', '删除确认', { type: 'warning' }).then(() => {
    request.delete('/admin/deleteBatch', { data: data.ids }).then(res => {
      if( res.code === '200'){
        ElMessage.success('操作成功')
        load()
      } else {
        ElMessage.error(res.msg)
      }
    })
  }).catch()
}

const handleSelectionChange = (rows) => {
  data.ids = rows.map(row => row.id)
}

const handleUpdate = (row) => {
  data.form = JSON.parse(JSON.stringify(row))
  data.formVisible = true
}

const del = (id) => {
  ElMessageBox.confirm('删除数据无法恢复，您确认删除吗', '删除确认', { type: 'warning' }).then(() => {
    request.delete('/admin/deleteById/' + id).then(res => {
      if(res.code === '200'){
        ElMessage.success('操作成功')
        load()
      } else {
        ElMessage.error(res.msg)
      }
    })
  })
}

const update = () => {
  request.put('/admin/update', data.form).then(res => { //修改的对象有id
    if ( res.code === '200') {
      data.formVisible = false
      ElMessage.success('操作成功')
      load() //更新后一定要重新加载最新的数据
    } else {
      ElMessage.error(res.msg)
    }
  })
}

const add = () => {
  request.post('/admin/add', data.form).then(res => {
    if(res.code === '200'){
      ElMessage.success('更新成功')
      load()
      data.formVisible = false
    } else {
      ElMessage.error(res.msg)
    }
  })
}

const handleAvatarSuccess = (res) => {
  data.form.avatar = res.data
}
</script>