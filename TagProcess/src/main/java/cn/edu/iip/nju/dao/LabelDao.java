package cn.edu.iip.nju.dao;

import cn.edu.iip.nju.model.Label;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by xu on 2017/10/27.
 */
public interface LabelDao extends JpaRepository<Label,Integer> {
}
