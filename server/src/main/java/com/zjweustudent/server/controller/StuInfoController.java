package com.zjweustudent.server.controller;

import com.zjweustudent.server.common.GlobalSet;
import com.zjweustudent.server.common.Result;
import com.zjweustudent.server.entity.StuClass;
import com.zjweustudent.server.entity.StuInfo;
import com.zjweustudent.server.entity.Users;
import com.zjweustudent.server.service.StuInfoService;
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
 * Function: 学生信息表(StuInfo)表控制层接口
 * Author: MOUJITX
 * Date: 2023-09-22 13:23:26
 */

@RestController
@RequestMapping("/stuInfo")
public class StuInfoController {

    @Autowired
    StuInfoService stuInfoService;

    /*新增数据*/
    @PostMapping("/add")
    public Result add(@RequestBody StuInfo stuInfo) {
        /* 唯一性校验*/
        StuInfo uniqueCheck = stuInfoService.selectByNumber(stuInfo.getNumber());
        if (uniqueCheck != null) {
            return Result.errorWithTitle("操作失败","该学号已存在");
        }

        stuInfoService.insert(stuInfo);
        return Result.success("新增数据成功");
    }


    /*修改数据*/
    @PutMapping("/update")
    public Result update(@RequestBody StuInfo stuInfo) {
        /* 唯一性校验*/
        StuInfo uniqueCheck = stuInfoService.selectByNumber(stuInfo.getNumber());
        StuInfo uniqueCheck1 = stuInfoService.selectById(stuInfo.getId());
        if (uniqueCheck != null && !uniqueCheck1.getNumber().equals(uniqueCheck.getNumber())) {
            return Result.errorWithTitle("操作失败","该学号已存在");
        }

        stuInfoService.update(stuInfo);
        return Result.success("更新数据成功");
    }

    /*通过主键删除单条数据*/
    @DeleteMapping("/del/{id}")
    public Result delete(@PathVariable Integer id) {
        stuInfoService.delete(id);
        return Result.success("删除数据成功");
    }

    /*删除多条数据*/
    @DeleteMapping("/del/batch")
    public Result batchDelete(@RequestBody List<Integer> ids) {
        stuInfoService.batchDelete(ids);
        return Result.success("删除数据成功");
    }

    /*查询全部数据*/
    @GetMapping("/selectAll")
    public Result selectAll() {
        List<StuInfo> list = stuInfoService.selectAll();
        return Result.success(list);
    }

    /*通过学院或专业或班级查询学生名单*/
    @GetMapping("/selectByCMC")
    public Result selectByCMC(@RequestParam String collegeNum,
                             @RequestParam String majorNum,
                              @RequestParam String classNum){
        List<StuInfo> list = stuInfoService.selectByCMC(collegeNum,majorNum,classNum);
        return Result.success(list);
    }

    /*通过ID查询单条数据*/
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        StuInfo list = stuInfoService.selectById(id);
        return Result.success(list);
    }

    /*查：按number*/
    @GetMapping("/selectByNumber/{number}")
    public Result selectByNumber(@PathVariable String number){
        StuInfo stuInfoList = stuInfoService.selectByNumber(number);
        return Result.success(stuInfoList);
    }
    /*多条件模糊查询*/
    @PostMapping("/select")
    public Result select(@RequestBody StuInfo stuInfo) {
        List<StuInfo> list = stuInfoService.select(stuInfo);
        return Result.success(list);
    }

    /*分页模糊多条件*/
    @PostMapping("/pageSelect")
    public Result pageSelect(@RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             @RequestBody StuInfo stuInfo) {
        Map<String, Object> result = new HashMap<>();
        List<StuInfo> list = stuInfoService.pageSelect(pageNum, pageSize, stuInfo);
        Integer total = stuInfoService.pageSelectCount(stuInfo);
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
    public Result export(@RequestBody StuInfo stuInfo) throws Exception {
        List<StuInfo> list = stuInfoService.select(stuInfo);
        GlobalSet globalSet = new GlobalSet();
        String docStorePath = globalSet.getDocStorePath() + "/files/export/";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String formatDate = sdf.format(new Date());
        String UniCodeName = UUID.randomUUID().toString();
        String fileName = "/exportStuInfo_" + formatDate + "_" + UniCodeName + ".xls";
        ExcelWriter writer = ExcelUtil.getWriter(docStorePath + fileName);
        //自定义标题别名
        writer.addHeaderAlias("number", "学工号");
        writer.addHeaderAlias("name", "姓名");
        writer.addHeaderAlias("sex", "性别");
        writer.addHeaderAlias("phone", "联系电话");
        writer.addHeaderAlias("idcard", "身份证号");
        writer.addHeaderAlias("college", "学院号");
        writer.addHeaderAlias("collegename", "学院名");
        writer.addHeaderAlias("major", "专业号");
        writer.addHeaderAlias("majorname", "专业名");
        writer.addHeaderAlias("classnum", "班级号");
        writer.addHeaderAlias("classname", "班级名");
        writer.addHeaderAlias("campus", "校区");

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
            StuInfo stuInfo = new StuInfo();

            /* 用于实现数据更新，而非新增*/
            String username = row.get(0).toString();
            stuInfo.setNumber(username);


            stuInfo.setName(row.get(1).toString());
            stuInfo.setSex(row.get(2).toString());
            stuInfo.setPhone(row.get(3).toString());
            stuInfo.setIdcard(row.get(4).toString());
            stuInfo.setCollege(row.get(5).toString());
            stuInfo.setCollegename(row.get(6).toString());
            stuInfo.setMajor(row.get(7).toString());
            stuInfo.setMajorname(row.get(8).toString());
            stuInfo.setClassnum(row.get(9).toString());
            stuInfo.setClassname(row.get(10).toString());
            stuInfo.setCampus(row.get(11).toString());

            /* 用于实现数据更新，而非新增*/
            StuInfo uniqueConfirm = stuInfoService.selectByNumber(username);
            if (uniqueConfirm == null) {
                stuInfoService.insert(stuInfo);
                addNum++;
            } else {
                stuInfo.setId(uniqueConfirm.getId());
                stuInfoService.update(stuInfo);
                updateNum++;
            }


        }
        Map<String, Object> Rst = new HashMap<>();
        Rst.put("updata", updateNum);
        Rst.put("add", addNum);
        return Result.success("导入完成", Rst);
    }
}
