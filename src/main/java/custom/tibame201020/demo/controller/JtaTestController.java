package custom.tibame201020.demo.controller;

import custom.tibame201020.demo.service.JtaTestService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/jta")
public class JtaTestController {
    private final Log logger = LogFactory.getLog(JtaTestController.class);
    private final JtaTestService jtaTestService;

    public JtaTestController(JtaTestService jtaTestService) {
        this.jtaTestService = jtaTestService;
    }

    @PostMapping("/testAll")
    public ResponseEntity<String> testAll() {
        try {
            jtaTestService.testAll();
            return ResponseEntity.ok("JTA transaction completed successfully");
        } catch (Exception e) {
            logger.error("JTA transaction failed", e);
            return ResponseEntity.badRequest().body("JTA transaction failed: " + e.getMessage());
        }
    }

    @PostMapping("/test1")
    public ResponseEntity<String> testJtaTransaction1() {
        try {
            jtaTestService.testJtaTransaction();
            return ResponseEntity.ok("JTA transaction 1 completed successfully");
        } catch (Exception e) {
            logger.error("JTA transaction failed", e);
            return ResponseEntity.badRequest().body("JTA transaction 1 failed: " + e.getMessage());
        }
    }

    @PostMapping("/test2")
    public ResponseEntity<String> testJtaTransaction2() {
        try {
            jtaTestService.testJtaTransaction2();
            return ResponseEntity.ok("JTA transaction 2 completed successfully");
        } catch (Exception e) {
            logger.error("JTA transaction failed", e);
            return ResponseEntity.badRequest().body("JTA transaction 2 failed: " + e.getMessage());
        }
    }

    @PostMapping("/test3")
    public ResponseEntity<String> testJtaTransaction3() {
        try {
            jtaTestService.testJtaTransaction3();
            return ResponseEntity.ok("JTA transaction 3 completed successfully");
        } catch (Exception e) {
            logger.error("JTA transaction failed", e);
            return ResponseEntity.badRequest().body("JTA transaction 3 failed: " + e.getMessage());
        }
    }
}
