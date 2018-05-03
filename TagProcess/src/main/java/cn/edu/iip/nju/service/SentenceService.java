package cn.edu.iip.nju.service;

import cn.edu.iip.nju.dao.LabelDao;
import cn.edu.iip.nju.dao.SentenceDao;
import cn.edu.iip.nju.model.Label;
import cn.edu.iip.nju.model.Sentence;
import cn.edu.iip.nju.model.WebData;
import cn.edu.iip.nju.util.WordLizeUtil.WordFilter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created by xu on 2017/10/26.
 */
@Service
public class SentenceService {
    private static Logger logger = LoggerFactory.getLogger(SentenceService.class);
    @Autowired
    private SentenceDao sentenceDao;
    private StringRedisTemplate template;
    private RedisService redisService;
    @Autowired
    private LabelDao labelDao;
    @Autowired
    private WebDataService webDataService;
    @Autowired
    private NewsDataService NewsDataService;
    //2017-10-23 23:20:59
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");





    /**
     * 拼接title和content,分句之后存储
     */
    public void sentence() {
        long pageNum = webDataService.pageNumForProcess();
        for (long i = 0; i < pageNum; i++) {
            List<WebData> pageData = webDataService.getByPageForProcess((int) i);
            for (WebData webData : pageData) {
                String content = webData.getTitle() + " " + webData.getContent();
                String[] sentence = content.split("[。？！]");
                List<Sentence> sentences = wrapperToSentence(sentence,webData.getId());
                sentenceDao.save(sentences);
                logger.info("save sentence done with webdata id = "+webData.getId());
            }
        }
    }

    private List<Sentence> wrapperToSentence(String[] sentence,long id) {
        List<Sentence> list = Lists.newArrayList();
        for (String s : sentence) {
            Sentence sen = new Sentence();
            sen.setDocumentId(id);
            sen.setContent(s);
            list.add(sen);
        }
        return list;
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
                //Date postTime = webDataDao.getOne(documentId).getPostTime();
//                if (postTime != null) {
//                    label.setPosttime(sdf.format(postTime));
//                }
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
        StringBuilder sb = new StringBuilder();
        for (String s : collection) {
            sb.append(s);
            sb.append(" ");
        }
        return sb.toString().trim();
    }




}
