package org.geotools.shpManageTest;


import org.geotools.shpmanage.ShpManager;
import org.geotools.shpmanage.impl.ShpMangerImpl;
import org.geotools.shpmanage.tools.Tool;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ShpManageTest {

    ShpManager shpManager=new ShpMangerImpl();

    @Test
    public void testGet() throws Exception {

        String fileurl="D:\\shapeFiles\\test6\\star2\\star2.shp";
        String geoJsonString = "{\"type\":\"Polygon\",\"coordinates\":[[ [4.150,0.712], [2.698,1.723], [2.493,-0.168],[4.150,0.712]]]}";//几何坐标
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("id",3);
        map.put("id",2);
        map.put("name","star10");
        map.put("name","star2");

        File file= Tool.checkShapeFile(fileurl);
        ShpManager shpManager=new ShpMangerImpl();
        shpManager.getShpFeatures(file,map,geoJsonString);
    }


    @Test
    public void testGet1() throws Exception {//使用ECQL过滤器

        String fileurl="D:\\shapeFiles\\test6\\star2\\star2.shp";
        //String cql="include";
        //String cql="ID in('star2.1')";//这样写报一个错误：警告: ID IN (...) is a deprecated syntax, you should use IN (...)
        String cql="IN ('star2.1')";//使用String作id

        File file= Tool.checkShapeFile(fileurl);
        ShpManager shpManager=new ShpMangerImpl();
        shpManager.getShpFeaturesByCQL(file,cql);
    }

    @Test
    public void testDel1() throws Exception {//使用CQL过滤器

        String fileurl="D:\\shapeFiles\\test6\\star2\\star2.shp";
        //String cql="include";
        String cql="index = 2";//删除所有id为x的要素
        File file= Tool.checkShapeFile(fileurl);
        ShpManager shpManager=new ShpMangerImpl();
        shpManager.deleteFeatureByCQL(file,cql);
    }


    @Test
    public void testUpdate1() throws Exception {//使用CQL过滤器

        //String fileurl="D:\\shapeFiles\\test6\\star2\\star2.shp";
        String fileurl="D:\\shapeFiles\\test6\\c1\\c1.shp";
        //String cql="include";
        String cql="c1_id=2";//shp文件中自定义的属性不能叫id否则会报错
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("c1_type","triangle");
        File file= Tool.checkShapeFile(fileurl);
        ShpManager shpManager=new ShpMangerImpl();
        shpManager.updateFeatureByCQL(file,cql,map);
    }
}
