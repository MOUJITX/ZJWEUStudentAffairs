package com.zjweustudent.server.controller;

import com.zjweustudent.server.common.GlobalSet;
import com.zjweustudent.server.common.Result;
import com.zjweustudent.server.entity.StuMajor;
import com.zjweustudent.server.service.StuMajorService;
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
 * Function: 专业表(StuMajor)表控制层接口
 * Author: MOUJITX
 * Date: 2023-09-24 21:50:37
 */

@RestController
@RequestMapping("/stuMajor")
public class StuMajorController {

    @Autowired
    StuMajorService stuMajorService;

    /*新增数据*/
    @PostMapping("/add")
    public Result add(@RequestBody StuMajor stuMajor) {
        /* 供参考：唯一性校验*/
        StuMajor uniqueCheck = stuMajorService.selectByZyh(stuMajor.getZyh());
        if (uniqueCheck != null) {
            return Result.errorWithTitle("操作失败","该专业号已存在");
        }


        stuMajorService.insert(stuMajor);
        return Result.success("新增数据成功");
    }


    /*修改数据*/
    @PutMapping("/update")
    public Result update(@RequestBody StuMajor stuMajor) {
        /* 供参考：唯一性校验*/
        StuMajor uniqueCheck = stuMajorService.selectByZyh(stuMajor.getZyh());
        StuMajor uniqueCheck1 = stuMajorService.selectById(stuMajor .getId());
        if (uniqueCheck != null && !uniqueCheck1.getZyh().equals(uniqueCheck.getZyh())) {
            return Result.errorWithTitle("操作失败","该专业号已存在");
        }


        stuMajorService.update(stuMajor);
        return Result.success("更新数据成功");
    }

    /*通过主键删除单条数据*/
    @DeleteMapping("/del/{id}")
    public Result delete(@PathVariable Integer id) {
        stuMajorService.delete(id);
        return Result.success("删除数据成功");
    }

    /*删除多条数据*/
    @DeleteMapping("/del/batch")
    public Result batchDelete(@RequestBody List<Integer> ids) {
        stuMajorService.batchDelete(ids);
        return Result.success("删除数据成功");
    }

    /*查询全部数据*/
    @GetMapping("/selectAll")
    public Result selectAll() {
        List<StuMajor> list = stuMajorService.selectAll();
        return Result.success(list);
    }

    /*通过ID查询单条数据*/
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        StuMajor list = stuMajorService.selectById(id);
        return Result.success(list);
    }

    /*由学院号查询专业*/
    @GetMapping("/selectByCollege/{id}")
    public Result selectByCollege(@PathVariable Integer id){
        List<StuMajor> list = stuMajorService.selectByCollege(id);
        return Result.success(list);
    }

    /*多条件模糊查询*/
    @PostMapping("/select")
    public Result select(@RequestBody StuMajor stuMajor) {
        List<StuMajor> list = stuMajorService.select(stuMajor);
        return Result.success(list);
    }

    /*分页模糊多条件*/
    @PostMapping("/pageSelect")
    public Result pageSelect(@RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             @RequestBody StuMajor stuMajor) {
        Map<String, Object> result = new HashMap<>();
        List<StuMajor> list = stuMajorService.pageSelect(pageNum, pageSize, stuMajor);
        Integer total = stuMajorService.pageSelectCount(stuMajor);
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
    public Result export(@RequestBody StuMajor stuMajor) throws Exception {
        List<StuMajor> list = stuMajorService.select(stuMajor);
        GlobalSet globalSet = new GlobalSet();
        String docStorePath = globalSet.getDocStorePath() + "/files/export/";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String formatDate = sdf.format(new Date());
        String UniCodeName = UUID.randomUUID().toString();
        String fileName = "/exportStuMajor_" + formatDate + "_" + UniCodeName + ".xls";
        ExcelWriter writer = ExcelUtil.getWriter(docStorePath + fileName);
        //自定义标题别名
        writer.addHeaderAlias("xyh", "学院号");
        writer.addHeaderAlias("xym", "学院名");
        writer.addHeaderAlias("zyh", "专业号");
        writer.addHeaderAlias("zym", "专业名");
        writer.addHeaderAlias("cc", "层次");
        writer.addHeaderAlias("lx", "类型");
        writer.addHeaderAlias("xz", "学制");
        writer.addHeaderAlias("zt", "状态");

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
            StuMajor stuMajor = new StuMajor();

            /* 用于实现数据更新，而非新增*/
            String zyh = row.get(2).toString();
            stuMajor.setZyh(zyh);


            stuMajor.setXyh(row.get(0).toString());
            stuMajor.setXym(row.get(1).toString());
            //stuMajor.setZyh(row.get(2).toString());
            stuMajor.setZym(row.get(3).toString());
            stuMajor.setCc(row.get(4).toString());
            stuMajor.setLx(row.get(5).toString());
            stuMajor.setXz(row.get(6).toString());
            stuMajor.setZt(row.get(7).toString());

            /* 用于实现数据更新，而非新增*/
            StuMajor uniqueConfirm = stuMajorService.selectByZyh(zyh);
            if (uniqueConfirm == null) {
                stuMajorService.insert(stuMajor);
                addNum++;
            } else {
                stuMajor .setId(uniqueConfirm.getId());
                stuMajorService.update(stuMajor);
                updateNum++;
            }


        }
        Map<String, Object> Rst = new HashMap<>();
        Rst.put("updata", updateNum);
        Rst.put("add", addNum);
        return Result.success("导入完成", Rst);
    }
}
