package cn.edu.iip.nju.crawler;

import cn.edu.hfut.dmic.contentextractor.News;
import cn.edu.iip.nju.dao.NewsDataDao;
import cn.edu.iip.nju.dao.WebDataDao;
import cn.edu.iip.nju.model.NewsData;
import cn.edu.iip.nju.model.WebData;
import cn.edu.iip.nju.util.ReadFileUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by libo on 2017/11/29.
 * 2018/04/24
 * 跑小数据量没问题
 */
@Component
public class Chinanews implements Crawler {
    private static Logger logger = LoggerFactory.getLogger(Chinanews.class);

    @Autowired
    private NewsDataDao dao;
    private String baseurl = "http://sou.chinanews.com.cn/search.do?q=";
    private Random ran = new Random(1000);

//    public ArrayList<String> geturl(Set<String> pkword, Set<String> ikword) throws Exception {
//        ArrayList<String> urls = new ArrayList<>();
//        for (String product : pkword) {
//            //关键字转码
//            String paim = URLEncoder.encode(product, "UTF-8");
//            for (String injure : ikword) {
//                String iaim = URLEncoder.encode(injure, "UTF-8");
//                urls.add(baseurl + paim + "%20" + iaim);
//            }
//        }
//        return urls;
//    }

    public ArrayList<String> geturl() throws UnsupportedEncodingException {
        Set<String> keyWords = ReadFileUtil.readKeyWords();
        ArrayList<String> urls = new ArrayList<>();
        for (String keyWord : keyWords) {
            logger.info(keyWord);
            String[] words = keyWord.split(" ");
            String productEncode = URLEncoder.encode(words[0], "UTF-8");
            String injureEncoder = URLEncoder.encode(words[1], "UTF-8");
            urls.add(baseurl + productEncode + "%20" + injureEncoder);
        }
        return urls;
    }

    public ArrayList<String> getallpaperurl(ArrayList<String> urls){
        ArrayList<String> finalurls = new ArrayList<>();
        for (int i = 0; i < urls.size(); i++) {
            String x = urls.get(i);
            try {
                Thread.sleep(ran.nextInt(1000) + 1000);
                Document dd = Jsoup.connect(x)
                        // Jsoup.connect("http://sou.chinanews.com.cn/search.do?q=%E7%94%B5%E5%8A%A8%E8%BD%A6%20%E6%92%9E%E5%87%BB")
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36")
                        .timeout(5000)
                        //   .proxy(proxy)
                        .get();
                Elements p = dd.select("div#news_list").first().select("table").select("li.news_title").select("a[href]");
                //System.out.println("p:"+p.toString());
                for (Element pi : p) {
                    finalurls.add(pi.attr("href"));
                }
            } catch (Exception e) {
                //e.getStackTrace();
            }
        }
        return finalurls;
    }

    public void getdata(ArrayList<String> urls) {
        for (int i = 0; i < urls.size(); i++) {
            String x = urls.get(i);
            try {
                Thread.sleep(ran.nextInt(1000) + 1000);
                Document dd = Jsoup.connect(x)
                        //Jsoup.connect("http://www.chinanews.com/life/2013/08-15/5166857.shtml")
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36")
                        //.timeout(5000).proxy(proxy)
                        .get();
                //System.out.println(dd.toString());
                NewsData data = new NewsData();
                Element p = dd.select("div.con_left").select("div#cont_1_1_2").first();
                //System.out.println(p);
                String title = p.select("h1").first().text();
                data.setTitle(title);
                String date = p.select("div.left-time").select("div.left-t").text();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
                Date d = sdf.parse(date.substring(0, date.indexOf(" ")));
                // System.out.println("d:"+d.toString());
                data.setPostTime(d);
                String temp = p.select("div.left_zw").select("p").text();
                data.setContent(temp);
                data.setCrawlerTime(new Date());
                data.setUrl(x);
                logger.info("save china news done");

            } catch (Exception e) {
//                e.getStackTrace();

            }
        }
    }


    @Override
    public void start() {
        try {
            ArrayList<String> urls = geturl();
            ArrayList<String> finalurl = getallpaperurl(urls);
            getdata(finalurl);
        } catch (Exception e) {
            logger.error("china news fail");
            e.printStackTrace();
        }

    }
}
