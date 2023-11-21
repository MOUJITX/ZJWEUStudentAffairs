package com.zjweustudent.server.controller;

import com.zjweustudent.server.common.GlobalSet;
import com.zjweustudent.server.common.Result;
import com.zjweustudent.server.entity.TchInfo;
import com.zjweustudent.server.service.TchInfoService;
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
 * Function: 教师信息表(TchInfo)表控制层接口
 * Author: MOUJITX
 * Date: 2023-09-25 09:18:02
 */

@RestController
@RequestMapping("/tchInfo")
public class TchInfoController {

    @Autowired
    TchInfoService tchInfoService;

    /*新增数据*/
    @PostMapping("/add")
    public Result add(@RequestBody TchInfo tchInfo) {
        /* 供参考：唯一性校验*/
        TchInfo uniqueCheck = tchInfoService.selectByNumber(tchInfo.getNumber());
        if (uniqueCheck != null) {
            return Result.errorWithTitle("操作失败","该XXX已存在");
        }


        tchInfoService.insert(tchInfo);
        return Result.success("新增数据成功");
    }


    /*修改数据*/
    @PutMapping("/update")
    public Result update(@RequestBody TchInfo tchInfo) {
        /* 供参考：唯一性校验*/
        TchInfo uniqueCheck = tchInfoService.selectByNumber(tchInfo.getNumber());
        TchInfo uniqueCheck1 = tchInfoService.selectById(tchInfo .getId());
        if (uniqueCheck != null && !uniqueCheck1.getNumber().equals(uniqueCheck.getNumber())) {
            return Result.errorWithTitle("操作失败","该XXX已存在");
        }


        tchInfoService.update(tchInfo);
        return Result.success("更新数据成功");
    }

    /*通过主键删除单条数据*/
    @DeleteMapping("/del/{id}")
    public Result delete(@PathVariable Integer id) {
        tchInfoService.delete(id);
        return Result.success("删除数据成功");
    }

    /*删除多条数据*/
    @DeleteMapping("/del/batch")
    public Result batchDelete(@RequestBody List<Integer> ids) {
        tchInfoService.batchDelete(ids);
        return Result.success("删除数据成功");
    }

    /*查询全部数据*/
    @GetMapping("/selectAll")
    public Result selectAll() {
        List<TchInfo> list = tchInfoService.selectAll();
        return Result.success(list);
    }

    /*通过ID查询单条数据*/
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        TchInfo list = tchInfoService.selectById(id);
        return Result.success(list);
    }

    /*多条件模糊查询*/
    @PostMapping("/select")
    public Result select(@RequestBody TchInfo tchInfo) {
        List<TchInfo> list = tchInfoService.select(tchInfo);
        return Result.success(list);
    }

    /*分页模糊多条件*/
    @PostMapping("/pageSelect")
    public Result pageSelect(@RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             @RequestBody TchInfo tchInfo) {
        Map<String, Object> result = new HashMap<>();
        List<TchInfo> list = tchInfoService.pageSelect(pageNum, pageSize, tchInfo);
        Integer total = tchInfoService.pageSelectCount(tchInfo);
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
    public Result export(@RequestBody TchInfo tchInfo) throws Exception {
        List<TchInfo> list = tchInfoService.select(tchInfo);
        GlobalSet globalSet = new GlobalSet();
        String docStorePath = globalSet.getDocStorePath() + "/files/export/";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String formatDate = sdf.format(new Date());
        String UniCodeName = UUID.randomUUID().toString();
        String fileName = "/exportTchInfo_" + formatDate + "_" + UniCodeName + ".xls";
        ExcelWriter writer = ExcelUtil.getWriter(docStorePath + fileName);
        //自定义标题别名
        writer.addHeaderAlias("number", "工号");
        writer.addHeaderAlias("name", "姓名");
        writer.addHeaderAlias("sex", "性别");
        writer.addHeaderAlias("phone", "联系电话");
        writer.addHeaderAlias("department", "学院");
        writer.addHeaderAlias("departname", "学院名称");
        writer.addHeaderAlias("avatar", "照片");

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
            TchInfo tchInfo = new TchInfo();

            /* 用于实现数据更新，而非新增*/
            String number = row.get(0).toString();
            tchInfo.setNumber(number);


            //tchInfo.setNumber(row.get(0).toString());
            tchInfo.setName(row.get(1).toString());
            tchInfo.setSex(row.get(2).toString());
            tchInfo.setPhone(row.get(3).toString());
            tchInfo.setDepartment(row.get(4).toString());
            tchInfo.setDepartname(row.get(5).toString());
            tchInfo.setAvatar(row.get(6).toString());

            /* 用于实现数据更新，而非新增*/
            TchInfo uniqueConfirm = tchInfoService.selectByNumber(number);
            if (uniqueConfirm == null) {
                tchInfoService.insert(tchInfo);
                addNum++;
            } else {
                tchInfo .setId(uniqueConfirm.getId());
                tchInfoService.update(tchInfo);
                updateNum++;
            }


        }
        Map<String, Object> Rst = new HashMap<>();
        Rst.put("updata", updateNum);
        Rst.put("add", addNum);
        return Result.success("导入完成", Rst);
    }
}
