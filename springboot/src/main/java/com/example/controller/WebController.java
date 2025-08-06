package com.example.controller;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.example.common.Result;
import com.example.entity.Account;
import com.example.entity.Employee;
import com.example.exception.CustomException;
import com.example.service.AdminService;
import com.example.service.ArticleService;
import com.example.service.EmployeeService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
public class WebController {

    @Resource
    EmployeeService employeeService;
    @Resource
    AdminService adminService;
    @Resource
    ArticleService articleService;

    @GetMapping("/hello")
    public String hello(){
        return "Hello";
    }

    /*
    * 管理员/员工登录
    * */
    @PostMapping("/login")
    public Result login(@RequestBody Account account){
        Account result = null;
        if("ADMIN".equals(account.getRole())) {
            result = adminService.login(account);
        } else if("EMP".equals(account.getRole())) {
            result = employeeService.login(account);
        } else {
            throw new CustomException("500", "非法输入");
        }
        return Result.success(result);
    }

    /*
    * 员工注册
    * */
    @PostMapping("/register")
    public Result register(@RequestBody Employee employee){
        employeeService.register(employee);
        return Result.success();
    }

    /*
     * 修改密码
     * */
    @PutMapping("/updatePassword")
    public Result updatePassword(@RequestBody Account account){
        if("ADMIN".equals(account.getRole())) {
            adminService.updatePassword(account);
        } else if("EMP".equals(account.getRole())) {
            employeeService.updatePassword(account);
        } else {
            throw new CustomException("500", "非法输入");
        }
        return Result.success();
    }

    @GetMapping("/barData")
    public Result barData(){
        Map<String,Object> map = new HashMap<>();
        List<Employee> employeeList = employeeService.selectAll(null);
        Set<String> departmentNameSet = employeeList.stream().map(Employee::getDepartmentName).collect(Collectors.toSet());
        map.put("department", departmentNameSet); // x轴数据
        List<Long> countList = new ArrayList<>();
        for(String departmentName : departmentNameSet) {
            // 统计这个部门下面的员工数量
            long count = employeeList.stream().filter(employee -> employee.getDepartmentName().equals(departmentName)).count();
            countList.add(count);
        }
        map.put("count", countList); // y轴员工数量的数据
        return Result.success(map);
    }

    @GetMapping("/lineData")
    public Result lineData(){
        Map<String,Object> map = new HashMap<>();

        Date date = new Date(); //今天当前的时间
        DateTime start = DateUtil.offsetDay(date, -7); // 起始日期
        List<DateTime> dateTimeList = DateUtil.rangeToList(start, date, DateField.DAY_OF_YEAR);
        //把DateTime类型的日期转换成 字符串类型的日期 （“10月20日”）
        List<String> dateStrList = dateTimeList.stream().map(dateTime -> DateUtil.format(dateTime, "MM月dd日")).sorted(Comparator.naturalOrder()).collect(Collectors.toList());
        map.put("date", dateStrList); // x轴数据

        List<Integer> countList = new ArrayList<>();
        for(DateTime day : dateTimeList) {
            //10月22日
            String dayFormat = DateUtil.formatDate(day); //2025-10-22
            // 获取当天发布的所有文章的数量统计
            Integer count = articleService.selectCountByDate(dayFormat);
            countList.add(count);
        }
        map.put("count", countList); // y轴 发布文章的数量
        return Result.success(map);
    }

    @GetMapping("/pieData")
    public Result pieData(){
        List<Map<String,Object>> list = new ArrayList<>();
        List<Employee> employeeList = employeeService.selectAll(null);
        Set<String> departmentNameSet = employeeList.stream().map(Employee::getDepartmentName).collect(Collectors.toSet());
        for(String departmentName : departmentNameSet) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("name", departmentName);
            // 统计这个部门下面的员工数量
            long count = employeeList.stream().filter(employee -> employee.getDepartmentName().equals(departmentName)).count();
            map.put("value", count);
            list.add(map);
        }
        return Result.success(list);
    }
}
