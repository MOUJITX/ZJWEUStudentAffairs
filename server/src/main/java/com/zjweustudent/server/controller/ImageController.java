package com.zjweustudent.server.controller;

import com.zjweustudent.server.common.GlobalSet;
import com.zjweustudent.server.common.Result;
import com.zjweustudent.server.entity.Image;
import com.zjweustudent.server.service.ImageService;
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
 * Function: 轮播图(Image)表控制层接口
 * Author: MOUJITX
 * Date: 2023-10-08 09:37:12
 */

@RestController
@RequestMapping("/image")
public class ImageController {

    @Autowired
    ImageService imageService;

    /*新增数据*/
    @PostMapping("/add")
    public Result add(@RequestBody Image image) {
        /* 供参考：唯一性校验
        Image uniqueCheck = imageService.selectByXXXX(image .getXXXX());
        if (uniqueCheck != null) {
            return Result.errorWithTitle("操作失败","该XXX已存在");
        }
         */

        imageService.insert(image);
        return Result.success("新增数据成功");
    }


    /*修改数据*/
    @PutMapping("/update")
    public Result update(@RequestBody Image image) {
        /* 供参考：唯一性校验
        Image uniqueCheck = imageService.selectByXXXX(image .getXXXX());
        Image uniqueCheck1 = imageService.selectById(image .getId());
        if (uniqueCheck != null && !uniqueCheck1.getXXXX().equals(uniqueCheck.getXXXX())) {
            return Result.errorWithTitle("操作失败","该XXX已存在");
        }
         */

        imageService.update(image);
        return Result.success("更新数据成功");
    }

    /*通过主键删除单条数据*/
    @DeleteMapping("/del/{id}")
    public Result delete(@PathVariable Integer id) {
        imageService.delete(id);
        return Result.success("删除数据成功");
    }

    /*删除多条数据*/
    @DeleteMapping("/del/batch")
    public Result batchDelete(@RequestBody List<Integer> ids) {
        imageService.batchDelete(ids);
        return Result.success("删除数据成功");
    }

    /*查询全部数据*/
    @GetMapping("/selectAll")
    public Result selectAll() {
        List<Image> list = imageService.selectAll();
        return Result.success(list);
    }

    /*通过ID查询单条数据*/
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Image list = imageService.selectById(id);
        return Result.success(list);
    }

    /*多条件模糊查询*/
    @PostMapping("/select")
    public Result select(@RequestBody Image image) {
        List<Image> list = imageService.select(image);
        return Result.success(list);
    }

    /*分页模糊多条件*/
    @PostMapping("/pageSelect")
    public Result pageSelect(@RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             @RequestBody Image image) {
        Map<String, Object> result = new HashMap<>();
        List<Image> list = imageService.pageSelect(pageNum, pageSize, image);
        Integer total = imageService.pageSelectCount(image);
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
    public Result export(@RequestBody Image image) throws Exception {
        List<Image> list = imageService.select(image);
        GlobalSet globalSet = new GlobalSet();
        String docStorePath = globalSet.getDocStorePath() + "/files/export/";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String formatDate = sdf.format(new Date());
        String UniCodeName = UUID.randomUUID().toString();
        String fileName = "/exportImage_" + formatDate + "_" + UniCodeName + ".xls";
        ExcelWriter writer = ExcelUtil.getWriter(docStorePath + fileName);
        //自定义标题别名
        writer.addHeaderAlias("id", "轮播图ID");
        writer.addHeaderAlias("title", "标题");
        writer.addHeaderAlias("url", "链接");
        writer.addHeaderAlias("img", "轮播图");
        writer.addHeaderAlias("createtime", "创建时间");
        writer.addHeaderAlias("updatetime", "更新时间");

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
            Image image = new Image();

            /* 用于实现数据更新，而非新增
            String XXXXX = row.get(0).toString();
            image .setXXXXX(XXXXX);
            */

            image.setTitle(row.get(0).toString());
            image.setUrl(row.get(1).toString());
            image.setImg(row.get(2).toString());

            /* 用于实现数据更新，而非新增
            Image uniqueConfirm = imageService.selectByXXXXX(XXXXX);
            if (uniqueConfirm == null) {
                imageService.insert(image);
                addNum++;
            } else {
                image .setId(uniqueConfirm.getId());
                imageService.update(image);
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
