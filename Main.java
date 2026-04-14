/*
 * Main.java
 * This program is used to test the functionality of
 * PersonalPlan and ProPlan classes.
 * It simulates different scenarios such as:
 * - Entering prompts
 * - Exceeding quota
 * - Purchasing more prompts
 * - Managing Pro Plan team members
 * - Displaying plan details
 *
 * @author Saman Khanal
 * @version 1.1
 */

public class Main
{
    public static void main(String[] args)
    {

        // ---------------------------------------------------------
        // Creating objects for testing the system
        // ---------------------------------------------------------

        // Creating a PersonalPlan object with a small prompt quota
        // so that we can easily test quota exhaustion
        PersonalPlan personal = new PersonalPlan(
                "MiniGPT",
                4.99,
                12,
                2048,
                2);   // only 2 prompts allowed initially


        // Creating a ProPlan object with limited team slots
        ProPlan pro = new ProPlan(
                "TeamGPT",
                19.99,
                65,
                4096,
                3);   // 3 available team slots



        // ---------------------------------------------------------
        // TEST 1 : Normal Prompt Execution
        // ---------------------------------------------------------

        System.out.println("TEST 1 : Submitting a Normal Prompt");

        // Sending a prompt to the Personal Plan model
        String result1 = personal.enterPrompt("Explain what Artificial Intelligence is.", 120);

        // Printing the response from the system
        System.out.println(result1);



        // TEST 2 : Simulating Heavy Prompt Usage

        System.out.println("TEST 2 : Simulating Large Token Usage");

        // Creating a long prompt to simulate heavy usage
        String longPrompt =
                "This is a long request asking the AI model to generate a "
                + "detailed explanation about the history of machine learning "
                + "and its impact on modern technology.";

        // Sending the prompt with a large expected response
        String result2 = personal.enterPrompt(longPrompt, 1800);

        // Displaying the result
        System.out.println(result2);



        // TEST 3 : Personal Plan Prompt Quota Exhaustion

        System.out.println("TEST 3 : Exhausting Personal Plan Prompt Limit");

        // Using prompts until the quota runs out
        System.out.println(personal.enterPrompt("Give 3 examples of AI applications.", 80));
        System.out.println(personal.enterPrompt("Explain neural networks simply.", 90));

        // This prompt should fail because quota is already used
        System.out.println(personal.enterPrompt("What is reinforcement learning?", 100));

        // Purchasing additional prompts after quota exhaustion
        System.out.println(personal.purchasePrompts(4));



        // TEST 4 : Pro Plan Team Collaboration

        System.out.println("TEST 4 : Managing Pro Plan Team Members");

        // Adding team members
        System.out.println(pro.addTeamMember("Alice"));
        System.out.println(pro.addTeamMember("Bob"));
        System.out.println(pro.addTeamMember("Charlie"));

        // Attempting to add a member when slots are full
        System.out.println(pro.addTeamMember("David"));

        // Removing a team member
        System.out.println(pro.removeTeamMember("Bob"));

        // Adding another member after a slot becomes available
        System.out.println(pro.addTeamMember("David"));



        // FINAL SECTION : Display Plan Details

        System.out.println("FINAL OUTPUT : Displaying Plan Information");

        // Displaying information for Personal Plan
        System.out.println("\n--- Personal Plan Information ---");
        System.out.println(personal.display());

        // Displaying information for Pro Plan
        System.out.println("\n--- Pro Plan Information ---");
        System.out.println(pro.display());

    }
}