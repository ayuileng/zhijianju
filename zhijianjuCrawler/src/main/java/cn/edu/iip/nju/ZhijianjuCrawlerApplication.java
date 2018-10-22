package cn.edu.iip.nju;

import cn.edu.iip.nju.crawler.*;
import cn.edu.iip.nju.crawler.fujian.ExcelProcess;
import cn.edu.iip.nju.dao.NewsDataDao;
import cn.edu.iip.nju.model.NewsData;
import cn.edu.iip.nju.service.NewsDataService;
import cn.edu.iip.nju.service.RedisService;
import cn.edu.iip.nju.util.ReadFileUtil;
import org.assertj.core.util.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;

@SpringBootApplication
public class ZhijianjuCrawlerApplication implements CommandLineRunner {
    private static Logger logger = LoggerFactory.getLogger(ZhijianjuCrawlerApplication.class);
    @Autowired
    ExecutorService pool;
    //京东
    @Autowired
    JingDongCrawler jingDongCrawler;
    //质检网站
    @Autowired
    ZhaoHui zhaoHui;//ok 3000tiao
    @Autowired
    SuwangZhijian suwangZhijian;//ok
    @Autowired
    Xiaoxie xiaoxie;//ok 2743条
    @Autowired
    JiangSu jiangSu;//ok 有下载
    @Autowired
    GJZLJDJYJYZJ gjzljdjyjyzj;//ok 有下载
    @Autowired
    BJXFZXH bjxfzxh;
    @Autowired
    LNXFZXH lnxfzxh;
    //11111111111111111111111111


    @Autowired
    Zhiliangxinwenwang zhiliangxinwenwang;
    @Autowired
    Zhilianganquan zhilianganquan;

    //附件处理
    @Autowired
    ExcelProcess excelProcess;//ok

    //医院数据
    @Autowired
    HospotalDataService hospotalDataService;//ok

    //新闻网站
    @Autowired
    NewsCrawler newsCrawler;

    @Autowired
    Chinanews chinanews;
    @Autowired
    Fenghuang fenghuang;

    @Autowired
    Suzhou suzhou;


    @Autowired
    RedisService redisService;

    @Autowired
    NewsDataService newsDataService;

    //    @Autowired
//    CPZLJDS cpzljds;//问题很大，网页变了，而且附件也没了
    @Override
    public void run(String... strings) throws Exception {
        pool.submit(() -> {
            newsCrawler.startBaiduNewsCrawler();
            logger.info("baidu success");
        });
        pool.submit(() -> {
            newsCrawler.startSougouNewsCrawler();
            logger.info("sougou success");
        });
        pool.submit(() -> {
            newsCrawler.start360NewsCrawler();
            logger.info("360 success");
        });
        pool.shutdown();
    }


    public static void main(String[] args) {
        SpringApplication.run(ZhijianjuCrawlerApplication.class, args);
    }
}
