package com.ngleanhvu.dsa_training_system.repo;

import com.ngleanhvu.dsa_training_system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.userDetails WHERE u.email = :email")
    Optional<User> findUserDetailsByEmail(@Param("email") String email);


    @Query(value = """
    SELECT MONTH(u.created_at), COUNT(*)
    FROM users u
    WHERE YEAR(u.created_at) = :year
    GROUP BY MONTH(u.created_at)
""", nativeQuery = true)
    List<Object[]> getUserByEachYear(@Param("year") int year);
}
