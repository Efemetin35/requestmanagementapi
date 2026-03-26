package com.efe.requestmanagementapi.controller;

import com.efe.requestmanagementapi.dto.CreateRequestDto;
import com.efe.requestmanagementapi.dto.RequestResponseDto;
import com.efe.requestmanagementapi.dto.UpdateRequestStatusDto;
import com.efe.requestmanagementapi.enums.RequestPriority;
import com.efe.requestmanagementapi.enums.RequestStatus;
import com.efe.requestmanagementapi.service.RequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
@Tag(name = "Requests", description = "Request management API")
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    @Operation(summary = "Create a new request")
    public ResponseEntity<RequestResponseDto> createRequest(@Valid @RequestBody CreateRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(requestService.createRequest(requestDto));
    }

    @GetMapping
    @Operation(summary = "List requests with optional filters")
    public ResponseEntity<List<RequestResponseDto>> getRequests(
            @RequestParam(required = false) RequestStatus status,
            @RequestParam(required = false) RequestPriority priority,
            @RequestParam(required = false) String requesterName) {
        return ResponseEntity.ok(requestService.getRequests(status, priority, requesterName));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a request by id")
    public ResponseEntity<RequestResponseDto> getRequestById(@PathVariable Long id) {
        return ResponseEntity.ok(requestService.getRequestById(id));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update request status")
    public ResponseEntity<RequestResponseDto> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRequestStatusDto statusDto) {
        return ResponseEntity.ok(requestService.updateRequestStatus(id, statusDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a request by id")
    public ResponseEntity<Void> deleteRequest(@PathVariable Long id) {
        requestService.deleteRequest(id);
        return ResponseEntity.noContent().build();
    }
}