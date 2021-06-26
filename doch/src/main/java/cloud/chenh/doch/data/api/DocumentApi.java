package cloud.chenh.doch.data.api;

import cloud.chenh.doch.data.model.Result;
import cloud.chenh.doch.data.service.DocumentService;
import cloud.chenh.doch.exception.InvalidDataException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("document")
public class DocumentApi {

    @Autowired
    private DocumentService documentService;

    @GetMapping
    public Result<?> get(
            @RequestParam(required = false) Long documentId,
            @RequestParam(required = false) Long projectId
    ) {
        try {
            if (documentId != null) {
                return Result.succeed(documentService.findOne(documentId));
            }
            if (projectId != null) {
                return Result.succeed(documentService.findDisplay(projectId));
            }
            return Result.fail("非法请求");
        } catch (InvalidDataException e) {
            return Result.fail(e.getMessage());
        }
    }

    @GetMapping("info")
    public Result<?> getEditInfo(@RequestParam Long projectId, @RequestParam(required = false) Long documentId) {
        try {
            Map<String, Object> result = new HashMap<>();

            result.put("parents", documentService.findParents(projectId));
            result.put("document", documentId == null ? null : documentService.findByUser(documentId));

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
            @RequestParam(required = false) String content
    ) {
        try {
            return Result.succeed(documentService.save(documentId, projectId, parent, name, content));
        } catch (InvalidDataException e) {
            return Result.fail(e.getMessage());
        }
    }

    @DeleteMapping
    public Result<?> delete(@RequestParam Long id) {
        documentService.deleteByUser(id);
        return Result.succeed();
    }

}
