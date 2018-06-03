package cn.edu.iip.nju.dao;

import cn.edu.iip.nju.model.NewsData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by xu on 2017/11/29.
 */
@Repository
public interface NewsDataDao extends JpaRepository<NewsData,Integer>{
    List<NewsData> getAllByIdIn(List<Integer> ids);
}
