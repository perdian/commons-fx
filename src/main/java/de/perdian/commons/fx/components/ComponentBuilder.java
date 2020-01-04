package de.perdian.commons.fx.components;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import de.perdian.commons.fx.properties.converters.IdentityStringConverter;
import de.perdian.commons.fx.properties.converters.SimpleStringConverter;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.util.StringConverter;

public class ComponentBuilder {

    private List<ComponentBuilderListener> listeners = null;

    public ComponentBuilder() {
        this.setListeners(new CopyOnWriteArrayList<>());
    }

    public ComponentBuilder createChild() {
        ComponentBuilder childBuilder = new ComponentBuilder();
        childBuilder.getListeners().addAll(this.getListeners());
        return childBuilder;
    }

    public Label createLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 80%");
        return label;
    }

    public ComponentBuilderItem<TextField> createTextField(StringProperty property) {
        return this.createTextField(property, new IdentityStringConverter());
    }

    public <T> ComponentBuilderItem<TextField> createTextField(Property<T> property, StringConverter<T> stringConverter) {
        TextField textField = new TextField();
        Bindings.bindBidirectional(textField.textProperty(), property, stringConverter);
        textField.setMinHeight(Node.BASELINE_OFFSET_SAME_AS_HEIGHT);
        textField.focusedProperty().addListener((o, oldValue, newValue) -> { if (newValue.booleanValue()) { Platform.runLater(() -> textField.selectAll()); } });
        return new ComponentBuilderItem<>(this, textField);
    }

    public <T> ComponentBuilderItem<ComboBox<T>> createComboBox(Property<T> property, Function<T, String> valueToStringFunction, List<T> availableValues) {
        ComboBox<T> comboBox = new ComboBox<>(FXCollections.observableArrayList(availableValues));
        comboBox.setConverter(new SimpleStringConverter<>(valueToStringFunction, string -> { throw new UnsupportedOperationException(); }));
        Bindings.bindBidirectional(comboBox.valueProperty(), property);
        return new ComponentBuilderItem<>(this, comboBox);
    }

    public ComponentBuilderItem<ComboBox<String>> createCurrencySelectionComboBox(Property<String> currencyProperty, List<Property<String>> allCurrencyProperties) {
        return this.createCurrencySelectionComboBox(currencyProperty, allCurrencyProperties, new ReadOnlyBooleanWrapper(false));
    }

    public ComponentBuilderItem<ComboBox<String>> createCurrencySelectionComboBox(Property<String> currencyProperty, List<Property<String>> allCurrencyProperties, BooleanExpression disabledExpression) {
        List<String> comboBoxInitialValues = allCurrencyProperties.stream().map(Property::getValue).filter(StringUtils::isNotEmpty).distinct().collect(Collectors.toList());
        ObservableList<String> comboBoxValues = FXCollections.observableArrayList(comboBoxInitialValues);
        allCurrencyProperties.forEach(changedProperty -> changedProperty.addListener((o, oldValue, newValue) -> {
            if (!Objects.equals(oldValue, newValue)) {
                Set<String> consolidatedCurrencies = new LinkedHashSet<>();
                allCurrencyProperties.forEach(property -> {
                    if (property.equals(o)) {
                        if (StringUtils.isNotEmpty(newValue)) {
                            consolidatedCurrencies.add(newValue);
                        }
                    } else {
                        if (StringUtils.isNotEmpty(property.getValue())) {
                            consolidatedCurrencies.add(property.getValue());
                        }
                    }
                });
                for (String consolidatedValue : consolidatedCurrencies) {
                    if (!comboBoxValues.contains(consolidatedValue)) {
                        comboBoxValues.add(consolidatedValue);
                    }
                }
                for (String existingValue : new ArrayList<>(comboBoxValues)) {
                    if (!consolidatedCurrencies.contains(existingValue)) {
                        comboBoxValues.remove(existingValue);
                    }
                }
                if (comboBoxValues.isEmpty()) {
                    allCurrencyProperties.forEach(property -> {
                        if (StringUtils.isNotEmpty(property.getValue())) {
                            comboBoxValues.add(property.getValue());
                        }
                    });
                }
            }
        }));
        ComboBox<String> comboBox = new ComboBox<>(comboBoxValues);
        comboBox.disableProperty().bind(disabledExpression.or(Bindings.size(comboBoxValues).lessThanOrEqualTo(1)));
        Bindings.bindBidirectional(comboBox.valueProperty(), currencyProperty);
        return new ComponentBuilderItem<>(this, comboBox);
    }

    public <R extends Region> ComponentBuilderItem<R> createCustom(R region) {
        return new ComponentBuilderItem<>(this, region);
    }

    public boolean addListener(ComponentBuilderListener listener) {
        return this.getListeners().add(listener);
    }
    public boolean removeListener(ComponentBuilderListener listener) {
        return this.getListeners().remove(listener);
    }
    List<ComponentBuilderListener> getListeners() {
        return this.listeners;
    }
    private void setListeners(List<ComponentBuilderListener> listeners) {
        this.listeners = listeners;
    }

}
