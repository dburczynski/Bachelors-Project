package com.forex.forexpredictor.repository;


import com.forex.forexpredictor.domain.PredictionData;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

public interface PredictionDataRepository extends MongoRepository<PredictionData, String> {

    PredictionData findByCurrency(String currency);

    PredictionData findByCurrencyAndDate(String currency, Date date);

    List<PredictionData> findAllByDate(Date date);
}

