package cn.edu.iip.nju.crawler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cn.edu.iip.nju.dao.WebDataDao;
import cn.edu.iip.nju.model.WebData;
import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 与2017.10.24整合测试 成功
 *
 * @author libo
 * 2018/04/24
 * 测试成功 61条
 */
@Component
public class Zhilianganquan {
    private final Logger logger = LoggerFactory.getLogger(Zhilianganquan.class);
    @Autowired
    private WebDataDao dao;
    private static String baseURL = "http://zlaq.cqn.com.cn";
    private ArrayList<String> urls1pre = new ArrayList<String>();//消费警示的preurl
    private ArrayList<String> urls1 = new ArrayList<String>();//消费警示的url
    private ArrayList<String> urls2 = new ArrayList<String>();//质量榜单的url
    private ArrayList<String> zhilaingbangdan = new ArrayList<String>();
    private ArrayList<String> urls2pre = new ArrayList<String>();//质量榜单的preurl
    private ArrayList<ArrayList<String>> urlsfinal = new ArrayList<ArrayList<String>>();

    private ArrayList<ArrayList<String>> pageurls() {
        try {
            Thread.sleep(1000);
            Document dd = Jsoup.connect(baseURL)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36")
                    .timeout(5000)
                    .get();
            Elements o = dd.select("div.hlayer3").select("a[href]");
            for (Element oi : o) {
                if (oi.text().equals("消费警示")) {
                    urls1pre.add(baseURL + oi.attr("href"));
                } else if (oi.text().equals("质量榜单")) {
                    zhilaingbangdan.add(baseURL + oi.attr("href"));
                }

            }

        } catch (Exception e) {
            e.getStackTrace();
        }

        for (String x : urls1pre) {
            try {
                Thread.sleep(1000);
                Document dd = Jsoup.connect(x)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36")
                        .timeout(5000)
                        .get();
                Elements o = dd.select("div.listbox").select("a[href]");
                for (Element oi : o) {
                    urls1.add(oi.attr("href"));
                }

            } catch (Exception e) {
                e.getStackTrace();
            }
        }
        urlsfinal.add(urls1);
        for (String x : zhilaingbangdan) {
            urls2pre.add(x);
            try {
                Thread.sleep(1000);
                Document dd = Jsoup.connect(x)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36")
                        .timeout(5000)
                        .get();
                Elements o = dd.select("div.dede_pages").select("ul.pagelist").select("a[href]");
                for (Element oi : o) {
                    if (oi.text().equals("末页")) {
                        String te = x.substring(0, x.lastIndexOf("/"));
                        urls2pre.add(te + "/" + oi.attr("href"));
                    }
                }

            } catch (Exception e) {
                e.getStackTrace();
            }
        }
        for (String x : urls2pre) {
            try {
                Thread.sleep(1000);
                Document dd = Jsoup.connect(x)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36")
                        .timeout(5000)
                        .get();
                Elements o = dd.select("div.listbox").select("a[href]");
                for (Element oi : o) {
                    urls2.add(baseURL + oi.attr("href"));
                }

            } catch (Exception e) {
                e.getStackTrace();
            }
        }
        urlsfinal.add(urls2);
        return urlsfinal;
    }

    public void start() {
        ArrayList<ArrayList<String>> t = pageurls();
        ArrayList<String> xiaofei = t.get(0);
        System.out.println(xiaofei.size());
        for (String x : xiaofei) {
            try {
                Thread.sleep(1000);
                Document dd = Jsoup.connect(x)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36")
                        .timeout(5000)
                        .get();
                WebData temp = new WebData();
                Element o = dd.select("div.Index_ShowDetail_Title").first();
                temp.setTitle(o.text());
                Element p = dd.select("div.Index_ShowDetail_Time").first();
                String oriDate = p.text().trim();
                String date = oriDate.substring(oriDate.lastIndexOf("2"), oriDate.lastIndexOf(" "));
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date d = sdf.parse(date);
                temp.setPostTime(d);
                Elements tr = dd.select("div.Index_ShowDetail_Content");

                String text = "";
                for (Element ti : tr) {
                    if (ti.hasText()) {
                        text = text + ti.text() + "/n";
                    }
                }
                temp.setSourceName("中国新闻网--质量安全");
                temp.setContent(text);
                temp.setUrl(x);
                temp.setCrawlerTime(new Date());
                temp.setHtml(dd.toString());
                dao.save(temp);
                //System.out.println("-------------------");
            } catch (Exception e) {
                e.getStackTrace();
            }
        }
        ArrayList<String> bangdan = t.get(1);

        for (String x : bangdan) {
            try {
                Thread.sleep(1000);
                Document dd = Jsoup.connect(x)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36")
                        .timeout(5000)
                        .get();
                WebData temp1 = new WebData();
                Element o = dd.select("div.title").first();
                temp1.setTitle(o.text());
                Element p = dd.select("div.info").first();
                String oriDate = p.text();
                String date = oriDate.substring(oriDate.indexOf("2"), oriDate.indexOf(" "));
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date d = sdf.parse(date);
                temp1.setPostTime(d);
                Elements tr = dd.select("div.content");
                String text = "";
                for (Element ti : tr) {
                    if (ti.hasText()) {
                        text = text + ti.text() + "/n";
                    }
                }
                temp1.setSourceName("中国新闻网--质量安全");
                temp1.setContent(text);
                temp1.setUrl(x);
                temp1.setHtml(dd.toString());
                temp1.setCrawlerTime(new Date());
                dao.save(temp1);
                logger.info("zhilianganquan save success");
            } catch (Exception e) {
                e.getStackTrace();
                logger.error("zhiliananquan sava fail");
            }
        }
    }


}
