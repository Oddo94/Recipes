package recipes.repository;

import recipes.model.Recipe;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface RecipeRepository extends CrudRepository<Recipe, Integer> {

   List<Recipe> findAllByCategoryIgnoreCaseOrderByDateDesc(String category);
   List<Recipe> findAllByNameIgnoreCaseContainingOrderByDateDesc(String name);

}
