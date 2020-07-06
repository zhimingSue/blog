package com.amingge.controller;

import com.amingge.pojo.User;
import com.amingge.service.UserService;
import com.amingge.util.UserUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpSession;


/**
 * 主页控制器.
 *
 */
@Controller
public class MainController {


	@Autowired
	private UserService userService;

	@RequestMapping("/blogList")
	public String blogList() {
		return "userspace/blogList";
	}

	@RequestMapping("/403")
	public String error() {
		return "403";
	}


	@GetMapping("/")
	public String root() {
		return "redirect:/blogs";
	}

	@GetMapping("/index")
	public String index() {
		return "index";
	}

	/**
	 * 获取登录界面
	 * @return
	 */
	@RequestMapping("/login")
	public String login() {
		return "login";
	}

	@PostMapping("/login")
	public String doLogin(@RequestParam("username") String username,
                          @RequestParam("password") String password,
                           User user,
                          HttpSession session, Model model){
//		if (userService.checkLogin(user)){
//			user = userService.findByUsernameAndPassword(username, password);
			String name = user.getUsername();
            name = HtmlUtils.htmlEscape(name);
			Subject subject = SecurityUtils.getSubject();
			UsernamePasswordToken token = new UsernamePasswordToken(name, user.getPassword());
            try {
				subject.login(token);
				UserUtil.saveUserToSession(session, user);
				subject.getSession().setAttribute("user", user);
				subject.getSession().setAttribute("username", username);
				return "redirect:/blogs";
			}catch (Exception e){
            	e.getStackTrace();
			}
//		}
		return "redirect:/login";

	}

	@PostMapping("/login-error")
	public String loginError(Model model) {
		model.addAttribute("loginError", true);
		model.addAttribute("errorMsg", "登陆失败，账号或者密码错误！");
		return "login";
	}

	@GetMapping("/register")
	public String register() {
		return "register";
	}

	/**
	 * 注册用户
	 */
	@PostMapping("/register")
	public String registerUser(User user){
		String name = user.getUsername();
		String password = user.getPassword();
		name = HtmlUtils.htmlEscape(name);
		user.setName(name);

		boolean exist = userService.isExist(name);

		if(exist){
			String message ="用户名已经被使用,不能使用";
			return "redirect:/common/error";
		}
		String salt = new SecureRandomNumberGenerator().nextBytes().toString();
		int times = 2;
		String algorithmName = "md5";
		String encodedPassword = new SimpleHash(algorithmName, password, salt, times).toString();
		user.setSalt(salt);
		user.setPassword(encodedPassword);

		userService.saveOrUpdateUser(user);
		return "redirect:/login";
	}


	@GetMapping("/search")
	public String search() {
		return "search";
	}

	@PostMapping("/logout")
	public String Logout(){
		Subject subject = SecurityUtils.getSubject();
		subject.logout();
		return "redirect:/login";
	}
}
