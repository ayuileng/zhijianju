package cn.edu.iip.nju.service;

import cn.edu.iip.nju.dao.SolrDocumentDao;
import cn.edu.iip.nju.model.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.stereotype.Service;

/**
 * Created by xu on 2017/9/8.
 */
@Service
public class SolrDocumentService {
    private final SolrDocumentDao solrDocumentDao;

    @Autowired
    public SolrDocumentService(SolrDocumentDao solrDocumentDao) {
        this.solrDocumentDao = solrDocumentDao;
    }


    public Page<Document> findBySearchText(String searchText, Pageable pageable) {
        int i = 0;
        HighlightPage<Document> documents = solrDocumentDao.findBySearchText(searchText.trim(), pageable);
        for (HighlightEntry<Document> documentHighlightEntry : documents.getHighlighted()) {
            Document entity = documents.getContent().get(i);
            if (entity.getContent().length() > 100) {
                entity.setContent(entity.getContent().substring(0, 99)+"...");
            }
            for (HighlightEntry.Highlight highlight : documentHighlightEntry.getHighlights()) {
                for (String snipplets : highlight.getSnipplets()) {
                    //System.out.println(highlight.getField().getName());
                    if (highlight.getField().getName().equals("title")) {
                        entity.setTitle(snipplets);
                    } else if (highlight.getField().getName().equals("html")) {
                        entity.setHtml(snipplets);
                    }
                }
            }
            i++;
        }
        return documents;
    }

    public String escapeQueryChars(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            // These characters are part of the query syntax and must be escaped
            if (c == '\\' || c == '+' || c == '-' || c == '!' || c == '('
                    || c == ')' || c == ':' || c == '^' || c == '[' || c == ']'
                    || c == '{' || c == '}' || c == '~'
                    || c == '*' || c == '?' || c == '|' || c == '&' || c == ';'
                    || c == '/') {
                sb.append('\\');
            }
            sb.append(c);
        }
        String q = sb.toString();
        q = q.replaceAll("“", "\"");   //中文引号替换
        q = q.replaceAll("”", "\"");   //中文引号替换
        q = q.toUpperCase();  //逻辑符号转换成大写
        return q;
    }
}
