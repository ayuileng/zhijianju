package cn.edu.iip.nju.crawler;

import cn.edu.iip.nju.model.WebData;
import cn.edu.iip.nju.dao.WebDataDao;
import cn.edu.iip.nju.util.AttachmentUtil;
import cn.edu.iip.nju.util.DownLoadUtil;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 江苏质监局爬虫
 * http://www.jsqts.gov.cn/zjxx/GovInfoPub/Department/moreinfo.aspx?categoryNum=001010
 * Created by xu on 2017/4/30.
 */
@Component
public class JiangSu implements Crawler {
    private static final Logger logger = LoggerFactory.getLogger(JiangSu.class);
    @Autowired
    private WebDataDao dao;
    private static String baseURL = "http://www.jsqts.gov.cn/zjxx/GovInfoPub/Department/moreinfo.aspx?categoryNum=001010";

    @Override
    public void start() {
        Map<String, Set<String>> downUrls;
        try {
            downUrls = process_each_url();
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
            logger.error("jiangsu crawler failed", e);
        }

    }

    /**
     * 获取总的页数(需要等待js加载完毕，因为网页使用的是asp.net的pager插件)
     *
     * @return
     * @throws InterruptedException
     * @throws IOException
     */
    public String getPageNum() throws InterruptedException, IOException {
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        //设置webClient的相关参数
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setCssEnabled(false);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        webClient.getOptions().setTimeout(50000);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        //模拟浏览器打开一个目标网址
        HtmlPage rootPage = webClient.getPage(baseURL);
        logger.info("为了获取js执行的数据 线程开始沉睡等待");
        Thread.sleep(1000);//主要是这个线程的等待 因为js加载也是需要时间的
        logger.info("线程结束沉睡");
        String xml = rootPage.asXml();
        Document document = Jsoup.parse(xml);
        Elements as = document.select("a[title]");
        for (Element a : as) {
            if ("尾页".equals(a.text())) {
                String tmp = a.attr("title");
                return tmp.substring(tmp.indexOf("第") + 1, tmp.indexOf("页"));
            }
        }
        return null;
    }

    /**
     * 获取所有的页面
     *
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public ArrayList<Document> getAllPages() throws IOException, InterruptedException {
        ArrayList<Document> docs = new ArrayList<>();
        Integer i = Integer.parseInt(getPageNum());
        for (Integer i1 = 1; i1 <= i; i1++) {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpPost post = new HttpPost(baseURL);
            //提交post表单
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("__EVENTTARGET", "MoreinfoList1$Pager"));
            params.add(new BasicNameValuePair("__EVENTARGUMENT", i1.toString()));//页数 这一项一定要设置
            //设置hhtp请求头部信息
            post.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36");
            post.addHeader("Upgrade-Insecure-Requests", "1");
            post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            HttpResponse response = httpclient.execute(post);
            String result = EntityUtils.toString(response.getEntity(), "UTF-8");
            Document document = Jsoup.parse(result, "http://www.jsqts.gov.cn/zjxx/GovInfoPub/Department/");
            docs.add(document);
        }
        return docs;
    }

    public Set<String> getAllUrl() throws IOException, InterruptedException {
        Set<String> allUrl = new HashSet<>();
        ArrayList<Document> allPages = getAllPages();
        for (Document allPage : allPages) {
            Elements as = allPage.select("a[onmouseout]");
            for (Element a : as) {
                allUrl.add(a.attr("abs:href"));
            }
        }
        return allUrl;
    }

    public Map<String, Set<String>> process_each_url() throws IOException, InterruptedException, ParseException {
        Set<String> allUrl = getAllUrl();
        Map<String, Set<String>> downloadURLs = new HashMap<>();
        downloadURLs.put("xls", new HashSet<>());
        downloadURLs.put("xlsx", new HashSet<>());
        downloadURLs.put("doc", new HashSet<>());
        downloadURLs.put("docx", new HashSet<>());
        downloadURLs.put("rar", new HashSet<>());
        downloadURLs.put("zip", new HashSet<>());
        downloadURLs.put("pdf", new HashSet<>());
        for (String s : allUrl) {
            WebData webdata = new WebData();
            Document doc = Jsoup.connect(s)
                    .userAgent("Mozilla")
                    .timeout(0)
                    .get();
            if (doc.select("span#InfoDetail1_lblTitle").first() != null) {
                String title = doc.select("span#InfoDetail1_lblTitle").first().text();
                webdata.setTitle(title);
            }
            if (doc.select("span#InfoDetail1_lbl_Date").first() != null) {
                String date = doc.select("span#InfoDetail1_lbl_Date").first().text();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
                Date d = sdf.parse(date);
                logger.info(d.toString());
                webdata.setPostTime(d);
            }
            webdata.setCrawlTime(new Date());
            webdata.setSourceName("江苏质监局");
            webdata.setUrl(s);
            Elements as = doc.select("iframe#navFrameContent");
            for (Element a : as) {
                Document document = Jsoup.connect("http://www.jsqts.gov.cn" + a.attr("src"))
                        .userAgent("Mozilla")
                        .timeout(0)
                        .get();
                webdata.setHtml(document.html());
                webdata.setContent(doc.text());
                //dao.save(webdata);
                AttachmentUtil.downloadURLs(document, downloadURLs);
                Thread.sleep(1000);
            }
        }
        return downloadURLs;
    }

}
