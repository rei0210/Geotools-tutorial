package org.geotools.tutorial.style;


import java.awt.Color;
import java.io.File;
import org.geotools.api.data.FeatureSource;
import org.geotools.api.data.FileDataStore;
import org.geotools.api.data.FileDataStoreFinder;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.filter.FilterFactory;
import org.geotools.api.style.FeatureTypeStyle;
import org.geotools.api.style.Fill;
import org.geotools.api.style.Graphic;
import org.geotools.api.style.LineSymbolizer;
import org.geotools.api.style.Mark;
import org.geotools.api.style.PointSymbolizer;
import org.geotools.api.style.PolygonSymbolizer;
import org.geotools.api.style.Rule;
import org.geotools.api.style.Stroke;
import org.geotools.api.style.Style;
import org.geotools.api.style.StyleFactory;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.swing.JMapFrame;
import org.geotools.swing.data.JFileDataStoreChooser;
import org.geotools.swing.dialog.JExceptionReporter;
import org.geotools.swing.styling.JSimpleStyleDialog;
import org.geotools.xml.styling.SLDParser;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;

import static org.geotools.styling.SLD.*;

public class StyleLab {
    static StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory();
    static FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory();

    public static void main(String[] args) throws Exception {
        StyleLab me = new StyleLab();
        me.displayShapefile();
    }

    /**
     * Prompts the user for a shapefile (unless a filename is provided on the command line; then
     * creates a simple Style and displays the shapefile on screen
     */
    private void displayShapefile() throws Exception {
        File file = JFileDataStoreChooser.showOpenFile("shp", null);
        if (file == null) {
            return;
        }

        FileDataStore store = FileDataStoreFinder.getDataStore(file);
        FeatureSource featureSource = store.getFeatureSource();
        System.out.println("features:"+featureSource.getFeatures().toArray()[0]);
        MapContent map = new MapContent();
        map.setTitle("StyleLab");

        // Create a basic Style to render the features
        //Style style = createStyle(file, featureSource);
        Style style = createStyle2(featureSource);

        // Add the features and the associated Style object to
        // the MapContent as a new Layer
        Layer layer = new FeatureLayer(featureSource, style);
        map.addLayer(layer);

        // Now display the map
        JMapFrame.showMap(map);
    }

    /**
     * Create a Style to display the features. If an SLD file is in the same directory as the
     * shapefile then we will create the Style by processing this. Otherwise we display a
     * JSimpleStyleDialog to prompt the user for preferences.
     */
    private Style createStyle(File file, FeatureSource featureSource) {
        File sld = toSLDFile(file);
        if (sld != null) {
            return createFromSLD(sld);
        }

        SimpleFeatureType schema = (SimpleFeatureType) featureSource.getSchema();
        System.out.println("schema:"+schema.getTypeName());
        return JSimpleStyleDialog.showDialog(null, schema);
    }
    /** Figure out if a valid SLD file is available. */
    public File toSLDFile(File file) {//查找在shp同目录下的sld文件
        String path = file.getAbsolutePath();
        String base = path.substring(0, path.length() - 4);
        String newPath = base + ".sld";
        File sld = new File(newPath);
        if (sld.exists()) {
            return sld;
        }
        newPath = base + ".SLD";
        sld = new File(newPath);
        System.out.println("newpath:"+newPath);
        if (sld.exists()) {
            System.out.println("exist");
            return sld;
        }
        return null;//如果不存在该文件,返回null
    }

    /** Create a Style object from a definition in a SLD document */
    private Style createFromSLD(File sld) {
        try {
            SLDParser stylereader = new SLDParser(styleFactory, sld.toURI().toURL());
            Style[] style = stylereader.readXML();
            return style[0];

        } catch (Exception e) {
            JExceptionReporter.showDialog(e, "Problem creating style");
        }
        return null;
    }

    /**
     * Here is a programmatic alternative to using JSimpleStyleDialog to get a Style. This methods
     * works out what sort of feature geometry we have in the shapefile and then delegates to an
     * appropriate style creating method.
     */
    private Style createStyle2(FeatureSource featureSource) {
        SimpleFeatureType schema = (SimpleFeatureType) featureSource.getSchema();
        Class geomType = schema.getGeometryDescriptor().getType().getBinding();

        if (Polygon.class.isAssignableFrom(geomType)
                || MultiPolygon.class.isAssignableFrom(geomType)) {
            return createPolygonStyle(Color.CYAN,Color.orange,0.5f);

        } else if (LineString.class.isAssignableFrom(geomType)
                || MultiLineString.class.isAssignableFrom(geomType)) {
            return createLineStyle(Color.CYAN,0.5f);

        } else {
            return createPointStyle("line",Color.CYAN,Color.orange,0.5f,0.5f);
        }
    }

}
