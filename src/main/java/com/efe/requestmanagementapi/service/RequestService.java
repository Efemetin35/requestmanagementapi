package com.efe.requestmanagementapi.service;

import com.efe.requestmanagementapi.dto.CreateRequestDto;
import com.efe.requestmanagementapi.dto.RequestResponseDto;
import com.efe.requestmanagementapi.dto.UpdateRequestStatusDto;
import com.efe.requestmanagementapi.enums.RequestPriority;
import com.efe.requestmanagementapi.enums.RequestStatus;
import java.util.List;

public interface RequestService {

    RequestResponseDto createRequest(CreateRequestDto requestDto);

    List<RequestResponseDto> getRequests(RequestStatus status, RequestPriority priority, String requesterName);

    RequestResponseDto getRequestById(Long id);

    RequestResponseDto updateRequestStatus(Long id, UpdateRequestStatusDto statusDto);

    void deleteRequest(Long id);
}