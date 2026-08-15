package com.hostel.tracker.resident;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/residents")
public class ResidentController {

    private final ResidentService residents;

    public ResidentController(ResidentService residents) {
        this.residents = residents;
    }

    @GetMapping
    public List<ResidentDtos.ResidentResponse> list() {
        return residents.list();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResidentDtos.ResidentResponse create(@Valid @RequestBody ResidentDtos.ResidentInput input) {
        return residents.create(input);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResidentDtos.ResidentResponse update(
            @PathVariable String id,
            @Valid @RequestBody ResidentDtos.ResidentInput input
    ) {
        return residents.update(id, input);
    }

    @PatchMapping("/{id}/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResidentDtos.ResidentResponse setActive(
            @PathVariable String id,
            @Valid @RequestBody ResidentDtos.ActivePatch patch
    ) {
        return residents.setActive(id, patch.active());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        residents.delete(id);
    }
}
