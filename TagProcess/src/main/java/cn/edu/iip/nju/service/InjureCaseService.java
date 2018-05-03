package cn.edu.iip.nju.service;

import cn.edu.iip.nju.dao.InjureCaseDao;
import cn.edu.iip.nju.model.InjureCase;
import cn.edu.iip.nju.model.NewsData;
import cn.edu.iip.nju.util.CityProvince;
import cn.edu.iip.nju.util.InjureLevelUtil;
import cn.edu.iip.nju.util.ReadFileUtil;
import cn.edu.iip.nju.util.WordLizeUtil.WordFilter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;


/**
 * Created by xu on 2017/10/24.
 * 根据爬取的新闻建立伤害事件案例
 */
@Service
public class InjureCaseService {
    @Autowired
    private InjureCaseDao injureCaseDao;
    @Autowired
    private NewsDataService newsDataService;
    private static Logger logger = LoggerFactory.getLogger(InjureCaseService.class);


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


    /**
     * 提取伤害事件，首先根据标题判断是否有产品名称，如果标题中有的话直接就提取
     * 如果标题中没有，则需要在正文中找，但是正文中可能找到很多个，可以采用统计数量的方式来减少到3个以内
     * 其他字段就直接在正文中关键字碰撞
     *
     * @param newsData
     */
    private void processNews(NewsData newsData) throws IOException {
        InjureCase injureCase = new InjureCase();
        String title = newsData.getTitle();
        String content = title + newsData.getContent();


        Set<String> products = ReadFileUtil.readProducts();
        if(Strings.isNullOrEmpty(title)){
            title = " ";
        }
        Set<String> pro = searchKeyWord(title, products);
        if (pro != null && pro.size() > 0) {
            String product = joinToString(pro);
            injureCase.setProductName(product);
        } else {
            Set<String> strings = searchKeyWord(content, products);
            //取出现频次最高的三个
            List<String> proSets = frequentWord(strings, content);
            injureCase.setProductName(joinToString(proSets));

        }//产品名ok
        Set<String> cities = ReadFileUtil.readCities();
        Set<String> cs = searchKeyWord(content, cities);
        List<String> city = frequentWord(cs, content);
        if (city != null && city.size() > 0) {
            injureCase.setInjureArea(city.get(0));
            String province = CityProvince.chooseProvinceOfCompany(city.get(0));
            injureCase.setProvince(province);//发生地点和省份ok
        }



        Set<String> injureType = ReadFileUtil.readInjureType();
        Set<String> ins = searchKeyWord(content, injureType);
        List<String> injureTypeSet = frequentWord(ins, content);
        injureCase.setInjureType(joinToString(injureTypeSet));//伤害类型


        String degree = InjureLevelUtil.checkInjureLevel(joinToString(injureTypeSet));
        injureCase.setInjureDegree(degree);
        Date date = newsData.getPostTime();
        if(date == null){
            date = new Date();
        }
        injureCase.setInjureTime(date);

        injureCase.setUrl(newsData.getUrl());//url是唯一性索引，所以插入时遇到相同的url会抛异常，可以捕获

        try {
            injureCaseDao.save(injureCase);
            logger.info("save injureCase success!");
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            logger.info("skip same url data");
        }
    }

    /**
     * 返回出现次数比较高的词
     *
     * @param words    要判断的词
     * @param sentence 出现次数前三的词
     * @return
     */
    private List<String> frequentWord(Set<String> words, String sentence) {
        List<String> result = Lists.newArrayList();
        if (words == null || words.size() == 0) return result;
        if (words.size() <= 3) {
            result.addAll(words);
            return result;
        }
        Map<String, Integer> map = Maps.newHashMap();
        for (String word : words) {
            map.put(word, 0);
        }

        List<Term> terms = StandardTokenizer.segment(sentence);
        for (Term term : terms) {
            String word = term.word;
            if (words.contains(word)) {
                map.put(word, map.get(word) + 1);
            }
        }
        ArrayList<Map.Entry<String, Integer>> list = Lists.newArrayList(map.entrySet());
        list.sort(Comparator.comparing(Map.Entry::getValue));
        Collections.reverse(list);
        result.add(list.get(0).getKey());
        result.add(list.get(1).getKey());
        result.add(list.get(2).getKey());
        return result;
    }


    private String joinToString(Collection<String> collection) {
        if (collection == null || collection.size() == 0) return null;
        if (collection.size() > 20) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (String s : collection) {
            sb.append(s);
            sb.append(" ");
        }
        return sb.toString().trim();
    }

    public void saveInjureCase() {
        long pageNum = newsDataService.pageNum();
        for (long i = 0; i < pageNum; i++) {
            List<NewsData> newsDataList = newsDataService.getByPageForProcess((int) i);
            for (NewsData newsData : newsDataList) {
                try {
                    processNews(newsData);
                } catch (IOException e) {
                    logger.error("save injureCase fail", e);
                }
            }
        }
    }

}
