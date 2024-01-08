package org.geotools.tutorial.process;

import org.geotools.api.feature.type.Name;
import org.geotools.feature.NameImpl;
import org.geotools.process.Process;
import org.geotools.process.ProcessExecutor;
import org.geotools.process.Processors;
import org.geotools.process.Progress;
import org.geotools.util.KVP;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ProcessApp {
    public static void main(String[] args) throws ExecutionException, InterruptedException, ParseException {
        WKTReader wktReader = new WKTReader(new GeometryFactory());
        Geometry geom = wktReader.read("MULTIPOINT (1 1, 5 4, 7 9, 5 5, 2 2)");

        Name name = new NameImpl("tutorial", "octagonalEnvelope");
        Process process = (Process) Processors.createProcess(name);

        ProcessExecutor engine = Processors.newProcessExecutor(2);

        // quick map of inputs
        Map<String, Object> input = new KVP("geom", geom);
        Progress working = engine.submit(process, input);

        // you could do other stuff whle working is doing its thing
//        if (working.isCancelled()) {
//            return;
//        }

//        Map<String, Object> result = working.get(); // get is BLOCKING
//        Geometry octo = (Geometry) result.get("result");
//
//        System.out.println(octo);
    }
}
