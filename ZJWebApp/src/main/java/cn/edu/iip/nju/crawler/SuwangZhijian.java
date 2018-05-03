package cn.edu.iip.nju.crawler;

import cn.edu.iip.nju.model.WebData;
import cn.edu.iip.nju.dao.WebDataDao;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 苏网质检,爬取相关板块的正文部分
 * Created bylibo 2016/11/9.
 * div class .
 * div id    #
 */
@Component
public class SuwangZhijian{

    @Autowired
    private WebDataDao dao;
    //private static dataDAO dao = new dataDAO();
    private static String baseURL = "http://jszj.jschina.com.cn";
    private static ArrayList<String> urlspre = new ArrayList<String>();
    private static ArrayList<String> urls = new ArrayList<String>();

    private ArrayList<String> pageurls() {
        try {
            Document doc = Jsoup.connect(baseURL)
                    .userAgent("Mozilla")
                    .timeout(5000)
                    .get();
            Element listper = doc.select("div.other_page").first();
            Elements list = listper.select("div.box");
            for (Element lis : list) {
                if (lis.text().equals("权威发布") || lis.text().equals("曝光台") || lis.text().equals("质监舆情")) {
                    String te = lis.select("a[href]").attr("href").toString();
                    String tee = baseURL + te.substring(te.indexOf("/"), te.length());
                    urlspre.add(tee);
                }
            }
            //urlspre.add("http://jsgs.jschina.com.cn/xfjs");//添加红盾在线-消费警示
            for (String x : urlspre) {
                try {
                    Thread.sleep(1000);
                    Document docc = Jsoup.connect(x)
                            .userAgent("Mozilla")
                            .timeout(5000)
                            .get();
                    Elements o = docc.select("div.biaot");
                    for (Element oi : o) {
                        String te = oi.select("a[href]").attr("href").toString();
                        String tee = te.substring(te.indexOf("/"));
                        urls.add(x.substring(0, x.length() - 1) + tee);
                    }

                } catch (Exception e) {
                    e.getStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return urls;
    }

    public void start() throws Exception {
        ArrayList<String> urls = pageurls();
        for (String x : urls) {
            try {
                Document doc = Jsoup.connect(x)
                        .userAgent("Mozilla")
                        .timeout(5000)
                        .get();
                WebData data = new WebData();
                Elements p = doc.select("div.text");
                if (p.hasText()) {
                    data.setTitle(p.select("h2#title").text());
                    Element dateTag = p.select("div.info").select("span#pubtime_baidu").first();
                    String oriDate = dateTag.text();
                    String date = oriDate.substring(0, oriDate.indexOf(" "));
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date d = sdf.parse(date);
                    data.setPostTime(d);

                    Elements contentTag = p.select("div.article");
                    StringBuilder texttemp = new StringBuilder();
                    for (Element text : contentTag) {
                        texttemp.append(text.text()).append("/n");
                    }
                    data.setSourceName("中国江苏网--苏网质检");
                    data.setContent(texttemp.toString());
                    data.setUrl(x);
                    data.setHtml(doc.toString());
                    data.setCrawlTime(new Date());

                    dao.save(data);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}










