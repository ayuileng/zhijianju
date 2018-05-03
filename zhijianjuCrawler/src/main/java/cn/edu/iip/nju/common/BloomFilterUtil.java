package cn.edu.iip.nju.common;

import cn.edu.iip.nju.dao.CrawledUrlDao;
import cn.edu.iip.nju.model.CrawledUrl;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
//单例模式
public class BloomFilterUtil {
    private static volatile BloomFilter<String> bf ;
    @Autowired
    private static CrawledUrlDao crawledUrlDao;

    private BloomFilterUtil(){}
    public static BloomFilter<String> getBFinstance(){
        if(bf == null){
            synchronized (BloomFilterUtil.class){
                if(bf == null){
                    bf = BloomFilter.create(Funnels.stringFunnel(Charset.forName("utf-8")),
                            1000000, 0.00001);
                    //每次程序启动就从数据库中预加载URL
                    int size = 1000;
                    Page<CrawledUrl> tmp = crawledUrlDao.findAll(new PageRequest(0, size));
                    for (int i = 0; i < tmp.getTotalPages(); i++) {
                        Page<CrawledUrl> all = crawledUrlDao.findAll(new PageRequest(i, size));
                        for (CrawledUrl crawledUrl : all) {
                            bf.put(crawledUrl.getUrl());
                        }
                    }
                }
            }
        }
        return bf;
    }
}
