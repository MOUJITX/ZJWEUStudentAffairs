package com.zjweustudent.server.controller;

import com.zjweustudent.server.common.Result;
import com.zjweustudent.server.entity.QgzxTerm;
import com.zjweustudent.server.service.QgzxTermService;
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
 * Function: 勤工助学批次(QgzxTerm)表控制层接口
 * Author: MOUJITX
 * Date: 2023-11-04 21:38:35
 */

@RestController
@RequestMapping("/qgzxTerm")
public class QgzxTermController {

    @Autowired
    QgzxTermService qgzxTermService;

    /*新增数据*/
    @PostMapping("/add")
    public Result add(@RequestBody QgzxTerm qgzxTerm) {
        /* 供参考：唯一性校验*/
        QgzxTerm uniqueCheck = qgzxTermService.selectByPcdm(qgzxTerm.getPcdm());
        if (uniqueCheck != null) {
            return Result.errorWithTitle("操作失败","该批次已存在");
        }


        qgzxTermService.insert(qgzxTerm);
        return Result.success("新增数据成功");
    }


    /*修改数据*/
    @PutMapping("/update")
    public Result update(@RequestBody QgzxTerm qgzxTerm) {
        /* 供参考：唯一性校验*/
        QgzxTerm uniqueCheck = qgzxTermService.selectByPcdm(qgzxTerm.getPcdm());
        QgzxTerm uniqueCheck1 = qgzxTermService.selectById(qgzxTerm.getId());
        if (uniqueCheck != null && !uniqueCheck1.getPcdm().equals(uniqueCheck.getPcdm())) {
            return Result.errorWithTitle("操作失败","该批次已存在");
        }


        qgzxTermService.update(qgzxTerm);
        return Result.success("更新数据成功");
    }

    /*通过主键删除单条数据*/
    @DeleteMapping("/del/{id}")
    public Result delete(@PathVariable Integer id) {
        qgzxTermService.delete(id);
        return Result.success("删除数据成功");
    }

    /*删除多条数据*/
    @DeleteMapping("/del/batch")
    public Result batchDelete(@RequestBody List<Integer> ids) {
        qgzxTermService.batchDelete(ids);
        return Result.success("删除数据成功");
    }

    /*查询全部数据*/
    @GetMapping("/selectAll")
    public Result selectAll() {
        List<QgzxTerm> list = qgzxTermService.selectAll();
        return Result.success(list);
    }

    /*通过ID查询单条数据*/
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        QgzxTerm list = qgzxTermService.selectById(id);
        return Result.success(list);
    }

    /*多条件模糊查询*/
    @PostMapping("/select")
    public Result select(@RequestBody QgzxTerm qgzxTerm) {
        List<QgzxTerm> list = qgzxTermService.select(qgzxTerm);
        return Result.success(list);
    }

    /*分页模糊多条件*/
    @PostMapping("/pageSelect")
    public Result pageSelect(@RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             @RequestBody QgzxTerm qgzxTerm) {
        Map<String, Object> result = new HashMap<>();
        List<QgzxTerm> list = qgzxTermService.pageSelect(pageNum, pageSize, qgzxTerm);
        Integer total = qgzxTermService.pageSelectCount(qgzxTerm);
        Integer totalPage = (int) Math.ceil((double) total / pageSize);
        result.put("list", list);
        result.put("total", total);
        result.put("totalPage", totalPage);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        return Result.success(result);
    }

}
