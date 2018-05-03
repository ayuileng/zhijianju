package cn.edu.iip.nju.crawler;

import cn.edu.iip.nju.dao.WebDataDao;
import cn.edu.iip.nju.model.WebData;
import cn.edu.iip.nju.util.AttachmentUtil;
import cn.edu.iip.nju.util.DownLoadUtil;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.common.collect.Sets;
import jdk.management.resource.ResourceType;
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
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ClassPathResource;
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
 * {"http://www.aqsiq.gov.cn/xxgk_13386/jlgg_12538/ccgg/",
 *             "http://www.aqsiq.gov.cn/zjsj/zhxx/"};
 *             完成 共254条记录， 附件已下载新的
 */
@Component
@Scope("prototype")
public class GJZLJDJYJYZJ implements Crawler {
    @Autowired
    WebDataDao dao;
    private static final Logger logger = LoggerFactory.getLogger(GJZLJDJYJYZJ.class);
    private static String[] baseURLs = {"http://www.aqsiq.gov.cn/xxgk_13386/jlgg_12538/ccgg/",
                                        "http://www.aqsiq.gov.cn/zjsj/zhxx/"};

    /**
     * 获取所有的子版块的页面url
     */
    public Set<String> getAllPages() throws IOException{
        Set<String> allPages = new HashSet<>();
        for (String baseURL : baseURLs) {
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

        logger.info("phase_1 done!");
        return allPages;
    }

    public Set<String> getPageUrls() throws IOException {
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
        logger.info("phase_2 done!");
        return pageUrls;
    }
    public Set<String> process_each_url() throws IOException, InterruptedException, ParseException {
        Set<String> pageUrls = getPageUrls();
        Set<String> downLoadUrls = Sets.newHashSet();
        for (String pageUrl : pageUrls) {
//            Thread.sleep(200);
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
            dao.save(webdata);
            logger.info("save success");
            Elements as = document.select("a[href]");
            for (Element a : as) {
                String aurl = a.attr("abs:href");
                String fileName = aurl.substring(aurl.lastIndexOf('/') + 1);
                String base = pageUrl.substring(0, pageUrl.lastIndexOf('/'));

                if(DownLoadUtil.isFile(aurl)){
                    logger.info("downloading->"+base+"/"+fileName);
                    downLoadUrls.add(base+"/"+fileName);
                }
            }
        }
        return downLoadUrls;
    }
    private void download() throws Exception {
        Set<String> strings = process_each_url();
        Resource resource = new ClassPathResource("fileNew");
        for (String string : strings) {
            DownLoadUtil.download(string,resource.getFile().getAbsolutePath());
        }
        logger.info("download done!");
    }
    @Override
    public void start() {
        try {
            download();
        } catch (Exception e) {
            logger.error("GJZLJYJYZJ FAILE");
            e.printStackTrace();
        }
    }

}

