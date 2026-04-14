import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;

/**
 * SubscriptionGUI provides a graphical interface for managing
 * AI subscription plans (PersonalPlan and ProPlan).
 *
 * Layouts used:
 *   BorderLayout  - main JFrame
 *   GridLayout    - north input panel (Model Name, Pricing, Parameters, Context Window)
 *   BoxLayout     - centre panel (vertical stack)
 *   FlowLayout    - card-switcher row and south button panel
 *   CardLayout    - switches between Personal and Pro management cards
 *   GridBagLayout - inside each card for labelled fields
 *
 * Both AWT (layouts, Color, Font, Insets, ActionEvent/Listener) and
 * Swing (JFrame, JPanel, JLabel, JTextField, JTextArea, JButton,
 * JScrollPane, BoxLayout, JComboBox, SwingUtilities) are used.
 *
 * @author (Saman Khanal)
 * @date (14-04-2026)
 */
public class SubscriptionGUI extends JFrame implements ActionListener
{
    // ArrayList to hold all AI subscription plan objects
    private ArrayList<AIModel> plans;

    // ----- Fields: AIModel base attributes -----
    private JTextField modelNameField;
    private JTextField pricingField;
    private JTextField parametersField;
    private JTextField contextWindowField;

    // ----- Fields: Personal Plan -----
    private JTextField promptsRemainingField;
    private JTextField promptTextField;
    private JTextField responseLengthField;
    private JTextField purchaseAmountField;

    // ----- Fields: Pro Plan -----
    private JTextField slotsField;
    private JTextField memberNameField;

    // ----- Output area -----
    private JTextArea outputArea;

    // ----- Main action buttons -----
    private JButton addPersonalBtn;
    private JButton addProBtn;
    private JButton displayAllBtn;
    private JButton clearBtn;

    // ----- Personal Plan action buttons -----
    private JButton enterPromptBtn;
    private JButton purchasePromptsBtn;

    // ----- Pro Plan action buttons -----
    private JButton addMemberBtn;
    private JButton removeMemberBtn;

    // ----- CardLayout references -----
    private JPanel cardPanel;
    private CardLayout cardLayout;

    private static final String PERSONAL_CARD = "Personal Plan";
    private static final String PRO_CARD       = "Pro Plan";

    // ---------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------

    /**
     * Constructs the SubscriptionGUI window and makes it visible.
     */
    public SubscriptionGUI()
    {
        super("AI Subscription Manager");

        plans = new ArrayList<AIModel>();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 680);
        setLocationRelativeTo(null);

        initComponents();

