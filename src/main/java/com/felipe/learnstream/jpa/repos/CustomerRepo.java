package com.felipe.learnstream.jpa.repos;

import com.felipe.learnstream.jpa.models.Customer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepo extends CrudRepository<Customer, Long> {
    List<Customer> findAll();
}
