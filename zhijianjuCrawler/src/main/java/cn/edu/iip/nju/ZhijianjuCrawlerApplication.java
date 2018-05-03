package cn.edu.iip.nju;

import cn.edu.iip.nju.crawler.*;
import cn.edu.iip.nju.crawler.fujian.ExcelProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import java.io.UnsupportedEncodingException;
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


    //    @Autowired
//    CPZLJDS cpzljds;//问题很大，网页变了，而且附件也没了
    @Override
    public void run(String... strings) throws Exception {

//
//        pool.submit(() -> zhaoHui.start());
//        pool.submit(() -> suwangZhijian.start());
//        pool.submit(() -> xiaoxie.start());
//        pool.submit(() -> jiangSu.start());
//        pool.submit(() -> gjzljdjyjyzj.start());
//        pool.submit(() -> bjxfzxh.start());
//        pool.submit(() -> lnxfzxh.start());
//        pool.submit(() -> zhilianganquan.start());
//        pool.submit(() -> zhiliangxinwenwang.start());
//        newsCrawler.start360NewsCrawler();
//        logger.info("360 wancheng!");
//        newsCrawler.startBaiduNewsCrawler();
//        logger.info("baidu wancheng ");
//        newsCrawler.startSougouNewsCrawler();
//        logger.info("ssougou wancheng!");
    }


    public static void main(String[] args) {
        SpringApplication.run(ZhijianjuCrawlerApplication.class, args);
    }
}
