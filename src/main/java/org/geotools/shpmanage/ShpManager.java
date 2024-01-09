package org.geotools.shpmanage;

import org.geotools.api.filter.Filter;
import com.alibaba.fastjson.JSONArray;
import org.geotools.api.data.DataStore;
import org.geotools.data.shapefile.files.ShpFileType;
import org.geotools.data.shapefile.files.ShpFiles;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public interface ShpManager {



    void addFeature(File file, Map<String, Object> featureMap, String geometryStr) throws IOException;


    public void deleteFeature();

    public void updateFeature();

    public void getFeaturesById(String id);



}
