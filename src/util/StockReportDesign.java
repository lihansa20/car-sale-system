package util;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.engine.design.JRDesignLine;
import net.sf.jasperreports.engine.design.JRDesignParameter;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JRDesignSection;
import net.sf.jasperreports.engine.design.JRDesignStaticText;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.design.JRDesignVariable;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.type.CalculationEnum;
import net.sf.jasperreports.engine.type.HorizontalTextAlignEnum;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.type.OrientationEnum;
import net.sf.jasperreports.engine.type.VerticalTextAlignEnum;
import net.sf.jasperreports.engine.type.WhenNoDataTypeEnum;

import java.awt.Color;
import java.math.BigDecimal;


public class StockReportDesign {

    private static final Color BLUE = new Color(25, 118, 210);
    private static final Color GREEN = new Color(46, 125, 50);
    private static final Color MUTED = new Color(108, 117, 125);
    private static final Color LINE = new Color(218, 222, 228);

    private static final String[] HEADINGS =
            {"ID", "Brand", "Model", "Year", "Color", "Fuel", "Cost (Rs.)", "Listed (Rs.)", "Status"};
    private static final int[] WIDTHS = {40, 100, 110, 55, 80, 80, 110, 110, 117};
    private static final String[] EXPRESSIONS =
            {"$F{id}", "$F{brand}", "$F{model}", "$F{year}", "$F{color}",
                    "$F{fuel_type}", "$F{cost_price}", "$F{selling_price}", "$F{status}"};
    private static final String[] PATTERNS =
            {"0", null, null, "0", null, null, "#,##0.00", "#,##0.00", null};
    private static final HorizontalTextAlignEnum[] ALIGNS = {
            HorizontalTextAlignEnum.LEFT, HorizontalTextAlignEnum.LEFT,
            HorizontalTextAlignEnum.LEFT, HorizontalTextAlignEnum.LEFT,
            HorizontalTextAlignEnum.LEFT, HorizontalTextAlignEnum.LEFT,
            HorizontalTextAlignEnum.RIGHT, HorizontalTextAlignEnum.RIGHT,
            HorizontalTextAlignEnum.LEFT};

    private static final int CONTENT_WIDTH = 802;

    private StockReportDesign() {
    }

    public static JasperDesign build() throws JRException {
        JasperDesign design = new JasperDesign();
        design.setName("stock_report");
        design.setLanguage("java");

        design.setOrientation(OrientationEnum.LANDSCAPE);
        design.setPageWidth(842);
        design.setPageHeight(595);
        design.setColumnWidth(CONTENT_WIDTH);
        design.setLeftMargin(20);
        design.setRightMargin(20);
        design.setTopMargin(20);
        design.setBottomMargin(20);
        design.setWhenNoDataType(WhenNoDataTypeEnum.ALL_SECTIONS_NO_DETAIL);

        addParameter(design, "STATUS", String.class);
        addParameter(design, "GENERATED_BY", String.class);

        JRDesignQuery query = new JRDesignQuery();
        query.setText("SELECT id, brand, model, year, color, fuel_type, "
                + "cost_price, selling_price, status "
                + "FROM cars "
                + "WHERE $P{STATUS} = 'All' OR status = $P{STATUS} "
                + "ORDER BY brand, model, id");
        design.setQuery(query);

        addField(design, "id", Integer.class);
        addField(design, "brand", String.class);
        addField(design, "model", String.class);
        addField(design, "year", Integer.class);
        addField(design, "color", String.class);
        addField(design, "fuel_type", String.class);
        addField(design, "cost_price", BigDecimal.class);
        addField(design, "selling_price", BigDecimal.class);
        addField(design, "status", String.class);

        addSumVariable(design, "TOTAL_COST", "$F{cost_price}");
        addSumVariable(design, "TOTAL_LISTED", "$F{selling_price}");
        addSumVariable(design, "POTENTIAL_PROFIT", "$F{selling_price}.subtract($F{cost_price})");

        design.setTitle(buildTitle());
        design.setColumnHeader(buildColumnHeader());
        ((JRDesignSection) design.getDetailSection()).addBand(buildDetail());
        design.setPageFooter(buildPageFooter());
        design.setSummary(buildSummary());

        return design;
    }


    private static JRDesignBand buildTitle() {
        JRDesignBand band = new JRDesignBand();
        band.setHeight(96);

        band.addElement(staticText("Car Sale System", 0, 0, CONTENT_WIDTH, 30,
                22, true, BLUE, null, HorizontalTextAlignEnum.LEFT));
        band.addElement(staticText("Car Stock Report", 0, 30, CONTENT_WIDTH, 22,
                14, true, Color.BLACK, null, HorizontalTextAlignEnum.LEFT));

        band.addElement(textField("\"Cars shown: \" + $P{STATUS}",
                0, 56, CONTENT_WIDTH, 16, 10, false, MUTED, null,
                HorizontalTextAlignEnum.LEFT, null));
        band.addElement(textField("\"Generated by: \" + $P{GENERATED_BY} + \" on \" + java.time.LocalDate.now()",
                0, 72, CONTENT_WIDTH, 16, 10, false, MUTED, null,
                HorizontalTextAlignEnum.LEFT, null));
        return band;
    }

    private static JRDesignBand buildColumnHeader() {
        JRDesignBand band = new JRDesignBand();
        band.setHeight(26);

        int x = 0;
        for (int i = 0; i < HEADINGS.length; i++) {
            band.addElement(staticText(HEADINGS[i], x, 0, WIDTHS[i], 26,
                    10, true, Color.WHITE, BLUE, ALIGNS[i]));
            x += WIDTHS[i];
        }
        return band;
    }

