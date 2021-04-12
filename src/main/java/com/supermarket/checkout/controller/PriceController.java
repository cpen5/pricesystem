package com.supermarket.checkout.controller;

import com.supermarket.checkout.domain.Item;
import com.supermarket.checkout.domain.Price;
import com.supermarket.checkout.domain.Rule;
import com.supermarket.checkout.service.PriceService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class PriceController {
    @Resource
    PriceService priceService;

    @GetMapping("/prices")
    private List<Price> getAllPrices() {
        return priceService.getAllPrices();
    }

    @GetMapping("/prices/{id}")
    private Price getPrice(@PathVariable("id") int id) {
        return priceService.getPriceById(id);
    }

    @DeleteMapping("/prices/{id}")
    private void deletePrice(@PathVariable("id") int id) {
        priceService.delete(id);
    }

    @PostMapping("/prices")
    private int savePrice(@RequestBody Price price) {
        priceService.saveOrUpdate(price);
        return price.getId();
    }

    /**
     * rule set to update special price
     * @param rules
     */
    @PostMapping("/prices/checkout")
    private void checkout(@RequestBody List<Rule> rules) {
      if(!rules.isEmpty()) {
          for (Rule rule: rules) {
              Price item = priceService.getPriceByItem(rule.getItemName());
              Price newPrice = new Price();
              BeanUtils.copyProperties(item, newPrice);
              //reset the special price
              if (rule.getRule().split(" ").length != 3) {
                  throw new IllegalArgumentException("not a valid rule");
              }
              newPrice.setSpecialPrice(rule.getRule());
              priceService.saveOrUpdate(newPrice);
          }
      }
    }



    /**
     * each item name separate by a space
     * assume scan all items then calculate the total
     * @param items
     * @return
     */
    @PostMapping("/prices/total")
    public BigDecimal calculatePrice(@RequestBody String items) {
        BigDecimal total = new BigDecimal(0);

        String[] itemA;
        if(!items.isEmpty()) {
            itemA = items.split(" ");
        } else {
            return total;
        }
        //store individual item
        ArrayList<Item> itemList = new ArrayList<>();
        //store item and its quantity
        Map<String, Long> itemTotal = Arrays.stream(itemA)
                .collect(Collectors.groupingBy(s -> s, Collectors.counting()));


        List<String> calculated = new LinkedList<>();

        for(String item: itemTotal.keySet()) {
            int quantity = itemTotal.get(item).intValue();
            int remain = 0;
            Price price = priceService.getPriceByItem(item);
            BigDecimal specialPrice = new BigDecimal(0);
            int specialQuantity = 0;
            if (!price.getSpecialPrice().isEmpty() ) {
                specialQuantity = Integer.valueOf(price.getSpecialPrice().split(" ")[0]);
                specialPrice = new BigDecimal(price.getSpecialPrice().split(" ")[2]);
            }
            //case for specialPrice
            if (specialPrice.compareTo(BigDecimal.ZERO) > 0 && quantity >=specialQuantity) {
                remain = quantity % specialQuantity;
                int div = (quantity - remain) / specialQuantity;
                total = total.add(new BigDecimal(div).multiply(specialPrice));
            } else {
                total = total.add(new BigDecimal(quantity).multiply(price.getUnitPrice()));
            }
            //assume remaining item cannot get discount
            if (remain > 0) {
                total = total.add(new BigDecimal(remain).multiply(price.getUnitPrice()));
            }

        }

        return total;

    }
}
