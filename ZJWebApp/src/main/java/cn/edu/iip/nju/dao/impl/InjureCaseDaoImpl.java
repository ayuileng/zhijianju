package cn.edu.iip.nju.dao.impl;

import cn.edu.iip.nju.dao.SQL.InjureCaseDaoSQL;
import cn.edu.iip.nju.model.InjureCase;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.List;

/**
 * Created by xu on 2017/10/23.
 */
@Repository
public class InjureCaseDaoImpl implements InjureCaseDaoSQL {
    @PersistenceContext
    private EntityManager em;
    @Override
    public List<InjureCase> search(String sql){
        Query query = em.createNativeQuery(sql,InjureCase.class);
        List<InjureCase> resultList = query.getResultList();
        return resultList;
    }

    @Override
    public long cousql(String sql) {
        BigInteger result = (BigInteger) em.createNativeQuery(sql).getSingleResult();
        return result.longValue();
    }

}
