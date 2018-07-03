package com.test.demo.service;
import java.util.Date;

import com.test.demo.dao.NewsDataDao;
import com.test.demo.dao.WebDataDao;
import com.test.demo.model.NewsData;
import com.test.demo.model.WebData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.jws.Oneway;
import java.util.ArrayList;
import java.util.List;




@Service
public class WebDataService {
    @Autowired
    private WebDataDao webDataDao;
    @Autowired
    private NewsDataDao newsDataDao;

    @Transactional
    public void transfer(){
        Page<WebData> pageInfo = webDataDao.findAll(new PageRequest(0, 2000));
        int pages = pageInfo.getTotalPages();
        for (int i = 0; i < pages; i++) {
            Page<WebData> webData = webDataDao.findAll(new PageRequest(i, 2000));
            List<WebData> webDataList = webData.getContent();
            List<NewsData> newsDataList = cast(webDataList);
            newsDataDao.save(newsDataList);

        }
    }
    private List<NewsData> cast( List<WebData> webDataList){
        List<NewsData> list = new ArrayList<>();
        for (WebData webData : webDataList) {
            NewsData newsData = new NewsData();
            newsData.setTitle(webData.getTitle());
            newsData.setContent(webData.getContent());
            newsData.setPostTime(webData.getPostTime());
            newsData.setCrawlerTime(webData.getCrawlTime());
            newsData.setUrl(webData.getUrl());
            newsData.setInjureNews(false);
            list.add(newsData);
        }
        return list;
    }



}
