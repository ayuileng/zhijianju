package cn.edu.iip.nju.crawler;

import cn.edu.iip.nju.dao.WebDataDao;
import cn.edu.iip.nju.model.WebData;
import cn.edu.iip.nju.util.AttachmentUtil;
import cn.edu.iip.nju.util.DownLoadUtil;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 国家质量监督检验检疫总局
 * http://www.aqsiq.gov.cn/xxgk_13386/jlgg_12538/ccgg/
 * http://www.aqsiq.gov.cn/zjsj/zhxx/
 * Created by xu on 2017/5/3.
 */
@Component
public class GJZLJDJYJYZJ implements Crawler {
    @Autowired
    private WebDataDao dao;
    private static final Logger logger = LoggerFactory.getLogger(GJZLJDJYJYZJ.class);
    private static String[] baseURLS = {"http://www.aqsiq.gov.cn/xxgk_13386/jlgg_12538/ccgg/",
            "http://www.aqsiq.gov.cn/zjsj/zhxx/"};

    /**
     * 获取所有的子版块的页面url
     *
     * @return
     */
    public Set<String> getAllPages() throws IOException, InterruptedException {
        Set<String> allPages = new HashSet<>();
        for (String baseURL : baseURLS) {
            Document doc = Jsoup.connect(baseURL)
                    .userAgent("Mozilla")
                    .timeout(0)
                    .get();
            Elements tds = doc.select("td.more");
            for (Element td : tds) {
                String url = td.select("a[href]").first().attr("abs:href");
                allPages.add(url);
            }
        }
        return allPages;
    }

    public Set<String> getUrl() throws IOException, InterruptedException {
        Set<String> allPages = getAllPages();
        Set<String> urls = new HashSet<>();
        for (String allPage : allPages) {
            urls.add(allPage);
            WebClient webClient = new WebClient(BrowserVersion.CHROME);
            //设置webClient的相关参数
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.getOptions().setCssEnabled(false);
            webClient.setAjaxController(new NicelyResynchronizingAjaxController());
            webClient.getOptions().setTimeout(50000);
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            //模拟浏览器打开一个目标网址
            HtmlPage rootPage = webClient.getPage(allPage);
            logger.info("为了获取js执行的数据 线程开始沉睡等待");
            Thread.sleep(1000);//主要是这个线程的等待 因为js加载也是需要时间的
            logger.info("线程结束沉睡");
            String xml = rootPage.asXml();
            Document document = Jsoup.parse(xml, "http://www.aqsiq.gov.cn");

            Elements as = document.select("div.quotes > a[href]");
            for (Element a : as) {
                System.out.println(a.attr("abs:href"));
            }
        }
        return urls;
    }

    public Set<String> getPageUrls() throws IOException, InterruptedException {
        Set<String> pageUrls = new HashSet<>();
        Set<String> allPages = getAllPages();
        for (String allPage : allPages) {
            Document document = Jsoup.connect(allPage)
                    .userAgent("Mozilla")
                    .timeout(0)
                    .get();
            //选择td标签下的带href属性的a标签
            Elements as = document.select("td > a[href]");
            for (Element a : as) {
                pageUrls.add(a.attr("abs:href"));
            }
        }
        return pageUrls;
    }

    public Map<String, Set<String>> process_each_url() throws IOException, InterruptedException, ParseException {
        Set<String> pageUrls = getPageUrls();
        Map<String, Set<String>> downloadURLs = new HashMap<>();
        downloadURLs.put("xls", new HashSet<>());
        downloadURLs.put("xlsx", new HashSet<>());
        downloadURLs.put("doc", new HashSet<>());
        downloadURLs.put("docx", new HashSet<>());
        downloadURLs.put("rar", new HashSet<>());
        downloadURLs.put("zip", new HashSet<>());
        downloadURLs.put("pdf", new HashSet<>());
        for (String pageUrl : pageUrls) {
            Thread.sleep(200);
            WebData webdata = new WebData();
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet get = new HttpGet(pageUrl);
            CloseableHttpResponse response = httpClient.execute(get);
            String result = EntityUtils.toString(response.getEntity(), "gb2312");
            Document document = Jsoup.parse(result, "http://www.aqsiq.gov.cn");
            Element content = document.select("div.TRS_Editor").first();
            if (content != null) {
                webdata.setContent(content.text());
            } else {
                continue;
            }
            webdata.setUrl(pageUrl);
            webdata.setHtml(document.html());
            webdata.setCrawlTime(new Date());
            webdata.setSourceName("国家质量监督检验检疫总局");
            Element title = document.select("h1").first();
            if (title != null) {
                webdata.setTitle(title.text());
            }

            if (document.select("div.xj2").first() != null) {
                String date = document.select("div.xj2").first().text();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date d = sdf.parse(date);
                webdata.setPostTime(d);
            }
            //dao.save(webdata);
            AttachmentUtil.downloadURLs(document, downloadURLs);
        }
        return downloadURLs;
    }

    @Override
    public void start() {
        try {
            Map<String, Set<String>> downUrls = process_each_url();
            Resource resource = new FileSystemResource("C:\\Users\\63117\\IdeaProjects\\zhijianju\\src\\main\\resources\\files\\attachment");
            String destinationDirectory = resource.getFile().getAbsolutePath();
            //分类保存（便于之后读取，因为07前的版本和之后的不一样）（丑）
            Set<String> doc = downUrls.get("doc");
            for (String s : doc) {
                DownLoadUtil.download(s, destinationDirectory + "/doc/");
            }
            Set<String> docx = downUrls.get("docx");
            for (String s : docx) {
                DownLoadUtil.download(s, destinationDirectory + "/docx/");
            }
            Set<String> xls = downUrls.get("xls");
            for (String s : xls) {
                DownLoadUtil.download(s, destinationDirectory + "/xls/");
            }
            Set<String> xlsx = downUrls.get("xlsx");
            for (String s : xlsx) {
                DownLoadUtil.download(s, destinationDirectory + "/xlsx/");
            }
            Set<String> rar = downUrls.get("rar");
            for (String s : rar) {
                DownLoadUtil.download(s, destinationDirectory + "/rar/");
            }
            Set<String> zip = downUrls.get("zip");
            for (String s : zip) {
                DownLoadUtil.download(s, destinationDirectory + "/zip/");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

