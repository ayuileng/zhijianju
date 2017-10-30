package cn.edu.iip.nju.crawler;

import cn.edu.iip.nju.ZhijianjuApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by xu on 2017/8/1.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ZhijianjuApplication.class)
public class CPZLJDSTest {
    @Autowired
    private CPZLJDS cpzljds;
    @Autowired
    private GJZLJDJYJYZJ gjzljdjyjyzj;
    @Autowired
    private JiangSu jiangSu;
    @Autowired
    private Suzhou suzhou;
    @Test
    public void start() throws Exception {

        cpzljds.start();
        gjzljdjyjyzj.start();
        jiangSu.start();
    }

}