package cn.edu.iip.nju.crawler;

import cn.edu.hfut.dmic.contentextractor.ContentExtractor;
import cn.edu.hfut.dmic.contentextractor.News;
import cn.edu.iip.nju.dao.NewsDataDao;
import cn.edu.iip.nju.model.NewsData;
import cn.edu.iip.nju.service.RedisService;
import cn.edu.iip.nju.util.DateUtil;
import cn.edu.iip.nju.util.ReadFileUtil;
import com.google.common.collect.Sets;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 改造为多线程
 * Created by xu on 2017/11/22.
 */
@Service
public class NewsCrawler {
    private static final Logger logger = LoggerFactory.getLogger(NewsCrawler.class);
    private static volatile BloomFilter<String> bf = BloomFilter.create(Funnels.stringFunnel(Charset.forName("utf-8")),
            100000, 0.00001);
    private static final String baiduUrl = "http://news.baidu.com/ns?sr=0&cl=2&rn=20&tn=news&ct=0&clk=sortbytime&word=";
    private static final String sougouUrl = "http://news.sogou.com/news?query=";
    //private static final String weixinUrl = "http://weixin.sogou.com/weixin?type=2&s_from=input&ie=utf8&_sug_=y&_sug_type_=&w=01019900&sut=1018&sst0=1511354003438&lkt=0%2C0%2C0&query=";
    private static final String _360Url = "https://news.so.com/ns?tn=news&rank=rank&j=0&nso=5&tp=19&nc=0&src=page&q=";
    private static final String _360PageNum = "&pn=";
    private final NewsDataDao newsDataDao;

    @Autowired
    public NewsCrawler(NewsDataDao newsDataDao) {
        this.newsDataDao = newsDataDao;
    }

    private Document getHtmlDoc(String url) throws IOException {
        return Jsoup.connect(url)
                .userAgent("Mozilla")
                .timeout(0)
                .get();
    }

    public Set<String> baiduCrawler(String keyWord) {
        Set<String> set = Sets.newHashSet();
        String realUrl = baiduUrl + keyWord;

        try {
            Document htmlDoc = getHtmlDoc(realUrl);
            //System.out.println(realUrl);
            //先确定页数 这个page的href里没有本页（第一页），所以后续要填上
            Element page = htmlDoc.select("p#page").first();
            if (page == null) {
                return set;
            }
            Elements pages = page.select("a[href]");
            //System.out.println(pages.toString());
            List<Element> as;
            //System.out.println(pages.size());
            if (pages.size() > 2) {
                as = pages.subList(0, 2);
            } else {
                as = pages.subList(0, pages.size());
            }
            //为了添上第一页，这里转成URL
            LinkedList<String> urls = new LinkedList<>();
            urls.add(realUrl);
            for (Element a : as) {
                urls.add(a.attr("abs:href"));
            }
            for (String pageurl : urls) {
                //取前三页，分别连接对应page
                Document eachPage = getHtmlDoc(pageurl);
                Elements results = eachPage.select("div.result").select("h3.c-title");
                //System.out.println(results.toString());
                for (Element result : results) {
                    Element url = result.select("a[href]").first();
                    if (url == null) break;
                    String u = url.attr("abs:href");
                    synchronized (NewsCrawler.class) {
                        if (!bf.mightContain(u)) {
                            //bf中不存在
                            set.add(u);
                            bf.put(u);
                        }
                    }
                }
            }
        } catch (IOException e) {
            logger.error("爬虫超时", e);

        }
        return set;
    }


    private Set<String> sanliuling(String keyWord) {
        Set<String> set = Sets.newHashSet();
        for (int i = 1; i <= 2; i++) {
            String realUrl = _360Url + keyWord + _360PageNum + i;
            //System.out.println(realUrl);
            try {
                Document htmlDoc = getHtmlDoc(realUrl);
                //System.out.println(htmlDoc.toString());
                Elements lis = htmlDoc.select("li.res-list");
                //System.out.println(lis.size());
                //System.out.println(lis.toString());
                for (Element li : lis) {
                    Element a = li.select("h3 > a[href]").first();
                    String aurl = a.attr("abs:href");
                    synchronized (NewsCrawler.class) {
                        if (!bf.mightContain(aurl)) {
                            //bf中不存在
                            set.add(aurl);
                            bf.put(aurl);
                        }
                    }

                }
            } catch (IOException e) {
                logger.error("爬虫超时?", e);
            }
        }
        return set;
    }

