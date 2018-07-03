package cn.edu.iip.nju.service;

import cn.edu.iip.nju.dao.WebDataDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by xu on 2018/1/16.
 */
@Service
public class WebDataService {
    @Autowired
    WebDataDao webDataDao;

    public long count(){
        return webDataDao.count();
    }

}
