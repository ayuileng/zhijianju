package cn.edu.iip.nju.dao;

import cn.edu.iip.nju.model.CrawledUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CrawledUrlDao extends JpaRepository<CrawledUrl,Long> {
}
