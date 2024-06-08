package org.example.userservicemay24.services;

import org.example.userservicemay24.exceptions.ExpiredTokenException;
import org.example.userservicemay24.exceptions.InvalidTokenException;
import org.example.userservicemay24.models.Token;
import org.example.userservicemay24.models.User;

public interface UserService {

    public User signup(String name, String email, String password) throws Exception;

    public Token login(String email, String password) throws Exception;

    public Token validateToken(String tokenValue) throws InvalidTokenException, ExpiredTokenException;

    public void logout(String token) throws Exception;
}
