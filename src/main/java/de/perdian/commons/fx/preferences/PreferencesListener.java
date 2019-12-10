package de.perdian.commons.fx.preferences;

interface PreferencesListener {

    void onPropertyChanged(String propertyName, String oldPropertyValue, String newPropertyValue);

}
