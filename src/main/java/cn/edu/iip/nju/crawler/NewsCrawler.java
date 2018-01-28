package cn.edu.iip.nju.crawler;

import cn.edu.hfut.dmic.contentextractor.ContentExtractor;
import cn.edu.hfut.dmic.contentextractor.News;
import cn.edu.iip.nju.dao.NewsDataDao;
import cn.edu.iip.nju.model.NewsData;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.google.common.io.Files;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

/**
 * 改造为多线程
 * Created by xu on 2017/11/22.
 */
@Service
public class NewsCrawler {
    private static final Logger logger = LoggerFactory.getLogger(NewsCrawler.class);
    private static final BloomFilter<String> bf = BloomFilter.create(Funnels.stringFunnel(Charset.forName("utf-8")),
            100000, 0.00001);
    private static final String baiduUrl = "http://news.baidu.com/ns?tn=news&from=news&cl=2&rn=20&ct=1&word=";
    private static final String sougouUrl = "http://news.sogou.com/news?query=";
    private static final String weixinUrl = "http://weixin.sogou.com/weixin?type=2&s_from=input&ie=utf8&_sug_=y&_sug_type_=&w=01019900&sut=1018&sst0=1511354003438&lkt=0%2C0%2C0&query=";
    private static final String _360Url = "http://news.so.com/ns?src=360portal&_re=0&q=";
    private BlockingQueue<String> news = Queues.newLinkedBlockingDeque();
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
            System.out.println(realUrl);
            Element page = htmlDoc.select("p#page").first();
            if (page == null) {
                return set;
            }
            Elements pages = page.select("a[href]");
            List<Element> as;
            System.out.println(pages.size());
            if (pages.size() > 2) {
                as = pages.subList(0, 2);
            } else {
                as = pages.subList(0, pages.size());
            }
            for (Element a : as) {
                Document eachPage = getHtmlDoc(a.attr("abs:href"));
                Elements results = eachPage.select("h3.c-title");
                Element url = results.select("a[href]").first();
                String u = url.attr("abs:href");
                if (!bf.mightContain(u)) {
                    //bf中不存在
                    set.add(u);
                    bf.put(u);
                }
            }
        } catch (IOException e) {
            logger.error("爬虫超时?", e);

        }
        return set;
    }


    private Set<String> sanliuling(String keyWord) {
        Set<String> set = Sets.newHashSet();
        String realUrl = _360Url + keyWord;
        try {
            Document htmlDoc = getHtmlDoc(realUrl);
            Elements pages = htmlDoc.select("div#page").select("a[href]");
            List<Element> as;
            if (pages.size() > 2) {
                as = pages.subList(0, 2);
            } else {
                as = pages.subList(0, pages.size());
            }
            for (Element a : as) {
                Document eachPage = getHtmlDoc(a.attr("abs:href"));
                Elements lis = eachPage.select("li[data-from=news]");
                for (Element li : lis) {
                    String u = li.select("a[href]").attr("abs:href");
                    if (!bf.mightContain(u)) {
                        //bf中不存在
                        set.add(u);
                        bf.put(u);
                    }
                }
            }
        } catch (IOException e) {
            logger.error("爬虫超时?", e);
        }
        return set;
    }

    private Set<String> sougouCrawler(String keyWord) {
        Set<String> set = Sets.newHashSet();
        String encode = null;
        try {
            encode = URLEncoder.encode(keyWord, "utf-8");
        } catch (UnsupportedEncodingException e) {
            encode = keyWord;
        }
        String realUrl = sougouUrl + encode;
        try {
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
                Document doc = getHtmlDoc(a.attr("abs:href"));
                Set<String> uls = processEachPageOfSougou(doc);
                set.addAll(uls);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return set;
    }

    private Set<String> processEachPageOfSougou(Document document) {
        Set<String> set = Sets.newHashSet();
        List<Element> as = document.select("div.vrwrap").select("a[href]");
        for (Element a : as) {
            String uu = a.attr("abs:href");
            if (!bf.mightContain(uu)) {
                //bf中不存在
                bf.put(uu);
                if (!uu.contains("news.sogou")) {
                    set.add(uu);
                }

            }
        }
        return set;
    }

    private Set<String> weixinCrawler(String keyWord) {
        Set<String> set = Sets.newHashSet();
        String realUrl = weixinUrl + keyWord;
        Document htmlDoc = null;
        try {
            htmlDoc = getHtmlDoc(realUrl);
            Element first = htmlDoc.select("ul.news-list").first();
            if (first == null) {
                return set;
            }
            Elements lis = first.select("li");
            for (Element li : lis) {
                Element a = li.select("a[href]").first();
                String result = a.attr("abs:href");
                if (!bf.mightContain(result)) {
                    //bf中不存在
                    bf.put(result);
                    set.add(result);
                }
            }
            Element page = htmlDoc.select("div#pagebar_container").first();
            Elements as = page.select("a[href]");
            List<Element> elements;
            if (as.size() > 2) {
                elements = as.subList(0, 2);
            } else {
                elements = as.subList(0, as.size());
            }
            for (Element element : elements) {
                String url = element.attr("abs:href");
                Document htmlDoc1 = getHtmlDoc(url);
                Element ul = htmlDoc1.select("ul.news-list").first();
                if (ul == null) {
                    continue;
                }
                lis = ul.select("li");
                for (Element li : lis) {
                    Element a = li.select("a[href]").first();
                    String result = a.attr("abs:href");
                    if (!bf.mightContain(result)) {
                        //bf中不存在
                        bf.put(result);
                        set.add(result);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return set;
    }

    public void startNewsCrawler(String keyWord) {
        Set<String> baiduCrawler = baiduCrawler(keyWord);
        saveNews(baiduCrawler);
        Set<String> sanliuling = sanliuling(keyWord);
        saveNews(sanliuling);
        Set<String> sougouCrawler = sougouCrawler(keyWord);
        saveNews(sougouCrawler);
        Set<String> weixinCrawler = weixinCrawler(keyWord);
        saveNews(weixinCrawler);


    }

    public Set<String> readKeyWords() {
        Resource resourceurl = new ClassPathResource("keywords/产品与伤害.txt");
        Set<String> keyWords = Sets.newHashSet();
        try {
            File file = resourceurl.getFile();
            List<String> lines = Files.readLines(file, Charsets.UTF_8);
            for (String line : lines) {
                String[] split = line.split("：");
                String[] words = split[1].split("，");
                for (String word : words) {
                    keyWords.add(split[0].trim() + " " + word.trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return keyWords;
    }

    private void saveNews(Set<String> crawler) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (String s : crawler) {
            News news = null;
            try {
                news = ContentExtractor.getNewsByUrl(s);
                NewsData newsData = new NewsData();
                String title = news.getTitle();
                String url = news.getUrl();
//                if(url.length()>255){
//                    url = "http://www.baidu.com";
//                }
                String time = news.getTime();
                String content = news.getContent();
                newsData.setCrawlerTime(new Date());
                newsData.setTitle(title);
                newsData.setContent(content);
                newsData.setUrl(url);
                Date parse;
                if (Strings.isNullOrEmpty(time)) {
                    parse = new Date();
                } else if (!time.startsWith("20")) {
                    parse = new Date();
                } else {
                    try {
                        parse = sdf.parse(time);
                    } catch (ParseException e) {
                        parse = new Date();
                    }
                }
                newsData.setPostTime(parse);
                //save
                newsDataDao.save(newsData);
                System.out.println("title: " + title + "url: " + url + " time: " + time);
            } catch (Exception e) {
                logger.error("save news error", e);
            }

        }
    }


}
