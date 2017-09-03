package cn.edu.iip.nju.service;

import cn.edu.iip.nju.dao.UserDao;
import cn.edu.iip.nju.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by xu on 2017/9/3.
 */
@Service
public class UserService {
    @Autowired
    private UserDao userDao;
    public User findByUsername(String username){
        return userDao.findByUsername(username);
    }

    @Transactional
    public User saveOrUpdateUser(User user){

        return userDao.save(user);
    }
}
