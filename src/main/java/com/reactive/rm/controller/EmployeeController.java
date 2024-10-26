package com.reactive.rm.controller;

import com.reactive.rm.dto.EmployeeDTO;
import com.reactive.rm.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<EmployeeDTO> createEmployee(@RequestBody EmployeeDTO employee) {
        return employeeService.saveEmployee(employee);
    }

    @PostMapping("/jpa")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<EmployeeDTO> create(@RequestBody EmployeeDTO employee) {
        return employeeService.saveEmployeeByRepo(employee);
    }

    @GetMapping
    public Flux<EmployeeDTO> getAllEmployees() {
        return employeeService.findAllEmployees();
    }

    @GetMapping("/department/{department}")
    public Flux<EmployeeDTO> getEmployeesByDepartment(@PathVariable String department) {
        return employeeService.findEmployeesByDepartment(department);
    }

    @GetMapping("/department/{department}/basic")
    public Flux<EmployeeDTO> getByDepartment(@PathVariable String department) {
        return employeeService.findByDepartment(department);
    }
}
