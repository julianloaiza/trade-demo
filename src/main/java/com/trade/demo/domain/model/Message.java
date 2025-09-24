package com.trade.demo.domain.model;

import java.util.Map;

/**
 * FIX message object.
 * 17 - execId
 * 48 - securityId
 * 22 - securityIdSource
 * 1 - account
 * 32 - lastShares (positive int)
 * 6 - avgPx (positive double)
 * 8 - side (BUY, SELL)
 */
public class Message {
    
    private final Map<String, Object> data;
    
    public Message(Map<String, Object> data) {
        this.data = data;
    }
    
    public String getString(int tag) {
        return data.get(String.valueOf(tag)).toString();
    }
    
    public Double getDouble(int tag) {
        return ((Number) data.get(String.valueOf(tag))).doubleValue();
    }
    
    public Integer getInt(int tag) {
        return ((Number) data.get(String.valueOf(tag))).intValue();
    }
}
