package com.gestiontemps.controleur;

import com.gestiontemps.dto.requete.RequeteTache;
import com.gestiontemps.dto.reponse.ReponseApi;
import com.gestiontemps.dto.reponse.ReponseTache;
import com.gestiontemps.service.ServiceTache;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final ServiceTache taskService;

    @GetMapping
    public ResponseEntity<ReponseApi<List<ReponseTache>>> getAllTasks() {
        List<ReponseTache> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(ReponseApi.success(tasks));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReponseApi<ReponseTache>> getTaskById(@PathVariable Long id) {
        ReponseTache task = taskService.getTaskById(id);
        return ResponseEntity.ok(ReponseApi.success(task));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<ReponseApi<List<ReponseTache>>> getTasksByProject(@PathVariable Long projectId) {
        List<ReponseTache> tasks = taskService.getTasksByProject(projectId);
        return ResponseEntity.ok(ReponseApi.success(tasks));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ReponseApi<List<ReponseTache>>> getTasksByUser(@PathVariable Long userId) {
        List<ReponseTache> tasks = taskService.getTasksByUser(userId);
        return ResponseEntity.ok(ReponseApi.success(tasks));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ReponseApi<ReponseTache>> createTask(@Valid @RequestBody RequeteTache request) {
        ReponseTache task = taskService.createTask(request);
        return ResponseEntity.ok(ReponseApi.success("Tâche créée avec succès", task));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ReponseApi<ReponseTache>> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody RequeteTache request) {
        ReponseTache task = taskService.updateTask(id, request);
        return ResponseEntity.ok(ReponseApi.success("Tâche mise à jour avec succès", task));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ReponseApi<Void>> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok(ReponseApi.success("Tâche supprimée avec succès", null));
    }

    @PostMapping("/{taskId}/assign/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ReponseApi<Void>> assignTaskToUser(
            @PathVariable Long taskId,
            @PathVariable Long userId) {
        taskService.assignTaskToUser(taskId, userId);
        return ResponseEntity.ok(ReponseApi.success("Tâche assignée avec succès", null));
    }

    @DeleteMapping("/{taskId}/assign/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ReponseApi<Void>> unassignTaskFromUser(
            @PathVariable Long taskId,
            @PathVariable Long userId) {
        taskService.unassignTaskFromUser(taskId, userId);
        return ResponseEntity.ok(ReponseApi.success("Tâche désassignée avec succès", null));
    }
}