package cn.edu.iip.nju.service;

import cn.edu.iip.nju.ZhijianjuApplication;
import cn.edu.iip.nju.model.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by xu on 2017/9/8.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ZhijianjuApplication.class)
public class SolrDocumentServiceTest {
    @Autowired
    private SolrDocumentService solrDocumentService;
    @Test
    public void findBySearchText() throws Exception {
        Page<Document> toys = solrDocumentService.findBySearchText("玩具", new PageRequest(88, 10));
        System.out.println(toys.getContent().get(0));

    }

}