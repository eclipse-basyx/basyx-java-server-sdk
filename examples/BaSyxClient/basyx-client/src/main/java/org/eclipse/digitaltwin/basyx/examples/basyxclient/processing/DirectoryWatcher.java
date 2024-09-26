package org.eclipse.digitaltwin.basyx.examples.basyxclient.processing;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;

import org.eclipse.digitaltwin.basyx.examples.basyxclient.model.MotorEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.opencsv.bean.CsvToBeanBuilder;

@Component
public class DirectoryWatcher {

    private Logger logger = LoggerFactory.getLogger(DirectoryWatcher.class);
    private List<EntryProcessor> entryProcessors;

    private final Path dirToWatch = Paths.get("/ingest");

    public void watchDirectory() throws IOException, InterruptedException {
        WatchService watchService = FileSystems.getDefault().newWatchService();
        dirToWatch.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

        logger.info("Watching directory: {}", dirToWatch);

        while (true) {
            WatchKey key = watchService.take();

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();

                if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                    @SuppressWarnings("unchecked")
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path newFilePath = dirToWatch.resolve(ev.context());

                    logger.info("New file detected: {}", newFilePath);

                    try {
                        processAllEntries(parseCsvFile(newFilePath));
                    } catch (Exception e) {
                        logger.error("Error processing file: {}", newFilePath, e);
                    }
                }
            }
            boolean valid = key.reset();
            if (!valid) {
                break;
            }
        }
    }

    private List<MotorEntry> parseCsvFile(Path filePath) throws IllegalStateException, FileNotFoundException {
        return new CsvToBeanBuilder<MotorEntry>(new FileReader(filePath.toString())).withType(MotorEntry.class).build().parse();
    }

    private void processAllEntries(List<MotorEntry> entries) {
        for (MotorEntry entry : entries) {
            for (EntryProcessor entryProcessor : entryProcessors) {
                entryProcessor.process(entry);
            }
        }
    }

}
