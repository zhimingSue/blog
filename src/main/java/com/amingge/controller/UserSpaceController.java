package com.amingge.controller;

import com.amingge.pojo.Blog;
import com.amingge.pojo.Catalog;
import com.amingge.pojo.User;
import com.amingge.pojo.Vote;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.data.domain.Sort.Direction;
import com.amingge.repository.BlogRepository;
import com.amingge.service.BlogService;
import com.amingge.service.CatalogService;
import com.amingge.service.UserService;
import com.amingge.util.ConstraintViolationExceptionHandler;
import com.amingge.util.MyException;
import com.amingge.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.UUID;

/**
 * 个人用户中心控制器
 * @author OJ's big hole
 *
 */
@Controller
@RequestMapping("/u")
public class UserSpaceController {

    @Autowired
    private UserService userService;

    @Autowired
    private BlogService blogService;

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private CatalogService catalogService;

    private static Long catalogId=0L;

    /**
     * 获取个人中心界面
     * @param username
     * @param model
     * @return
     */
    @GetMapping("/{username}")
    public String userSpace(@PathVariable("username") String username,
                            Model model) {
        User user = userService.findByUsername(username);
        model.addAttribute("user", user);
        return "redirect:/u/" + username + "/blogs";
    }

    /**
     * 个人主页博客列表
     *
     * @param username
     * @param order

     * @param keyword
     * @param async
     * @param pageIndex
     * @param pageSize
     * @param model
     * @return
     */
    @GetMapping("/{username}/blogs")
    public String listBlogsByOrder(
            @PathVariable("username") String username,
            @RequestParam(value = "order", required = false, defaultValue = "new") String order,
            @RequestParam(value = "catalog", required = false) Long catalogId,
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(value = "async", required = false) boolean async,
            @RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
            Model model) {

        User user = userService.findByUsername(username);

        Page<Blog> page = null;

        if (catalogId != null && catalogId > 0) { // 分类查询
            Catalog catalog = catalogService.getCatalogById(catalogId);
            Pageable pageable = new PageRequest(pageIndex, pageSize);
            page = blogService.listBlogsByCatalog(catalog, pageable);
            order = "";
        } else if (order.equals("hot")) { // 最热查询
            Sort sort = new Sort(Direction.DESC, "readSize", "commentSize",
                    "voteSize");
            Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);
            page = blogService.listBlogsByTitleVoteAndSort(user, keyword,
                    pageable);
        } else if (order.equals("new")) { // 最新查询
            Pageable pageable = PageRequest.of(pageIndex, pageSize);
            page = blogService.listBlogsByTitleVote(user, keyword, pageable);
        }

        List<Blog> list = page.getContent(); // 当前所在页面数据列表

        model.addAttribute("user", user);
        model.addAttribute("order", order);
        model.addAttribute("catalogId", catalogId);
        model.addAttribute("keyword", keyword);
        model.addAttribute("page", page);
        model.addAttribute("blogList", list);
        return (async == true ? "userspace/u :: #mainContainerRepleace"
                : "userspace/u");
    }

    /**
     * 获取博客展示界面
     *
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/{username}/blogs/{id}")
    public String getBlogById(@PathVariable("username") String username,
                              @PathVariable("id") Long id, Model model) {

        User principal = null;
        Blog blog = blogService.getBlogById(id);
        // 每次读取，简单的可以认为阅读量增加1次
        blogService.readingIncrease(id);

        boolean isBlogOwner = false;

        // 判断操作用户是否是博客的所有者
        Session session= SecurityUtils.getSubject().getSession();
        principal = (User) session.getAttribute("userSession");
        if (principal !=null && username.equals(principal.getUsername())) {
            isBlogOwner = true;
        }
        // 判断操作用户的点赞情况
        List<Vote> votes = blog.getVotes();
        Vote currentVote = null; // 当前用户的点赞情况

        if (principal != null) {
            for (Vote vote : votes) {
                vote.getUser().getUsername().equals(principal.getUsername());
                currentVote = vote;
                break;
            }
        }

        model.addAttribute("isBlogOwner", isBlogOwner);
        model.addAttribute("blogModel", blogService.getBlogById(id));
        model.addAttribute("currentVote", currentVote);

        return "userspace/blog";
    }

    /**
     * 获取新增博客的界面
     *
     * @param model
     * @return
     */
    @GetMapping("/{username}/blogs/edit")

    public ModelAndView createBlog(@PathVariable("username") String username,
                                   Model model) {
        User user = userService.findByUsername(username);
        List<Catalog> catalogs = catalogService.listCatalogs(user);

        model.addAttribute("blog", new Blog(null, null, null));
        model.addAttribute("catalogs", catalogs);
        return new ModelAndView("userspace/blogedit", "blogModel", model);
    }

    /**
     * 获取编辑博客的界面
     *
     * @param model
     * @return
     */
    @GetMapping("/{username}/blogs/edit/{id}")
    public ModelAndView editBlog(@PathVariable("username") String username,
                                 @PathVariable("id") Long id, Model model) {
        User user = userService.findByUsername(username);
        List<Catalog> catalogs = catalogService.listCatalogs(user);

        model.addAttribute("blog", blogService.getBlogById(id));
        model.addAttribute("catalogs", catalogs);
        return new ModelAndView("userspace/blogedit", "blogModel", model);
    }

    /**
     * 保存博客
     *
     * @param username
     * @param blog
     * @return
     */
    @PostMapping("/{username}/blogs/edit")

