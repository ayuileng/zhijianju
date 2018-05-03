package cn.edu.iip.nju.dao;

import cn.edu.iip.nju.model.Document;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.solr.repository.Highlight;
import org.springframework.data.solr.repository.SolrCrudRepository;

/**
 * Created by xu on 2017/9/8.
 */
public interface SolrDocumentDao extends SolrCrudRepository<Document, String> {
    @Highlight(prefix = "<font color='red'>", postfix = "</font>")
    HighlightPage<Document> findBySearchText(String searchText, Pageable pageable);

}
