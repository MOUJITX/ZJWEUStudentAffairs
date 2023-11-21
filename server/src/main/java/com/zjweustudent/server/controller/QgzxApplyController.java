package com.zjweustudent.server.controller;

import com.zjweustudent.server.common.GlobalSet;
import com.zjweustudent.server.common.Result;
import com.zjweustudent.server.entity.QgzxApply;
import com.zjweustudent.server.service.QgzxApplyService;
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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Blob;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Function: 勤工申请(QgzxApply)表控制层接口
 * Author: MOUJITX
 * Date: 2023-11-04 21:38:32
 */

@RestController
@RequestMapping("/qgzxApply")
public class QgzxApplyController {

    @Autowired
    QgzxApplyService qgzxApplyService;

    /*新增数据*/
    @PostMapping("/add")
    public Result add(@RequestBody QgzxApply qgzxApply) {
        /* 供参考：唯一性校验*/
        QgzxApply uniqueCheck = qgzxApplyService.selectByStuApplyInfo(qgzxApply.getUsername(),qgzxApply.getSqxn());
        if (uniqueCheck != null) {
            return Result.errorWithTitle("操作失败","当前学年已存在该学生申请记录");
        }


        qgzxApplyService.insert(qgzxApply);
        return Result.success("新增数据成功");
    }


    /*修改数据*/
    @PutMapping("/update")
    public Result update(@RequestBody QgzxApply qgzxApply) {
        /* 供参考：唯一性校验*/
        QgzxApply uniqueCheck = qgzxApplyService.selectByStuApplyInfo(qgzxApply.getUsername(),qgzxApply.getSqxn());
        QgzxApply uniqueCheck1 = qgzxApplyService.selectById(qgzxApply.getId());
        if (uniqueCheck != null && !uniqueCheck1.getUsername().equals(uniqueCheck.getUsername())) {
            return Result.errorWithTitle("操作失败","当前学年已存在该学生申请记录");
        }


        qgzxApplyService.update(qgzxApply);
        return Result.success("更新数据成功");
    }

    /*修改审核状态信息*/
    @GetMapping("/setSpeed")
    public Result setSpeed(@RequestParam Integer id,
                           @RequestParam String speed,
                           @RequestParam String rtime,
                           @RequestParam String rname,
                           @RequestParam String rnum){
        qgzxApplyService.setSpeed(id,speed,rtime,rname,rnum);
        return Result.success("已审核");
    }

    /*批量修改*/
    @PostMapping("/setSpeedBatch")
    public Result setSpeedBatch(@RequestParam String speed,
                                @RequestBody List<Integer> ids, String rtime, String rname, String rnum) {
        qgzxApplyService.setSpeedBatch(speed,ids,rtime,rname,rnum);
        return Result.success("修改数据成功");
    }

    /*通过主键删除单条数据*/
    @DeleteMapping("/del/{id}")
    public Result delete(@PathVariable Integer id) {
        qgzxApplyService.delete(id);
        return Result.success("删除数据成功");
    }

    /*删除多条数据*/
    @DeleteMapping("/del/batch")
    public Result batchDelete(@RequestBody List<Integer> ids) {
        qgzxApplyService.batchDelete(ids);
        return Result.success("删除数据成功");
    }

    /*查询全部数据*/
    @GetMapping("/selectAll")
    public Result selectAll() {
        List<QgzxApply> list = qgzxApplyService.selectAll();
        return Result.success(list);
    }



    /*查询审批通过名单*/
    @GetMapping("/selectFinish/{pcdm}")
    public Result selectFinish(@PathVariable String pcdm) {
        List<QgzxApply> list = qgzxApplyService.selectFinish(pcdm);
        return Result.success(list);
    }


    /*查询相应报名部门数据（审批通过*/
    @GetMapping("/selectFinishByBM")
    public Result selectFinishByBM(@RequestParam String pcdm, @RequestParam String bm) {
        List<QgzxApply> list = qgzxApplyService.selectFinishByBM(pcdm,bm);
        return Result.success(list);
    }

