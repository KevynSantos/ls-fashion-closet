package com.lsfashioncloset.repository;

import com.lsfashioncloset.model.StoreOrder;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreOrderRepository extends JpaRepository<StoreOrder, Long> {
    List<StoreOrder> findAllByOrderByCreatedAtDesc();
}
