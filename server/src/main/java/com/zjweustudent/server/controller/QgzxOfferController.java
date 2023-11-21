package com.zjweustudent.server.controller;

import com.zjweustudent.server.common.AuthAccess;
import com.zjweustudent.server.common.GlobalSet;
import com.zjweustudent.server.common.Result;
import com.zjweustudent.server.entity.QgzxApply;
import com.zjweustudent.server.entity.QgzxOffer;
import com.zjweustudent.server.entity.QgzxOfferCount;
import com.zjweustudent.server.entity.QgzxTerm;
import com.zjweustudent.server.service.QgzxApplyService;
import com.zjweustudent.server.service.QgzxOfferService;
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
 * Function: (QgzxOffer)表控制层接口
 * Author: MOUJITX
 * Date: 2023-11-13 14:46:23
 */

@RestController
@RequestMapping("/qgzxOffer")
public class QgzxOfferController {

    @Autowired
    QgzxOfferService qgzxOfferService;

    @Autowired
    QgzxApplyService qgzxApplyService;

    @Autowired
    QgzxTermService qgzxTermService;

    /*新增数据*/
    @PostMapping("/add")
    public Result add(@RequestBody QgzxOffer qgzxOffer) {
        /* 供参考：唯一性校验*/
        QgzxOffer uniqueCheck = qgzxOfferService.selectByXHPC(qgzxOffer.getXh(),qgzxOffer.getPc());
        if (uniqueCheck != null) {
            return Result.errorWithTitle("操作失败","该学生在该学年批次中已被【"+uniqueCheck.getBm()+"】录用");
        }

        qgzxOfferService.insert(qgzxOffer);
        return Result.success("新增数据成功");
    }


    /*修改数据*/
    @PutMapping("/update")
    public Result update(@RequestBody QgzxOffer qgzxOffer) {
        /* 供参考：唯一性校验*/
        QgzxOffer uniqueCheck = qgzxOfferService.selectByXHPC(qgzxOffer.getXh(),qgzxOffer.getPc());
        QgzxOffer uniqueCheck1 = qgzxOfferService.selectById(qgzxOffer.getId());
        if (uniqueCheck != null && !uniqueCheck1.getXh().equals(uniqueCheck.getXh())) {
            return Result.errorWithTitle("操作失败","该学生在该学年批次中已被【"+uniqueCheck.getBm()+"】录用");
        }


        qgzxOfferService.update(qgzxOffer);
        return Result.success("更新数据成功");
    }

    /*通过主键删除单条数据*/
    @DeleteMapping("/del/{id}")
    public Result delete(@PathVariable Integer id) {
        qgzxOfferService.delete(id);
        return Result.success("删除数据成功");
    }

    /*删除多条数据*/
    @DeleteMapping("/del/batch")
    public Result batchDelete(@RequestBody List<Integer> ids) {
        qgzxOfferService.batchDelete(ids);
        return Result.success("删除数据成功");
    }

    /*二维码解码录入*/
    @PostMapping("/qrcode")
    public Result readQRcode(@RequestParam String bm, @RequestBody String qrcode){
        Integer applyID = qgzxApplyService.readQRcode(qrcode);
        if (applyID == 0) return Result.errorWithTitle("操作失败","二维码读取失败，请尝试手动录入。");
        QgzxApply qgzxApply = qgzxApplyService.selectById(applyID);
        QgzxTerm qgzxTerm = qgzxTermService.selectAll().get(0);
        if (qgzxApply != null) {
            if (!Objects.equals(qgzxApply.getSqxn(), qgzxTerm.getPcdm())) {
                return Result.errorWithTitle("操作失败","当前申请表非本批次申请表！");
            }
            //获取到申请表，检查是否通过审核
            QgzxOffer qgzxOffer = new QgzxOffer();
            qgzxOffer.setXq(qgzxApply.getCampus());
            qgzxOffer.setNj(qgzxApply.getGrade());
            qgzxOffer.setXy(qgzxApply.getCollege());
            qgzxOffer.setBj(qgzxApply.getClassname());
            String xm = qgzxApply.getName();
            qgzxOffer.setXm(xm);
            String xh = qgzxApply.getUsername();
            qgzxOffer.setXh(xh);
            qgzxOffer.setBm(bm);
            qgzxOffer.setPc(qgzxApply.getSqxn());
            qgzxOffer.setApplyid(applyID);
            if (!qgzxApply.getSpeed().equals("finish")){
                //审核未通过
                qgzxOffer.setZt("暂未通过学院审核");
                qgzxOfferService.insert(qgzxOffer);
                return Result.success("操作成功",xm+xh+"暂未通过学院审核");
            } else{
                //审核通过，检查是否存在录用数据
                QgzxOffer qgzxOfferUnique = qgzxOfferService.selectByXHPC(qgzxApply.getUsername(), qgzxApply.getSqxn());
                if (qgzxOfferUnique != null){
                    //存在录用数据，检查是否被其他部门录用
                    if (!qgzxOfferUnique.getBm().equals(bm)){
                        //被其他部门录用
                        qgzxOffer.setZt("已被【"+qgzxOfferUnique.getBm()+"】录用");
                        qgzxOfferService.insert(qgzxOffer);
                        return Result.success("操作成功",xm+xh+qgzxOffer.getZt());
                    } else{
                        //未被其他部门录用，说明已被自己扫过
                        return Result.success("操作成功",xm+xh+"已录用");
                    }
                } else {
                    //不存在录用数据，录用之
                    qgzxOffer.setZt("确认录用");
                    qgzxOfferService.insert(qgzxOffer);
                    return Result.success("操作成功",xm+xh+"录用成功");
                }
            }
        } else {
            //未获取到申请表
            return Result.errorWithTitle("操作失败","二维码读取失败，请尝试手动录入。");
        }
    }

