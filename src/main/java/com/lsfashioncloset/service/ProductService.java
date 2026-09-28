package com.lsfashioncloset.service;

import com.lsfashioncloset.model.Product;
import com.lsfashioncloset.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> publicCatalog() {
        return productRepository.findByActiveTrueOrderByCreatedAtDesc();
    }

    public List<Product> adminCatalog() {
        return productRepository.findAll();
    }

    public Product find(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Produto nao encontrado"));
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public Product update(Long id, Product request) {
        Product product = find(id);
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setCategory(request.getCategory());
        product.setSize(request.getSize());
        product.setImageUrl(request.getImageUrl());
        product.setActive(request.isActive());
        return productRepository.save(product);
    }

    public void delete(Long id) {
        productRepository.delete(find(id));
    }
}
