package cloud.chenh.doch.data.entity;

import cloud.chenh.doch.data.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
@Entity
@Table(name = "user")
public class User extends BaseEntity {
    
    @Column(nullable = false, unique = true)
    private String username;
    
    @JsonIgnore
    @Column(nullable = false)
    private String password;
    
    //@JsonIgnore
    //@Column(nullable = false)
    //private String salt;
    //
    //@JsonIgnore
    //@Column(unique = true)
    //private String token;
    
    @JsonIgnore
    private Date operateAt;
    
    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Project> projects;
    
}
