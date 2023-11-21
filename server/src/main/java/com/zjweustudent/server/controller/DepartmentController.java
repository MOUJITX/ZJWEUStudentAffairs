package com.zjweustudent.server.controller;

import com.zjweustudent.server.common.GlobalSet;
import com.zjweustudent.server.common.Result;
import com.zjweustudent.server.entity.Department;
import com.zjweustudent.server.service.DepartmentService;
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
 * Function: 学院/部门表(Department)表控制层接口
 * Author: MOUJITX
 * Date: 2023-09-24 21:26:00
 */

@RestController
@RequestMapping("/department")
public class DepartmentController {

    @Autowired
    DepartmentService departmentService;

    /*新增数据*/
    @PostMapping("/add")
    public Result add(@RequestBody Department department) {
        /* 供参考：唯一性校验*/
        Department uniqueCheck = departmentService.selectByDepartid(department.getDepartid());
        if (uniqueCheck != null) {
            return Result.errorWithTitle("操作失败","该部门id已存在");
        }


        departmentService.insert(department);
        return Result.success("新增数据成功");
    }


    /*修改数据*/
    @PutMapping("/update")
    public Result update(@RequestBody Department department) {
        /* 供参考：唯一性校验*/
        Department uniqueCheck = departmentService.selectByDepartid(department.getDepartid());
        Department uniqueCheck1 = departmentService.selectById(department .getId());
        if (uniqueCheck != null && !uniqueCheck1.getDepartid().equals(uniqueCheck.getDepartid())) {
            return Result.errorWithTitle("操作失败","该部门id已存在");
        }


        departmentService.update(department);
        return Result.success("更新数据成功");
    }

    /*通过主键删除单条数据*/
    @DeleteMapping("/del/{id}")
    public Result delete(@PathVariable Integer id) {
        departmentService.delete(id);
        return Result.success("删除数据成功");
    }

    /*删除多条数据*/
    @DeleteMapping("/del/batch")
    public Result batchDelete(@RequestBody List<Integer> ids) {
        departmentService.batchDelete(ids);
        return Result.success("删除数据成功");
    }

    /*查询全部数据*/
    @GetMapping("/selectAll")
    public Result selectAll() {
        List<Department> list = departmentService.selectAll();
        return Result.success(list);
    }

    /*查询全部可用数据*/
    @GetMapping("/selectAllAvailable")
    public Result selectAllAvailable() {
        List<Department> list = departmentService.selectAllAvailable();
        return Result.success(list);
    }

    /*通过ID查询单条数据*/
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Department list = departmentService.selectById(id);
        return Result.success(list);
    }

    /*获取教学机构*/
    @GetMapping("/selectCollege")
    public Result selectCollege() {
        List<Department> list = departmentService.selectCollege();
        return Result.success(list);
    }

    /*多条件模糊查询*/
    @PostMapping("/select")
    public Result select(@RequestBody Department department) {
        List<Department> list = departmentService.select(department);
        return Result.success(list);
    }

    /*分页模糊多条件*/
    @PostMapping("/pageSelect")
    public Result pageSelect(@RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             @RequestBody Department department) {
        Map<String, Object> result = new HashMap<>();
        List<Department> list = departmentService.pageSelect(pageNum, pageSize, department);
        Integer total = departmentService.pageSelectCount(department);
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
    public Result export(@RequestBody Department department) throws Exception {
        List<Department> list = departmentService.select(department);
        
        GlobalSet globalSet = new GlobalSet();
        String docStorePath = globalSet.getDocStorePath() + "/files/export/";
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String formatDate = sdf.format(new Date());
        String UniCodeName = UUID.randomUUID().toString();
        String fileName = "/exportDepartment_" + formatDate + "_" + UniCodeName + ".xls";
        ExcelWriter writer = ExcelUtil.getWriter(docStorePath + fileName);
        //自定义标题别名
        writer.addHeaderAlias("departid", "部门编号");
        writer.addHeaderAlias("departname", "部门名称");
        writer.addHeaderAlias("type", "类型");
        writer.addHeaderAlias("state", "状态");
        writer.addHeaderAlias("college", "教学机构");
        writer.addHeaderAlias("ranknum", "显示顺序");

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
            Department department = new Department();

            /* 用于实现数据更新，而非新增*/
            String departid = row.get(0).toString();
            department.setDepartid(departid);


            //department.setDepartid(row.get(0).toString());
            department.setDepartname(row.get(1).toString());
            department.setType(row.get(2).toString());
            department.setState(row.get(3).toString());
            department.setCollege(row.get(4).toString());
            department.setRanknum(Integer.valueOf(row.get(5).toString()));

            /* 用于实现数据更新，而非新增 */
            Department uniqueConfirm = departmentService.selectByDepartid(departid);
            if (uniqueConfirm == null) {
                departmentService.insert(department);
                addNum++;
            } else {
                department .setId(uniqueConfirm.getId());
                departmentService.update(department);
                updateNum++;
            }


        }
        Map<String, Object> Rst = new HashMap<>();
        Rst.put("updata", updateNum);
        Rst.put("add", addNum);
        return Result.success("导入完成", Rst);
    }
}
