package com.zjweustudent.server.controller;

import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.zjweustudent.server.common.AuthAccess;
import com.zjweustudent.server.common.Result;
import com.zjweustudent.server.entity.Register;
import com.zjweustudent.server.entity.StuInfo;
import com.zjweustudent.server.entity.TchInfo;
import com.zjweustudent.server.entity.Users;
import com.zjweustudent.server.exception.ServiceException;
import com.zjweustudent.server.service.StuInfoService;
import com.zjweustudent.server.service.TchInfoService;
import com.zjweustudent.server.service.UsersService;
import lombok.Data;
import org.apache.poi.ss.formula.functions.Now;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Function: 提供接口返回数据
 * Author: MOUJITX
 * Date: 2023/9/17 21:16
 */


@RestController
public class WebController {

    @Autowired
    UsersService usersService;
    @Autowired
    StuInfoService stuInfoService;
    @Autowired
    TchInfoService tchInfoService;

    /**
     * 测试服务器连接情况
     * **/

    @AuthAccess
    @RequestMapping("/")
    public Result hello(){
        return Result.success("hello");
    }

    /**
     * 用户登录
     */

    @AuthAccess
    @PostMapping("/login")
    public Result login(@RequestBody Users users){
        if (StrUtil.isBlank(users.getUsername()) || StrUtil.isBlank(users.getPassword())){
            return Result.error("用户名或密码不允许为空！");
        }

        /* 自动激活账号--BEGIN-- */
        Users dUser = usersService.selectByUsername(users.getUsername());
        if (dUser == null){
            StuInfo stuInfo = stuInfoService.selectByNumber(users.getUsername());
            if (stuInfo == null){
                return Result.error("用户不存在。");
            } else {
                if (!stuInfo.getIdcard().equals(users.getPassword())) {
                    return Result.error("密码错误！初始密码为身份证号（X需大写）。");
                } else {
                    users.setNickname(stuInfo.getName());
                    users.setUsergroup("2");
                    users.setNote("激活时间：" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
                    usersService.insert(users);
                }
            }
        }
        /* 自动激活账号--END-- */


        users = usersService.login(users);

        Map<String, Object> result = new HashMap<>();
        result.put("id", users.getId());
        result.put("nickname", users.getNickname());
        result.put("username", users.getUsername());
        result.put("userGroup", users.getUsergroup());
        result.put("note", users.getNote());
        result.put("token", users.getToken());

        return Result.success(result);
    }

    /**
     * 用户激活/注册
     */
    @AuthAccess
    @PostMapping("/register")
    public Result register(@RequestBody Register register){
        if (StrUtil.isBlank(register.getUsername())) {
            return Result.error("用户名不允许为空");
        } else if (StrUtil.isBlank(register.getPhone())) {
            return Result.error("联系电话验证不允许为空");
        } else if (StrUtil.isBlank(register.getPassword())) {
            return Result.error("密码不允许设置为空");
        } else {
            Users usersUnique = usersService.selectByUsername(register.getUsername());
            if (usersUnique != null) {
                return Result.errorWithTitle("账号激活失败","该账号已激活，请直接登录");
            }
            StuInfo stuInfo = stuInfoService.selectByNumber(register.getUsername());
            Users users = new Users();
            users.setUsername(register.getUsername());
            users.setPassword(register.getPassword());
            if (stuInfo != null) {
                //学生表查询到数据，验证联系电话，并注册账号
                if (stuInfo.getPhone().equals(register.getPhone())) {
                    //注册账号
                    users.setNickname(stuInfo.getName());
                    users.setUsergroup("2");
                    usersService.insert(users);
                    return Result.success("同学你好！","账号激活成功，请登录");
                } else {
                    return Result.errorWithTitle("账号激活失败","账号信息验证失败");
                }
            } else {
                //学生表查询为空，查询教师表
                TchInfo tchInfo = tchInfoService.selectByNumber(register.getUsername());
                if (tchInfo != null) {
                    //教师表查询到数据，验证联系电话，并注册账号
                    if (tchInfo.getPhone().equals(register.getPhone())){
                        //注册账号
                        users.setNickname(tchInfo.getName());
                        users.setUsergroup("5");
                        usersService.insert(users);
                        return Result.success("老师你好！","账号激活成功，请登录");
                    } else {
                        return Result.errorWithTitle("账号激活失败","账号信息验证失败");
                    }
                } else {
                    //教师表查询为空，返回错误
                    return Result.errorWithTitle("账号激活失败","未查询到该用户");
                }
            }
        }
    }

    /**
     * 解析token获取用户信息
     * **/
    @AuthAccess
    @PostMapping("/tokenAccess")
    public Result login(@RequestParam String token){
        String userId;
        try {
            userId = JWT.decode(token).getAudience().get(0);
        } catch (JWTDecodeException j) {
            throw new ServiceException("401","验证失败");
        }
        Users users = usersService.selectById(Integer.valueOf(userId));
        if (users == null || userId == null) {
            throw new ServiceException("401", "用户不存在");
        }
        Map<String, Object> result = new HashMap<>();
        result.put("id", users.getId());
        result.put("nickname", users.getNickname());
        result.put("username", users.getUsername());
        result.put("userGroup", users.getUsergroup());
        result.put("note", users.getNote());
        result.put("token", token);
        return Result.success(result);
    }
}