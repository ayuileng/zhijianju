package cn.edu.iip.nju.crawler;

import cn.edu.iip.nju.model.WebData;
import cn.edu.iip.nju.dao.WebDataDao;
import cn.edu.iip.nju.util.AttachmentUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by xu on 2017/5/2.
 * http://www.dpac.gov.cn/xfpzh/
 * http://www.dpac.gov.cn/qczh/
 */
@Component
public class ZhaoHui {
    private static final Logger logger = LoggerFactory.getLogger(ZhaoHui.class);
    private String[] baseUrls = {"http://www.dpac.gov.cn/xfpzh/", "http://www.dpac.gov.cn/qczh/"};
    @Autowired
    private WebDataDao dao;

    /**
     * 抓取完整的页面版块(“更多版块”)
     *
     * @return
     * @throws IOException
     */
    private Set<String> getAllPages() throws IOException {
        Set<String> allPages = new HashSet<String>();
        for (String baseUrl : baseUrls) {
            Document document = Jsoup.connect(baseUrl)
                    .userAgent("Mozilla")
                    .timeout(0)
                    .get();
            Elements divs = document.select("div.titb");
            for (Element div : divs) {
                allPages.add(div.select("a[href]").first().attr("abs:href"));
            }
        }
        return allPages;
    }

    /**
     * 抓取每一个页面的完整分页
     *
     * @return
     */
    private Set<String> getAllPagesUrls() throws IOException, InterruptedException {
        Set<String> allPages = getAllPages();
        Set<String> allPagesUrls = new HashSet<String>();
        for (String allPage : allPages) {

            if (allPage.contains("xfyj")) {
                continue;
            }
            allPagesUrls.add(allPage);
            String html = AttachmentUtil.getHtmlAfterJsExcuted(allPage, logger);
            Document document = Jsoup.parse(html, allPage);
            Element pageDiv = document.select("div.page").first();
            String text = pageDiv.text();
            String pageNum = text.substring(text.indexOf("共") + 1, text.indexOf("页"));
            int num = Integer.parseInt(pageNum);
            //从第二页开始
            for (int i = 1; i < num; i++) {
                allPagesUrls.add(allPage + "index_" + i + ".html");
            }
        }
        return allPagesUrls;
    }

    /**
     * 获得所有要抓取的页面
     *
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    private Set<String> getAllUrls() throws IOException, InterruptedException {
        Set<String> allPagesUrls = getAllPagesUrls();
        Set<String> allUrls = new HashSet<String>();
        for (String allPagesUrl : allPagesUrls) {
            Document document = Jsoup.connect(allPagesUrl)
                    .userAgent("Mozilla")
                    .timeout(0)
                    .get();
            Element div = document.select("div.boxl_ul").first();
            Elements as = div.select("li > a[href]");
            for (Element a : as) {
                allUrls.add(a.attr("abs:href"));
            }

        }
        return allUrls;
    }

    /**
     * 抓取每一个url
     */
    private void prcess_each_url() throws IOException, InterruptedException, ParseException {
        Set<String> allUrls = getAllUrls();
        for (String Url : allUrls) {
            WebData webdata = new WebData();
            webdata.setUrl(Url);
            Document document = Jsoup.connect(Url)
                    .userAgent("Mozilla")
                    .timeout(0)
                    .get();
            webdata.setHtml(document.html());
            webdata.setCrawlTime(new Date());
            webdata.setSourceName("国家质量总局缺陷产品管理中心");
            Element title = document.select("div.show_tit").first();
            String dateString = document.select("div.show_tit2").first().text();
            String date = dateString.substring(dateString.indexOf("2")).trim();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date d = sdf.parse(date);
            webdata.setPostTime(d);
            webdata.setTitle(title.text());
            Element text = document.select("div.TRS_Editor").first();
            if (text == null) {
                webdata.setContent(document.text());
            } else {
                webdata.setContent(text.text());
            }
            dao.save(webdata);
            logger.info("save done");
        }
    }


    public void start() throws Exception {
        prcess_each_url();
    }

}
