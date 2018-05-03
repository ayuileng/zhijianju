package cn.edu.iip.nju.web;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * Created by xu on 2017/8/2.
 */
@RestController
public class SolrController {
    @Autowired
    private SolrClient client;


    @RequestMapping("/solrtest")
    public String test() throws IOException, SolrServerException {
        SolrDocument document = client.getById("judge_Core","1");
        return document.toString();
    }
}
