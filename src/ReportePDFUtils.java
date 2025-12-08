import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import javax.swing.*;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReportePDFUtils {

    // Método genérico para reportes de tablas (Inventario, Clientes, etc.)
    public static void generarReporteTablaPDF(JTable tabla, String titulo, String nombreArchivo) {
        Document documento = new Document();

        try {
            String ruta = System.getProperty("user.home") + "/Desktop/" + nombreArchivo + ".pdf";
            PdfWriter.getInstance(documento, new FileOutputStream(ruta));

            documento.open();

            // Título
            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.DARK_GRAY);
            Paragraph parrafoTitulo = new Paragraph(titulo, fontTitulo);
            parrafoTitulo.setAlignment(Element.ALIGN_CENTER);
            parrafoTitulo.setSpacingAfter(20);
            documento.add(parrafoTitulo);

            // Fecha
            Paragraph parrafoFecha = new Paragraph("Fecha de emisión: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()));
            parrafoFecha.setAlignment(Element.ALIGN_RIGHT);
            parrafoFecha.setSpacingAfter(20);
            documento.add(parrafoFecha);

            // Tabla
            PdfPTable pdfTable = new PdfPTable(tabla.getColumnCount());
            pdfTable.setWidthPercentage(100);

            // Encabezados
            for (int i = 0; i < tabla.getColumnCount(); i++) {
                PdfPCell cell = new PdfPCell(new Phrase(tabla.getColumnName(i), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.setPadding(5);
                pdfTable.addCell(cell);
            }

            // Datos
            for (int rows = 0; rows < tabla.getRowCount(); rows++) {
                for (int cols = 0; cols < tabla.getColumnCount(); cols++) {
                    String valor = tabla.getValueAt(rows, cols) != null ? tabla.getValueAt(rows, cols).toString() : "";
                    PdfPCell cell = new PdfPCell(new Phrase(valor, FontFactory.getFont(FontFactory.HELVETICA, 9)));
                    cell.setPadding(5);
                    pdfTable.addCell(cell);
                }
            }

            documento.add(pdfTable);
            documento.close();

            JOptionPane.showMessageDialog(null, "Reporte PDF generado en el Escritorio:\n" + ruta);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al generar PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Método específico para Facturas
    public static void generarFacturaPDF(String numeroFactura, String cliente, String vehiculo, 
                                         String fecha, double subtotal, double iva, double total, 
                                         String descripcionServicio) {
        Document documento = new Document();
        try {
            String ruta = System.getProperty("user.home") + "/Desktop/Factura_" + numeroFactura + ".pdf";
            PdfWriter.getInstance(documento, new FileOutputStream(ruta));

            documento.open();

            // Logo y Encabezado de la Empresa
            Font fontEmpresa = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, new BaseColor(30, 60, 114));
            Paragraph nombreEmpresa = new Paragraph("LA CASA DEL MOTOR", fontEmpresa);
            nombreEmpresa.setAlignment(Element.ALIGN_CENTER);
            documento.add(nombreEmpresa);
            
            Paragraph datosEmpresa = new Paragraph("Dirección del Taller Mecánico\nTel: 555-1234-5678\nRFC: XXXX010101XX1");
            datosEmpresa.setAlignment(Element.ALIGN_CENTER);
            datosEmpresa.setSpacingAfter(20);
            documento.add(datosEmpresa);

            // Título de Factura
            Paragraph tituloFac = new Paragraph("FACTURA N° " + numeroFactura, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.RED));
            tituloFac.setAlignment(Element.ALIGN_RIGHT);
            documento.add(tituloFac);

            documento.add(new Paragraph(" ")); // Espacio

            // Datos del Cliente y Vehículo
            PdfPTable tablaDatos = new PdfPTable(2);
            tablaDatos.setWidthPercentage(100);
            
            PdfPCell celdaCliente = new PdfPCell();
            celdaCliente.addElement(new Paragraph("CLIENTE:", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
            celdaCliente.addElement(new Paragraph(cliente));
            celdaCliente.setBorder(Rectangle.NO_BORDER);
            
            PdfPCell celdaVehiculo = new PdfPCell();
            celdaVehiculo.addElement(new Paragraph("VEHÍCULO / SERVICIO:", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
            celdaVehiculo.addElement(new Paragraph(vehiculo));
            celdaVehiculo.addElement(new Paragraph("Fecha: " + fecha));
            celdaVehiculo.setBorder(Rectangle.NO_BORDER);

            tablaDatos.addCell(celdaCliente);
            tablaDatos.addCell(celdaVehiculo);
            documento.add(tablaDatos);

            documento.add(new Paragraph(" ")); // Espacio

            // Tabla de Conceptos
            PdfPTable tablaConceptos = new PdfPTable(2);
            tablaConceptos.setWidthPercentage(100);
            tablaConceptos.setWidths(new float[]{3, 1}); // Ancho de columnas

            // Header tabla
            PdfPCell h1 = new PdfPCell(new Phrase("Descripción", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE)));
            h1.setBackgroundColor(new BaseColor(30, 60, 114));
            h1.setPadding(8);
            tablaConceptos.addCell(h1);

            PdfPCell h2 = new PdfPCell(new Phrase("Importe", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE)));
            h2.setBackgroundColor(new BaseColor(30, 60, 114));
            h2.setPadding(8);
            h2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tablaConceptos.addCell(h2);

            // Fila del servicio principal (en un sistema real iterarías sobre detalles)
            PdfPCell cDesc = new PdfPCell(new Phrase(descripcionServicio));
            cDesc.setPadding(10);
            tablaConceptos.addCell(cDesc);

            PdfPCell cTotal = new PdfPCell(new Phrase("$ " + String.format("%.2f", subtotal)));
            cTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cTotal.setPadding(10);
            tablaConceptos.addCell(cTotal);

            documento.add(tablaConceptos);

            // Totales
            PdfPTable tablaTotales = new PdfPTable(2);
            tablaTotales.setWidthPercentage(100);
            tablaTotales.setWidths(new float[]{3, 1});

            tablaTotales.addCell(getCellSinBorde("Subtotal:", Element.ALIGN_RIGHT));
            tablaTotales.addCell(getCellSinBorde("$ " + String.format("%.2f", subtotal), Element.ALIGN_RIGHT));

            tablaTotales.addCell(getCellSinBorde("IVA (16%):", Element.ALIGN_RIGHT));
            tablaTotales.addCell(getCellSinBorde("$ " + String.format("%.2f", iva), Element.ALIGN_RIGHT));

            PdfPCell totalLabel = new PdfPCell(new Phrase("TOTAL:", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
            totalLabel.setBorder(Rectangle.TOP);
            totalLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalLabel.setPaddingTop(5);
            tablaTotales.addCell(totalLabel);

            PdfPCell totalVal = new PdfPCell(new Phrase("$ " + String.format("%.2f", total), FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
            totalVal.setBorder(Rectangle.TOP);
            totalVal.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalVal.setPaddingTop(5);
            tablaTotales.addCell(totalVal);

            documento.add(tablaTotales);

            documento.close();
            JOptionPane.showMessageDialog(null, "Factura PDF generada exitosamente en el Escritorio.");

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error PDF: " + e.getMessage());
        }
    }

    private static PdfPCell getCellSinBorde(String texto, int alineacion) {
        PdfPCell cell = new PdfPCell(new Phrase(texto));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(alineacion);
        cell.setPadding(3);
        return cell;
    }
}