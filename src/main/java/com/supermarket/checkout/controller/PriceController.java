package com.supermarket.checkout.controller;

import com.supermarket.checkout.common.exception.BusinessException;
import com.supermarket.checkout.common.exception.BusinessExceptionCode;
import com.supermarket.checkout.common.exception.dto.ResponseDto;
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
    private ResponseDto getAllPrices() {
        ResponseDto responseDto = new ResponseDto();
        responseDto.setContent(priceService.getAllPrices());
        return responseDto;
    }

    @GetMapping("/prices/{id}")
    private ResponseDto getPrice(@PathVariable("id") int id) {
        ResponseDto responseDto = new ResponseDto();
        Price price = priceService.getPriceById(id);
        responseDto.setContent(price);
        return responseDto;
    }

    @DeleteMapping("/prices/{id}")
    private ResponseDto deletePrice(@PathVariable("id") int id) {
        priceService.delete(id);
        return new ResponseDto();
    }

    @PostMapping("/prices")
    private ResponseDto savePrice(@RequestBody Price price) {
        priceService.saveOrUpdate(price);
        ResponseDto responseDto = new ResponseDto();
        responseDto.setContent(price.getId());
        return responseDto;
    }

    /**
     * rule set to update special price
     *
     * @param rules
     */
    @PostMapping("/prices/checkout")
    private ResponseDto checkout(@RequestBody List<Rule> rules) {
        List<Integer> ids = new ArrayList<>();
        if (!rules.isEmpty()) {
            for (Rule rule : rules) {
                Price item = priceService.getPriceByItem(rule.getItemName());
                Price newPrice = new Price();
                BeanUtils.copyProperties(item, newPrice);
                //reset the special price
                if (rule.getRule().split(" ").length != 3) {
                    throw new BusinessException(BusinessExceptionCode.INVALID_RULE);
                }
                newPrice.setSpecialPrice(rule.getRule().trim());
                priceService.saveOrUpdate(newPrice);
                ids.add(item.getId());
            }
        }
        ResponseDto responseDto = new ResponseDto();
        responseDto.setContent(ids);
        return responseDto;
    }


    /**
     * each item name separate by a space
     * assume scan all items then calculate the total
     *
     * @param items
     * @return
     */
    @PostMapping("/prices/total")
    public ResponseDto calculatePrice(@RequestBody String items) {
        BigDecimal total = new BigDecimal(0);
        ResponseDto responseDto = new ResponseDto();
        String[] itemA;
        if (!items.isEmpty()) {
            itemA = items.split(" ");
        } else {
            responseDto.setContent(total);
            return responseDto;
        }
        //store individual item
        ArrayList<Item> itemList = new ArrayList<>();
        //store item and its quantity
        Map<String, Long> itemTotal = Arrays.stream(itemA)
                .collect(Collectors.groupingBy(s -> s, Collectors.counting()));


        List<String> calculated = new LinkedList<>();

        for (String item : itemTotal.keySet()) {
            int quantity = itemTotal.get(item).intValue();
            int remain = 0;
            Price price = priceService.getPriceByItem(item);
            BigDecimal specialPrice = new BigDecimal(0);
            int specialQuantity = 0;
            if (!price.getSpecialPrice().isEmpty()) {
                String[] sPrices = price.getSpecialPrice().trim().split(" ");
                if (sPrices.length != 3) {
                    throw new BusinessException(BusinessExceptionCode.INVALID_RULE);
                }
                specialQuantity = Integer.valueOf(sPrices[0]);
                specialPrice = new BigDecimal(sPrices[2]);
            }
            //case for specialPrice
            if (specialPrice.compareTo(BigDecimal.ZERO) > 0 && quantity >= specialQuantity) {
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

        responseDto.setContent(total);
        return responseDto;
    }
}
