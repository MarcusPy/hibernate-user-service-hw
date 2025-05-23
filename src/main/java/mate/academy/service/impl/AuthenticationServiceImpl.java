package mate.academy.service.impl;

import java.util.Optional;
import mate.academy.exception.AuthenticationException;
import mate.academy.exception.RegistrationException;
import mate.academy.lib.Inject;
import mate.academy.lib.Service;
import mate.academy.model.User;
import mate.academy.service.AuthenticationService;
import mate.academy.service.UserService;
import mate.academy.util.HashUtil;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    @Inject
    private UserService userService;

    @Override
    public void registerUser(String email, String password) throws RegistrationException {
        Optional<User> optionalUserByEmail = userService.findByEmail(email);
        if (optionalUserByEmail.isPresent()) {
            throw new RegistrationException("Email " + email + " already registered");
        }
        
        User newUser = new User();
        newUser.setEmail(email);
        byte[] salt = HashUtil.getSalt(); 
        newUser.setSalt(salt); 
        String hashedPassword = HashUtil.hashPassword(password, salt); 
        newUser.setPassword(hashedPassword); 
        userService.add(newUser); 
    }

    @Override
    public User login(String email, String password) throws AuthenticationException {
        Optional<User> optionalUserByEmail = userService.findByEmail(email);
        if (optionalUserByEmail.isEmpty()) {
            throw new AuthenticationException("No user with email: " + email);
        }
        User user = optionalUserByEmail.get();
        
        String hashedPassword = HashUtil.hashPassword(password, user.getSalt());
        if (user.getPassword().equals(hashedPassword)) {
            return user;
        }
        throw new AuthenticationException("Wrong password");
    }
}
