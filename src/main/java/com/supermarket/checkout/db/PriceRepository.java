package com.supermarket.checkout.db;

import com.supermarket.checkout.domain.Price;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PriceRepository extends CrudRepository<Price, Integer> {
    @Query("from Price where item = ?1")
    Price findByItem(String item);


}
