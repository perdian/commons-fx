package de.perdian.commons.fx.properties.converters;

import java.util.function.Function;

import javafx.util.StringConverter;

public class SimpleStringConverter<T> extends StringConverter<T> {

    private Function<T, String> valueToStringFunction = null;
    private Function<String, T> stringToValueFunction = null;

    public SimpleStringConverter(Function<T, String> valueToStringFunction, Function<String, T> stringToValueFunction) {
        this.setValueToStringFunction(valueToStringFunction);
        this.setStringToValueFunction(stringToValueFunction);
    }

    @Override
    public String toString(T object) {
        return this.getValueToStringFunction().apply(object);
    }

    @Override
    public T fromString(String string) {
        return this.getStringToValueFunction().apply(string);
    }

    private Function<T, String> getValueToStringFunction() {
        return this.valueToStringFunction;
    }
    private void setValueToStringFunction(Function<T, String> valueToStringFunction) {
        this.valueToStringFunction = valueToStringFunction;
    }

    private Function<String, T> getStringToValueFunction() {
        return this.stringToValueFunction;
    }
    private void setStringToValueFunction(Function<String, T> stringToValueFunction) {
        this.stringToValueFunction = stringToValueFunction;
    }

}