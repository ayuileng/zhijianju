package cn.edu.iip.nju.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by xu on 2017/10/24.
 * 伤害案例库
 * 使用了jpa建立索引
 */
@Entity
@Data
@Table(indexes = {@Index(name = "injure_case_product_name_index",columnList ="productName"),
        @Index(name = "injure_case_injure_type_index",columnList ="injureType"),
        @Index(name = "injure_case_injure_degree_index",columnList ="injureDegree")})
public class InjureCase implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    private String productName;//产品名

    private Date injureTime;//伤害发生时间

    private String injureArea;//伤害发生地点


    private String injureType;//伤害类别

    private String province;

    private String injureDegree;

    @Column(name = "url",unique = true)
    private String url;
}
