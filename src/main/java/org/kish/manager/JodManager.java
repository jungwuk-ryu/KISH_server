package org.kish.manager;

import org.apache.commons.io.FilenameUtils;
import org.jodconverter.core.document.DefaultDocumentFormatRegistry;
import org.jodconverter.core.document.DocumentFormat;
import org.jodconverter.core.office.OfficeException;
import org.jodconverter.local.JodConverter;
import org.jodconverter.local.office.LocalOfficeManager;
import org.kish.KishServer;
import org.kish.MainLogger;
import org.kish.config.ConfigOption;

import java.io.File;

public class JodManager {
    private LocalOfficeManager officeManager;

    public JodManager() throws OfficeException {
        int port = ((Long) KishServer.CONFIG.get(ConfigOption.JOD_PORT)).intValue();
        this.officeManager = LocalOfficeManager.builder()
                .portNumbers(port).install().build();
        officeManager.start();
    }

    public void fileToPDF(File srcFile, File output){
        String srcName = srcFile.getName();
        DocumentFormat type = DefaultDocumentFormatRegistry.getFormatByExtension(FilenameUtils.getExtension(srcName));

        if(type == null){
            MainLogger.error(srcFile.getAbsolutePath() + "는 지원하지 않는 파일입니다.");
            return;
        }

        try {
            JodConverter
                    .convert(srcFile)
                    .to(output)
                    .as(DefaultDocumentFormatRegistry.PDF)
                    .execute();
        } catch (OfficeException e) {
            MainLogger.error(e);
        }
    }
}
