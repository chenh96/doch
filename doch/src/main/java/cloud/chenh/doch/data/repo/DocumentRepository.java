package cloud.chenh.doch.data.repo;

import cloud.chenh.doch.data.base.BaseRepository;
import cloud.chenh.doch.data.entity.Document;
import cloud.chenh.doch.data.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends BaseRepository<Document> {
    
    @Query("SELECT DISTINCT parent FROM Document  WHERE project.id = ?1 AND project.user = ?2 ORDER BY parent ASC")
    List<String> findParents(Long projectId, User user);
    
    @Query("SELECT NEW Document(id, parent, name) FROM Document " +
            "WHERE project.id = ?1 AND project.user = ?2 ORDER BY name asc")
    List<Document> findDisplay(Long projectId, User user);
    
    Document findFirstByIdAndProject_user(Long id, User user);
    
    void deleteByIdAndProject_user(Long id, User user);
    
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
    
    Document findFirstByIdAndProject_privatePassword(Long documentId, String password);
    
    void deleteByIdAndProject_privatePassword(Long id, String password);
    
    void deleteAllByProjectNull();
    
}
