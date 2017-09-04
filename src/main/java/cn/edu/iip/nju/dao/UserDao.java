package cn.edu.iip.nju.dao;

import cn.edu.iip.nju.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by xu on 2017/9/3.
 */
public interface UserDao extends JpaRepository<User,Integer> {
    User findByUsername(String username);
    long countByUsername(String username);
}
