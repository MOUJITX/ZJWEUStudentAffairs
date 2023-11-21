package com.zjweustudent.server.controller;

import com.zjweustudent.server.common.GlobalSet;
import com.zjweustudent.server.common.Result;
import com.zjweustudent.server.entity.Semesters;
import com.zjweustudent.server.service.SemestersService;
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
 * Function: 学年学期设置(Semesters)表控制层接口
 * Author: MOUJITX
 * Date: 2023-09-25 10:32:42
 */

@RestController
@RequestMapping("/semesters")
public class SemestersController {

    @Autowired
    SemestersService semestersService;

    /*新增数据*/
    @PostMapping("/add")
    public Result add(@RequestBody Semesters semesters) {
        /* 供参考：唯一性校验
        Semesters uniqueCheck = semestersService.selectByXXXX(semesters .getXXXX());
        if (uniqueCheck != null) {
            return Result.errorWithTitle("操作失败","该XXX已存在");
        }
         */

        semestersService.insert(semesters);
        return Result.success("新增数据成功");
    }


    /*修改数据*/
    @PutMapping("/update")
    public Result update(@RequestBody Semesters semesters) {
        /* 供参考：唯一性校验
        Semesters uniqueCheck = semestersService.selectByXXXX(semesters .getXXXX());
        Semesters uniqueCheck1 = semestersService.selectById(semesters .getId());
        if (uniqueCheck != null && !uniqueCheck1.getXXXX().equals(uniqueCheck.getXXXX())) {
            return Result.errorWithTitle("操作失败","该XXX已存在");
        }
         */

        semestersService.update(semesters);
        return Result.success("更新数据成功");
    }

    /*通过主键删除单条数据*/
    @DeleteMapping("/del/{id}")
    public Result delete(@PathVariable Integer id) {
        semestersService.delete(id);
        return Result.success("删除数据成功");
    }

    /*删除多条数据*/
    @DeleteMapping("/del/batch")
    public Result batchDelete(@RequestBody List<Integer> ids) {
        semestersService.batchDelete(ids);
        return Result.success("删除数据成功");
    }

    /*查询全部数据*/
    @GetMapping("/selectAll")
    public Result selectAll() {
        List<Semesters> list = semestersService.selectAll();
        return Result.success(list);
    }

    @GetMapping("/selectAllYear")
    public Result selectAllYear() {
        List<Semesters> list = semestersService.selectAllYear();
        return Result.success(list);
    }

    /*通过ID查询单条数据*/
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Semesters list = semestersService.selectById(id);
        return Result.success(list);
    }

    /*多条件模糊查询*/
    @PostMapping("/select")
    public Result select(@RequestBody Semesters semesters) {
        List<Semesters> list = semestersService.select(semesters);
        return Result.success(list);
    }

    /*分页模糊多条件*/
    @PostMapping("/pageSelect")
    public Result pageSelect(@RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             @RequestBody Semesters semesters) {
        Map<String, Object> result = new HashMap<>();
        List<Semesters> list = semestersService.pageSelect(pageNum, pageSize, semesters);
        Integer total = semestersService.pageSelectCount(semesters);
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
    public Result export(@RequestBody Semesters semesters) throws Exception {
        List<Semesters> list = semestersService.select(semesters);
        GlobalSet globalSet = new GlobalSet();
        String docStorePath = globalSet.getDocStorePath() + "/files/export/";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String formatDate = sdf.format(new Date());
        String UniCodeName = UUID.randomUUID().toString();
        String fileName = "/exportSemesters_" + formatDate + "_" + UniCodeName + ".xls";
        ExcelWriter writer = ExcelUtil.getWriter(docStorePath + fileName);
        //自定义标题别名
        writer.addHeaderAlias("starttime", "开始周日期");
        writer.addHeaderAlias("endtime", "结束周日期");
        writer.addHeaderAlias("schoolyear", "学年");
        writer.addHeaderAlias("semester", "学期");

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
            Semesters semesters = new Semesters();

            /* 用于实现数据更新，而非新增
            String XXXXX = row.get(0).toString();
            semesters .setXXXXX(XXXXX);
            */

            semesters.setStarttime((Date) row.get(0));
            semesters.setEndtime((Date) row.get(1));
            semesters.setSchoolyear(row.get(2).toString());
            semesters.setSemester(row.get(3).toString());

            /* 用于实现数据更新，而非新增
            Semesters uniqueConfirm = semestersService.selectByXXXXX(XXXXX);
            if (uniqueConfirm == null) {
                semestersService.insert(semesters);
                addNum++;
            } else {
                semesters .setId(uniqueConfirm.getId());
                semestersService.update(semesters);
                updateNum++;
            }
            */

        }
        Map<String, Object> Rst = new HashMap<>();
        Rst.put("updata", updateNum);
        Rst.put("add", addNum);
        return Result.success("导入完成", Rst);
    }
}
