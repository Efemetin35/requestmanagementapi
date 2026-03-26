package com.efe.requestmanagementapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.efe.requestmanagementapi.dto.CreateRequestDto;
import com.efe.requestmanagementapi.dto.RequestResponseDto;
import com.efe.requestmanagementapi.dto.UpdateRequestStatusDto;
import com.efe.requestmanagementapi.entity.RequestEntity;
import com.efe.requestmanagementapi.enums.RequestPriority;
import com.efe.requestmanagementapi.enums.RequestStatus;
import com.efe.requestmanagementapi.exception.BusinessRuleViolationException;
import com.efe.requestmanagementapi.exception.ResourceNotFoundException;
import com.efe.requestmanagementapi.mapper.RequestMapper;
import com.efe.requestmanagementapi.repository.RequestRepository;
import com.efe.requestmanagementapi.service.impl.RequestServiceImpl;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private RequestMapper requestMapper;

    @InjectMocks
    private RequestServiceImpl requestService;

    @Test
    void createRequestShouldSetStatusToOpen() {
        CreateRequestDto createRequestDto = CreateRequestDto.builder()
                .title("Printer issue")
                .description("Office printer is not working")
                .requesterName("Efe")
                .priority(RequestPriority.HIGH)
                .build();

        RequestEntity entity = RequestEntity.builder()
                .title(createRequestDto.getTitle())
                .description(createRequestDto.getDescription())
                .requesterName(createRequestDto.getRequesterName())
                .priority(createRequestDto.getPriority())
                .build();

        RequestEntity savedEntity = RequestEntity.builder()
                .id(1L)
                .title(createRequestDto.getTitle())
                .description(createRequestDto.getDescription())
                .requesterName(createRequestDto.getRequesterName())
                .priority(RequestPriority.HIGH)
                .status(RequestStatus.OPEN)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        RequestResponseDto responseDto = RequestResponseDto.builder()
                .id(1L)
                .title(savedEntity.getTitle())
                .description(savedEntity.getDescription())
                .requesterName(savedEntity.getRequesterName())
                .priority(savedEntity.getPriority())
                .status(savedEntity.getStatus())
                .createdAt(savedEntity.getCreatedAt())
                .updatedAt(savedEntity.getUpdatedAt())
                .build();

        when(requestMapper.toEntity(createRequestDto)).thenReturn(entity);
        when(requestRepository.save(entity)).thenReturn(savedEntity);
        when(requestMapper.toResponse(savedEntity)).thenReturn(responseDto);

        RequestResponseDto result = requestService.createRequest(createRequestDto);

        assertThat(entity.getStatus()).isEqualTo(RequestStatus.OPEN);
        assertThat(result.getStatus()).isEqualTo(RequestStatus.OPEN);
    }

        @Test
        void getRequestByIdShouldReturnMappedResponse() {
                RequestEntity entity = RequestEntity.builder()
                                .id(8L)
                                .title("Laptop request")
                                .description("Need a new laptop")
                                .requesterName("Efe")
                                .priority(RequestPriority.HIGH)
                                .status(RequestStatus.OPEN)
                                .build();

                RequestResponseDto responseDto = RequestResponseDto.builder()
                                .id(8L)
                                .title(entity.getTitle())
                                .description(entity.getDescription())
                                .requesterName(entity.getRequesterName())
                                .priority(entity.getPriority())
                                .status(entity.getStatus())
                                .build();

                when(requestRepository.findById(8L)).thenReturn(Optional.of(entity));
                when(requestMapper.toResponse(entity)).thenReturn(responseDto);

                RequestResponseDto result = requestService.getRequestById(8L);

                assertThat(result).isEqualTo(responseDto);
        }

    @Test
    void getRequestByIdShouldThrowWhenRequestDoesNotExist() {
        when(requestRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> requestService.getRequestById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Request not found with id: 99");
    }

    @Test
    void updateRequestStatusShouldRejectCompletedToOpenTransition() {
        RequestEntity entity = RequestEntity.builder()
                .id(1L)
                .title("Access request")
                .description("Need access to reporting")
                .requesterName("Efe")
                .priority(RequestPriority.MEDIUM)
                .status(RequestStatus.COMPLETED)
                .build();

        UpdateRequestStatusDto statusDto = UpdateRequestStatusDto.builder()
                .status(RequestStatus.OPEN)
                .build();

        when(requestRepository.findById(1L)).thenReturn(Optional.of(entity));

        assertThatThrownBy(() -> requestService.updateRequestStatus(1L, statusDto))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessage("A completed request cannot be moved back to Open");
    }

    @Test
    void updateRequestStatusShouldRejectCancelledToCompletedTransition() {
        RequestEntity entity = RequestEntity.builder()
                .id(2L)
                .title("Email access")
                .description("Need mailbox restored")
                .requesterName("Efe")
                .priority(RequestPriority.MEDIUM)
                .status(RequestStatus.CANCELLED)
                .build();

        UpdateRequestStatusDto statusDto = UpdateRequestStatusDto.builder()
                .status(RequestStatus.COMPLETED)
                .build();

        when(requestRepository.findById(2L)).thenReturn(Optional.of(entity));

        assertThatThrownBy(() -> requestService.updateRequestStatus(2L, statusDto))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessage("A cancelled request cannot be moved to Completed");
    }

    @Test
    void updateRequestStatusShouldReturnUpdatedRequest() {
        RequestEntity entity = RequestEntity.builder()
                .id(3L)
                .title("VPN request")
                .description("Need VPN access")
                .requesterName("Efe")
                .priority(RequestPriority.HIGH)
                .status(RequestStatus.OPEN)
                .build();

        UpdateRequestStatusDto statusDto = UpdateRequestStatusDto.builder()
                .status(RequestStatus.IN_PROGRESS)
                .build();

        RequestResponseDto responseDto = RequestResponseDto.builder()
                .id(3L)
                .title(entity.getTitle())
                .description(entity.getDescription())
                .requesterName(entity.getRequesterName())
                .priority(entity.getPriority())
                .status(RequestStatus.IN_PROGRESS)
                .build();

        when(requestRepository.findById(3L)).thenReturn(Optional.of(entity));
        when(requestRepository.save(entity)).thenReturn(entity);
        when(requestMapper.toResponse(entity)).thenReturn(responseDto);

        RequestResponseDto result = requestService.updateRequestStatus(3L, statusDto);

        assertThat(entity.getStatus()).isEqualTo(RequestStatus.IN_PROGRESS);
        assertThat(result).isEqualTo(responseDto);
    }

    @Test
    void getRequestsShouldReturnMappedResponses() {
        RequestEntity entity = RequestEntity.builder()
                .id(5L)
                .title("VPN issue")
                .description("VPN disconnects frequently")
                .requesterName("Ayse")
                .priority(RequestPriority.LOW)
                .status(RequestStatus.OPEN)
                .build();

        RequestResponseDto responseDto = RequestResponseDto.builder()
                .id(5L)
                .title("VPN issue")
                .description("VPN disconnects frequently")
                .requesterName("Ayse")
                .priority(RequestPriority.LOW)
                .status(RequestStatus.OPEN)
                .build();

        when(requestRepository.findAll(org.mockito.ArgumentMatchers.<org.springframework.data.jpa.domain.Specification<RequestEntity>>any()))
                .thenReturn(List.of(entity));
        when(requestMapper.toResponse(entity)).thenReturn(responseDto);

        List<RequestResponseDto> result = requestService.getRequests(RequestStatus.OPEN, RequestPriority.LOW, "Ayse");

        assertThat(result).containsExactly(responseDto);
        verify(requestRepository).findAll(org.mockito.ArgumentMatchers.<org.springframework.data.jpa.domain.Specification<RequestEntity>>any());
    }

    @Test
    void deleteRequestShouldRemoveEntityWhenItExists() {
        RequestEntity entity = RequestEntity.builder()
                .id(11L)
                .title("Keyboard request")
                .description("Need replacement keyboard")
                .requesterName("Efe")
                .priority(RequestPriority.LOW)
                .status(RequestStatus.OPEN)
                .build();

        when(requestRepository.findById(11L)).thenReturn(Optional.of(entity));

        requestService.deleteRequest(11L);

        verify(requestRepository).delete(entity);
    }
}