package com.efe.requestmanagementapi.service.impl;

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
import com.efe.requestmanagementapi.repository.RequestSpecifications;
import com.efe.requestmanagementapi.service.RequestService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;

    @Override
    @Transactional
    public RequestResponseDto createRequest(CreateRequestDto requestDto) {
        RequestEntity entity = requestMapper.toEntity(requestDto);
        entity.setStatus(RequestStatus.OPEN);
        return requestMapper.toResponse(requestRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestResponseDto> getRequests(RequestStatus status, RequestPriority priority, String requesterName) {
        Specification<RequestEntity> specification = RequestSpecifications.hasStatus(status)
            .and(RequestSpecifications.hasPriority(priority))
            .and(RequestSpecifications.hasRequesterName(requesterName));

        return requestRepository.findAll(specification).stream()
                .map(requestMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RequestResponseDto getRequestById(Long id) {
        return requestMapper.toResponse(findRequestById(id));
    }

    @Override
    @Transactional
    public RequestResponseDto updateRequestStatus(Long id, UpdateRequestStatusDto statusDto) {
        RequestEntity entity = findRequestById(id);
        validateStatusTransition(entity.getStatus(), statusDto.getStatus());
        entity.setStatus(statusDto.getStatus());
        return requestMapper.toResponse(requestRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteRequest(Long id) {
        RequestEntity entity = findRequestById(id);
        requestRepository.delete(entity);
    }

    private RequestEntity findRequestById(Long id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found with id: " + id));
    }

    private void validateStatusTransition(RequestStatus currentStatus, RequestStatus newStatus) {
        if (currentStatus == RequestStatus.COMPLETED && newStatus == RequestStatus.OPEN) {
            throw new BusinessRuleViolationException("A completed request cannot be moved back to Open");
        }

        if (currentStatus == RequestStatus.CANCELLED && newStatus == RequestStatus.COMPLETED) {
            throw new BusinessRuleViolationException("A cancelled request cannot be moved to Completed");
        }
    }
}