    private static JRDesignBand buildDetail() {
        JRDesignBand band = new JRDesignBand();
        band.setHeight(22);

        int x = 0;
        for (int i = 0; i < EXPRESSIONS.length; i++) {
            band.addElement(textField(EXPRESSIONS[i], x, 0, WIDTHS[i], 21,
                    10, false, Color.BLACK, null, ALIGNS[i], PATTERNS[i]));
            x += WIDTHS[i];
        }

        JRDesignLine line = new JRDesignLine();
        line.setX(0);
        line.setY(21);
        line.setWidth(CONTENT_WIDTH);
        line.setHeight(1);
        line.setForecolor(LINE);
        band.addElement(line);
        return band;
    }

    private static JRDesignBand buildPageFooter() {
        JRDesignBand band = new JRDesignBand();
        band.setHeight(24);

        band.addElement(textField("\"Page \" + $V{PAGE_NUMBER}",
                0, 4, CONTENT_WIDTH, 16, 9, false, MUTED, null,
                HorizontalTextAlignEnum.RIGHT, null));
        return band;
    }

    private static JRDesignBand buildSummary() {
        JRDesignBand band = new JRDesignBand();
        band.setHeight(64);

        JRDesignLine line = new JRDesignLine();
        line.setX(0);
        line.setY(6);
        line.setWidth(CONTENT_WIDTH);
        line.setHeight(1);
        line.setForecolor(BLUE);
        band.addElement(line);

        band.addElement(textField("\"Number of cars: \" + $V{REPORT_COUNT}",
                0, 14, 300, 20, 11, true, Color.BLACK, null,
                HorizontalTextAlignEnum.LEFT, null));

        // the totals sit under the Cost, Listed and Status columns
        band.addElement(staticText("Total cost", 465, 14, 110, 18,
                10, false, MUTED, null, HorizontalTextAlignEnum.RIGHT));
        band.addElement(staticText("Total listed price", 575, 14, 110, 18,
                10, false, MUTED, null, HorizontalTextAlignEnum.RIGHT));
        band.addElement(staticText("Potential profit", 685, 14, 117, 18,
                10, false, MUTED, null, HorizontalTextAlignEnum.RIGHT));

        band.addElement(textField("$V{TOTAL_COST}", 465, 32, 110, 20,
                11, true, Color.BLACK, null, HorizontalTextAlignEnum.RIGHT, "#,##0.00"));
        band.addElement(textField("$V{TOTAL_LISTED}", 575, 32, 110, 20,
                11, true, Color.BLACK, null, HorizontalTextAlignEnum.RIGHT, "#,##0.00"));
        band.addElement(textField("$V{POTENTIAL_PROFIT}", 685, 32, 117, 20,
                11, true, GREEN, null, HorizontalTextAlignEnum.RIGHT, "#,##0.00"));
        return band;
    }


    private static void addParameter(JasperDesign design, String name, Class<?> type) throws JRException {
        JRDesignParameter parameter = new JRDesignParameter();
        parameter.setName(name);
        parameter.setValueClass(type);
        design.addParameter(parameter);
    }

    private static void addField(JasperDesign design, String name, Class<?> type) throws JRException {
        JRDesignField field = new JRDesignField();
        field.setName(name);
        field.setValueClass(type);
        design.addField(field);
    }

    private static void addSumVariable(JasperDesign design, String name, String expression) throws JRException {
        JRDesignVariable variable = new JRDesignVariable();
        variable.setName(name);
        variable.setValueClass(BigDecimal.class);
        variable.setCalculation(CalculationEnum.SUM);
        variable.setExpression(new JRDesignExpression(expression));
        design.addVariable(variable);
    }

    private static JRDesignStaticText staticText(String text, int x, int y, int width, int height,
                                                 float size, boolean bold, Color color, Color backColor,
                                                 HorizontalTextAlignEnum align) {
        JRDesignStaticText element = new JRDesignStaticText();
        element.setText(text);
        element.setX(x);
        element.setY(y);
        element.setWidth(width);
        element.setHeight(height);
        element.setFontSize(size);
        element.setBold(bold);
        element.setForecolor(color);
        if (backColor != null) {
            element.setMode(ModeEnum.OPAQUE);
            element.setBackcolor(backColor);
        }
        element.setHorizontalTextAlign(align);
        element.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
        element.getLineBox().setLeftPadding(4);
        element.getLineBox().setRightPadding(4);
        return element;
    }

    private static JRDesignTextField textField(String expression, int x, int y, int width, int height,
                                               float size, boolean bold, Color color, Color backColor,
                                               HorizontalTextAlignEnum align, String pattern) {
        JRDesignTextField element = new JRDesignTextField();
        element.setExpression(new JRDesignExpression(expression));
        element.setX(x);
        element.setY(y);
        element.setWidth(width);
        element.setHeight(height);
        element.setFontSize(size);
        element.setBold(bold);
        element.setForecolor(color);
        if (backColor != null) {
            element.setMode(ModeEnum.OPAQUE);
            element.setBackcolor(backColor);
        }
        element.setHorizontalTextAlign(align);
        element.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
        element.getLineBox().setLeftPadding(4);
        element.getLineBox().setRightPadding(4);
        element.setBlankWhenNull(true);
        if (pattern != null) {
            element.setPattern(pattern);
        }
        return element;
    }
}