package org.example.userservicemay24.services;

import org.example.userservicemay24.models.User;

public interface UserService {

    public User signup(String name, String email, String password) throws Exception;
}
