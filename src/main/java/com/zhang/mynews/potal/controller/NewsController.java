package com.zhang.mynews.potal.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.zhang.mynews.bean.News;
import com.zhang.mynews.bean.User;
import com.zhang.mynews.potal.service.NewsService;
import com.zhang.mynews.util.AjaxResult;
import com.zhang.mynews.util.Const;
import com.zhang.mynews.util.Page;

@Controller
@RequestMapping("/news")
public class NewsController {
	
	
	@Autowired
	NewsService newsService;
	
	/**
	 * 将发布的新闻存入数据库
	 * @param news
	 * @param coverimg
	 * @param session
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/doPublish" , method = RequestMethod.POST)
	public AjaxResult doEdit(
			News news,
			@RequestParam(value = "coverimg",required = false) MultipartFile coverimg,
			HttpSession session) {
		
		AjaxResult result = new AjaxResult();
		try {
			User user=(User)session.getAttribute(Const.LOGIN_USER);
			news.setAuthor(user.getId());//设置作者
			if(coverimg!=null) {
				String imgName = UUID.randomUUID().toString()+coverimg.getOriginalFilename()
								.substring(coverimg.getOriginalFilename().lastIndexOf("."));
				String path=session.getServletContext().getRealPath("coverImg")+"/"+imgName;
				FileUtils.copyInputStreamToFile(coverimg.getInputStream(), new File(path));
				System.out.println(path);
				news.setCoverpicture(imgName);//设置封面图
			}
			//插入数据
			int count=newsService.insertNews(news);
			//构建返回结果
			result.setSuccess(count==1);
		} catch (Exception e) {
			e.printStackTrace();
			result.setMessage("插入错误");
			result.setSuccess(false);
		}
		return result;
	}
	
	/**
	 * 新闻编辑页图片上传
	 * @param file
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/uploadImage",method = RequestMethod.POST)  
    public Map<String,Object> uploadFile(
    		@RequestParam(value = "upfile", required = false) MultipartFile file,   
            HttpServletRequest request,
            HttpServletResponse response,
            HttpSession session){ 
		
		System.out.println("uploadFile方法被执行");
		Map<String,Object> map = new HashMap<String, Object>();
		try {
			 
			 String imgName = UUID.randomUUID().toString()+file.getOriginalFilename()
							.substring(file.getOriginalFilename().lastIndexOf("."));
		String path=session.getServletContext().getRealPath("ueditor/img")+"/"+imgName;
			FileUtils.copyInputStreamToFile(file.getInputStream(), new File(path));
			
		map.put("state", "SUCCESS");// UEDITOR的规则:不为SUCCESS则显示state的内容
		map.put("url","/ueditor/img/"+imgName);         //能访问到你现在图片的路径 这里
		map.put("title","");
		map.put("original","imgName" );         
		} catch (IOException e) {
			// TODO Auto-generated catch block		
			map.put("state", "文件上传失败!"); //在此处写上错误提示信息，这样当错误的时候就会显示此信息
	   	map.put("url","");
	   	map.put("title", "");
	   	map.put("original", "");    	
	   	e.printStackTrace();
		}		 
		 return map;
	 }
	
	@ResponseBody
	@RequestMapping(value = "/showByPage" , method = RequestMethod.POST)
	public AjaxResult showByPage(@RequestParam(value="pageno",required=false,defaultValue="1") Integer pageno,
			@RequestParam(value="pagesize",required=false,defaultValue="10") Integer pagesize,
			String queryText,
			HttpSession session){
		AjaxResult result = new AjaxResult();
		try {
			User user = (User)session.getAttribute(Const.LOGIN_USER);
			Map<String,Object> paramMap = new HashMap<String,Object>();
			paramMap.put("pageno", pageno);
			paramMap.put("pagesize", pagesize);
			if(user!=null) {
				paramMap.put("author", user.getId());			
			}
			
			
			if((queryText!=null)&&(!queryText.isEmpty())){
				
				if(queryText.contains("%")){
					queryText = queryText.replaceAll("%", "\\\\%");
				}
				
				paramMap.put("queryText", queryText); //   \%
			}
			Page<News> page = newsService.queryPage(paramMap);
			
			result.setSuccess(true);
			
			result.setPage(page);
			
		} catch (Exception e) {
			result.setSuccess(false);
			e.printStackTrace();
			result.setMessage("查询数据失败!");
		}
		 
		return result; //将对象序列化为JSON字符串,以流的形式返回.
	}
	
	@ResponseBody
	@RequestMapping(value = "/showNewsList" , method = RequestMethod.POST)
	public AjaxResult showNewsList(@RequestParam(value="pageno",required=false,defaultValue="1") Integer pageno,
			@RequestParam(value="pagesize",required=false,defaultValue="10") Integer pagesize,
			String queryText){
		AjaxResult result = new AjaxResult();
		try {
			Map<String,Object> paramMap = new HashMap<String,Object>();
			paramMap.put("pageno", pageno);
			paramMap.put("pagesize", pagesize);
			
			if((queryText!=null)&&(!queryText.isEmpty())){
				
				if(queryText.contains("%")){
					queryText = queryText.replaceAll("%", "\\\\%");
				}
				
				paramMap.put("queryText", queryText); //   \%
			}
			Page<News> page = newsService.queryPage(paramMap);
			
			result.setSuccess(true);
			
			result.setPage(page);
			
		} catch (Exception e) {
			result.setSuccess(false);
			e.printStackTrace();
			result.setMessage("查询数据失败!");
		}
		 
		return result; //将对象序列化为JSON字符串,以流的形式返回.
	}
	
	@RequestMapping(value = "/showNews" , method = RequestMethod.GET)
	public String showNews(int newsid,HttpServletRequest req) {
		AjaxResult result = new AjaxResult();
		try {
			News news=newsService.queryNewsById(newsid);
			req.setAttribute(Const.NEWS, news);
			result.setSuccess(true);
		} catch (Exception e) {
			result.setSuccess(false);
			result.setMessage(e.getMessage());
		}
		return "show";
	}
	 
	 
}
