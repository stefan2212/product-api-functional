package com.wirebraincoffee.productapifunctional.repository;

import com.wirebraincoffee.productapifunctional.model.Product;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ProductRepository extends ReactiveMongoRepository<Product, String> {

}
