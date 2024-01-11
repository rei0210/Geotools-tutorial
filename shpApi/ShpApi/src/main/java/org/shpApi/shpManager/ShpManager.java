package org.shpApi.shpManager;

import org.geotools.filter.text.cql2.CQLException;
import org.shpApi.entity.ShpFeatureInfo;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface ShpManager {
    public List<ShpFeatureInfo> getShpFeaturesList(File file, ShpFeatureInfo shpFeatureInfo);

    public void addShpFeatures(File file,ShpFeatureInfo shpFeatureInfo);

    public void deleteShpFeatures(File file,ShpFeatureInfo shpFeatureInfo);

    public void updateShpFeaturesById(File file,ShpFeatureInfo shpFeatureInfo) throws Exception;
}
