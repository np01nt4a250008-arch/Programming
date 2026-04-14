/**
 * This class represents a Pro Plan subscription for an AI model.
 * The Pro Plan allows teams to collaborate by adding multiple members.
 * It inherits basic AI model information from the AIModel class
 * and adds functionality related to team member slots.
 *
 * @author (Saman Khanal)
 * @date (05-03-2025)
 */

// ProPlan class inherits attributes and methods from the AIModel parent class
public class ProPlan extends AIModel
{
    // This variable stores the number of available slots
    // that can be used to add team members to the Pro Plan
    private int availableSlots;


    /**
     * Constructor of the ProPlan class.
     * This constructor initializes both the AIModel attributes
     * (inherited from the parent class) and the availableSlots attribute
     * specific to the ProPlan class.
     */
    public ProPlan(String modelName, double price, int parameterCount, int windowSize, int availableSlots)
    {
        // Calling the constructor of the parent class (AIModel)
        // This initializes modelName, price, parameterCount and windowSize
        super(modelName, price, parameterCount, windowSize);

        // Assigning the number of available team slots to this class variable
        this.availableSlots = availableSlots;
    }


    /**
     * Getter method used to retrieve the number of
     * available team member slots in the Pro Plan.
     */
    public int getAvailableSlots()
    {
        // Returning the current number of available slots
        return availableSlots;
    }


    /**
     * This method allows a new team member to be added
     * to the Pro Plan if slots are still available.
     * When a member is added, the available slot count decreases.
     */
    public String addTeamMember(String memberName)
    {
        // Checking if there is at least one slot available
        if (availableSlots > 0)
        {
            // Decreasing the number of available slots by 1
            availableSlots--;

            // Returning success message showing the added member
            return "Team member '" + memberName + "' added successfully. "
                    + "Remaining slots: " + availableSlots;
        }
        else
        {
            // If no slots are left, return an error message
            return "Error: No available slots. Cannot add team member '" + memberName + "'.";
        }
    }


    /**
     * This method removes a team member from the Pro Plan.
     * When a member is removed, a slot becomes available again.
     */
    public String removeTeamMember(String memberName)
    {
        // Increasing the number of available slots since a member left
        availableSlots++;

        // Returning confirmation message showing updated slot count
        return "Team member '" + memberName + "' removed. "
                + "Available slots: " + availableSlots;
    }


    /**
     * This method overrides the display() method from the AIModel class.
     * It first calls the parent display() method to show AI model details
     * and then adds Pro Plan specific information like team slots.
     */
    @Override
    public String display()
    {
        // Calling the display() method from the parent class (AIModel)
        // and then adding extra details related to the Pro Plan
        return super.display()
                + "\nPlan Type: Pro Plan"
                + "\nAvailable Team Slots: " + availableSlots;
    }
}