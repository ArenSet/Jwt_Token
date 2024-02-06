package am.hitech.service;

import am.hitech.model.User;
import am.hitech.model.dto.UserRequestDto;
import am.hitech.util.exceptions.IllegalArgumentException;
import am.hitech.util.exceptions.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    User getByUserName(String email);

    User getByNumberOrEmail(String number, String email) throws NotFoundException;

    void getNumberCode(String number);

    ResponseEntity<?> createPassword(String password, String checkPassword, int id);

    /*void forgotPassword(String emailOrNumber) throws NotFoundException;

    void newPassword(String password, String confirmPassword, Integer code, String email) throws IllegalArgumentException;*/

    ResponseEntity<?> numberVerification(int numberCode, int id) throws NotFoundException;

    ResponseEntity<?> getEmail(UserRequestDto requestDto, int id) throws IllegalArgumentException;


    ResponseEntity<?> emailVerification(int emailCode, int id) throws NotFoundException;


    void forgotPassword(String emailOrNumber) throws NotFoundException;

    void newPassword(String password, String confirmPassword, Integer code, String email) throws IllegalArgumentException;

    void changePassword(String oldPassword, String newPassword, String confirmPassword, String email) throws IllegalArgumentException, NotFoundException;
}
