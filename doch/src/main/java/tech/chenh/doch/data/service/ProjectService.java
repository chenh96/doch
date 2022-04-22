package tech.chenh.doch.data.service;

import tech.chenh.doch.auth.UserManager;
import tech.chenh.doch.data.base.BaseService;
import tech.chenh.doch.data.entity.Project;
import tech.chenh.doch.data.repo.ProjectRepository;
import tech.chenh.doch.exception.InvalidDataException;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class ProjectService extends BaseService<Project> {

    public static final String PUBLIC_PREFIX = "PUB";
    public static final String PRIVATE_PREFIX = "PRI";

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

        var project = id == null ?
            new Project().setUser(userService.findByUsername(userManager.get())) :
            findByUser(id);

        project.setName(name);

        return save(project);
    }

    public List<Project> findDisplay() {
        return getRepository().findDisplay(userManager.get());
    }

    public Project findByUser(Long id) throws InvalidDataException {
        var project = findById(id);
        if (project == null) {
            throw new InvalidDataException("找不到该项目");
        }
        if (
            userManager.get() == null ||
                project.getUser() == null ||
                !project.getUser().getUsername().equals(userManager.get())
        ) {
            throw new InvalidDataException("无权操作此项目");
        }
        return project;
    }

    @Transactional
    public void deleteByUser(Long id) throws InvalidDataException {
        getRepository().deleteByIdAndUser_Username(id, userManager.get());
    }

    public Project share(Long id) throws InvalidDataException {
        var project = findByUser(id)
            .setPublicPassword(PUBLIC_PREFIX + RandomUtils.nextInt(100000, 999999))
            .setPrivatePassword(PRIVATE_PREFIX + RandomUtils.nextInt(100000, 999999));
        return save(project);
    }

    public Project cancelShare(Long id) throws InvalidDataException {
        var project = findByUser(id)
            .setPublicPassword(null)
            .setPrivatePassword(null);
        return save(project);
    }

    public Project findPrivateShare(Long id, String privatePassword) throws InvalidDataException {
        var project = getRepository().findFirstByIdAndPrivatePassword(id, privatePassword);
        if (project == null) {
            throw new InvalidDataException("找不到该项目");
        }
        return project;
    }

    public void checkShare(Long id, String password) throws InvalidDataException {
        var project = findById(id);
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
        var project = findByUser(id);
        var user = userService.findByUsername(username);
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
