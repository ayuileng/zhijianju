package cn.edu.iip.nju.crawler;

import cn.edu.iip.nju.model.WebData;
import cn.edu.iip.nju.dao.WebDataDao;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.common.collect.Sets;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

/**
 * 苏网质检,爬取相关板块的正文部分
 * 1393条
 * Created bylibo 2016/11/9.
 * div class .
 * div id    #
 */
@Component
public class SuwangZhijian {
    private static Logger logger = LoggerFactory.getLogger(SuwangZhijian.class);
    @Autowired
    private WebDataDao dao;
    private static String baseURL = "http://jszj.jschina.com.cn";
    private static ArrayList<String> urlspre = new ArrayList<>();

    private Set<String> pageurls() throws Exception {
        Set<String> page = Sets.newHashSet();

        Document doc = Jsoup.connect(baseURL)
                .userAgent("Mozilla")
                .timeout(0)
                .get();
        Element listper = doc.select("div.other_page").first();
        Elements list = listper.select("div.box");
        for (Element lis : list) {
            if ("权威发布".equals(lis.text()) || "曝光台".equals(lis.text()) || "质监舆情".equals(lis.text())) {
                String te = lis.select("a[href]").attr("href");
                String tee = baseURL + te.substring(te.indexOf("/"), te.length());
                System.out.println(tee);
                urlspre.add(tee);
            }
        }
//            urlspre.add("http://jsgs.jschina.com.cn/xfjs");//添加红盾在线-消费警示

        for (String x : urlspre) {

            WebClient webClient = new WebClient(BrowserVersion.CHROME);
            //设置webClient的相关参数
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.getOptions().setCssEnabled(false);
            webClient.setAjaxController(new NicelyResynchronizingAjaxController());
            webClient.getOptions().setTimeout(50000);
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            //模拟浏览器打开一个目标网址
            HtmlPage rootPage = webClient.getPage(x);
            logger.info("为了获取js执行的数据 线程开始沉睡等待");
            Thread.sleep(100);//主要是这个线程的等待 因为js加载也是需要时间的
            logger.info("线程结束沉睡");
            String xml = rootPage.asXml();
            Document docc = Jsoup.parse(xml, "http://jsgs.jschina.com.cn/");

            page.add(x);
            Element div = docc.select("div.pageLink").first();
            Elements as = div.select("a[href]");
            for (Element a : as) {
                page.add(x + a.attr("href"));
            }
        }

        return page;
    }

    private Set<String> urls() throws Exception {
        Set<String> urls = Sets.newHashSet();
        for (String s : pageurls()) {
            Document docc = Jsoup.connect(s)
                    .userAgent("Mozilla")
                    .timeout(0)
                    .get();
            Elements o = docc.select("div.biaot");
            for (Element oi : o) {
                String te = oi.select("a[href]").attr("href");
                String tee = te.substring(te.indexOf("/"));
                urls.add(s.substring(0, s.lastIndexOf('/')) + tee);
            }

        }
        return urls;
    }

    public void start() {
        try {
            for (String x : urls()) {

                Document doc = Jsoup.connect(x)
                        .userAgent("Mozilla")
                        .timeout(0)
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
                    data.setCrawlerTime(new Date());
                    dao.save(data);
                    logger.info("save done!");
                }
            }
        } catch (Exception e) {
            logger.error("suwang fail");
        }

    }

}










