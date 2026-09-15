package com.abj.user_service.repository;

import com.abj.user_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUserId(Long userId);

    List<User> findByDepartmentId(Long departmentId);

    boolean existsByEmail(String email);
}
