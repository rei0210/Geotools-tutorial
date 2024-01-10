package org.geotools.shpmanage.impl;

import org.geotools.api.data.*;
import org.geotools.api.feature.Property;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.feature.type.AttributeDescriptor;
import org.geotools.api.feature.type.Name;
import org.geotools.api.filter.Filter;
import org.geotools.api.filter.FilterFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.filter.text.ecql.ECQL;
import org.geotools.geojson.geom.GeometryJSON;
import org.geotools.shpmanage.ShpManager;
import org.locationtech.jts.geom.Geometry;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
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
    public void getShpFeatures(File file, Map<String, Object> featureMap, String geometryStr) throws IOException {//使用Filter查询数据
        DataStore store = FileDataStoreFinder.getDataStore(file);
        Geometry geometry = new GeometryJSON().read(geometryStr);

        FilterFactory filterFactory= CommonFactoryFinder.getFilterFactory();
        Filter filter=filterFactory.intersects(filterFactory.property("the_geom"),filterFactory.literal(geometry));//工厂过滤器貌似只能进行单条件查询
        for(Map.Entry<String, Object> entry : featureMap.entrySet()){
            System.out.println("key:"+entry.getKey()+" "+"val:"+entry.getValue());
            filter =  filterFactory.equals(filterFactory.property(entry.getKey()), filterFactory.literal(entry.getValue()));
        }
        String typeName=store.getTypeNames()[0];
        SimpleFeatureSource featureSource = store.getFeatureSource(typeName);

        SimpleFeatureCollection features = featureSource.getFeatures(filter);

        SimpleFeatureIterator simpleFeatureIterator = features.features();
        while (simpleFeatureIterator.hasNext()) {
            SimpleFeature simpleFeature = simpleFeatureIterator.next();
            Iterator<Property> iterator = simpleFeature.getProperties().iterator();
            while (iterator.hasNext()) {
                Property property = iterator.next();
                System.out.println(property.getName() + ":" + property.getValue());
            }
            System.out.println("");
        }

    }

    @Override
    public void getShpFeaturesByCQL(File file, String cql) throws IOException, CQLException {
        DataStore store = FileDataStoreFinder.getDataStore(file);
        String typeName=store.getTypeNames()[0];

        SimpleFeatureSource source = store.getFeatureSource(typeName);
        Filter filter = ECQL.toFilter(cql);//使用ECQL语句查询
        SimpleFeatureCollection features = source.getFeatures(filter);

        SimpleFeatureIterator simpleFeatureIterator = features.features();
        while (simpleFeatureIterator.hasNext()) {
            SimpleFeature simpleFeature = simpleFeatureIterator.next();
            System.out.println("主键id:"+simpleFeature.getID());//获取主键
            Iterator<Property> iterator = simpleFeature.getProperties().iterator();
            while (iterator.hasNext()) {
                Property property = iterator.next();
                System.out.println(property.getName() + ":" + property.getValue());
//                SimpleFeature feature1 = simpleFeatureIterator.next();
               // System.out.println("主键id:"+feature1.getAttribute("id"));
            }
            System.out.println("");
        }
    }

    @Override
    public void deleteFeatureByCQL(File file, String cql) throws IOException, CQLException {
        DataStore store = FileDataStoreFinder.getDataStore(file);
        String typeName=store.getTypeNames()[0];
        Filter filter = ECQL.toFilter(cql);

        SimpleFeatureSource featureSource = store.getFeatureSource(typeName);//获取FeatureSource
        if( featureSource instanceof SimpleFeatureStore){
            SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource; // write access!
            featureStore.removeFeatures(filter);
        }

    }

    @Override
    public void updateFeatureByCQL(File file, String cql,Map<String, Object> featureMap) throws IOException, CQLException {
        DataStore store = FileDataStoreFinder.getDataStore(file);
        String typeName=store.getTypeNames()[0];
        SimpleFeatureSource featureSource = store.getFeatureSource(typeName);//获取FeatureSource
        if( featureSource instanceof SimpleFeatureStore){
            Filter filter = ECQL.toFilter(cql);
//            Query query = new Query(filter);
            SimpleFeatureCollection features = featureSource.getFeatures(filter);
            SimpleFeatureIterator iterator = features.features();
            FeatureWriter<SimpleFeatureType, SimpleFeature> featureWriter = store.getFeatureWriter(store.getTypeNames()[0], filter,Transaction.AUTO_COMMIT);
            while (featureWriter.hasNext()) {
                SimpleFeature simpleFeature = featureWriter.next();
                for(Map.Entry<String, Object> entry : featureMap.entrySet()){
                    simpleFeature.setAttribute(entry.getKey(),entry.getValue());
                }
            }
            featureWriter.close();

        }


    }


}
