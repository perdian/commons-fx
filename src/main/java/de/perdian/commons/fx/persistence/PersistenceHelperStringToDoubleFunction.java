package de.perdian.commons.fx.persistence;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.function.Function;

class PersistenceHelperStringToDoubleFunction implements Function<String, Double> {

    private NumberFormat numberFormat = null;

    PersistenceHelperStringToDoubleFunction(NumberFormat numberFormat) {
        this.setNumberFormat(numberFormat);
    }

    @Override
    public Double apply(String stringValue) {
        try {
            return this.getNumberFormat().parse(stringValue).doubleValue();
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid numeric value: " + stringValue, e);
        }
    }

    private NumberFormat getNumberFormat() {
        return this.numberFormat;
    }
    private void setNumberFormat(NumberFormat numberFormat) {
        this.numberFormat = numberFormat;
    }

}
