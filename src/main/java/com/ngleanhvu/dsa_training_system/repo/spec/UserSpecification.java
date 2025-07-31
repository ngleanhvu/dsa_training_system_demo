package com.ngleanhvu.dsa_training_system.repo.spec;

import com.ngleanhvu.dsa_training_system.entity.User;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {
    public static Specification<User> hasKeyword(String keyword) {
        return ((root, query, criteriaBuilder) -> {
            if (keyword == null || keyword.isEmpty()) {
                return null;
            }

            return criteriaBuilder.or(
                    criteriaBuilder.like(root.get("email"), String.format("%%%s%%", keyword)),
                    criteriaBuilder.like(root.get("displayName"), String.format("%%%s%%", keyword))
            );
        });
    }
}
