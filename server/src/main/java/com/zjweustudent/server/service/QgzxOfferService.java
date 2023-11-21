package com.zjweustudent.server.service;

import com.zjweustudent.server.entity.QgzxApply;
import com.zjweustudent.server.entity.QgzxDepart;
import com.zjweustudent.server.entity.QgzxOffer;
import com.zjweustudent.server.entity.QgzxOfferCount;
import com.zjweustudent.server.mapper.QgzxApplyMapper;
import com.zjweustudent.server.mapper.QgzxOfferMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.rmi.CORBA.Util;
import java.util.*;

/**
 * Function: (QgzxOffer)表服务接口
 * Author: MOUJITX
 * Date: 2023-11-13 14:46:23
 */

@Service
public class QgzxOfferService {

    @Autowired
    QgzxOfferMapper qgzxOfferMapper;
    @Autowired
    QgzxApplyMapper qgzxApplyMapper;

    /**
     * 新增数据
     */
    public void insert(QgzxOffer qgzxOffer) {
        qgzxOfferMapper.insert(qgzxOffer);
    }

    public String insertCheck(String xh, String xn, String bm){
        QgzxOffer qgzxOffer = new QgzxOffer();
        QgzxOffer uniqueCheck = qgzxOfferMapper.selectByXHPC(xh,xn);
        if (uniqueCheck == null) {
            QgzxApply apply = qgzxApplyMapper.selectByStuApplyInfo(xh,xn);
            if (apply == null){
                qgzxOffer.setXq("");
                qgzxOffer.setNj("");
                qgzxOffer.setXy("");
                qgzxOffer.setBj("");
                qgzxOffer.setXm("");
                qgzxOffer.setXh(xh);
                qgzxOffer.setBm(bm);
                qgzxOffer.setPc(xn);
                qgzxOffer.setZt("暂未通过学院审核");
                qgzxOffer.setApplyid(0);
                qgzxOfferMapper.insert(qgzxOffer);
                return "wsh";
            } else {
                if (apply.getSpeed().equals("finish")){
                    qgzxOffer.setXq(apply.getCampus());
                    qgzxOffer.setNj(apply.getGrade());
                    qgzxOffer.setXy(apply.getCollege());
                    qgzxOffer.setBj(apply.getClassname());
                    qgzxOffer.setXm(apply.getName());
                    qgzxOffer.setXh(xh);
                    qgzxOffer.setBm(bm);
                    qgzxOffer.setPc(xn);
                    qgzxOffer.setZt("确认录用");
                    qgzxOffer.setApplyid(apply.getId());
                    qgzxOfferMapper.insert(qgzxOffer);
                    return "ly";
                } else {
                    qgzxOffer.setXq(apply.getCampus());
                    qgzxOffer.setNj(apply.getGrade());
                    qgzxOffer.setXy(apply.getCollege());
                    qgzxOffer.setBj(apply.getClassname());
                    qgzxOffer.setXm(apply.getName());
                    qgzxOffer.setXh(xh);
                    qgzxOffer.setBm(bm);
                    qgzxOffer.setPc(xn);
                    qgzxOffer.setZt("暂未通过学院审核");
                    qgzxOffer.setApplyid(apply.getId());
                    qgzxOfferMapper.insert(qgzxOffer);
                    return "wsh";
                }
            }
        } else if (!uniqueCheck.getBm().equals(bm)) {
            qgzxOffer = uniqueCheck;
            qgzxOffer.setZt("已被【"+uniqueCheck.getBm()+"】录用");
            qgzxOffer.setBm(bm);
            qgzxOfferMapper.insert(qgzxOffer);
            return "qt";
        } else {
            return "cf";
        }
    }

    /**
     * 修改数据
     */
    public void update(QgzxOffer qgzxOffer) {
        qgzxOfferMapper.update(qgzxOffer);
    }

    /**
     * 通过主键删除数据
     */
    public void delete(Integer id) {
        qgzxOfferMapper.delete(id);
    }

    /**
     * 删除多条数据
     */
    public void batchDelete(List<Integer> ids) {
        for (Integer id : ids) {
            qgzxOfferMapper.delete(id);
        }
    }

    /**
     * 查询全部数据
     */
    public List<QgzxOffer> selectAll() {
        return qgzxOfferMapper.selectAll();
    }

    /**
     * 通过ID查询单条数据
     */
    public QgzxOffer selectById(Integer id) {
        return qgzxOfferMapper.selectById(id);
    }

    /**
     * 多条件查询
     */
    public List<QgzxOffer> select(QgzxOffer qgzxOffer) {
        return qgzxOfferMapper.select(1, qgzxOffer);
    }

    /**
     * 多条件分页
     */
    public List<QgzxOffer> pageSelect(Integer pageNum, Integer pageSize, QgzxOffer qgzxOffer) {
        Integer skipNum = (pageNum - 1) * pageSize;
        return qgzxOfferMapper.pageSelect(skipNum, pageSize, qgzxOffer);
    }

    /**
     * 多条件查询返回查询数据总数
     */
    public Integer pageSelectCount(QgzxOffer qgzxOffer) {
        return qgzxOfferMapper.pageSelectCount(1, qgzxOffer);
    }

    /**
     * 修改参照
     * 通过（某唯一字段）查询
     */

    public QgzxOffer selectByXHPC(String xh,String pc) {
        return qgzxOfferMapper.selectByXHPC(xh,pc);
    }

    public List<QgzxOffer> selectByXH(String xh) {
        return qgzxOfferMapper.selectByXH(xh);
    }

    public Map<String, Object> queryNumber(String bm, String pc) {
        QgzxDepart DepartNum = qgzxOfferMapper.queryDepartNum(bm);
        Integer nxrs = 0;
        Integer qtrs = 0;
        try {
            nxrs = DepartNum.getNxrs();
            qtrs = DepartNum.getQtrs();
        } catch (Exception e) {
            nxrs = 0;
            qtrs = 0;
        }
        List<QgzxOffer> OfferNum = qgzxOfferMapper.queryOfferNum(pc, bm);
        Integer nxly = 0;
        Integer qtly = 0;
        if (OfferNum.size() == 2){
            qtly = Integer.valueOf(OfferNum.get(0).getXh());
            nxly = Integer.valueOf(OfferNum.get(1).getXh());
        } else if (OfferNum.size() == 1){
            if (OfferNum.get(0).getXq().equals("钱塘校区")) qtly = Integer.valueOf(OfferNum.get(0).getXh());
            else if (OfferNum.get(0).getXq().equals("南浔校区")) nxly = Integer.valueOf(OfferNum.get(0).getXh());
        }
        Integer nxsy = nxrs - nxly;
        Integer qtsy = qtrs - qtly;

        Map<String, Object> rt = new HashMap<>();
        rt.put("nxrs", nxrs);
        rt.put("nxly", nxly);
        rt.put("nxsy", nxsy);
        rt.put("qtrs", qtrs);
        rt.put("qtly", qtly);
        rt.put("qtsy", qtsy);

        return rt;

    }

    public List<QgzxOfferCount> countpc(String pc) {
        return qgzxOfferMapper.countpc(pc);
    }
}
