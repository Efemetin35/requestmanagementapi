package com.efe.requestmanagementapi.repository;

import com.efe.requestmanagementapi.entity.RequestEntity;
import com.efe.requestmanagementapi.enums.RequestPriority;
import com.efe.requestmanagementapi.enums.RequestStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public final class RequestSpecifications {

    private RequestSpecifications() {
    }

    public static Specification<RequestEntity> hasStatus(RequestStatus status) {
        return (root, query, criteriaBuilder) -> status == null
                ? criteriaBuilder.conjunction()
                : criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<RequestEntity> hasPriority(RequestPriority priority) {
        return (root, query, criteriaBuilder) -> priority == null
                ? criteriaBuilder.conjunction()
                : criteriaBuilder.equal(root.get("priority"), priority);
    }

    public static Specification<RequestEntity> hasRequesterName(String requesterName) {
        return (root, query, criteriaBuilder) -> !StringUtils.hasText(requesterName)
                ? criteriaBuilder.conjunction()
                : criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("requesterName")),
                        "%" + requesterName.toLowerCase() + "%"
                );
    }
}