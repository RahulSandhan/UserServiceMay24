package org.example.userservicemay24.services;

import jdk.jshell.spi.ExecutionControlProvider;
import org.example.userservicemay24.models.User;
import org.example.userservicemay24.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
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
}
