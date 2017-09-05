package cn.edu.iip.nju.service;

import cn.edu.iip.nju.dao.UserSearchHistoryDao;
import cn.edu.iip.nju.model.UserSearchHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by xu on 2017/9/5.
 */
@Service
public class UserSearchHistoryService {
    private final UserSearchHistoryDao userSearchHistoryDao;

    @Autowired
    public UserSearchHistoryService(UserSearchHistoryDao userSearchHistoryDao) {
        this.userSearchHistoryDao = userSearchHistoryDao;
    }

    @Transactional
    public void saveSearchHistory(UserSearchHistory history) {
        userSearchHistoryDao.save(history);
    }

    public List<UserSearchHistory> getAllHistoryByUserId(Integer userId) {
        return userSearchHistoryDao.findAllByUserIdOrderBySearchTimeDesc(userId);
    }

    public Page<UserSearchHistory> getRecentHistory(Integer userId, Pageable pageable) {
        return userSearchHistoryDao.findAllByUserIdOrderBySearchTimeDesc(userId, pageable);
    }

    @Transactional
    public void deleteAllHistoryByUserId(Integer userId){
        userSearchHistoryDao.deleteAllByUserId(userId);
    }

}
