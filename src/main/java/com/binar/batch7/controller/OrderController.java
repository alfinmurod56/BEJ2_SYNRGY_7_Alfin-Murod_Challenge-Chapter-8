package com.binar.batch7.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import com.binar.batch7.dto.base.BaseResponse;
import com.binar.batch7.dto.OrderDetailRequest;
import com.binar.batch7.dto.OrderRequest;
import com.binar.batch7.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@Tag(name = "Order")
@RestController
@RequestMapping("v1/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody OrderRequest request, Principal principal) {
        return ResponseEntity.ok(BaseResponse.success(orderService.create(request, principal), "Success Create Order"));
    }

    @GetMapping
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(BaseResponse.success(orderService.findAll(), "Success Get All Orders"));
    }

    @PutMapping(path = "{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody OrderRequest request) {
        return ResponseEntity.ok(BaseResponse.success(orderService.update(id, request), "Success Update Order"));
    }

    @DeleteMapping(path = "{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        return ResponseEntity.ok(BaseResponse.success(orderService.delete(id), "Success Delete Order"));
    }

    @GetMapping(path = "details")
    public ResponseEntity<?> findAllDetails(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(BaseResponse.success(orderService.findAllDetails(PageRequest.of(page, size)).getContent(), "Success Get All Order Details"));
    }

    @PostMapping(path = "details")
    public ResponseEntity<?> createDetail(@RequestBody OrderDetailRequest request) {
        return ResponseEntity.ok(BaseResponse.success(orderService.createDetail(request), "Success create order detail"));
    }
}
