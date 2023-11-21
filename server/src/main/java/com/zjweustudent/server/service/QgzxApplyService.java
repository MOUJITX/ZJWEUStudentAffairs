package com.zjweustudent.server.service;

import cn.hutool.extra.qrcode.QrCodeUtil;

import java.awt.image.BufferedImage;

import cn.hutool.extra.qrcode.QrConfig;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;
import com.zjweustudent.server.common.GlobalSet;
import com.zjweustudent.server.entity.QgzxApply;
import com.zjweustudent.server.mapper.QgzxApplyMapper;
import com.zjweustudent.server.utils.AesUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Function: 勤工申请(QgzxApply)表服务接口
 * Author: MOUJITX
 * Date: 2023-11-04 21:38:31
 */

@Service
public class QgzxApplyService {

    @Autowired
    QgzxApplyMapper qgzxApplyMapper;

    /**
     * 新增数据
     */
    public void insert(QgzxApply qgzxApply) {
        qgzxApplyMapper.insert(qgzxApply);
    }


    /**
     * 修改数据
     */
    public void update(QgzxApply qgzxApply) {
        qgzxApplyMapper.update(qgzxApply);
    }

    /**
     * 通过主键删除数据
     */
    public void delete(Integer id) {
        qgzxApplyMapper.delete(id);
    }

    /**
     * 删除多条数据
     */
    public void batchDelete(List<Integer> ids) {
        for (Integer id : ids) {
            qgzxApplyMapper.delete(id);
        }
    }

    /**
     * 查询全部数据
     */
    public List<QgzxApply> selectAll() {
        return qgzxApplyMapper.selectAll();
    }

    /**
     * 通过ID查询单条数据
     */
    public QgzxApply selectById(Integer id) {
        return qgzxApplyMapper.selectById(id);
    }

    /**
     * 多条件查询
     */
    public List<QgzxApply> select(QgzxApply qgzxApply) {
        return qgzxApplyMapper.select(1, qgzxApply);
    }

    /**
     * 多条件分页
     */
    public List<QgzxApply> pageSelect(Integer pageNum, Integer pageSize, QgzxApply qgzxApply) {
        Integer skipNum = (pageNum - 1) * pageSize;
        return qgzxApplyMapper.pageSelect(skipNum, pageSize, qgzxApply);
    }

    /**
     * 多条件查询返回查询数据总数
     */
    public Integer pageSelectCount(QgzxApply qgzxApply) {
        return qgzxApplyMapper.pageSelectCount(1, qgzxApply);
    }

    public void setSpeed(Integer id, String speed, String rtime, String rname, String rnum) {
        qgzxApplyMapper.setSpeed(id,speed,rtime,rname,rnum);
    }

    public void setSpeedBatch(String speed,
                              List<Integer> ids, String rtime, String rname, String rnum) {
        for (Integer id : ids) {
            qgzxApplyMapper.setSpeed(id,speed,rtime,rname,rnum);
        }
    }

    public List<QgzxApply> selectByNum(String number) {
        return qgzxApplyMapper.selectByNum(number);
    }



    /**
     * 修改参照
     * 通过（某唯一字段）查询
     */

    public QgzxApply selectByStuApplyInfo(String username, String sqxn) {
        return qgzxApplyMapper.selectByStuApplyInfo(username,sqxn);
    }

    public List<QgzxApply> selectFinish(String pcdm) {
        return qgzxApplyMapper.selectFinish(pcdm);
    }


    public List<QgzxApply> selectFinishByBM(String pcdm, String bm) {
        return qgzxApplyMapper.selectFinishByBM(pcdm,bm);
    }

    public Integer readQRcode(String qrcode) {
        String beforeQRcode = qrcode.replace(" ", "+").replace("\"","");
        //System.out.println(beforeQRcode);
        String afterQRcode = AesUtils.decryptNew(qrcode);
        if (afterQRcode.equals("0")) return 0;
        else return Integer.valueOf(afterQRcode);
    }

