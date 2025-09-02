package com.example.service;

import cn.hutool.core.util.ObjectUtil;
import com.example.entity.Speciality;
import com.example.mapper.SpecialityMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 专业信息业务层处理
 */
@Service
public class SpecialityService {

    @Resource
    private SpecialityMapper specialityMapper;

    public void add(Speciality speciality) {
        specialityMapper.insert(speciality);
    }

    public PageInfo<Speciality> selectPage(Speciality speciality, Integer pageNum, Integer pageSize) {
        List<Speciality> list;
        PageHelper.startPage(pageNum, pageSize);
        if (ObjectUtil.isNotEmpty(speciality.getName())) {
            list = specialityMapper.selectByName(speciality.getName());
        } else {
            list = specialityMapper.selectAll();
        }
        return PageInfo.of(list);
    }

    public void updateById(Speciality speciality) {
        specialityMapper.updateById(speciality);
    }

    public void deleteById(Integer id) {
        specialityMapper.deleteById(id);
    }

    public List<Speciality> selectAll() {
        return specialityMapper.selectAll();
    }
}
