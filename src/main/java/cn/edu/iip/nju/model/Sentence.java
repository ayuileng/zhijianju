package cn.edu.iip.nju.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by xu on 2017/9/21.
 * 每篇文档进行分句.
 */
@Entity
public class Sentence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer documentID;
    private String content;

}
