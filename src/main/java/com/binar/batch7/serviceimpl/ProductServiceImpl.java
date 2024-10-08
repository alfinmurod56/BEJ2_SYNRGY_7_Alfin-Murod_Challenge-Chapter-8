package com.binar.batch7.serviceimpl;

import javax.persistence.criteria.Predicate;

import lombok.extern.slf4j.Slf4j;
import com.binar.batch7.config.Config;
import com.binar.batch7.dto.ProductRequest;
import com.binar.batch7.dto.ProductResponse;
import com.binar.batch7.entity.Merchant;
import com.binar.batch7.entity.Product;
import com.binar.batch7.mapper.ProductMapper;
import com.binar.batch7.repository.MerchantRepository;
import com.binar.batch7.repository.ProductRepository;
import com.binar.batch7.service.ProductService;
import com.binar.batch7.service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    private ValidationService validationService;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private Config config;

    @Override
    public ProductResponse create(ProductRequest request) {
        validationService.validate(request);
        Product product = new Product();
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setName(request.getName());
        Merchant merchant = merchantRepository.findById(config.isValidUUID(request.getMerchantId()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Merchant not found with id " + request.getMerchantId()));
        product.setMerchant(merchant);
        productRepository.save(product);
        return productMapper.toProductResponse(product);
    }

    @Override
    public List<ProductResponse> findAll(Pageable pageable, String name, Double price) {
        Specification<Product> spec = ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (name != null && !name.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }
            if (price != null) {
                predicates.add(criteriaBuilder.equal(root.get("price"), price));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
        List<ProductResponse> response = new ArrayList<ProductResponse>();
        productRepository.findAll(spec, pageable).forEach(
                product -> {
                    log.info("PRODUCT : {}", product);
                    response.add(productMapper.toProductResponse(product));
                }
        );
        return response;
    }

    @Override
    @Transactional
    public ProductResponse update(UUID id, ProductRequest request) {
        validationService.validate(request);
        log.info("REQUEST : {}", request);
        Product product = productRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ID Product not found"));

        if (Objects.nonNull(request.getName())) {
            product.setName(request.getName());
        }

        if (Objects.nonNull(request.getPrice())) {
            product.setPrice(request.getPrice());
        }

        product.setMerchant(product.getMerchant());

        productRepository.save(product);

        return productMapper.toProductResponse(product);
    }

    @Override
    @Transactional
    public ProductResponse delete(UUID id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ID Product not found"));
        productRepository.delete(product);
        return productMapper.toProductResponse(product);
    }

    @Override
    public ProductResponse findById(UUID id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ID Product not found"));
        return productMapper.toProductResponse(product);
    }

    @Override
    public List<ProductResponse> findByPromo(Double priceThreshold) {
        List<ProductResponse> response = new ArrayList<ProductResponse>();
        productRepository.findPromotionalProducts(priceThreshold).forEach(
                product -> {
                    log.info("PRODUCT : {}", product);
                    response.add(productMapper.toProductResponse(product));
                }
        );
        return response;
    }
}
