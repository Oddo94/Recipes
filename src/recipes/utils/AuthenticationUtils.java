package recipes.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import recipes.model.Recipe;
import recipes.model.User;
import recipes.repository.RecipeRepository;
import recipes.repository.RegistrationRepository;

import java.util.Objects;
import java.util.Optional;

@Service
public class AuthenticationUtils {


    public static boolean isAuthorizedToUpdateOrDelete(int recipeId, RegistrationRepository registrationRepository, RecipeRepository recipeRepository) {

        //Retrieves the recipe that the user wants to update/delete from the database
        Optional<Recipe> recipe = recipeRepository.findById(recipeId);


        if (!recipe.isPresent()) {
            return false;
        }

        //Retrieves the UserDetails of the currently authenticated user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) auth.getPrincipal();

        //Retrieves the ID of the user who created the recipe and based on it the user object from the database
        int recipeCreatorId = recipe.get().getUser().getId();
        User user = registrationRepository.getUserById(recipeCreatorId);

        //Retrieves the name of the user who created the recipe and the name of the authenticated user
        String recipeCreatorName = user.getEmail();
        String authenticatedUserName = auth.getName();

        if (Objects.equals(recipeCreatorName, authenticatedUserName)) {
            return true;
        }

        return false;
    }

    public static User getUser(RegistrationRepository registrationRepository) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) auth.getPrincipal();

        User user = registrationRepository.getUserByEmail(userDetails.getUsername());

        return user;
    }
}
