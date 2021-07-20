package cloud.chenh.doch.data.repo;

import cloud.chenh.doch.data.base.BaseRepository;
import cloud.chenh.doch.data.entity.Project;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends BaseRepository<Project> {
    
    @Query("SELECT NEW Project(prj.id, prj.name, SIZE(prj.documents)) FROM Project prj WHERE prj.user.username = ?1")
    List<Project> findDisplay(String username);
    
    void deleteByIdAndUser_Username(Long id, String username);
    
    Project findFirstByIdAndPrivatePassword(Long id, String privatePassword);
    
    void deleteAllByUserNull();
    
}
