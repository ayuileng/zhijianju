package cn.edu.iip.nju.crawler.fujian;

import cn.edu.iip.nju.ZhijianjuApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by xu on 2017/10/26.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ZhijianjuApplication.class)
public class ExcelProcessTest {
    @Autowired
    private ExcelProcess excelProcess;
    @Test
    public void run() throws Exception {
        excelProcess.run();
    }

}