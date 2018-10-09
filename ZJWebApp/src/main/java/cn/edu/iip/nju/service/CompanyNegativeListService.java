package cn.edu.iip.nju.service;

import cn.edu.iip.nju.dao.AttachmentDataDao;
import cn.edu.iip.nju.dao.CompanyNegativeListDao;
import cn.edu.iip.nju.model.AttachmentData;
import cn.edu.iip.nju.model.CompanyNegativeList;
import cn.edu.iip.nju.model.vo.CompanyForm;
import com.google.common.base.Strings;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by xu on 2017/10/26.
 * 完成产品负面清单的创建，以及字段封装，向controller返回分页结果
 */
@Service
@Log4j
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

    public Page<CompanyNegativeList> getByCondition(CompanyForm companyForm) {
        String factory = companyForm.getFactory();
        String province = companyForm.getProvince();
        final String factor = factory == null?"":factory.trim();
        final String provinc = province == null?"":province.trim();
        int sort = companyForm.getSort();//0-按照不合格率降序（即合格率升序） 1-按照召回次数降序
        Page<CompanyNegativeList> list = companyNegativeListDao.findAll((root, criteriaQuery, criteriaBuilder) -> {
            if (Strings.isNullOrEmpty(factor) && Strings.isNullOrEmpty(provinc)) {
                return null;
            } else if (!Strings.isNullOrEmpty(factor)) {
                return criteriaBuilder.like(root.get("companyName"), "%" + factor + "%");
            } else if (!Strings.isNullOrEmpty(provinc)) {
                return criteriaBuilder.like(root.get("province"), "%" + provinc + "%");
            } else {
                return criteriaBuilder.and(criteriaBuilder.like(root.get("province"), "%" + provinc + "%"),
                        criteriaBuilder.like(root.get("companyName"), "%" + factor + "%"));
            }
        }, new PageRequest(companyForm.getPage()-1, 50, sort == 0 ?
                new Sort(Sort.Direction.ASC, "passPercent") :
                new Sort(Sort.Direction.DESC, "callbackNum")));
        return list;
    }

}
