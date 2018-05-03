package cn.edu.iip.nju.service;

import cn.edu.iip.nju.dao.LabelDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 对一篇网页文档进行打标签，生成标签库并保存
 */
@Service
public class LabelService {
    @Autowired
    LabelDao labelDao;
    @Autowired
    SentenceService sentenceService;
    @Autowired
    WebDataService webDataService;

    /**
     * 处理每一篇文档
     * @param webDataId
     */
    public void extractLabel(int webDataId){

    }


}
