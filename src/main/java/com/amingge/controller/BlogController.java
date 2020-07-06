package com.amingge.controller;

import com.amingge.pojo.Blog;
import com.amingge.pojo.EsBlog;
import com.amingge.pojo.User;
import com.amingge.service.BlogService;
import com.amingge.service.EsBlogService;
import com.amingge.vo.TagVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@Controller
@RequestMapping("/blogs")
public class BlogController {

    @Autowired
    private BlogService blogService;

    /**
     * 分页列出博客
     * @param async 是否异步请求
     * @param order 排序
     * @param keyword 关键字检索
     * @param pageIndex 页码
     * @param pageSize 每页显示多少
     * @param model
     * @return
     */

    @GetMapping
    public String listEsBlogs(
            @RequestParam(value="async",required=false) boolean async,
            @RequestParam(value="order",required=false,defaultValue="new") String order,
            @RequestParam(value="keyword",required=false,defaultValue="" ) String keyword,
            @RequestParam(value="pageIndex",required=false,defaultValue="0") int pageIndex,
            @RequestParam(value="pageSize",required=false,defaultValue="10") int pageSize,
            Model model) {

        Page<Blog> page = null;
        List<Blog> list = null;
        boolean isEmpty = true; // 系统初始化时，没有博客数据
        try {
            if (order.equals("hot")) { // 最热查询
                Sort sort = new Sort(Direction.DESC,"readSize","commentSize","voteSize","createTime");
                Pageable pageable = new PageRequest(pageIndex, pageSize, sort);
                page = blogService.listBlogsByTitleVoteAndSort(keyword, pageable);
            } else if (order.equals("new")) { // 最新查询
                Sort sort = new Sort(Direction.DESC,"createTime");
                Pageable pageable = new PageRequest(pageIndex, pageSize, sort);
                page = blogService.listBlogsByTitleVote(keyword, pageable);
            }

            isEmpty = false;
        } catch (Exception e) {
            Pageable pageable = new PageRequest(pageIndex, pageSize);
            page = blogService.listBlogs(pageable);
        }

        list = page.getContent();	// 当前所在页面数据列表


        model.addAttribute("order", order);
        model.addAttribute("keyword", keyword);
        model.addAttribute("page", page);
        model.addAttribute("blogList", list);

        // 首次访问页面才加载
        if (!async &&!isEmpty) {
            List<Blog> newest = blogService.listTop5NewestBlogs();
            model.addAttribute("newest", newest);
            List<Blog> hotest = blogService.listTop5HotestBlogs();
            model.addAttribute("hotest", hotest);
        }
        return (async==true?"index :: #mainContainerRepleace":"index");
    }

//    @GetMapping
//    public String listEsBlogs(
//            @RequestParam(value = "order", required = false, defaultValue = "new") String order,
//            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
//            @RequestParam(value = "async", required = false) boolean async,
//            @RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
//            @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
//            Model model) {
//
//        Page<EsBlog> page = null;
//        List<EsBlog> list = null;
//        boolean isEmpty = true;
//        try {
//            if (order.equals("hot")) {
//                Pageable pageable = PageRequest.of(pageIndex, pageSize);
//                page = esBlogService.listHottestEsBlogs(keyword, pageable);
//
//            } else if (order.equals("new")) {
//                Pageable pageable = PageRequest.of(pageIndex, pageSize);
//                page = esBlogService.listNewestEsBlogs(keyword, pageable);
//            }
//            isEmpty = false;
//        } catch (Exception e) {
//            Pageable pageable = PageRequest.of(pageIndex, pageSize);
//            page = esBlogService.listEsBlogs(pageable);
//        }
//
//        list = page.getContent();
//
//
//        model.addAttribute("order", order);
//        model.addAttribute("keyword", keyword);
//        model.addAttribute("page", page);
//        model.addAttribute("blogList", list);
//
//        if (!async && !isEmpty) {
//            List<EsBlog> newest = esBlogService.listTop5NewestEsBlogs();
//            model.addAttribute("newest", newest);
//            List<EsBlog> hotest = esBlogService.listTop5HottestEsBlogs();
//            model.addAttribute("hotest", hotest);
//        }
//        return (async ? "index :: #mainContainerReplace" : "index");
//    }
}
