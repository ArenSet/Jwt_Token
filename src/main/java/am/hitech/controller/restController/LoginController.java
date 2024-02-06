package am.hitech.controller.restController;

import am.hitech.config.JwtTokenProvider;
import am.hitech.model.User;
import am.hitech.model.dto.AuthenticationRequestDto;
import am.hitech.service.UserService;
import am.hitech.util.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class LoginController {

    @Autowired
    private final AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public LoginController(AuthenticationManager authenticationManager, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<?> login12(@RequestParam String emailOrNumber,
                                     @RequestParam String password) throws NotFoundException {
        User user = userService.getByNumberOrEmail(emailOrNumber,emailOrNumber);


        if (user.getPassword().equals(password)){
            return new ResponseEntity<>("You have logged in successfully", HttpStatus.OK);
        }
        else return new ResponseEntity<>("The username or password is invalid", HttpStatus.UNAUTHORIZED);
    }

    @PostMapping(value = "/login",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequestDto request){
        try {
            String email = request.getEmail();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            User user = userService.getByUserName(request.getEmail());
            if (user == null){
                throw new UsernameNotFoundException("User doesn't exist");
            }
            String token = jwtTokenProvider.createToken(request.getEmail(), user.getPassword());
            Map<Object, Object> response = new HashMap<>();
            response.put("email", request.getEmail());
            response.put("token", token);

            return ResponseEntity.ok(response);

        }catch (AuthenticationException e){

            return new ResponseEntity<>("Invalid email or password", HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response){
        SecurityContextLogoutHandler securityContextLogoutHandler = new SecurityContextLogoutHandler();
        securityContextLogoutHandler.logout(request,response,null);
    }

}
