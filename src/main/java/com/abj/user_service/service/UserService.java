package com.abj.user_service.service;

import com.abj.user_service.VO.Department;
import com.abj.user_service.VO.ResponseTemplateVO;
import com.abj.user_service.entity.User;
import com.abj.user_service.repository.UserRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentFeignClient departmentFeignClient;

    public User saveUser(User user) {
        log.info("inside saveUser method of UserService");
        return userRepository.save(user);
    }

    @CircuitBreaker(
            name = "departmentService",
            fallbackMethod = "departmentFallback"
    )
    public ResponseTemplateVO getUserWitDepartment(Long userId) {
        ResponseTemplateVO responseTemplateVO = new ResponseTemplateVO();
        User user = userRepository.findByUserId(userId);
        Department department =  departmentFeignClient.getDepartment(user.getDepartmentId());
        responseTemplateVO.setUser(user);
        responseTemplateVO.setDepartment(department);
        return  responseTemplateVO;
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
