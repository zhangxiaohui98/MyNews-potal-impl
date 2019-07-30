package com.zhang.mynews.potal.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zhang.mynews.bean.User;
import com.zhang.mynews.exception.LoginFailException;
import com.zhang.mynews.potal.dao.UserMapper;
import com.zhang.mynews.potal.service.UserService;
import com.zhang.mynews.util.MD5Util;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserMapper userMapper;
	
	/**
	 * 	用户登录
	 */
	public User queryUserLogin(HashMap<String,Object> paramMap) {
		//查询用户名对应的user对象
		User user=userMapper.queryUserByUsername(paramMap);
		//检查结果
		if(user==null) {
			throw new LoginFailException("用户名不存在！");
		}else if(!(user.getPassword()).equals(
				MD5Util.digest((String)paramMap.get("password")))){
			throw new LoginFailException("密码错误！");
		}
		return user;
	}
	
	/**
	 * 注册用户
	 * @param paramMap
	 * @return
	 */
	public int doReg(User user) {
		//加入创建时间
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		user.setCreationtime(simpleDateFormat.format(date));
		//加入头像
		user.setAvatar("me.jpg");
		//加密密码
		user.setPassword(MD5Util.digest(user.getPassword()));
		//插入数据库
		int count =userMapper.insert(user);
		
		return count;
	}

	/**
	 * 修改用户信息
	 */
	public int doChang(User changeUser) {
		return userMapper.updateByPrimaryKey(changeUser);
	}

}
