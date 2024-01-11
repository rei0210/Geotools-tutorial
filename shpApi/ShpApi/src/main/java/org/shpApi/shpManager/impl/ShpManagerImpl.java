package org.shpApi.shpManager.impl;

import org.geotools.api.data.*;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.feature.type.AttributeDescriptor;
import org.geotools.api.filter.Filter;
import org.geotools.api.filter.FilterFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.filter.text.ecql.ECQL;
import org.geotools.geojson.geom.GeometryJSON;
import org.locationtech.jts.geom.Geometry;
import org.shpApi.entity.ShpAttribute;
import org.shpApi.entity.ShpFeatureInfo;
import org.shpApi.shpManager.ShpManager;
import org.shpApi.tools.ShpTools;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

public class ShpManagerImpl implements ShpManager {
    public List<ShpFeatureInfo> getShpFeaturesList(File file, ShpFeatureInfo shpFeatureInfo) {

        return null;
    }

    public void addShpFeatures(File file, ShpFeatureInfo shpFeatureInfo) {
        try{
            DataStore store = FileDataStoreFinder.getDataStore(file);
            String typeName=store.getTypeNames()[0];
            List<AttributeDescriptor> attrList = store.getFeatureSource(typeName).getSchema()
                    .getAttributeDescriptors();
            //List<String> fieldnames=new ArrayList<>();
            FeatureWriter<SimpleFeatureType, SimpleFeature> featureWriter = store.getFeatureWriterAppend(store.getTypeNames()[0], Transaction.AUTO_COMMIT);
            SimpleFeature next = featureWriter.next();
            String fieldname="";
            Map<String,Object> map=shpFeatureInfo.getShpAttrMap();
            for (AttributeDescriptor attr : attrList) {
                fieldname=attr.getLocalName();
                if(map.containsKey(fieldname)){
                    next.setAttribute(fieldname,map.get(fieldname));
                }
                System.out.println("fieldname:"+attr.getLocalName());//输出字段名
                //fieldnames.add(attr.getLocalName());
                //System.out.println("fieldtype:"+attr.getType().getBinding());//输出字段类型
            }
            Geometry geometry =shpFeatureInfo.getGeometry();
            StringWriter writer = new StringWriter();
//            GeometryJSON g = new GeometryJSON();
//            System.out.println(geometry.toString());
//            g.write(geometry, writer);
//            System.out.println(writer.toString());
            next.setDefaultGeometry(geometry);
            // 必须写入和关闭
            featureWriter.write();
            featureWriter.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void deleteShpFeatures(File file, ShpFeatureInfo shpFeatureInfo) {

    }

    public void updateShpFeaturesById(File file, ShpFeatureInfo shpFeatureInfo) throws Exception {//判断file是否有效
        if(ShpTools.isValidShpFile(file)){
            try {
                DataStore store = FileDataStoreFinder.getDataStore(file);
                String typeName=store.getTypeNames()[0];
                SimpleFeatureSource featureSource = store.getFeatureSource(typeName);//获取FeatureSource
                FilterFactory ff = CommonFactoryFinder.getFilterFactory();
                Filter filter=ff.id(ff.featureId(shpFeatureInfo.getId()));
                //String cql="IN ('"+shpFeatureInfo.getId()+"')";
                if( featureSource instanceof SimpleFeatureStore) {
                   // Filter filter = ECQL.toFilter(cql);
                    SimpleFeatureCollection features = featureSource.getFeatures(filter);
                    FeatureWriter<SimpleFeatureType, SimpleFeature> featureWriter = store.getFeatureWriter(store.getTypeNames()[0], filter, Transaction.AUTO_COMMIT);
                    Map<String,Object> map=shpFeatureInfo.getShpAttrMap();
                    while (featureWriter.hasNext()) {
                        SimpleFeature simpleFeature = featureWriter.next();
                        for(Map.Entry<String, Object> a : map.entrySet()){
                            simpleFeature.setAttribute(a.getKey(),a.getValue());
                        }
                        simpleFeature.setDefaultGeometry(shpFeatureInfo.getGeometry());
                    }
                    featureWriter.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {

            }
        }

    }


}
