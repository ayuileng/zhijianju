package cn.edu.iip.nju.crawler;

import org.junit.Test;

import java.util.Set;

/**
 * Created by xu on 2017/8/1.
 */
public class SuzhouTest {
    private Suzhou suzhou = new Suzhou();
    @Test
    public void baseURLS() throws Exception {
        Set<String> baseURLS = suzhou.baseURLS();
        baseURLS.forEach(s -> System.out.println(s));
    }

    @Test
    public void pageURLs() throws Exception {
        Set<String> strings = suzhou.pageURLs();
        strings.forEach(s -> System.out.println(s));
    }

    @Test
    public void downloadURLs() throws Exception {

    }

    @Test
    public void start() throws Exception {
        suzhou.start();
    }

}