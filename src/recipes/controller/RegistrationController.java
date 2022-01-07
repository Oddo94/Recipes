package recipes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import recipes.model.User;
import recipes.repository.RegistrationRepository;
import recipes.utils.DetailedResponse;

import javax.validation.Valid;

@RestController
@ComponentScan("recipes/security")
public class RegistrationController {
    @Autowired
    RegistrationRepository registrationRepository;
    @Autowired
    PasswordEncoder encoder;

    @RequestMapping(value="/api/register", method = RequestMethod.POST)
    public ResponseEntity register(@Valid @RequestBody User user) {
        User retrievedUser = registrationRepository.getUserByEmail(user.getEmail());
        DetailedResponse detailedResponse = null;

        if (retrievedUser != null) {
            detailedResponse = new DetailedResponse("error", HttpStatus.BAD_REQUEST, "The user with the specified email address is already present in the database");
            return new ResponseEntity(detailedResponse, HttpStatus.BAD_REQUEST);
        }

        user.setRole("ROLE_USER");

        String encodedPassword = encoder.encode((user.getPassword()));

        user.setPassword(encodedPassword);

        registrationRepository.save(user);

        detailedResponse = new DetailedResponse("ok", HttpStatus.OK, "User successfully created!");
        return new ResponseEntity(detailedResponse, HttpStatus.OK);
    }
}
