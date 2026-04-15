import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * subscriptiongui stores simple subscription details and supports
 * exporting to and loading from a text file.
 */
public class subscriptiongui
{
    private String subscriberName;
    private String planType;
    private int promptsRemaining;
    private int availableSlots;

    public subscriptiongui()
    {
        this.subscriberName = "";
        this.planType = "Personal";
        this.promptsRemaining = 0;
        this.availableSlots = 0;
    }

    public subscriptiongui(String subscriberName, String planType, int promptsRemaining, int availableSlots)
    {
        this.subscriberName = subscriberName;
        this.planType = planType;
        this.promptsRemaining = promptsRemaining;
        this.availableSlots = availableSlots;
    }

    /**
     * Exports subscription data to a file.
     * FileWriter + BufferedWriter + PrintWriter are used for buffered text output.
     */
    public String exportToFile(String filePath)
    {
        // try-catch is used to safely handle file writing errors.
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(filePath))))
        {
            writer.println("subscriberName=" + subscriberName);
            writer.println("planType=" + planType);
            writer.println("promptsRemaining=" + promptsRemaining);
            writer.println("availableSlots=" + availableSlots);
            return "Subscription data exported successfully to: " + filePath;
        }
        catch (IOException e)
        {
            return "Error exporting file: " + e.getMessage();
        }
    }

    /**
     * Loads subscription data from a file.
     * FileReader + Scanner are used to read text line-by-line.
     */
    public String loadFromFile(String filePath)
    {
        // try-catch is used to safely handle file reading and parse errors.
        try (Scanner scanner = new Scanner(new FileReader(filePath)))
        {
            int lineNumber = 0;
            StringBuilder invalidLines = new StringBuilder();

            while (scanner.hasNextLine())
            {
                lineNumber++;
                String line = scanner.nextLine();
                String[] parts = line.split("=", 2);
                if (parts.length != 2)
                {
                    if (invalidLines.length() == 0)
                    {
                        invalidLines.append("Ignored malformed lines: ");
                    }
                    invalidLines.append(lineNumber).append(" ");
                    continue;
                }

                String key = parts[0].trim();
                String value = parts[1].trim();

                if (key.equals("subscriberName"))
                {
                    subscriberName = value;
                }
                else if (key.equals("planType"))
                {
                    planType = value;
                }
                else if (key.equals("promptsRemaining"))
                {
                    try
                    {
                        promptsRemaining = Integer.parseInt(value);
                    }
                    catch (NumberFormatException e)
                    {
                        return "Error parsing field 'promptsRemaining': " + value;
                    }
                }
                else if (key.equals("availableSlots"))
                {
                    try
                    {
                        availableSlots = Integer.parseInt(value);
                    }
                    catch (NumberFormatException e)
                    {
                        return "Error parsing field 'availableSlots': " + value;
                    }
                }
            }
            if (invalidLines.length() > 0)
            {
                return "Subscription data loaded from: " + filePath + ". " + invalidLines.toString().trim();
            }
            return "Subscription data loaded successfully from: " + filePath;
        }
        catch (IOException e)
        {
            return "Error loading file: " + e.getMessage();
        }
    }

    public String display()
    {
        return "Subscriber Name: " + subscriberName + "\n"
                + "Plan Type: " + planType + "\n"
                + "Prompts Remaining: " + promptsRemaining + "\n"
                + "Available Slots: " + availableSlots;
    }
}
