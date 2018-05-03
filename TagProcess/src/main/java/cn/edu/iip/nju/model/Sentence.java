package cn.edu.iip.nju.model;




import javax.persistence.*;

/**
 * Created by xu on 2017/9/21.
 * 每篇文档进行分句.
 */
@Entity
@Table(indexes = {@Index(name = "doc_id",columnList ="document_id")})
public class Sentence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "document_id")
    private Long documentId;

    @Lob
    @Column(name = "content", columnDefinition = "LONGTEXT")
    private String content;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Sentence() {
    }


    @Override
    public String toString() {
        return "Sentence{" +
                "id=" + id +
                ", documentId=" + documentId +
                ", content='" + content + '\'' +
                '}';
    }
}
