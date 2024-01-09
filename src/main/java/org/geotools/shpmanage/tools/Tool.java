package org.geotools.shpmanage.tools;

import com.alibaba.fastjson.JSONArray;
import org.geotools.api.filter.Filter;
import org.geotools.data.shapefile.files.ShpFileType;
import org.geotools.data.shapefile.files.ShpFiles;

import java.io.File;

public class Tool {
    public static File checkShapeFile(String fileUrl) throws Exception {
        File file = new File(fileUrl);
        if (file.isDirectory()) {
            File[] fa = file.listFiles();
            if (fa == null || fa.length < 1) {
                throw new Exception("找不到shp文件");
            }
            boolean flag = true;
            for (File f : fa) {
                if (new ShpFiles(file).exists(ShpFileType.SHP)) {
                    file = f;
                    flag = false;
                    break;
                }
            }
            if (flag) {
                throw new Exception("找不到shp文件");
            }
        } else {
            if (!new ShpFiles(file).exists(ShpFileType.SHP)) {
                throw new Exception("找不到shp文件");
            }
        }
        return file;
    }

    public static JSONArray shapeFileToJsonArray(String fileUrl, Filter filter) {
        return null;
    }
}
