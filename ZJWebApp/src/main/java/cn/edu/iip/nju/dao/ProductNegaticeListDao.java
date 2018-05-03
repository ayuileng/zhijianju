package cn.edu.iip.nju.dao;

import cn.edu.iip.nju.model.ProductNegativeList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by xu on 2017/10/26.
 */
@Repository
public interface ProductNegaticeListDao extends JpaRepository<ProductNegativeList,Integer>{

}
