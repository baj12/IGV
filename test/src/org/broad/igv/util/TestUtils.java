/*
 * Copyright (c) 2007-2012 The Broad Institute, Inc.
 * SOFTWARE COPYRIGHT NOTICE
 * This software and its documentation are the copyright of the Broad Institute, Inc. All rights are reserved.
 *
 * This software is supplied without any warranty or guaranteed support whatsoever. The Broad Institute is not responsible for its use, misuse, or functionality.
 *
 * This software is licensed under the terms of the GNU Lesser General Public License (LGPL),
 * Version 2.1 which is available at http://www.opensource.org/licenses/lgpl-2.1.php.
 */

package org.broad.igv.util;

import org.broad.igv.Globals;
import org.broad.igv.PreferenceManager;
import org.broad.igv.feature.genome.Genome;
import org.broad.igv.tools.IgvTools;
import org.broad.igv.ui.IGV;
import org.broad.igv.ui.Main;
import org.broad.tribble.readers.AsciiLineReader;
import org.broad.tribble.util.ftp.FTPClient;
import org.junit.Ignore;

import javax.swing.*;
import java.awt.*;
import java.io.*;

/**
 * @author jrobinso
 * @date Jul 28, 2010
 */
@Ignore
public class TestUtils {
    public static String DATA_DIR = "test/data/";
    public static String dataFileName = DATA_DIR + "/genomes/hg18.unittest.genome";
    public static String AVAILABLE_FTP_URL = "ftp://ftp.broadinstitute.org/pub/igv/TEST/test.txt";
    public static String UNAVAILABLE_FTP_URL = "ftp://www.example.com/file.txt";
    //This is so ant can set the large data directory
    private static String LARGE_DATA_DIR_KEY = "LARGE_DATA_DIR";
    public static String LARGE_DATA_DIR = "test/largedata/";

    static {
        LARGE_DATA_DIR = System.getProperty(LARGE_DATA_DIR_KEY, LARGE_DATA_DIR);
    }

    private static void setUpTestEnvironment() {
        Globals.setTesting(true);
        PreferenceManager.getInstance().setPrefsFile("testprefs.properties");
        Globals.READ_TIMEOUT = 60 * 1000;
        Globals.CONNECT_TIMEOUT = 60 * 1000;
        FTPClient.READ_TIMEOUT = 60 * 1000;

        //Create output directory if it doesn't exist
        File outDir = new File(DATA_DIR, "out");
        if (!outDir.exists()) {
            outDir.mkdir();
        }
    }

    public static void setUpHeadless() {
        Globals.setHeadless(true);
        setUpTestEnvironment();
    }

    /**
     * This closes the IGV window.
     */
    public static void stopGUI() {
        try {
            IGV igv = IGV.getInstance();
        } catch (RuntimeException e) {
            return;
        }

        IGV.getMainFrame().setVisible(false);
        IGV.getMainFrame().dispose();
    }


    /**
     * Start GUI with default genome file
     *
     * @return
     * @throws IOException
     */
    public static IGV startGUI() throws IOException {
        return startGUI(dataFileName);
    }


    /**
     * Load a gui with the specified genome file.
     * No genome is loaded if null
     *
     * @param genomeFile
     * @return
     * @throws IOException
     */
    public static IGV startGUI(String genomeFile) throws IOException {
        assumeNotHeadless();

        setUpTestEnvironment();
        Globals.setHeadless(false);
        IGV igv;
        //If IGV is already open, we get the instance.
        try {
            igv = IGV.getInstance();
            IGV.getMainFrame().setVisible(true);
            System.out.println("Using old IGV");
        } catch (RuntimeException e) {
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            Main.open(frame);
            System.out.println("Started new IGV");
            igv = IGV.getInstance();
        }
        if (genomeFile != null) {
            igv.loadGenome(genomeFile, null);
        }
        return igv;
    }

    /**
     * Loads the session into IGV. This blocks until the session
     * is loaded.
     *
     * @param igv
     * @param sessionPath
     * @throws InterruptedException
     */
    public static void loadSession(IGV igv, String sessionPath) throws InterruptedException {
        igv.doRestoreSession(sessionPath, null, false);
    }

    public static void assumeNotHeadless() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        boolean headless = ge.isHeadless();
        if (headless) {
            System.out.println("You are trying to start a GUI in a headless environment. Aborting test");
        }
        org.junit.Assume.assumeTrue(!headless);
    }

    /**
     * See TestUtils.createIndex(file, indexType, binSize)
     *
     * @param file
     * @throws IOException
     */
    public static void createIndex(String file) throws IOException {
        createIndex(file, IgvTools.LINEAR_INDEX, IgvTools.LINEAR_BIN_SIZE);
    }

    /**
     * Destroys index file if it exists, and creates new one under
     * the specified parameters
     *
     * @param file
     * @param indexType
     * @param binSize
     * @throws IOException
     */
    public static void createIndex(String file, int indexType, int binSize) throws IOException {
        File indexFile = new File(file + ".idx");
        if (indexFile.exists()) {
            indexFile.delete();
        }
        (new IgvTools()).doIndex(file, indexType, binSize);
        indexFile.deleteOnExit();
    }

    /**
     * Load a test genome, do some test setup
     *
     * @return
     * @throws IOException
     */
    public static Genome loadGenome() throws IOException {
        final String genomeFile = dataFileName;
        return IgvTools.loadGenome(genomeFile, true);
    }

    public static void clearOutputDir() throws IOException {
        File outputDir = new File(DATA_DIR + "/out/");
        if (outputDir.isDirectory()) {
            File[] listFiles = outputDir.listFiles();
            for (File fi : listFiles) {
                //Keep hidden files and directories
                if (!fi.getName().startsWith(".")) {
                    fi.delete();
                }
            }
        }
    }

    /**
     * Returns either 1 or 2, representing the number of
     * bytes used to end a line. Reads only from first line of a file
     * @param filePath
     * @return
     * @throws IOException
     */
    public static int getBytesAtEnd(String filePath) throws IOException{
        InputStream is = new FileInputStream(filePath);
        AsciiLineReader reader = new AsciiLineReader(is);
        String line = reader.readLine();
        int bytesThisLine = (int) reader.getPosition();

        reader.close();

        return bytesThisLine - line.length();

    }
}
