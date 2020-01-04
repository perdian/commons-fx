package de.perdian.commons.fx.persistence;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public interface PersistenceEnabled {

    boolean isPersistable();
    void loadFromXML(Element xmlElement, Document owningDocument);
    void appendToXML(Element xmlElement, Document owningDocument);
    boolean addChangeListener(ChangeListener<Object> changeListener);
    boolean removeChangeListener(ChangeListener<Object> changeListener);

    public static <T extends PersistenceEnabled> ObservableList<T> loadRecordsObserved(Path storageFile, Class<T> recordClass) {
        return PersistenceEnabled.loadRecordsObserved(storageFile, recordClass, new PersistenceEnabledDefaultConstructorSupplier<>(recordClass));
    }

    public static <T extends PersistenceEnabled> ObservableList<T> loadRecordsObserved(Path storageFile, Class<T> recordClass, Supplier<T> recordFacory) {

        ObservableList<T> records = FXCollections.observableArrayList();
        ChangeListener<Object> changeListener = (o, oldValue, newValue) -> PersistenceEnabled.writeRecords(records, storageFile);
        records.addListener((ListChangeListener.Change<? extends T> change) -> {
            while (change.next()) {
                change.getRemoved().forEach(removedRecord -> removedRecord.removeChangeListener(changeListener));
                change.getAddedSubList().forEach(addedRecord -> addedRecord.addChangeListener(changeListener));
            }
        });

        if (Files.exists(storageFile)) {
            LoggerFactory.getLogger(PersistenceEnabled.class).info("Loading records of class {} from file: {}", recordClass.getName(), storageFile);
            try (InputStream storageFileStream = new BufferedInputStream(Files.newInputStream(storageFile))) {
                records.addAll(PersistenceEnabled.loadRecords(storageFileStream, recordClass, recordFacory));
            } catch (Exception e) {
                LoggerFactory.getLogger(PersistenceEnabled.class).warn("Cannot load records of class {} from file: {}", recordClass.getName(), storageFile, e);
            }
        }

        records.addListener((ListChangeListener.Change<? extends T> change) -> PersistenceEnabled.writeRecords(records, storageFile));
        return records;

    }

    public static <T extends PersistenceEnabled> List<T> loadRecords(InputStream inputStream, Class<T> recordClass) throws IOException {
        return PersistenceEnabled.loadRecords(inputStream, recordClass, new PersistenceEnabledDefaultConstructorSupplier<>(recordClass));
    }

    public static <T extends PersistenceEnabled> List<T> loadRecords(InputStream inputStream, Class<T> recordClass, Supplier<T> recordFactory) throws IOException {
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.parse(inputStream);
            NodeList recordElements = document.getDocumentElement().getElementsByTagName(recordClass.getSimpleName());
            List<T> records = new ArrayList<>();
            for (int recordIndex = 0; recordIndex < recordElements.getLength(); recordIndex++) {
                Element recordElement = (Element)recordElements.item(recordIndex);
                T record = recordFactory.get();
                record.loadFromXML(recordElement, document);
                records.add(record);
            }
            return records;
        } catch (Exception e) {
            throw new IOException("Cannot import records of class: " + recordClass.getName(), e);
        }
    }

    public static <T extends PersistenceEnabled> void writeRecords(Collection<T> records, OutputStream outputStream) throws IOException {
        try {

            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.newDocument();
            Element rootElement = document.createElement("records");
            document.appendChild(rootElement);
            for (T record : records) {
                if (record.isPersistable()) {
                    Element recordElement = document.createElement(record.getClass().getSimpleName());
                    record.appendToXML(recordElement, document);
                    rootElement.appendChild(recordElement);
                }
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.transform(new DOMSource(document), new StreamResult(outputStream));

        } catch (Exception e) {
            throw new IOException("Cannot export records", e);
        }
    }

    private static <T extends PersistenceEnabled> void writeRecords(List<T> records, Path storageFile) {
        if (!Files.exists(storageFile.getParent())) {
            try {
                LoggerFactory.getLogger(PersistenceEnabled.class).debug("Creating target directory: {}", storageFile.getParent());
                Files.createDirectories(storageFile.getParent());
            } catch (IOException e) {
                LoggerFactory.getLogger(PersistenceEnabled.class).warn("Cannot create target directory: {}", storageFile.getParent(), e);
            }
        }
        LoggerFactory.getLogger(PersistenceEnabled.class).debug("Storing {} records into file: {}", records.size(), storageFile);
        try (OutputStream storageFileStream = new BufferedOutputStream(Files.newOutputStream(storageFile))) {
            PersistenceEnabled.writeRecords(records, storageFileStream);
            storageFileStream.flush();
        } catch (Exception e) {
            LoggerFactory.getLogger(PersistenceEnabled.class).warn("Cannot write records into file: {}", storageFile, e);
        }
    }

}
