package org.example.userservicemay24.services;

import jdk.jshell.spi.ExecutionControlProvider;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.userservicemay24.exceptions.ExpiredTokenException;
import org.example.userservicemay24.exceptions.InvalidTokenException;
import org.example.userservicemay24.exceptions.PasswordMisMatchException;
import org.example.userservicemay24.models.Token;
import org.example.userservicemay24.models.User;
import org.example.userservicemay24.repositories.TokenRepository;
import org.example.userservicemay24.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final TokenRepository tokenRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, TokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.tokenRepository = tokenRepository;
    }


    @Override
    public User signup(String name, String email, String password) throws Exception {

        Optional<User> optionalUser = this.userRepository.findUserByEmail(email);

        if(optionalUser.isPresent()){
            throw new Exception("User already present"); 
        }

        String encodedPassword = this.bCryptPasswordEncoder.encode(password);

        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setPassword(encodedPassword);

        return this.userRepository.save(user); // upsert = update + insert. when no Id is present Insert, if Id is present then Update
    }

    @Override
    public Token login(String email, String password) throws Exception {
        Optional<User> optionalUser = this.userRepository.findUserByEmail(email);

        User user = optionalUser.orElseThrow(()-> new UsernameNotFoundException("User not found"));

        boolean matches = this.bCryptPasswordEncoder.matches(password, user.getPassword());

        if(matches){
            // Issue a token
            String value = RandomStringUtils.randomAlphanumeric(128);
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, 30);
            Date thirtyDaysLater = c.getTime();

            Token token = new Token();
            token.setUser(user);
            token.setValue(value);
            token.setExpiresAt(thirtyDaysLater);
            token.setActive(true);
            return this.tokenRepository.save(token);
        } else {
             // throw exception
            throw new PasswordMisMatchException("Password is incorrect");
        }
    }

    @Override
    public Token validateToken(String tokenValue) throws InvalidTokenException, ExpiredTokenException {
        /*
        1. Fetch the token from db using value (select * from tokens where value = {value})
        2. If token is not present in db, throw Exception
        3. Else, check whether the token has expired or not
        4. If token is expired, then throw an Exception
        5. Else you are going to return the token
         */

        Optional<Token> tokenByValue = this.tokenRepository.findTokenByValue(tokenValue);

        Token token = tokenByValue.orElseThrow(() -> new InvalidTokenException("Please use a valid token"));

        Date expiresAt = token.getExpiresAt();
        Date now = new Date();
        // If now is greater than expires at
        if(now.after(expiresAt) || !token.isActive()){   // now is 30th may but expires at was 17th may
            throw new ExpiredTokenException("The token has expired");
        }
        return token;
    }

    @Override
    public void logout(String tokenValue) throws Exception {
        /*
         1. Fetch token from db
         2. If the token is not present, then return 400(Bad request)
         3. Else set the isActive to false  and return
         */
        Optional<Token> tokenByValue = this.tokenRepository.findTokenByValue(tokenValue);

        Token token = tokenByValue.orElseThrow(() -> new InvalidTokenException("Please use a valid token"));

        if(token.isActive()){
            token.setActive(false);
            this.tokenRepository.save(token);
        }
    }
}
