package com.supermarket.checkout.db;

import com.supermarket.checkout.domain.Price;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PriceRepository extends CrudRepository<Price, Integer> {
}
