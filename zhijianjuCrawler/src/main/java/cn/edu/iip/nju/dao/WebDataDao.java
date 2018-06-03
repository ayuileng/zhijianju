package cn.edu.iip.nju.dao;

import cn.edu.iip.nju.model.WebData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by xu on 2017/7/31.
 */
@Repository
public interface WebDataDao extends JpaRepository<WebData,Integer>{

    List<WebData> getAllByIdGreaterThan(Integer id);

}
