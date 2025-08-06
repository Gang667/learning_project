package com.example.service;

import cn.hutool.core.util.StrUtil;
import com.example.entity.Account;
import com.example.entity.Department;
import com.example.exception.CustomException;
import com.example.mapper.DepartmentMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService {

    @Resource
    private DepartmentMapper departmentMapper;

    public void add(Department department) {
        departmentMapper.insert(department);
    }

    public void update(Department department) {
        departmentMapper.updateById(department);
    }

    public void deleteById(Integer id) {
        departmentMapper.deleteById(id);
    }

    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            this.deleteById(id);
        }
    }

    public List<Department> selectAll(Department department) {
        List<Department> list = departmentMapper.selectAll(department);
        return list;
    }

    public Department selectById(Integer id) {
        Department department = departmentMapper.selectById(id);
        return department;
    }

    public PageInfo<Department> selectPage(Department department, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Department> list = departmentMapper.selectAll(department);
        return PageInfo.of(list);
    }
}
