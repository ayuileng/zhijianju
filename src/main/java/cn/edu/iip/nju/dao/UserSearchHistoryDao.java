package cn.edu.iip.nju.dao;

import cn.edu.iip.nju.model.UserSearchHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by xu on 2017/8/8.
 */
public interface UserSearchHistoryDao extends JpaRepository<UserSearchHistory,Integer> {

    //根据用户id查询该用户的所有搜索记录，按照搜索时间降序排列
    List<UserSearchHistory> findAllByUserIdOrderBySearchTimeDesc(Integer userId);

    //上一方法的分页实现
    Page<UserSearchHistory> findAllByUserIdOrderBySearchTimeDesc(Integer userId, Pageable pageable);

    void deleteAllByUserId(Integer userId);

    //根据时间顺序查询最近的搜索记录
    List<UserSearchHistory> findAll(Sort sort);
}
