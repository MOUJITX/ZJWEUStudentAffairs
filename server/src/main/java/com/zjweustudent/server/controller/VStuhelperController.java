package com.zjweustudent.server.controller;

import com.zjweustudent.server.common.GlobalSet;
import com.zjweustudent.server.common.Result;
import com.zjweustudent.server.entity.VStuhelper;
import com.zjweustudent.server.service.VStuhelperService;
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
import javax.websocket.server.PathParam;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Blob;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Function: (VStuhelper)表控制层接口
 * Author: MOUJITX
 * Date: 2023-11-07 19:53:38
 */

@RestController
@RequestMapping("/vStuhelper")
public class VStuhelperController {

    @Autowired
    VStuhelperService vStuhelperService;

    /*新增数据*/
    @PostMapping("/add")
    public Result add(@RequestBody VStuhelper vStuhelper) {
        /* 供参考：唯一性校验*/
        VStuhelper uniqueCheck = vStuhelperService.selectByStuInfo(vStuhelper.getXh(),vStuhelper.getXn());
        if (uniqueCheck != null) {
            return Result.errorWithTitle("操作失败","该学生已存在该学年资助认定记录");
        }


        vStuhelperService.insert(vStuhelper);
        return Result.success("新增数据成功");
    }


    /*修改数据*/
    @PutMapping("/update")
    public Result update(@RequestBody VStuhelper vStuhelper) {
        /* 供参考：唯一性校验*/
        VStuhelper uniqueCheck = vStuhelperService.selectByStuInfo(vStuhelper.getXh(),vStuhelper.getXn());
        VStuhelper uniqueCheck1 = vStuhelperService.selectById(vStuhelper .getId());
        if (uniqueCheck != null && !uniqueCheck1.getXh().equals(uniqueCheck.getXh())) {
            return Result.errorWithTitle("操作失败","该学生已存在该学年资助认定记录");
        }


        vStuhelperService.update(vStuhelper);
        return Result.success("更新数据成功");
    }

    /*通过主键删除单条数据*/
    @DeleteMapping("/del/{id}")
    public Result delete(@PathVariable Integer id) {
        vStuhelperService.delete(id);
        return Result.success("删除数据成功");
    }

    /*删除多条数据*/
    @DeleteMapping("/del/batch")
    public Result batchDelete(@RequestBody List<Integer> ids) {
        vStuhelperService.batchDelete(ids);
        return Result.success("删除数据成功");
    }

    /*查询全部数据*/
    @GetMapping("/selectAll")
    public Result selectAll() {
        List<VStuhelper> list = vStuhelperService.selectAll();
        return Result.success(list);
    }

    @GetMapping("/getType")
    public Result getType(@RequestParam(defaultValue = "0") String xh,
                          @RequestParam(defaultValue = "0") String xn) {
        String type = vStuhelperService.getType(xh, xn);
        return Result.success(type);
    }

    /*通过ID查询单条数据*/
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        VStuhelper list = vStuhelperService.selectById(id);
        return Result.success(list);
    }

    /*多条件模糊查询*/
    @PostMapping("/select")
    public Result select(@RequestBody VStuhelper vStuhelper) {
        List<VStuhelper> list = vStuhelperService.select(vStuhelper);
        return Result.success(list);
    }

    /*分页模糊多条件*/
    @PostMapping("/pageSelect")
    public Result pageSelect(@RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             @RequestBody VStuhelper vStuhelper) {
        Map<String, Object> result = new HashMap<>();
        List<VStuhelper> list = vStuhelperService.pageSelect(pageNum, pageSize, vStuhelper);
        Integer total = vStuhelperService.pageSelectCount(vStuhelper);
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
    public Result export(@RequestBody VStuhelper vStuhelper) throws Exception {
        List<VStuhelper> list = vStuhelperService.select(vStuhelper);
        GlobalSet globalSet = new GlobalSet();
        String docStorePath = globalSet.getDocStorePath() + "/files/export/";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String formatDate = sdf.format(new Date());
        String UniCodeName = UUID.randomUUID().toString();
        String fileName = "/exportVStuhelper_" + formatDate + "_" + UniCodeName + ".xls";
        ExcelWriter writer = ExcelUtil.getWriter(docStorePath + fileName);
        //自定义标题别名
        writer.addHeaderAlias("nj", "年级");
        writer.addHeaderAlias("xym", "学院");
        writer.addHeaderAlias("bjm", "班级");
        writer.addHeaderAlias("xm", "姓名");
        writer.addHeaderAlias("xh", "*学号");
        writer.addHeaderAlias("lx", "*认定类别");
        writer.addHeaderAlias("xn", "*认定学年");

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
            VStuhelper vStuhelper = new VStuhelper();

            /* 用于实现数据更新，而非新增*/
            String xh = row.get(4).toString();
            vStuhelper.setXh(xh);
            String xn = row.get(6).toString();
            vStuhelper.setXn(xn);


            vStuhelper.setNj(row.get(0).toString());
            vStuhelper.setXym(row.get(1).toString());
            vStuhelper.setBjm(row.get(2).toString());
            vStuhelper.setXm(row.get(3).toString());
            //vStuhelper.setXh(row.get(4).toString());
            vStuhelper.setLx(row.get(5).toString());
            //vStuhelper.setXn(row.get(6).toString());

            /* 用于实现数据更新，而非新增*/
            VStuhelper uniqueConfirm = vStuhelperService.selectByStuInfo(vStuhelper.getXh(),vStuhelper.getXn());
            if (uniqueConfirm == null) {
                vStuhelperService.insert(vStuhelper);
                addNum++;
            } else {
                vStuhelper .setId(uniqueConfirm.getId());
                vStuhelperService.update(vStuhelper);
                updateNum++;
            }


        }
        Map<String, Object> Rst = new HashMap<>();
        Rst.put("updata", updateNum);
        Rst.put("add", addNum);
        return Result.success("导入完成", Rst);
    }
}
