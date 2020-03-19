package com.china.fortune.thirdTools;

import com.china.fortune.global.Log;
import com.china.fortune.os.file.PathUtils;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

import java.io.FileOutputStream;

public class PdfHelper {
	private Document document = new Document();
	private PdfWriter writer = null;

	public Document getDocument() {
		return document;
	}

	public boolean open(String sFile) {
		boolean rs = false;
		try {
			String sPath = PathUtils.getParentPath(sFile, false);
			PathUtils.create(sPath);
			writer = PdfWriter.getInstance(document, new FileOutputStream(sFile));
			document.open();
			rs = true;
		} catch (Exception e) {
			Log.logException(e);
		}
		return rs;
	}

	public void close() {
		document.close();
		if (writer != null) {
			writer.close();
		}
	}

	public void addParagraphIndent(String sText, int align, Font font) {
		addParagraph("　　" + sText, align, font);
	}

	public void addParagraphEnterIndent(String sText, int align, Font font) {
		addParagraph("\n　　" + sText, align, font);
	}

	public void addParagraphEnter(String sText, int align, Font font) {
		addParagraph("\n" + sText, align, font);
	}

	public void addParagraphEnter() {
		Paragraph paragraph = new Paragraph("\n");
		try {
			document.add(paragraph);
		} catch (Exception e) {
			Log.logException(e);
		}
	}

	public void addParagraph(String sText, int align, Font font) {
		Paragraph paragraph = new Paragraph(sText, font);
		paragraph.setAlignment(align);
		try {
			document.add(paragraph);
		} catch (Exception e) {
			Log.logException(e);
		}
	}

	public void addLineSeparator() {
		Paragraph p2 = new Paragraph();
		p2.add(new Chunk(new LineSeparator()));
		try {
			document.add(p2);
		} catch (Exception e) {
			Log.logException(e);
		}
	}

	static public BaseFont createFont(String name, String encoding, boolean embedded) {
		BaseFont bf = null;
		try {
			bf = BaseFont.createFont(name, encoding, embedded, true, null, null, false);
		} catch (Exception e) {
			Log.logException(e);
		}
		return bf;
	}
}
