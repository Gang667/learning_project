import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/login'
    },
    {
      path: '/manager',
      children: [
        {
          path: 'home',
          name: 'home',
          meta: { title: '主页'},
          component: () => import('../views/Home.vue'),
        },
        {
          path: 'test',
          name: 'test',
          meta: { title: '测试页'},
          component: () => import('../views/test.vue'),
        },
        {
          path: 'data',
          name: 'data',
          meta: { title: '数据展示页面'},
          component: () => import('../views/Data.vue'),
        },
        {
          path: 'employee',
          name: 'employee',
          meta: { title: '员工信息页面'},
          component: () => import('../views/Employee.vue'),
        },
        {
          path: 'admin',
          name: 'admin',
          meta: { title: '管理员信息页面'},
          component: () => import('../views/Admin.vue'),
        },
        {
          path: 'person',
          name: 'person',
          meta: { title: '个人信息页面'},
          component: () => import('../views/Person.vue'),
        },
        {
          path: 'password',
          name: 'password',
          meta: { title: '修改密码页面'},
          component: () => import('../views/Password.vue'),
        },
        {
          path: 'article',
          name: 'article',
          meta: { title: '文章信息页面'},
          component: () => import('../views/Article.vue'),
        },
        {
          path: 'department',
          name: 'department',
          meta: { title: '部门信息页面'},
          component: () => import('../views/Department.vue'),
        },
      ],
      component: () => import('../views/Manager.vue')
    },
    {
      path: '/login',
      name: 'login',
      meta: { title: '登录系统' },
      component: () => import('../views/Login.vue'),
    },
    {
      path: '/register',
      name: 'register',
      meta: { title: '欢迎注册' },
      component: () => import('../views/Register.vue'),
    },
    {
      path: '/404',
      name: 'notFound',
      meta: { title: 'notFound' },
      component: () => import('../views/404.vue'),
    },
    {
      path: '/:pathMatch(.*)', redirect: '/404'
    }

  ],

})

//beforeEach表示跳转前的一些操作
router.beforeEach((to, from, next) => {
  document.title = to.meta.title
  next() //必须调用这个函数才能实现跳转
})

export default router
