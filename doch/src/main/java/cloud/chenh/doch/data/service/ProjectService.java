package cloud.chenh.doch.data.service;

import cloud.chenh.doch.auth.UserManager;
import cloud.chenh.doch.data.base.BaseService;
import cloud.chenh.doch.data.entity.Project;
import cloud.chenh.doch.data.entity.User;
import cloud.chenh.doch.data.repo.ProjectRepository;
import cloud.chenh.doch.exception.InvalidDataException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class ProjectService extends BaseService<Project> {
    
    public static final String PUBLIC_PREFIX = "pub-";
    public static final String PRIVATE_PREFIX = "pri-";
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserManager userManager;
    
    @Override
    public ProjectRepository getRepository() {
        return projectRepository;
    }
    
    public Project edit(Long id, String name) throws InvalidDataException {
        if (StringUtils.isBlank(name) || name.length() < 2 || name.length() > 32) {
            throw new InvalidDataException("请输入2到32位项目名");
        }
        
        Project project;
        if (id == null) {
            project = new Project();
            project.setUser(userManager.get());
        } else {
            project = findByUser(id);
            if (project == null) {
                throw new InvalidDataException("找不到该项目");
            }
        }
        
        project.setName(name);
        
        return save(project);
    }
    
    public List<Project> findDisplay() {
        return getRepository().findDisplay(userManager.get());
    }
    
    public Project findByUser(Long id) throws InvalidDataException {
        Project project = findById(id);
        if (project == null) {
            throw new InvalidDataException("找不到该项目");
        }
        if (userManager.get() == null ||
                project.getUser() == null ||
                !project.getUser().getId().equals(userManager.get().getId())) {
            throw new InvalidDataException("无权操作此项目");
        }
        return project;
    }
    
    @Transactional
    public void deleteByUser(Long id) throws InvalidDataException {
        getRepository().deleteByIdAndUser(id, userManager.get());
    }
    
    public Project share(Long id) throws InvalidDataException {
        Project project = findByUser(id);
        project.setPublicPassword(PUBLIC_PREFIX + UUID.randomUUID().toString());
        project.setPrivatePassword(PRIVATE_PREFIX + UUID.randomUUID().toString());
        return save(project);
    }
    
    public Project cancelShare(Long id) throws InvalidDataException {
        Project project = findByUser(id);
        project.setPublicPassword(null);
        project.setPrivatePassword(null);
        return save(project);
    }
    
    public Project findPrivateShare(Long id, String privatePassword) throws InvalidDataException {
        Project project = getRepository().findFirstByIdAndPrivatePassword(id, privatePassword);
        if (project == null) {
            throw new InvalidDataException("找不到该项目");
        }
        return project;
    }
    
    public void checkShare(Long id, String password) throws InvalidDataException {
        Project project = findById(id);
        if (project == null) {
            throw new InvalidDataException("找不到该项目");
        }
        if (StringUtils.isBlank(password)) {
            throw new InvalidDataException("未授权的访问");
        }
        if (!password.equals(project.getPublicPassword()) && !password.equals(project.getPrivatePassword())) {
            throw new InvalidDataException("未授权的访问");
        }
    }
    
    public Project transfer(Long id, String username) throws InvalidDataException {
        Project project = findByUser(id);
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new InvalidDataException("找不到该用户");
        }
        project.setUser(user);
        return save(project);
    }
    
    public void clean() {
        getRepository().deleteAllByUserNull();
    }
    
}
