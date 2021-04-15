package cloud.chenh.doch.data.api;

import cloud.chenh.doch.data.model.Result;
import cloud.chenh.doch.data.service.DocumentService;
import cloud.chenh.doch.data.service.ProjectService;
import cloud.chenh.doch.exception.InvalidDataException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("share")
public class ShareApi {
    
    @Autowired
    private ProjectService projectService;
    
    @Autowired
    private DocumentService documentService;
    
    @GetMapping
    public Result<?> get(
            @RequestParam(required = false) Long documentId,
            @RequestParam(required = false) Long projectId,
            @RequestParam String password
    ) {
        try {
            projectService.checkShare(projectId, password);
            if (documentId != null) {
                return Result.succeed(documentService.shareOne(documentId, password));
            }
            if (projectId != null) {
                return Result.succeed(documentService.shareDisplay(projectId, password));
            }
            return Result.fail("非法请求");
        } catch (InvalidDataException e) {
            return Result.fail(e.getMessage());
        }
    }
    
    @GetMapping("info")
    public Result<?> getEditInfo(
            @RequestParam(required = false) Long documentId,
            @RequestParam Long projectId,
            @RequestParam String password) {
        try {
            projectService.checkShare(projectId, password);
            
            Map<String, Object> result = new HashMap<>();
            
            result.put("parents", documentService.shareParents(projectId, password));
            result.put("document", documentId == null ? null : documentService.shareOne(documentId, password));
            
            return Result.succeed(result);
        } catch (InvalidDataException e) {
            return Result.fail(e.getMessage());
        }
    }
    
    @PostMapping
    public Result<?> post(
            @RequestParam(required = false) Long documentId,
            @RequestParam Long projectId,
            @RequestParam String parent,
            @RequestParam String name,
            @RequestParam(required = false) String content,
            @RequestParam String password
    ) {
        try {
            projectService.checkShare(projectId, password);
            return Result.succeed(documentService.saveShare(documentId, projectId, parent, name, content, password));
        } catch (InvalidDataException e) {
            return Result.fail(e.getMessage());
        }
    }
    
    @DeleteMapping
    public Result<?> delete(
            @RequestParam Long documentId,
            @RequestParam Long projectId,
            @RequestParam String password) {
        try {
            projectService.checkShare(projectId, password);
            documentService.deleteShare(documentId, password);
            return Result.succeed();
        } catch (InvalidDataException e) {
            return Result.fail(e.getMessage());
        }
    }
    
}
