package com.zhang.mynews.potal.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.zhang.mynews.bean.News;
import com.zhang.mynews.potal.dao.NewsMapper;
import com.zhang.mynews.potal.service.NewsService;
import com.zhang.mynews.util.Page;

@Service
public class NewsServiceImpl implements NewsService {

	@Autowired
	NewsMapper newsMapper;
	
	public int insertNews(News news) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		news.setCreationtime(simpleDateFormat.format(date));//加入创建日期
		news.setVisible(1);//加入可见性
		news.setType("今日要闻");//加入类型
		return newsMapper.insert(news);
	}

	//按页查询
	public Page<News> queryPage(Map<String,Object> paramMap) {
		
		Page<News> page = new Page<News>((Integer)paramMap.get("pageno"),(Integer)paramMap.get("pagesize"));
		
		Integer startIndex = page.getStartIndex();
		paramMap.put("startIndex", startIndex);
		
		List<News> datas = newsMapper.queryList(paramMap);
		
		page.setData(datas);
		
		Integer totalsize = newsMapper.queryCount(paramMap);
		
		page.setTotalsize(totalsize);		
		
		return page;
	}

	/**
	 * 根据id查找新闻
	 * @param newsid
	 * @return
	 */
	public News queryNewsById(int newsid) {
		News news = newsMapper.selectByPrimaryKey(newsid);
		if (news==null) {
			throw new RuntimeException("没有此新闻！");
		}
		return news;
	}
	

}
