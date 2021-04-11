package com.supermarket.checkout.controller;

import com.supermarket.checkout.domain.Item;
import com.supermarket.checkout.domain.Price;
import com.supermarket.checkout.domain.Rule;
import com.supermarket.checkout.service.PriceService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

@RestController
public class PriceController {
    @Resource
    com.supermarket.checkout.service.PriceService priceService;

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
              newPrice.setSpecialPrice(rule.getRule());

              priceService.saveOrUpdate(newPrice);

          }
      }
    }



    /**
     * each item name separate by a space
     * @param items
     * @return
     */
    @PostMapping("/prices/total")
    public BigDecimal total(String items) {
        BigDecimal total = new BigDecimal(0);

        String[] itemA;
        if(!items.isEmpty()) {
            itemA = items.split(" ");
        } else {
            return total;
        }

        ArrayList<Item> itemList = new ArrayList<>();
        Map<String, Integer> itemTotal = new HashMap<>();
        for (String item: itemA) {

            Price price = priceService.getPriceByItem(item);

            Item item1 = new Item();
            item1.setName(price.getItem());
            item1.setUnitPrice(price.getUnitPrice());
            Map<Integer, BigDecimal> map = new HashMap<>();
            if (!price.getSpecialPrice().isEmpty()) {
                String[] special = price.getSpecialPrice().split(" ");
                if (special.length != 3) {
                    throw new IllegalArgumentException("not a valid special price");
                }
                // map quantity to price
                map.put(Integer.valueOf(special[0]), new BigDecimal(special[2]));
                item1.setSpecialQuantity(Integer.valueOf(special[0]));
                item1.setSpecialPrice(new BigDecimal(special[2]));
            }

            itemList.add(item1);

            itemTotal.put(item1.getName(), itemTotal.getOrDefault(item1.getName(), 0) + 1);
        }

        // calculate total
        List<String> calculated = new LinkedList<>();
        for(Item item: itemList) {
            if(!calculated.contains(item.getName())) {
                calculated.add(item.getName());
                int remain = 0;
                Integer quantity = itemTotal.get(item.getName());
                if (item.getSpecialQuantity() != 0 && quantity >= item.getSpecialQuantity()) {
                    remain = quantity % item.getSpecialQuantity();
                    int div = (quantity - remain) / item.getSpecialQuantity();
                    total = total.add(new BigDecimal(div).multiply(item.getSpecialPrice()));
                } else {
                    total = total.add(new BigDecimal(quantity).multiply(item.getUnitPrice()));
                }
                if (remain > 0) {
                    total = total.add(new BigDecimal(remain).multiply(item.getUnitPrice()));
                }
            }
        }

        return total;



    }
}