//    @PreAuthorize("authentication.name.equals(#username)")
    public ResponseEntity<Response> saveBlog(
            @PathVariable("username") String username, @RequestBody Blog blog) {

        User user = userService.findByUsername(username);

        String blogId = null;
        try {
            if (blog.getId() != 0) {
                Blog orignalBlog = blogService.getBlogById(blog.getId());
                orignalBlog.setTitle(blog.getTitle());
                orignalBlog.setContent(blog.getContent());
                orignalBlog.setSummary(blog.getSummary());
                orignalBlog.setCatalog(blog.getCatalog());
                orignalBlog.setTags(blog.getTags());
                blogService.saveBlog(orignalBlog);
                blogId = blog.getId() + "";
            } else {
                if (blog.getCatalog().getId() == null) {
                    // 创建[未分类]分类

                    Catalog catalog = new Catalog(user, "未分类");
                    catalog=catalogService.insertAnonymousCatalog(catalog);
                    catalogId=catalog.getId();


                }
                if (blog.getCatalog().getId() == null && catalogService.getCatalogById(catalogId)!=null) {
                    blog.setCatalog(catalogService.getCatalogById(catalogId));
                }else{
                    blog.setCatalog(blog.getCatalog());
                }
                blog.setUser(user);
                Blog getIdBlog = blogRepository.saveAndFlush(blog);
                blogId = getIdBlog.getId() + "";
            }
        } catch (ConstraintViolationException e) {
            return ResponseEntity.ok().body(
                    new Response(false, ConstraintViolationExceptionHandler
                            .getMessage(e)));
        } catch (Exception e) {
            return ResponseEntity.ok()
                    .body(new Response(false, e.getMessage()));
        }

        String redirectUrl = "/u/" + username + "/blogs/" + blogId;

        return ResponseEntity.ok()
                .body(new Response(true, "处理成功", redirectUrl));
    }

    /**
     * 删除博客
     *
     * @param id

     * @return
     */
    @DeleteMapping("/{username}/blogs/{id}")
//    @PreAuthorize("authentication.name.equals(#username)")
    public ResponseEntity<Response> deleteBlog(
            @PathVariable("username") String username,
            @PathVariable("id") Long id) {

        try {
            blogService.removeBlog(id);
        } catch (Exception e) {
            return ResponseEntity.ok()
                    .body(new Response(false, e.getMessage()));
        }

        String redirectUrl = "/u/" + username + "/blogs";
        return ResponseEntity.ok()
                .body(new Response(true, "处理成功", redirectUrl));
    }

    /**
     * 获取编辑头像的界面
     *
     * @param username
     * @param model
     * @return
     */
    @GetMapping("/{username}/avatar")
//    @PreAuthorize("authentication.name.equals(#username)")
    public ModelAndView avatar(@PathVariable("username") String username,
                               Model model) {
        User user = userService.findByUsername(username);
        model.addAttribute("user", user);
        return new ModelAndView("userspace/avatar", "userModel", model);
    }

    /**
     * 保存头像
     *
     * @param username

     * @return
     */
    @PostMapping("/{username}/avatar")
//    @PreAuthorize("authentication.name.equals(#username)")
    public ResponseEntity<Response> saveAvatar(
            @PathVariable("username") String username, @RequestBody User user) {
        String avatarUrl = user.getAvatar();

        User originalUser = userService.getUserById(user.getId());
        originalUser.setAvatar(avatarUrl);
        userService.saveOrUpdateUser(originalUser);

        return ResponseEntity.ok().body(new Response(true, "处理成功", avatarUrl));
    }

    /**
     * 获取个人设置页面
     *
     * @param username
     * @param model
     * @return
     */
    @GetMapping("/{username}/profile")
//    @PreAuthorize("authentication.name.equals(#username)")
    public ModelAndView profile(@PathVariable("username") String username,
                                Model model) {
        User user = userService.findByUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("saveProfileTip", "更改信息成功");
        return new ModelAndView("userspace/profile", "userModel", model);
    }

    /**
     * 保存个人设置
     *
     * @param user

     * @return
     * @throws MyException
     */
    @PostMapping("/{username}/profile")
//    @PreAuthorize("authentication.name.equals(#username)")
    public String saveProfile(@PathVariable("username") String username,
                              User user) throws MyException {
        User originalUser = userService.getUserById(user.getId());
        if(!(userService.findByEmail(user.getEmail())==originalUser) && userService.findByEmail(user.getEmail())!=null){
            throw new MyException("*邮箱已存在");
        }
        originalUser.setEmail(user.getEmail());
        originalUser.setName(user.getName());


        String rawPassword = originalUser.getPassword();
        String salt = new SecureRandomNumberGenerator().nextBytes().toString();
        String encodedPassword = new SimpleHash("md5", user.getPassword(), salt, 2).toString();
        if(!rawPassword.equals(encodedPassword)) {
            originalUser.setSalt(salt);
            originalUser.setPassword(encodedPassword);
        }

        userService.saveOrUpdateUser(originalUser);
        return "redirect:/u/" + username + "/profile";
    }
}
