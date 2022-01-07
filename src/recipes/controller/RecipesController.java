package recipes.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import recipes.model.Recipe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import recipes.model.User;
import recipes.repository.RegistrationRepository;
import recipes.utils.AuthenticationUtils;
import recipes.utils.DetailedResponse;
import recipes.utils.Response;
import recipes.repository.RecipeRepository;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@ComponentScan("recipes/repository")
public class RecipesController {

    @Autowired
    RecipeRepository recipeRepository;

    @Autowired
    RegistrationRepository registrationRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(RecipeRepository.class);

    @RequestMapping(value="/api/recipe/new", method = RequestMethod.POST)
    public ResponseEntity addRecipe(@AuthenticationPrincipal UserDetails userDetails, @RequestBody Recipe recipe) {
        LocalDateTime recipeCreationDate = LocalDateTime.now();
        recipe.setDate(recipeCreationDate);

        User user = AuthenticationUtils.getUser(registrationRepository);
        recipe.setUser(user);

        Recipe resultRecipe = recipeRepository.save(recipe);

        Response response = new Response(resultRecipe.getId());


        return new ResponseEntity(response, HttpStatus.OK);
    }

    @RequestMapping(value="/api/recipe/{id}", method = RequestMethod.PUT)
    public ResponseEntity updateRecipe(@PathVariable int id, @RequestBody Recipe recipe) {
        Optional<Recipe> recipeToUpdate = recipeRepository.findById(id);
        DetailedResponse detailedResponse = null;
        if (!recipeToUpdate.isPresent()) {
            detailedResponse = new DetailedResponse("error", HttpStatus.NOT_FOUND, "Unable to find the recipe having the specified ID");
            return new ResponseEntity(detailedResponse, HttpStatus.NOT_FOUND);
        }

        if (!AuthenticationUtils.isAuthorizedToUpdateOrDelete(id, registrationRepository, recipeRepository)) {
            detailedResponse = new DetailedResponse("error", HttpStatus.FORBIDDEN, "You are not authorized to update this recipe! Only its creator has this right");
            return new ResponseEntity(detailedResponse, HttpStatus.FORBIDDEN);
        }

        LocalDateTime recipeUpdateDate = LocalDateTime.now();

        //Updated object setup
        recipeToUpdate.get().setId(id);
        recipeToUpdate.get().setName(recipe.getName());
        recipeToUpdate.get().setCategory(recipe.getCategory());
        recipeToUpdate.get().setDate(recipeUpdateDate);
        recipeToUpdate.get().setDescription(recipe.getDescription());
        recipeToUpdate.get().setIngredients(recipe.getIngredients());
        recipeToUpdate.get().setDirections(recipe.getDirections());


        Recipe updatedRecipe = recipeRepository.save(recipe);

        return new ResponseEntity(HttpStatus.NO_CONTENT);


    }

    @RequestMapping(value="/api/recipe/{id}", method = RequestMethod.GET)
    public ResponseEntity getRecipe(@PathVariable int id) {
        Optional<Recipe> requestedRecipe = recipeRepository.findById(id);

        if (!requestedRecipe.isPresent()) {
            DetailedResponse detailedResponse = new DetailedResponse("error", HttpStatus.NOT_FOUND, "Unable to find the recipe having the specified ID");
            return new ResponseEntity(detailedResponse, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity(requestedRecipe, HttpStatus.OK);
    }

    @RequestMapping(value="api/recipe/{id}", method = RequestMethod.DELETE)
    public ResponseEntity deleteRecipe(@PathVariable int id) {
        Optional<Recipe> recipeToDelete = recipeRepository.findById(id);

        DetailedResponse detailedResponse = null;
        if (!recipeToDelete.isPresent()) {
            detailedResponse = new DetailedResponse("error", HttpStatus.NOT_FOUND, "Unable to find the recipe with the specified ID.");
            return new ResponseEntity(detailedResponse, HttpStatus.NOT_FOUND);
        }

        Recipe recipe = recipeToDelete.get();
        if (!AuthenticationUtils.isAuthorizedToUpdateOrDelete(id, registrationRepository, recipeRepository)) {
            detailedResponse = new DetailedResponse("error", HttpStatus.FORBIDDEN, "You are not authorized to delete this recipe! Only its creator has this right");
            return new ResponseEntity(detailedResponse, HttpStatus.FORBIDDEN);
        }

        recipeRepository.deleteById(id);

        detailedResponse = new DetailedResponse("ok", HttpStatus.NO_CONTENT, "The recipe having id  " + id + " was successfully deleted");
        return new ResponseEntity(detailedResponse, HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value="/api/recipe/search", method = RequestMethod.GET)
    public ResponseEntity searchRecipe(@RequestParam HashMap<String, String> parameterMap) {
        //LOGGER.info("SEARCH INPUT: \nParameter list: category = " + parameterMap.get("category") + "; name = " +  parameterMap.get("name"));
        if (parameterMap == null || ("".equals(parameterMap.get("category")) && "".equals(parameterMap.get("name")))) {
            DetailedResponse detailedResponse = new DetailedResponse("error", HttpStatus.BAD_REQUEST, "Incorrect number of arguments or argument value/s! Please provide a single argument to be used as search criterion.");
            return new ResponseEntity(detailedResponse, HttpStatus.BAD_REQUEST);
        }

        String category = parameterMap.get("category");
        String name = parameterMap.get("name");

        List<Recipe> recipeList = new ArrayList<>();

        if (category == null) {
            recipeList = recipeRepository.findAllByNameIgnoreCaseContainingOrderByDateDesc(name);
        } else if (name == null) {
            recipeList = recipeRepository.findAllByCategoryIgnoreCaseOrderByDateDesc(category);
        }

        //LOGGER.info("SEARCH OUTPUT ARRAY LENGTH: " + recipeList.size());
        return new ResponseEntity(recipeList, HttpStatus.OK);
    }
}
