package org.geotools.shpmanage;

import org.geotools.filter.text.cql2.CQLException;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public interface ShpManager {



    void addFeature(File file, Map<String, Object> featureMap, String geometryStr) throws IOException;


    public void deleteFeature();

    public void updateFeature();

    public void getShpFeatures(File file, Map<String, Object> featureMap, String geometryStr) throws IOException;

    public void getShpFeaturesByCQL(File file, String cql) throws IOException, CQLException;

    public void deleteFeatureByCQL(File file,String cql) throws IOException, CQLException;

    public void updateFeatureByCQL(File file,String cql,Map<String, Object> featureMap) throws IOException, CQLException;



}
