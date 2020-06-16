package com.forex.forexpredictor.controllers;

import com.forex.forexpredictor.domain.CurrencyPairList;
import com.forex.forexpredictor.domain.RealData;
import com.forex.forexpredictor.domain.User;
import com.forex.forexpredictor.repository.PredictionDataRepository;
import com.forex.forexpredictor.repository.RealDataRepository;
import com.forex.forexpredictor.service.CustomUserDetailsService;
import com.forex.forexpredictor.service.PredictionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class UserPanelController {

    @Autowired
    private CustomUserDetailsService userService;

    @Autowired
    private PredictionDataRepository pr;

    @Autowired
    PredictionService ps;

    @Autowired
    private RealDataRepository realDataRepository;
    @Autowired
    private PredictionService predictionService;


    @Value("${application.domain.name}")
    String applicationDomainName;
    @Value("${application.domain.port}")
    String applicationDomainPort;

    //TODO change currency to list of currencies
    @RequestMapping(value = "/user-home", method = RequestMethod.GET)
    public ModelAndView userhome() throws ParseException {
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        modelAndView.addObject("domainName", applicationDomainName);
        modelAndView.addObject("domainPort", applicationDomainPort);
        modelAndView.addObject("user", userService.findUserByEmail(auth.getName()));
        modelAndView.addObject("currencyPairList", userService.findUserByEmail(auth.getName()).getSubscribedCurrencyPairs());
        modelAndView.addObject("amountOfPairs", userService.findUserByEmail(auth.getName()).getSubscribedCurrencyPairs().size());

        modelAndView.setViewName("user-home");
        return modelAndView;
    }

    @RequestMapping(value = "/user-edit", method = RequestMethod.GET)
    public ModelAndView useredit() {
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());

        modelAndView.addObject("afterChangeUser", new User());
        modelAndView.addObject("beforeChangeUser", user);
        modelAndView.setViewName("user-edit");
        return modelAndView;
    }

    @RequestMapping(value = "/user-edit", method = RequestMethod.POST)
    public ModelAndView userupdate(@Valid User updatedUserDetails, BindingResult bindingResult) {
        ModelAndView modelAndView = new ModelAndView();
        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("user-edit");
        } else {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = userService.findUserByEmail(auth.getName());
            userService.updateUserPassord(user, updatedUserDetails.getPassword());
            modelAndView.addObject("successMessage", "Account has been edited successfully");
            modelAndView.addObject("user", new User());
            modelAndView.setViewName("login");

        }

        return modelAndView;
    }


    @RequestMapping(value = "/user-add-currency", method = RequestMethod.GET)
    public ModelAndView userAddCurrency() {
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        modelAndView.addObject("role", user.getRoles().toArray()[0]);
        modelAndView.addObject("subscribedCurrencyPairs", user.getSubscribedCurrencyPairs());
        if (user.isPremium()) {
            modelAndView.addObject("currencyPairList", CurrencyPairList.getCurrencyPairListDifference(CurrencyPairList.getAllCurrencyPairList(), user.getSubscribedCurrencyPairs()));
        } else {
            modelAndView.addObject("currencyPairList", CurrencyPairList.getCurrencyPairListDifference(CurrencyPairList.getDefaultCurrencyPairList(), user.getSubscribedCurrencyPairs()));
        }
        modelAndView.setViewName("user-add-currency");

        return modelAndView;
    }

    @RequestMapping(value = "/user-add-currency", method = RequestMethod.POST)
    public String userAddedCurrency(@RequestParam("currencyPair") String currencyPair, @RequestParam("action") String action) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());

        userService.updateCurrency(user, currencyPair, action);

        return "redirect:/user-home";
    }

    @RequestMapping(value = "/view-currency-pair", method = RequestMethod.GET)
    public ModelAndView userViewCurrencyPair(@RequestParam("cp") String currencyPair) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());

        if (userService.canViewCurrencyPair(user.getEmail(), currencyPair)) {
            List<RealData> data = realDataRepository.findByCurrencyOrderByDateAsc(currencyPair);
            List<Long> date = new ArrayList<>();
            List<Float> open = new ArrayList<>();
            List<Float> high = new ArrayList<>();
            List<Float> low = new ArrayList<>();
            List<Float> close = new ArrayList<>();
            List<Float> ema = new ArrayList<>();

            List<List<String>> dataList = new ArrayList();

            for (RealData dataObject : data) {

                Date tempDate = new SimpleDateFormat("yyyy/MM/dd").parse(dataObject.getDate().replace("-", "/"));
                date.add(tempDate.getTime());
                open.add(dataObject.getOpen());
                high.add(dataObject.getHigh());
                low.add(dataObject.getLow());
                close.add(dataObject.getClose());
                ema.add(dataObject.getEma());
            }
            modelAndView.addObject("currency", currencyPair);
            modelAndView.addObject("date", date);
            modelAndView.addObject("open", open);
            modelAndView.addObject("high", high);
            modelAndView.addObject("low", low);
            modelAndView.addObject("close", close);
            modelAndView.addObject("ema", ema);

            modelAndView.addObject("prediction", predictionService.findLatestByCurrency(currencyPair));

            modelAndView.setViewName("view-currency-pair");
        }
        else {
            modelAndView.addObject("domainName", applicationDomainName);
            modelAndView.addObject("domainPort", applicationDomainPort);
            modelAndView.addObject("user", user);
            modelAndView.addObject("currencyPairList", user.getSubscribedCurrencyPairs());
            modelAndView.addObject("amountOfPairs", user.getSubscribedCurrencyPairs().size());

            modelAndView.setViewName("user-home");

        }
        return modelAndView;

    }
}
