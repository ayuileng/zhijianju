package cn.edu.iip.nju.dao;

import cn.edu.iip.nju.model.JingDong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by xu on 2017/11/18.
 */
@Repository
public interface JingDongDao extends JpaRepository<JingDong,Integer>{
}
