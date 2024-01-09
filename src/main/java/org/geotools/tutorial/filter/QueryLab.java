package org.geotools.tutorial.filter;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import org.geotools.api.data.DataStore;
import org.geotools.api.data.DataStoreFactorySpi;
import org.geotools.api.data.DataStoreFinder;
import org.geotools.api.data.Query;
import org.geotools.api.data.SimpleFeatureSource;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.type.AttributeDescriptor;
import org.geotools.api.feature.type.FeatureType;
import org.geotools.api.filter.Filter;
import org.geotools.data.postgis.PostgisNGDataStoreFactory;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.swing.action.SafeAction;
import org.geotools.swing.data.JDataStoreWizard;
import org.geotools.swing.table.FeatureCollectionTableModel;
import org.geotools.swing.wizard.JWizard;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;

public class QueryLab extends JFrame {
    private DataStore dataStore;
    private JComboBox<String> featureTypeCBox;
    private JTable table;
    private JTextField text;

    public static void main(String[] args) throws Exception {
        JFrame frame = new QueryLab();
        frame.setVisible(true);
    }

    public QueryLab() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());

        text = new JTextField(80);
        text.setText("include"); // include selects everything!
        getContentPane().add(text, BorderLayout.NORTH);

        table = new JTable();
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setModel(new DefaultTableModel(5, 5));
        table.setPreferredScrollableViewportSize(new Dimension(500, 200));

        JScrollPane scrollPane = new JScrollPane(table);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        JMenuBar menubar = new JMenuBar();
        setJMenuBar(menubar);

        JMenu fileMenu = new JMenu("File");
        menubar.add(fileMenu);

        featureTypeCBox = new JComboBox<>();
        menubar.add(featureTypeCBox);

        JMenu dataMenu = new JMenu("Data");
        menubar.add(dataMenu);
        pack();
        fileMenu.add(
                new SafeAction("Open shapefile...") {
                    public void action(ActionEvent e) throws Throwable {
                        connect(new ShapefileDataStoreFactory());
                    }
                });
        fileMenu.add(
                new SafeAction("Connect to PostGIS database...") {
                    public void action(ActionEvent e) throws Throwable {
                        connect(new PostgisNGDataStoreFactory());
                    }
                });
        fileMenu.add(
                new SafeAction("Connect to DataStore...") {
                    public void action(ActionEvent e) throws Throwable {
                        connect(null);
                    }
                });
        fileMenu.addSeparator();
        fileMenu.add(
                new SafeAction("Exit") {
                    public void action(ActionEvent e) throws Throwable {
                        System.exit(0);
                    }
                });
        dataMenu.add(
                new SafeAction("Get features") {
                    public void action(ActionEvent e) throws Throwable {
                        filterFeatures();
                    }
                });
        dataMenu.add(
                new SafeAction("Count") {
                    public void action(ActionEvent e) throws Throwable {
                        countFeatures();
                    }
                });
        dataMenu.add(
                new SafeAction("Geometry") {
                    public void action(ActionEvent e) throws Throwable {
                        queryFeatures();
                    }
                });
    }
    private void connect(DataStoreFactorySpi format) throws Exception {
        try{
            System.out.println(format.getDescription());
            JDataStoreWizard wizard = new JDataStoreWizard(format);//与教程中不同的地方：需要添加gt-xml依赖，否则会显示无法初始化JDataStoreWizard
            System.out.println("wizard");
            int result = wizard.showModalDialog();
            System.out.println("result"+result);
            if (result == JWizard.FINISH) {
                Map<String, Object> connectionParameters = wizard.getConnectionParameters();
                for (Map.Entry<String, Object> entry : connectionParameters.entrySet()) {
                    System.out.println("key = " + entry.getKey() + ", value = " + entry.getValue());
                }
                dataStore = DataStoreFinder.getDataStore(connectionParameters);

                String shpFilePath="D:\\shapeFiles\\test6\\country\\countries.shp";
                ShapefileDataStore dataStore1=buildDataStore(shpFilePath);//直接通过路径获取datastore

                String typeName=dataStore.getTypeNames()[0];
                List<AttributeDescriptor> attrList = dataStore.getFeatureSource(typeName).getSchema()
                        .getAttributeDescriptors();
//                System.out.println(dataStore1.getFeatureSource().getSchema().getAttributeDescriptors().toString());
                for (AttributeDescriptor attr : attrList) {
                    System.out.println("fieldname:"+attr.getLocalName());//输出字段名
                    System.out.println("fieldtype:"+attr.getType().getBinding());//输出字段类型
                }
                if (dataStore == null) {
                    JOptionPane.showMessageDialog(null, "Could not connect - check parameters");
                }
                updateUI();
                filterFeatures();//打开文件后显示所有feature
            }

        }catch (Exception e){
            e.printStackTrace();
        }


    }
    public static ShapefileDataStore buildDataStore(String shpFilePath) {
        ShapefileDataStoreFactory factory = new ShapefileDataStoreFactory();
        try {
            ShapefileDataStore dataStore = (ShapefileDataStore) factory
                    .createDataStore(new File(shpFilePath).toURI().toURL());
            if (dataStore != null) {
                dataStore.setCharset(Charset.forName("UTF-8"));
            }
            return dataStore;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
    private void updateUI() throws Exception {
        System.out.println(Arrays.toString(dataStore.getTypeNames()));
        ComboBoxModel<String> cbm = new DefaultComboBoxModel<>(dataStore.getTypeNames());
        featureTypeCBox.setModel(cbm);

        table.setModel(new DefaultTableModel(5, 5));
    }

    private void filterFeatures() throws Exception {
        String typeName = (String) featureTypeCBox.getSelectedItem();
        SimpleFeatureSource source = dataStore.getFeatureSource(typeName);
        System.out.println("typename"+typeName);
        Filter filter = CQL.toFilter(text.getText());
        SimpleFeatureCollection features = source.getFeatures(filter);
        System.out.println(features.getSchema().getGeometryDescriptor().getType().getName().toString());
        FeatureCollectionTableModel model = new FeatureCollectionTableModel(features);
        table.setModel(model);
    }

    private void countFeatures() throws Exception {
        String typeName = (String) featureTypeCBox.getSelectedItem();
        SimpleFeatureSource source = dataStore.getFeatureSource(typeName);

        Filter filter = CQL.toFilter(text.getText());
        SimpleFeatureCollection features = source.getFeatures(filter);

        int count = features.size();
        JOptionPane.showMessageDialog(text, "Number of selected features:" + count);
    }
    private void queryFeatures() throws Exception {
        String typeName = (String) featureTypeCBox.getSelectedItem();
        SimpleFeatureSource source = dataStore.getFeatureSource(typeName);

        FeatureType schema = source.getSchema();
        String name = schema.getGeometryDescriptor().getLocalName();
        System.out.println("name"+name);
        Filter filter = CQL.toFilter(text.getText());

        Query query = new Query(typeName, filter, new String[] {name});

        SimpleFeatureCollection features = source.getFeatures(query);

        FeatureCollectionTableModel model = new FeatureCollectionTableModel(features);
        table.setModel(model);
    }


}
