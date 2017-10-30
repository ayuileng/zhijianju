package cn.edu.iip.nju.dao;

import cn.edu.iip.nju.model.HospitalData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by xu on 2017/10/23.
 */
@Repository
public interface HospitalDataDao extends JpaRepository<HospitalData,Integer> {
    @Query("select distinct h.injureLocation from HospitalData h")
    List<Object> getLocation();
    Long countAllByInjureLocation(String location);
    @Query(value = "select count(1) from hospital_data where month(injure_date) = ?1",nativeQuery = true)
    Long countByMonth(int month);

}
