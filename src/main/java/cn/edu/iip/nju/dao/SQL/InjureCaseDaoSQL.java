package cn.edu.iip.nju.dao.SQL;

import cn.edu.iip.nju.model.InjureCase;

import java.util.List;

/**
 * Created by xu on 2017/10/24.
 */
public interface InjureCaseDaoSQL {
    List<InjureCase> search(String sql);
}
