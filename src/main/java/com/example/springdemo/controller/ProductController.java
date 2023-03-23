package com.example.springdemo.controller;

import com.example.springdemo.model.Product;
import com.example.springdemo.model.ResponseObject;
import com.example.springdemo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private ProductRepository repository;
    @GetMapping("")
    List<Product> getAllProducts(){
        return repository.findAll();
    }
    @GetMapping("/{id}")
    ResponseEntity<ResponseObject> foundProduct(@PathVariable Long id){
        Optional<Product> foundProduct = repository.findById(id);
        return  (foundProduct.isPresent()) ?
             ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("OK", "Query product success", foundProduct)
            ) :
             ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("Fail", "Can't find product with id = " + id, "")
            );
    }
    @PostMapping("/insert")
    ResponseEntity<ResponseObject> insertProduct(@RequestBody Product newProduct){
        List<Product> foundProduct = repository.findProductByName(newProduct.getName().trim());
            return  (foundProduct.size() > 0) ?
                    ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                            new ResponseObject("fail", "Product name already exists", "")
            ):

                    ResponseEntity.status(HttpStatus.OK).body(
                            new ResponseObject("OK", "Insert product success", repository.save(newProduct))
        );

    }@PutMapping("/update/{id}")
    ResponseEntity<ResponseObject> updateProduct(@RequestBody Product newProduct, @PathVariable Long id){
        Product updateProduct = repository.findById(id)
                .map(product -> {
                    product.setName(newProduct.getName());
                    product.setPrice(newProduct.getPrice());
                    product.setYear(newProduct.getYear());
                    product.setUrl(newProduct.getUrl());
                    return repository.save(newProduct);
                }).orElseGet(() -> {
                    newProduct.setId(id);
                    return repository.save(newProduct);
                });
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Update success", updateProduct)
        );
    }
    @DeleteMapping("/delete/{id}")
    ResponseEntity<ResponseObject> deleteProduct(@PathVariable Long id){
        boolean exists = repository.existsById(id);
        if (exists){
            repository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("OK", "Delete success", "")
            );
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ResponseObject("Fail", "Can't find product", "")
        );
    }
}
