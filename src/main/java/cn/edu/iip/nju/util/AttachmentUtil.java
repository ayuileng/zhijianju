package cn.edu.iip.nju.util;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * Created by xu on 2017/5/1.
 */
public class AttachmentUtil {
    public static void downloadURLs(Document document, Map<String, Set<String>> downloadURLs) throws IOException, InterruptedException {
        //匹配后缀名(代码略丑)
//        Document document = Jsoup.connect(url)
//                    .userAgent("Mozilla")
//                    .timeout(0)
//                    .get();
        Elements xlsUrls = document.select("a[href$=.xls]");
        if (xlsUrls != null) {
            for (Element xlsUrl : xlsUrls) {
                downloadURLs.get("xls").add(xlsUrl.attr("abs:href"));
            }
        }
        Elements xlsxUrls = document.select("a[href$=.xlsx]");
        if (xlsxUrls != null) {
            for (Element xlsxUrl : xlsxUrls) {
                downloadURLs.get("xlsx").add(xlsxUrl.attr("abs:href"));
            }
        }

        Elements docUrls = document.select("a[href$=.doc]");
        if (docUrls != null) {
            for (Element docUrl : docUrls) {
                downloadURLs.get("doc").add(docUrl.attr("abs:href"));
            }
        }
        Elements docxUrls = document.select("a[href$=.docx]");
        if (docxUrls != null) {
            for (Element docxUrl : docxUrls) {
                downloadURLs.get("docx").add(docxUrl.attr("abs:href"));
            }
        }
        Elements rarUrls = document.select("a[href$=.rar]");
        if (rarUrls != null) {
            for (Element rarUrl : rarUrls) {
                downloadURLs.get("rar").add(rarUrl.attr("abs:href"));
            }
        }
        
        Elements zipUrls = document.select("a[href$=.zip]");
        if (zipUrls != null) {
            for (Element zipUrl : zipUrls) {
                downloadURLs.get("zip").add(zipUrl.attr("abs:href"));
            }
        }
        Elements pdfUrls = document.select("a[href$=.pdf]");
        if (pdfUrls != null) {
            for (Element pdfUrl : pdfUrls) {
                downloadURLs.get("pdf").add(pdfUrl.attr("abs:href"));
            }
        }
        Thread.sleep(500);
    }

    public static String getHtmlAfterJsExcuted(String url, Logger logger) throws IOException, InterruptedException {
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
        return rootPage.asXml();
    }
}
