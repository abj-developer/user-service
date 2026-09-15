package com.abj.user_service.service;

import com.abj.user_service.VO.Department;
import com.abj.user_service.dto.ResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "DEPARTMENT-SERVICE")
public interface DepartmentFeignClient {

    @GetMapping("/departments/{id}")
    ResponseDTO<Department> getDepartment(@PathVariable Long id);
}
