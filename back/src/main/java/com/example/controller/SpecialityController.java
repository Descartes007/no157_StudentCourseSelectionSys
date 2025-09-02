package com.example.controller;

import com.example.common.Result;
import com.example.entity.Speciality;
import com.example.service.SpecialityService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 专业信息前端请求的接口入口
 */
@RestController
@RequestMapping("/speciality")
public class SpecialityController {

    @Resource
    private SpecialityService specialityService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody Speciality speciality) {
        specialityService.add(speciality);
        return Result.success();
    }

    /**
     * 更新
     */
    @PutMapping("/update")
    public Result update(@RequestBody Speciality speciality) {
        specialityService.updateById(speciality);
        return Result.success();
    }

    /**
     * 根据id删除
     */
    @DeleteMapping("/deleteById/{id}")
    public Result deleteById(@PathVariable Integer id) {
        specialityService.deleteById(id);
        return Result.success();
    }


    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(Speciality speciality,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "5") Integer pageSize) {
        PageInfo<Speciality> pageInfo = specialityService.selectPage(speciality, pageNum, pageSize);
        return Result.success(pageInfo);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll() {
        List<Speciality> list = specialityService.selectAll();
        return Result.success(list);
    }
}