    public Set<String> sougouCrawler(String keyWord) {
        Set<String> set = Sets.newHashSet();
        try {
            String product = URLEncoder.encode(keyWord.split(" ")[0], "UTF-8");
            String injure = URLEncoder.encode(keyWord.split(" ")[1], "UTF-8");
            String realUrl = sougouUrl + product + "%20" + injure;
            //System.out.println(realUrl);
            Document htmlDoc = getHtmlDoc(realUrl);
            Set<String> urls = processEachPageOfSougou(htmlDoc);
            set.addAll(urls);
            Elements pages = htmlDoc.select("div#pagebar_container").select("a[href]");
            List<Element> as;
            if (pages.size() > 2) {
                as = pages.subList(0, 2);
            } else {
                as = pages.subList(0, pages.size());
            }
            for (Element a : as) {
                //System.out.println(a.attr("abs:href").toString());
                Document doc = getHtmlDoc(a.attr("abs:href"));
                Set<String> uls = processEachPageOfSougou(doc);
                set.addAll(uls);
            }
        } catch (Exception e) {
            logger.error("sougou error");
            e.printStackTrace();
        }
        return set;
    }

    private Set<String> processEachPageOfSougou(Document document) {
        Set<String> set = Sets.newHashSet();
        Elements as = document.select("div.results").select("div.vrwrap").select("a[href]");
        //System.out.println(as.size());
        for (Element a : as) {
            String uu = a.attr("abs:href");
            synchronized (NewsCrawler.class) {
                if (!bf.mightContain(uu)) {
                    //bf中不存在
                    bf.put(uu);
                    if (!uu.contains("news.sogou")) {
                        set.add(uu);
                    }
                }
            }
        }
        return set;
    }


    //百度新闻文件读取的关键字组合,参数为空
    public void startBaiduNewsCrawler() {
        Set<String> KeyWords = ReadFileUtil.readKeyWords();
        for (String keyWord : KeyWords) {
            Set<String> baiduCrawler = baiduCrawler(keyWord);
            saveNews(baiduCrawler);
        }
    }


    //搜狗新闻文件读取的关键字组合,参数为空
    public void startSougouNewsCrawler() {
        Set<String> KeyWords = ReadFileUtil.readKeyWords();
        for (String keyWord : KeyWords) {
            Set<String> sougouCrawler = sougouCrawler(keyWord);
            saveNews(sougouCrawler);
        }
    }



    //360新闻文件读取的关键字组合,参数为空
    public void start360NewsCrawler() {
        Set<String> KeyWords = ReadFileUtil.readKeyWords();
        for (String keyWord : KeyWords) {
            Set<String> sanliuling = sanliuling(keyWord);
            saveNews(sanliuling);
        }
    }


    private void saveNews(Set<String> crawler) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (String s : crawler) {
            News news = null;
            try {
                news = ContentExtractor.getNewsByUrl(s);
                NewsData newsData = new NewsData();
                String title = news.getTitle();
                if(title.contains("快视频")){
                    continue;
                }
                String url = news.getUrl();
                String content = news.getContent();
                newsData.setCrawlerTime(new Date());
                newsData.setTitle(title);
                newsData.setContent(content);
                newsData.setUrl(url);
                Document htmlDoc = getHtmlDoc(s);
                String htmlContent = htmlDoc.text();
                Date date = DateUtil.getDate(htmlContent);
//                if (date == null) {
//                    String time = news.getTime();
//                    Date dateFromParse = sdf.parse(time);
//                    Calendar current = Calendar.getInstance();
//                    Calendar calendar = Calendar.getInstance();
//                    calendar.setTime(dateFromParse);
//                    if (current.compareTo(calendar) >= 0) {
//                        date = dateFromParse;
//                    }
//                }
                //TODO date可能是NULL 需要人工修正
                newsData.setPostTime(date);
                //save
                newsDataDao.save(newsData);
                logger.info("saving news done");
                //System.out.println("title: " + title + "url: " + url + " time: " + time);
            } catch (Exception e) {
                logger.error("save news error", e);
            }

        }
    }


}
