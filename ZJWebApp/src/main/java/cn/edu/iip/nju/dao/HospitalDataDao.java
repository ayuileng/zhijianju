package cn.edu.iip.nju.dao;

import cn.edu.iip.nju.dao.SQL.HospitalDataDaoSQL;
import cn.edu.iip.nju.model.HospitalData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by xu on 2017/10/23.
 */
@Repository
public interface HospitalDataDao extends JpaRepository<HospitalData,Integer> ,HospitalDataDaoSQL {

    @Query("select distinct h.injureLocation from HospitalData h")
    List<String> getLocation();

    Long countAllByInjureLocation(String location);

    @Query(value = "select count(1) from hospital_data where month(injure_date) = ?1",nativeQuery = true)
    Long countByMonth(int month);

    @Query(value = "select count(1) from hospital_data where year(injure_date) = ?1 and product_cat = ?2",nativeQuery = true)
    Long countAllByProductCat(int year,String productCat);

    @Query(value = "SELECT count(1) from hospital_data WHERE year(injure_date) = ?1 and injure_degree = ?2 and product_cat = ?3",nativeQuery = true)
    Long countAllByInjureDegreeAndProductCat(int year,String injureDegree,String productCat);

    @Query(value = "SELECT injure_date FROM hospital_data WHERE product_cat = ?1 ORDER BY injure_date DESC  LIMIT 1",nativeQuery = true)
    Date findLastDateByProductCat(String productCat);
}
