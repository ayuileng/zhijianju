package cn.edu.iip.nju.dao;

import cn.edu.iip.nju.model.InjureCase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by xu on 2017/10/24.
 */
@Repository
public interface InjureCaseDao extends JpaRepository<InjureCase,Integer> {
    Page<InjureCase> findAllByInjureTypeNotNullAndAndInjureTypeNot(Pageable pageable,String not);
    Page<InjureCase> findAllByProductName(Pageable pageable,String productName);

    long countAllByProvinceLike(String prov);

    long countAllByProvinceLikeAndInjureDegreeEquals(String prov,String injureDegree);

    InjureCase findFirstByProductNameOrderByInjureTimeAsc(String productName);

    InjureCase findFirstByProductNameOrderByInjureTimeDesc(String productName);

    long countAllByProductNameAndInjureTypeNot(String productName,String not);

    @Query(value = "select * from injure_case_ori where injure_time = null ",nativeQuery = true)
    List<InjureCase> findAllByTime();

    void deleteAllByIdIn(List<Integer> ids);

    List<InjureCase> getAllByProductNameLike(String productName);
    List<InjureCase> getAllByInjureTypeLike(String injureType);
}
