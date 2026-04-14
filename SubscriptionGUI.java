import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.util.ArrayList;

/**
 * SubscriptionGUI — a dark-themed graphical interface for managing
 * AI subscription plans (PersonalPlan and ProPlan).
 *
 * Layouts used:
 *   BorderLayout  - main JFrame zones (N/C/S/E)
 *   GridLayout    - north input panel (Model Name, Pricing, Parameters, Context Window)
 *   BoxLayout     - centre panel (vertical stack of switcher + cards)
 *   FlowLayout    - card-switcher row and south button panel
 *   CardLayout    - swaps between the Personal and Pro management panels
 *   GridBagLayout - inside each card for neatly aligned labelled fields
 *
 * Both AWT (layouts, Color, Font, Insets, ActionEvent/Listener) and
 * Swing (JFrame, JPanel, JLabel, JTextField, JTextArea, JButton,
 * JScrollPane, BoxLayout, JComboBox, SwingUtilities) are used.
 *
 * Color scheme inspired by GitHub Copilot's dark UI.
 *
 * @author (Saman Khanal)
 * @date (14-04-2026)
 */
public class SubscriptionGUI extends JFrame implements ActionListener
{
    // ---------------------------------------------------------------
    // GitHub Copilot-inspired color palette
    // ---------------------------------------------------------------

    // The darkest background — used for the frame and text fields
    private static final Color BG_DARK    = new Color(13,  17,  23);
    // Slightly lighter — used for panel backgrounds so they stand out
    private static final Color BG_PANEL   = new Color(22,  27,  34);
    // The subtle border colour that separates sections without shouting
    private static final Color BORDER_CLR = new Color(48,  54,  61);
    // Main body text — a soft off-white, easy on the eyes
    private static final Color FG_TEXT    = new Color(201, 209, 217);
    // Accent blue used for titles and the Copilot "glow" feel
    private static final Color ACCENT     = new Color(88,  166, 255);
    // Green button — adding things, positive actions
    private static final Color BTN_GREEN  = new Color(35,  134,  54);
    // Red button — removing things, destructive actions
    private static final Color BTN_RED    = new Color(218,  54,  51);
    // Blue button — informational / primary action
    private static final Color BTN_BLUE   = new Color(31,  111, 235);
    // Purple button — Copilot's signature colour, used for the Pro plan
    private static final Color BTN_PURPLE = new Color(130,  80, 228);

    // Shared font sizes — bump these if the screen is too small/large
    private static final Font FONT_LABEL  = new Font("SansSerif", Font.PLAIN, 14);
    private static final Font FONT_BUTTON = new Font("SansSerif", Font.BOLD,  14);
    private static final Font FONT_FIELD  = new Font("SansSerif", Font.PLAIN, 14);
    private static final Font FONT_TITLE  = new Font("SansSerif", Font.BOLD,  14);
    private static final Font FONT_MONO   = new Font("Monospaced", Font.PLAIN, 14);

    // ---------------------------------------------------------------
    // All the plans the user has created so far
    // ---------------------------------------------------------------
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

    // ----- Output area — where results and success messages are shown -----
    private JTextArea outputArea;

    // ----- Main action buttons at the bottom -----
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

    // ----- CardLayout references — used to swap between the two plan cards -----
    private JPanel cardPanel;
    private CardLayout cardLayout;

    private static final String PERSONAL_CARD = "Personal Plan";
    private static final String PRO_CARD       = "Pro Plan";

    // ---------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------

    /**
     * Builds the window, wires everything together, and makes it visible.
     */
    public SubscriptionGUI()
    {
        super("AI Subscription Manager");

        // Start with an empty list — the user will fill it up
        plans = new ArrayList<AIModel>();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Wider than before to give the output panel more breathing room
        setSize(1100, 720);
        setLocationRelativeTo(null);

        // Dark background on the content pane itself
        getContentPane().setBackground(BG_DARK);

        initComponents();

        setVisible(true);
    }

    // ---------------------------------------------------------------
    // GUI construction helpers
    // ---------------------------------------------------------------

