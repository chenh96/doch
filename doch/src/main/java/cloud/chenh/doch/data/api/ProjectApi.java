package cloud.chenh.doch.data.api;

import cloud.chenh.doch.data.entity.Project;
import cloud.chenh.doch.data.model.Result;
import cloud.chenh.doch.data.service.ProjectService;
import cloud.chenh.doch.exception.InvalidDataException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("project")
public class ProjectApi {
    
    @Autowired
    private ProjectService projectService;
    
    @GetMapping
    public Result<?> get() {
        return Result.succeed(projectService.findDisplay());
    }
    
    @PostMapping
    public Result<?> post(
            @RequestParam(required = false) Long id,
            @RequestParam String name
    ) {
        try {
            return Result.succeed(projectService.edit(id, name));
        } catch (InvalidDataException e) {
            return Result.fail(e.getMessage());
        }
    }
    
    @DeleteMapping
    public Result<?> delete(@RequestParam Long id) {
        try {
            projectService.deleteByUser(id);
            return Result.succeed();
        } catch (InvalidDataException e) {
            return Result.fail(e.getMessage());
        }
    }
    
    @PostMapping("transfer")
    public Result<?> transfer(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String username
    ) {
        try {
            return Result.succeed(projectService.transfer(id, username));
        } catch (InvalidDataException e) {
            return Result.fail(e.getMessage());
        }
    }
    
    @PostMapping("share")
    public Result<?> addShare(@RequestParam Long id) {
        try {
            Project project = projectService.share(id);
            Map<String, String> passwords = new HashMap<>();
            passwords.put("public", project.getPublicPassword());
            passwords.put("private", project.getPrivatePassword());
            return Result.succeed(passwords);
        } catch (InvalidDataException e) {
            return Result.fail(e.getMessage());
        }
    }
    
    @GetMapping("share")
    public Result<?> getShare(@RequestParam Long id) {
        try {
            Project project = projectService.findByUser(id);
            Map<String, String> passwords = new HashMap<>();
            passwords.put("public", project.getPublicPassword());
            passwords.put("private", project.getPrivatePassword());
            return Result.succeed(passwords);
        } catch (InvalidDataException e) {
            return Result.fail(e.getMessage());
        }
    }
    
    @DeleteMapping("share")
    public Result<?> cancelShare(@RequestParam Long id) {
        try {
            Project project = projectService.cancelShare(id);
            Map<String, String> passwords = new HashMap<>();
            passwords.put("public", project.getPublicPassword());
            passwords.put("private", project.getPrivatePassword());
            return Result.succeed(passwords);
        } catch (InvalidDataException e) {
            return Result.fail(e.getMessage());
        }
    }
    
}
