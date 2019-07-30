package com.zhang.mynews.potal.controller;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

import javax.servlet.ServletContextEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.zhang.mynews.bean.User;
import com.zhang.mynews.potal.service.UserService;
import com.zhang.mynews.util.AjaxResult;
import com.zhang.mynews.util.Const;
import com.zhang.mynews.util.MD5Util;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	/**
	 * 用户登录
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/doLogin",method = RequestMethod.POST)
	public AjaxResult doLogin(String username,String password,HttpSession session) {
		AjaxResult result = new AjaxResult();
		try {
			//将参数填入map中
			HashMap<String, Object> paramMap = new HashMap<String,Object>();
			paramMap.put("username", username);
			paramMap.put("password", password);
			//拿到数据库中对应的user
			User user=userService.queryUserLogin(paramMap);
			//放入session
			session.setAttribute(Const.LOGIN_USER, user);
			//构建返回结果
			result.setSuccess(true);
		} catch (Exception e) {
			e.printStackTrace();
			result.setMessage(e.getMessage());
			result.setSuccess(false);
		}
		return result;
	}
	
	/**
	 *  退出登录
	 * @return
	 */
	@RequestMapping(value = "/logout",method = RequestMethod.GET)
	public String logout(HttpSession session) {
		
		session.invalidate(); //销毁session对象,或清理session域.
		
		return "redirect:/index.htm";
	}
	
	/**
	 * 用户注册
	 * @param user
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/doReg" , method = RequestMethod.POST)
	public AjaxResult doReg(User user) {
		AjaxResult result = new AjaxResult();
		try {
			//拿到数据库中对应的user
			int count=userService.doReg(user);
			//构建返回结果
			result.setSuccess(count==1);
		} catch (Exception e) {
			e.printStackTrace();
			result.setMessage("用户名已存在，或其他问题。");
			result.setSuccess(false);
		}
		return result;
	}
	
	/**
	 * 修改用户
	 * @param user
	 * @param img
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/doChange" , method = RequestMethod.POST)
	public AjaxResult doChange(User changeUser,@RequestParam(value = "img",required = false) MultipartFile img,
			HttpSession session) {
		AjaxResult result = new AjaxResult();
		try {
			User user=(User)session.getAttribute(Const.LOGIN_USER);
			changeUser.setId(user.getId());
			changeUser.setCreationtime(user.getCreationtime());
			if(img!=null) {
				String imgName = UUID.randomUUID().toString()+img.getOriginalFilename()
								.substring(img.getOriginalFilename().lastIndexOf("."));
				String path=session.getServletContext().getRealPath("avatar")+"/"+imgName;
				FileUtils.copyInputStreamToFile(img.getInputStream(), new File(path));
				System.out.println(path);
				changeUser.setAvatar(imgName);
			}else {
				changeUser.setAvatar(user.getAvatar());
			}
			if(changeUser.getPassword()!=null) {
				changeUser.setPassword(MD5Util.digest(changeUser.getPassword()));
			}else {
				changeUser.setPassword(user.getPassword());
			}
			//更新数据
			int count=userService.doChang(changeUser);
			//构建返回结果
			result.setSuccess(count==1);
		} catch (Exception e) {
			e.printStackTrace();
			result.setMessage("有空值。");
			result.setSuccess(false);
		}
		return result;
	}
	
}
