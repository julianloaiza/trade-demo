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
    
    private final Map<Integer, String> data;
    
    public Message(Map<Integer, String> data) {
        this.data = data;
    }
    
    public String getString(int tag) {
        return data.get(tag);
    }
    
    public Double getDouble(int tag) {
        return Double.parseDouble(data.get(tag));
    }
    
    public Integer getInt(int tag) {
        return Integer.parseInt(data.get(tag));
    }
}
