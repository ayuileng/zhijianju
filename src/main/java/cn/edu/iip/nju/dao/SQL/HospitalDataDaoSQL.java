package cn.edu.iip.nju.dao.SQL;

import cn.edu.iip.nju.model.HospitalData;

import java.util.List;

/**
 * Created by xu on 2017/11/15.
 */
public interface HospitalDataDaoSQL {
    long conditionCount(String sql);
    List<HospitalData> pagingGet(String sql,int page);
}
