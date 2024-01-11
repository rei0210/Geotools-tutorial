package org.geotools.shpmanage;

import org.geotools.api.data.FileDataStore;
import org.geotools.api.data.FileDataStoreFinder;
import org.geotools.shpmanage.impl.ShpMangerImpl;
import org.geotools.shpmanage.tools.Tool;
import org.geotools.swing.data.JFileDataStoreChooser;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ShpManagerApp {


    public static void main(String[] args) throws Exception {
        String fileurl="D:\\shapeFiles\\test6\\star2\\star2.shp";
        String geoJsonString = "{\"type\":\"Polygon\",\"coordinates\":[[[4.150,0.712], [2.698,1.723], [2.493,-0.168],[4.150,0.712]]]}";//几何坐标
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id",10);
        map.put("name","star10");
        File file= Tool.checkShapeFile(fileurl);
        ShpManager shpManager=new ShpMangerImpl();
        shpManager.addFeature(file,map,geoJsonString);

    }
}
