package com.test.demo.service;

import com.test.demo.dao.WebDataDao;
import com.test.demo.dao.WebDataDaoOri;
import com.test.demo.model.WebData;
import com.test.demo.model.WebDataOri;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;




@Service
public class WebDataService {
    @Autowired
    private WebDataDao webDataDao;//2
    @Autowired
    private WebDataDaoOri webDataDaoOri;//1

    public Long countOri1(){
        return webDataDaoOri.count();
    }//3744
    public Long countOri2(){
        return webDataDao.count();
    }//440

    public void transfer(){
        Page<WebData> tmp = webDataDao.findAll(new PageRequest(0, 200));
        for (int i = 0; i < tmp.getTotalPages(); i++) {
            Page<WebData> page = webDataDao.findAll(new PageRequest(i, 200));
            List<WebData> list = page.getContent();
            List<WebDataOri> list1 = cast(list);
            webDataDaoOri.save(list1);
        }
    }

    private List<WebDataOri> cast(List<WebData> list) {
        List<WebDataOri> res = new ArrayList<>();
        for (WebData webData : list) {
            WebDataOri ori = new WebDataOri();
            ori.setContent(webData.getContent());
            ori.setCrawlTime(webData.getCrawlTime());
            ori.setHtml(webData.getHtml());
            ori.setPostTime(webData.getPostTime());
            ori.setSourceName(webData.getSourceName());
            ori.setTitle(webData.getTitle());
            ori.setUrl(webData.getUrl());
            res.add(ori);
        }
        return res;
    }
}
