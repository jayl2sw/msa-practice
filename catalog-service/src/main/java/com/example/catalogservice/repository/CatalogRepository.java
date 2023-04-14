package com.example.catalogservice.repository;

import com.example.catalogservice.entity.Catalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CatalogRepository  extends JpaRepository<Catalog, Long> {

    Catalog findByProductId(String productId);
}
