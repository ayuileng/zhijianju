package cn.edu.iip.nju.dao.impl;

import cn.edu.iip.nju.dao.SQL.HospitalDataDaoSQL;
import cn.edu.iip.nju.model.HospitalData;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.List;

/**
 * Created by xu on 2017/11/15.
 */
public class HospitalDataDaoImpl implements HospitalDataDaoSQL {
    @PersistenceContext
    private EntityManager em;
    @Override
    public long conditionCount(String sql) {
        BigInteger result = (BigInteger) em.createNativeQuery(sql).getSingleResult();
        return result.longValue();
    }

    @Override
    public List<HospitalData> pagingGet(String sql, int page) {
        Query query = em.createNativeQuery(sql,HospitalData.class);
        List<HospitalData> resultList = query.getResultList();
        return resultList;
    }
}
