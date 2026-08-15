package com.hostel.tracker.bootstrap;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BootstrapController {

    private final BootstrapService bootstrap;
    private final ImportService importService;

    public BootstrapController(BootstrapService bootstrap, ImportService importService) {
        this.bootstrap = bootstrap;
        this.importService = importService;
    }

    @GetMapping("/api/bootstrap")
    public BootstrapDtos.BootstrapResponse bootstrap() {
        return bootstrap.load();
    }

    @PostMapping("/api/admin/import")
    @PreAuthorize("hasRole('ADMIN')")
    public ImportDtos.ImportResult importData(@RequestBody ImportDtos.AppDataImport data) {
        return importService.importData(data);
    }
}
