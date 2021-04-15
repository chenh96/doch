package cloud.chenh.doch.data.base;

import com.google.common.collect.Lists;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;

public abstract class BaseService<T extends BaseEntity> {
    
    public abstract BaseRepository<T> getRepository();
    
    @Transactional
    public T save(T entity) {
        return getRepository().save(entity);
    }
    
    @Transactional
    public void save(Collection<T> entities) {
        getRepository().saveAll(entities);
    }
    
    @Transactional
    public void delete(T entity) {
        getRepository().delete(entity);
    }
    
    @Transactional
    public void deleteById(Long id) {
        getRepository().deleteById(id);
    }
    
    public List<T> findAll() {
        return Lists.newArrayList(getRepository().findAll());
    }
    
    public T findById(Long id) {
        return getRepository().findById(id).orElse(null);
    }
    
}
