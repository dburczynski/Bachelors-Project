package com.forex.forexpredictor.repository;


import com.forex.forexpredictor.domain.RealData;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

public interface RealDataRepository extends MongoRepository<RealData, String> {

    List<RealData> findByCurrencyOrderByDateAsc(String currency);

    RealData findByCurrencyAndDate(String currency, Date date);

    List<RealData> findAllByDate(Date date);

}
