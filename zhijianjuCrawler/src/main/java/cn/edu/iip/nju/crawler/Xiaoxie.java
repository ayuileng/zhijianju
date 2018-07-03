package cn.edu.iip.nju.crawler;

import cn.edu.iip.nju.model.WebData;
import cn.edu.iip.nju.dao.WebDataDao;
import cn.edu.iip.nju.util.AttachmentUtil;
import com.google.common.base.Strings;
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
 * 2743条
 * Created by xu on 2017/5/8.
 */
@Component
public class Xiaoxie implements Crawler {
    @Autowired
    private WebDataDao dao;

    private static final Logger logger = LoggerFactory.getLogger(Xiaoxie.class);
    private static String[] cca = {"http://www.cca.org.cn/jmxf/list/17.html"
//            "http://www.cca.org.cn/jmxf/list/13.html",
//            "http://www.cca.org.cn/tsdh/list/19.html"};
    };
    private static String[] sanyaowu = {"http://www.315.gov.cn/spzljd/",
            "http://www.315.gov.cn/wqsj/index.html"};

    /**
     * 页码数是不确定，要不断遍历下一页知道下一页是disable的时候才可以，不然只从首页取就永远是6页
     */
    public Set<String> getCCAurl() throws Exception {
        Set<String> pageUrls = new HashSet<>();
        Set<String> ccaUrls = new HashSet<>();
        for (String s : cca) {
            Document document = Jsoup.connect(s)
                    .userAgent("Mozilla")
                    .timeout(0)
                    .get();
            Thread.sleep(2000);
            pageUrls.add(s);
            Element page = document.select("div.page").first();
            Elements as = page.select("a[href]");
            for (Element a : as) {
                pageUrls.add(a.attr("abs:href"));
            }
            Element down = page.select("a[title]").last();
            while (down != null && !Strings.isNullOrEmpty(down.attr("abs:href"))) {
                logger.info("in loop");
                //必须
                Thread.sleep(2000);
                document = Jsoup.connect(down.attr("abs:href"))
                        .userAgent("Mozilla")
                        .timeout(0)
                        .get();
                page = document.select("div.page").first();
                as = page.select("a[href]");
                for (Element a : as) {
                    pageUrls.add(a.attr("abs:href"));
                }

                down = page.select("a[title]").last();
            }
        }
        logger.info(pageUrls.size() + "");
        for (String pageUrl : pageUrls) {
            logger.info(pageUrl);
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
            webData.setCrawlerTime(new Date());
            webData.setSourceName("中国消费者协会");
            dao.save(webData);
            logger.info("save done");
        }
    }
    /**
     * @throws IOException
     * @throws InterruptedException
     */
    public void getSanyaowuUrls() throws Exception {
        String xml = AttachmentUtil.getHtmlAfterJsExcuted(sanyaowu[0], logger);
        Document document = Jsoup.parse(xml, "http://www.315.gov.cn/spzljd/");
        String text = document.select("div.more_more").first().text();
        String pageNum = text.substring(text.indexOf("共") + 1, text.indexOf("页"));//22
        Integer pagenum = Integer.parseInt(pageNum);
        logger.info(pageNum + "");
        Set<String> allPages = new HashSet<>();
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
                Document document1;
                try {
                    document1 = Jsoup.connect(url)
                            .userAgent("Mozilla")
                            .timeout(0)
                            .get();
                    WebData webData = new WebData();
                    webData.setContent(document1.text());
                    webData.setUrl(url);
                    webData.setCrawlerTime(new Date());
                    webData.setSourceName("中国消费者权益保护网");
                    webData.setHtml(document1.html());
                    Element titleDivs = document1.select("div.end_tab").first();
                    if (titleDivs != null) {
                        String title = titleDivs.text();
                        webData.setTitle(title);
                    }
                    Element postTimeDivs = document1.select("div.end_tab_02 > span").first();
                    if (postTimeDivs != null) {
                        String postTime = postTimeDivs.text().trim();
                        String realPostTime = postTime.substring(postTime.indexOf("2"));
                        SimpleDateFormat sdf;
                        Date d;
                        try {
                            sdf = new SimpleDateFormat("yyyy年MM月dd日");
                            d = sdf.parse(realPostTime);
                        } catch (java.text.ParseException e) {
                            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            try {
                                d = sdf.parse(realPostTime);
                            }catch (java.text.ParseException e1){
                                d = new Date();
                            }
                        }
                        webData.setPostTime(d);
                    }
                    dao.save(webData);
                    logger.info("save done");
                }catch (Exception e){
                    logger.error("skip this url");
                }
            }
        }
    }

    @Override
    public void start() {
        try {
            process_cca_urls();
            getSanyaowuUrls();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
