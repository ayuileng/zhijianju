package cn.edu.iip.nju.service;

import cn.edu.iip.nju.dao.InjureCaseDao;
import cn.edu.iip.nju.dao.NewsDataDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * Created by xu on 2017/12/6.
 */
@Service
public class NewsDataService {
    @Autowired
    private NewsDataDao newsDataDao;
    @Autowired
    private InjureCaseDao injureCaseDao;
    @Autowired
    private StringRedisTemplate template;
    private final int defaultPageSize = 40;

    public long count(){
        return newsDataDao.count();
    }
}
