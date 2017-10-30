package cn.edu.iip.nju.crawler;

/**
 * Created by xu on 2017/10/26.
 */
public class MainCrawler implements Runnable {
    private Crawler c;

    public MainCrawler(Crawler c) {
        this.c = c;
    }

    @Override
    public void run() {
        c.start();
    }
}
