package cn.edu.iip.nju.service;

import cn.edu.iip.nju.ZhijianjuApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * Created by xu on 2017/10/26.
 */
@RunWith(SpringRunner.class)
@Rollback(false)
@SpringBootTest(classes = ZhijianjuApplication.class)
public class SentenceServiceTest {
    @Autowired
    private SentenceService sentenceService;
    @Test
    public void sentence() throws Exception {
        sentenceService.sentence();
    }
    @Test
    public void size(){
        System.out.println(sentenceService.getAllData(new PageRequest(0,2000)).getContent().size());
    }


    @Test
    public void testLabel(){
        int defaultSize = 500;
//        sentenceService.getLabel(sentenceService.getIds());
        List<Integer> ids = sentenceService.getIds();
        if(ids.size()>defaultSize){
            int nums = ids.size() / defaultSize;
            for (int i = 0; i < nums; i++) {
                List<Integer> list = ids.subList(i * defaultSize, (i + 1) * defaultSize);
                sentenceService.getLabel(list);
            }
            sentenceService.getLabel(ids.subList(nums*defaultSize,ids.size()));
        }else {
            sentenceService.getLabel(ids);
        }
    }



}