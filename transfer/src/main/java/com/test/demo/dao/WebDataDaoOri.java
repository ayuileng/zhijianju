package com.test.demo.dao;

import com.test.demo.model.WebData;
import com.test.demo.model.WebDataOri;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by xu on 2017/7/31.
 */
@Repository
public interface WebDataDaoOri extends JpaRepository<WebDataOri,Integer>{


}
