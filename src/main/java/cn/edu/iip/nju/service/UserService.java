package cn.edu.iip.nju.service;

import cn.edu.iip.nju.dao.UserDao;
import cn.edu.iip.nju.exception.UsernameExsitedException;
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

    public User findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    @Transactional
    public void saveUser(User user) throws UsernameExsitedException {
        long count = userDao.countByUsername(user.getUsername());
        if (count > 0) {
            throw new UsernameExsitedException("用户名已经存在");
        }
        userDao.save(user);
    }

    @Transactional
    public void updateUser(User newUser) throws UsernameExsitedException {
        User user = userDao.findByUsername(newUser.getUsername());
        if (user!=null && !user.getId().equals(newUser.getId())) {
            throw new UsernameExsitedException("该用户名已被注册");
        } else {
            userDao.saveAndFlush(newUser);
        }
    }

    public boolean isPasswordRight(User user, String password_ori) {
        //TODO 如果严格的话要改成md5加密还要加上cache_token
        return user.getPassword().equals(password_ori);
    }

    public User findById(Integer id) {
        return userDao.findOne(id);
    }
}
