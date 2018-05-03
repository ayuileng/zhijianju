package cn.edu.iip.nju.crawler;

import cn.edu.iip.nju.model.WebData;
import cn.edu.iip.nju.dao.WebDataDao;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * 消协、315
 * http://www.cca.org.cn/jmxf/list/17.html
 * http://www.cca.org.cn/jmxf/list/16.html(暂时失效)
 * http://www.cca.org.cn/jmxf/list/13.html
 * http://www.cca.org.cn/tsdh/list/19.html
 * http://www.315.gov.cn/spzljd/
 * http://www.315.gov.cn/wqsj/index.html(配置关键字搜索)
 * <p>
 * Created by xu on 2017/5/8.
 */
@Component
public class Xiaoxie {
    @Autowired
    private WebDataDao dao;

    private static final Logger logger = LoggerFactory.getLogger(Xiaoxie.class);
    private static String[] cca = {"http://www.cca.org.cn/jmxf/list/17.html",
            "http://www.cca.org.cn/jmxf/list/13.html",
            "http://www.cca.org.cn/tsdh/list/19.html"};
    private static String[] sanyaowu = {"http://www.315.gov.cn/spzljd/",
            "http://www.315.gov.cn/wqsj/index.html"};

    /**
     * @return
     * @throws IOException
     */
    public Set<String> getCCAurl() throws IOException {
        Set<String> pageUrls = new HashSet<>();
        Set<String> ccaUrls = new HashSet<>();
        for (String s : cca) {
            Document document = Jsoup.connect(s)
                    .userAgent("Mozilla")
                    .timeout(0)
                    .get();
            pageUrls.add(s);
            Element page = document.select("div.page").first();
            Elements as = page.select("a[href]");
            for (Element a : as) {
                pageUrls.add(a.attr("abs:href"));
            }
        }
        for (String pageUrl : pageUrls) {
            Document document = Jsoup.connect(pageUrl)
                    .userAgent("Mozilla")
                    .timeout(0)
                    .get();
            Elements uls = document.select("ul.jm_ul");
            for (Element ul : uls) {
                Elements as = ul.select("li > a[href]");
                for (Element a : as) {
                    ccaUrls.add(a.attr("abs:href"));
                }
            }

        }
        return ccaUrls;
    }

    /**
     * @throws IOException
     */
    public void process_cca_urls() throws Exception {
        Set<String> ccAurl = getCCAurl();
        for (String s : ccAurl) {
            WebData webData = new WebData();

            Document document = Jsoup.connect(s)
                    .userAgent("Mozilla")
                    .timeout(0)
                    .get();
            String title = document.select("div.title").first().text();
            String content = document.text();
            String postTime = document.select("div.text_author > span").first().text();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date d = sdf.parse(postTime);
            webData.setPostTime(d);
            webData.setTitle(title);
            webData.setContent(content);
            webData.setHtml(document.html());
            webData.setUrl(s);
            webData.setCrawlTime(new Date());
            webData.setSourceName("中国消费者协会");
//            System.out.println(webData.getPostTime());
            dao.save(webData);
            logger.info("save done");
        }
    }

    /**
     * @throws IOException
     * @throws InterruptedException
     */
    public void getSanyaowuUrls() throws Exception {
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        //设置webClient的相关参数
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setCssEnabled(false);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        webClient.getOptions().setTimeout(50000);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        //模拟浏览器打开一个目标网址
        HtmlPage rootPage = webClient.getPage(sanyaowu[0]);
        logger.info("为了获取js执行的数据 线程开始沉睡等待");
        Thread.sleep(1000);//主要是这个线程的等待 因为js加载也是需要时间的
        logger.info("线程结束沉睡");
        String xml = rootPage.asXml();
        Document document = Jsoup.parse(xml, "http://www.315.gov.cn/spzljd/");
        String text = document.select("div.more_more").first().text();
        String pageNum = text.substring(text.indexOf("共") + 1, text.indexOf("页"));//22
        Integer pagenum = Integer.parseInt(pageNum);
        Set<String> allPages = new HashSet<String>();
        allPages.add(sanyaowu[0]);
        for (Integer i = 1; i < pagenum; i++) {
            allPages.add("http://www.315.gov.cn/spzljd/index_" + i.toString() + ".html");//http://www.315.gov.cn/spzljd/index_1.html
        }

        for (String allPage : allPages) {
            Document doc = Jsoup.connect(allPage)
                    .userAgent("Mozilla")
                    .timeout(0)
                    .get();
            Elements lis = doc.select("li.dot1");
            for (Element li : lis) {
                String url = li.select("a[href]").first().attr("abs:href");
                Document document1 = Jsoup.connect(url)
                        .userAgent("Mozilla")
                        .timeout(0)
                        .get();
                WebData webData = new WebData();
                webData.setContent(document1.text());
                webData.setUrl(url);
                webData.setCrawlTime(new Date());
                webData.setSourceName("中国消费者权益保护网");
                webData.setHtml(document1.html());
                Elements titleDivs = document1.select("div.end_tab");
                if (titleDivs != null) {
                    String title = titleDivs.first().text();
                    webData.setTitle(title);
                }
                Elements postTimeDivs = document1.select("div.end_tab_02 > span");
                if (postTimeDivs != null) {
                    String postTime = postTimeDivs.first().text().trim();
                    String realPostTime = postTime.substring(postTime.indexOf("2"));
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
                    Date d = sdf.parse(realPostTime);
                    webData.setPostTime(d);
                }
                System.out.println(webData.getPostTime());
                dao.save(webData);
                logger.info("save done");
            }
        }
    }

    public void start() throws Exception {
        process_cca_urls();
        getSanyaowuUrls();
    }


}
