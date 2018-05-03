package cn.edu.iip.nju.dao;


import cn.edu.iip.nju.model.HospitalData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by xu on 2017/10/23.
 */
@Repository
public interface HospitalDataDao extends JpaRepository<HospitalData,Integer>{

}
