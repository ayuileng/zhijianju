package cn.edu.iip.nju.crawler;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import cn.edu.iip.nju.dao.WebDataDao;
import cn.edu.iip.nju.model.WebData;
import cn.edu.iip.nju.util.AttachmentUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 北京市消费者协会——消费维权——案例点评
 * 北京市消费者协会——消费调查
 * 北京市消费者协会——消费要闻
 * http://www.bj315.org/xfwq/aldp/
 * http://www.bj315.org/xfdc/
 * http://www.bj315.org/xxyw/xfxw/
 *
 * @author Wei Sun on 2017/9/24
 * 网站维护跑不通+posttime
 * by libo 2017.10.25
 * 2018/04/25
 * 测试通过 185条
 */
@Component
public class BJXFZXH implements Crawler{
    private final Logger logger = LoggerFactory.getLogger(BJXFZXH.class);
    private final String[] basicURLs = {"http://www.bj315.org/xfwq/aldp/"};

    @Autowired
    WebDataDao webDataDao;

    /**
     * 动态加载 js
     * 返回执行完js之后的html
     *
     * @param url
     * @return
     */
    public Document getExcutedJsHtml(String url) throws IOException, InterruptedException {
        String xml = AttachmentUtil.getHtmlAfterJsExcuted(url, logger);
        return Jsoup.parse(xml);
    }

    /**
     * 读取所有分页 URL
     *
     * @return
     * @throws IOException InterruptedException
     */
    public Set<String> getAllPageUrls() throws IOException, InterruptedException {
        Set<String> allPageUrls = new HashSet<String>();
        for (String basicURL : basicURLs) {
            allPageUrls.add(basicURL);

            Document excutedJsHtml = getExcutedJsHtml(basicURL);
            //System.out.println(excutedJsHtml);
            Elements fonts = excutedJsHtml.select("font");
            String temp = fonts.last().select("a").attr("href");
            int pageNum = Integer.parseInt(temp.substring(temp.indexOf('_') + 1, temp.indexOf('.'))) + 1;
            for (int j = 1; j < pageNum; j++) {
                String tempUrl = basicURL.replaceAll(".shtml", "") + "index_" + j + ".shtml";
                allPageUrls.add(tempUrl);
            }
        }
        return allPageUrls;
    }


    /**
     * 得到翻页的所有URLS
     *
     * @param
     * @return ArrayList<String>
     * @throws IOException
     */

    public ArrayList<String> getAllPagesUrls() throws IOException, InterruptedException {

        Set<String> pageUrls = getAllPageUrls();
        ArrayList<String> allPagesUrls = new ArrayList<>();

        for (String eachPageUrl : pageUrls) {
            try {
                Document doc = Jsoup.connect(eachPageUrl)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36")
                        .timeout(30000).get();
                Element ul = doc.select("ul").last();
                Elements as = ul.select("[href]");
                //System.out.println(as);
                for (Element a : as) {
                    allPagesUrls.add(a.absUrl("abs:href"));
                    //System.out.println(a.absUrl("abs:href"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return allPagesUrls;
    }

    public void getDetailPageInfo() throws Exception {
        ArrayList<String> urls = getAllPagesUrls();
        for (String url : urls) {

            WebData webData = new WebData();
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36")
                    .timeout(30000).get();
            String titleText = doc.select("div.h").first().text();
            webData.setHtml(doc.html());
            webData.setUrl(url);
            webData.setCrawlerTime(new Date());
            webData.setSourceName("北京消费者协会");
            Elements posttime = doc.select("div.zj_tit").select("div.info").select("span");
            String oriDate = posttime.text();
            //System.out.println(oriDate);
            String date = oriDate.substring(oriDate.indexOf("：") + 1, oriDate.lastIndexOf("-") + 2);
            //System.out.println(date);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date d = sdf.parse(date);
            webData.setPostTime(d);

            if (titleText != null) {
                webData.setTitle(titleText);
            }

            String contentText = doc.select("div#Zoom").first().text();
            if (null != contentText) {
                webData.setContent(contentText);
            }
            webDataDao.save(webData);
            logger.info("BJZFZXH save success");
            Thread.sleep(500);
        }
    }


    @Override
    public void start() {
        try {
            getDetailPageInfo();
        } catch (Exception e) {
            logger.error("BJZFZXH error!");
        }
    }
}
