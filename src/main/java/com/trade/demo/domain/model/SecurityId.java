package com.trade.demo.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecurityId {
    private String ric;
    private String isin;
    private String cusip;
    private String sedol;
    private String ticker;
    private String name;
}
