package com.reactive.rm.service;

import com.reactive.rm.dto.EmployeeDTO;
import com.reactive.rm.entity.Employee;
import com.reactive.rm.mapper.EmployeeMapper;
import com.reactive.rm.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log4j2
@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final R2dbcEntityTemplate template;
    private final DatabaseClient databaseClient;
    private final EmployeeMapper employeeMapper;
    private final TransactionalOperator transactionalOperator;

    /**
     * if the Repository extends ReactiveRepository then it will return the Mono
     */
    public Mono<EmployeeDTO> saveEmployeeByRepo(EmployeeDTO employee) {
        Employee entity = employeeMapper.toEntity(employee);
        return employeeRepository.save(entity).map(employeeMapper::toDto);
    }

    /**
     * R2DBC provide R2dbcEntityTemplate which have Insert, Update, Get and Delete methods
     * here we can pass directly the entity
     */
    public Mono<EmployeeDTO> saveEmployee(EmployeeDTO employee) {
        //Employee entity = Employee.builder().name(employee.getName()).email(employee.getEmail()).build();
        Employee entity = employeeMapper.toEntity(employee);
        return template.insert(Employee.class).using(entity).map(employeeMapper::toDto);
    }

    /**
     * To roll back the complete transaction if any exception occurred, need to use TransactionalOperator
     */
    public Mono<EmployeeDTO> saveEmployeeRollBack(EmployeeDTO employee) {
        Employee entity = employeeMapper.toEntity(employee);
        return transactionalOperator.transactional(
                template.insert(Employee.class).using(entity).map(employeeMapper::toDto)
        ).doOnError(e -> log.error("Exception occurred while saving employee", e));
    }

    public Flux<EmployeeDTO> findAllEmployees() {
        return template.select(Employee.class).all().map(employeeMapper::toDto);
    }

    public Flux<EmployeeDTO> findEmployeesByDepartment(String department) {
        return template.select(Employee.class)
                .matching(Query.query(Criteria.where("department").is(department)))
                .all().map(employeeMapper::toDto);
    }

    /**
     * Can directly execute the query by using R2dbcEntityTemplate.getDatabaseClient() or can define the DatabaseClient reference directly
     * if query has the parameters then need to use bind then directly use .bind("columnName". value)
     */
    public Flux<EmployeeDTO> findByDepartment(String department) {
        //String query = "SELECT name, email FROM employee WHERE department = :department";
        String query = String.format("SELECT name, email FROM employee WHERE department = '%s'", department);

        return template.getDatabaseClient()
                .sql(query)
                //.bind("department", department)
                .map((row, metadata) -> EmployeeDTO.builder().name(row.get("name", String.class)).email(row.get("email", String.class)).build())
                .all();
    }
}
