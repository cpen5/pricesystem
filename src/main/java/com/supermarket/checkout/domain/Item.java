package com.supermarket.checkout.domain;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class Item {
    private String name;
    private BigDecimal unitPrice;
    private int specialQuantity;
    private BigDecimal specialPrice;

    public int getSpecialQuantity() {
        return specialQuantity;
    }

    public Item() {
        this.specialQuantity = 0;
    }

    public void setSpecialQuantity(int specialQuantity) {
        this.specialQuantity = specialQuantity;
    }

    public BigDecimal getSpecialPrice() {
        return specialPrice;
    }

    public void setSpecialPrice(BigDecimal specialPrice) {
        this.specialPrice = specialPrice;
    }


    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
