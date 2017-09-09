package cn.edu.iip.nju.service;

import cn.edu.iip.nju.ZhijianjuApplication;
import cn.edu.iip.nju.model.UserSearchHistory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by xu on 2017/9/5.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ZhijianjuApplication.class)
public class UserSearchHistoryServiceTest {
    @Test
    public void isNewSearchHistory() throws Exception {
        UserSearchHistory history1 = new UserSearchHistory();
        history1.setSearchTime(new Date(2017,9,9,16,11));
        history1.setUserId(2);
        history1.setSearchHistory("123");
        userSearchHistoryService.saveSearchHistory(history1);
        UserSearchHistory history2 = new UserSearchHistory();
        history2.setSearchTime(new Date(2017,9,9,16,31));
        history2.setUserId(2);
        history2.setSearchHistory("123");
        UserSearchHistory history3 = new UserSearchHistory();
        history3.setSearchTime(new Date(2017,9,9,16,51));
        history3.setUserId(2);
        history3.setSearchHistory("123");
        System.out.println(userSearchHistoryService.isNewSearchHistory(history2));//F
        System.out.println(userSearchHistoryService.isNewSearchHistory(history3));//T
    }

    @Autowired
    private UserSearchHistoryService userSearchHistoryService;
    @Test
    public void saveSearchHistory() throws Exception {
        Random rand = new Random();
        for (int i = 0; i < 10; i++) {
            UserSearchHistory userSearchHistory = new UserSearchHistory();
            userSearchHistory.setSearchHistory("word"+i);
            userSearchHistory.setUserId(rand.nextInt(10));
            userSearchHistory.setSearchTime(new Date());
            Thread.sleep(2000);
            userSearchHistoryService.saveSearchHistory(userSearchHistory);
        }
    }

    @Test
    public void getAllHistoryByUserId() throws Exception {
        List<UserSearchHistory> history = userSearchHistoryService.getAllHistoryByUserId(8);
        history.forEach(System.out::println);
    }

    @Test
    public void getRecentHistory() throws Exception {
        Page<UserSearchHistory> recentHistory = userSearchHistoryService.getRecentHistory(8, new PageRequest(0, 3));
        recentHistory.getContent().forEach(System.out::println);
    }

    @Test
    public void deleteAllHistoryByUserId() throws Exception {
    }

}