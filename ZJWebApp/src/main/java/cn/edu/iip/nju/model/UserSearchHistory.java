package cn.edu.iip.nju.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
@Entity
@Data
public class UserSearchHistory implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer userId;
    //用户的搜索记录
    private String searchHistory;
    //用户的搜索时间
    private Date searchTime;

}
