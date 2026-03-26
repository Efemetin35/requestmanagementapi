package com.efe.requestmanagementapi.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.efe.requestmanagementapi.dto.CreateRequestDto;
import com.efe.requestmanagementapi.dto.RequestResponseDto;
import com.efe.requestmanagementapi.dto.UpdateRequestStatusDto;
import com.efe.requestmanagementapi.enums.RequestPriority;
import com.efe.requestmanagementapi.enums.RequestStatus;
import com.efe.requestmanagementapi.exception.GlobalExceptionHandler;
import com.efe.requestmanagementapi.exception.ResourceNotFoundException;
import com.efe.requestmanagementapi.service.RequestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@WebMvcTest(RequestController.class)
@Import(GlobalExceptionHandler.class)
class RequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

        @MockitoBean
    private RequestService requestService;

    @Test
    void createRequestShouldReturnCreatedResponse() throws Exception {
        CreateRequestDto requestDto = CreateRequestDto.builder()
                .title("Email issue")
                .description("Mailbox is full")
                .requesterName("Efe")
                .priority(RequestPriority.HIGH)
                .build();

        RequestResponseDto responseDto = buildResponseDto(1L, RequestStatus.OPEN);

        when(requestService.createRequest(any(CreateRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("Open"));
    }

    @Test
    void getRequestsShouldSupportOptionalFilters() throws Exception {
        RequestResponseDto responseDto = RequestResponseDto.builder()
                .id(2L)
                .title("Sample request")
                .description("Sample description")
                .requesterName("Efe")
                .priority(RequestPriority.MEDIUM)
                .status(RequestStatus.IN_PROGRESS)
                .createdAt(LocalDateTime.of(2026, 3, 24, 12, 0))
                .updatedAt(LocalDateTime.of(2026, 3, 24, 12, 30))
                .build();

        when(requestService.getRequests(RequestStatus.IN_PROGRESS, RequestPriority.MEDIUM, "Efe"))
                .thenReturn(List.of(responseDto));

        mockMvc.perform(get("/api/requests")
                        .param("status", "InProgress")
                        .param("priority", "Medium")
                        .param("requesterName", "Efe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].priority").value("Medium"))
                .andExpect(jsonPath("$[0].status").value("InProgress"));

        verify(requestService).getRequests(eq(RequestStatus.IN_PROGRESS), eq(RequestPriority.MEDIUM), eq("Efe"));
    }

    @Test
    void getRequestByIdShouldReturnRequest() throws Exception {
        RequestResponseDto responseDto = buildResponseDto(7L, RequestStatus.OPEN);

        when(requestService.getRequestById(7L)).thenReturn(responseDto);

        mockMvc.perform(get("/api/requests/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.status").value("Open"));
    }

    @Test
    void getRequestByIdShouldReturnNotFound() throws Exception {
        when(requestService.getRequestById(99L))
                .thenThrow(new ResourceNotFoundException("Request not found with id: 99"));

        mockMvc.perform(get("/api/requests/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Request not found with id: 99"));
    }

    @Test
    void updateStatusShouldAcceptRequestBody() throws Exception {
        UpdateRequestStatusDto statusDto = UpdateRequestStatusDto.builder()
                .status(RequestStatus.COMPLETED)
                .build();

        RequestResponseDto responseDto = buildResponseDto(3L, RequestStatus.COMPLETED);
        when(requestService.updateRequestStatus(any(Long.class), any(UpdateRequestStatusDto.class))).thenReturn(responseDto);

        mockMvc.perform(put("/api/requests/3/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Completed"));
    }

    @Test
    void deleteRequestShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/requests/4"))
                .andExpect(status().isNoContent());

        verify(requestService).deleteRequest(4L);
    }

    @Test
    void deleteRequestShouldReturnNotFoundWhenRequestDoesNotExist() throws Exception {
        org.mockito.Mockito.doThrow(new ResourceNotFoundException("Request not found with id: 12"))
                .when(requestService)
                .deleteRequest(12L);

        mockMvc.perform(delete("/api/requests/12"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Request not found with id: 12"));
    }

    @Test
    void createRequestShouldReturnValidationErrorWhenTitleIsBlank() throws Exception {
        CreateRequestDto requestDto = CreateRequestDto.builder()
                .title(" ")
                .description("Valid description")
                .requesterName("Efe")
                .priority(RequestPriority.LOW)
                .build();

        mockMvc.perform(post("/api/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.validationErrors.title").value("Title must not be blank"));
    }

    @Test
    void createRequestShouldReturnValidationErrorWhenDescriptionIsTooLong() throws Exception {
        CreateRequestDto requestDto = CreateRequestDto.builder()
                .title("Printer issue")
                .description("x".repeat(501))
                .requesterName("Efe")
                .priority(RequestPriority.LOW)
                .build();

        mockMvc.perform(post("/api/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.validationErrors.description").value("Description must be at most 500 characters"));
    }

    @Test
    void createRequestShouldReturnValidationErrorWhenPriorityIsMissing() throws Exception {
        mockMvc.perform(post("/api/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"title\":\"Printer issue\"," +
                                "\"description\":\"Paper jam\"," +
                                "\"requesterName\":\"Efe\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.validationErrors.priority").value("Priority is required"));
    }

    @Test
    void updateStatusShouldReturnValidationErrorWhenStatusIsNull() throws Exception {
        mockMvc.perform(put("/api/requests/3/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":null}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.validationErrors.status").value("Status is required"));
    }

    @Test
    void getRequestsShouldReturnBadRequestForInvalidStatusFilter() throws Exception {
        mockMvc.perform(get("/api/requests")
                        .param("status", "Started"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid value for 'status'. Allowed values: Open, InProgress, Completed, Cancelled"));
    }

    @Test
    void getRequestsShouldReturnBadRequestForInvalidPriorityFilter() throws Exception {
        mockMvc.perform(get("/api/requests")
                        .param("priority", "Urgent"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid value for 'priority'. Allowed values: Low, Medium, High"));
    }

    @Test
    void updateStatusShouldReturnBadRequestForInvalidEnumValue() throws Exception {
        mockMvc.perform(put("/api/requests/3/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"Done\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid value for 'status'. Allowed values: Open, InProgress, Completed, Cancelled"));
    }

    private RequestResponseDto buildResponseDto(Long id, RequestStatus status) {
        return RequestResponseDto.builder()
                .id(id)
                .title("Sample request")
                .description("Sample description")
                .requesterName("Efe")
                .priority(RequestPriority.HIGH)
                .status(status)
                .createdAt(LocalDateTime.of(2026, 3, 24, 12, 0))
                .updatedAt(LocalDateTime.of(2026, 3, 24, 12, 30))
                .build();
    }
}