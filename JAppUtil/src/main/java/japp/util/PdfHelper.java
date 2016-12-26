package japp.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.tidy.Tidy;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.lowagie.text.DocumentException;

public abstract class PdfHelper {
	
	private static final String CHARSET_UTF_8 = "UTF-8";
	
	protected PdfHelper() {
		
	}
	
	public static void convert(final String input, final OutputStream outputStream) throws DocumentException, ParserConfigurationException, TransformerException, UnsupportedEncodingException {
		convert(new ByteArrayInputStream(ByteHelper.toBytes(input)), outputStream);
	}
	
	public static void convert(final InputStream inputStream, final OutputStream outputStream) throws DocumentException, ParserConfigurationException, TransformerException {
		final Tidy tidy = new Tidy();
		tidy.setInputEncoding(CHARSET_UTF_8);
		tidy.setOutputEncoding(CHARSET_UTF_8);
		
		final Document document = tidy.parseDOM(inputStream, null);
		
		final ITextRenderer textRenderer = new ITextRenderer();
		textRenderer.setDocument(document, null);
		textRenderer.layout();
		textRenderer.createPDF(outputStream);
	}
}
