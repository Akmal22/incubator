package kz.incubator.ui.view.util;

import kz.incubator.ui.view.dto.ShortReport;
import com.itextpdf.awt.PdfGraphics2D;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.ImgTemplate;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import com.vaadin.flow.component.charts.export.SVGGenerator;
import lombok.experimental.UtilityClass;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.dom.svg.SVGDocumentFactory;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.svg.SVGDocument;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@UtilityClass
public class ReportPdfWriter {
    public static File exportShortReport(ShortReport shortReport, String fileName) {
        Document document = new Document();
        document.addTitle("Short report");
        document.addCreator("BIA");
        File file = null;
        try {
            file = createTmpFile(fileName);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();
            writePdfContent(document, writer, shortReport);
            document.close();
        } catch (DocumentException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return file;
    }

    private static File createTmpFile(String filename) {
        File file = null;
        try {
            file = File.createTempFile(filename, ".pdf");
            file.deleteOnExit();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    private void writePdfContent(Document document, PdfWriter writer, ShortReport shortReport) throws DocumentException, IOException, InterruptedException {
        Paragraph caption = new Paragraph();
        caption.add(new Chunk("SHORT REPORT"));
        document.add(caption);

        Paragraph br = new Paragraph(Chunk.NEWLINE);
        document.add(br);

        Paragraph founded = new Paragraph();
        founded.add(new Chunk("Founded: " + shortReport.getFounded()));
        document.add(founded);

        Paragraph founder = new Paragraph();
        founder.add(new Chunk("Founder: " + shortReport.getFounder()));
        document.add(founder);

        if (isNotBlank(shortReport.getProjectName())) {
            Paragraph projectName = new Paragraph();
            founder.add(new Chunk("Project name: " + shortReport.getProjectName()));
            document.add(projectName);
        }
        document.add(br);
        SVGGenerator svgGenerators = new SVGGenerator();
        document.add(createSvgImage(writer.getDirectContent(), svgGenerators.generate(shortReport.getIncomeChart().getConfiguration()), 400, 400));
        document.add(br);
        document.newPage();
        document.add(createSvgImage(writer.getDirectContent(), svgGenerators.generate(shortReport.getApplicationChart().getConfiguration()), 400, 400));
    }

    private static Image createSvgImage(PdfContentByte contentByte, String svgStr,
                                        float maxPointWidth, float maxPointHeight) throws IOException {
        Image image = drawUnscaledSvg(contentByte, svgStr);
//        image.scaleToFit(maxPointWidth, maxPointHeight);
//        image.setAlignment(ALIGN_TOP);
        return image;
    }

    private static Image drawUnscaledSvg(PdfContentByte contentByte, String svgStr)
            throws IOException {

        // First, lets create a graphics node for the SVG image.
        GraphicsNode imageGraphics = buildBatikGraphicsNode(svgStr);

        // SVG's width and height
        float width = (float) imageGraphics.getBounds().getWidth();
        float height = (float) imageGraphics.getBounds().getHeight();

        // Create a PDF template for the SVG image
        PdfTemplate template = contentByte.createTemplate(width, height);
        // Create Graphics2D rendered object from the template
        Graphics2D graphics = new PdfGraphics2D(contentByte, width, height);
        try {
            // SVGs can have their corner at coordinates other than (0,0).
            Rectangle2D bounds = imageGraphics.getBounds();
            graphics.translate(bounds.getX(), bounds.getY());

            // Paint SVG GraphicsNode with the 2d-renderer.
            imageGraphics.paint(graphics);

            // Create and return a iText Image element that contains the SVG
            // image.
            return new ImgTemplate(template);
        } catch (BadElementException e) {
            throw new RuntimeException("Couldn't generate PDF from SVG", e);
        } finally {
            // Manual cleaning (optional)
            graphics.dispose();
        }
    }

    private static GraphicsNode buildBatikGraphicsNode(String svg) throws IOException {
        UserAgent agent = new UserAgentAdapter();

        SVGDocument svgDoc = createSVGDocument(svg, agent);

        DocumentLoader loader = new DocumentLoader(agent);
        BridgeContext bridgeContext = new BridgeContext(agent, loader);
        bridgeContext.setDynamicState(BridgeContext.STATIC);

        GVTBuilder builder = new GVTBuilder();

        return builder.build(bridgeContext, svgDoc);
    }

    private SVGDocument createSVGDocument(String svg, UserAgent agent)
            throws IOException {
        SVGDocumentFactory documentFactory = new SAXSVGDocumentFactory(
                agent.getXMLParserClassName(), true);

        return documentFactory.createSVGDocument(null,
                new StringReader(svg));
    }
}
