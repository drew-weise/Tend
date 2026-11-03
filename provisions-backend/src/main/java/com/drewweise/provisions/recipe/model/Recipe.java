package com.drewweise.provisions.recipe.model;

import com.drewweise.provisions.user.model.User;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Recipe.
 */
@Entity
@Table(name = "recipes")
/**
 * Represents a recipe in Provisions.
 *
 * @author dweise
 */
public class Recipe {

    /** The recipe's database recipeId. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_id")
    private int recipeId;

    /** The recipe's recipeTitle. */
    @Column(name = "title", nullable = false)
    private String recipeTitle;

    @Column(name = "description", columnDefinition = "TEXT")
    private String recipeDescription;

    @Column(name = "instructions", columnDefinition = "TEXT")
    private String recipeInstructions;

    /** The recipe's recipeServings. */
    @Column(name = "servings")
    private int recipeServings;

    /** The recipe's user. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** The recipe's recipeIngredients. */
    @OneToMany(mappedBy = "recipe", fetch = FetchType.LAZY)
    private List<RecipeIngredient> recipeIngredients = new ArrayList<>();

    /**
     * Required by Hibernate.
     */
    public Recipe() {
    }

    /**
     * Creates a new Recipe.
     *
     * @param recipeTitle        the recipe title
     * @param recipeDescription  the recipe description
     * @param recipeInstructions the recipe instruction
     * @param recipeServings     the recipe servings
     * @param user               the user
     */
    public Recipe(String recipeTitle, String recipeDescription,
                  String recipeInstructions, int recipeServings, User user) {
        this.recipeTitle = recipeTitle;
        this.recipeDescription = recipeDescription;
        this.recipeInstructions = recipeInstructions;
        this.recipeServings = recipeServings;
        this.user = user;
    }

    /**
     * Gets recipe id.
     *
     * @return the recipe id
     */
    public int getRecipeId() {return recipeId;}

    /**
     * Gets user.
     *
     * @return the user
     */
    public User getUser() {return user;}

    /**
     * Sets the user.  @param user the user
     */
    public void setUser(User user) {this.user = user;}

    /**
     * Gets recipe title.
     *
     * @return the recipe title
     */
    public String getRecipeTitle() {return recipeTitle;}

    /**
     * Sets the recipe title.  @param recipeTitle the recipe title
     */
    public void setRecipeTitle(String recipeTitle) {this.recipeTitle = recipeTitle;}

    /**
     * Gets recipe description.
     *
     * @return the recipe description
     */
    public String getRecipeDescription() {return recipeDescription;}

    /**
     * Sets the recipe description.  @param recipeDescription the recipe description
     */
    public void setRecipeDescription(String recipeDescription) {this.recipeDescription = recipeDescription;}

    /**
     * Gets recipe instructions.
     *
     * @return the recipe instruction
     */
    public String getRecipeInstructions() {return recipeInstructions;}

    /**
     * Sets the recipe instruction.  @param recipeInstructions the recipe instructions
     */
    public void setRecipeInstructions(String recipeInstructions) {this.recipeInstructions = recipeInstructions;}

    /**
     * Gets recipe servings.
     *
     * @return the recipe servings
     */
    public int getRecipeServings() {return recipeServings;}

    /**
     * Sets the recipe servings.  @param recipeServings the recipe servings
     */
    public void setRecipeServings(int recipeServings) {this.recipeServings = recipeServings;}

    /**
     * Gets recipe ingredients.
     *
     * @return the recipe ingredients
     */
    public List<RecipeIngredient> getRecipeIngredients() {return recipeIngredients;}

    /**
     * Sets the recipe ingredients.  @param recipeIngredients the recipe ingredients
     */
    public void setRecipeIngredients(List<RecipeIngredient> recipeIngredients) {this.recipeIngredients = recipeIngredients;}

    @Override
    public String toString() {
        return "Recipe{" +
                "recipeId=" + recipeId +
                ", recipeTitle='" + recipeTitle + '\'' +
                ", recipeDescription='" + recipeDescription + '\'' +
                ", recipeInstruction='" + recipeInstructions + '\'' +
                ", recipeServings=" + recipeServings +
                '}';
    }
}
