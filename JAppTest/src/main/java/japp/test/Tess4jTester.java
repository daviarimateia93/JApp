package japp.test;

import java.io.File;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.util.LoadLibs;

public class Tess4jTester {

    public Tess4jTester() {
        File imageFile = new File(getClass().getClassLoader().getResource("times_new_roman_fundo.jpg").getPath());
        // ImageIO.scanForPlugins(); // for server environment
        // imageFile = new File("eurotext.tif");
        ITesseract instance = new Tesseract(); // JNA Interface Mapping
        // ITesseract instance = new Tesseract1(); // JNA Direct Mapping
        File tessDataFolder = LoadLibs.extractTessResources("tessdata"); // Maven build only; only English data bundled
        instance.setDatapath(tessDataFolder.getAbsolutePath());

        try {
            String result = instance.doOCR(imageFile);
            System.out.println(result);
        } catch (final TesseractException e) {
            System.err.println(e.getMessage());
        }
    }
}
