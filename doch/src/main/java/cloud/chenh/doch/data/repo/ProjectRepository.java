package cloud.chenh.doch.data.repo;

import cloud.chenh.doch.data.base.BaseRepository;
import cloud.chenh.doch.data.entity.Project;
import cloud.chenh.doch.data.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends BaseRepository<Project> {
    
    @Query("SELECT NEW Project(prj.id, prj.name, SIZE(prj.documents)) FROM Project prj WHERE prj.user = ?1")
    List<Project> findDisplay(User user);
    
    void deleteByIdAndUser(Long id, User user);
    
    Project findFirstByIdAndPrivatePassword(Long id, String privatePassword);
    
    void deleteAllByUserNull();

}
