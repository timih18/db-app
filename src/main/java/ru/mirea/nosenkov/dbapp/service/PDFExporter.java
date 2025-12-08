package ru.mirea.nosenkov.dbapp.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

public class PDFExporter {
    public static void exportToPDF(String path, String tableName, List<String> columns, List<Map<String, String>> rows) throws FileNotFoundException, DocumentException {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(path));
        document.open();

        document.add(new Paragraph("Table: " + tableName, new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD)));
        document.add(new Paragraph("\n"));

        PdfPTable table = new PdfPTable(columns.size());
        for (String column : columns) {
            table.addCell(new PdfPCell(new Phrase(column, new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD))));
        }

        for (Map<String, String> row : rows) {
            for (String column : columns) {
                String value = row.getOrDefault(column, "");
                table.addCell(new Phrase(value, new Font(Font.FontFamily.HELVETICA, 10)));
            }
        }

        document.add(table);
        document.close();
    }
}
