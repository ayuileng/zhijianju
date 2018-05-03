package com.test.demo.service;

import com.test.demo.dao.NewsDataDao;
import com.test.demo.dao.NewsDataOriDao;
import com.test.demo.dao.WebDataDao;
import com.test.demo.dao.WebDataDaoOri;
import com.test.demo.model.NewsData;
import com.test.demo.model.NewsDataOri;
import com.test.demo.model.WebData;
import com.test.demo.model.WebDataOri;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class NewsDataService {
    private static Logger logger = LoggerFactory.getLogger(NewsDataService.class);
    @Autowired
    private NewsDataDao newsDataDao;
    @Autowired
    private NewsDataOriDao newsDataOriDao;

    public Long countOri1(){
        return newsDataDao.count();
    }//3744
    public Long countOri2(){
        return newsDataOriDao.count();
    }//440

    public void transfer(){
        Page<NewsData> tmp = newsDataDao.findAll(new PageRequest(0, 200));
        for (int i = 0; i < tmp.getTotalPages(); i++) {
            Page<NewsData> page = newsDataDao.findAll(new PageRequest(i, 200));
            List<NewsData> list = page.getContent();
            List<NewsDataOri> list1 = cast(list);
            newsDataOriDao.save(list1);
            logger.info("save done");
        }
    }

    private List<NewsDataOri> cast(List<NewsData> list) {
        List<NewsDataOri> res = new ArrayList<>();
        for (NewsData webData : list) {
            NewsDataOri ori = new NewsDataOri();
            ori.setContent(webData.getContent());
            ori.setCrawlerTime(webData.getCrawlerTime());
            ori.setTitle(webData.getTitle());
            ori.setUrl(webData.getUrl());
            res.add(ori);
        }
        return res;
    }
}
