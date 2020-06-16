package com.forex.forexpredictor.controllers;

import com.forex.forexpredictor.domain.UnconfirmedUser;
import com.forex.forexpredictor.domain.User;
import com.forex.forexpredictor.service.CustomUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.*;
import javax.validation.Valid;
import java.util.Date;
import java.util.Properties;

@Controller
public class LoginController {

    @Value("${application.domain.name}")
    String applicationDomainName;
    @Value("${application.domain.port}")
    String applicationDomainPort;
    @Value("${application.mail.email}")
    String applicationMailEmail;
    @Value("${application.mail.password}")
    String applicationMailPassword;
    @Value("${application.mail.host}")
    String applicationMailHost;
    @Value("${application.mail.port}")
    String applicationMailPort;
    @Value("${application.mail.auth}")
    String applicationMailAuth;
    @Value("${application.mail.ttls}")
    String getApplicationMailTtls;

    private static final Logger log = LoggerFactory.getLogger(AdminPanelController.class);

    @Autowired
    private CustomUserDetailsService userService;



    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView login() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        return modelAndView;
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public ModelAndView register() {
        ModelAndView modelAndView = new ModelAndView();
        UnconfirmedUser unconfirmedUser = new UnconfirmedUser();
        modelAndView.addObject("user", unconfirmedUser);
        modelAndView.setViewName("register");
        return modelAndView;
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ModelAndView createNewUser(@Valid UnconfirmedUser unconfirmedUser, BindingResult bindingResult) {
        ModelAndView modelAndView = new ModelAndView();
        UnconfirmedUser unconfirmedUserExists = userService.findUnconfirmedUserByEmail(unconfirmedUser.getEmail());
        User registeredUserExists = userService.findUserByEmail((unconfirmedUser.getEmail()));
        if (unconfirmedUserExists != null || registeredUserExists != null) {
            bindingResult
                    .rejectValue("email", "error.user",
                            "There is already a user registered with the username provided");
        }
        if (bindingResult.hasErrors()) {
            modelAndView.addObject("userExists", true);
            modelAndView.setViewName("register");
        } else {
            unconfirmedUser.setDateCreated(new Date());
            userService.saveAsUnconfirmedUser(unconfirmedUser);
            try {
                Properties properties = System.getProperties();
                properties.put("mail.smtp.port", applicationMailPort);
                properties.put("mail.smtp.auth", applicationMailAuth);
                properties.put("mail.smtp.starttls.enable", getApplicationMailTtls);

                Session session = Session.getDefaultInstance(properties, null);

                MimeMessage mimeMessage = new MimeMessage(session);
                mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(unconfirmedUser.getEmail()));
                mimeMessage.setSubject("Confirm Your Forex Predictor Account");
                String emailBody= "<p>Do not reply to this email!<p>" +
                        "<p>To confirm your account please click the link below:<p></br>" +
                        "<a href=\"http://"+ applicationDomainName+":"+applicationDomainPort+"/confirm?k="
                        +unconfirmedUser.getEmail()+"\">Account Confirmation Link</a>\n";

                mimeMessage.setContent(emailBody,"text/html");
                Transport transport = session.getTransport("smtp");
                transport.connect(applicationMailHost,applicationMailEmail,applicationMailPassword);
                transport.sendMessage(mimeMessage,mimeMessage.getAllRecipients());
                transport.close();
                modelAndView.addObject("successMessage", "Sent Confirmation Email.");
                modelAndView.addObject("user", new User());
                modelAndView.setViewName("login");
            }
            catch (AddressException e) {
                log.info("Error in mail address!");
            }
            catch (MessagingException e) {
                 log.info("Error in sending message!");
            }
        }
        return modelAndView;
    }

    @RequestMapping(value = "/confirm", method = RequestMethod.GET)
    public ModelAndView confirm(@RequestParam String k) {
        userService.confirmUser(k);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/login");
        return modelAndView;
    }

    @RequestMapping(value = {"/","/home"}, method = RequestMethod.GET)
    public ModelAndView home() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("home");
        return modelAndView;
    }

}

