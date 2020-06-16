package com.forex.forexpredictor.service;

import com.forex.forexpredictor.controllers.AdminPanelController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProcessService {

    private static final Logger log = LoggerFactory.getLogger(AdminPanelController.class);

    private static boolean running = false;

    public void runProcess(String command) {

        if(!isRunning()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        running = true;
                        log.info("Starting process: " + command);
                        Runtime.getRuntime().exec(command).waitFor();
                        running = false;
                        log.info("Finised process: " + command);
                    } catch (IOException e) {
                    } catch (InterruptedException e) {
                    }
                }
            }).start();
        }
    }

    public String commandBuilder(String command, List<String> args) {
        for(String a: args) {
            command +=" "+a;
        }

        return command;
    }

    public boolean isRunning() {
        return running;
    }

    public List<String> getModels(String directoryPath) {
        File directory = new File(directoryPath);
        File[] listOfFiles = directory.listFiles();
        List<String> fileNames = new ArrayList<>();
        if(listOfFiles != null) {
            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile())
                    fileNames.add(listOfFiles[i].getName());
            }
        }
        return fileNames;
    }

}
