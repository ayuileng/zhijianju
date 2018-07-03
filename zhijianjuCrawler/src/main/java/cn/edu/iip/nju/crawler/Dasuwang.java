package cn.edu.iip.nju.crawler;

import java.io.File;
import java.net.Proxy;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

import cn.edu.iip.nju.dao.WebDataDao;
import cn.edu.iip.nju.model.WebData;
import cn.edu.iip.nju.util.ReadFileUtil;
import com.google.common.collect.Queues;
import com.google.common.io.Files;
import org.apache.commons.io.Charsets;
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

/**
 * 2018/04/24
 * 关键字 iproxy
 * 测试小数据通过
 */
@Service
public class Dasuwang implements Crawler {
    private static Logger logger = LoggerFactory.getLogger(Dasuwang.class);
    @Autowired
    private WebDataDao dao;
    private String baseURLfirst = "http://www.sogou.com/tx?site=js.qq.com&";
    private String baseURLthird = "&hdq=sogou-wsse-b58ac8403eb9cf17-0050&sourceid=&idx=f&idx=f";
    private ArrayList<String> urlspre = new ArrayList<>();
    private ArrayList<String> urls = new ArrayList<>();
    private ArrayList<String> urlsfinal = new ArrayList<>();
    private ArrayList<String> firstfaildurl = new ArrayList<>();
    private Random ran = new Random(1000);
    @Autowired
    private IPproxy iPproxy;


    private Queue<Proxy> ipnew = new LinkedList<>();


