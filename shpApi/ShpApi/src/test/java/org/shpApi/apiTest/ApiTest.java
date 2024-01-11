package org.shpApi.apiTest;

import org.geotools.geojson.geom.GeometryJSON;
import org.junit.Test;
import org.locationtech.jts.geom.Geometry;
import org.shpApi.entity.ShpAttribute;
import org.shpApi.entity.ShpFeatureInfo;
import org.shpApi.shpManager.ShpManager;
import org.shpApi.shpManager.impl.ShpManagerImpl;
import org.shpApi.tools.ShpTools;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ApiTest {

    ShpManager shpManager=new ShpManagerImpl();

    @Test
    public void TestUpdate() throws Exception {
        String fileurl="D:\\shapeFiles\\test6\\c1\\c1.shp";
        String geoJsonString = "{\"type\":\"Polygon\",\"coordinates\":[[ [4.150,0.712], [5.698,1.723], [3,-0.168],[4.150,0.712]]]}";//几何坐标
        File file= ShpTools.checkShapeFile(fileurl);
        Geometry geometry = new GeometryJSON().read(geoJsonString);
        String id="c1.1";
        Map<String,Object> map=new LinkedHashMap<>();
        map.put("c1_id",6);
        map.put("c1_type","triangle");
        ShpFeatureInfo shpFeatureInfo=new ShpFeatureInfo();
        shpFeatureInfo.setId(id);
        shpFeatureInfo.setGeometry(geometry);
        shpFeatureInfo.setShpAttrMap(map);
        shpManager.updateShpFeaturesById(file,shpFeatureInfo);


    }
}
