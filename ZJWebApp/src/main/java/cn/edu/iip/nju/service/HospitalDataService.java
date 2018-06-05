package cn.edu.iip.nju.service;

import cn.edu.iip.nju.dao.HospitalDataDao;
import cn.edu.iip.nju.model.HospitalData;
import cn.edu.iip.nju.model.vo.HospitalForm;
import cn.edu.iip.nju.util.WarningDegree;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Created by xu on 2017/10/23.
 */
@Service
public class HospitalDataService {
    public static final int PAGESIZE = 50;
    private final HospitalDataDao hospitalDataDao;

    @Autowired
    public HospitalDataService(HospitalDataDao hospitalDataDao) {
        this.hospitalDataDao = hospitalDataDao;
    }


    //查询所有地点
    public Set<String> getLocations() {
        Set<String> set = Sets.newHashSet();
        List<String> location = hospitalDataDao.getLocation();
        location.forEach(s -> set.add(s));
        return set;
    }

    //根据location查询记录数
    @Cacheable(value = "injureLocationCount", key = "'1'+#location")
    public Long countByLocation(String location) {
        return hospitalDataDao.countAllByInjureLocation(location);
    }


    //根据月份查询记录数
    @Cacheable(value = "monthhospitalcount", key = "'1'+#month")
    public int countByMonth(int month) {
        return Math.toIntExact(hospitalDataDao.countByMonth(month));
    }

    /**
     * 根据前端的条件参数返回响应的数据
     * @param hospitalForm
     * @return
     */
    public Page<HospitalData> getByCondition(HospitalForm hospitalForm) {
        Date from = hospitalForm.getDatefrom();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.set(2010,1,1);
        Date start = new Date(calendar.getTimeInMillis());
        Date to = hospitalForm.getDateto();
        int page = hospitalForm.getPage();
        String howGetInjure = hospitalForm.getHowgetInjure();
        String injureDegree = hospitalForm.getInjureDegree();
        String productType = hospitalForm.getProductType();
        Page<HospitalData> list = hospitalDataDao.findAll((Specification<HospitalData>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicateList = Lists.newArrayList();
            if(from == null){
                predicateList.add(criteriaBuilder.between(root.get("injureDate"),start,to));
            }else{
                predicateList.add(criteriaBuilder.between(root.get("injureDate"),from,to));
            }
            if(!Strings.isNullOrEmpty(howGetInjure)){
                predicateList.add(criteriaBuilder.equal(root.get("howGetInjure"),howGetInjure));
            }
            if(!Strings.isNullOrEmpty(injureDegree)){
                predicateList.add(criteriaBuilder.equal(root.get("injureDegree"),injureDegree));
            }
            if(!Strings.isNullOrEmpty(productType)){
                predicateList.add(criteriaBuilder.like(root.get("product"),"%"+productType+"%"));
            }
            Predicate[] result = new Predicate[predicateList.size()];
            return criteriaBuilder.and(predicateList.toArray(result));
        }, new PageRequest(page-1, PAGESIZE));
        return list;
    }


    public Map<String, Double> pca() throws Exception {
//        服装：1
//        家具及家庭生活用品：2
//        文教体育用品：3
//        锐器：4
//        玩具：5
//        家用电器：6
//        其他：7
        //顺序 f p 轻中重
        Map<String, Double> pca = Maps.newTreeMap();
        String[] cats = {"服装", "家具及家庭生活用品", "文教体育用品", "锐器", "玩具", "家用电器", "其他"};
        for (String cat : cats) {
            Long F = hospitalDataDao.countAllByProductCat(2017, cat);
            System.out.println(F);
            Date date = hospitalDataDao.findLastDateByProductCat(cat);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            Calendar today = Calendar.getInstance();
            Long p = ((today.getTimeInMillis() - calendar.getTimeInMillis()) / (1000 * 60 * 60 * 24));
            Long sc1 = hospitalDataDao.countAllByInjureDegreeAndProductCat(2017, "轻度", cat);
            Long sc2 = hospitalDataDao.countAllByInjureDegreeAndProductCat(2017, "中度", cat);
            Long sc3 = hospitalDataDao.countAllByInjureDegreeAndProductCat(2017, "重度", cat);
            System.out.println(WarningDegree.warningScore(F, p, sc1, sc2, sc3));
            pca.put(cat, WarningDegree.warningScore(F, p, sc1, sc2, sc3));

        }
        return pca;
    }
}
