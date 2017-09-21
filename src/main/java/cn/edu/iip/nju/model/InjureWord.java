package cn.edu.iip.nju.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by xu on 2017/9/21.
 */
@Entity
public class InjureWord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;



}
