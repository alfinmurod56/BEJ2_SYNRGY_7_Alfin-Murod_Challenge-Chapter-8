package com.binar.batch7.serviceimpl;

import lombok.extern.slf4j.Slf4j;
import com.binar.batch7.dto.MerchantRequest;
import com.binar.batch7.dto.MerchantResponse;
import com.binar.batch7.entity.Merchant;
import com.binar.batch7.mapper.MerchantMapper;
import com.binar.batch7.repository.MerchantRepository;
import com.binar.batch7.service.MerchantService;
import com.binar.batch7.service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class MerchantServiceImpl implements MerchantService {

    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    private ValidationService validationService;

    @Autowired
    private MerchantMapper merchantMapper;

    @Override
    public MerchantResponse create(MerchantRequest request) {
        validationService.validate(request);
        Merchant merchant = new Merchant();
        return getMerchantResponse(request, merchant);
    }

    @Override
    public List<MerchantResponse> findAll(Boolean isOpen, Pageable pageable, String name, String location) {
        Specification<Merchant> spec = ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (name != null && !name.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }
            if (location != null && !location.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("location")), "%" + location.toLowerCase() + "%"));
            }
            if (isOpen != null) {
                predicates.add(criteriaBuilder.equal(root.get("isOpen"), isOpen));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
        List<MerchantResponse> response = new ArrayList<MerchantResponse>();
        merchantRepository.findAll(spec, pageable).forEach(merchant -> response.add(merchantMapper.toMerchantResponse(merchant)));
        return response;
    }

    @Override
    @Transactional
    public MerchantResponse update(UUID id, MerchantRequest request) {
        validationService.validate(request);
        Merchant merchant = merchantRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID Merchant not found"));
        return getMerchantResponse(request, merchant);
    }

    @Override
    @Transactional
    public MerchantResponse delete(UUID id) {
        Merchant merchant = merchantRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ID Merchant not found"));
        merchantRepository.delete(merchant);
        return merchantMapper.toMerchantResponse(merchant);
    }

    @Override
    public MerchantResponse findById(UUID id) {
        Merchant merchant = merchantRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ID Merchant not found"));
        return merchantMapper.toMerchantResponse(merchant);
    }

    private MerchantResponse getMerchantResponse(MerchantRequest request, Merchant merchant) {
        merchant.setName(request.getName());
        merchant.setLocation(request.getLocation());
        merchant.setIsOpen(request.getIsOpen());
        merchantRepository.save(merchant);
        return merchantMapper.toMerchantResponse(merchant);
    }

}
