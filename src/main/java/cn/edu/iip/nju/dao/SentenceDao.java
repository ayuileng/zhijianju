package cn.edu.iip.nju.dao;

import cn.edu.iip.nju.model.Sentence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by xu on 2017/10/26.
 */
@Repository
public interface SentenceDao extends JpaRepository<Sentence,Integer>{
    @Query("select distinct data.documentId from Sentence data")
    List<Object> getDocumentIds();
    List<Sentence> getAllByDocumentId(Integer id);
}
