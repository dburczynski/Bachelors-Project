package com.forex.forexpredictor.controllers;

import com.forex.forexpredictor.domain.CurrencyPairList;
import com.forex.forexpredictor.service.PredictionService;
import com.forex.forexpredictor.service.ProcessService;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@Controller

public class AdminPanelController {

    @Value("${spring.data.mongodb.host}")
    String hostAddress;
    @Value("${spring.data.mongodb.username}")
    String mongoAuthUser;
    @Value("${spring.data.mongodb.password}")
    String mongoAuthUserPassword;
    @Value("${spring.data.mongodb.database}")
    String database;
    @Value("${alphavantage.api.key}")
    String alphavantageApiKey;
    @Value("${application.server.paths.api}")
    String pathToApiScripts;
    @Value("${application.server.paths.ml}")
    String pathToMlScripts;

    @Autowired
    ProcessService processService;


    @GetMapping("/admin")
    public ModelAndView adminHome() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("currencyPairs", CurrencyPairList.getAllCurrencyPairList());

        if (processService.getModels(pathToMlScripts+"models") != null)
            modelAndView.addObject("models", processService.getModels(pathToMlScripts+"models"));
        else
            modelAndView.addObject("models", new ArrayList<>());

        if(processService.isRunning())
            modelAndView.addObject("taskrunning", true);
        else
            modelAndView.addObject("taskrunning", false);
        modelAndView.setViewName("admin");

        return modelAndView;

    }

    @RequestMapping(value = "/admin", method = RequestMethod.POST)
    public ModelAndView adminHome(@RequestParam("cp") String currencyPair, @RequestParam("action") String action, @RequestParam String model) {

        ModelAndView modelAndView = new ModelAndView();

        if (!processService.isRunning()) {
            List<String> args = new ArrayList<>();
            args.add(currencyPair.substring(0, 3));
            args.add(currencyPair.substring(3));
            args.add(hostAddress);
            args.add(mongoAuthUser);
            args.add(mongoAuthUserPassword);


            if (action.equals("setup")) {
                args.add(alphavantageApiKey);
                processService.runProcess(processService.commandBuilder("python "+pathToApiScripts+"DataSetup.py", args));
            }
            if (action.equals("update")) {
                args.add(alphavantageApiKey);
                processService.runProcess(processService.commandBuilder("python "+pathToApiScripts+"DataUpdate.py", args));
            }
            if (action.equals("fix")) {
                args.add(alphavantageApiKey);
                processService.runProcess(processService.commandBuilder("python "+pathToApiScripts+"DataFix.py", args));
            }
            if (action.equals("create"))
                processService.runProcess(processService.commandBuilder("python3 "+pathToMlScripts+"CreateModel.py", args));
            if (action.equals("predict")) {
                args.add(pathToMlScripts+"models"+model);
                processService.runProcess(processService.commandBuilder("python3 "+pathToMlScripts+"Prediction.py", args));

            }
        }

        modelAndView.setViewName("redirect:/admin");
        return modelAndView;

    }

}











