package cn.edu.iip.nju.service;

import cn.edu.iip.nju.dao.LabelDao;
import cn.edu.iip.nju.dao.SentenceDao;
import cn.edu.iip.nju.dao.WebDataDao;
import cn.edu.iip.nju.model.Label;
import cn.edu.iip.nju.model.Sentence;
import cn.edu.iip.nju.model.WebData;
import cn.edu.iip.nju.util.WordLizeUtil.WordFilter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by xu on 2017/10/26.
 */
@Service
public class SentenceService {
    private static Logger logger = LoggerFactory.getLogger(SentenceService.class);
    private final SentenceDao sentenceDao;
    private final WebDataDao webDataDao;
    private final StringRedisTemplate template;
    private final RedisService redisService;
    private final LabelDao labelDao;
    //2017-10-23 23:20:59
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public SentenceService(SentenceDao sentenceDao, WebDataDao webDataDao, StringRedisTemplate template, RedisService redisService, LabelDao labelDao) {
        this.sentenceDao = sentenceDao;
        this.webDataDao = webDataDao;
        this.template = template;
        this.redisService = redisService;
        this.labelDao = labelDao;
    }

    public Page<WebData> getAllData(Pageable pageable) {
        return webDataDao.findAll(pageable);
    }

    //1. 拼接title和content,分句之后存储
    public void sentence() {
        int defaultSize = 1000;
        Page<WebData> firstPage = getAllData(new PageRequest(0, defaultSize));
        int totalPages = firstPage.getTotalPages();
        for (int i = 0; i < totalPages; i++) {
            for (WebData webData : getAllData(new PageRequest(i, defaultSize)).getContent()) {
                String content = webData.getTitle() + " " + webData.getContent();
                String[] sentence = content.split("[。？！]");
                //用set来去重
                HashSet<String> set = Sets.newHashSet(sentence);
                for (String s : set) {
                    Sentence sen = new Sentence();
                    sen.setDocumentId(webData.getId());
                    sen.setContent(s);
                    sentenceDao.save(sen);
                }
            }
        }
    }


    //获取所有句子所属文档的ID
    public List<Integer> getIds() {
        List<Object> ids = sentenceDao.getDocumentIds();
        List<Integer> list = Lists.newArrayList();
        for (Object id : ids) {
            list.add((Integer) id);
        }
        return list;

    }

    public void getLabel(List<Integer> list) {
        for (Integer documentId : list) {
            List<Label> labels = labelExtract(documentId);
            labelDao.save(labels);
        }
    }


    //每一句话抽取出Label
    public List<Label> labelExtract(Integer documentId) {
        List<Label> labels = Lists.newArrayList();
        List<Sentence> allSens = sentenceDao.getAllByDocumentId(documentId);
        for (Sentence sen : allSens) {
            //处理每一句话,如果那句话不包含product就直接跳过
            Set<String> product = searchKeyWord(sen.getContent(), "product");
            if (product == null || product.size() <= 0) {
                continue;
            } else {
                Label label = new Label();
                label.setProductName(joinToString(product));
                label.setDocumentId(documentId);
                //伤害类型
                Set<String> injureType = Sets.union(searchKeyWord(sen.getContent(), "allInjureType"),
                        searchKeyWord(sen.getContent(), "allFengxian"));
                label.setInjureType(joinToString(injureType));
                Set<String> zhaohui = searchKeyWord(sen.getContent(), Sets.newHashSet("召回"));
                label.setZhaohui(joinToString(zhaohui));
                Set<String> cities = searchKeyWord(sen.getContent(), "所有城市");
                String string = joinToString(cities);
                if (string.length() > 128) {
                    string = "北京";
                }
                label.setArea(string);
                Date postTime = webDataDao.getOne(documentId).getPostTime();
                if (postTime != null) {
                    label.setPosttime(sdf.format(postTime));
                }
                labels.add(label);
            }
        }
        return labels;
    }


    /**
     * @param s:需要匹配的字符串
     * @param redisSetName:redis中该set的名称
     * @return
     */
    private Set<String> searchKeyWord(String s, String redisSetName) {
        Set<String> serial1 = template.opsForSet().members(redisSetName);
        WordFilter wordFilter = new WordFilter();
        wordFilter.init(serial1);
        wordFilter.doFilter(s);
        return wordFilter.getKeyword();
    }

    /**
     * @param s:需要匹配的字符串
     * @param keywords:关键字集合
     * @return
     */
    private Set<String> searchKeyWord(String s, Set<String> keywords) {
        WordFilter wordFilter = new WordFilter();
        wordFilter.init(keywords);
        wordFilter.doFilter(s);
        return wordFilter.getKeyword();
    }


    private String joinToString(Collection<String> collection) {
        if (collection.size() > 20) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (String s : collection) {
            sb.append(s);
            sb.append(" ");
        }
        return sb.toString().trim();
    }




}
