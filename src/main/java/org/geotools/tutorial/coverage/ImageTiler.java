package org.geotools.tutorial.coverage;

import java.io.File;
import java.io.IOException;
import org.geotools.api.geometry.Bounds;
import org.geotools.api.parameter.ParameterValueGroup;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.GridCoverage2DReader;
import org.geotools.coverage.grid.io.GridFormatFinder;
import org.geotools.coverage.processing.CoverageProcessor;
import org.geotools.coverage.processing.Operations;
import org.geotools.gce.geotiff.GeoTiffFormat;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.util.Arguments;
import org.geotools.util.factory.Hints;

/**
 * Simple tiling of a coverage based simply on the number vertical/horizontal tiles desired and
 * subdividing the geographic envelope. Uses coverage processing operations.
 */
public class ImageTiler {

    private final int NUM_HORIZONTAL_TILES = 16;
    private final int NUM_VERTICAL_TILES = 8;

    private Integer numberOfHorizontalTiles;
    private Integer numberOfVerticalTiles;
    private Double tileScale;//scale变大会让运行速度降低,scale越大，能放大的程度越大，文件大小越大
    private File inputFile;
    private File outputDirectory;

    private String getFileExtension(File file) {
        String name = file.getName();
        try {
            return name.substring(name.lastIndexOf(".") + 1);
        } catch (Exception e) {
            return "";
        }
    }

    public Integer getNumberOfHorizontalTiles() {
        return numberOfHorizontalTiles;
    }

    public void setNumberOfHorizontalTiles(Integer numberOfHorizontalTiles) {
        this.numberOfHorizontalTiles = numberOfHorizontalTiles;
    }

    public Integer getNumberOfVerticalTiles() {
        return numberOfVerticalTiles;
    }

    public void setNumberOfVerticalTiles(Integer numberOfVerticalTiles) {
        this.numberOfVerticalTiles = numberOfVerticalTiles;
    }

    public File getInputFile() {
        return inputFile;
    }

