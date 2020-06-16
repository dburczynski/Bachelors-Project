package com.forex.forexpredictor.service;

import com.forex.forexpredictor.controllers.AdminPanelController;
import com.forex.forexpredictor.domain.CurrencyPairList;
import com.forex.forexpredictor.domain.Role;
import com.forex.forexpredictor.domain.UnconfirmedUser;
import com.forex.forexpredictor.domain.User;
import com.forex.forexpredictor.repository.RoleRepository;
import com.forex.forexpredictor.repository.UnconfirmedUserRepository;
import com.forex.forexpredictor.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UnconfirmedUserRepository unconfirmedUserRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public UnconfirmedUser findUnconfirmedUserByEmail(String email) { return unconfirmedUserRepository.findByEmail(email);}

    public void saveAsUnconfirmedUser(UnconfirmedUser unconfirmedUser) {
        unconfirmedUser.setPassword(bCryptPasswordEncoder.encode(unconfirmedUser.getPassword()));
        Role userRole = roleRepository.findByRole("USER");
        unconfirmedUser.setRoles(new HashSet<>(Arrays.asList(userRole)));
        unconfirmedUserRepository.save(unconfirmedUser);
    }

    public void saveAsAdminUser(User user) {
        Role adminRole = roleRepository.findByRole("ADMIN");
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setRoles(new HashSet<>(Arrays.asList(adminRole)));
        userRepository.save(user);
    }

    public void confirmUser(String email) {

        UnconfirmedUser unconfirmedUser = unconfirmedUserRepository.findByEmail(email);
        unconfirmedUserRepository.delete(unconfirmedUser);

        User confirmedUser = unconfirmedUser;
        userRepository.save(confirmedUser);

    }

    public void updateUserPassord(User user, String password){
        user.setPassword(bCryptPasswordEncoder.encode(password));
        userRepository.save(user);
    }

    public void updateCurrency(User user, String currency, String action){
        user.setPassword(user.getPassword());
        if(action.equals("subscribe"))
            user.addCurrencyPair(currency);
        if(action.equals("unsubscribe"))
            user.removeCurrencyPair(currency);
        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email);
        if(user != null) {
            List<GrantedAuthority> authorities = getUserAuthority(user.getRoles());
            return buildUserForAuthentication(user, authorities);
        } else {
            throw new UsernameNotFoundException("username not found");
        }
    }

    public boolean canViewCurrencyPair(String email, String currencyPair) {
        User user = userRepository.findByEmail(email);

        if(!CurrencyPairList.getAllCurrencyPairList().contains(currencyPair))
            return false;
        else {
            if(CurrencyPairList.getDefaultCurrencyPairList().contains(currencyPair))
                return true;
            else {
                if (user.getRoles().contains("PREMIUM"))
                    return true;
                else
                    return false;
            }
        }
    }

    private List<GrantedAuthority> getUserAuthority(Set<Role> userRoles) {
        Set<GrantedAuthority> roles = new HashSet<>();

        userRoles.forEach((role) -> {
            roles.add(new SimpleGrantedAuthority(role.getRole()));
        });

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>(roles);
        return grantedAuthorities;
    }

    private UserDetails buildUserForAuthentication(User user, List<GrantedAuthority> authorities) {
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
    }



}

