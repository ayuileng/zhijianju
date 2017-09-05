package cn.edu.iip.nju.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
@Entity
public class UserSearchHistory implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer userId;
    //用户的搜索记录
    private String searchHistory;
    //用户的搜索时间
    private Date searchTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getSearchHistory() {
        return searchHistory;
    }

    public void setSearchHistory(String searchHistory) {
        this.searchHistory = searchHistory;
    }

    public Date getSearchTime() {
        return searchTime;
    }

    public void setSearchTime(Date searchTime) {
        this.searchTime = searchTime;
    }

    @Override
    public String toString() {
        return "UserSearchHistory{" +
                "id=" + id +
                ", userId=" + userId +
                ", searchHistory='" + searchHistory + '\'' +
                ", searchTime=" + searchTime +
                '}';
    }
}
