package com.amingge.controller;



import com.amingge.pojo.User;
import com.amingge.service.BlogService;
import com.amingge.service.UserService;
import com.amingge.util.ConstraintViolationExceptionHandler;
import com.amingge.vo.Response;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

/*
*用户控制器
*
 */
@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;


    /**
     * 查询所有用户
     * @return
     */
    @GetMapping
    @RequiresPermissions("userControl")//权限管理;
    public ModelAndView list(@RequestParam(value="async",required=false) boolean async,
                             @RequestParam(value="pageIndex",required=false,defaultValue="0") int pageIndex,
                             @RequestParam(value="pageSize",required=false,defaultValue="10") int pageSize,
                             @RequestParam(value="name",required=false,defaultValue="") String name,
                             Model model) {

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        Page<User> page = userService.listUsersByNameLike(name, pageable);
        List<User> list = page.getContent();	// 当前所在页面数据列表

        model.addAttribute("page", page);
        model.addAttribute("userList", list);
        return new ModelAndView(async==true?"users/list :: #mainContainerRepleace":"users/list", "userModel", model);
    }


    /**
     * 获取增加用户界面
     * @param
     * @return
     */
    @GetMapping("/add")
    @RequiresPermissions("userControl")//权限管理;
    public ModelAndView createForm(Model model) {
        model.addAttribute("user", new User(null, null, null, null,null));
        return new ModelAndView("users/add", "userModel", model);
    }



    /**
     * 保存或者修改用户
     * @param user
     * @return
     */
    @PostMapping
    @RequiresPermissions("userControl")//权限管理;
    public ResponseEntity<Response> saveOrUpdate(User user,Long authorityId){

//        List<Authority> authorities=new ArrayList<Authority>();
//        authorities.add(authorityService.getAuthorityById(authorityId));
//        user.setAuthorities(authorities);

        try{
            userService.saveOrUpdateUser(user);
        }catch(ConstraintViolationException e){
            return ResponseEntity.ok().body(new Response(false	, ConstraintViolationExceptionHandler.getMessage(e)));
        }

        return ResponseEntity.ok().body(new Response(true, "处理成功",user));
    }

    /**
     * 获取用户编辑页面
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/edit/{id}")
    @RequiresPermissions("userControl")//权限管理;
    public ModelAndView editGetUserInfo(@PathVariable(value="id") Long id,Model model){
        User user=userService.getUserById(id);
        model.addAttribute("user", user);
        return new ModelAndView("users/add", "userModel", model);
    }

    /**
     * 删除用户
     * @param id
     * @param model
     * @return
     */
    @DeleteMapping("/{id}")
    @RequiresPermissions("userControl")//权限管理;
    public Response deleteUser(@PathVariable(value="id") Long id,Model model){
        try {

            userService.removeUser(id);
        } catch (Exception e) {
            return new Response(false, e.getMessage());
        }
        return new Response(true, "删除成功");
    }

}
