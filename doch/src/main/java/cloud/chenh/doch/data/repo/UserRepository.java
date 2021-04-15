package cloud.chenh.doch.data.repo;

import cloud.chenh.doch.data.base.BaseRepository;
import cloud.chenh.doch.data.entity.User;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface UserRepository extends BaseRepository<User> {
    
    User findFirstByUsername(String username);
    
    Boolean existsByUsername(String username);
    
    Boolean existsByUsernameAndIdNot(String username, Long id);
    
    User findFirstByToken(String token);
    
    void deleteAllByOperateAtBefore(Date before);
    
}
