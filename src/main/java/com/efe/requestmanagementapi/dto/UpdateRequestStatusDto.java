package com.efe.requestmanagementapi.dto;

import com.efe.requestmanagementapi.enums.RequestStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRequestStatusDto {

    @NotNull(message = "Status is required")
    private RequestStatus status;
}