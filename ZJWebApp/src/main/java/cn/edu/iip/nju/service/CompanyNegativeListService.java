package cn.edu.iip.nju.service;

import cn.edu.iip.nju.dao.AttachmentDataDao;
import cn.edu.iip.nju.dao.CompanyNegativeListDao;
import cn.edu.iip.nju.model.AttachmentData;
import cn.edu.iip.nju.model.CompanyNegativeList;
import cn.edu.iip.nju.model.form.CompanyForm;
import cn.edu.iip.nju.util.CityProvince;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
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

    public Page<CompanyNegativeList> getAll(Pageable pageable) {
        return companyNegativeListDao.findAllByPassPercentGreaterThan(pageable, 0.0);
    }


    @Cacheable(value = "companyProvCount", key = "#proName")
    public Long countByProvinceName(String proName) {
        return companyNegativeListDao.countAllByProvince(proName);
    }

    @Cacheable(value = "companyProvCountByPassPercent", key = "#prov+#from+#to")
    public long countAllByProvinceAndPassPercentBetween(String prov, Double from, Double to) {
        return companyNegativeListDao.countAllByProvinceStartingWithAndPassPercentBetween(prov, from, to);
    }

    public List<AttachmentData> getCompanyDetail(String name) {
        return attachmentDataDao.getAllByFactoryName(name);
    }

    public List<CompanyNegativeList> getByCondition(CompanyForm companyForm) {
        String factory = companyForm.getFactory();
        String province = companyForm.getProvince();
        int sort = companyForm.getSort();//0-按照案例数降序 1-按照召回次数降序
        Page<CompanyNegativeList> list = companyNegativeListDao.findAll(new Specification<CompanyNegativeList>() {
            @Override
            public Predicate toPredicate(Root<CompanyNegativeList> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                if (Strings.isNullOrEmpty(factory)&&Strings.isNullOrEmpty(province)) {
                    return null;
                }
                if(Strings.isNullOrEmpty(factory)){
                    return criteriaBuilder.like(root.get("province"), "%" + province + "%");
                }
                if(Strings.isNullOrEmpty(province)){
                    return criteriaBuilder.like(root.get("companyName"), "%" + factory + "%");
                }
                return criteriaBuilder.and(criteriaBuilder.like(root.get("province"), "%" + province + "%"),
                        criteriaBuilder.like(root.get("companyName"), "%" + factory + "%"));
            }
        }, new PageRequest(0, 50, new Sort(Sort.Direction.DESC, sort == 0 ? "caseNum" : "callbackNum")));
        return list.getContent();
    }

}
