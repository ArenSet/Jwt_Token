package am.hitech.service.impl;


import am.hitech.model.User;
import am.hitech.model.dto.UserRequestDto;
import am.hitech.repository.UserRepository;
import am.hitech.service.UserService;
import am.hitech.util.ErrorMessage;
import am.hitech.util.exceptions.IllegalArgumentException;
import am.hitech.util.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Random;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User getByUserName(String email){
        User user = userRepository.findByEmail(email);

        return user;
    }

    @Override
    public User getByNumberOrEmail(String number, String email) throws NotFoundException {
        if (userRepository.getByNumberOrEmail(number, email) != null){
            return userRepository.getByNumberOrEmail(number, email);}
        else throw new NotFoundException(ErrorMessage.NOT_FOUND);

    }

    @Override
    public void getNumberCode(String number){
        User userRegistration = new User();
        userRegistration.setNumber(number);

        Random random = new Random();

        int verificationCode = random.nextInt((9999 - 1000) + 1) + 1000;
        userRegistration.setNumberVerification(verificationCode);
        userRepository.save(userRegistration);
    }

    @Override
    public ResponseEntity<?> numberVerification(int numberCode, int id) throws NotFoundException {
        User user = userRepository.findById(id);
        if (user.getNumberVerification() == numberCode && id == user.getId()){
            user.setNumberVerification(null);
            userRepository.save(user);
            return null;
        }else throw new NotFoundException("The verification code is invalid or wrong user");
    }

    @Override
    public ResponseEntity<?> getEmail(UserRequestDto requestDto, int id) throws IllegalArgumentException {
        User user = userRepository.findById(id);
        if (id == user.getId()) {
            user.setFirstName(requestDto.getName());
            user.setLastName(requestDto.getLastName());
            user.setEmail(requestDto.getEmail());

            Random random = new Random();
            int verificationCode = random.nextInt((9999 - 1000) + 1) + 1000;
            user.setEmailVerification(verificationCode);
            userRepository.save(user);

        }else throw new IllegalArgumentException("The wrong user");
        return null;
    }

    @Override
    public ResponseEntity<?> emailVerification(int emailCode, int id) throws NotFoundException {
        User user = userRepository.findById(id);
        if (id == user.getId()) {
            if (emailCode == user.getEmailVerification()) {
                user.setEmailVerification(null);
                userRepository.save(user);
                return null;
            }
        }else throw new NotFoundException("The verification code is invalid");
        return null;
    }

    @Override
    public ResponseEntity<?> createPassword(String password, String checkPassword, int id){
        User user = userRepository.findById(id);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        return null;
    }


    @Override
    public void forgotPassword(String emailOrNumber) throws NotFoundException {
        User userForgot = userRepository.getByNumberOrEmail(emailOrNumber, emailOrNumber);
        if (userForgot == null){
            throw new NotFoundException("There is no any user with that phone number or email");
        }else {
            Random random = new Random();
            int verificationCode = random.nextInt((9999 - 1000) + 1) + 1000;
            userForgot.setForgotPasswordCode(verificationCode);
            userRepository.save(userForgot);
        }
    }

    @Override
    public void newPassword(String password, String confirmPassword, Integer code, String email) throws IllegalArgumentException {
        User userForgot = userRepository.findByEmail(email);
        if (password.equals(confirmPassword) && Objects.equals(code, userForgot.getForgotPasswordCode())){
            userForgot.setPassword(passwordEncoder.encode(password));
            userForgot.setForgotPasswordCode(null);
            userRepository.save(userForgot);
        } else if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("The passwords are not matching");
        } else {
            throw new IllegalArgumentException("The code is invalid");
        }
    }

    @Override
    public void changePassword(String oldPassword, String newPassword, String confirmPassword, String email) throws IllegalArgumentException, NotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null){
            throw new NotFoundException("There is no any user with that email address");
        }else {
            if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                throw new IllegalArgumentException("The old password is wrong");
            } else if (!newPassword.equals(confirmPassword)) {
                throw new IllegalArgumentException("The passwords are not matching");
            } else if (!newPassword.matches("^.*(?=.{8,})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$")) {
                throw new IllegalArgumentException("The new password format is wrong");
            } else {
                user.setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(user);
            }
        }
    }
}
