package cn.edu.iip.nju.crawler;

import cn.edu.iip.nju.dao.JingDongDao;
import cn.edu.iip.nju.model.JingDong;
import cn.edu.iip.nju.service.RedisService;
import com.google.common.collect.Sets;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.HttpStatus;
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
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


/**
 * Created by xu on 2017/11/18.
 */
@Component
public class JingDongCrawler {
    private static Logger logger = LoggerFactory.getLogger(JingDongCrawler.class);
    private final JingDongDao jingDongDao;
    private final RedisService redisService;
    private final String baseUrl = "https://search.jd.com/Search?keyword="
            + "&enc=utf-8";
    private final int count = 10;//默认爬取销量前十的商品，如果不足10，则全部爬取

    @Autowired
    public JingDongCrawler(JingDongDao jingDongDao, RedisService redisService) {
        this.jingDongDao = jingDongDao;
        this.redisService = redisService;
    }

    private Set<String> getAllProducts() {
        return redisService.getAllProducts();
    }

    private Set<String> getProductIds(String product) {
        HashSet<String> ids = Sets.newHashSet();
        product = "冰箱";
        try {
            String aimcode = URLEncoder.encode(product, "UTF-8");
            String[] split = baseUrl.split("&");
            String seachUrl = split[0] + aimcode + "&" + split[1];
            Document document = Jsoup.connect(seachUrl)
                    .userAgent("Mozilla")
                    .timeout(0)
                    .get();
            Elements lis = document.select("li[class=gl-item]");
            List<Element> liList;
            if (lis.size() > 10) {
                liList = lis.subList(0, 10);
            } else {
                liList = lis.subList(0, lis.size());
            }
            for (Element element : liList) {
                //https://item.jd.com/4629814.html
                String productId = element.attr("data-sku");//4629814
                ids.add(productId);
            }

        } catch (Exception e) {
            logger.error("jingdong error", e);
        }
        return ids;
    }

    private Set<String> getComment(int id) {
        String url;
        for (int i = 0; i < 10; i++) {
            url = "https://sclub.jd.com/comment/productPageComments.action" +
                    "?fetchJSON_comment98vv2941&productId=" + id +
                    "&score=1&sortType=6&page=" + i + "&pageSize=5&isShadowSku=0&fold=1";
            JSONObject jsonResponse = getJsonResponse(url);
            Set<String> comments = getComments(jsonResponse);
            if(comments==null || comments.size()==0){
                break;
            }
//            comments.forEach(System.out::println);
            return comments;
        }


        return null;
    }

    private JSONObject getJsonResponse(String url) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
//        HttpHost proxy=new HttpHost("177.154.50.77", 53281);
//        RequestConfig requestConfig=RequestConfig.custom().setProxy(proxy).build();
        HttpGet get = new HttpGet(url);
        get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:50.0) Gecko/20100101 Firefox/50.0");
//        get.setConfig(requestConfig);
        try {
            CloseableHttpResponse response = httpClient.execute(get);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String result = EntityUtils.toString(response.getEntity());
                String substring = result.substring(result.indexOf("{"), result.lastIndexOf("}") + 1);
                JSONObject jsonRes = JSONObject.fromObject(substring);
                return jsonRes;
            }
        } catch (Exception e) {
            logger.error("jingdong error", e);
        }
        return null;
    }

    private Set<String> getComments(JSONObject jsonRes) {

        JSONArray comments = jsonRes.getJSONArray("comments");
        if (comments != null && comments.size() > 0) {
            Set<String> strings = Sets.newHashSet();
            Iterator iterator = comments.iterator();
            while (iterator.hasNext()) {
                JSONObject comment = (JSONObject) iterator.next();
                strings.add(comment.getString("content"));

            }
            return strings;
        }
        return Sets.newHashSet();
    }

    @Transactional
    public void startJingdongCrawler() throws InterruptedException {
        for (String product : getAllProducts()) {
            Set<String> productIds = getProductIds(product);
            for (String productId : productIds) {
                Set<String> comment = getComment(Integer.parseInt(productId));
                for (String s : comment) {
                    JingDong jingDong = new JingDong();
                    jingDong.setProductName(product);
                    jingDong.setComment(s);
                    jingDongDao.save(jingDong);
                }
            }
            Thread.sleep(2000);
        }
    }

}
