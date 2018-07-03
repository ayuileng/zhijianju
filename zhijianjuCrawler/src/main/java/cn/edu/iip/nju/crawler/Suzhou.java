package cn.edu.iip.nju.crawler;

/**
 * Created by xu on 2017/4/30.
 */

import cn.edu.iip.nju.util.AttachmentUtil;
import cn.edu.iip.nju.util.DownLoadUtil;
import com.google.common.collect.Sets;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 只要下载
 * 爬取苏州质检网http://www.szqts.gov.cn/zhiliangchoucha.html
 * 的附件（本页面只需要爬附件）
 */
@Component
public class Suzhou implements Crawler {
    private static final Logger logger = LoggerFactory.getLogger(Suzhou.class);
    private static String baseURL = "http://www.szqts.gov.cn/zhiliangchoucha.html";

    /**
     * 读取所有页面
     *
     * @return
     * @throws IOException
     */
    public Set<String> baseURLS() throws IOException {
        Document doc = Jsoup.connect(baseURL)
                .userAgent("Mozilla")
                .timeout(0)
                .get();
        Element page = doc.select("#page").first();
        Elements pages = page.select("a[href]");
        Set<String> baseURLS = new HashSet<>();
        baseURLS.add(baseURL);
        for (Element element : pages) {
            baseURLS.add(element.attr("href"));
        }
        return baseURLS;
    }

    /**
     * 获取所有子页面的url
     *
     * @return
     * @throws IOException
     */
    public Set<String> pageURLs() throws IOException {
        Set<String> baseURLS = baseURLS();
        Set<String> urls = new HashSet<>();
        for (String url : baseURLS) {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla")
                    .timeout(0)
                    .get();
            Elements as = doc.select("ul#list a[href]");
            for (Element a : as) {
                urls.add(a.attr("abs:href"));
            }
        }
        return urls;
    }

    /**
     * 从每个单独的页面中提取出要下载的附件的url
     */
    public Set<String> downloadURLs() throws IOException, InterruptedException {
        Set<String> downloadURLs = Sets.newHashSet();
        Set<String> pageURLs = pageURLs();
        for (String url : pageURLs) {
            Document document = Jsoup.connect(url)
                    .userAgent("Mozilla")
                    .timeout(0)
                    .get();
            Elements as = document.select("a[href]");
            for (Element a : as) {
                String aurl = a.attr("href");
                if(DownLoadUtil.isFile(aurl)){
                    downloadURLs.add(aurl);
                }
            }
        }
        return downloadURLs;
    }

    /**
     * 下载附件
     */
    private void downloadAttachment() throws Exception {
        Set<String> urls = downloadURLs();
        for (String url : urls) {
            DownLoadUtil.download(url, "C:\\Users\\yajima\\Desktop\\zhijianju\\zhijianjuCrawler\\src\\main\\resources\\files\\");
        }

    }

    @Override
    public void start() {
        try {
            downloadAttachment();
        } catch (Exception e) {
            logger.error("error", e);
        }
    }

}
