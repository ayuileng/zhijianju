package cn.edu.iip.nju.crawler;

import cn.edu.iip.nju.dao.WebDataDao;
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

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by libo on 2017/11/27.
 * 2018/04/24
 * 关键字 小数据测试通过
 */
//http://search.ifeng.com/sofeng/search.action?q=%E7%94%B5%E5%8A%A8%E8%BD%A6%2B%E7%A2%B0%E6%92%9E&c=1&p=1
//搜索后的页面请求地址
@Component
public class Fenghuang implements Crawler {
    private static Logger logger = LoggerFactory.getLogger(Fenghuang.class);
    @Autowired
    private WebDataDao dao;
    private String baseurl = "http://search.ifeng.com/sofeng/search.action?q=";
    private Random ran = new Random(1000);

    public ArrayList<String> geturl() throws Exception {
        ArrayList<String> urls = new ArrayList<>();

        //关键字转码
        Set<String> keyWords = ReadFileUtil.readKeyWords();
        for (String keyWord : keyWords) {
            String[] words = keyWord.split(" ");
            String productEncoder = URLEncoder.encode(words[0], "UTF-8");
            String injureEncoder = URLEncoder.encode(words[1], "UTF-8");
            String temp = productEncoder + "%2B" + injureEncoder + "&c=1&p=";
            for (int i = 1; i <= 3; i++) {
                urls.add(baseurl + temp + i);
            }
        }
        return urls;
    }

    public ArrayList<String> getallpaperurl(ArrayList<String> urls) throws Exception {
        ArrayList<String> finalurls = new ArrayList<>();
        for (int i = 0; i < urls.size(); i++) {
            String x = urls.get(i);
            try {
                Thread.sleep(ran.nextInt(1000) + 1000);
                Document dd = Jsoup.connect(x)
                        //Jsoup.connect("http://search.ifeng.com/sofeng/search.action?q=%E7%94%B5%E5%8A%A8%E8%BD%A6%2B%E7%A2%B0%E6%92%9E&c=1&p=1")
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36")
                        .timeout(5000)
                        //   .proxy(proxy)
                        .get();
                Elements p = dd.select("div.mainM").first().select("div.searchResults").select("a[href]");
                //   System.out.println("p:"+p.toString());
                for (Element pi : p) {
                    finalurls.add(pi.attr("href").toString());
                }

            } catch (Exception e) {
                //e.getStackTrace();
            }
        }
      /*  for(String s:finalurls){
            System.out.println(s);
        }*/
        //System.out.println("最终获取页面"+finalurls.size()+"个");
        return finalurls;
    }

    public void getdata(ArrayList<String> urls) throws Exception {
        for (int i = 0; i < urls.size(); i++) {
            String x = urls.get(i);
            try {
                Thread.sleep(ran.nextInt(1000) + 1000);
                Document dd = Jsoup.connect(x)
                        // Jsoup.connect("http://news.ifeng.com/a/20160314/47850748_0.shtml")
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36")
                        //.timeout(5000).proxy(proxy)
                        .get();
                // System.out.println(dd.toString());
                WebData data = new WebData();
                Element p = dd.select("div.left").select("div#artical").first();
                //System.out.println("p:"+p);
                Element o = p.select("h1#artical_topic").first();
                // System.out.println("title:"+o.text());
                data.setTitle(o.text());
                String date = p.select("div#artical_sth").select("p.p_time").text();
                //System.out.println("date:"+date);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
                Date d = sdf.parse(date.substring(0, date.indexOf(" ")));
                //System.out.println("d:"+d.toString());
                data.setPostTime(d);
                String temp = p.select("div#main_content").select("p").text();
                // System.out.println("temp:"+temp);
                data.setContent(temp);
                data.setUrl(x);
                data.setCrawlTime(new Date());
                data.setSourceName("凤凰网");
                data.setHtml(dd.toString());
                dao.save(data);
                logger.info("fenghuang wang save success");
            } catch (Exception e) {
                //e.getStackTrace();
                //System.out.println("save 凤凰网"+x+"  faild...............................................");
            }
        }
    }



    @Override
    public void start() {

        try {
            ArrayList<String> urls = geturl();
            ArrayList<String> finalurls = getallpaperurl(urls);
            getdata(finalurls);
        } catch (Exception e) {
            logger.error("fenghuang wang save fail");
            //e.printStackTrace();
        }
    }
}