    /*通过ID查询单条数据*/
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        QgzxApply list = qgzxApplyService.selectById(id);
        return Result.success(list);
    }

    /*通过学号查询数据*/
    @GetMapping("/selectByNum/{number}")
    public Result selectByNum(@PathVariable String number) {
        List<QgzxApply> list = qgzxApplyService.selectByNum(number);
        return Result.success(list);
    }

    /*多条件模糊查询*/
    @PostMapping("/select")
    public Result select(@RequestBody QgzxApply qgzxApply) {
        List<QgzxApply> list = qgzxApplyService.select(qgzxApply);
        return Result.success(list);
    }

    /*分页模糊多条件*/
    @PostMapping("/pageSelect")
    public Result pageSelect(@RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             @RequestBody QgzxApply qgzxApply) {
        Map<String, Object> result = new HashMap<>();
        List<QgzxApply> list = qgzxApplyService.pageSelect(pageNum, pageSize, qgzxApply);
        Integer total = qgzxApplyService.pageSelectCount(qgzxApply);
        Integer totalPage = (int) Math.ceil((double) total / pageSize);
        result.put("list", list);
        result.put("total", total);
        result.put("totalPage", totalPage);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        return Result.success(result);
    }

    /*导出PDF*/
    @PostMapping("/pdf")
    public Result getPDF(@RequestBody Integer id, HttpServletResponse response) throws UnsupportedEncodingException {
        QgzxApply qgzxApply = qgzxApplyService.selectById(id);
        if (qgzxApply.getSpeed().equals("finish")) {
            String fileName = qgzxApplyService.getPDF(qgzxApply, response);
            return Result.success("申请表生成成功，正在下载", fileName);
        } else {
            return Result.errorWithTitle("申请表生成失败","当前申请暂未通过学院审核。");
        }
    }





    /*下载*/
    @PostMapping("/export")
    public Result export(@RequestBody QgzxApply qgzxApply) throws Exception {
        List<QgzxApply> list = qgzxApplyService.select(qgzxApply);
        GlobalSet globalSet = new GlobalSet();
        String docStorePath = globalSet.getDocStorePath() + "/files/export/";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String formatDate = sdf.format(new Date());
        String UniCodeName = UUID.randomUUID().toString();
        String fileName = "/exportQgzxApply_" + formatDate + "_" + UniCodeName + ".xls";
        ExcelWriter writer = ExcelUtil.getWriter(docStorePath + fileName);
        //自定义标题别名
        //writer.addHeaderAlias("id", "id");
        writer.addHeaderAlias("sqxn", "申请学年");
        writer.addHeaderAlias("speed", "当前进度");
        writer.addHeaderAlias("name", "姓名");
        writer.addHeaderAlias("username", "学号");
        writer.addHeaderAlias("male", "性别");
        writer.addHeaderAlias("campus", "校区");
        writer.addHeaderAlias("grade", "年级");
        writer.addHeaderAlias("college", "学院");
        writer.addHeaderAlias("classname", "班级");
        writer.addHeaderAlias("phone", "联系电话");
        writer.addHeaderAlias("qq", "qq");
        writer.addHeaderAlias("type", "资助对象");
        writer.addHeaderAlias("skill", "个人技能");
        writer.addHeaderAlias("depart", "申请岗位");
        writer.addHeaderAlias("departb", "申请岗位2");
        writer.addHeaderAlias("choosetj", "是否调剂");
        writer.addHeaderAlias("lastdepart", "往年勤工经历");
        writer.addHeaderAlias("timea", "课余时间（周一）");
        writer.addHeaderAlias("timeb", "课余时间（周二）");
        writer.addHeaderAlias("timec", "课余时间（周三）");
        writer.addHeaderAlias("timed", "课余时间（周四）");
        writer.addHeaderAlias("timee", "课余时间（周五）");
        writer.addHeaderAlias("timef", "课余时间（周六）");
        writer.addHeaderAlias("timeg", "课余时间（周日）");
        writer.addHeaderAlias("uptime", "提交时间");
        writer.addHeaderAlias("replytime", "审核时间");
        writer.addHeaderAlias("replyname", "审核人");
        writer.addHeaderAlias("replynum", "审核人账号");

        // 一次性写出list内的对象到excel，使用默认样式，强制输出标题
        writer.setOnlyAlias(true);  //只导出设置了别名的字段
        writer.write(list, true);
        writer.close();

        return Result.success("导出成功", fileName);
    }

    /*上传*/
    /*
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
            QgzxApply qgzxApply = new QgzxApply();

            //用于实现数据更新，而非新增
            String XXXXX = row.get(0).toString();
            qgzxApply .setXXXXX(XXXXX);


            qgzxApply.setId(row.get(1).toString());
            qgzxApply.setSqxn(row.get(1).toString());
            qgzxApply.setSpeed(row.get(1).toString());
            qgzxApply.setName(row.get(1).toString());
            qgzxApply.setUsername(row.get(1).toString());
            qgzxApply.setMale(row.get(1).toString());
            qgzxApply.setCollege(row.get(1).toString());
            qgzxApply.setClassname(row.get(1).toString());
            qgzxApply.setPhone(row.get(1).toString());
            qgzxApply.setQq(row.get(1).toString());
            qgzxApply.setType(row.get(1).toString());
            qgzxApply.setSkill(row.get(1).toString());
            qgzxApply.setDepart(row.get(1).toString());
            qgzxApply.setDepartb(row.get(1).toString());
            qgzxApply.setChoosetj(row.get(1).toString());
            qgzxApply.setLastdepart(row.get(1).toString());
            qgzxApply.setTimea(row.get(1).toString());
            qgzxApply.setUptime(row.get(1).toString());
            qgzxApply.setReply(row.get(1).toString());
            qgzxApply.setReplychoose(row.get(1).toString());
            qgzxApply.setReplytime(row.get(1).toString());
            qgzxApply.setReplyname(row.get(1).toString());
            qgzxApply.setReplynum(row.get(1).toString());

            // 用于实现数据更新，而非新增
            QgzxApply uniqueConfirm = qgzxApplyService.selectByXXXXX(XXXXX);
            if (uniqueConfirm == null) {
                qgzxApplyService.insert(qgzxApply);
                addNum++;
            } else {
                qgzxApply .setId(uniqueConfirm.getId());
                qgzxApplyService.update(qgzxApply);
                updateNum++;
            }


        }
        Map<String, Object> Rst = new HashMap<>();
        Rst.put("updata", updateNum);
        Rst.put("add", addNum);
        return Result.success("导入完成", Rst);
    }
    */
}
