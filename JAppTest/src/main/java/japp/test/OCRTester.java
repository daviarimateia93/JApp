package japp.test;

import java.io.File;

import com.asprise.ocr.Ocr;

public class OCRTester {

    public OCRTester() {
        // http://asprise.com/royalty-free-library/java-ocr-source-code-examples-demos.html

        Ocr.setUp(); // one time setup
        Ocr ocr = new Ocr(); // create a new OCR engine
        ocr.startEngine("eng", Ocr.SPEED_FASTEST); // Portuguese
        String s = ocr.recognize(
                new File[] { new File(getClass().getClassLoader().getResource("carro_placa.jpg").getPath()) },
                Ocr.RECOGNIZE_TYPE_ALL, Ocr.OUTPUT_FORMAT_PLAINTEXT); // PLAINTEXT | XML | PDF | RTF
        System.out.println("Result: " + s);
        ocr.stopEngine();
    }
}