        setVisible(true);
    }

    // ---------------------------------------------------------------
    // GUI construction helpers
    // ---------------------------------------------------------------

    /**
     * Assembles all panels into the main frame (BorderLayout).
     */
    private void initComponents()
    {
        // Main frame uses AWT BorderLayout
        setLayout(new BorderLayout(10, 10));

        add(createInputPanel(),  BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
        add(createOutputPanel(), BorderLayout.EAST);
    }

    /**
     * North panel — AWT GridLayout (5 rows × 2 cols).
     * Holds labels and text fields for the four AIModel attributes.
     */
    private JPanel createInputPanel()
    {
        // AWT GridLayout
        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("AI Model Details"));
        panel.setBackground(new Color(230, 240, 255));

        // Header row
        panel.add(new JLabel("  Field",  SwingConstants.LEFT));
        panel.add(new JLabel("  Value",  SwingConstants.LEFT));

        // Model Name
        panel.add(new JLabel("  Model Name:"));
        modelNameField = new JTextField(15);
        panel.add(modelNameField);

        // Pricing
        panel.add(new JLabel("  Pricing ($):"));
        pricingField = new JTextField(15);
        panel.add(pricingField);

        // Parameters
        panel.add(new JLabel("  Parameters (billions):"));
        parametersField = new JTextField(15);
        panel.add(parametersField);

        // Context Window
        panel.add(new JLabel("  Context Window (tokens):"));
        contextWindowField = new JTextField(15);
        panel.add(contextWindowField);

        return panel;
    }

    /**
     * Centre panel — Swing BoxLayout (vertical).
     * Contains a FlowLayout card-switcher row and a CardLayout panel.
     */
    private JPanel createCenterPanel()
    {
        // Swing BoxLayout (vertical)
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createTitledBorder("Plan Management"));

        // Card-switcher row (AWT FlowLayout)
        JPanel switcherPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        switcherPanel.add(new JLabel("Select Plan Type: "));
        JComboBox<String> planSwitcher = new JComboBox<>(new String[]{ PERSONAL_CARD, PRO_CARD });
        planSwitcher.addActionListener(e ->
            cardLayout.show(cardPanel, (String) planSwitcher.getSelectedItem()));
        switcherPanel.add(planSwitcher);

        // Card panel (AWT CardLayout)
        cardLayout = new CardLayout();
        cardPanel  = new JPanel(cardLayout);
        cardPanel.add(createPersonalCard(), PERSONAL_CARD);
        cardPanel.add(createProCard(),      PRO_CARD);

        centerPanel.add(switcherPanel);
        centerPanel.add(Box.createVerticalStrut(5));
        centerPanel.add(cardPanel);

        return centerPanel;
    }

    /**
     * Personal Plan card — AWT GridBagLayout.
     * Fields: Prompts Remaining, Prompt Text, Response Length, Purchase Amount.
     */
    private JPanel createPersonalCard()
    {
        JPanel panel = new JPanel(new GridBagLayout()); // AWT GridBagLayout
        panel.setBorder(BorderFactory.createTitledBorder("Personal Plan - Prompt Management"));
        panel.setBackground(new Color(255, 250, 230));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = new Insets(4, 4, 4, 4);
        gbc.anchor  = GridBagConstraints.WEST;

        // Row 0 — Prompts Remaining
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Prompts Remaining:"), gbc);
        gbc.gridx = 1;
        promptsRemainingField = new JTextField(8);
        panel.add(promptsRemainingField, gbc);

        // Row 1 — Prompt Text
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Prompt Text:"), gbc);
        gbc.gridx = 1;
        promptTextField = new JTextField(20);
        panel.add(promptTextField, gbc);

        // Row 2 — Response Length
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Response Length (tokens):"), gbc);
        gbc.gridx = 1;
        responseLengthField = new JTextField(8);
        panel.add(responseLengthField, gbc);

        // Row 3 — Enter Prompt button
        gbc.gridx = 1; gbc.gridy = 3;
        enterPromptBtn = new JButton("Enter Prompt");
        enterPromptBtn.setBackground(new Color(100, 180, 100));
        enterPromptBtn.setForeground(Color.WHITE);
        enterPromptBtn.addActionListener(this);
        panel.add(enterPromptBtn, gbc);

        // Row 4 — Purchase Amount
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Purchase Prompts:"), gbc);
        gbc.gridx = 1;
        purchaseAmountField = new JTextField(8);
        panel.add(purchaseAmountField, gbc);

        // Row 5 — Purchase Prompts button
        gbc.gridx = 1; gbc.gridy = 5;
        purchasePromptsBtn = new JButton("Purchase Prompts");
        purchasePromptsBtn.setBackground(new Color(80, 140, 200));
        purchasePromptsBtn.setForeground(Color.WHITE);
        purchasePromptsBtn.addActionListener(this);
        panel.add(purchasePromptsBtn, gbc);

        return panel;
    }

    /**
     * Pro Plan card — AWT GridBagLayout.
     * Fields: Available Slots, Team Member Name.
     */
    private JPanel createProCard()
    {
        JPanel panel = new JPanel(new GridBagLayout()); // AWT GridBagLayout
        panel.setBorder(BorderFactory.createTitledBorder("Pro Plan - Team Collaboration"));
        panel.setBackground(new Color(230, 255, 240));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        // Row 0 — Available Slots
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Available Slots:"), gbc);
        gbc.gridx = 1;
        slotsField = new JTextField(8);
        panel.add(slotsField, gbc);

        // Row 1 — Member Name
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Team Member Name:"), gbc);
        gbc.gridx = 1;
        memberNameField = new JTextField(15);
        panel.add(memberNameField, gbc);

        // Row 2 — Add / Remove buttons
        gbc.gridx = 0; gbc.gridy = 2;
        addMemberBtn = new JButton("Add Team Member");
        addMemberBtn.setBackground(new Color(100, 180, 100));
        addMemberBtn.setForeground(Color.WHITE);
        addMemberBtn.addActionListener(this);
        panel.add(addMemberBtn, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        removeMemberBtn = new JButton("Remove Team Member");
        removeMemberBtn.setBackground(new Color(200, 80, 80));
        removeMemberBtn.setForeground(Color.WHITE);
        removeMemberBtn.addActionListener(this);
        panel.add(removeMemberBtn, gbc);

        return panel;
    }

    /**
     * South button panel — AWT FlowLayout.
     * Buttons: Add Personal Plan, Add Pro Plan, Display All, Clear.
     */
    private JPanel createButtonPanel()
    {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); // AWT FlowLayout
        panel.setBackground(new Color(210, 220, 240));

        addPersonalBtn = new JButton("Add Personal Plan");
        addPersonalBtn.setBackground(new Color(70, 130, 180));
        addPersonalBtn.setForeground(Color.WHITE);
        addPersonalBtn.addActionListener(this);

        addProBtn = new JButton("Add Pro Plan");
        addProBtn.setBackground(new Color(100, 60, 160));
        addProBtn.setForeground(Color.WHITE);
        addProBtn.addActionListener(this);

        displayAllBtn = new JButton("Display All");
        displayAllBtn.setBackground(new Color(60, 150, 90));
        displayAllBtn.setForeground(Color.WHITE);
        displayAllBtn.addActionListener(this);

        clearBtn = new JButton("Clear");
        clearBtn.setBackground(new Color(180, 80, 80));
        clearBtn.setForeground(Color.WHITE);
        clearBtn.addActionListener(this);

        panel.add(addPersonalBtn);
        panel.add(addProBtn);
        panel.add(displayAllBtn);
        panel.add(clearBtn);

        return panel;
    }

    /**
     * East output panel — BorderLayout (Swing JScrollPane + JTextArea).
     */
    private JPanel createOutputPanel()
    {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Output"));

        outputArea = new JTextArea(20, 25);
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        outputArea.setBackground(new Color(245, 245, 245));

        JScrollPane scrollPane = new JScrollPane(outputArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // ---------------------------------------------------------------
    // ActionListener
    // ---------------------------------------------------------------

    /**
     * Routes button clicks to their respective handler methods.
     */
    @Override
    public void actionPerformed(ActionEvent e)
    {
        Object source = e.getSource();

        if      (source == addPersonalBtn)    { addPersonalPlan();         }
        else if (source == addProBtn)         { addProPlan();              }
        else if (source == displayAllBtn)     { displayAll();              }
        else if (source == clearBtn)          { clearAll();                }
        else if (source == enterPromptBtn)    { enterPromptAction();       }
        else if (source == purchasePromptsBtn){ purchasePromptsAction();   }
        else if (source == addMemberBtn)      { addTeamMemberAction();     }
        else if (source == removeMemberBtn)   { removeTeamMemberAction();  }
    }

    // ---------------------------------------------------------------
    // Business logic helpers
    // ---------------------------------------------------------------

    /**
     * Reads and validates the four common AIModel fields.
     * Returns a String array {name, price, params, window} or null on error.
     */
    private String[] readCommonFields()
    {
        String name      = modelNameField.getText().trim();
        String priceStr  = pricingField.getText().trim();
        String paramsStr = parametersField.getText().trim();
        String windowStr = contextWindowField.getText().trim();

        if (name.isEmpty() || priceStr.isEmpty() || paramsStr.isEmpty() || windowStr.isEmpty())
        {
            outputArea.setText("Error: Please fill in all Model Details fields.");
            return null;
        }

        try
        {
            Double.parseDouble(priceStr);
            Integer.parseInt(paramsStr);
            Integer.parseInt(windowStr);
        }
        catch (NumberFormatException ex)
        {
            outputArea.setText("Error: Pricing must be a decimal number; "
                    + "Parameters and Context Window must be integers.");
            return null;
        }

        return new String[]{ name, priceStr, paramsStr, windowStr };
    }

    /**
     * Returns the most-recently added PersonalPlan, or null if none exist.
     */
    private PersonalPlan findLastPersonalPlan()
    {
        PersonalPlan found = null;
        for (AIModel m : plans)
        {
            if (m instanceof PersonalPlan)
            {
                found = (PersonalPlan) m;
            }
        }
        return found;
    }

    /**
     * Returns the most-recently added ProPlan, or null if none exist.
     */
    private ProPlan findLastProPlan()
    {
        ProPlan found = null;
        for (AIModel m : plans)
        {
            if (m instanceof ProPlan)
            {
                found = (ProPlan) m;
            }
        }
        return found;
    }

    // ---------------------------------------------------------------
    // Button handlers
    // ---------------------------------------------------------------

    /**
     * Reads fields, creates a PersonalPlan, and adds it to the ArrayList.
     */
    private void addPersonalPlan()
    {
        String[] common = readCommonFields();
        if (common == null) return;

        String promptsStr = promptsRemainingField.getText().trim();
        if (promptsStr.isEmpty())
        {
            outputArea.setText("Error: Please enter Prompts Remaining for the Personal Plan.");
            return;
        }

        int prompts;
        try
        {
            prompts = Integer.parseInt(promptsStr);
        }
        catch (NumberFormatException ex)
        {
            outputArea.setText("Error: Prompts Remaining must be an integer.");
            return;
        }

        PersonalPlan plan = new PersonalPlan(
                common[0],
                Double.parseDouble(common[1]),
                Integer.parseInt(common[2]),
                Integer.parseInt(common[3]),
                prompts);

        plans.add(plan);
        outputArea.setText("Personal Plan added successfully! "
                + "(Total plans: " + plans.size() + ")\n\n" + plan.display());
    }

    /**
     * Reads fields, creates a ProPlan, and adds it to the ArrayList.
     */
    private void addProPlan()
    {
        String[] common = readCommonFields();
        if (common == null) return;

        String slotsStr = slotsField.getText().trim();
        if (slotsStr.isEmpty())
        {
            outputArea.setText("Error: Please enter Available Slots for the Pro Plan.");
            return;
        }

        int slots;
        try
        {
            slots = Integer.parseInt(slotsStr);
        }
        catch (NumberFormatException ex)
        {
            outputArea.setText("Error: Available Slots must be an integer.");
            return;
        }

        ProPlan plan = new ProPlan(
                common[0],
                Double.parseDouble(common[1]),
                Integer.parseInt(common[2]),
                Integer.parseInt(common[3]),
                slots);

        plans.add(plan);
        outputArea.setText("Pro Plan added successfully! "
                + "(Total plans: " + plans.size() + ")\n\n" + plan.display());
    }

    /**
     * Iterates over the ArrayList and prints every plan's display() text.
     */
    private void displayAll()
    {
        if (plans.isEmpty())
        {
            outputArea.setText("No plans available. Please add a plan first.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== All Subscription Plans (").append(plans.size()).append(") ===\n\n");

        for (int i = 0; i < plans.size(); i++)
        {
            sb.append("--- Plan ").append(i + 1).append(" ---\n");
            sb.append(plans.get(i).display()).append("\n\n");
        }

        outputArea.setText(sb.toString());
    }

    /**
     * Clears all input fields and the output area.
     */
    private void clearAll()
    {
        modelNameField.setText("");
        pricingField.setText("");
        parametersField.setText("");
        contextWindowField.setText("");
        promptsRemainingField.setText("");
        promptTextField.setText("");
        responseLengthField.setText("");
        purchaseAmountField.setText("");
        slotsField.setText("");
        memberNameField.setText("");
        outputArea.setText("");
    }

    /**
     * Calls enterPrompt() on the most-recently added PersonalPlan.
     */
    private void enterPromptAction()
    {
        PersonalPlan plan = findLastPersonalPlan();
        if (plan == null)
        {
            outputArea.setText("Error: No Personal Plan found. Please add a Personal Plan first.");
            return;
        }

        String prompt    = promptTextField.getText().trim();
        String lengthStr = responseLengthField.getText().trim();

        if (prompt.isEmpty() || lengthStr.isEmpty())
        {
            outputArea.setText("Error: Please enter Prompt Text and Response Length.");
            return;
        }

        int length;
        try
        {
            length = Integer.parseInt(lengthStr);
        }
        catch (NumberFormatException ex)
        {
            outputArea.setText("Error: Response Length must be an integer.");
            return;
        }

        outputArea.setText("Plan: " + plan.getModelName() + "\n\n"
                + plan.enterPrompt(prompt, length));
    }

    /**
     * Calls purchasePrompts() on the most-recently added PersonalPlan.
     */
    private void purchasePromptsAction()
    {
        PersonalPlan plan = findLastPersonalPlan();
        if (plan == null)
        {
            outputArea.setText("Error: No Personal Plan found. Please add a Personal Plan first.");
            return;
        }

        String amountStr = purchaseAmountField.getText().trim();
        if (amountStr.isEmpty())
        {
            outputArea.setText("Error: Please enter the number of prompts to purchase.");
            return;
        }

        int amount;
        try
        {
            amount = Integer.parseInt(amountStr);
        }
        catch (NumberFormatException ex)
        {
            outputArea.setText("Error: Purchase amount must be an integer.");
            return;
        }

        outputArea.setText("Plan: " + plan.getModelName() + "\n\n"
                + plan.purchasePrompts(amount));
    }

    /**
     * Calls addTeamMember() on the most-recently added ProPlan.
     */
    private void addTeamMemberAction()
    {
        ProPlan plan = findLastProPlan();
        if (plan == null)
        {
            outputArea.setText("Error: No Pro Plan found. Please add a Pro Plan first.");
            return;
        }

        String memberName = memberNameField.getText().trim();
        if (memberName.isEmpty())
        {
            outputArea.setText("Error: Please enter a Team Member Name.");
            return;
        }

        outputArea.setText("Plan: " + plan.getModelName() + "\n\n"
                + plan.addTeamMember(memberName));
    }

    /**
     * Calls removeTeamMember() on the most-recently added ProPlan.
     */
    private void removeTeamMemberAction()
    {
        ProPlan plan = findLastProPlan();
        if (plan == null)
        {
            outputArea.setText("Error: No Pro Plan found. Please add a Pro Plan first.");
            return;
        }

        String memberName = memberNameField.getText().trim();
        if (memberName.isEmpty())
        {
            outputArea.setText("Error: Please enter a Team Member Name to remove.");
            return;
        }

        outputArea.setText("Plan: " + plan.getModelName() + "\n\n"
                + plan.removeTeamMember(memberName));
    }

    // ---------------------------------------------------------------
    // Entry point
    // ---------------------------------------------------------------

    /**
     * Launches the SubscriptionGUI on the Swing event-dispatch thread.
     */
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() -> new SubscriptionGUI());
    }
}
