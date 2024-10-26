package com.reactive.rm.mapper;

import com.reactive.rm.dto.EmployeeDTO;
import com.reactive.rm.entity.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {
    public EmployeeDTO toDto(Employee entity) {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDepartment(entity.getDepartment());
        dto.setEmail(entity.getEmail());

        return dto;
    }

    public Employee toEntity(EmployeeDTO dto) {
        Employee entity = new Employee();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setDepartment(dto.getDepartment());
        entity.setEmail(dto.getEmail());

        return entity;
    }
}