    public String getPDF(QgzxApply qgzxApply, HttpServletResponse response) throws UnsupportedEncodingException {
        // 模板路径
        GlobalSet globalSet = new GlobalSet();
        String path = globalSet.getDocStorePath() + "/files/public/";
        String templatePath = path + "applyDemo2.pdf";
        //System.out.println("path:" + templatePath);
        // 生成导出PDF的文件名称
        String fileName = qgzxApply.getUsername() + ".pdf";
        fileName = URLEncoder.encode(fileName, "UTF-8");
        // 设置响应头
        response.setContentType("application/force-download");
        response.setHeader("Content-Disposition",
                "attachment;fileName=" + fileName);
        OutputStream out = null;
        ByteArrayOutputStream bos = null;
        PdfStamper stamper = null;
        PdfReader reader = null;
        try {
            // 保存到本地
            String docStorePath = globalSet.getDocStorePath() + "/files/export/";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String formatDate = sdf.format(new Date());
            String exportFileName = "/exportPDF_" + formatDate + "_" + fileName;
            out = Files.newOutputStream(Paths.get(docStorePath + exportFileName));
            // 输出到浏览器端
            //out = response.getOutputStream();
            // 读取PDF模板表单
            reader = new PdfReader(templatePath);
            // 字节数组流，用来缓存文件流
            bos = new ByteArrayOutputStream();
            // 根据模板表单生成一个新的PDF
            stamper = new PdfStamper(reader, bos);
            // 获取新生成的PDF表单
            AcroFields form = stamper.getAcroFields();
            // 给表单生成中文字体，这里采用系统字体，不设置的话，中文显示会有问题
            BaseFont font = BaseFont.createFont(path + "Deng.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            form.addSubstitutionFont(font);
            // 装配数据
            Map<String, Object> data = new HashMap<>(15);

            String qrcodeString = AesUtils.encryptNew(qgzxApply.getId().toString());
            //System.out.println("加密后"+qrcodeString);
            data.put("no", String.format("%s%06d-%s", "QG", qgzxApply.getId(), qgzxApply.getUsername()));

            QrConfig qrConfig = new QrConfig(300,300);
            qrConfig.setMargin(0);
            BufferedImage qrCode = QrCodeUtil.generate("http://47.99.69.136/Phone/QgzxOfferDepart?qrcode=" + qrcodeString, qrConfig);
            data.put("qrcode", qrCode);

            if (qgzxApply.getSqxn() != null) data.put("sqxn", qgzxApply.getSqxn());
            if (qgzxApply.getName() != null) data.put("name", qgzxApply.getName());
            if (qgzxApply.getUsername() != null) data.put("username", qgzxApply.getUsername());
            if (qgzxApply.getMale() != null) data.put("male", qgzxApply.getMale());
            if (qgzxApply.getCollege() != null) data.put("college", qgzxApply.getCollege());
            if (qgzxApply.getClassname() != null) data.put("classname", qgzxApply.getClassname());
            if (qgzxApply.getPhone() != null) data.put("phone", qgzxApply.getPhone());
            if (qgzxApply.getType().equals("1")) data.put("type", "一般困难");
            else if (qgzxApply.getType().equals("2")) data.put("type", "困难");
            else if (qgzxApply.getType().equals("3")) data.put("type", "特别困难");
            else data.put("type", "未认定");
            if (qgzxApply.getLastdepart() != null) data.put("lastdepart", qgzxApply.getLastdepart());
            if (qgzxApply.getSkill() != null) data.put("skill", qgzxApply.getSkill());
            if (qgzxApply.getDepart() != null) data.put("depart", qgzxApply.getDepart());
            if (qgzxApply.getDepartb() != null) data.put("departb", qgzxApply.getDepartb());
            if (qgzxApply.getChoosetj() != null) {
                if (qgzxApply.getChoosetj().equals("true")) data.put("choosetj", "是");
                else data.put("choosetj", "否");
            }
            if (qgzxApply.getTimea() != null) data.put("timea", qgzxApply.getTimea());
            if (qgzxApply.getTimeb() != null) data.put("timeb", qgzxApply.getTimeb());
            if (qgzxApply.getTimec() != null) data.put("timec", qgzxApply.getTimec());
            if (qgzxApply.getTimed() != null) data.put("timed", qgzxApply.getTimed());
            if (qgzxApply.getTimee() != null) data.put("timee", qgzxApply.getTimee());
            if (qgzxApply.getSpeed().equals("finish")) data.put("speed1", "√");
            else data.put("speed2", "√");
            String rtime = sdf.format(qgzxApply.getReplytime());
            //System.out.println(rtime);
            if (qgzxApply.getReplytime() != null){
                data.put("replytime1", rtime.substring(0, 4));
                data.put("replytime2", rtime.substring(4, 6));
                data.put("replytime3", rtime.substring(6, 8));
            }
            // 遍历data，给pdf表单赋值
            for(String key : data.keySet()){
                // 图片要单独处理
                if("qrcode".equals(key)){
                    int pageNo = form.getFieldPositions(key).get(0).page;
                    Rectangle signRect = form.getFieldPositions(key).get(0).position;
                    float x = signRect.getLeft();
                    float y = signRect.getBottom();
                    //读取图片
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(qrCode, "png", baos);
                    Image image = Image.getInstance(baos.toByteArray());
                    //获取图片页面
                    PdfContentByte under = stamper.getOverContent(pageNo);
                    //图片大小自适应
                    image.scaleToFit(signRect.getWidth(), signRect.getHeight());
                    //添加图片
                    image.setAbsolutePosition(x, y);
                    under.addImage(image);
                }
                // 设置普通文本数据
                else {
                    form.setField(key, data.get(key).toString());
                }

            }
            // 表明该PDF不可修改
            stamper.setFormFlattening(true);
            // 关闭资源
            stamper.close();
            // 将ByteArray字节数组中的流输出到out中（即输出到浏览器）
            Document doc = new Document();
            PdfCopy copy = new PdfCopy(doc, out);
            doc.open();
            PdfImportedPage importPage = copy.getImportedPage(new PdfReader(bos.toByteArray()), 1);
            copy.addPage(importPage);
            doc.close();

            if (out != null) {
                out.flush();
                out.close();
            }
            if (reader != null) {
                reader.close();
            }

            return exportFileName;
        }catch (Exception e){
            e.printStackTrace();
            return "0";
        }
    }


}
