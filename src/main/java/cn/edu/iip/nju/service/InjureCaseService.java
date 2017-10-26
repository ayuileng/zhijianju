package cn.edu.iip.nju.service;

import cn.edu.iip.nju.dao.InjureCaseDao;
import cn.edu.iip.nju.model.InjureCase;
import cn.edu.iip.nju.common.PageHelper;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by xu on 2017/10/24.
 */
@Service
public class InjureCaseService {
    private final InjureCaseDao injureCaseDao;

    @Autowired
    public InjureCaseService(InjureCaseDao injureCaseDao) {
        this.injureCaseDao = injureCaseDao;
    }
    //测试用
    public List<InjureCase> makeData(){
        Random random = new Random();
        InjureCase injureCase;
        List<InjureCase> list = Lists.newArrayList();
        for (int i = 0; i < 100; i++) {
            injureCase = new InjureCase("product"+random.nextInt(100),"type"+random.nextInt(100),"brand"+random.nextInt(100),
                    new Date(),"area"+random.nextInt(100),"injureType"+random.nextInt(100),"injureDegree"+random.nextInt(100),random.nextInt(100));
            list.add(injureCase);
        }
        return list;
    }

    @Transactional
    public void saveTestData(){
        injureCaseDao.save(makeData());
    }

    public List<InjureCase> getInjureCases(Pageable pageable){
        //saveTestData();
        return injureCaseDao.findAll(pageable).getContent();
    }


    //service层分页
    public PageHelper<InjureCase> getByCondition(String sql,Integer page){
        List<InjureCase> allCases = injureCaseDao.search(sql);
        PageHelper<InjureCase> pageData = new PageHelper<>(page,allCases.size());
        pageData.setContent(allCases.subList(pageData.getBeginIndex(),pageData.getEndIndex()));
        return pageData;

    }





}
