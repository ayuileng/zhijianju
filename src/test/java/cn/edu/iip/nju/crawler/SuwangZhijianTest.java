package cn.edu.iip.nju.crawler;

import cn.edu.iip.nju.ZhijianjuApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by xu on 2017/7/31.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ZhijianjuApplication.class)
public class SuwangZhijianTest {
    @Autowired
    SuwangZhijian suwangZhijian;
    @Test
    public void start() throws Exception {
        suwangZhijian.start();
    }

}