package com.supermarket.checkout.domain;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class Rule {
    private String itemName;
    private String rule;

    public String getItemName() {
        return itemName;
    }



    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }
}
