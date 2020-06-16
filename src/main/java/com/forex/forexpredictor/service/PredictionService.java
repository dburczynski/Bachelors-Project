package com.forex.forexpredictor.service;

import com.forex.forexpredictor.domain.PredictionData;
import com.forex.forexpredictor.repository.PredictionDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class PredictionService {
    @Autowired
    PredictionDataRepository predictionRepository;

    public PredictionData findLatestByCurrency(String currency) throws ParseException {
        PredictionData latest = new PredictionData();
        List<PredictionData> predictionDataList = predictionRepository.findAll();
        Date dateLatest = new SimpleDateFormat("yyyy-MM-dd").parse("1971-01-01");
        Date dateCheck;

        for (PredictionData predictionData : predictionDataList)
        {
            if (predictionData.getCurrency().equals(currency)){
                dateCheck = new SimpleDateFormat("yyyy-MM-dd").parse(predictionData.getDate());
                if (dateLatest.compareTo(dateCheck)<0){
                    dateLatest = dateCheck;
                    latest = predictionData;
                }
            }
        }
        return latest;
    }
}
