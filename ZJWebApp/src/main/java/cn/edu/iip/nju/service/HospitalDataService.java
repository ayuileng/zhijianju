package cn.edu.iip.nju.service;

import cn.edu.iip.nju.common.HospitalEnum;
import cn.edu.iip.nju.common.PageHelper;
import cn.edu.iip.nju.dao.HospitalDataDao;
import cn.edu.iip.nju.model.HospitalData;
import cn.edu.iip.nju.util.ProductCatUtil;
import cn.edu.iip.nju.util.WarningDegree;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Created by xu on 2017/10/23.
 */
@Service
public class HospitalDataService {
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
    @Cacheable(value = "injureLocationCount",key = "'1'+#location")
    public Long countByLocation(String location) {
        return hospitalDataDao.countAllByInjureLocation(location);
    }

    //根据月份查询记录数
    @Cacheable(value = "monthhospitalcount",key = "'1'+#month")
    public int countByMonth(int month) {
        return Math.toIntExact(hospitalDataDao.countByMonth(month));
    }

    //根据条件sql查询并封装分页结果
    public PageHelper<HospitalData> getListData(String sql, int page, String countSQL) {
        List<HospitalData> hospitalData = hospitalDataDao.pagingGet(sql, page);
        long count = hospitalDataDao.conditionCount(countSQL);
        PageHelper<HospitalData> pageData = new PageHelper(page, (int) count);
        pageData.setContent(hospitalData);
        return pageData;
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
