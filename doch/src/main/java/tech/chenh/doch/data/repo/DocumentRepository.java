package tech.chenh.doch.data.repo;

import org.springframework.transaction.annotation.Transactional;
import tech.chenh.doch.data.base.BaseRepository;
import tech.chenh.doch.data.entity.Document;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends BaseRepository<Document> {
    
    @Query("SELECT DISTINCT parent FROM Document  WHERE project.id = ?1 AND project.user.username = ?2 ORDER BY parent ASC")
    List<String> findParents(Long projectId, String username);
    
    @Query("SELECT NEW Document(id, parent, name) FROM Document " +
            "WHERE project.id = ?1 AND project.user.username = ?2 ORDER BY name asc")
    List<Document> findDisplay(Long projectId, String username);
    
    Document findFirstByIdAndProject_User_Username(Long id, String username);

    @Transactional
    void deleteByIdAndProject_User_Username(Long id, String username);
    
    @Query("SELECT DISTINCT parent FROM Document " +
            "WHERE project.id = ?1 AND (project.publicPassword = ?2 OR project.privatePassword = ?2) " +
            "ORDER BY parent ASC")
    List<String> shareParents(Long projectId, String password);
    
    @Query("SELECT NEW Document(id, parent, name) FROM Document " +
            "WHERE project.id = ?1 AND (project.publicPassword = ?2 OR project.privatePassword = ?2) " +
            "ORDER BY name asc")
    List<Document> shareDisplay(Long projectId, String password);
    
    @Query("FROM Document WHERE id = ?1 AND (project.publicPassword = ?2 OR project.privatePassword = ?2)")
    Document shareOne(Long documentId, String password);
    
    Document findFirstByIdAndProject_PrivatePassword(Long documentId, String password);

    @Transactional
    void deleteByIdAndProject_PrivatePassword(Long id, String password);

    @Transactional
    void deleteAllByProjectNull();
    
}
