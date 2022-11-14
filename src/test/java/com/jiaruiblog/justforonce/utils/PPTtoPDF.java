package com.jiaruiblog.justforonce.utils;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.hslf.usermodel.HSLFSlide;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class PPTtoPDF {

    public boolean convertPPTToPDF(File file, File toFile) {
        try {
            Document pdfDocument = new Document();
            PdfWriter pdfWriter = PdfWriter.getInstance(pdfDocument, new FileOutputStream(toFile));
            FileInputStream is = new FileInputStream(file);
            HSLFSlideShow hslfSlideShow = convertPPTToPDFByPPT(is);
            double zoom = 2;
            // HSLFSlideShow 用于ppt文件，XMLSlideShow 用于pptx文件。
            if (hslfSlideShow == null) {
                is = new FileInputStream(file);
                XMLSlideShow ppt = convertPPTToPDFByPPTX(is);
                if (ppt == null) {
                    throw new NullPointerException("This PPTX get data is error....");
                }
                Dimension pgsize = ppt.getPageSize();
                List<XSLFSlide> slide = ppt.getSlides();
                AffineTransform at = new AffineTransform();
                at.setToScale(zoom, zoom);
                pdfDocument.setPageSize(new Rectangle((float) pgsize.getWidth(), (float) pgsize.getHeight()));
                pdfWriter.open();
                pdfDocument.open();
                PdfPTable table = new PdfPTable(1);
                for (XSLFSlide xslfSlide : slide) {
                    BufferedImage img = new BufferedImage(
                            (int) Math.ceil(pgsize.width * zoom),
                            (int) Math.ceil(pgsize.height * zoom),
                            BufferedImage.TYPE_INT_RGB);
                    Graphics2D graphics = img.createGraphics();
                    graphics.setTransform(at);

                    graphics.setPaint(Color.white);
                    graphics.fill(new Rectangle2D.Float(0, 0, pgsize.width, pgsize.height));

                    xslfSlide.draw(graphics);
                    graphics.getPaint();
                    Image slideImage = Image.getInstance(img, null);

                    table.addCell(new PdfPCell(slideImage, true));
                }
                ppt.close();
                pdfDocument.add(table);
                pdfDocument.close();
                pdfWriter.close();
                System.out.println(file.getAbsolutePath() + "Powerpoint file converted to PDF successfully");
                return true;
            }

            Dimension pgsize = hslfSlideShow.getPageSize();
            List<HSLFSlide> slides = hslfSlideShow.getSlides();
            pdfDocument.setPageSize(new Rectangle((float) pgsize.getWidth(), (float) pgsize.getHeight()));
            pdfWriter.open();
            pdfDocument.open();
            AffineTransform at = new AffineTransform();
            PdfPTable table = new PdfPTable(1);
            for (HSLFSlide hslfSlide : slides) {
                BufferedImage img = new BufferedImage((int) Math.ceil(pgsize.width * zoom), (int) Math.ceil(pgsize.height * zoom), BufferedImage.TYPE_INT_RGB);
                Graphics2D graphics = img.createGraphics();
                graphics.setTransform(at);

                graphics.setPaint(Color.white);
                graphics.fill(new Rectangle2D.Float(0, 0, pgsize.width, pgsize.height));
                hslfSlide.draw(graphics);
                graphics.getPaint();
                Image slideImage = Image.getInstance(img, null);
                table.addCell(new PdfPCell(slideImage, true));
            }
            hslfSlideShow.close();
            pdfDocument.add(table);
            pdfDocument.close();
            pdfWriter.close();
            System.out.println(file.getAbsolutePath() + "Powerpoint file converted to PDF successfully");
            return true;
        } catch (Exception e) {
            System.out.println(file.getAbsolutePath() + "--->" + e.getMessage());
            return false;
        }
    }

    private XMLSlideShow convertPPTToPDFByPPTX(FileInputStream is) {
        try {
            return new XMLSlideShow(is);
        } catch (IOException e) {
            return null;
        }
    }

    private HSLFSlideShow convertPPTToPDFByPPT(FileInputStream is) {
        try {
            return new HSLFSlideShow(is);
        } catch (Exception e) {
            return null;
        }
    }
}
