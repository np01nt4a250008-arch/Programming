/**
 * This class represents an AI Model.
 * It stores basic information like model name, price,
 * parameter count and window size.
 *
 * @author (Saman Khanal)
 * @date (23-01-2026)
 */

public class AIModel
{
    // ---------------------- Attributes ----------------------

    private String modelName;      // Variable to store the name of the AI model
    private double price;          // Variable to store the price of the model
    private int parameterCount;    // Variable to store the number of parameters
    private int windowSize;        // Variable to store the window/context size


    // ---------------------- Constructor ----------------------

    // Constructor used to initialize the object with given values
    public AIModel(String model1, double price1, int parameter1, int window1)
    {
        this.modelName = model1;        // Assigning the model name to the attribute
        this.price = price1;            // Assigning the price value
        this.parameterCount = parameter1; // Assigning the parameter count value
        this.windowSize = window1;      // Assigning the window size value
    }


    // ---------------------- Getter Methods ----------------------

    // Getter method to return the model name
    public String getModelName()
    {
        return this.modelName;   // Returns the model name
    }

    // Getter method to return the price
    public double getPrice()
    {
        return this.price;   // Returns the price of the model
    }

    // Getter method to return the parameter count
    public int getParameterCount()
    {
        return this.parameterCount;   // Returns the number of parameters
    }

    // Getter method to return the window size
    public int getWindowSize()
    {
        return this.windowSize;   // Returns the window/context size
    }


    // ---------------------- Display Method ----------------------

    // This method returns the model information as a formatted string
    public String display()
    {
        return "Model name is: " + this.modelName + "\n"
                + "Price is: " + this.price + "\n"
                + "Parameter Count is: " + this.parameterCount + "\n"
                + "Window size is: " + this.windowSize;
    }
}