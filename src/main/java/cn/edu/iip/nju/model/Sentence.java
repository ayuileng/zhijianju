package cn.edu.iip.nju.model;




import javax.persistence.*;

/**
 * Created by xu on 2017/9/21.
 * 每篇文档进行分句.
 */
@Entity
public class Sentence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer documentId;
    @Lob
    @Column(name = "content", columnDefinition = "LONGTEXT")
    private String content;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Integer documentId) {
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

    public Sentence(Integer documentId, String content) {
        this.documentId = documentId;
        this.content = content;
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
