package cn.edu.iip.nju.service;

import cn.edu.iip.nju.dao.NewsDataDao;
import cn.edu.iip.nju.dao.WebDataDao;
import cn.edu.iip.nju.model.NewsData;
import cn.edu.iip.nju.model.WebData;
import com.google.common.io.Files;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

@Service
public class NewsDataService {
    @Autowired
    NewsDataDao newsDataDao;
    @Autowired
    WebDataDao webDataDao;

    public void getNewsBy4() throws IOException {
        String regex = "|@|";
        List<Integer> ids = Lists.newArrayList();
        for (int i = 1; i <= 1000; i++) {
            ids.add(16000 + i * 4);
        }
        int count = 0;
        List<NewsData> newsDataList = newsDataDao.getAllByIdIn(ids);
        Resource resource = new FileSystemResource("C:\\Users\\yajima\\Desktop\\5.csv");
        File file = resource.getFile();
        Resource resultResource = new FileSystemResource("C:\\Users\\yajima\\Desktop\\res1.csv");
        File resultFile = resultResource.getFile();
        List<String> lines = Files.readLines(file, Charset.forName("utf-8"));
        for (int i = 0; i < lines.size(); i++) {
            String result = lines.get(i).substring(lines.get(i).length() - 1);
            count += Integer.valueOf(result);
            String line = newsDataList.get(i).getTitle() + regex + newsDataList.get(i).getContent().replaceAll("[\\t\\n\\r]", "") + regex + result + "\n";
            Files.append(line, resultFile, Charset.forName("utf-8"));
            newsDataList.get(i).setInjureNews("1".equals(result));
        }
        newsDataDao.save(newsDataList);
        System.out.println(count);
    }

    //5421+371 19040 23191
    public void transfer(){
        List<WebData> webDataList = webDataDao.getAllByIdGreaterThan(13249);
        List<NewsData> newsDataList = Lists.newArrayList();
        for (WebData webData : webDataList) {
            NewsData newsData = new NewsData();
            newsData.setTitle(webData.getTitle());
            newsData.setContent(webData.getContent());
            newsData.setPostTime(webData.getPostTime());
            newsData.setCrawlerTime(webData.getCrawlerTime());
            newsData.setUrl(webData.getUrl());
            newsDataList.add(newsData);
        }
        newsDataDao.save(newsDataList);
    }
}
