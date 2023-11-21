package com.zjweustudent.server.controller;

import com.zjweustudent.server.common.AuthAccess;
import com.zjweustudent.server.common.GlobalSet;
import com.zjweustudent.server.common.Result;
import com.zjweustudent.server.entity.TchInfo;
import com.zjweustudent.server.entity.Users;
import com.zjweustudent.server.service.UsersService;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Blob;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Function: 用户信息(Users)表控制层接口
 * Author: MOUJITX
 * Date: 2023-09-22 14:46:05
 */

@RestController
@RequestMapping("/users")
public class UsersController {

    @Autowired
    UsersService usersService;

    /*新增数据*/
    @PostMapping("/add")
    public Result add(@RequestBody Users users) {
        /* 唯一性校验*/
        Users uniqueCheck = usersService.selectByUsername(users.getUsername());
        if (uniqueCheck != null) {
            return Result.errorWithTitle("操作失败","该工号已存在");
        }

        usersService.insert(users);
        return Result.success("新增数据成功");
    }


    /*修改数据*/
    @PutMapping("/update")
    public Result update(@RequestBody Users users) {
        /* 唯一性校验*/
        Users uniqueCheck = usersService.selectByUsername(users.getUsername());
        Users uniqueCheck1 = usersService.selectById(users.getId());
        if (uniqueCheck != null && !uniqueCheck1.getUsername().equals(uniqueCheck.getUsername())) {
            return Result.errorWithTitle("操作失败","该用户名已存在");
        }


        usersService.update(users);
        return Result.success("更新数据成功");
    }

    @PutMapping("/changePSW")
    public Result changePSW(@RequestBody List<String> cpsw) {
        Users users = usersService.selectById(Integer.valueOf(cpsw.get(0)));
        if (!users.getPassword().equals(cpsw.get(1))) {
            return Result.error("旧密码错误！");
        } else {
            users.setPassword(cpsw.get(2));
            usersService.update(users);
            return Result.success("密码修改成功，请重新登陆");
        }
    }

    /*通过主键删除单条数据*/
    @DeleteMapping("/del/{id}")
    public Result delete(@PathVariable Integer id) {
        usersService.delete(id);
        return Result.success("删除数据成功");
    }

    /*删除多条数据*/
    @DeleteMapping("/del/batch")
    public Result batchDelete(@RequestBody List<Integer> ids) {
        usersService.batchDelete(ids);
        return Result.success("删除数据成功");
    }

    /*查询全部数据*/
    @GetMapping("/selectAll")
    public Result selectAll() {
        List<Users> list = usersService.selectAll();
        return Result.success(list);
    }

    /*通过ID查询单条数据*/
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Users list = usersService.selectById(id);
        return Result.success(list);
    }

    /*多条件模糊查询*/
    @PostMapping("/select")
    public Result select(@RequestBody Users users) {
        List<Users> list = usersService.select(users);
        return Result.success(list);
    }

    @AuthAccess
    @GetMapping("/selectDepartName")
    public Result selectDepartName() {
        List<Users> list = usersService.selectDepartName();
        return Result.success(list);
    }

    /*分页模糊多条件*/
    @PostMapping("/pageSelect")
    public Result pageSelect(@RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             @RequestBody Users users) {
        Map<String, Object> result = new HashMap<>();
        List<Users> list = usersService.pageSelect(pageNum, pageSize, users);
        Integer total = usersService.pageSelectCount(users);
        Integer totalPage = (int) Math.ceil((double) total / pageSize);
        result.put("list", list);
        result.put("total", total);
        result.put("totalPage", totalPage);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        return Result.success(result);
    }



    /*下载*/
    @PostMapping("/export")
    public Result export(@RequestBody Users users) throws Exception {
        List<Users> list = usersService.select(users);
        GlobalSet globalSet = new GlobalSet();
        String docStorePath = globalSet.getDocStorePath() + "/files/export/";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String formatDate = sdf.format(new Date());
        String UniCodeName = UUID.randomUUID().toString();
        String fileName = "/exportUsers_" + formatDate + "_" + UniCodeName + ".xls";
        ExcelWriter writer = ExcelUtil.getWriter(docStorePath + fileName);
        //自定义标题别名
        writer.addHeaderAlias("nickname", "昵称");
        writer.addHeaderAlias("username", "账号");
        writer.addHeaderAlias("password", "密码");
        writer.addHeaderAlias("note", "说明");
        writer.addHeaderAlias("usergroup", "用户组");

        // 一次性写出list内的对象到excel，使用默认样式，强制输出标题
        writer.setOnlyAlias(true);  //只导出设置了别名的字段
        writer.write(list, true);
        writer.close();

        return Result.success("导出成功", fileName);
    }

    /*上传*/
    @PostMapping("/import")
    public Result imp(MultipartFile file) throws Exception {
        InputStream inputStream = file.getInputStream();
        ExcelReader reader = ExcelUtil.getReader(inputStream);
        // 方式1：(推荐) 通过 javabean的方式读取Excel内的对象，但是要求表头必须是英文，跟javabean的属性要对应起来
        //List<User> list = reader.readAll(User.class);
        // 方式2：忽略表头的中文，直接读取表的内容
        List<List<Object>> list = reader.read(1);
        int addNum = 0;
        int updateNum = 0;
        for (List<Object> row : list) {
            //导入
            Users users = new Users();

            /* 用于实现数据更新，而非新增*/
            String username = row.get(1).toString();
            users.setUsername(username);

            users.setNickname(row.get(0).toString());
            //users.setUsername(row.get(1).toString());
            users.setPassword(row.get(2).toString());
            users.setNote(row.get(3).toString());
            users.setUsergroup(row.get(4).toString());

            /* 用于实现数据更新，而非新增*/
            Users uniqueConfirm = usersService.selectByUsername(username);
            if (uniqueConfirm == null) {
                usersService.insert(users);
                addNum++;
            } else {
                users.setId(uniqueConfirm.getId());
                usersService.update(users);
                updateNum++;
            }


        }
        Map<String, Object> Rst = new HashMap<>();
        Rst.put("updata", updateNum);
        Rst.put("add", addNum);
        return Result.success("导入完成", Rst);
    }
}
