package com.zjweustudent.server.controller;

import com.zjweustudent.server.common.GlobalSet;
import com.zjweustudent.server.common.Result;
import com.zjweustudent.server.entity.QgzxDepart;
import com.zjweustudent.server.service.QgzxDepartService;
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
 * Function: 用工部门信息(QgzxDepart)表控制层接口
 * Author: MOUJITX
 * Date: 2023-11-04 21:38:34
 */

@RestController
@RequestMapping("/qgzxDepart")
public class QgzxDepartController {

    @Autowired
    QgzxDepartService qgzxDepartService;

    /*新增数据*/
    @PostMapping("/add")
    public Result add(@RequestBody QgzxDepart qgzxDepart) {
        /* 供参考：唯一性校验*/
        QgzxDepart uniqueCheck = qgzxDepartService.selectByName(qgzxDepart.getName());
        if (uniqueCheck != null) {
            return Result.errorWithTitle("操作失败","该部门已存在");
        }


        qgzxDepartService.insert(qgzxDepart);
        return Result.success("新增数据成功");
    }


    /*修改数据*/
    @PutMapping("/update")
    public Result update(@RequestBody QgzxDepart qgzxDepart) {
        /* 供参考：唯一性校验*/
        QgzxDepart uniqueCheck = qgzxDepartService.selectByName(qgzxDepart .getName());
        QgzxDepart uniqueCheck1 = qgzxDepartService.selectById(qgzxDepart .getId());
        if (uniqueCheck != null && !uniqueCheck1.getName().equals(uniqueCheck.getName())) {
            return Result.errorWithTitle("操作失败","该部门已存在");
        }


        qgzxDepartService.update(qgzxDepart);
        return Result.success("更新数据成功");
    }

    /*通过主键删除单条数据*/
    @DeleteMapping("/del/{id}")
    public Result delete(@PathVariable Integer id) {
        qgzxDepartService.delete(id);
        return Result.success("删除数据成功");
    }

    /*删除多条数据*/
    @DeleteMapping("/del/batch")
    public Result batchDelete(@RequestBody List<Integer> ids) {
        qgzxDepartService.batchDelete(ids);
        return Result.success("删除数据成功");
    }

    /*查询全部数据*/
    @GetMapping("/selectAll")
    public Result selectAll() {
        List<QgzxDepart> list = qgzxDepartService.selectAll();
        return Result.success(list);
    }

    /*通过ID查询单条数据*/
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        QgzxDepart list = qgzxDepartService.selectById(id);
        return Result.success(list);
    }

    /*多条件模糊查询*/
    @PostMapping("/select")
    public Result select(@RequestBody QgzxDepart qgzxDepart) {
        List<QgzxDepart> list = qgzxDepartService.select(qgzxDepart);
        return Result.success(list);
    }

    /*分页模糊多条件*/
    @PostMapping("/pageSelect")
    public Result pageSelect(@RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             @RequestBody QgzxDepart qgzxDepart) {
        Map<String, Object> result = new HashMap<>();
        List<QgzxDepart> list = qgzxDepartService.pageSelect(pageNum, pageSize, qgzxDepart);
        Integer total = qgzxDepartService.pageSelectCount(qgzxDepart);
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
    public Result export(@RequestBody QgzxDepart qgzxDepart) throws Exception {
        List<QgzxDepart> list = qgzxDepartService.select(qgzxDepart);
        GlobalSet globalSet = new GlobalSet();
        String docStorePath = globalSet.getDocStorePath() + "/files/export/";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String formatDate = sdf.format(new Date());
        String UniCodeName = UUID.randomUUID().toString();
        String fileName = "/exportQgzxDepart_" + formatDate + "_" + UniCodeName + ".xls";
        ExcelWriter writer = ExcelUtil.getWriter(docStorePath + fileName);
        //自定义标题别名
        //writer.addHeaderAlias("id", "id");
        writer.addHeaderAlias("name", "部门名称");
        writer.addHeaderAlias("number", "部门编号");
        writer.addHeaderAlias("detail", "开放状态");
        writer.addHeaderAlias("qtlxr", "钱塘校区联系人");
        writer.addHeaderAlias("qtdh", "钱塘校区联系电话");
        writer.addHeaderAlias("qtbgdz", "钱塘校区办公地址");
        writer.addHeaderAlias("qtrs", "钱塘校区岗位人数");
        writer.addHeaderAlias("nxlxr", "南浔校区联系人");
        writer.addHeaderAlias("nxdh", "南浔校区联系电话");
        writer.addHeaderAlias("nxbgdz", "南浔校区办公地址");
        writer.addHeaderAlias("nxrs", "南浔校区岗位人数");
        writer.addHeaderAlias("gwsm", "岗位说明");
        writer.addHeaderAlias("pytj", "聘用条件");

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
            QgzxDepart qgzxDepart = new QgzxDepart();

            /* 用于实现数据更新，而非新增 */
            String name = row.get(0).toString();
            qgzxDepart .setName(name);


            //qgzxDepart.setName(row.get(0).toString());
            qgzxDepart.setNumber(row.get(1).toString());
            qgzxDepart.setDetail(row.get(2).toString());
            qgzxDepart.setQtlxr(row.get(3).toString());
            qgzxDepart.setQtdh(row.get(4).toString());
            qgzxDepart.setQtbgdz(row.get(5).toString());
            if (row.get(6).toString().isEmpty()) qgzxDepart.setQtrs(0); else qgzxDepart.setQtrs(Integer.valueOf(row.get(6).toString()));
            qgzxDepart.setNxlxr(row.get(7).toString());
            qgzxDepart.setNxdh(row.get(8).toString());
            qgzxDepart.setNxbgdz(row.get(9).toString());
            if (row.get(10).toString().isEmpty()) qgzxDepart.setNxrs(0); else qgzxDepart.setNxrs(Integer.valueOf(row.get(10).toString()));
            qgzxDepart.setGwsm(row.get(11).toString());
            qgzxDepart.setPytj(row.get(12).toString());

            /* 用于实现数据更新，而非新增*/
            QgzxDepart uniqueConfirm = qgzxDepartService.selectByName(name);
            if (uniqueConfirm == null) {
                qgzxDepartService.insert(qgzxDepart);
                addNum++;
            } else {
                qgzxDepart .setId(uniqueConfirm.getId());
                qgzxDepartService.update(qgzxDepart);
                updateNum++;
            }


        }
        Map<String, Object> Rst = new HashMap<>();
        Rst.put("updata", updateNum);
        Rst.put("add", addNum);
        return Result.success("导入完成", Rst);
    }
}
