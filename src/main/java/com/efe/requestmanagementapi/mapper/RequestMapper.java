package com.efe.requestmanagementapi.mapper;

import com.efe.requestmanagementapi.dto.CreateRequestDto;
import com.efe.requestmanagementapi.dto.RequestResponseDto;
import com.efe.requestmanagementapi.entity.RequestEntity;
import org.springframework.stereotype.Component;

@Component
public class RequestMapper {

    public RequestEntity toEntity(CreateRequestDto requestDto) {
        return RequestEntity.builder()
                .title(requestDto.getTitle())
                .description(requestDto.getDescription())
                .requesterName(requestDto.getRequesterName())
                .priority(requestDto.getPriority())
                .build();
    }

    public RequestResponseDto toResponse(RequestEntity entity) {
        return RequestResponseDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .requesterName(entity.getRequesterName())
                .priority(entity.getPriority())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}