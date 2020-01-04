package de.perdian.commons.fx.preferences.persistence;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;

import de.perdian.commons.fx.persistence.PersistenceHelper;

public class PersistenceHelperTest {

    @Test
    public void appendAttributeString() {
        Element targetElement = this.createElement("foo");
        PersistenceHelper.appendAttribute(targetElement, "bar", "baz");
        Assertions.assertEquals("baz", targetElement.getAttribute("bar"));
    }

    @Test
    public void appendAttributeStringEmpty() {
        Element targetElement = this.createElement("foo");
        PersistenceHelper.appendAttribute(targetElement, "bar", "");
        Assertions.assertFalse(targetElement.hasAttribute("bar"));
    }

    @Test
    public void appendAttributeStringNull() {
        Element targetElement = this.createElement("foo");
        PersistenceHelper.appendAttribute(targetElement, "bar", (String)null);
        Assertions.assertFalse(targetElement.hasAttribute("bar"));
    }

    @Test
    public void appendAttributeDate() {
        Element targetElement = this.createElement("foo");
        PersistenceHelper.appendAttribute(targetElement, "bar", LocalDate.of(2020, 1, 1));
        Assertions.assertEquals("2020-01-01", targetElement.getAttribute("bar"));
    }

    @Test
    public void appendAttributeDateNull() {
        Element targetElement = this.createElement("foo");
        PersistenceHelper.appendAttribute(targetElement, "bar", (LocalDate)null);
        Assertions.assertFalse(targetElement.hasAttribute("bar"));
    }

    @Test
    public void appendAttributeNumber() {
        Element targetElement = this.createElement("foo");
        PersistenceHelper.appendAttribute(targetElement, "bar", Double.valueOf(1.234), new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.GERMANY)));
        Assertions.assertEquals("1,23", targetElement.getAttribute("bar"));
    }

    @Test
    public void appendAttributeNumberNull() {
        Element targetElement = this.createElement("foo");
        PersistenceHelper.appendAttribute(targetElement, "bar", (Double)null, new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.GERMANY)));
        Assertions.assertFalse(targetElement.hasAttribute("bar"));
    }

    private Element createElement(String name) {
        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument().createElement(name);
        } catch (ParserConfigurationException e) {
            throw new IllegalArgumentException("Cannot create DocumentBuilder", e);
        }
    }

}
