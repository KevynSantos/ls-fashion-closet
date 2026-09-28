package com.lsfashioncloset.config;

import com.lsfashioncloset.model.Product;
import com.lsfashioncloset.repository.ProductRepository;
import java.math.BigDecimal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner seedProducts(ProductRepository productRepository) {
        return args -> {
            if (productRepository.count() > 0) {
                return;
            }

            productRepository.save(product(
                "Vestido Rose Glam",
                "Peca leve com acabamento delicado para eventos e encontros especiais.",
                "Vestidos",
                "P/M/G",
                new BigDecimal("159.90"),
                8
            ));
            productRepository.save(product(
                "Blusa Cetim Perola",
                "Toque acetinado, caimento elegante e brilho sutil para compor looks versateis.",
                "Blusas",
                "P/M/G",
                new BigDecimal("89.90"),
                12
            ));
            productRepository.save(product(
                "Conjunto Pink Soft",
                "Conjunto confortavel com visual feminino e moderno para o dia a dia.",
                "Conjuntos",
                "P/M/G",
                new BigDecimal("189.90"),
                5
            ));
        };
    }

    private Product product(
        String name,
        String description,
        String category,
        String size,
        BigDecimal price,
        int stock
    ) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setCategory(category);
        product.setSize(size);
        product.setPrice(price);
        product.setStock(stock);
        product.setImageUrl("/placeholder.svg");
        product.setActive(true);
        return product;
    }
}
