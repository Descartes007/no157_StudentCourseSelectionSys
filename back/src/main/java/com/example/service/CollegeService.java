package com.example.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.example.entity.College;
import com.example.mapper.CollegeMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 学院信息业务层处理
 */
@Service
public class CollegeService {

    @Resource
    private CollegeMapper collegeMapper;

    public void add(College college) {
        collegeMapper.insert(college);
    }

    public PageInfo<College> selectPage(College college, Integer pageNum, Integer pageSize) {
        List<College> list;
        PageHelper.startPage(pageNum, pageSize);
        if (ObjectUtil.isNotEmpty(college.getName())) {
            list = collegeMapper.selectByName(college.getName());
        } else {
            list = collegeMapper.selectAll();
        }
        return PageInfo.of(list);
    }

    public void updateById(College college) {
        collegeMapper.updateById(college);
    }

    public void deleteById(Integer id) {
        collegeMapper.deleteById(id);
    }

    public List<College> selectAll() {
        return collegeMapper.selectAll();
    }
}
