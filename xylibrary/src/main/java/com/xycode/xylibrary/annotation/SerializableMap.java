package com.xycode.xylibrary.annotation;

import java.io.Serializable;
import java.util.Map;

public class SerializableMap implements Serializable {

    public SerializableMap(Map<String, Object> map) {
        this.map = map;
    }

    private Map<String,Object> map;
 
    public Map<String, Object> getMap() {
        return map;
    }
 
    public void setMap(Map<String, Object> map) {
        this.map = map;
    }
}