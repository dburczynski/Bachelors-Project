package com.forex.forexpredictor.domain;

import java.util.ArrayList;
import java.util.List;

public class CurrencyPairList {
    private static List<String> DefaultCurrencyPairList = new ArrayList<>();
    private static List<String> AllCurrencyPairList = new ArrayList<>();

    public static void addCurrencyPairToAllCurrencyPairList(String currencyPair) {
        if(findCurrencyInAllCurrencyPairList(currencyPair) == null) {
            AllCurrencyPairList.add(currencyPair.toUpperCase());
        }
    }

    public static void removeCurrencyFromAllCurrencyPairList(String currencyPair) {
        if(findCurrencyInAllCurrencyPairList(currencyPair) != null) {
            AllCurrencyPairList.forEach(cp -> {
                if (cp.equals(currencyPair.toUpperCase()))
                    AllCurrencyPairList.remove(cp);
            });
        }
    }

    public static String findCurrencyInAllCurrencyPairList(String currencyPair) {
        for(String cp : AllCurrencyPairList) {
            if(cp.equals(currencyPair))
                return cp;
        }
        return null;
    }

    public static List<String> getAllCurrencyPairList() {
        return AllCurrencyPairList;
    }

    public static void setAllCurrencyPairList(List<String> currencyPairList) {
        AllCurrencyPairList = currencyPairList;
    }

    public static void addCurrencyPairToDefaultCurrencyPairList(String currencyPair) {
        if(findCurrencyInDefaultCurrencyPairList(currencyPair) == null) {
            DefaultCurrencyPairList.add(currencyPair);
        }
    }

    public static void removeCurrencyPairFromDefaultCurrenyPairList(String currencyPair) {
        if(findCurrencyInDefaultCurrencyPairList(currencyPair) != null) {
            DefaultCurrencyPairList.remove(currencyPair);
        }
    }

    public static String findCurrencyInDefaultCurrencyPairList(String currencyPair) {
        for(String cp: DefaultCurrencyPairList) {
            if(cp.equals(currencyPair))
                return cp;
        }
        return null;
    }

    public static List<String> getDefaultCurrencyPairList() {
        return DefaultCurrencyPairList;
    }

    public static void setDefaultCurrencyPairList(List<String> currencyPairList) {
        DefaultCurrencyPairList = currencyPairList;
    }

    public static List<String> getCurrencyPairListDifference(List<String> currencyPairList, List<String> currencyPairListToSubtract) {
        List<String> resultList = new ArrayList<>();
        resultList.addAll(currencyPairList);
        if(currencyPairListToSubtract.size() != 0)
            resultList.removeAll(currencyPairListToSubtract);

        return resultList;
    }
}
