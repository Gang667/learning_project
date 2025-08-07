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
<!--      <el-button type="info">导 入</el-button>
      <el-button type="success">重 置</el-button>-->
    </div>

    <div class="card" style="margin-bottom: 5px">
      <el-table :data="data.tableData" stripe @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" />
        <el-table-column label="名称" prop="name"></el-table-column>
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

    <el-dialog v-model="data.formVisible" title="部门信息" width="500" destroy-on-close>
      <el-form ref="formRef" :rules="data.rules" :model="data.form" style="padding-right: 50px; padding-top: 20px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="data.form.name" autocomplete="off" placeholder="请输入名称"/>
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
  rules: {
    name: [
      { required: true, message: '请输入名称', trigger: 'blur' }
    ]
  }
})

const formRef = ref()

const load = () => {
  request.get('/department/selectPage', {
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
  request.post('/department/add', data.form).then(res => { //新增的对象没有id
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
  request.put('/department/update', data.form).then(res => { //修改的对象有id
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
    request.delete('/department/deleteById/' + id).then(res => {
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
    request.delete('/department/deleteBatch', { data: data.ids }).then(res => {
      if ( res.code === '200') {
        ElMessage.success('操作成功')
        load() //删除后一定要重新加载最新的数据
      } else {
        ElMessage.error(res.msg)
      }
    })
  })
}

</script>
