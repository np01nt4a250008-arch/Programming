/**
 * This class represents a Personal Plan for an AI model.
 * It extends the AIModel class and adds features related
 * to user prompt limits in a monthly subscription plan.
 *
 * @author (Saman Khanal)
 * @date (05-03-2026)
 */

// PersonalPlan class is inheriting properties and methods from AIModel class
public class PersonalPlan extends AIModel
{
    // This variable stores how many prompts the user still has left
    // in their monthly usage quota
    private int promptsRemaining;


    /**
     * Constructor of PersonalPlan class.
     * This constructor initializes both:
     * 1. The attributes from the parent class (AIModel)
     * 2. The promptsRemaining attribute from this class
     */
    public PersonalPlan(String modelName, double price, int parameterCount, int windowSize, int promptsRemaining)
    {
        // Calling the constructor of the parent class (AIModel)
        // This initializes modelName, price, parameterCount, and windowSize
        super(modelName, price, parameterCount, windowSize);

        // Assigning the remaining prompt count to the attribute of this class
        this.promptsRemaining = promptsRemaining;
    }


    /**
     * Getter method to retrieve the number of prompts remaining.
     * This allows other classes to check how many prompts are left.
     */
    public int getPromptsRemaining()
    {
        // Returning the value of promptsRemaining
        return promptsRemaining;
    }


    /**
     * This method allows the user to purchase additional prompts.
     * The user enters the number of prompts they want to add.
     * The method also checks if the entered value is valid.
     */
    public String purchasePrompts(int additionalPrompts)
    {
        // Checking if the user entered a negative or zero value
        if (additionalPrompts <= 0)
        {
            // Returning an error message if value is invalid
            return "Error: Must enter positive value or upgrade to Pro Plan.";
        }

        // Adding the purchased prompts to the existing remaining prompts
        promptsRemaining += additionalPrompts;

        // Returning confirmation message showing new prompt balance
        return "Successfully added " + additionalPrompts + " prompts. "
                + "Total remaining: " + promptsRemaining;
    }


    /**
     * This method simulates the user entering a prompt to the AI model.
     * It checks if the user still has prompts available.
     * If yes, it decreases the prompt count and processes the request.
     * If not, it shows an error message.
     */
    public String enterPrompt(String promptText, int responseLength)
    {
        // Checking if the user still has prompts available
        if (promptsRemaining > 0)
        {
            // Reducing the number of remaining prompts by 1
            promptsRemaining--;

            // Returning success message along with prompt details
            return "Prompt entered successfully!\n"
                    + "Prompt: " + promptText + "\n"
                    + "Expected Tokens: " + responseLength + "\n"
                    + "Remaining Prompts: " + promptsRemaining;
        }
        else
        {
            // If there are no prompts left, return an error message
            return "Error: Monthly quota reached. Please purchase additional prompts or upgrade to Pro Plan.";
        }
    }


    /**
     * This method overrides the display() method from the parent class.
     * It first shows the AIModel information and then adds
     * additional information specific to PersonalPlan.
     */
    @Override
    public String display()
    {
        // Calling the display() method from AIModel class
        // and then adding PersonalPlan specific information
        return super.display()
                + "\nPlan Type: Personal Plan"
                + "\nRemaining Prompts: " + promptsRemaining;
    }
}