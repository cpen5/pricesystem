package com.supermarket.checkout.controller;

import com.supermarket.checkout.domain.Price;
import com.supermarket.checkout.service.PriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class PriceController {
    @Resource
    com.supermarket.checkout.service.PriceService PriceService;

    @GetMapping("/prices")
    private List<Price> getAllPrices() {
        return PriceService.getAllPrices();
    }

    @GetMapping("/prices/{id}")
    private Price getPrice(@PathVariable("id") int id) {
        return PriceService.getPriceById(id);
    }

    @DeleteMapping("/prices/{id}")
    private void deletePrice(@PathVariable("id") int id) {
        PriceService.delete(id);
    }

    @PostMapping("/prices")
    private int savePrice(@RequestBody Price Price) {
        PriceService.saveOrUpdate(Price);
        return Price.getId();
    }
}
