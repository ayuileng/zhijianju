package cn.edu.iip.nju.dao;

import cn.edu.iip.nju.model.NewsData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by xu on 2017/11/29.
 */
@Repository
public interface NewsDataDao extends JpaRepository<NewsData,Integer>{
    long countAllByIsInjureNewsEquals(boolean bool);
    Page<NewsData> findAllByIsInjureNewsEquals(boolean bool, Pageable pageable);

}
