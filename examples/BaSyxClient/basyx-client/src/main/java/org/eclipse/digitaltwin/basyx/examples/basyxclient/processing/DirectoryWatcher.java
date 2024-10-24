/*******************************************************************************
 * Copyright (C) 2024 the Eclipse BaSyx Authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * SPDX-License-Identifier: MIT
 ******************************************************************************/

package org.eclipse.digitaltwin.basyx.examples.basyxclient.processing;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;

import org.eclipse.digitaltwin.basyx.examples.basyxclient.model.MotorEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.bean.CsvToBeanBuilder;

public class DirectoryWatcher implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(DirectoryWatcher.class);

    private final List<EntryProcessor> entryProcessors;

    private final Path dirToWatch;

    public DirectoryWatcher(List<EntryProcessor> entryProcessors, Path dirToWatch) {
        this.entryProcessors = entryProcessors;
        this.dirToWatch = dirToWatch;
    }

    @Override
    public void run() {
        try {
            watchDirectory(dirToWatch);
        } catch (IOException | InterruptedException e) {
            logger.error("Error watching directory", e);
            Thread.currentThread().interrupt();
        }
    }

    public void watchDirectory(Path dirToWatch) throws IOException, InterruptedException {
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
                        List<MotorEntry> entries = parseCsvFile(newFilePath);
                        processAllEntries(entries);
                    } catch (Exception e) {
                        logger.error("Error parsing file: {}", newFilePath, e);
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
        for (EntryProcessor entryProcessor : entryProcessors) {
            try {
                logger.info("Applying processor: {}", entryProcessor.getClass().getSimpleName());
                entryProcessor.process(entries);
            } catch (Exception e) {
                logger.error("Processor {} failed", entryProcessor.getClass().getSimpleName(), e);
            }
        }
    }


}
