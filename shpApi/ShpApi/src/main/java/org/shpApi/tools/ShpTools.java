package org.shpApi.tools;

import org.geotools.api.data.DataStore;
import org.geotools.api.data.FeatureSource;
import org.geotools.api.data.FileDataStoreFinder;
import org.geotools.api.data.SimpleFeatureSource;
import org.geotools.data.shapefile.files.ShpFileType;
import org.geotools.data.shapefile.files.ShpFiles;

import java.io.File;
import java.io.IOException;

public class ShpTools {
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

    public static boolean isValidShpFile(File file) throws Exception {
        if (file == null) {
            throw new Exception("文件不能为空");
        }
        else if(!file.exists()){
            throw new Exception("文件不存在");
        }
        String path=file.getPath();
        String fileType=path.substring(path.lastIndexOf(".")+1);
        if(fileType.equals("shp")){
            return true;
        }else {
            throw new Exception("文件不是shapefile文件");
        }
    }

    public static SimpleFeatureSource getShpFeatureSource(File file) throws IOException {
        DataStore store = FileDataStoreFinder.getDataStore(file);
        String typeName=store.getTypeNames()[0];
        return  store.getFeatureSource(typeName);
    }
}
