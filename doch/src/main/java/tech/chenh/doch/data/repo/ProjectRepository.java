package tech.chenh.doch.data.repo;

import org.springframework.transaction.annotation.Transactional;
import tech.chenh.doch.data.base.BaseRepository;
import tech.chenh.doch.data.entity.Project;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends BaseRepository<Project> {
    
    @Query("SELECT NEW Project(prj.id, prj.name, SIZE(prj.documents)) FROM Project prj WHERE prj.user.username = ?1")
    List<Project> findDisplay(String username);

    @Transactional
    void deleteByIdAndUser_Username(Long id, String username);
    
    Project findFirstByIdAndPrivatePassword(Long id, String privatePassword);

    @Transactional
    void deleteAllByUserNull();
    
}
