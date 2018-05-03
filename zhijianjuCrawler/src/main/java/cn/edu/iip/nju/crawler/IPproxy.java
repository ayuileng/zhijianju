package cn.edu.iip.nju.crawler;


import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.util.SocketUtils;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by libo on 2017/11/18.
 */
@Component()
public class IPproxy {
    //访问http://www.xdaili.cn/freeproxy获得一些免费的ip
    //更新频率几分钟一次，ip池为空后更新ip池可用
    // 返回结果json在
    //不够稳定
    public  Queue<Proxy> getIpfromxun ()throws  Exception {
        Queue<Proxy> ipandport=new LinkedList<>();
        int times=3;
        while (ipandport.size()<2&&times>0) {
            Thread.sleep(1000);
            try {
                Document dd = Jsoup.connect("http://www.xdaili.cn/ipagent//freeip/getFreeIps?page=1&rows=10")
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36")
                        .timeout(3000)
                        .get();
                JSONObject jo = JSONObject.fromObject(dd.text());
                //   System.out.println("jo:"+jo.toString());
                JSONArray ja = jo.getJSONObject("RESULT").getJSONArray("rows");
                //   System.out.println("ja:"+ja.toString());
                String temp = "";
                for (int i = 0; i < ja.size(); i++) {
                    JSONObject ji = ja.getJSONObject(i);
                    String ip=ji.getString("ip");
                    int port= Integer.parseInt(ji.getString("port"));
                    Proxy proxy=new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port));
                  //  System.out.println("正在检测ip。。。。。。");
                    if(ipcheck(proxy)) {
                        ipandport.offer(proxy);
                    }
                }
                times--;
                //System.out.println("times:"+times);
            } catch (Exception e) {
                e.getStackTrace();
            }
        }
            //System.out.println("此次获取IP from 讯代理" + ipandport.size() + "个");
           /* for (Proxy s : ipandport) {
                System.out.println(s.toString());
            }*/
      return  ipandport;
    }
    //访问http://www.kuaidaili.com/free/inha/1/获得一些免费的ip
    //更新频率不高，几小时一次,一天一两个页面
    public  Queue<Proxy> getIpfromkuai ()throws  Exception {
        Queue<Proxy> ipandport = new LinkedList<>();
        ArrayList<String> url=new ArrayList<>();
        String base1="http://www.kuaidaili.com/free/inha/";
        for(int i=1;i<=5;i++){
            url.add(base1+i);
        }
        for(String s:url) {
            try {
                Document dd = Jsoup.connect(s)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36")
                        .timeout(1500)
                        .get();
                Elements p = dd.select("div#list").select("tbody").select("tr");
                //System.out.println("p:"+p.toString());
                for (Element pi : p) {
                    float time = Float.parseFloat(pi.select("td[data-title=响应速度]").text().split("秒")[0]);
                    //System.out.println("time:"+time);
                    String ip = pi.select("td[data-title=IP]").text();
                    int port = Integer.parseInt(pi.select("td[data-title=PORT]").text());
                    Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port));
                    // System.out.println("正在检测ip。。。。。。");
                    if (time <= 2 && ipcheck(proxy)) {
                        ipandport.offer(proxy);
                    }
                }

            } catch (Exception e) {
                e.getStackTrace();
            }
        }
       // System.out.println("此次获取IP from 快代理" + ipandport.size() + "个");
     return  ipandport;
    }
    //采集ip from http://ipway.net/
    //15分钟一次
    //ip给力。。。。。2。3十个能用
    //2018/04/25 测试网站关闭。。。
    public  Queue<Proxy> getIpfromipway ()throws  Exception {
        Queue<Proxy> ipandport = new LinkedList<>();
        try {
            Document dd = Jsoup.connect("http://ipway.net/")
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36")
                    .timeout(5000)
                    .get();
            Element p = dd.select("div").last();
            String[] tem=p.text().split(" ");
            for(int i=0;i<tem.length;i++){
                String ip=tem[i].split(":")[0];
                int port=Integer.parseInt(tem[i].split(":")[1]);
                Proxy proxy=new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port));
                if(ipcheck(proxy)) {
                    ipandport.offer(proxy);
                }
            }
        } catch (Exception e) {
            e.getStackTrace();
        }
        //System.out.println("此次获取IP代理 from ipway.net  " + ipandport.size() + "个");
        return ipandport;
    }
    //采集ip form http://www.bugng.com/gngn?page=0
    //20分钟更新一次
    //ip较稳定??稳定个毛
    //2018/04/25 网站关闭
    public  Queue<Proxy> getIpfrombug ()throws  Exception {
        Queue<Proxy> ipandport = new LinkedList<>();
        ArrayList<String> url=new ArrayList<>();
        String base1="http://www.bugng.com/gngn?page=";
       for(int i=0;i<10;i++){
           url.add(base1+i);
       }
       for(String s:url) {
           try {
               Document dd = Jsoup.connect(s)
                       .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36")
                       .timeout(5000)
                       .get();
               Elements p = dd.select("div#table1").select("tbody#target").select("tr");
               //System.out.println(p.toString());
               for (Element pi : p) {
                   Elements r = pi.select("td");
                   String ip = r.get(0).text();
                   int port = Integer.parseInt(r.get(1).text());
                   Proxy proxy=new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port));
                   if(ipcheck(proxy)) {
                       ipandport.offer(proxy);
                   }
               }


           } catch (Exception e) {
               e.getStackTrace();
           }
       }
        //System.out.println("此次获取IP代理 from 虫代理  " + ipandport.size() + "个");
        return ipandport;
    }
       // ip from http://www.kxdaili.com/dailiip/1/1.html#ip
    //2018/04/25 网站关闭
    public  Queue<Proxy> getIpfromkx ()throws  Exception {
        Queue<Proxy> ipandport = new LinkedList<>();
        ArrayList<String> url=new ArrayList<>();
        String base1="http://www.kxdaili.com/dailiip/1/";
        for(int i=1;i<=9;i++){
            url.add(base1+i+".html#ip");
        }
        for(String s:url) {
            try {
                Document dd = Jsoup.connect(s)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36")
                        .timeout(5000)
                        .get();
                Elements p = dd.select("table[class=ui table segment]").select("tbody").select("tr");
               //System.out.println(p.toString());
               for (Element pi : p) {
                  // System.out.println("pi:"+pi.toString());
                    Elements r = pi.select("td");
                    String ip = r.get(0).text();
                    int port = Integer.parseInt(r.get(1).text());
                    Proxy proxy=new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port));
                    if(ipcheck(proxy)) {
                        ipandport.offer(proxy);
                    }
                }
            } catch (Exception e) {
                e.getStackTrace();
            }
       }
        //System.out.println("此次获取IP代理 from 开心代理  " + ipandport.size() + "个");
        return ipandport;
    }



    //访问https://www.baidu.com/本地检测ip是否可用
    //https://www.json.cn比较快
    public  boolean ipcheck(Proxy proxy)throws  Exception{
        try {
            Document dd = Jsoup.connect("https://www.json.cn")
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36")
                    .timeout(1000)
                    .proxy(proxy)
                    .get();
          // System.out.println("连接成功");
            return true;
        }catch (Exception e){
            return  false;
        }
    }
    public Queue<Proxy> getallIP()throws Exception{
        ArrayList<Queue<Proxy>> ip=new ArrayList<>();
        Queue<Proxy> res=new LinkedList<>();
        //ip.add(getIpfrombug());
        //ip.add(getIpfromipway());
        ip.add(getIpfromxun());
        ip.add(getIpfromkuai());
        //ip.add(getIpfromkx());
        for(int i=0;i<ip.size();i++) {
            Object[] ipp = ip.get(i).toArray();
            for (int j = 0; j < ipp.length; j++) {
               Proxy temp=(Proxy) ipp[j];
                    res.offer((Proxy) ipp[j]);
            }
        }
       // System.out.println("有效ip总数："+res.size());
        return res;
    }
    //[0]=ip  [1]=port
    public String[] getipandport(Proxy proxy)throws Exception{
        String[] res=new String[2];
        String tem=proxy.toString().split("/")[1];
        res[0]=tem.split(":")[0];
        res[1]=tem.split(":")[1];
        return res;
    }
    public static void main(String[] args) throws  Exception{
        IPproxy ip=new IPproxy();
        //ip.ipcheck("121.232.146.59",9000);
        ip.getIpfromkx();
        // String[] s= ip.getipandport(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("1.199.195.173", 35436)));
    }

}
