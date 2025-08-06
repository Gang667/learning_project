package com.example.service;

import cn.hutool.core.util.StrUtil;
import com.example.entity.Account;
import com.example.entity.Employee;
import com.example.exception.CustomException;
import com.example.mapper.DepartmentMapper;
import com.example.mapper.EmployeeMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    @Resource
    private EmployeeMapper employeeMapper;
    @Resource
    private DepartmentMapper departmentMapper;

    public Employee login(Account account) {
        String username = account.getUsername();
        Employee dbEmployee = employeeMapper.selectByUsername(username);
        if( dbEmployee == null ) { //没查询到任何用户
            throw new CustomException("500", "账号不存在");
        }
        //数据库存在这个用户
        String password = account.getPassword();
        if(!password.equals(dbEmployee.getPassword())) {
            throw new CustomException("500", "账号或密码错误");
        }
        return dbEmployee;
    }

    public void add(Employee employee) {
        String username = employee.getUsername();
        Employee dbEmployee = employeeMapper.selectByUsername(username);
        if( dbEmployee != null ) { //注册的账号已存在，无法注册
            throw new CustomException("500","账号已存在，请更换别的账号");
        }
        String no = employee.getNo();
        Employee dbEmployee1 = employeeMapper.selectByNo(no);
        if( dbEmployee1 != null ) { //注册的工号已存在，无法注册
            throw new CustomException("500","工号已存在，请更换别的工号");
        }
        Integer departmentId = departmentMapper.selectByName(employee.getDepartmentName()).getId();
        employee.setDepartmentId(departmentId);
        if(StrUtil.isBlank(employee.getPassword())){
            employee.setPassword("123");
        }
        if(StrUtil.isBlank(employee.getName())) {
            employee.setName(employee.getUsername());
        }
        employee.setRole("EMP");
        employeeMapper.insert(employee);
    }

    public void update(Employee employee) {
        employeeMapper.updateById(employee);
    }

    public void deleteById(Integer id) {
        employeeMapper.deleteById(id);
    }

    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            this.deleteById(id);
        }
    }

    public List<Employee> selectAll(Employee employee) {
        List<Employee> list = employeeMapper.selectAll(employee);
        return list;
    }

    public Employee selectById(Integer id) {
        Employee employee = employeeMapper.selectById(id);
        return employee;
    }

    public PageInfo<Employee> selectPage(Employee employee, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Employee> list = employeeMapper.selectAll(employee);
        return PageInfo.of(list);
    }

    public void register(Employee employee) {
        this.add(employee);
    }

    public void updatePassword(Account account) {
        Integer id = account.getId();
        Employee employee = this.selectById(id);
        if(!account.getPassword().equals(employee.getPassword())) {
            throw new CustomException("500", "对不起，原密码错误");
        }
        employee.setPassword(account.getNewPassword());
        this.update(employee);
    }
}
