package cn.edu.iip.nju.service;

import cn.edu.iip.nju.ZhijianjuApplication;
import cn.edu.iip.nju.dao.LabelDao;
import cn.edu.iip.nju.model.InjureCase;
import cn.edu.iip.nju.model.Label;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * Created by xu on 2017/10/25.
 */
@RunWith(SpringRunner.class)
@Rollback(value = false)
@SpringBootTest(classes = ZhijianjuApplication.class)
public class InjureCaseServiceTest {
    @Autowired
    InjureCaseService injureCaseService;
    @Autowired
    private LabelDao labelDao;

    @Test
    public void getByCondition() throws Exception {
        int num = 500;
        Page<Label> all = labelDao.findAll(new PageRequest(0, num));
        for (int i = 0; i < all.getTotalPages(); i++) {
            Page<Label> page = labelDao.findAll(new PageRequest(i, num));
            List<InjureCase> list = injureCaseService.saveInjureCases(page.getContent());
            injureCaseService.save(list);
        }

    }

    @Test
    public void test() {
        for (InjureCase injureCase : injureCaseService.getInjureCases(new PageRequest(1, 10))) {
            System.out.println(injureCase);
        }
    }

}