    public Queue<Proxy> getIp() {
        Queue<Proxy> ip = Queues.newArrayDeque();
        try {
            ip = iPproxy.getallIP();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ip;
    }

    private Set<String> getProductkword() {
        //检索产品关键字接口
        Set<String> pkword = new HashSet<>();
        pkword.add("电动车");
        pkword.add("儿童三轮车");
        pkword.add("电脑");
        return pkword;
    }

    private Set<String> getInjurekword() {
        //检索伤害关键字接口
        Set<String> ikword = new HashSet<>();
        ikword.add("绊倒");
        ikword.add("窒息");
        ikword.add("撞击");
        return ikword;
    }

    public ArrayList<String> getUrls()
            throws Exception {
//        int times = 0;
//        //关键字转码()做产品+伤害组合
//        //暴力相乘
//        List<Set<String>> temp = new ArrayList<>();
//        for (String s : pkword) {
//            String saim = URLEncoder.encode(s, "UTF-8");
//            for (String x : ikword) {
//                String xaim = URLEncoder.encode(x, "UTF-8");
//                //System.out.println(aimcode);
//                String sx = saim + "+" + xaim;
//                String second = "&query=" + sx;
//                urlspre.add(baseURLfirst + second + baseURLthird);
//            }
//        }
//        for (String x : urlspre) {
//            for (int i = 1; i <= 5; i++) {
//                String page = x + "&page=" + i;
//                urls.add(page);
//            }
//        }

        Set<String> keyWords = ReadFileUtil.readKeyWords();
        for (String keyWord : keyWords) {
            String[] words = keyWord.split(" ");
            String productEncoder = URLEncoder.encode(words[0], "UTF-8");
            String injureEncoder = URLEncoder.encode(words[1], "UTF-8");
            String second = "&query=" + productEncoder + "+" + injureEncoder;
            urlspre.add(baseURLfirst + second + baseURLthird);
        }
        for (String x : urlspre) {
            for (int i = 1; i <= 5; i++) {
                String page = x + "&page=" + i;
                urls.add(page);
            }
        }
        return urls;
    }

    public ArrayList<String> getallpaperurl(ArrayList<String> firsturls, int times) {
        Queue<Proxy> ip = getIp();
        if (times <= 0 || firsturls.size() <= 0) {
            return urlsfinal;
        } else {
            for (int i = 0; i < firsturls.size(); i++) {
                String x = firsturls.get(i);
                try {
                    if (ip.size() == 0) {
                        ipnew = getIp();
                        for (Proxy s : ipnew) {
                            ip.offer(s);
                        }
                        //System.out.println("加入新ip后长度:" + ip.size());
                    }
                    //随机休眠0.5s-1.5s
                    //Thread.sleep(ran.nextInt(1000) + 500);
                    Proxy proxy = ip.poll();
                    //System.out.println("此次用到的ip为:" + proxy.toString());
                    Document dd = Jsoup.connect(x)
                            //这里关键字编码问题，不能看浏览器地址栏中信息，要看network中的hearder信息
                            //	Document dd=Jsoup.connect("http://www.sogou.com/tx?site=js.qq.com&&query=%E5%84%BF%E7%AB%A5%E4%B8%89%E8%BD%AE%E8%BD%A6+%E6%92%9E%E5%87%BB&hdq=sogou-wsse-b58ac8403eb9cf17-0050&sourceid=&idx=f&idx=f&page=1")
                            .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36")
                            .timeout(5000)
                            .proxy(proxy)
                            .get();
                    ip.offer(proxy);
                    firsturls.remove(i);
                    //System.out.println("ip队列长度：" + ip.size());
                    //System.out.println("连接 "+x);
                    //System.out.println("成功");
                    //System.out.println(dd.toString());
                    Elements o = dd.select("div.vrwrap").select("h3.vrTitle").select("a[href]");
                    for (Element oi : o) {
                        urlsfinal.add(oi.attr("href"));
                    }
                } catch (Exception e) {
                    //e.getStackTrace();
                }

            }
            times--;
            getallpaperurl(firsturls, times);
            return urlsfinal;
        }
    }

    //times递归次数
    public void getdata(ArrayList<String> urls, int times) throws Exception {
        Queue<Proxy> ip = getIp();
        if (times <= 0 || urls.size() <= 0) {
            return;
        } else {
            for (int i = 0; i < urls.size(); i++) {
                String x = urls.get(i);
                try {
                    //随机1s到2s
                    //Thread.sleep(ran.nextInt(1000) + 1000);
                    if (ip.size() == 0) {
                        ipnew = getIp();
                        for (Proxy s : ipnew) {
                            ip.offer(s);
                        }
                    }
                    Proxy proxy = ip.poll();
                    Document dd = Jsoup.connect(x)
                            .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36")
                            .timeout(5000).proxy(proxy)
                            .get();
                    //如果这个ip能用，加入队列尾部
                    ip.offer(proxy);
                    urls.remove(i);
                    WebData temp = new WebData();
                    Element o = dd.select("div.hd").first();
                    temp.setTitle(o.select("h1").text());
                    Element p = dd.select("div.ll").first();
                    String date = p.select("span.article-time").text();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date d = sdf.parse(date.substring(0, date.indexOf(" ")));
                    temp.setPostTime(d);
                    Elements c = dd.select("div.bd").select("div#Cnt-Main-Article-QQ");
                    // System.out.println("c:"+c.toString());
                    String texttemp = "";
                    texttemp = dd.select("div.bd").select("div#Cnt-Main-Article-QQ").select("p").text();
                    //System.out.println("text：" + texttemp);
                    temp.setContent(texttemp);
                    temp.setUrl(x);
                    temp.setHtml(dd.toString());
                    temp.setCrawlerTime(new Date());
                    temp.setSourceName("大苏网");
                    dao.save(temp);
                    logger.info("dasuwang save success");
                } catch (Exception e) {
                   // e.getStackTrace();
                }
            }
            //次数减1
            times--;
            getdata(urls, times);
        }
    }


    public static void main(String[] args) throws Exception {
        Dasuwang dasuwang = new Dasuwang();
        Resource tests = new ClassPathResource("keywords/dasusearch.txt");
        ArrayList<String> test = new ArrayList<>();
        try {
            File file = tests.getFile();
            ArrayList<String> temp = (ArrayList<String>) Files.readLines(file, Charsets.UTF_8);

            for (int i = 0; i < 15; i++) {
                test.add(temp.get(i));
            }
        } catch (Exception e) {
            //e.getStackTrace();
        }
        ArrayList<String> finurls = dasuwang.getallpaperurl(test, 5);
        dasuwang.getdata(finurls, 3);
    }

    @Override
    public void start() {
        try {
            ArrayList<String> urls = getUrls();
            ArrayList<String> finalUrls = getallpaperurl(urls, 5);
            getdata(finalUrls,5);
        } catch (Exception e) {
            logger.error("dasuwang fail");
        }
    }
}