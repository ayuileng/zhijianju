package cn.edu.iip.nju.dao;

import cn.edu.iip.nju.model.InjureCase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by xu on 2017/10/24.
 */
@Repository
public interface InjureCaseDao extends JpaRepository<InjureCase,Integer>,JpaSpecificationExecutor<InjureCase> {
    Page<InjureCase> findAllByInjureTypeNotNullAndAndInjureTypeNot(Pageable pageable,String not);
    Page<InjureCase> findAllByProductName(Pageable pageable,String productName);

    long countAllByProvinceLike(String prov);

    long countAllByProvinceLikeAndInjureDegreeEquals(String prov,String injureDegree);

    InjureCase findFirstByProductNameOrderByInjureTimeAsc(String productName);

    InjureCase findFirstByProductNameOrderByInjureTimeDesc(String productName);

    long countAllByProductNameAndInjureTypeNot(String productName,String not);

    List<InjureCase> findAll(Specification<InjureCase> specification);

    Page<InjureCase> findAll(Specification<InjureCase> specification, Pageable pageable);
}
