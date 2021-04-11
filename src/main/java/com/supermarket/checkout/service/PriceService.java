package com.supermarket.checkout.service;

import com.supermarket.checkout.db.PriceRepository;
import com.supermarket.checkout.domain.Price;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class PriceService {
    @Resource
    private PriceRepository priceRepository;

    public List<Price> getAllPrices() {
        List<Price> prices = new ArrayList<Price>();
        priceRepository.findAll().forEach(price -> prices.add(price));
        return prices;
    }

    public Price getPriceById(int id) {
        return priceRepository.findById(id).get();
    }

    public void saveOrUpdate(Price price) {
        priceRepository.save(price);
    }

    public void delete(int id) {
        priceRepository.deleteById(id);
    }
}
