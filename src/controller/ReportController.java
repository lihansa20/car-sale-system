package controller;

import database.DBConnection;
import model.User;
import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import util.SalesReportDesign;
import util.StockReportDesign;
import util.ValidationException;
import util.Validator;

import java.io.File;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

public class ReportController {

    // each report is compiled once (it takes a few seconds) and then reused
    private static JasperReport compiledSalesReport;
    private static JasperReport compiledStockReport;

    static {
        // use a font every computer has, so no extra font files are needed
        DefaultJasperReportsContext context = DefaultJasperReportsContext.getInstance();
        context.setProperty("net.sf.jasperreports.awt.ignore.missing.font", "true");
        context.setProperty("net.sf.jasperreports.default.font.name", "SansSerif");
    }

    // ---------- sales report ----------

    /**
     * Creates the sales report for the given dates and saves it as a PDF file.
     * The dates are typed as text (yyyy-MM-dd).
     */
    public void exportSalesReport(String fromText, String toText, File outputFile)
            throws ValidationException, SQLException, JRException {

        LocalDate from = parseDate(fromText, "From date");
        LocalDate to = parseDate(toText, "To date");

        if (from.isAfter(to)) {
            throw new ValidationException("The From date cannot be after the To date.");
        }

        Map<String, Object> params = new HashMap<>();
        params.put("FROM_DATE", Date.valueOf(from));
        params.put("TO_DATE", Date.valueOf(to));
        params.put("GENERATED_BY", generatedBy());

        export(getSalesReport(), params, outputFile);
    }

    // ---------- stock report ----------

    /**
     * Creates the car stock report and saves it as a PDF file.
     * status must be "All", "Available" or "Sold".
     */
    public void exportStockReport(String status, File outputFile)
            throws ValidationException, SQLException, JRException {

        if (!"All".equals(status) && !"Available".equals(status) && !"Sold".equals(status)) {
            throw new ValidationException("Please choose which cars to include (All, Available or Sold).");
        }

        Map<String, Object> params = new HashMap<>();
        params.put("STATUS", status);
        params.put("GENERATED_BY", generatedBy());

        export(getStockReport(), params, outputFile);
    }

    // ---------- helpers ----------

    // fills the report with data from MySQL and saves it as a PDF
    private void export(JasperReport report, Map<String, Object> params, File outputFile)
            throws SQLException, JRException {

        Connection con = DBConnection.getInstance().getConnection();
        JasperPrint print = JasperFillManager.fillReport(report, params, con);
        JasperExportManager.exportReportToPdfFile(print, outputFile.getAbsolutePath());
    }

    private String generatedBy() {
        User user = AuthController.getCurrentUser();
        return (user == null) ? "Unknown" : user.getFullName();
    }

    private static synchronized JasperReport getSalesReport() throws JRException {
        if (compiledSalesReport == null) {
            compiledSalesReport = JasperCompileManager.compileReport(SalesReportDesign.build());
        }
        return compiledSalesReport;
    }

    private static synchronized JasperReport getStockReport() throws JRException {
        if (compiledStockReport == null) {
            compiledStockReport = JasperCompileManager.compileReport(StockReportDesign.build());
        }
        return compiledStockReport;
    }

    private LocalDate parseDate(String text, String fieldName) throws ValidationException {
        Validator.requireText(text, fieldName);
        try {
            return LocalDate.parse(text.trim());
        } catch (DateTimeParseException e) {
            throw new ValidationException(fieldName + " must be a valid date in the format yyyy-MM-dd.");
        }
    }
}