package com.example.controller;

import com.example.common.Result;
import com.example.entity.College;
import com.example.service.CollegeService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学院信息前端请求的接口入口
 */
@RestController
@RequestMapping("/college")
public class CollegeController {

    @Resource
    private CollegeService collegeService;
    
    @Resource
    private JdbcTemplate jdbcTemplate;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody College college) {
        collegeService.add(college);
        return Result.success();
    }

    /**
     * 更新
     */
    @PutMapping("/update")
    public Result update(@RequestBody College college) {
        collegeService.updateById(college);
        return Result.success();
    }

    /**
     * 根据id删除
     */
    @DeleteMapping("/deleteById/{id}")
    public Result deleteById(@PathVariable Integer id) {
        collegeService.deleteById(id);
        return Result.success();
    }


    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(College college,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "5") Integer pageSize) {
        PageInfo<College> pageInfo = collegeService.selectPage(college, pageNum, pageSize);
        return Result.success(pageInfo);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll() {
        List<College> list = collegeService.selectAll();
        return Result.success(list);
    }
    
    /**
     * 获取学院总数
     */
    @GetMapping("/count")
    public Result count() {
        String sql = "SELECT COUNT(*) FROM college";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return Result.success(count);
    }
}
