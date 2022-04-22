package cloud.chenh.doch.data.entity;

import cloud.chenh.doch.data.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
@Entity
@Table(name = "project")
public class Project extends BaseEntity {
    
    @Column(nullable = false)
    private String name;
    
    @JsonIgnore
    private String publicPassword;
    
    @JsonIgnore
    private String privatePassword;
    
    @JsonIgnore
    @ManyToOne(cascade = CascadeType.DETACH)
    private User user;
    
    @JsonIgnore
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<Document> documents;
    
    @Transient
    private Integer documentNumber;
    
    public Project() {
    }
    
    public Project(Long id, String name, Integer documentNumber) {
        setId(id);
        setName(name);
        setDocumentNumber(documentNumber);
    }
    
}
