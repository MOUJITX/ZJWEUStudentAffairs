package com.zjweustudent.server.controller;

import com.zjweustudent.server.common.GlobalSet;
import com.zjweustudent.server.common.Result;
import com.zjweustudent.server.entity.Notices;
import com.zjweustudent.server.service.NoticesService;
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
 * Function: 通知公告(Notices)表控制层接口
 * Author: MOUJITX
 * Date: 2023-10-08 09:37:32
 */

@RestController
@RequestMapping("/notices")
public class NoticesController {

    @Autowired
    NoticesService noticesService;

    /*新增数据*/
    @PostMapping("/add")
    public Result add(@RequestBody Notices notices) {
        /* 供参考：唯一性校验
        Notices uniqueCheck = noticesService.selectByXXXX(notices .getXXXX());
        if (uniqueCheck != null) {
            return Result.errorWithTitle("操作失败","该XXX已存在");
        }
         */

        noticesService.insert(notices);
        return Result.success("新增数据成功");
    }


    /*修改数据*/
    @PutMapping("/update")
    public Result update(@RequestBody Notices notices) {
        /* 供参考：唯一性校验
        Notices uniqueCheck = noticesService.selectByXXXX(notices .getXXXX());
        Notices uniqueCheck1 = noticesService.selectById(notices .getId());
        if (uniqueCheck != null && !uniqueCheck1.getXXXX().equals(uniqueCheck.getXXXX())) {
            return Result.errorWithTitle("操作失败","该XXX已存在");
        }
         */

        noticesService.update(notices);
        return Result.success("更新数据成功");
    }

    /*通过主键删除单条数据*/
    @DeleteMapping("/del/{id}")
    public Result delete(@PathVariable Integer id) {
        noticesService.delete(id);
        return Result.success("删除数据成功");
    }

    /*删除多条数据*/
    @DeleteMapping("/del/batch")
    public Result batchDelete(@RequestBody List<Integer> ids) {
        noticesService.batchDelete(ids);
        return Result.success("删除数据成功");
    }

    /*查询全部数据*/
    @GetMapping("/selectAll")
    public Result selectAll() {
        List<Notices> list = noticesService.selectAll();
        return Result.success(list);
    }

    /*通过ID查询单条数据*/
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Notices list = noticesService.selectById(id);
        return Result.success(list);
    }

    /*多条件模糊查询*/
    @PostMapping("/select")
    public Result select(@RequestBody Notices notices) {
        List<Notices> list = noticesService.select(notices);
        return Result.success(list);
    }

    /*分页模糊多条件*/
    @PostMapping("/pageSelect")
    public Result pageSelect(@RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             @RequestBody Notices notices) {
        Map<String, Object> result = new HashMap<>();
        List<Notices> list = noticesService.pageSelect(pageNum, pageSize, notices);
        Integer total = noticesService.pageSelectCount(notices);
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
    public Result export(@RequestBody Notices notices) throws Exception {
        List<Notices> list = noticesService.select(notices);
        GlobalSet globalSet = new GlobalSet();
        String docStorePath = globalSet.getDocStorePath() + "/files/export/";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String formatDate = sdf.format(new Date());
        String UniCodeName = UUID.randomUUID().toString();
        String fileName = "/exportNotices_" + formatDate + "_" + UniCodeName + ".xls";
        ExcelWriter writer = ExcelUtil.getWriter(docStorePath + fileName);
        //自定义标题别名
        writer.addHeaderAlias("title", "标题");
        writer.addHeaderAlias("top", "置顶");
        writer.addHeaderAlias("publish", "发布状态");
        writer.addHeaderAlias("date", "发布日期");
        writer.addHeaderAlias("source", "发布位置");
        writer.addHeaderAlias("author", "作者");
        writer.addHeaderAlias("detail", "内容");
        writer.addHeaderAlias("simple", "概要");
        writer.addHeaderAlias("adduser", "发布人");
        writer.addHeaderAlias("edituser", "最后修改人");

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
            Notices notices = new Notices();

            /* 用于实现数据更新，而非新增
            String XXXXX = row.get(0).toString();
            notices .setXXXXX(XXXXX);
            */

            notices.setTitle(row.get(0).toString());
            notices.setTop(row.get(1).toString());
            notices.setPublish(row.get(2).toString());
            notices.setDate((Date) row.get(3));
            notices.setSource(row.get(4).toString());
            notices.setAuthor(row.get(5).toString());
            notices.setDetail(row.get(6).toString());
            notices.setSimple(row.get(7).toString());
            notices.setAdduser(row.get(8).toString());
            notices.setEdituser(row.get(9).toString());

            /* 用于实现数据更新，而非新增
            Notices uniqueConfirm = noticesService.selectByXXXXX(XXXXX);
            if (uniqueConfirm == null) {
                noticesService.insert(notices);
                addNum++;
            } else {
                notices .setId(uniqueConfirm.getId());
                noticesService.update(notices);
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
