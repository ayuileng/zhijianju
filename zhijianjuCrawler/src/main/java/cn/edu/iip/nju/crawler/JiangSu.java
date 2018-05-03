package cn.edu.iip.nju.crawler;

import cn.edu.iip.nju.model.WebData;
import cn.edu.iip.nju.dao.WebDataDao;
import cn.edu.iip.nju.util.AttachmentUtil;
import cn.edu.iip.nju.util.DownLoadUtil;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.common.collect.Sets;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.tools.ant.taskdefs.condition.Http;
import org.assertj.core.util.Lists;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 只需要下载
 * 江苏质监局爬虫
 * http://www.jsqts.gov.cn/zjxx/GovInfoPub/Department/moreinfo.aspx?categoryNum=001010
 * Created by xu on 2017/4/30.
 */
@Component
public class JiangSu implements Crawler {
    @Autowired
    private WebDataDao dao;
    private static final Logger logger = LoggerFactory.getLogger(JiangSu.class);

    private static String baseURL = "http://www.jsqts.gov.cn/zjxx/GovInfoPub/Department/moreinfo.aspx?categoryNum=001010";

    @Override
    public void start() {
        try {
            process_each_url();
        } catch (Exception e) {
            logger.error("jiangsu error");
        }

    }

    /**
     * 获取总的页数(需要等待js加载完毕，因为网页使用的是asp.net的pager插件)
     *
     * @return
     * @throws InterruptedException
     * @throws IOException
     */
    public int getPageNum() throws InterruptedException, IOException {
        int num=0;
        String xml = AttachmentUtil.getHtmlAfterJsExcuted(baseURL, logger);
        Document document = Jsoup.parse(xml);
        Elements as = document.select("a[title]");
        for (Element a : as) {
            if ("尾页".equals(a.text())) {
                String tmp = a.attr("title");
                String numString = tmp.substring(tmp.indexOf("第") + 1, tmp.indexOf("页"));
                num = Integer.parseInt(numString);
            }
        }
//        System.out.println(num);    15页
        logger.info("get jiangsu pageNum done！");
        return num;
    }

    /**
     * 获取所有的页面
     *
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public List<Document> getAllPages() throws IOException, InterruptedException {
        ArrayList<Document> docs = new ArrayList<>();
        int num = getPageNum();
        for (int i = 1; i <= num; i++) {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost post = new HttpPost("http://www.jsqts.gov.cn/zjxx/GovInfoPub/Department/moreinfo.aspx?categoryNum=001010");
            post.addHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
            post.addHeader("Content-Type","application/x-www-form-urlencoded");
//        post.addHeader("Content-Length","415");这一行千万别加，否则抛异常
            post.addHeader("Cookie","ASP.NET_SessionId=a4nyvo45nkslwv45lhhqn43u; __CSRFCOOKIE=9f8d3117-0718-430e-83d5-a15e751115f2; yunsuo_session_verify=37e3d625ecca2a27d55b2c131c042a24");
            post.addHeader("Host","www.jsqts.gov.cn");
            post.addHeader("Origin","http://www.jsqts.gov.cn");
            post.addHeader("Upgrade-Insecure-Requests","1");
            post.addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");

            List<NameValuePair> entitys = Lists.newArrayList();
            //下面的参数都是动态改变的，所以需要每次手动修改
            entitys.add(new BasicNameValuePair("__CSRFTOKEN","/wEFJDlmOGQzMTE3LTA3MTgtNDMwZS04M2Q1LWExNWU3NTExMTVmMg=="));
            entitys.add(new BasicNameValuePair("__VIEWSTATE","6mOBB1/Hb8ELZQ/YTzHTGeIEal/arM9pdvMoSy2o+OZf6C065cRY0AeSXEgGCQDqYOwJSWXt/rgkMhSdWne53+ZDCxI="));
            entitys.add(new BasicNameValuePair("__EVENTTARGET","MoreinfoList1$Pager"));
            entitys.add(new BasicNameValuePair("__EVENTARGUMENT",i+""));
            entitys.add(new BasicNameValuePair("__VIEWSTATEENCRYPTED",""));
            post.setEntity(new UrlEncodedFormEntity(entitys,Charset.forName("utf-8")));
            HttpResponse response = httpClient.execute(post);
//            System.out.println(response.getStatusLine().getStatusCode()); 200？
            String html = EntityUtils.toString(response.getEntity(), Charset.forName("utf-8"));
            Document doc = Jsoup.parse(html,"http://www.jsqts.gov.cn/zjxx/GovInfoPub/Department/");
            docs.add(doc);
        }
        logger.info("get jiangsu page html done,size = "+docs.size());
        return docs;
    }

    public Set<String> getAllUrl() throws IOException, InterruptedException {
        Set<String> allUrl = new HashSet<>();
        List<Document> allPages = getAllPages();
        for (Document allPage : allPages) {
//            System.out.println(allPage.html());
            Elements as = allPage.select("a[onmouseout]");
            for (Element a : as) {
                allUrl.add(a.attr("abs:href"));
            }
        }
//        System.out.println(allUrl.size());//292
        return allUrl;
    }

    public void process_each_url() throws IOException, InterruptedException, ParseException {
        Set<String> allUrl = getAllUrl();
        Set<String> downLoadUrl = Sets.newHashSet();
        for (String s : allUrl) {
            WebData webdata = new WebData();
            Document doc = Jsoup.connect(s)
                    .userAgent("Mozilla")
                    .timeout(0)
                    .get();
            if (doc.select("span#InfoDetail1_lblTitle").first() != null) {
                String title = doc.select("span#InfoDetail1_lblTitle").first().text();
                webdata.setTitle(title);
            }
            if (doc.select("span#InfoDetail1_lbl_Date").first() != null) {
                String date = doc.select("span#InfoDetail1_lbl_Date").first().text();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
                Date d = sdf.parse(date);
                webdata.setPostTime(d);
            }
            webdata.setCrawlTime(new Date());
            webdata.setSourceName("江苏质监局");
            webdata.setUrl(s);
            Elements as = doc.select("iframe#navFrameContent");
            for (Element a : as) {
                Document document = Jsoup.connect("http://www.jsqts.gov.cn" + a.attr("src"))
                        .userAgent("Mozilla")
                        .timeout(0)
                        .get();
                webdata.setHtml(document.html());
                webdata.setContent(doc.text());
                Elements files = document.select("a[href]");
                for (Element file : files) {
                    String aurl = file.attr("href");

                    String base = "http://www.jsqts.gov.cn";
                    if(DownLoadUtil.isFile(aurl)){

                        //     /zjxxlogin/UploadFile/b3f9bd78-04a1-48d1-bd78-4336e413a065/00e7be49-1fbb-4edd-8cc9-ecdb1ce54b70.xlsx
                        downLoadUrl.add(base+aurl);
                    }
                }
                dao.save(webdata);
                logger.info("save jiangsu success");

            }
        }
        logger.info("save jiangsu done!");
        //System.out.println("fileSize = "+downLoadUrl.size());

//        for (String url : downLoadUrl) {
//            DownLoadUtil.download(url,"C:\\Users\\yajima\\Desktop\\zhijianju\\zhijianju\\src\\main\\resources\\fileNew\\");
//        }
//        logger.info("jiangsu xiazai done!");

    }

}
