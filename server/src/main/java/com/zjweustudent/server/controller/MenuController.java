package com.zjweustudent.server.controller;

import com.zjweustudent.server.common.GlobalSet;
import com.zjweustudent.server.common.Result;
import com.zjweustudent.server.entity.Menu;
import com.zjweustudent.server.entity.StuInfo;
import com.zjweustudent.server.service.MenuService;
import com.zjweustudent.server.service.UserGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Blob;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Function: 目录(Menu)表控制层接口
 * Author: MOUJITX
 * Date: 2023-09-22 14:21:29
 */

@RestController
@RequestMapping("/menu")
public class MenuController {

    @Autowired
    MenuService menuService;

    /*新增数据*/
    @PostMapping("/add")
    public Result add(@RequestBody Menu menu) {
        /* 唯一性校验*/
        Menu uniqueCheck = menuService.selectByPath(menu.getPath());
        if (uniqueCheck != null) {
            return Result.errorWithTitle("操作失败","该目录已存在");
        }

        menuService.insert(menu);
        return Result.success("新增数据成功");
    }


    /*修改数据*/
    @PutMapping("/update")
    public Result update(@RequestBody Menu menu) {
        /* 唯一性校验*/
        Menu uniqueCheck = menuService.selectByPath(menu.getPath());
        Menu uniqueCheck1 = menuService.selectById(menu.getId());
        if (uniqueCheck != null && !uniqueCheck1.getPath().equals(uniqueCheck.getPath())) {
            return Result.errorWithTitle("操作失败","该目录已存在");
        }

        menuService.update(menu);
        return Result.success("更新数据成功");
    }

    /*通过主键删除单条数据*/
    @DeleteMapping("/del/{id}")
    public Result delete(@PathVariable Integer id) {
        menuService.delete(id);
        return Result.success("删除数据成功");
    }

    /*删除多条数据*/
    @DeleteMapping("/del/batch")
    public Result batchDelete(@RequestBody List<Integer> ids) {
        menuService.batchDelete(ids);
        return Result.success("删除数据成功");
    }

    /*查询全部数据*/
    @GetMapping("/selectAll")
    public Result selectAll() {
        List<Menu> list = menuService.selectAll();
        return Result.success(list);
    }

    /*通过ID查询单条数据*/
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Menu list = menuService.selectById(id);
        return Result.success(list);
    }

    /*通过父ID查询同组数据*/
    @GetMapping("/selectByFather/{id}")
    public Result selectByFather(@PathVariable String id) {
        List<Menu> list = menuService.selectByFather(id);
        return Result.success(list);
    }

    @Autowired
    private WebController webController;

    @Autowired
    private UserGroupService userGroupService;

    /*由用户token获取可访问目录*/
    @GetMapping("/getMenu")
    public Result getMenuByToken(HttpServletRequest request) {
        //由token获取userGroup
        String userToken = request.getHeader("token");
        //由token获取可访问目录
        Map<String, Object> userData = (Map<String, Object>) webController.login(userToken).getData();
        Integer userGroupId = Integer.valueOf(userData.get("userGroup").toString());
        //由可访问目录获取目录列表
        String userGroup = userGroupService.selectById(userGroupId).getAccessmenu();
        List<Menu> menuList = menuService.selectByIds(userGroup);
        return Result.success(menuList);
    }

    /*判断用户是否有访问当前页面的权限*/
    @PostMapping("/ConfirmAccess")
    public Result confirmAccess(HttpServletRequest request,
                                @RequestBody String menuPath){
        List<Menu> menuList = (List<Menu>) this.getMenuByToken(request).getData();
        Menu menu = menuService.selectByPath(menuPath.replace("\"", ""));
        if (menuList.contains(menu)) {
            return Result.success();
        } else {
            return Result.error("403", "noAccess");
        }
    }

    /*多条件模糊查询*/
    @PostMapping("/select")
    public Result select(@RequestBody Menu menu) {
        List<Menu> list = menuService.select(menu);
        return Result.success(list);
    }

    /*分页模糊多条件*/
    @PostMapping("/pageSelect")
    public Result pageSelect(@RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             @RequestBody Menu menu) {
        Map<String, Object> result = new HashMap<>();
        List<Menu> list = menuService.pageSelect(pageNum, pageSize, menu);
        Integer total = menuService.pageSelectCount(menu);
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
    public Result export(@RequestBody Menu menu) throws Exception {
        List<Menu> list = menuService.select(menu);

        GlobalSet globalSet = new GlobalSet();
        String docStorePath = globalSet.getDocStorePath() + "/files/export/";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String formatDate = sdf.format(new Date());
        String UniCodeName = UUID.randomUUID().toString();
        String fileName = "/exportMenu_" + formatDate + "_" + UniCodeName + ".xls";
        ExcelWriter writer = ExcelUtil.getWriter(docStorePath + fileName);
        //自定义标题别名
        writer.addHeaderAlias("title", "名称");
        writer.addHeaderAlias("path", "路径");
        writer.addHeaderAlias("icon", "图标");
        writer.addHeaderAlias("father", "父级目录id");
        writer.addHeaderAlias("note", "说明");
        writer.addHeaderAlias("ranknum", "显示顺序");
        writer.addHeaderAlias("show", "显示");

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
            Menu menu = new Menu();

            /* 用于实现数据更新，而非新增*/
            String path = row.get(1).toString();
            menu.setPath(path);

            menu.setTitle(row.get(0).toString());
            //menu.setPath(row.get(1).toString());
            menu.setIcon(row.get(2).toString());
            menu.setFather(row.get(3).toString());
            menu.setNote(row.get(4).toString());
            menu.setRanknum(Integer.valueOf(row.get(5).toString()));
            menu.setShow(row.get(6).toString());

            /* 用于实现数据更新，而非新增*/
            Menu uniqueConfirm = menuService.selectByPath(path);
            if (uniqueConfirm == null) {
                menuService.insert(menu);
                addNum++;
            } else {
                menu.setId(uniqueConfirm.getId());
                menuService.update(menu);
                updateNum++;
            }


        }
        Map<String, Object> Rst = new HashMap<>();
        Rst.put("updata", updateNum);
        Rst.put("add", addNum);
        return Result.success("导入完成", Rst);
    }
}
