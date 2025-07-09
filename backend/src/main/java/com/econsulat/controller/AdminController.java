package com.econsulat.controller;

import com.econsulat.dto.DemandeAdminResponse;
import com.econsulat.dto.UserAdminResponse;
import com.econsulat.model.Demande;
import com.econsulat.model.User;
import com.econsulat.service.AdminService;
import com.econsulat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private UserService userService;

    // Gestion des demandes
    @GetMapping("/demandes")
    public ResponseEntity<Page<DemandeAdminResponse>> getAllDemandes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<DemandeAdminResponse> demandes = adminService.getAllDemandes(pageable);
        return ResponseEntity.ok(demandes);
    }

    @GetMapping("/demandes/status/{status}")
    public ResponseEntity<Page<DemandeAdminResponse>> getDemandesByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<DemandeAdminResponse> demandes = adminService.getDemandesByStatus(status, pageable);
        return ResponseEntity.ok(demandes);
    }

    @GetMapping("/demandes/search")
    public ResponseEntity<Page<DemandeAdminResponse>> searchDemandes(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<DemandeAdminResponse> demandes = adminService.searchDemandes(q, pageable);
        return ResponseEntity.ok(demandes);
    }

    @GetMapping("/demandes/{id}")
    public ResponseEntity<DemandeAdminResponse> getDemandeById(@PathVariable Long id) {
        DemandeAdminResponse demande = adminService.getDemandeById(id);
        return ResponseEntity.ok(demande);
    }

    @PutMapping("/demandes/{id}/status")
    public ResponseEntity<Demande> updateDemandeStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        Demande demande = adminService.updateDemandeStatus(id, status);
        return ResponseEntity.ok(demande);
    }

    // Gestion des utilisateurs
    @GetMapping("/users")
    public ResponseEntity<Page<UserAdminResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<UserAdminResponse> users = adminService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/search")
    public ResponseEntity<Page<UserAdminResponse>> searchUsers(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<UserAdminResponse> users = adminService.searchUsers(q, pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserAdminResponse> getUserById(@PathVariable Long id) {
        UserAdminResponse user = adminService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody com.econsulat.dto.UserRequest userRequest) {
        User user = userService.createUser(userRequest);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable Long id,
            @RequestBody com.econsulat.dto.UserRequest userRequest) {

        User user = userService.updateUser(id, userRequest);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    // Statistiques
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalDemandes", adminService.getTotalDemandes());
        stats.put("pendingDemandes", adminService.getDemandesByStatus("PENDING"));
        stats.put("approvedDemandes", adminService.getDemandesByStatus("APPROVED"));
        stats.put("rejectedDemandes", adminService.getDemandesByStatus("REJECTED"));
        stats.put("completedDemandes", adminService.getDemandesByStatus("COMPLETED"));
        stats.put("totalUsers", adminService.getTotalUsers());
        stats.put("totalGeneratedDocuments", adminService.getTotalGeneratedDocuments());

        return ResponseEntity.ok(stats);
    }
}