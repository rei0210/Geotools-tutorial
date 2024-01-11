package org.shpApi.entity;

import org.locationtech.jts.geom.Geometry;

import java.util.List;
import java.util.Map;

public class ShpFeatureInfo {

    private String id;

    // private List<ShpAttribute> shpAttributeList;
    // 属性
    private Map<String,Object> shpAttrMap;

    private Geometry geometry;

    public ShpFeatureInfo(String id, Map<String,Object> shpAttrMap, Geometry geometry) {
        this.id = id;
        this.shpAttrMap = shpAttrMap;
        this.geometry = geometry;
    }

    public ShpFeatureInfo() {

    }

    /*
    *
    * */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }




    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public Map<String, Object> getShpAttrMap() {
        return shpAttrMap;
    }

    public void setShpAttrMap(Map<String, Object> shpAttrMap) {
        this.shpAttrMap = shpAttrMap;
    }
}
