package cn.edu.iip.nju.crawler;

import cn.edu.iip.nju.dao.JingDongDao;
import cn.edu.iip.nju.model.JingDong;
import cn.edu.iip.nju.service.RedisService;
import com.google.common.collect.Sets;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.assertj.core.util.Lists;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
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
    private static List<HttpHost> proxy = Lists.newArrayList(new HttpHost("60.0.214.203", 9999),
            new HttpHost("119.28.152.208", 80));

    @Autowired
    public JingDongCrawler(JingDongDao jingDongDao, RedisService redisService) {
        this.jingDongDao = jingDongDao;
        this.redisService = redisService;
    }

    private Set<String> getAllProducts() {
        return redisService.getAllProducts();
    }

    private Set<String> getProductIds(String product) throws Exception {
        HashSet<String> ids = Sets.newHashSet();
        String aimcode = URLEncoder.encode(product, "UTF-8");
        String[] split = baseUrl.split("&");
        String seachUrl = split[0] + aimcode + "&" + split[1];
        Document document;
        try {
            document= Jsoup.connect(seachUrl)
                    .userAgent("Mozilla")
                    .timeout(5000)
                    .get();
            Elements lis = document.select("li[class=gl-item]");
            if(lis==null||lis.isEmpty()){
                return ids;
            }
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
        }catch (Exception e){
            logger.info(e.getMessage());
        }

        return ids;
    }

    private Set<String> getComment(Long id) throws IOException {
        Set<String> all = new HashSet<>();
        String url;
        for (int i = 0; i < 10; i++) {
            url = "https://sclub.jd.com/comment/productPageComments.action" +
                    "?fetchJSON_comment98vv2941&productId=" + id +
                    "&score=1&sortType=6&page=" + i + "&pageSize=5&isShadowSku=0&fold=1";
            JSONObject jsonResponse = getJsonResponse(url);
            Set<String> comments = getComments(jsonResponse);
            if (comments == null || comments.size() == 0) {
                break;
            }
            all.addAll(comments);
        }
        return all;
    }

    private JSONObject getJsonResponse(String url) throws IOException {
        //需要把httpclient中的默认cookie设置取消掉， 不然会cookie rejected报错
        //.setProxy(proxy.get((int)(Math.random()*proxy.size())))
        RequestConfig globalConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.IGNORE_COOKIES).setConnectTimeout(5000).setSocketTimeout(5000).build();
        HttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(globalConfig).build();
        HttpGet get = new HttpGet(url);
        //get.setConfig(globalConfig);
        HttpResponse response = null;

        JSONObject jsonRes = JSONObject.fromObject(null);
        try {
            response = httpClient.execute(get);
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        if (response != null && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String result = EntityUtils.toString(response.getEntity());
            String substring = result.substring(result.indexOf("{"), result.lastIndexOf("}") + 1);
            jsonRes = JSONObject.fromObject(substring);
        }
        return jsonRes;
    }

    private Set<String> getComments(JSONObject jsonRes) {
        Set<String> strings = Sets.newHashSet();
        if (jsonRes.isNullObject()) {
            return strings;
        }
        JSONArray comments = jsonRes.getJSONArray("comments");
        if (comments != null && comments.size() > 0) {
            Iterator iterator = comments.iterator();
            while (iterator.hasNext()) {
                JSONObject comment = (JSONObject) iterator.next();
                strings.add(comment.getString("content"));
            }
        }
        return strings;
    }

    public void startJingdongCrawler() {
        Set<String> allProducts = getAllProducts();
        try {
            for (String product : allProducts) {
                logger.info("processing " + product);
                Set<String> productIds = getProductIds(product);
                for (String productId : productIds) {
                    //logger.info(productId);
                    Set<String> comment = getComment(Long.parseLong(productId));
                    for (String s : comment) {
                        JingDong jingDong = new JingDong();
                        jingDong.setProductName(product);
                        jingDong.setComment(s);
                        jingDongDao.save(jingDong);
                        logger.info("jingdong save success! productName" + product);
                    }
                    //时间太短会被京东阻塞，一直卡住
                    Thread.sleep(1000);
                }
            }
        } catch (Exception e) {
            logger.error("jingdong error!");
            e.printStackTrace();
        }
    }

}
