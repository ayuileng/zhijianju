package cn.edu.iip.nju.dao;

import cn.edu.iip.nju.model.CompanyNegativeList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by xu on 2017/10/26.
 */
@Repository
public interface CompanyNegativeListDao extends JpaRepository<CompanyNegativeList,Integer>,JpaSpecificationExecutor<CompanyNegativeList> {
    Page<CompanyNegativeList> findAllByPassPercentGreaterThan(Pageable pageable, Double low);
    Long countAllByProvince(String province);
    Long countAllByProvinceStartingWithAndPassPercentBetween(String prov, Double from, Double to);
    List<CompanyNegativeList> getAllByCompanyName(String companyName);

    Page<CompanyNegativeList> findAll(Specification<CompanyNegativeList> var1, Pageable pageable);
}
