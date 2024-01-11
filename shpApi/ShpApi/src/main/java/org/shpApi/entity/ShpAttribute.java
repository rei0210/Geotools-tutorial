package org.shpApi.entity;

import org.locationtech.jts.geom.Geometry;

public class ShpAttribute {

    private String key;
    private Object value;



    public ShpAttribute(String key, Object value) {

        this.key = key;
        this.value = value;
    }

    public ShpAttribute() {
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }


}
