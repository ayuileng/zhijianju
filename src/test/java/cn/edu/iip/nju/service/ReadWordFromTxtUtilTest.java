package cn.edu.iip.nju.service;

import cn.edu.iip.nju.ZhijianjuApplication;
import cn.edu.iip.nju.common.FengxianType;
import cn.edu.iip.nju.common.InjureLevel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;

/**
 * Created by xu on 2017/9/21.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ZhijianjuApplication.class)
public class ReadWordFromTxtUtilTest {
    @Test
    public void getFengxianWords() throws Exception {
        readKeyWord.readFengxianWord();
        Set<String> fengxianWords = readKeyWord.getFengxianWords(FengxianType.化学伤害.getName());
        fengxianWords.forEach(System.out::println);
    }

    @Test
    public void getInjureWords() throws Exception {
        readKeyWord.getInjureWords(InjureLevel.LEVELONE.getName());
    }

    @Autowired
    private ReadWordFromTxtUtil readKeyWord;
    @Test
    public void readInjureWords() throws Exception {
        readKeyWord.readInjureWords();
    }

}