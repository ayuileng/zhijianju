package cn.edu.iip.nju.crawler;

/**
 * Created by xu on 2017/4/30.
 */

import cn.edu.iip.nju.dao.WebDataDao;
import cn.edu.iip.nju.model.WebData;
import cn.edu.iip.nju.util.AttachmentUtil;
import cn.edu.iip.nju.util.DownLoadUtil;
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
import java.util.*;

/**
 * 国家质量检验检疫总局产品质量监督司爬虫
 * http://cpzljds.aqsiq.gov.cn/xfpzlaqjg/jggz/ 工作动态
 * http://cpzljds.aqsiq.gov.cn/xfpzlaqjg/jdcc/  监督抽查
 * http://cpzljds.aqsiq.gov.cn/xfpzlaqjg/fxjk/   风险监控
 * http://cpzljds.aqsiq.gov.cn/dfxx/  地方新闻
 * 同一个数据源的不同的版块，页面结构都一样
 * 包含附件
 */
@Component
public class CPZLJDS  {
    @Autowired
    private WebDataDao dao;
    private static final Logger logger = LoggerFactory.getLogger(CPZLJDS.class);
    private static final String[] basicURLs = {"http://cpzljds.aqsiq.gov.cn/xfpzlaqjg/jggz/",
            "http://cpzljds.aqsiq.gov.cn/xfpzlaqjg/jdcc/",
            "http://cpzljds.aqsiq.gov.cn/xfpzlaqjg/fxjk/",
            "http://cpzljds.aqsiq.gov.cn/dfxx/"};

    /**
     * 返回执行完js之后的html
     *
     * @param url
     * @return
     */
    private Document getExcutedJsHtml(String url) throws IOException, InterruptedException {
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        //设置webClient的相关参数
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setCssEnabled(false);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        webClient.getOptions().setTimeout(50000);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        //模拟浏览器打开一个目标网址
        HtmlPage rootPage = webClient.getPage(url);
        logger.info("为了获取js执行的数据 线程开始沉睡等待");
        Thread.sleep(1000);//主要是这个线程的等待 因为js加载也是需要时间的
        logger.info("线程结束沉睡");
        String xml = rootPage.asXml();
        return Jsoup.parse(xml);
    }

    /**
     * 读取所有分页的url
     *
     * @return
     * @throws IOException
     */
    private Set<String> allPageurls() throws IOException, InterruptedException {
        Set<String> allPageurls = new HashSet<>();
        for (String basicURL : basicURLs) {
            allPageurls.add(basicURL);
            Document excutedJsHtml = getExcutedJsHtml(basicURL);
            Element span = excutedJsHtml.select("span.disabled").first();
            String pageText = span.text();
            int pageNum = Integer.parseInt(pageText.substring(pageText.indexOf("共") + 1, pageText.indexOf("页")));
            //从第二页开始
            for (int i = 1; i < pageNum; i++) {
                String tmp = basicURL + "index_" + i + ".htm";
                allPageurls.add(tmp);
            }
        }
        return allPageurls;
    }

    private ArrayList<String> pageURLs() throws IOException, InterruptedException {
        Set<String> allPageurls = allPageurls();
        ArrayList<String> pageURLs = new ArrayList<>();
        for (String allPageurl : allPageurls) {
            Document doc = Jsoup.connect(allPageurl)
                    .userAgent("Mozilla")
                    .timeout(0)
                    .get();
            Elements as = doc.select("a[href$=htm]");
            for (Element a : as) {
                pageURLs.add(a.attr("abs:href"));
            }
        }
        return pageURLs;
    }

    private Map<String, Set<String>> process_each_url() throws IOException, InterruptedException {
        Map<String, Set<String>> downloadURLs = new HashMap<>();
        downloadURLs.put("xls", new HashSet<>());
        downloadURLs.put("xlsx", new HashSet<>());
        downloadURLs.put("doc", new HashSet<>());
        downloadURLs.put("docx", new HashSet<>());
        downloadURLs.put("rar", new HashSet<>());
        downloadURLs.put("zip", new HashSet<>());
        downloadURLs.put("pdf", new HashSet<>());
        ArrayList<String> pageURLs = pageURLs();

        for (String pageURL : pageURLs) {
            logger.info(pageURL);
            Document document = Jsoup.connect(pageURL)
                    .userAgent("Mozilla")
                    .timeout(0)
                    .get();
            WebData webData = new WebData();
            webData.setUrl(pageURL);
            webData.setHtml(document.html());
            webData.setSourceName("产品质量监督司");
            webData.setCrawlTime(new Date());
            Element h1 = document.select("h1").first();
            if (h1 != null) {
                webData.setTitle(h1.text());
            }
            Element content = document.select("div.TRS_Editor").first();
            if (content != null) {
                webData.setContent(content.text());
            }
            dao.save(webData);
            AttachmentUtil.downloadURLs(document, downloadURLs);
            Thread.sleep(500);
        }
        return downloadURLs;
    }
    /**
     * 下载附件
     * http://cpzljds.aqsiq.gov.cn/fwfh/zxgg/201704/P020170420590189495451.xls
     */
    private void downloadAttachment() throws Exception {
        Map<String, Set<String>> stringSetMap = process_each_url();
        String destinationDirectory = "files/CPZLJDS";
        //分类保存（便于之后读取，因为07前的版本和之后的不一样）（丑）
        Set<String> doc = stringSetMap.get("doc");
        for (String s : doc) {
            DownLoadUtil.download(s, destinationDirectory + "/doc/");
        }
        Set<String> docx = stringSetMap.get("docx");
        for (String s : docx) {
            DownLoadUtil.download(s, destinationDirectory + "/docx/");
        }
        Set<String> xls = stringSetMap.get("xls");
        for (String s : xls) {
            DownLoadUtil.download(s, destinationDirectory + "/xls/");
        }
        Set<String> xlsx = stringSetMap.get("xlsx");
        for (String s : xlsx) {
            DownLoadUtil.download(s, destinationDirectory + "/xlsx/");
        }
        Set<String> rar = stringSetMap.get("rar");
        for (String s : rar) {
            DownLoadUtil.download(s, destinationDirectory + "/rar/");
        }
        Set<String> zip = stringSetMap.get("zip");
        for (String s : zip) {
            DownLoadUtil.download(s, destinationDirectory + "/zip/");
        }

    }


    public void start() {
        try {
            downloadAttachment();
        } catch (Exception e) {
            logger.error("save failed!",e);

        }

    }

}
