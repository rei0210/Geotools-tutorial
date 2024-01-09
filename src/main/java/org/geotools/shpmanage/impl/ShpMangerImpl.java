package org.geotools.shpmanage.impl;

import org.geotools.api.data.*;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.feature.type.AttributeDescriptor;
import org.geotools.geojson.geom.GeometryJSON;
import org.geotools.shpmanage.ShpManager;
import org.locationtech.jts.geom.Geometry;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public  class ShpMangerImpl implements ShpManager {


    public ShpMangerImpl() {

    }




    @Override
    public void addFeature(File file, Map<String, Object> featureMap, String geometryStr) throws IOException {//添加feature

        DataStore store = FileDataStoreFinder.getDataStore(file);
        String typeName=store.getTypeNames()[0];
        List<AttributeDescriptor> attrList = store.getFeatureSource(typeName).getSchema()
                .getAttributeDescriptors();
        //List<String> fieldnames=new ArrayList<>();
        FeatureWriter<SimpleFeatureType, SimpleFeature> featureWriter = store.getFeatureWriterAppend(store.getTypeNames()[0], Transaction.AUTO_COMMIT);
        SimpleFeature next = featureWriter.next();
        String fieldname="";
        for (AttributeDescriptor attr : attrList) {
            fieldname=attr.getLocalName();
            if(featureMap.containsKey(fieldname)){
                next.setAttribute(fieldname,featureMap.get(fieldname));
            }
            System.out.println("fieldname:"+attr.getLocalName());//输出字段名
            //fieldnames.add(attr.getLocalName());
            //System.out.println("fieldtype:"+attr.getType().getBinding());//输出字段类型
        }
        Geometry geometry = new GeometryJSON().read(geometryStr);

        next.setDefaultGeometry(geometry);
        // 必须写入和关闭
        featureWriter.write();
        featureWriter.close();
    }


    @Override
    public void deleteFeature() {

    }

    @Override
    public void updateFeature() {

    }

    @Override
    public void getFeaturesById(String id) {

    }





}