    /**
     * Plugs all the sub-panels into the main frame using BorderLayout.
     * Think of this as the skeleton everything else hangs off.
     */
    private void initComponents()
    {
        // Main frame uses AWT BorderLayout with a bit of gap between zones
        setLayout(new BorderLayout(10, 10));

        add(createInputPanel(),  BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
        add(createOutputPanel(), BorderLayout.EAST);
    }

    /**
     * A small helper so every titled border looks consistently Copilot-styled —
     * dark panel, accent-coloured title text, subtle outline.
     */
    private TitledBorder makeTitledBorder(String title)
    {
        return BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BORDER_CLR),
            title,
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION,
            FONT_TITLE,
            ACCENT
        );
    }

    /**
     * Styles a JTextField to match the dark theme — dark background,
     * light text, and the accent colour as a caret so it's still visible.
     */
    private void styleField(JTextField field)
    {
        field.setBackground(BG_DARK);
        field.setForeground(FG_TEXT);
        field.setCaretColor(ACCENT);
        field.setFont(FONT_FIELD);
        // A thin border so the field boundary is visible against the dark panel
        field.setBorder(BorderFactory.createLineBorder(BORDER_CLR));
    }

    /**
     * Styles a JLabel with the shared light text colour and font size.
     */
    private void styleLabel(JLabel label)
    {
        label.setForeground(FG_TEXT);
        label.setFont(FONT_LABEL);
    }

    /**
     * Styles a JButton — sets its colours and font, and removes the focus
     * ring that looks odd on dark backgrounds.
     */
    private void styleButton(JButton btn, Color bg)
    {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(FONT_BUTTON);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
    }

    /**
     * North panel — AWT GridLayout (5 rows × 2 cols).
     * Holds labels and slightly narrower text fields for the four AIModel attributes.
     * Columns are a bit smaller (11) than before to keep this panel compact.
     */
    private JPanel createInputPanel()
    {
        // AWT GridLayout — one row per field, label on the left, field on the right
        JPanel panel = new JPanel(new GridLayout(5, 2, 6, 6));
        panel.setBorder(makeTitledBorder("AI Model Details"));
        panel.setBackground(BG_PANEL);

        // Header row — just labels to explain the columns
        JLabel fieldHeader = new JLabel("  Field", SwingConstants.LEFT);
        JLabel valueHeader = new JLabel("  Value", SwingConstants.LEFT);
        styleLabel(fieldHeader);
        styleLabel(valueHeader);
        panel.add(fieldHeader);
        panel.add(valueHeader);

        // Model Name row
        JLabel nameLabel = new JLabel("  Model Name:");
        styleLabel(nameLabel);
        panel.add(nameLabel);
        modelNameField = new JTextField(11); // slightly narrower than original 15
        styleField(modelNameField);
        panel.add(modelNameField);

        // Pricing row
        JLabel priceLabel = new JLabel("  Pricing ($):");
        styleLabel(priceLabel);
        panel.add(priceLabel);
        pricingField = new JTextField(11);
        styleField(pricingField);
        panel.add(pricingField);

        // Parameters row
        JLabel paramsLabel = new JLabel("  Parameters (billions):");
        styleLabel(paramsLabel);
        panel.add(paramsLabel);
        parametersField = new JTextField(11);
        styleField(parametersField);
        panel.add(parametersField);

        // Context Window row
        JLabel windowLabel = new JLabel("  Context Window (tokens):");
        styleLabel(windowLabel);
        panel.add(windowLabel);
        contextWindowField = new JTextField(11);
        styleField(contextWindowField);
        panel.add(contextWindowField);

        return panel;
    }

    /**
     * Centre panel — Swing BoxLayout (vertical).
     * Stacks the plan-type selector drop-down above the card area.
     */
    private JPanel createCenterPanel()
    {
        // Swing BoxLayout (vertical) — the switcher sits above the cards
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(makeTitledBorder("Plan Management"));
        centerPanel.setBackground(BG_PANEL);

        // Card-switcher row (AWT FlowLayout) — lets the user pick which card to show
        JPanel switcherPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        switcherPanel.setBackground(BG_PANEL);

        JLabel switcherLabel = new JLabel("Select Plan Type: ");
        styleLabel(switcherLabel);
        switcherPanel.add(switcherLabel);

        // The combo box that drives the CardLayout
        JComboBox<String> planSwitcher = new JComboBox<>(new String[]{ PERSONAL_CARD, PRO_CARD });
        planSwitcher.setBackground(BG_DARK);
        planSwitcher.setForeground(FG_TEXT);
        planSwitcher.setFont(FONT_LABEL);
        planSwitcher.addActionListener(e ->
            cardLayout.show(cardPanel, (String) planSwitcher.getSelectedItem()));
        switcherPanel.add(planSwitcher);

        // Card panel (AWT CardLayout) — only one card is visible at a time
        cardLayout = new CardLayout();
        cardPanel  = new JPanel(cardLayout);
        cardPanel.setBackground(BG_PANEL);
        cardPanel.add(createPersonalCard(), PERSONAL_CARD);
        cardPanel.add(createProCard(),      PRO_CARD);

        centerPanel.add(switcherPanel);
        centerPanel.add(Box.createVerticalStrut(5));
        centerPanel.add(cardPanel);

        return centerPanel;
    }

    /**
     * Personal Plan card — AWT GridBagLayout.
     * Handles prompts: entering them and buying more when they run out.
     */
    private JPanel createPersonalCard()
    {
        JPanel panel = new JPanel(new GridBagLayout()); // AWT GridBagLayout
        panel.setBorder(makeTitledBorder("Personal Plan - Prompt Management"));
        panel.setBackground(BG_PANEL);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = new Insets(6, 6, 6, 6);
        gbc.anchor  = GridBagConstraints.WEST;

        // Row 0 — How many prompts does this plan start with?
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel promptsLabel = new JLabel("Prompts Remaining:");
        styleLabel(promptsLabel);
        panel.add(promptsLabel, gbc);
        gbc.gridx = 1;
        promptsRemainingField = new JTextField(8);
        styleField(promptsRemainingField);
        panel.add(promptsRemainingField, gbc);

        // Row 1 — The actual text of the prompt the user wants to send
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel promptTextLabel = new JLabel("Prompt Text:");
        styleLabel(promptTextLabel);
        panel.add(promptTextLabel, gbc);
        gbc.gridx = 1;
        promptTextField = new JTextField(20);
        styleField(promptTextField);
        panel.add(promptTextField, gbc);

        // Row 2 — How long should the AI's response be (in tokens)?
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lengthLabel = new JLabel("Response Length (tokens):");
        styleLabel(lengthLabel);
        panel.add(lengthLabel, gbc);
        gbc.gridx = 1;
        responseLengthField = new JTextField(8);
        styleField(responseLengthField);
        panel.add(responseLengthField, gbc);

        // Row 3 — Submit the prompt to the plan
        gbc.gridx = 1; gbc.gridy = 3;
        enterPromptBtn = new JButton("Enter Prompt");
        styleButton(enterPromptBtn, BTN_GREEN);
        enterPromptBtn.addActionListener(this);
        panel.add(enterPromptBtn, gbc);

        // Row 4 — Top up the prompt balance
        gbc.gridx = 0; gbc.gridy = 4;
        JLabel purchaseLabel = new JLabel("Purchase Prompts:");
        styleLabel(purchaseLabel);
        panel.add(purchaseLabel, gbc);
        gbc.gridx = 1;
        purchaseAmountField = new JTextField(8);
        styleField(purchaseAmountField);
        panel.add(purchaseAmountField, gbc);

        // Row 5 — Actually trigger the purchase
        gbc.gridx = 1; gbc.gridy = 5;
        purchasePromptsBtn = new JButton("Purchase Prompts");
        styleButton(purchasePromptsBtn, BTN_BLUE);
        purchasePromptsBtn.addActionListener(this);
        panel.add(purchasePromptsBtn, gbc);

        return panel;
    }

    /**
     * Pro Plan card — AWT GridBagLayout.
     * Manages team collaboration slots — adding and removing members.
     */
    private JPanel createProCard()
    {
        JPanel panel = new JPanel(new GridBagLayout()); // AWT GridBagLayout
        panel.setBorder(makeTitledBorder("Pro Plan - Team Collaboration"));
        panel.setBackground(BG_PANEL);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;

        // Row 0 — Total team slots available on this plan
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel slotsLabel = new JLabel("Available Slots:");
        styleLabel(slotsLabel);
        panel.add(slotsLabel, gbc);
        gbc.gridx = 1;
        slotsField = new JTextField(8);
        styleField(slotsField);
        panel.add(slotsField, gbc);

        // Row 1 — The name of the person being added or removed
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel memberLabel = new JLabel("Team Member Name:");
        styleLabel(memberLabel);
        panel.add(memberLabel, gbc);
        gbc.gridx = 1;
        memberNameField = new JTextField(15);
        styleField(memberNameField);
        panel.add(memberNameField, gbc);

        // Row 2 — Add and Remove sit side-by-side for quick access
        gbc.gridx = 0; gbc.gridy = 2;
        addMemberBtn = new JButton("Add Team Member");
        styleButton(addMemberBtn, BTN_GREEN);
        addMemberBtn.addActionListener(this);
        panel.add(addMemberBtn, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        removeMemberBtn = new JButton("Remove Team Member");
        styleButton(removeMemberBtn, BTN_RED);
        removeMemberBtn.addActionListener(this);
        panel.add(removeMemberBtn, gbc);

        return panel;
    }

    /**
     * South button panel — AWT FlowLayout.
     * The four main actions: add plans, see everything, or wipe the slate clean.
     */
    private JPanel createButtonPanel()
    {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10)); // AWT FlowLayout
        panel.setBackground(BG_DARK);

        // Blue for adding a personal plan — straightforward, primary action
        addPersonalBtn = new JButton("Add Personal Plan");
        styleButton(addPersonalBtn, BTN_BLUE);
        addPersonalBtn.addActionListener(this);

        // Purple for the Pro plan — matches Copilot's accent
        addProBtn = new JButton("Add Pro Plan");
        styleButton(addProBtn, BTN_PURPLE);
        addProBtn.addActionListener(this);

        // Green for Display All — it's a safe, read-only action
        displayAllBtn = new JButton("Display All");
        styleButton(displayAllBtn, BTN_GREEN);
        displayAllBtn.addActionListener(this);

        // Red for Clear — destructive, so it stands out as a warning
        clearBtn = new JButton("Clear");
        styleButton(clearBtn, BTN_RED);
        clearBtn.addActionListener(this);

        panel.add(addPersonalBtn);
        panel.add(addProBtn);
        panel.add(displayAllBtn);
        panel.add(clearBtn);

        return panel;
    }

    /**
     * East output panel — shows plan details and action results.
     * Made wider (38 columns) so long model names and results fit comfortably.
     */
    private JPanel createOutputPanel()
    {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(makeTitledBorder("Output"));
        panel.setBackground(BG_PANEL);

        // Monospaced keeps columns aligned — great for structured plan output
        outputArea = new JTextArea(20, 38); // wider than the original 25
        outputArea.setEditable(false);
        outputArea.setFont(FONT_MONO);
        outputArea.setBackground(BG_DARK);
        outputArea.setForeground(FG_TEXT);
        outputArea.setCaretColor(ACCENT);

        JScrollPane scrollPane = new JScrollPane(outputArea);
        // Dark scroll-pane viewport so it blends with the text area
        scrollPane.getViewport().setBackground(BG_DARK);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_CLR));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // ---------------------------------------------------------------
    // ActionListener
    // ---------------------------------------------------------------

    /**
     * Central dispatcher — every button click lands here and gets
     * forwarded to the right handler method.
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
     * Shows an error dialog so the user can't miss the problem.
     * Using JOptionPane keeps things tidy — no need to hunt the output area.
     */
    private void showError(String message)
    {
        JOptionPane.showMessageDialog(this, message, "Input Error",
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Reads and validates the four common AIModel fields that every plan needs.
     * Returns a String array {name, price, params, window}, or null if anything is wrong.
     * Errors are shown as dialog boxes so they're impossible to miss.
     */
    private String[] readCommonFields()
    {
        String name      = modelNameField.getText().trim();
        String priceStr  = pricingField.getText().trim();
        String paramsStr = parametersField.getText().trim();
        String windowStr = contextWindowField.getText().trim();

        // All four fields must be filled in before we can build a plan
        if (name.isEmpty() || priceStr.isEmpty() || paramsStr.isEmpty() || windowStr.isEmpty())
        {
            showError("Please fill in all Model Details fields.");
            return null;
        }

        // Now make sure the numeric fields are actually numbers, not random text
        try
        {
            Double.parseDouble(priceStr);
            Integer.parseInt(paramsStr);
            Integer.parseInt(windowStr);
        }
        catch (NumberFormatException ex)
        {
            showError("Pricing must be a decimal number (e.g. 9.99).\n"
                    + "Parameters and Context Window must be whole integers.");
            return null;
        }

        return new String[]{ name, priceStr, paramsStr, windowStr };
    }

    /**
     * Walks backwards through the list to find the most recently added PersonalPlan.
     * Going from the end is faster when the latest plan is almost always at the tail.
     */
    private PersonalPlan findLastPersonalPlan()
    {
        for (int i = plans.size() - 1; i >= 0; i--)
        {
            if (plans.get(i) instanceof PersonalPlan)
            {
                return (PersonalPlan) plans.get(i);
            }
        }
        // Nothing found — caller needs to handle this
        return null;
    }

    /**
     * Same idea as findLastPersonalPlan but for ProPlan objects.
     */
    private ProPlan findLastProPlan()
    {
        for (int i = plans.size() - 1; i >= 0; i--)
        {
            if (plans.get(i) instanceof ProPlan)
            {
                return (ProPlan) plans.get(i);
            }
        }
        // No Pro Plans exist yet
        return null;
    }

    // ---------------------------------------------------------------
    // Button handlers
    // ---------------------------------------------------------------

    /**
     * Collects the form data, builds a new PersonalPlan, and drops it in the list.
     * On success the output area shows a summary so the user knows it worked.
     */
    private void addPersonalPlan()
    {
        // Validate the shared AIModel fields first
        String[] common = readCommonFields();
        if (common == null) return;

        // Also need the initial prompts count — specific to PersonalPlan
        String promptsStr = promptsRemainingField.getText().trim();
        if (promptsStr.isEmpty())
        {
            showError("Please enter Prompts Remaining for the Personal Plan.");
            return;
        }

        int prompts;
        try
        {
            prompts = Integer.parseInt(promptsStr);
        }
        catch (NumberFormatException ex)
        {
            // The user typed letters or decimals — only whole numbers make sense here
            showError("Prompts Remaining must be a whole number (e.g. 50).");
            return;
        }

        // Everything checks out — create the plan and store it
        PersonalPlan plan = new PersonalPlan(
                common[0],
                Double.parseDouble(common[1]),
                Integer.parseInt(common[2]),
                Integer.parseInt(common[3]),
                prompts);

        plans.add(plan);
        outputArea.setText("✔  Personal Plan added! "
                + "(Total plans: " + plans.size() + ")\n\n" + plan.display());
    }

    /**
     * Collects the form data, builds a new ProPlan, and drops it in the list.
     * Team slots are the extra field that makes a Pro Plan different.
     */
    private void addProPlan()
    {
        // Validate the shared AIModel fields first
        String[] common = readCommonFields();
        if (common == null) return;

        // Pro Plans need an initial slot count for the team
        String slotsStr = slotsField.getText().trim();
        if (slotsStr.isEmpty())
        {
            showError("Please enter Available Slots for the Pro Plan.");
            return;
        }

        int slots;
        try
        {
            slots = Integer.parseInt(slotsStr);
        }
        catch (NumberFormatException ex)
        {
            // Slots must be a whole number — you can't have 2.5 team members
            showError("Available Slots must be a whole number (e.g. 5).");
            return;
        }

        // Build and store the new Pro Plan
        ProPlan plan = new ProPlan(
                common[0],
                Double.parseDouble(common[1]),
                Integer.parseInt(common[2]),
                Integer.parseInt(common[3]),
                slots);

        plans.add(plan);
        outputArea.setText("✔  Pro Plan added! "
                + "(Total plans: " + plans.size() + ")\n\n" + plan.display());
    }

    /**
     * Loops through every plan in the list and dumps their display() output
     * into the output area so the user can see everything at a glance.
     */
    private void displayAll()
    {
        // Nothing to show if the list is still empty
        if (plans.isEmpty())
        {
            outputArea.setText("No plans yet — add a Personal or Pro Plan first.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== All Subscription Plans (").append(plans.size()).append(") ===\n\n");

        // Each plan knows how to describe itself thanks to display() polymorphism
        for (int i = 0; i < plans.size(); i++)
        {
            sb.append("--- Plan ").append(i + 1).append(" ---\n");
            sb.append(plans.get(i).display()).append("\n\n");
        }

        outputArea.setText(sb.toString());
    }

    /**
     * Wipes every input field and the output area back to a blank state.
     * Handy when starting a fresh entry without restarting the app.
     */
    private void clearAll()
    {
        // Clear all the AI model detail fields
        modelNameField.setText("");
        pricingField.setText("");
        parametersField.setText("");
        contextWindowField.setText("");

        // Clear the Personal Plan specific fields
        promptsRemainingField.setText("");
        promptTextField.setText("");
        responseLengthField.setText("");
        purchaseAmountField.setText("");

        // Clear the Pro Plan specific fields
        slotsField.setText("");
        memberNameField.setText("");

        // Reset the output area too — clean slate
        outputArea.setText("");
    }

    /**
     * Sends a prompt to the most recently created PersonalPlan.
     * Uses the prompt text and desired response length from the form fields.
     */
    private void enterPromptAction()
    {
        // Grab the latest Personal Plan — nothing to do if there isn't one
        PersonalPlan plan = findLastPersonalPlan();
        if (plan == null)
        {
            showError("No Personal Plan found.\nPlease add a Personal Plan first.");
            return;
        }

        String prompt    = promptTextField.getText().trim();
        String lengthStr = responseLengthField.getText().trim();

        // Both fields are required before we can send the prompt
        if (prompt.isEmpty() || lengthStr.isEmpty())
        {
            showError("Please fill in both Prompt Text and Response Length.");
            return;
        }

        int length;
        try
        {
            length = Integer.parseInt(lengthStr);
        }
        catch (NumberFormatException ex)
        {
            // Response length is measured in tokens — must be a whole number
            showError("Response Length must be a whole number (e.g. 512).");
            return;
        }

        // Hand off to the plan and show the result
        outputArea.setText("Plan: " + plan.getModelName() + "\n\n"
                + plan.enterPrompt(prompt, length));
    }

    /**
     * Tops up the prompt balance on the most recently created PersonalPlan.
     * The user specifies how many prompts they want to buy.
     */
    private void purchasePromptsAction()
    {
        // Need an existing Personal Plan to top up
        PersonalPlan plan = findLastPersonalPlan();
        if (plan == null)
        {
            showError("No Personal Plan found.\nPlease add a Personal Plan first.");
            return;
        }

        String amountStr = purchaseAmountField.getText().trim();
        if (amountStr.isEmpty())
        {
            showError("Please enter how many prompts you want to purchase.");
            return;
        }

        int amount;
        try
        {
            amount = Integer.parseInt(amountStr);
        }
        catch (NumberFormatException ex)
        {
            // Can't buy a fractional number of prompts
            showError("Purchase amount must be a whole number (e.g. 100).");
            return;
        }

        // Complete the purchase and report back
        outputArea.setText("Plan: " + plan.getModelName() + "\n\n"
                + plan.purchasePrompts(amount));
    }

    /**
     * Adds a new team member to the most recently created ProPlan.
     * Uses whatever name is typed in the Team Member Name field.
     */
    private void addTeamMemberAction()
    {
        // We need a Pro Plan to work with — check for one first
        ProPlan plan = findLastProPlan();
        if (plan == null)
        {
            showError("No Pro Plan found.\nPlease add a Pro Plan first.");
            return;
        }

        String memberName = memberNameField.getText().trim();
        if (memberName.isEmpty())
        {
            // Hard to add someone with no name
            showError("Please enter the Team Member's name.");
            return;
        }

        // Delegate to the plan and show what happened
        outputArea.setText("Plan: " + plan.getModelName() + "\n\n"
                + plan.addTeamMember(memberName));
    }

    /**
     * Removes a team member from the most recently created ProPlan.
     * The name must match exactly what was used when they were added.
     */
    private void removeTeamMemberAction()
    {
        // We need a Pro Plan to work with — check for one first
        ProPlan plan = findLastProPlan();
        if (plan == null)
        {
            showError("No Pro Plan found.\nPlease add a Pro Plan first.");
            return;
        }

        String memberName = memberNameField.getText().trim();
        if (memberName.isEmpty())
        {
            // Can't remove someone if we don't know who they are
            showError("Please enter the Team Member's name to remove.");
            return;
        }

        // Delegate to the plan and show what happened
        outputArea.setText("Plan: " + plan.getModelName() + "\n\n"
                + plan.removeTeamMember(memberName));
    }

    // ---------------------------------------------------------------
    // Entry point
    // ---------------------------------------------------------------

    /**
     * Kicks off the GUI on the Swing event-dispatch thread — the safe way
     * to start any Swing application.
     */
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() -> new SubscriptionGUI());
    }
}
