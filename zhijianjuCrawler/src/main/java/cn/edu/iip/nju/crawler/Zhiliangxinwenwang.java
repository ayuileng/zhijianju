package cn.edu.iip.nju.crawler;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cn.edu.iip.nju.dao.WebDataDao;
import cn.edu.iip.nju.model.WebData;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 与2017.10.24整合测试 成功
 * 这个网站信息量大，历时6h 20m 14s
 * 后续可能进行取舍
 *
 * @author libo
 */
@Component
public class Zhiliangxinwenwang implements Crawler{
    @Autowired
    private WebDataDao dao;

    private static Logger logger = LoggerFactory.getLogger(Zhiliangxinwenwang.class);
    private String baseURL = "http://www.cqn.com.cn/ms/";
    private String urlspreminsheng = "";
    private String urlsprezhaohui = "";
    private ArrayList<String> urlspre = new ArrayList<String>();
    private ArrayList<String> urls = new ArrayList<String>();
    private ArrayList<String> urlsfinal = new ArrayList<String>();
    private ArrayList<String> urlguojiadifang = new ArrayList<String>();

    private ArrayList<String> pageurls() throws Exception {
        try {
            Thread.sleep(1000);
            Document doc = Jsoup.connect(baseURL)
                    .userAgent("Mozilla/4.0(compatible ；MSIE 8.0；Windows NT 6.0)")
                    .timeout(5000)
                    .get();
            Elements lists = doc.select("div.subclass").select("a[href]");
            for (Element lis : lists) {
                if (lis.text().equals("公告")) {
                    urlspreminsheng = baseURL + lis.attr("href");
                }
                if (lis.text().equals("舆情") || lis.text().equals("召回") || lis.text().equals("投诉")) {
                    urlspre.add(baseURL + lis.attr("href"));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(urlspreminsheng);
        try {
            Thread.sleep(1000);
            Document dd = Jsoup.connect(urlspreminsheng)
                    //Jsoup.connect(urlspreminsheng)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36")
                    .timeout(5000)
                    .get();
            Elements o = dd.select("div.List_Sub_List").select("dt");
            for (Element oi : o) {
                if (oi.select("h2").text().trim().equals("产品质量调查")) {
                    urlspre.add(baseURL + oi.select("a[href]").attr("href"));
                }
                if (oi.select("h2").text().trim().equals("国家抽查") ||
                        oi.select("h2").text().trim().equals("地方抽查")) {
                    urlguojiadifang.add(baseURL + oi.select("a[href]").attr("href"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (String x : urlguojiadifang) {
            try {
                Thread.sleep(1000);
                Document dd = Jsoup.connect(x)
                        //Jsoup.connect(urlspreminsheng)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36")
                        .timeout(5000)
                        .get();
                Elements o = dd.select("div.List_Sub_List").select("dt");
                for (Element oi : o) {
                    if (oi.select("h2").text().trim().equals("2016") ||
                            oi.select("h2").text().trim().equals("2015") ||
                            oi.select("h2").text().trim().equals("2014")) {
                        urlspre.add(baseURL + oi.select("a[href]").attr("href"));
                    }
                }
            } catch (Exception e) {
                e.getStackTrace();
            }

        }
        for (String x : urlspre) {
            urls.add(x);
            try {
                Thread.sleep(1000);
                Document dd = Jsoup.connect(x)
                        //Jsoup.connect(urlspreminsheng)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36")
                        .timeout(5000)
                        .get();
                Elements o = dd.select("center").select("a[href]");
                // System.out.println(o);
                String num = "";
                String numnew = "";
                String use = "";
                for (Element oi : o) {
                    if (oi.text().trim().equals("尾页")) {
                        num = oi.attr("href");
                    }
                }
                numnew = num.substring(0, num.length() - 4);
                String[] yy = numnew.split("_");
                numnew = yy[yy.length - 1];
                // System.out.println(numnew);
                int t = Integer.parseInt(numnew);
                // System.out.println(num);
                use = yy[0];
                for (int i = 1; i < yy.length - 1; i++) {
                    use = use + "_" + yy[i];
                }
                use = use + "_";
                for (int i = 2; i <= t; i++) {
                    urls.add(baseURL + use + i + ".htm");
                }


            } catch (Exception e) {
                e.getStackTrace();
            }
        }
        for (String x : urls) {
            System.out.println(x);
            try {
                Thread.sleep(1000);
                Document dd = Jsoup.connect(x)
                        //Jsoup.connect(urlspreminsheng)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36")
                        .timeout(5000)
                        .get();
                Elements o = dd.select("div.List_List").select("dd.cont_l").select("a[href]");
                for (Element oi : o) {
                    urlsfinal.add(baseURL + oi.attr("href"));
                }
            } catch (Exception e) {
                e.getStackTrace();
            }
        }


        return urlsfinal;
    }


    public void startCrawler() throws Exception {
        //ArrayList<databasestruc> out=new ArrayList();
        ArrayList<String> t = pageurls();
        for (String x : t) {
            try {
                Thread.sleep(1000);
                Document dd = Jsoup.connect(x)
                        //Jsoup.connect(urlspreminsheng)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36")
                        .timeout(5000)
                        .get();
                WebData temp = new WebData();
                Elements o = dd.select("div.Detail_Title").select("h1");
                if (o.hasText()) {
                    temp.setTitle(o.text());
                }
                Element p = dd.select("div.publish").first();
                String oriDate = p.text();
                String date = oriDate.substring(0, oriDate.indexOf(" "));
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date d = sdf.parse(date);
                temp.setPostTime(d);
                Elements h = dd.select("div.Detail_Content").select("div.content");
                String texttemp = "";
                for (Element hi : h) {
                    texttemp = texttemp + hi.text() + "/n";
                }
                temp.setSourceName("中国质量新闻网--民生");
                temp.setCrawlerTime(new Date());
                temp.setContent(texttemp);
                temp.setUrl(x);
                temp.setHtml(dd.toString());
                dao.save(temp);
                logger.info("zhiliangxinwenwang save success");
                // out.add(temp);

            } catch (Exception e) {
                logger.error("zhiliangxinwenwang save fale");
                e.getStackTrace();
            }
        }
    }


    @Override
    public void start() {
        try {
            startCrawler();
        } catch (Exception e) {
            logger.error("zhiliangxinwenwang save fale");
        }
    }
}
