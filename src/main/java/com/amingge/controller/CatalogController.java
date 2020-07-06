package com.amingge.controller;

import com.amingge.pojo.Catalog;
import com.amingge.pojo.User;
import com.amingge.service.CatalogService;
import com.amingge.service.UserService;
import com.amingge.util.ConstraintViolationExceptionHandler;
import com.amingge.vo.CatalogVO;
import com.amingge.vo.Response;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
import java.util.List;

@Controller
@RequestMapping("/catalogs")
public class CatalogController {
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private UserService userService;

//    获取分类列表
    @GetMapping
    public String listComments(@RequestParam(value="username", required = true)String username, Model model){
        User user = userService.findByUsername(username);
        List<Catalog>catalogs = catalogService.listCatalogs(user);
//        判断操作用户是否是分类的所有者
        boolean isOwner = false;
        String loginName = (String) SecurityUtils.getSubject().getPrincipal();
        if(loginName != null && username.equals(loginName)){
            isOwner = true;
        }
        model.addAttribute("isCatalogsOwner", isOwner);
        model.addAttribute("catalogs", catalogs);
        return "userspace/u :: #catalogRepleace";
    }
    //发表分类
    @PostMapping
//    @PreAuthorize("authentication.name.equals(#catalogVO.username)")// 指定用户才能操作方法
    public ResponseEntity<Response>create(@RequestBody CatalogVO catalogVO){
        String username = catalogVO.getUsername();
        Catalog catalog = catalogVO.getCatalog();

        User user = userService.findByUsername(username);

        try {
            catalog.setUser(user);
            catalogService.saveCatalog(catalog);
        } catch (ConstraintViolationException e)  {
            return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }

        return ResponseEntity.ok().body(new Response(true, "处理成功", null));
    }

    //删除分类
    @DeleteMapping("/{id}")
//    @PreAuthorize("authentication.name.equals(#username)")//指定用户才能操作方法
    public ResponseEntity<Response>delete(String username,@PathVariable("id")long id){
        try {
            catalogService.removeCatalog(id);
        } catch (ConstraintViolationException e)  {
            return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(true, "处理成功", null));
    }
    //获取分类编辑界面
    @GetMapping("/edit")
    public String getCatalogEdit(Model model){
        Catalog catalog = new Catalog(null,null);
        model.addAttribute("catalog", catalog);
        return "userspace/catalogedit";
    }
    //根据 Id 获取分类信息
    @GetMapping("/edit/{id}")
    public String getCatalogById(@PathVariable("id")Long id, Model model){
        Catalog catalog = catalogService.getCatalogById(id);
        model.addAttribute("catalog", catalog);
        return "userspace/catalogedit";
    }
}

