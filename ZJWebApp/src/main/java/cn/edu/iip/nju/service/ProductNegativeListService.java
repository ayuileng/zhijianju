package cn.edu.iip.nju.service;

import cn.edu.iip.nju.dao.InjureCaseDao;
import cn.edu.iip.nju.model.InjureCase;
import com.google.common.base.Strings;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.Charset;

/**
 * Created by xu on 2017/10/26.
 */
@Service
@Transactional
public class ProductNegativeListService {
    @Autowired
    private InjureCaseDao injureCaseDao;

    public void test() {
//        injureCaseDao.findFirstByProductName()
        StringBuffer sb = new StringBuffer();
        int size = 1000;
        BloomFilter<String> bf = BloomFilter.create(Funnels.stringFunnel(Charset.forName("utf-8")), 1000, 0.000001);
        Page<InjureCase> tvs = injureCaseDao.findAllByProductName(new PageRequest(0, size), "玩具");
        sb.append("产品名：").append("玩具").append(" ");

        sb.append("本类产品伤害案例数：").append(injureCaseDao.countAllByProductNameAndInjureTypeNot("玩具", "")).append(" ");

        sb.append("危害起始时间：").append(injureCaseDao.findFirstByProductNameOrderByInjureTimeAsc("玩具").getInjureTime()).
                append(" 危害结束时间：").append(injureCaseDao.findFirstByProductNameOrderByInjureTimeDesc("玩具").getInjureTime()).append("\n");


        for (int i = 0; i < tvs.getTotalPages(); i++) {
            Page<InjureCase> tv = injureCaseDao.findAllByProductName(new PageRequest(i, size), "玩具");
            //分析每一个伤害事件
            for (InjureCase injureCase : tv.getContent()) {
                //如果伤害类型是空则跳过
                if (Strings.isNullOrEmpty(injureCase.getInjureType())) {
                    continue;
                }
                //针对url去重 todo
                sb.append("伤害事件为：").append(injureCase.getProductName() + " " + injureCase.getInjureType() + " 伤害严重程度：" + injureCase.getInjureDegree());
                sb.append(" 危害人口数：").append(" ").append(" ");
                sb.append("危害地域广度：").append(injureCase.getInjureArea()).append(" ");
                sb.append("危害持续时间: ").append(" ");
                sb.append("评论总数：" + 0 + " ");
                sb.append("卫视、纸媒关注总数：" + 0 + " ");
                sb.append("原文地址：" + injureCase.getUrl() + "\n");
            }
        }

        System.out.println(sb);


    }


}
