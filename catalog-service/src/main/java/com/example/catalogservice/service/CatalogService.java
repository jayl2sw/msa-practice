package com.example.catalogservice.service;

import com.example.catalogservice.dto.CatalogResponseDto;
import com.example.catalogservice.repository.CatalogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CatalogService {

    private final CatalogRepository catalogRepository;

    public List<CatalogResponseDto> getAllCatalogs() {
        return catalogRepository.findAll().stream().map(catalog -> catalog.toResponseDto()).collect(Collectors.toList());

    }
}
