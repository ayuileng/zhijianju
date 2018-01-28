package cn.edu.iip.nju.service;

import cn.edu.iip.nju.ZhijianjuApplication;
import cn.edu.iip.nju.dao.HospitalDataDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

/**
 * Created by xu on 2017/10/23.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ZhijianjuApplication.class)
public class HospitalDataServiceTest {
    @Test
    public void countByLocation() throws Exception {
        for (String s : hospitalDataService.getLocations()) {
            System.out.println(s);
            System.out.println(hospitalDataService.countByLocation(s));
        }
    }

    @Test
    public void countByMonth() throws Exception {
        System.out.println(hospitalDataService.countByMonth(9));
    }

    @Autowired
    private HospitalDataService hospitalDataService;
    @Autowired
    private HospitalDataDao hospitalDataDao;


    @Test
    public void readExcelToDatabase() throws Exception {
        hospitalDataService.readExcelToDatabase();
    }

    @Test
    public void setProductCat() {
        long count = hospitalDataDao.count();
        int defaultSize = 1000;
        int pageNum = (int) (count / defaultSize + 1);
        for (int i = 0; i < pageNum; i++) {
            hospitalDataService.setProductCat(new PageRequest(i,defaultSize));
        }
    }

    @Test
    public void pca() throws Exception {
        Map<String, Double> pca = hospitalDataService.pca();
        for (Map.Entry<String, Double> entry : pca.entrySet()) {
            System.out.println(entry.getKey()+"->"+entry.getValue());
        }
    }


}