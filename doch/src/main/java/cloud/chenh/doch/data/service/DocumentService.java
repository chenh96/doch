package cloud.chenh.doch.data.service;

import cloud.chenh.doch.auth.UserManager;
import cloud.chenh.doch.data.base.BaseService;
import cloud.chenh.doch.data.entity.Document;
import cloud.chenh.doch.data.entity.Project;
import cloud.chenh.doch.data.repo.DocumentRepository;
import cloud.chenh.doch.exception.InvalidDataException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class DocumentService extends BaseService<Document> {
    
    @Autowired
    private ProjectService projectService;
    
    @Autowired
    private UserManager userManager;
    
    @Autowired
    private DocumentRepository documentRepository;
    
    @Override
    public DocumentRepository getRepository() {
        return documentRepository;
    }
    
    public List<String> findParents(Long projectId) {
        return getRepository().findParents(projectId, userManager.get());
    }
    
    public Document findByUser(Long id) throws InvalidDataException {
        Document document = findById(id);
        if (document == null) {
            throw new InvalidDataException("找不到该文档");
        }
        if (userManager.get() == null ||
                document.getProject() == null ||
                document.getProject().getUser() == null ||
                !document.getProject().getUser().getUsername().equals(userManager.get())) {
            throw new InvalidDataException("无权操作此项目");
        }
        return document;
    }
    
    public List<Document> findDisplay(Long projectId) throws InvalidDataException {
        return getRepository().findDisplay(projectId, userManager.get());
    }
    
    public Document findOne(Long id) throws InvalidDataException {
        Document document = getRepository().findFirstByIdAndProject_User_Username(id, userManager.get());
        if (document == null) {
            throw new InvalidDataException("找不到该文档");
        }
        return document;
    }
    
    public Document save(Long documentId, Long projectId, String parent, String name, String content)
            throws InvalidDataException {
        
        if (StringUtils.isBlank(parent) || parent.length() < 2 || parent.length() > 32) {
            throw new InvalidDataException("请输入2到32位分组名");
        }
        
        if (StringUtils.isBlank(name) || name.length() < 2 || name.length() > 32) {
            throw new InvalidDataException("请输入2到32位文档名");
        }
        
        Project project = projectService.findByUser(projectId);
        Document document = documentId == null ? new Document() : findByUser(documentId);
        
        document.setParent(parent);
        document.setName(name);
        document.setContent(content);
        document.setProject(project);
        
        return save(document);
    }
    
    @Transactional
    public void deleteByUser(Long id) {
        getRepository().deleteByIdAndProject_User_Username(id, userManager.get());
    }
    
    public List<String> shareParents(Long projectId, String password) {
        return getRepository().shareParents(projectId, password);
    }
    
    public List<Document> shareDisplay(Long projectId, String password) {
        return getRepository().shareDisplay(projectId, password);
    }
    
    public Document shareOne(Long documentId, String password) throws InvalidDataException {
        Document document = getRepository().shareOne(documentId, password);
        if (document == null) {
            throw new InvalidDataException("找不到该文档");
        }
        return document;
    }
    
    public Document shareEdit(Long documentId, String password) throws InvalidDataException {
        Document document = getRepository().findFirstByIdAndProject_PrivatePassword(documentId, password);
        if (document == null) {
            throw new InvalidDataException("找不到该文档");
        }
        return document;
    }
    
    public Document saveShare(
            Long documentId,
            Long projectId,
            String parent,
            String name,
            String content,
            String password
    ) throws InvalidDataException {
        if (StringUtils.isBlank(parent) || parent.length() < 2 || parent.length() > 32) {
            throw new InvalidDataException("请输入2到32位分组名");
        }
        
        if (StringUtils.isBlank(name) || name.length() < 2 || name.length() > 32) {
            throw new InvalidDataException("请输入2到32位文档名");
        }
        
        Document document;
        if (documentId == null) {
            Project project = projectService.findPrivateShare(projectId, password);
            document = new Document();
            document.setProject(project);
        } else {
            document = shareEdit(documentId, password);
        }
        
        document.setParent(parent);
        document.setName(name);
        document.setContent(content);
        
        return save(document);
    }
    
    @Transactional
    public void deleteShare(Long id, String password) {
        getRepository().deleteByIdAndProject_PrivatePassword(id, password);
    }
    
    public void clean() {
        getRepository().deleteAllByProjectNull();
    }
    
}
