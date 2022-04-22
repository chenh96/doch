package cloud.chenh.doch.data.entity;

import cloud.chenh.doch.data.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Getter
@Setter
@Accessors(chain = true)
@Entity
@Table(name = "document")
public class Document extends BaseEntity {
    
    private String parent;
    
    private String name;
    
    @Lob
    private String content;
    
    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    private Project project;
    
    public Document() {
    }
    
    public Document(Long id, String parent, String name) {
        setId(id);
        setParent(parent);
        setName(name);
    }
    
}
