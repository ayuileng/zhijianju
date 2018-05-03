package cn.edu.iip.nju.service;

import cn.edu.iip.nju.dao.WebDataDao;
import cn.edu.iip.nju.model.WebData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by xu on 2018/1/16.
 */
@Service
public class WebDataService {
    private static final int pageSizeForProcess = 500;//打标签时的默认页大小
    @Autowired
    WebDataDao webDataDao;

    public long count(){
        return webDataDao.count();
    }

    /**
     * 一页500条的情况下共有多少页
     * @return
     */
    public long pageNumForProcess(){
        Page<WebData> page = webDataDao.findAll(new PageRequest(0, pageSizeForProcess));
        return page.getTotalPages();
    }

    /**
     * 根据页码取数据，一页500条数据
     * @param pageNum
     * @return
     */
    public List<WebData> getByPageForProcess(int pageNum){
        Page<WebData> page = webDataDao.findAll(new PageRequest(pageNum, pageSizeForProcess));
        return page.getContent();
    }
}
