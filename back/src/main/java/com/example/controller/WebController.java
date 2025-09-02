package com.example.controller;

import com.example.common.Result;
import com.example.entity.Account;
import com.example.exception.CustomException;
import com.example.service.AdminService;
import com.example.service.StudentService;
import com.example.service.TeacherService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;


@RestController
public class WebController {

    @Resource
    private AdminService adminService;
    @Resource
    private TeacherService teacherService;
    @Resource
    private StudentService studentService;


    /**
     * 默认请求接口
     */
    @GetMapping("/")
    public Result hello() {
        return Result.success();
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public Result login(@RequestBody Account account) {
        // 检查角色是否为空
        if (account.getRole() == null || account.getRole().isEmpty()) {
            return Result.error("请选择身份类型");
        }

        // 根据角色类型调用不同服务的登录验证
        try {
            Object user = null;
            if ("ADMIN".equals(account.getRole())) {
                user = adminService.login(account);
            } else if ("TEACHER".equals(account.getRole())) {
                user = teacherService.login(account);
            } else if ("STUDENT".equals(account.getRole())) {
                user = studentService.login(account);
            } else {
                return Result.error("无效的身份类型");
            }
            
            return Result.success(user);
        } catch (CustomException e) {
            // 登录验证失败，返回错误信息
            return Result.error(e.getMsg());
        } catch (Exception e) {
            // 其他异常
            return Result.error("登录失败，请稍后再试");
        }
    }

    /**
     * 注册
     */
    @PostMapping("/register")
    public Result register(@RequestBody Account account) {
        studentService.register(account);
        return Result.success();
    }

    /**
     * 修改密码
     */
    @PutMapping("/updatePassword")
    public Result updatePassword(@RequestBody Account account) {
        if ("ADMIN".equals(account.getRole())) {
            adminService.updatePassword(account);
        }
        if ("TEACHER".equals(account.getRole())) {
            teacherService.updatePassword(account);
        }
        if ("STUDENT".equals(account.getRole())) {
            studentService.updatePassword(account);
        }
        return Result.success();
    }

}