    public void setInputFile(File inputFile) {
        this.inputFile = inputFile;
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public Double getTileScale() {
        return tileScale;
    }

    public void setTileScale(Double tileScale) {
        this.tileScale = tileScale;
    }

    public static void main(String[] args) throws Exception {

        // GeoTools provides utility classes to parse command line arguments
        Arguments processedArgs = new Arguments(args);
        ImageTiler tiler = new ImageTiler();

        try {
            tiler.setInputFile(new File(processedArgs.getRequiredString("-f")));
            tiler.setOutputDirectory(new File(processedArgs.getRequiredString("-o")));
            tiler.setNumberOfHorizontalTiles(processedArgs.getOptionalInteger("-htc"));
            tiler.setNumberOfVerticalTiles(processedArgs.getOptionalInteger("-vtc"));
            tiler.setTileScale(processedArgs.getOptionalDouble("-scale"));
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            printUsage();
            System.exit(1);
        }

        tiler.tile();
    }

    private static void printUsage() {
        System.out.println(
                "Usage: -f inputFile -o outputDirectory [-tw tileWidth<default:256> "
                        + "-th tileHeight<default:256> ");
        System.out.println(
                "-htc horizontalTileCount<default:16> -vtc verticalTileCount<default:8>");
    }

    private void tile() throws IOException {
        AbstractGridFormat format = GridFormatFinder.findFormat(this.getInputFile());
        String fileExtension = this.getFileExtension(this.getInputFile());

        // working around a bug/quirk in geotiff loading via format.getReader which doesn't set this
        // correctly
        Hints hints = null;
        if (format instanceof GeoTiffFormat) {
            hints = new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE);
        }

        GridCoverage2DReader gridReader = format.getReader(this.getInputFile(), hints);
        GridCoverage2D gridCoverage = gridReader.read(null);
        ReferencedEnvelope coverageEnvelope = gridCoverage.getEnvelope2D();
        double coverageMinX = coverageEnvelope.getMinX();
        double coverageMaxX = coverageEnvelope.getMaxX();
        double coverageMinY = coverageEnvelope.getMinY();
        double coverageMaxY = coverageEnvelope.getMaxY();

        System.out.println("MInX:"+coverageMinX);
        System.out.println("MInY:"+coverageMinY);
        System.out.println("MaxX:"+coverageMaxX);
        System.out.println("MaxY:"+coverageMaxY);

        int htc =
                this.getNumberOfHorizontalTiles() != null
                        ? this.getNumberOfHorizontalTiles()
                        : NUM_HORIZONTAL_TILES;
        int vtc =
                this.getNumberOfVerticalTiles() != null
                        ? this.getNumberOfVerticalTiles()
                        : NUM_VERTICAL_TILES;

        double geographicTileWidth = (coverageMaxX - coverageMinX) / (double) htc;
        double geographicTileHeight = (coverageMaxY - coverageMinY) / (double) vtc;

        CoordinateReferenceSystem targetCRS = gridCoverage.getCoordinateReferenceSystem();

        // make sure to create our output directory if it doesn't already exist
        File tileDirectory = this.getOutputDirectory();
        if (!tileDirectory.exists()) {
            tileDirectory.mkdirs();
        }

        // iterate over our tile counts
        for (int i = 0; i < htc; i++) {
            for (int j = 0; j < vtc; j++) {

                System.out.println("Processing tile at indices i: " + i + " and j: " + j);
                // create the envelope of the tile
                Bounds envelope =
                        getTileEnvelope(
                                coverageMinX,
                                coverageMinY,
                                geographicTileWidth,
                                geographicTileHeight,
                                targetCRS,
                                i,
                                j);

                GridCoverage2D finalCoverage = cropCoverage(gridCoverage, envelope);

                if (this.getTileScale() != null) {
                    finalCoverage = scaleCoverage(finalCoverage);
                }

                // use the AbstractGridFormat's writer to write out the tile
                File tileFile = new File(tileDirectory, i + "_" + j + "." + fileExtension);
                format.getWriter(tileFile).write(finalCoverage, null);
            }
        }
    }
    private Bounds getTileEnvelope(
            double coverageMinX,
            double coverageMinY,
            double geographicTileWidth,
            double geographicTileHeight,
            CoordinateReferenceSystem targetCRS,
            int horizontalIndex,
            int verticalIndex) {

        double envelopeStartX = (horizontalIndex * geographicTileWidth) + coverageMinX;
        double envelopeEndX = envelopeStartX + geographicTileWidth;
        double envelopeStartY = (verticalIndex * geographicTileHeight) + coverageMinY;
        double envelopeEndY = envelopeStartY + geographicTileHeight;

        return new ReferencedEnvelope(
                envelopeStartX, envelopeEndX, envelopeStartY, envelopeEndY, targetCRS);
    }

    private GridCoverage2D cropCoverage(GridCoverage2D gridCoverage, Bounds envelope) {
        CoverageProcessor processor = CoverageProcessor.getInstance();

        // An example of manually creating the operation and parameters we want
        final ParameterValueGroup param = processor.getOperation("CoverageCrop").getParameters();
        param.parameter("Source").setValue(gridCoverage);
        param.parameter("Envelope").setValue(envelope);

        return (GridCoverage2D) processor.doOperation(param);
    }
    /**
     * Scale the coverage based on the set tileScale
     *
     * <p>As an alternative to using parameters to do the operations, we can use the Operations
     * class to do them in a slightly more type safe way.
     *
     * @param coverage the coverage to scale
     * @return the scaled coverage
     */
    private GridCoverage2D scaleCoverage(GridCoverage2D coverage) {
        Operations ops = new Operations(null);
        coverage =
                (GridCoverage2D)
                        ops.scale(coverage, this.getTileScale(), this.getTileScale(), 0, 0);
        return coverage;
    }
}
