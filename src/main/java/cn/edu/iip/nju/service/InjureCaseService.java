package cn.edu.iip.nju.service;

import cn.edu.iip.nju.common.PageHelper;
import cn.edu.iip.nju.dao.InjureCaseDao;
import cn.edu.iip.nju.dao.LabelDao;
import cn.edu.iip.nju.dao.WebDataDao;
import cn.edu.iip.nju.model.InjureCase;
import cn.edu.iip.nju.model.Label;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by xu on 2017/10/24.
 */
@Service
@Transactional
public class InjureCaseService {
    private final InjureCaseDao injureCaseDao;
    private final LabelDao labelDao;
    private final WebDataDao webDataDao;


    @Autowired
    public InjureCaseService(InjureCaseDao injureCaseDao, LabelDao labelDao, WebDataDao webDataDao) {
        this.injureCaseDao = injureCaseDao;
        this.labelDao = labelDao;
        this.webDataDao = webDataDao;
    }


    public Page<InjureCase> getInjureCases(Pageable pageable) {
        //saveTestData();
        return injureCaseDao.findAllByInjureTypeNotNullAndAndInjureTypeNot(pageable,"");
    }


    public void generateInjureCase() {
        int defaultSize = 500;

        Page<Label> all = labelDao.findAll(new PageRequest(0, defaultSize));
        for (int i = 0; i < all.getTotalPages(); i++) {
            Page<Label> page = labelDao.findAll(new PageRequest(i, defaultSize));
            List<InjureCase> list = saveInjureCases(page.getContent());
            injureCaseDao.save(list);
        }

    }

    public List<InjureCase> saveInjureCases(List<Label> content){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<InjureCase> list=Lists.newArrayList();
        for (Label label : content) {
            String pros = label.getProductName();
            String area = label.getArea();
            String injureType = label.getInjureType();
            if (isNull(pros) && isNull(injureType)) {
                continue;
            }
            String[] split = pros.split(" ");
            //每个product都是一个伤害案例
            for (String s : split) {
                InjureCase injureCase = new InjureCase();
                injureCase.setInjureType(label.getInjureType());
                injureCase.setProductName(s);
                if (label.getPosttime() == null) {
                    injureCase.setInjureTime(new Date());
                } else {
                    try {
                        injureCase.setInjureTime(sdf.parse(label.getPosttime()));
                    } catch (ParseException e) {
                        injureCase.setInjureTime(new Date());
                    }
                }
                injureCase.setInjureArea(area);
                injureCase.setUrl(webDataDao.findOne(label.getDocumentId()).getUrl());
                list.add(injureCase);
            }

        }
        return list;
    }

    public void save(List<InjureCase> list){
        injureCaseDao.save(list);
    }

    private boolean isNull(String string) {
        if (string == null || string.isEmpty()) {
            return true;
        }
        return false;
    }


    //service层分页
    public PageHelper<InjureCase> getByCondition(String sql, Integer page) {
        long count = injureCaseDao.count();
        List<InjureCase> pg = injureCaseDao.search(sql);
        PageHelper<InjureCase> pageData = new PageHelper<>(page, (int) count);
        pageData.setContent(pg);
        return pageData;

    }


}
