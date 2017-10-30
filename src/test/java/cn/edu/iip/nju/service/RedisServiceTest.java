package cn.edu.iip.nju.service;

import cn.edu.iip.nju.ZhijianjuApplication;
import cn.edu.iip.nju.common.InjureLevel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by xu on 2017/9/21.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ZhijianjuApplication.class)
public class RedisServiceTest {
    @Test
    public void readProductName() throws Exception {
        readKeyWord.readProductName();
    }

    @Test
    public void readFengxianWords() throws Exception {
        readKeyWord.readFengxianWord();

    }

    @Test
    public void getInjureWords() throws Exception {
        readKeyWord.getInjureWords(InjureLevel.LEVELONE.getName());
    }

    @Autowired
    private RedisService readKeyWord;
    @Test
    public void readInjureWords() throws Exception {
        readKeyWord.readInjureWords();
    }

}