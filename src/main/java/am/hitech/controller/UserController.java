package am.hitech.controller;

import am.hitech.model.User;
import am.hitech.model.dto.UserRequestDto;
import am.hitech.service.UserService;
import am.hitech.util.exceptions.IllegalArgumentException;
import am.hitech.util.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/get-user")
    public ResponseEntity<User> getUser(@RequestParam String email){
        User user = userService.getByUserName(email);

        return ResponseEntity.ok(user);
    }

    @PostMapping("registration/get-number-code")
    public ResponseEntity<Void> getNumberCode(@RequestParam String number){
        userService.getNumberCode(number);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/registration/number-verification")
    public ResponseEntity<Void> numberVerification(@RequestParam int numberCode,
                                                   @RequestParam int id) throws NotFoundException {
        userService.numberVerification(numberCode, id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("registration/email")
    public ResponseEntity<Void> getEmail(@Valid @RequestBody UserRequestDto requestDto,
                                         @RequestParam int id) throws IllegalArgumentException {
        userService.getEmail(requestDto, id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/registration/email-verification")
    public ResponseEntity<?> emailVerification(@RequestParam int emailCode,
                                               @RequestParam int id) throws NotFoundException {
        userService.emailVerification(emailCode, id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/registration/create-password")
    public ResponseEntity<?> createPassword(@RequestParam int id,
                                            @Valid @RequestParam String password,
                                            @RequestParam String checkPassword){
        if (password.matches("^.*(?=.{8,})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$") && password.equals(checkPassword)) {
            userService.createPassword(password, checkPassword, id);
            return new ResponseEntity<>("You have registered successfully", HttpStatus.OK);
        }
        else return new ResponseEntity<>("The password is in invalid form or passwords are not matching",HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/forgot/email")
    public ResponseEntity<?> forgotPassword(@RequestParam String emailOrNumber) throws NotFoundException {
        userService.forgotPassword(emailOrNumber);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/forgot/new-password")
    public ResponseEntity<?> newPassword(@Valid @RequestParam String password,
                                         @RequestParam String confirmPassword,
                                         @RequestParam Integer code,
                                         @RequestParam String email) throws IllegalArgumentException{
        if (password.matches("^.*(?=.{8,})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$")) {
            userService.newPassword(password, confirmPassword, code, email);
            return ResponseEntity.ok().build();
        }else return new ResponseEntity<>("The password format is wrong", HttpStatus.FORBIDDEN);
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestParam String oldPassword,
                                            @RequestParam String newPassword,
                                            @RequestParam String ConfirmPassword,
                                            @RequestParam String email) throws IllegalArgumentException, NotFoundException {
        userService.changePassword(oldPassword, newPassword, ConfirmPassword, email);
        return ResponseEntity.ok().build();
    }
}
