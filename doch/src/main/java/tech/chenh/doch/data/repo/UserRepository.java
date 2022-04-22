package tech.chenh.doch.data.repo;

import org.springframework.transaction.annotation.Transactional;
import tech.chenh.doch.data.base.BaseRepository;
import tech.chenh.doch.data.entity.User;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface UserRepository extends BaseRepository<User> {

    User findFirstByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByUsernameAndIdNot(String username, Long id);

    @Transactional
    void deleteAllByOperateAtBefore(Date before);

}
