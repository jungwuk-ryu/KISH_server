package org.kish.manager;

import org.jodconverter.core.document.DefaultDocumentFormatRegistry;
import org.jodconverter.core.office.OfficeException;
import org.jodconverter.local.JodConverter;
import org.jodconverter.local.office.LocalOfficeManager;
import org.kish.MainLogger;

import java.io.File;

public class JodManager {
    private LocalOfficeManager officeManager;

    public JodManager() throws OfficeException {
        this.officeManager = LocalOfficeManager.install();
        officeManager.start();
    }

    public void docxToPDF(File docxFile, File output){
        try {
            JodConverter
                    .convert(docxFile)
                    .as(DefaultDocumentFormatRegistry.DOCX)
                    .to(output)
                    .as(DefaultDocumentFormatRegistry.PDF)
                    .execute();
        } catch (OfficeException e) {
            MainLogger.error(e);
        }
    }
}
