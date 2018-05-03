package cn.edu.iip.nju.service;

import cn.edu.iip.nju.dao.UserSearchHistoryDao;
import cn.edu.iip.nju.model.UserSearchHistory;
import org.apache.commons.lang3.StringUtils;
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
    //如果搜索是同一个单词并且搜索间隔在30分钟内，则判定为分页时产生的搜索请求
    public boolean isNewSearchHistory(UserSearchHistory history) {
        Integer id = history.getUserId();
        UserSearchHistory oldHistory = userSearchHistoryDao.findFirstByUserIdOrderBySearchTimeDesc(id);
        if (oldHistory == null) {
            return true;
        }else if (StringUtils.equals(oldHistory.getSearchHistory(), history.getSearchHistory()) && (history.getSearchTime().getTime() - oldHistory.getSearchTime().getTime()) < 1000 * 60 * 30) {
            return false;
        }
        return true;


    }

    @Transactional
    public void deleteAllHistoryByUserId(Integer userId) {
        userSearchHistoryDao.deleteAllByUserId(userId);
    }

}
