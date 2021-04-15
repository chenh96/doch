package cloud.chenh.doch.cron;

import cloud.chenh.doch.data.service.DocumentService;
import cloud.chenh.doch.data.service.ProjectService;
import cloud.chenh.doch.data.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CleanJob {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ProjectService projectService;
    
    @Autowired
    private DocumentService documentService;
    
    @Scheduled(fixedRate = 24 * 60 * 60 * 1000L)
    public void clean() {
        userService.clean();
        projectService.clean();
        documentService.clean();
        log.info("Cleaned all data");
    }
    
}
