import javax.swing.*;
import javax.swing.table.TableModel;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.Component; // <--- ¡CORRECCIÓN AÑADIDA!

public class ExportarUtils {

    public static void exportarTablaACSV(JTable tabla, Component parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar como CSV");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
        // Sugerir nombre de archivo
        fileChooser.setSelectedFile(new File("reporte_datos.csv"));

        int userSelection = fileChooser.showSaveDialog(parent);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".csv")) {
                filePath += ".csv";
                fileToSave = new File(filePath);
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {
                TableModel model = tabla.getModel();
                
                // Escribir encabezados
                for (int i = 0; i < model.getColumnCount(); i++) {
                    writer.write(model.getColumnName(i));
                    if (i < model.getColumnCount() - 1) writer.write(",");
                }
                writer.newLine();

                // Escribir filas
                for (int i = 0; i < model.getRowCount(); i++) {
                    for (int j = 0; j < model.getColumnCount(); j++) {
                        Object value = model.getValueAt(i, j);
                        String text = (value == null) ? "" : value.toString();
                        // Escapar comillas y comas para CSV
                        text = text.replace("\"", "\"\"");
                        if (text.contains(",") || text.contains("\n")) {
                            text = "\"" + text + "\"";
                        }
                        writer.write(text);
                        if (j < model.getColumnCount() - 1) writer.write(",");
                    }
                    writer.newLine();
                }

                JOptionPane.showMessageDialog(parent, 
                    "Datos exportados exitosamente a:\n" + filePath, 
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException e) {
                JOptionPane.showMessageDialog(parent, 
                    "Error al guardar el archivo: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
}