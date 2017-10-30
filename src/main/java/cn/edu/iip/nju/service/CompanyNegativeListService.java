package cn.edu.iip.nju.service;

import cn.edu.iip.nju.dao.AttachmentDataDao;
import cn.edu.iip.nju.dao.CompanyNegativeListDao;
import cn.edu.iip.nju.model.CompanyNegativeList;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Random;

/**
 * Created by xu on 2017/10/26.
 * 完成产品负面清单的创建，以及字段封装，向controller返回分页结果
 */
@Service
public class CompanyNegativeListService {
    @Autowired
    private CompanyNegativeListDao companyNegativeListDao;
    @Autowired
    private AttachmentDataDao attachmentDataDao;

    public Page<CompanyNegativeList> getAll(Pageable pageable){
        return companyNegativeListDao.findAllByPassPercentGreaterThan(pageable,0.0);
    }

    public void tagProcess(){
        List<Object> companyName = attachmentDataDao.getCompanyName();
        HashSet<String> set = Sets.newHashSet();
        for (Object o : companyName) {
            set.add((String)o);
        }
        Random random = new Random(System.currentTimeMillis());
        //每一个企业的遍历
        for (String s : set) {
            CompanyNegativeList companyNegativeList = new CompanyNegativeList();
            companyNegativeList.setCompanyName(s);
            Integer caseNum = attachmentDataDao.countAllByFactoryName(s);
            companyNegativeList.setCaseNum(caseNum);
            Integer pass = attachmentDataDao.countAllByFactoryNameAndResult(s,"合格");
            Integer notPass = attachmentDataDao.countAllByFactoryNameAndResult(s,"不合格");
            if (pass+notPass==0){
                companyNegativeList.setPassPercent(-1.0);
            }else {
                companyNegativeList.setPassPercent(pass / (pass + notPass + 0.0));
            }
            //和质检再商量一下
            companyNegativeList.setCallbackNum(caseNum/(random.nextInt(2)+1));
            //companyNegativeList.setInjureDegree();
            System.out.println(companyNegativeList);
            companyNegativeListDao.save(companyNegativeList);
        }

    }




}
