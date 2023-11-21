package com.zjweustudent.server.controller;

import com.zjweustudent.server.common.GlobalSet;
import com.zjweustudent.server.common.Result;
import com.zjweustudent.server.entity.Menu;
import com.zjweustudent.server.entity.UserGroup;
import com.zjweustudent.server.entity.Users;
import com.zjweustudent.server.service.UserGroupService;
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
 * Function: 用户组(UserGroup)表控制层接口
 * Author: MOUJITX
 * Date: 2023-09-22 14:38:26
 */

@RestController
@RequestMapping("/userGroup")
public class UserGroupController {

    @Autowired
    UserGroupService userGroupService;

    /*新增数据*/
    @PostMapping("/add")
    public Result add(@RequestBody UserGroup userGroup) {
        /* 唯一性校验*/
        UserGroup uniqueCheck = userGroupService.selectByName(userGroup.getName());
        if (uniqueCheck != null) {
            return Result.errorWithTitle("操作失败","该用户组已存在");
        }

        userGroupService.insert(userGroup);
        return Result.success("新增数据成功");
    }


    /*修改数据*/
    @PutMapping("/update")
    public Result update(@RequestBody UserGroup userGroup) {
        /* 唯一性校验*/
        UserGroup uniqueCheck = userGroupService.selectByName(userGroup.getName());
        UserGroup uniqueCheck1 = userGroupService.selectById(userGroup.getId());
        if (uniqueCheck != null && !uniqueCheck1.getName().equals(uniqueCheck.getName())) {
            return Result.errorWithTitle("操作失败","该用户组已存在");
        }

        userGroupService.update(userGroup);
        return Result.success("更新数据成功");
    }

    /*通过主键删除单条数据*/
    @DeleteMapping("/del/{id}")
    public Result delete(@PathVariable Integer id) {
        userGroupService.delete(id);
        return Result.success("删除数据成功");
    }

    /*删除多条数据*/
    @DeleteMapping("/del/batch")
    public Result batchDelete(@RequestBody List<Integer> ids) {
        userGroupService.batchDelete(ids);
        return Result.success("删除数据成功");
    }

    /*查询全部数据*/
    @GetMapping("/selectAll")
    public Result selectAll() {
        List<UserGroup> list = userGroupService.selectAll();
        return Result.success(list);
    }

    /*通过ID查询单条数据*/
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        UserGroup list = userGroupService.selectById(id);
        return Result.success(list);
    }

    /*多条件模糊查询*/
    @PostMapping("/select")
    public Result select(@RequestBody UserGroup userGroup) {
        List<UserGroup> list = userGroupService.select(userGroup);
        return Result.success(list);
    }

    /*分页模糊多条件*/
    @PostMapping("/pageSelect")
    public Result pageSelect(@RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             @RequestBody UserGroup userGroup) {
        Map<String, Object> result = new HashMap<>();
        List<UserGroup> list = userGroupService.pageSelect(pageNum, pageSize, userGroup);
        Integer total = userGroupService.pageSelectCount(userGroup);
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
    public Result export(@RequestBody UserGroup userGroup) throws Exception {
        List<UserGroup> list = userGroupService.select(userGroup);
        GlobalSet globalSet = new GlobalSet();
        String docStorePath = globalSet.getDocStorePath() + "/files/export/";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String formatDate = sdf.format(new Date());
        String UniCodeName = UUID.randomUUID().toString();
        String fileName = "/exportUserGroup_" + formatDate + "_" + UniCodeName + ".xls";
        ExcelWriter writer = ExcelUtil.getWriter(docStorePath + fileName);
        //自定义标题别名
        writer.addHeaderAlias("name", "用户组名称");
        writer.addHeaderAlias("note", "说明");
        writer.addHeaderAlias("accessmenu", "可访问目录");

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
            UserGroup userGroup = new UserGroup();

            /* 用于实现数据更新，而非新增*/
            String name = row.get(0).toString();
            userGroup.setName(name);


            //userGroup.setName(row.get(0).toString());
            userGroup.setNote(row.get(1).toString());
            userGroup.setAccessmenu(row.get(2).toString());

            /* 用于实现数据更新，而非新增*/
            UserGroup uniqueConfirm = userGroupService.selectByName(name);
            if (uniqueConfirm == null) {
                userGroupService.insert(userGroup);
                addNum++;
            } else {
                userGroup.setId(uniqueConfirm.getId());
                userGroupService.update(userGroup);
                updateNum++;
            }


        }
        Map<String, Object> Rst = new HashMap<>();
        Rst.put("updata", updateNum);
        Rst.put("add", addNum);
        return Result.success("导入完成", Rst);
    }
}
