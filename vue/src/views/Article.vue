<template>
  <div>
    <div class="card" style="margin-bottom: 5px">
      <el-input style="width: 240px; margin-right: 10px" v-model="data.title" placeholder="请输入标题搜索" prefix-icon="Search"></el-input>
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
        <el-table-column label="标题" prop="title"></el-table-column>
        <el-table-column label="封面">
          <template #default="scope">
            <el-image v-if="scope.row.img" :src="scope.row.img" :preview-src-list=[scope.row.img] preview-teleported  alt="" style="display: block; width: 100px; height: 60px"/>
          </template>
        </el-table-column>
        <el-table-column label="简介" prop="description" show-overflow-tooltip></el-table-column>
        <el-table-column label="内容">
          <template #default="scope">
            <el-button type="primary" @click="view(scope.row.content)">查看内容</el-button>
          </template>
        </el-table-column>
        <el-table-column label="发布时间" prop="time"></el-table-column>
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

    <el-dialog v-model="data.formVisible" title="文章信息" width="50%" destroy-on-close>
      <el-form :model="data.form" style="padding-right: 50px; padding-top: 20px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="data.form.title" autocomplete="off" placeholder="请输入标题"></el-input>
        </el-form-item>
        <el-form-item label="封面">
          <el-upload
              action="http://localhost:9090/files/upload"
              list-type="picture"
              :on-success="handleImgSuccess"
          >
            <el-button type="primary">上传封面</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item label="简介" prop="description">
          <el-input type="textarea" :rows="3" v-model="data.form.description" autocomplete="off" placeholder="请输入简介"></el-input>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <div style="border: 1px solid #ccc; width:100%">
            <Toolbar
                style="border-bottom: 1px solid #ccc"
                :editor="editorRef"
                :mode="mode"
            />
            <Editor
                style="height: 500px; overflow-y: hidden;"
                v-model="data.form.content"
                :defaultConfig="editorConfig"
                :mode="mode"
                @onCreated="handleCreated"
            />
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="data.formVisible = false">取消</el-button>
          <el-button type="primary" @click="save">保存</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog title="内容" v-model="data.viewVisible" :close-on-click-modal="false" style="width:50%" destroy-on-close>
      <div class="editor-content-view" style="padding: 20px" v-html="data.content"></div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="data.viewVisible = false">关 闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref } from "vue";
import {Delete, Edit} from "@element-plus/icons-vue"
import request from "@/utils/request.js";
import {ElMessage, ElMessageBox} from "element-plus";

import '@wangeditor/editor/dist/css/style.css' // 引入 css
import { onBeforeUnmount, shallowRef } from 'vue'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'

const data = reactive({
  title: null,
  pageNum: 1,
  pageSize: 10,
  total: 1,
  formVisible: false,
  tableData: [],
  form: {},
  ids: [],
  viewVisible: false,
  content: null
})

/* wangeditor5 初始化开始 */
const baseUrl = 'http://localhost:9090'
// 编辑器实例，必须用 shallowRef
const editorRef = shallowRef()
const mode = 'default'
const editorConfig = { MENU_CONF: {} }
//图片上传配置
editorConfig.MENU_CONF['uploadImage'] = {
  server: baseUrl + '/files/wang/upload', //服务端图片上传接口
  fieldName: 'file' //服务端图片上传接口参数
}
//组件销毁时，及时销毁编辑器，否则可能会造成内存泄露
onBeforeUnmount(() => {
  const editor = editorRef.value
  if(editor == null) return
  editor.destroy();
})
//记录editor实例，重要！
const handleCreated = (editor) => {
  editorRef.value = editor
}

/* wangeditor5 初始化结束 */

const view = (content) => {
  data.content = content
  data.viewVisible = true
}

const load = () => {
  request.get('/article/selectByPage', {
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
  data.form.id ? update() : add()
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
    request.delete('/article/deleteBatch', { data: data.ids }).then(res => {
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
    request.delete('/article/deleteById/' + id).then(res => {
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
  request.put('/article/update', data.form).then(res => { //修改的对象有id
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
  request.post('/article/add', data.form).then(res => {
    if(res.code === '200'){
      ElMessage.success('更新成功')
      load()
      data.formVisible = false
    } else {
      ElMessage.error(res.msg)
    }
  })
}

const handleImgSuccess = (res) => {
  data.form.img = res.data
}
</script>