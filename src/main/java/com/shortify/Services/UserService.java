package com.shortify.Services;

import java.sql.SQLException;

import com.shortify.models.User;
import com.shortify.repositories.UserRepository;
import com.shortify.utils.Security;
import com.shortify.utils.ServiceJDBCException;
import com.shortify.utils.UserRegisterException;
import com.shortify.utils.Validate;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class UserService {
    @Inject
    UserRepository userRepository;

    public User logInUser(User user) {
        User loggedUser = null;

        try {
            if (user != null && Validate.isValidUser(user)) {
                User tempUser = user.getUsername() != null
                        ? userRepository.getUserByUsername(user.getUsername())
                        : userRepository.getUserByEmail(user.getEmail());
                if (tempUser != null) {
                    System.out.println("\nContrasenna haseahda:"+ tempUser.getPassword());
                    System.out.println("\nContrasenna :"+ user.getPassword());
                    System.out.println(Security.desEncriptationComparation(user.getPassword(), tempUser.getPassword()));
                    if (tempUser.equals(user) && Security.desEncriptationComparation(user.getPassword(), tempUser.getPassword())) {
                        // Eliminando la contraseña del objeto a devolver por seguridad
                        tempUser.setPassword(null);
                        loggedUser = tempUser;
                    }
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServiceJDBCException(e.getMessage(), e);
        }

        return loggedUser;
    }

    public User signUp(User userNew) throws UserRegisterException {
        User userCreated = null;

        try {
            if (Validate.isValidUser(userNew)) {
                // Comprobar si no exite otro usuario con los mismos datos
                if (userRepository.getUserByUsername(userNew.getUsername()) != null) {
                    throw new UserRegisterException("User name is already taken");
                } else if (userRepository.getUserByEmail(userNew.getEmail()) != null) {
                    throw new UserRegisterException("A user with this email already exists");
                } else {
                    // En este caso no existe ningun usuario con el mismo username o email
                    userRepository.createUser(userNew, Security.encriptation(userNew.getPassword()));
                    userCreated = userRepository.getUserByEmail(userNew.getUsername());
                    if (userCreated == null) {
                        userCreated = userRepository.getUserByEmail(userNew.getEmail());
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServiceJDBCException(e.getMessage(), e);
        }

        return userCreated;

    }
}
