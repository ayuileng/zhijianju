package cn.edu.iip.nju.service;

import cn.edu.iip.nju.dao.InjureCaseDao;
import cn.edu.iip.nju.model.InjureCase;
import cn.edu.iip.nju.model.vo.InjureCaseForm;
import cn.edu.iip.nju.model.vo.ProductNegListForm;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by xu on 2017/10/24.
 */
@Service
@Transactional
public class InjureCaseService {
    private final InjureCaseDao injureCaseDao;
    public static final int PAGESIZE = 50;

    @Autowired
    public InjureCaseService(InjureCaseDao injureCaseDao) {
        this.injureCaseDao = injureCaseDao;
    }


    @Cacheable(value = "proInjureCaseCount", key = "#prov")
    public long countByProv(String prov) {
        return injureCaseDao.countAllByProvinceLike(prov);
    }

    @Cacheable(value = "companyProvAndDegreeCount", key = "#prov+#injureDrgree")
    public long countByProvAndInjureDegree(String prov, String injureDrgree) {
        return injureCaseDao.countAllByProvinceLikeAndInjureDegreeEquals(prov, injureDrgree);
    }


    public List<InjureCase> getProductListByCondition(ProductNegListForm form) {
        Date from = form.getDatefrom();
        Date to = form.getDateto();
        String injureDegree = form.getInjureDegree();
        String productName = form.getProductName();
        String province = form.getProvince();
        String injureType = form.getInjureType();

        Date start = getDate();

        return injureCaseDao.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicateList = com.google.common.collect.Lists.newArrayList();
            if (from == null) {
                predicateList.add(criteriaBuilder.between(root.get("injureTime"), start, to));
            } else {
                predicateList.add(criteriaBuilder.between(root.get("injureTime"), from, to));
            }
            if (!Strings.isNullOrEmpty(injureDegree)) {
                predicateList.add(criteriaBuilder.equal(root.get("injureDegree"), injureDegree));
            }
            if (!Strings.isNullOrEmpty(productName)) {
                predicateList.add(criteriaBuilder.like(root.get("productName"), "%" + productName + "%"));
            }
            if (!Strings.isNullOrEmpty(province)) {
                predicateList.add(criteriaBuilder.like(root.get("province"), "%" + province + "%"));
            }
            if (!Strings.isNullOrEmpty(injureType)) {
                predicateList.add(criteriaBuilder.like(root.get("injureType"), "%" + injureType + "%"));
            }
            Predicate[] result = new Predicate[predicateList.size()];
            return criteriaBuilder.and(predicateList.toArray(result));
        });
    }

    private Date getDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.set(2010, 1, 1);
        return new Date(calendar.getTimeInMillis());
    }

    public Page<InjureCase> getByCondition(InjureCaseForm injureCaseForm) {

        Date from = injureCaseForm.getDatefrom();
        Date to = injureCaseForm.getDateto();
        String productName = injureCaseForm.getProductName();
        String injureDegree = injureCaseForm.getInjureDegree();
        String injureType = injureCaseForm.getInjureType();
        String area = injureCaseForm.getArea();
        String province = injureCaseForm.getProvince();
        int page = injureCaseForm.getPage();
        int sort = injureCaseForm.getSort();//1时间升序 2时间降序 3伤害程度升序 4伤害程度降序
        Sort s;
        switch (sort) {
            case 1:
                s = new Sort(Sort.Direction.ASC,"injureTime");
                break;
            case 2:
                s = new Sort(Sort.Direction.DESC,"injureTime");
                break;
            case 3:
                s = new Sort(Sort.Direction.DESC,"injureDegree");
                break;
            case 4:
                s = new Sort(Sort.Direction.ASC,"injureDegree");
                break;
            default:
                s = new Sort(Sort.Direction.DESC,"injureTime");
                break;
        }
        Date start = getDate();
        return injureCaseDao.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicateList = com.google.common.collect.Lists.newArrayList();
            if (from == null) {
                predicateList.add(criteriaBuilder.between(root.get("injureTime"), start, to));
            } else {
                predicateList.add(criteriaBuilder.between(root.get("injureTime"), from, to));
            }
            if (!Strings.isNullOrEmpty(injureDegree)) {
                predicateList.add(criteriaBuilder.equal(root.get("injureDegree"), injureDegree));
            }
            if (!Strings.isNullOrEmpty(productName)) {
                predicateList.add(criteriaBuilder.like(root.get("productName"), "%" + productName + "%"));
            }
            if (!Strings.isNullOrEmpty(area)) {
                predicateList.add(criteriaBuilder.like(root.get("injureArea"), "%" + area + "%"));
            }
            if (!Strings.isNullOrEmpty(province)) {
                predicateList.add(criteriaBuilder.like(root.get("province"), "%" + province + "%"));
            }
            if (!Strings.isNullOrEmpty(injureType)) {
                predicateList.add(criteriaBuilder.like(root.get("injureType"), "%" + injureType + "%"));
            }
            Predicate[] result = new Predicate[predicateList.size()];
            return criteriaBuilder.and(predicateList.toArray(result));
        }, new PageRequest(page-1, PAGESIZE,s));


    }
}
