package com.test.demo.dao;

import com.test.demo.model.NewsData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by xu on 2017/11/29.
 */
@Repository
public interface NewsDataDao extends JpaRepository<NewsData,Integer>{
}
