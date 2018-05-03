package cn.edu.iip.nju.service;

import cn.edu.iip.nju.dao.AttachmentDataDao;
import cn.edu.iip.nju.dao.CompanyNegativeListDao;
import cn.edu.iip.nju.model.AttachmentData;
import cn.edu.iip.nju.model.CompanyNegativeList;
import cn.edu.iip.nju.util.CityProvince;
import com.google.common.collect.Sets;
import org.assertj.core.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Created by xu on 2017/10/26.
 * 完成产品负面清单的创建，数据来源是附件中的信息
 */
@Service
public class CompanyNegativeListService {
    private static Logger logger = LoggerFactory.getLogger(CompanyNegativeListService.class);

    @Autowired
    private CompanyNegativeListDao companyNegativeListDao;
    @Autowired
    AttachmentDataService attachmentDataService;

    /**
     * 根据附件的数据生成企业负面清单
                //企业名称
                //案例数
                //伤害程度
                //召回次数
                //抽查合格率
                //企业所在省份
     */
    public void tagProcess() {
        Set<String> companyName = attachmentDataService.getAllCompanys();
        HashSet<String> set = Sets.newHashSet();
        for (String name : companyName) {
            set.add(name);
        }
        //每一个企业的遍历
        for (String s : set) {
            CompanyNegativeList companyNegativeList = new CompanyNegativeList();
            companyNegativeList.setCompanyName(s);//企业名称
            String province = "";
            try {
                province = CityProvince.chooseProvinceOfCompany(s);
            } catch (IOException e) {
                logger.error("无法获取到企业所在地!");
            }
            if (!Strings.isNullOrEmpty(province)) {
                companyNegativeList.setProvince(province);//所在省份
            }
            long numsOfFactory = attachmentDataService.numsOfFactory(s);
            companyNegativeList.setCaseNum((int) numsOfFactory);//案例数
            long pass = attachmentDataService.numsOfFactoryByResult(s, "合格");
            long notPass = attachmentDataService.numsOfFactoryByResult(s, "不合格");
            if (pass + notPass == 0) {
                companyNegativeList.setPassPercent(-1.0);
            } else {
                companyNegativeList.setPassPercent(pass / (pass + notPass + 0.0));//合格率
            }

            companyNegativeList.setPassCase((int) pass);
            companyNegativeList.setUnPassCase((int) notPass);
            //TODO 和质检再商量一下召回次数和伤害严重程度
            //companyNegativeList.setCallbackNum(0);
            //companyNegativeList.setInjureDegree();
            companyNegativeListDao.save(companyNegativeList);
            logger.info("saving 企业负面信息");
        }
        logger.info("企业负面清单 save done");

    }

}
