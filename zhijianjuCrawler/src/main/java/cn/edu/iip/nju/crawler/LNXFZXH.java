package cn.edu.iip.nju.crawler;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import cn.edu.iip.nju.dao.WebDataDao;
import cn.edu.iip.nju.model.WebData;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 辽宁省消费者协会信息网——消费资讯——媒体追踪			http://www.315ln.cn/a/xfzx/mtzz/
 * 辽宁省消费者协会信息网——消费引导——比较试验			http://www.315ln.cn/a/xfyd/bjsy/
 * 辽宁省消费者协会信息网——投诉导航——维权案例			http://www.315ln.cn/a/tsdh/wqal/
 * 2017.10.25
 * by libo
 * 2018.4.24
 * 三个页面 47 条
 */

@Component
public class LNXFZXH implements Crawler{
    @Autowired
    WebDataDao webDataDao;

    private static final Logger logger = LoggerFactory.getLogger(LNXFZXH.class);
    private static String[] baseURLS = {"http://www.315ln.cn/a/xfzx/mtzz/",
            "http://www.315ln.cn/a/xfyd/bjsy/",
            "http://www.315ln.cn/a/tsdh/wqal/"};
    private static String[] urlParam = {"11", "14", "21"};

    private static Map<String, String> map = new HashMap<String, String>() {
        {
            put(baseURLS[0], urlParam[0]);
            put(baseURLS[1], urlParam[1]);
            put(baseURLS[2], urlParam[2]);
        }
    };

    /**
     * 获取所有 分页的URL
     *
     * @return
     * @throws IOException
     **/
    private Set<String> getPageTopUrls() {
        Set<String> pageTopUrls = new HashSet<String>();
        for (String url : baseURLS) {
            try {
                pageTopUrls.add(url);
                Document doc = Jsoup.connect(url)
                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                        .header("Accept-Encoding", "gzip, deflate, sdch")
                        .header("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6")
                        .header("Cache-Control", "max-age=0")
                        .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36")
                        .header("Cookie", "Hm_lvt_7ed65b1cc4b810e9fd37959c9bb51b31=1462812244; _gat=1; _ga=GA1.2.1061361785.1462812244")
                        .header("Host", "www.kuaidaili.com")
                        .header("Referer", "http://www.kuaidaili.com/free/outha/")
                        .timeout(30 * 1000)
                        .get();
                String pageText = doc.select("span.pageinfo").first().text();
                // System.out.println(pageText);
                int pageNum = Integer.valueOf(pageText.substring(pageText.indexOf("共") + 1, pageText.indexOf("页")).trim());
                //System.out.println(pageNum);
                for (int i = 2; i <= pageNum; i++) {
                    String tempUrl = url + "list_" + map.get(url) + "_" + i + ".html";
                    //System.out.println(tempUrl);
                    pageTopUrls.add(tempUrl);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return pageTopUrls;
    }


    /**
     * 获取所有子页包含的URL
     *
     * @return ArrayList<String>
     */
    private ArrayList<String> getAllPagesUrls() {
        ArrayList<String> allPageUrls = new ArrayList<String>();
        Set<String> baseUrls = getPageTopUrls();
        for (String url : baseUrls) {
            try {
                Document doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36")
                        .timeout(5000).get();
                Elements as = doc.select("div.kmBox").select("a");

                for (Element a : as) {
                    // System.out.println(a.absUrl("abs:href"));
                    allPageUrls.add(a.absUrl("abs:href"));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return allPageUrls;
    }


    public void getDetailPageInfo() {

        ArrayList<String> urls = getAllPagesUrls();
        //WebData webdata = new WebData();
        for (String url : urls) {
            try {
                WebData webData = new WebData();
                Document doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36")
                        .timeout(30000).get();
                Element posttime = doc.select("div.content").select("div.infoLine").first();
                String oriDate = posttime.text();
                //System.out.println(oriDate);
                String date = oriDate.substring(oriDate.indexOf("：") + 1, oriDate.indexOf(" "));
                //System.out.println(date);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date d = sdf.parse(date);
                webData.setPostTime(d);
                webData.setCrawlerTime(new Date());
                webData.setSourceName("辽宁省消费者协会");
                webData.setHtml(doc.html());
                webData.setUrl(url);
                String titleText = doc.select("h1").first().text();
                if (null != titleText) {
                    webData.setTitle(titleText);
                }
                String conText = doc.select("div.zhengwen").first().text();
                if (null != conText) {
                    webData.setContent(conText);
                }
                webDataDao.save(webData);
                logger.info("LNXFZXH save success");
                Thread.sleep(500);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
    @Override
    public void start() {
        getDetailPageInfo();
    }
}
