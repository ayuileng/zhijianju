package cn.edu.iip.nju.service;

import cn.edu.iip.nju.ZhijianjuApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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


    @Test
    public void readExcelToDatabase() throws Exception {
        hospitalDataService.readExcelToDatabase();
    }

}