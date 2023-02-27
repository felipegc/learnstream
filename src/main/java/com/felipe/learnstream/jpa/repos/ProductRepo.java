package com.felipe.learnstream.jpa.repos;

import com.felipe.learnstream.jpa.models.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepo extends CrudRepository<Product, Long> {
    List<Product> findAll();
}
