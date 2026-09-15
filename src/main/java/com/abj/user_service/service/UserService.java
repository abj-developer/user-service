package com.abj.user_service.service;

import com.abj.user_service.VO.Department;
import com.abj.user_service.VO.ResponseTemplateVO;
import com.abj.user_service.dto.ResponseDTO;
import com.abj.user_service.dto.UserRequestDTO;
import com.abj.user_service.dto.UserResponseDTO;
import com.abj.user_service.dto.UserUpdateRequestDTO;
import com.abj.user_service.entity.User;
import com.abj.user_service.event.UserCreatedEvent;
import com.abj.user_service.event.UserEventProducer;
import com.abj.user_service.exception.DuplicateEmailException;
import com.abj.user_service.exception.UserNotFoundException;
import com.abj.user_service.repository.UserRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    private DepartmentFeignClient departmentFeignClient;

    private final UserEventProducer userEventProducer;

    public UserService(
            UserRepository userRepository,
            UserEventProducer userEventProducer) {

        this.userRepository = userRepository;
        this.userEventProducer = userEventProducer;
    }

    public UserResponseDTO saveUser(UserRequestDTO request) {
        log.info("inside saveUser method of UserService");
        String email = request.getEmail() == null ? null : request.getEmail().trim();
        if (email != null && userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException(email);
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(email);
        user.setDepartmentId(request.getDepartmentId());

        User savedUser = userRepository.save(user);
        UserCreatedEvent event = new UserCreatedEvent(
                savedUser.getUserId(),
                savedUser.getFirstName() + " " + savedUser.getLastName(),
                savedUser.getEmail()
        );

        userEventProducer.publishUserCreated(event);
        return mapToUserResponse(savedUser);
    }

    @CircuitBreaker(
            name = "departmentService",
            fallbackMethod = "departmentFallback"
    )
    public ResponseTemplateVO getUserWitDepartment(Long userId) {
        ResponseTemplateVO responseTemplateVO = new ResponseTemplateVO();
        User user = userRepository.findByUserId(userId);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + userId);
        }

        ResponseDTO<Department> departmentResponse =
                departmentFeignClient.getDepartment(user.getDepartmentId());

        Department department = departmentResponse != null && departmentResponse.getResult() != null
                ? departmentResponse.getResult()
                : null;

        responseTemplateVO.setUser(user);
        responseTemplateVO.setDepartment(department);
        return responseTemplateVO;
    }

    public List<UserResponseDTO> getAllUsers() {
        log.info("inside getAllUsers method of UserService");
        return userRepository.findAll().stream()
                .map(this::mapToUserResponse)
                .toList();
    }

    public UserResponseDTO getUserById(Long userId) {
        log.info("inside getUserById method of UserService for userId={}", userId);
        User user = userRepository.findByUserId(userId);
        if (user == null) {
            throw new UserNotFoundException(userId);
        }
        return mapToUserResponse(user);
    }

    public List<UserResponseDTO> getUsersByDepartment(Long departmentId) {
        log.info("inside getUsersByDepartment method of UserService for departmentId={}", departmentId);
        return userRepository.findByDepartmentId(departmentId).stream()
                .map(this::mapToUserResponse)
                .toList();
    }

    public UserResponseDTO updateUser(Long userId, UserUpdateRequestDTO request) {
        log.info("inside updateUser method of UserService for userId={}", userId);
        User user = userRepository.findByUserId(userId);
        if (user == null) {
            throw new UserNotFoundException(userId);
        }

        if (request.getName() != null && !request.getName().isBlank()) {
            String[] nameParts = request.getName().trim().split("\\s+");
            user.setFirstName(nameParts[0]);
            user.setLastName(nameParts.length > 1 ? String.join(" ", java.util.Arrays.copyOfRange(nameParts, 1, nameParts.length)) : "");
        }

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            user.setEmail(request.getEmail());
        }

        if (request.getDepartmentId() != null) {
            user.setDepartmentId(request.getDepartmentId());
        }

        User updatedUser = userRepository.save(user);
        return mapToUserResponse(updatedUser);
    }

    public void deleteUser(Long userId) {
        log.info("inside deleteUser method of UserService for userId={}", userId);
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
        userRepository.deleteById(userId);
    }

    private UserResponseDTO mapToUserResponse(User user) {
        String fullName = user.getLastName() == null || user.getLastName().isBlank()
                ? user.getFirstName()
                : user.getFirstName() + " " + user.getLastName();

        return new UserResponseDTO(
                user.getUserId(),
                fullName,
                user.getEmail(),
                user.getDepartmentId()
        );
    }

    public ResponseTemplateVO departmentFallback(Long userId, Exception ex) {
        log.info("Fallback executed because: {}", ex.getMessage());
        User user = userRepository.findByUserId(userId);
        Department department = new Department();
        department.setDepartmentId(0L);
        department.setDepartmentName("Department Service Unavailable");
        department.setDepartmentAddress("N/A");
        department.setDepartmentCode("N/A");
        return new ResponseTemplateVO(user, department);
    }
}
