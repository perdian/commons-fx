package de.perdian.commons.fx.persistence;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Optional;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

public class PersistenceHelper {

    public static <T> void appendAttribute(Element targetElement, String attributeName, String value) {
        if (StringUtils.isNotEmpty(value)) {
            targetElement.setAttribute(attributeName, value);
        }
    }

    public static <T> void appendAttribute(Element targetElement, String attributeName, LocalDate date) {
        if (date != null) {
            PersistenceHelper.appendAttribute(targetElement, attributeName, date.toString());
        }
    }

    public static <T> void appendAttribute(Element targetElement, String attributeName, Number number, NumberFormat numberFormat) {
        if (number != null) {
            PersistenceHelper.appendAttribute(targetElement, attributeName, numberFormat.format(number.doubleValue()));
        }
    }

    public static <T> Optional<T> extractAttribute(Element element, String attributeName, Function<String, T> stringConverterFunction) {
        String stringValue = element.getAttribute(attributeName);
        if (StringUtils.isEmpty(stringValue)) {
            return Optional.empty();
        } else {
            return Optional.ofNullable(stringConverterFunction.apply(stringValue));
        }
    }

    public static Optional<String> extractAttributeString(Element element, String attributeName) {
        return PersistenceHelper.extractAttribute(element, attributeName, Function.identity());
    }

    public static <E extends Enum<E>> Optional<E> extractAttributeEnum(Element element, String attributeName, Class<E> enumClass) {
        return PersistenceHelper.extractAttributeString(element, attributeName).map(stringValue -> Enum.valueOf(enumClass, stringValue));
    }

    public static Optional<LocalDate> extractAttributeDate(Element transactionElement, String attributeName) {
        return PersistenceHelper.extractAttribute(transactionElement, attributeName, LocalDate::parse);
    }

    public static Optional<Double> extractAttributeDouble(Element transactionElement, String attributeName, NumberFormat numberFormat) {
        return PersistenceHelper.extractAttribute(transactionElement, attributeName, new PersistenceHelperStringToDoubleFunction(numberFormat));
    }

}
