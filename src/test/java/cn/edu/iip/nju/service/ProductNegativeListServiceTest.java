package cn.edu.iip.nju.service;

import cn.edu.iip.nju.ZhijianjuApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by xu on 2017/11/13.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ZhijianjuApplication.class)
public class ProductNegativeListServiceTest {
    @Autowired
    private ProductNegativeListService productNegativeListService;
    @Test
    public void test1() throws Exception {
        productNegativeListService.test();
    }

}