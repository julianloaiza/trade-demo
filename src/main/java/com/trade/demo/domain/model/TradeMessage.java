package com.trade.demo.domain.model;

import com.trade.demo.domain.enums.IdSource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TradeMessage {
    private String tradeId;
    private String account;
    private String securityId;
    private IdSource idSource;
    private String isin;
    private String sedol;
    private String cusip;
    private String ric;
    private String ticker;
    private Integer qty;
    private Double price;
}