    /*批量添加*/
    @PostMapping("/insertBatch")
    public Result insertBatch(@RequestBody List<String> xhs,
                              @RequestParam String xn,
                              @RequestParam String bm) {
        int ly = 0;
        int qt = 0;
        int cf = 0;
        int wsh = 0;
        int s = 0;
        for (String xh : xhs) {
            s++;
            String rt = qgzxOfferService.insertCheck(xh,xn,bm);
            if (rt.equals("wsh")) wsh++;
            else if (rt.equals("cf")) cf++;
            else if (rt.equals("qt")) qt++;
            else if (rt.equals("ly")) ly++;
        }
        ly = ly + cf;
        return Result.success("添加完成","共添加"+s+"条数据，" +
                "其中成功录用"+ly+"条，" +
                "已被其他部门录用"+qt+"条，" +
                "暂未通过学院审核"+wsh+"条");
    }

    /*查询全部数据*/
    @GetMapping("/selectAll")
    public Result selectAll() {
        List<QgzxOffer> list = qgzxOfferService.selectAll();
        return Result.success(list);
    }

    /*通过ID查询单条数据*/
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        QgzxOffer list = qgzxOfferService.selectById(id);
        return Result.success(list);
    }

    /*通过学号查询数据*/
    @GetMapping("/selectByXh/{xh}")
    public Result selectByXh(@PathVariable String xh) {
        List<QgzxOffer> list = qgzxOfferService.selectByXH(xh);
        return Result.success(list);
    }

    @GetMapping("/count/{pc}")
    public Result countpc(@PathVariable String pc){
        List<QgzxOfferCount> list = qgzxOfferService.countpc(pc);
        return Result.success(list);
    }

    /*多条件模糊查询*/
    @PostMapping("/select")
    public Result select(@RequestBody QgzxOffer qgzxOffer) {
        List<QgzxOffer> list = qgzxOfferService.select(qgzxOffer);
        return Result.success(list);
    }

    /*分页模糊多条件*/
    @PostMapping("/pageSelect")
    public Result pageSelect(@RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             @RequestBody QgzxOffer qgzxOffer) {
        Map<String, Object> result = new HashMap<>();
        List<QgzxOffer> list = qgzxOfferService.pageSelect(pageNum, pageSize, qgzxOffer);
        Integer total = qgzxOfferService.pageSelectCount(qgzxOffer);
        Integer totalPage = (int) Math.ceil((double) total / pageSize);
        result.put("list", list);
        result.put("total", total);
        result.put("totalPage", totalPage);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        return Result.success(result);
    }

    @GetMapping("/queryNumber")
    public Result queryNumber(@RequestParam String bm, @RequestParam String pc){
        return Result.success(qgzxOfferService.queryNumber(bm,pc));
    }


    /*下载*/
    @PostMapping("/export")
    public Result export(@RequestBody QgzxOffer qgzxOffer) throws Exception {
        List<QgzxOffer> list = qgzxOfferService.select(qgzxOffer);
        GlobalSet globalSet = new GlobalSet();
        String docStorePath = globalSet.getDocStorePath() + "/files/export/";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String formatDate = sdf.format(new Date());
        String UniCodeName = UUID.randomUUID().toString();
        String fileName = "/exportQgzxOffer_" + formatDate + "_" + UniCodeName + ".xls";
        ExcelWriter writer = ExcelUtil.getWriter(docStorePath + fileName);
        //自定义标题别名
        writer.addHeaderAlias("xq", "*校区");
        writer.addHeaderAlias("nj", "*年级");
        writer.addHeaderAlias("xy", "*学院");
        writer.addHeaderAlias("bj", "*班级");
        writer.addHeaderAlias("xh", "*学号");
        writer.addHeaderAlias("xm", "*姓名");
        writer.addHeaderAlias("bm", "*录用部门");
        writer.addHeaderAlias("xn", "*录用学年");
        writer.addHeaderAlias("pc", "*招聘批次");
        writer.addHeaderAlias("time", "录用时间");
        writer.addHeaderAlias("zt", "*录用结果");
        writer.addHeaderAlias("applyid", "申请表id");

        // 一次性写出list内的对象到excel，使用默认样式，强制输出标题
        writer.setOnlyAlias(true);  //只导出设置了别名的字段
        writer.write(list, true);
        writer.close();

        return Result.success("导出成功", fileName);
    }

    /*上传*/
    @PostMapping("/import/{pc}/{bm}")
    public Result imp(MultipartFile file,@PathVariable String pc,@PathVariable String bm) throws Exception {
        InputStream inputStream = file.getInputStream();
        ExcelReader reader = ExcelUtil.getReader(inputStream);
        // 方式1：(推荐) 通过 javabean的方式读取Excel内的对象，但是要求表头必须是英文，跟javabean的属性要对应起来
        //List<User> list = reader.readAll(User.class);
        // 方式2：忽略表头的中文，直接读取表的内容
        List<List<Object>> list = reader.read(1);

        int i = 0;
        int j = 0;
        int k = 0;
        int x = 0;
        int s = 0;

        for (List<Object> row : list) {
            /* 用于实现数据更新，而非新增*/
            String xh = row.get(0).toString();
            s++;
            QgzxOffer qgzxOffer = new QgzxOffer();
            QgzxOffer uniqueCheck = qgzxOfferService.selectByXHPC(xh,pc);
            if (uniqueCheck == null) {
                QgzxApply apply = qgzxApplyService.selectByStuApplyInfo(xh,pc);
                if (apply == null){
                    qgzxOffer.setXq("");
                    qgzxOffer.setNj("");
                    qgzxOffer.setXy("");
                    qgzxOffer.setBj("");
                    qgzxOffer.setXm("");
                    qgzxOffer.setXh(xh);
                    qgzxOffer.setBm(bm);
                    qgzxOffer.setPc(pc);
                    qgzxOffer.setZt("暂未通过学院审核");
                    qgzxOffer.setApplyid(0);
                    qgzxOfferService.insert(qgzxOffer);
                    x++;
                } else {
                    if (apply.getSpeed().equals("finish")){
                        qgzxOffer.setXq(apply.getCampus());
                        qgzxOffer.setNj(apply.getGrade());
                        qgzxOffer.setXy(apply.getCollege());
                        qgzxOffer.setBj(apply.getClassname());
                        qgzxOffer.setXm(apply.getName());
                        qgzxOffer.setXh(xh);
                        qgzxOffer.setBm(bm);
                        qgzxOffer.setPc(pc);
                        qgzxOffer.setZt("确认录用");
                        qgzxOffer.setApplyid(apply.getId());
                        qgzxOfferService.insert(qgzxOffer);
                        i++;
                    } else {
                        qgzxOffer.setXq(apply.getCampus());
                        qgzxOffer.setNj(apply.getGrade());
                        qgzxOffer.setXy(apply.getCollege());
                        qgzxOffer.setBj(apply.getClassname());
                        qgzxOffer.setXm(apply.getName());
                        qgzxOffer.setXh(xh);
                        qgzxOffer.setBm(bm);
                        qgzxOffer.setPc(pc);
                        qgzxOffer.setZt("暂未通过学院审核");
                        qgzxOffer.setApplyid(apply.getId());
                        qgzxOfferService.insert(qgzxOffer);
                        x++;
                    }
                }
            } else if (!uniqueCheck.getBm().equals(bm)) {
                qgzxOffer = uniqueCheck;
                qgzxOffer.setZt("已被【"+uniqueCheck.getBm()+"】录用");
                qgzxOffer.setBm(bm);
                qgzxOfferService.insert(qgzxOffer);
                j++;
            } else {
                k++;
            }
        }
        return Result.success("导入完成", "共添加"+s+"条数据，其中录用成功"+i+"条，已添加"+k+"条，已被其他部门录用"+j+"条，申请未通过学院审核"+x+"条");
    }
}
