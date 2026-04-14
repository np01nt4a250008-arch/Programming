import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
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
    // --- GitHub Copilot-inspired color palette ---
    private static final Color BG_DARK    = new Color(13,  17,  23);  // darkest bg
    private static final Color BG_PANEL   = new Color(22,  27,  34);  // panel bg
    private static final Color BORDER_CLR = new Color(48,  54,  61);  // subtle border
    private static final Color FG_TEXT    = new Color(201, 209, 217); // soft off-white text
    private static final Color ACCENT     = new Color(88,  166, 255); // Copilot blue
    private static final Color BTN_GREEN  = new Color(35,  134,  54);
    private static final Color BTN_RED    = new Color(218,  54,  51);
    private static final Color BTN_BLUE   = new Color(31,  111, 235);
    private static final Color BTN_PURPLE = new Color(130,  80, 228); // Copilot signature

    // --- Shared fonts — change the size here to affect the whole UI ---
    private static final Font FONT_LABEL  = new Font("SansSerif", Font.PLAIN, 14);
    private static final Font FONT_BUTTON = new Font("SansSerif", Font.BOLD,  14);
    private static final Font FONT_TITLE  = new Font("SansSerif", Font.BOLD,  14);
    private static final Font FONT_MONO   = new Font("Monospaced", Font.PLAIN, 14);

    // All the plans the user has created so far
    private ArrayList<AIModel> plans;

    // AIModel base attribute fields
    private JTextField modelNameField, pricingField, parametersField, contextWindowField;

    // Personal Plan fields
    private JTextField promptsRemainingField, promptTextField, responseLengthField, purchaseAmountField;

    // Pro Plan fields
    private JTextField slotsField, memberNameField;

    // Output area — where results and success messages appear
    private JTextArea outputArea;

    // Bottom action buttons
    private JButton addPersonalBtn, addProBtn, displayAllBtn, clearBtn;

    // Personal Plan action buttons
    private JButton enterPromptBtn, purchasePromptsBtn;

    // Pro Plan action buttons
    private JButton addMemberBtn, removeMemberBtn;

    // CardLayout references — used to swap between the two plan panels
    private JPanel cardPanel;
    private CardLayout cardLayout;

    private static final String PERSONAL_CARD = "Personal Plan";
    private static final String PRO_CARD      = "Pro Plan";

    // ---------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------

    /** Builds the window and makes it visible. */
    public SubscriptionGUI()
    {
        super("AI Subscription Manager");
        plans = new ArrayList<AIModel>();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 720);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);
        initComponents();
        setVisible(true);
    }

    // ---------------------------------------------------------------
    // GUI construction helpers
    // ---------------------------------------------------------------

    /** Plugs all sub-panels into the frame — think of it as the skeleton. */
    private void initComponents()
    {
        setLayout(new BorderLayout(10, 10));
        add(createInputPanel(),  BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
        add(createOutputPanel(), BorderLayout.EAST);
    }

    /** Returns a styled titled border that fits the Copilot dark theme. */
    private Border makeTitledBorder(String title)
    {
        return BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BORDER_CLR),
            title, TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION, FONT_TITLE, ACCENT);
    }

    /** Creates a styled JTextField (dark bg, light text, accent caret). */
    private JTextField makeField(int cols)
    {
        JTextField f = new JTextField(cols);
        f.setBackground(BG_DARK);
        f.setForeground(FG_TEXT);
        f.setCaretColor(ACCENT);
        f.setFont(FONT_LABEL);
        f.setBorder(BorderFactory.createLineBorder(BORDER_CLR));
        return f;
    }

    /** Creates a styled JLabel so every label looks the same without repetition. */
    private JLabel makeLabel(String text)
    {
        JLabel l = new JLabel(text);
        l.setForeground(FG_TEXT);
        l.setFont(FONT_LABEL);
        return l;
    }

    /** Creates a styled JButton with the given background colour. */
    private JButton makeButton(String text, Color bg)
    {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(FONT_BUTTON);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        b.addActionListener(this);
        return b;
    }

    /**
     * North panel — AWT GridLayout (5 rows × 2 cols).
     * Slightly narrower fields (col 11) keep this section compact.
     */
    private JPanel createInputPanel()
    {
        JPanel panel = new JPanel(new GridLayout(5, 2, 6, 6));
        panel.setBorder(makeTitledBorder("AI Model Details"));
        panel.setBackground(BG_PANEL);

        panel.add(makeLabel("  Field")); panel.add(makeLabel("  Value"));

        panel.add(makeLabel("  Model Name:"));
        panel.add(modelNameField = makeField(11));

        panel.add(makeLabel("  Pricing ($):"));
        panel.add(pricingField = makeField(11));

        panel.add(makeLabel("  Parameters (billions):"));
        panel.add(parametersField = makeField(11));

        panel.add(makeLabel("  Context Window (tokens):"));
        panel.add(contextWindowField = makeField(11));

        return panel;
    }

    /**
     * Centre panel — Swing BoxLayout (vertical).
     * Stacks the plan-type selector above the card area.
     */
    private JPanel createCenterPanel()
    {
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(makeTitledBorder("Plan Management"));
        centerPanel.setBackground(BG_PANEL);

        // The combo box drives the CardLayout — picking a plan type swaps the card
        JPanel switcherPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        switcherPanel.setBackground(BG_PANEL);
        switcherPanel.add(makeLabel("Select Plan Type: "));
        JComboBox<String> planSwitcher = new JComboBox<>(new String[]{ PERSONAL_CARD, PRO_CARD });
        planSwitcher.setBackground(BG_DARK);
        planSwitcher.setForeground(FG_TEXT);
        planSwitcher.setFont(FONT_LABEL);
        planSwitcher.addActionListener(e ->
            cardLayout.show(cardPanel, (String) planSwitcher.getSelectedItem()));
        switcherPanel.add(planSwitcher);

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
     * Handles entering prompts and topping up the prompt balance.
     */
    private JPanel createPersonalCard()
    {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(makeTitledBorder("Personal Plan - Prompt Management"));
        panel.setBackground(BG_PANEL);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; panel.add(makeLabel("Prompts Remaining:"), gbc);
        gbc.gridx = 1; panel.add(promptsRemainingField = makeField(8), gbc);

        gbc.gridx = 0; gbc.gridy = 1; panel.add(makeLabel("Prompt Text:"), gbc);
        gbc.gridx = 1; panel.add(promptTextField = makeField(20), gbc);

        gbc.gridx = 0; gbc.gridy = 2; panel.add(makeLabel("Response Length (tokens):"), gbc);
        gbc.gridx = 1; panel.add(responseLengthField = makeField(8), gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        panel.add(enterPromptBtn = makeButton("Enter Prompt", BTN_GREEN), gbc);

        gbc.gridx = 0; gbc.gridy = 4; panel.add(makeLabel("Purchase Prompts:"), gbc);
        gbc.gridx = 1; panel.add(purchaseAmountField = makeField(8), gbc);

        gbc.gridx = 1; gbc.gridy = 5;
        panel.add(purchasePromptsBtn = makeButton("Purchase Prompts", BTN_BLUE), gbc);

        return panel;
    }

    /**
     * Pro Plan card — AWT GridBagLayout.
     * Manages team collaboration slots — adding and removing members.
     */
    private JPanel createProCard()
    {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(makeTitledBorder("Pro Plan - Team Collaboration"));
        panel.setBackground(BG_PANEL);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; panel.add(makeLabel("Available Slots:"), gbc);
        gbc.gridx = 1; panel.add(slotsField = makeField(8), gbc);

        gbc.gridx = 0; gbc.gridy = 1; panel.add(makeLabel("Team Member Name:"), gbc);
        gbc.gridx = 1; panel.add(memberNameField = makeField(15), gbc);

        // Add and Remove sit side-by-side for quick access
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(addMemberBtn = makeButton("Add Team Member", BTN_GREEN), gbc);
        gbc.gridx = 1;
        panel.add(removeMemberBtn = makeButton("Remove Team Member", BTN_RED), gbc);

        return panel;
    }

    /**
     * South button panel — AWT FlowLayout.
     * The four main actions: add plans, see everything, or wipe the slate clean.
     */
    private JPanel createButtonPanel()
    {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        panel.setBackground(BG_DARK);

        panel.add(addPersonalBtn = makeButton("Add Personal Plan", BTN_BLUE));
        panel.add(addProBtn      = makeButton("Add Pro Plan",      BTN_PURPLE));
        panel.add(displayAllBtn  = makeButton("Display All",       BTN_GREEN));
        panel.add(clearBtn       = makeButton("Clear",             BTN_RED));

        return panel;
    }

    /**
     * East output panel — wider (38 cols) so long results don't get cut off.
     */
    private JPanel createOutputPanel()
    {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(makeTitledBorder("Output"));
        panel.setBackground(BG_PANEL);

        outputArea = new JTextArea(20, 38);
        outputArea.setEditable(false);
        outputArea.setFont(FONT_MONO);
        outputArea.setBackground(BG_DARK);
        outputArea.setForeground(FG_TEXT);
        outputArea.setCaretColor(ACCENT);

        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.getViewport().setBackground(BG_DARK);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_CLR));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // ---------------------------------------------------------------
    // ActionListener
    // ---------------------------------------------------------------

    /** Routes every button click to the right handler method. */
    @Override
    public void actionPerformed(ActionEvent e)
    {
        Object src = e.getSource();
        if      (src == addPersonalBtn)    addPersonalPlan();
        else if (src == addProBtn)         addProPlan();
        else if (src == displayAllBtn)     displayAll();
        else if (src == clearBtn)          clearAll();
        else if (src == enterPromptBtn)    enterPromptAction();
        else if (src == purchasePromptsBtn)purchasePromptsAction();
        else if (src == addMemberBtn)      addTeamMemberAction();
        else if (src == removeMemberBtn)   removeTeamMemberAction();
    }

    // ---------------------------------------------------------------
    // Helpers shared by multiple handlers
    // ---------------------------------------------------------------

    /** Pops an error dialog — much harder to miss than text in the output area. */
    private void showError(String msg)
    {
        JOptionPane.showMessageDialog(this, msg, "Input Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Reads and validates the four fields every plan needs.
     * Returns {name, price, params, window} or null if anything is wrong.
     */
    private String[] readCommonFields()
    {
        String name  = modelNameField.getText().trim();
        String price = pricingField.getText().trim();
        String prms  = parametersField.getText().trim();
        String win   = contextWindowField.getText().trim();

        if (name.isEmpty() || price.isEmpty() || prms.isEmpty() || win.isEmpty())
        {
            showError("Please fill in all Model Details fields.");
            return null;
        }

        try { Double.parseDouble(price); Integer.parseInt(prms); Integer.parseInt(win); }
        catch (NumberFormatException ex)
        {
            showError("Pricing must be a decimal (e.g. 9.99).\n"
                    + "Parameters and Context Window must be whole numbers.");
            return null;
        }

        return new String[]{ name, price, prms, win };
    }

    /** Finds the most recently added PersonalPlan, or null if none exist yet. */
    private PersonalPlan findLastPersonalPlan()
    {
        for (int i = plans.size() - 1; i >= 0; i--)
            if (plans.get(i) instanceof PersonalPlan)
                return (PersonalPlan) plans.get(i);
        return null;
    }

    /** Same idea, but for ProPlan objects. */
    private ProPlan findLastProPlan()
    {
        for (int i = plans.size() - 1; i >= 0; i--)
            if (plans.get(i) instanceof ProPlan)
                return (ProPlan) plans.get(i);
        return null;
    }

    // ---------------------------------------------------------------
    // Button handlers
    // ---------------------------------------------------------------

    /** Creates a PersonalPlan from the form and adds it to the list. */
    private void addPersonalPlan()
    {
        String[] c = readCommonFields();
        if (c == null) return;

        String ps = promptsRemainingField.getText().trim();
        if (ps.isEmpty()) { showError("Please enter Prompts Remaining."); return; }

        int prompts;
        try { prompts = Integer.parseInt(ps); }
        catch (NumberFormatException ex)
        { showError("Prompts Remaining must be a whole number (e.g. 50)."); return; }

        PersonalPlan plan = new PersonalPlan(c[0], Double.parseDouble(c[1]),
                Integer.parseInt(c[2]), Integer.parseInt(c[3]), prompts);
        plans.add(plan);
        outputArea.setText("Personal Plan added! (Total: " + plans.size() + ")\n\n" + plan.display());
    }

    /** Creates a ProPlan from the form and adds it to the list. */
    private void addProPlan()
    {
        String[] c = readCommonFields();
        if (c == null) return;

        String ss = slotsField.getText().trim();
        if (ss.isEmpty()) { showError("Please enter Available Slots."); return; }

        int slots;
        try { slots = Integer.parseInt(ss); }
        catch (NumberFormatException ex)
        { showError("Available Slots must be a whole number (e.g. 5)."); return; }

        ProPlan plan = new ProPlan(c[0], Double.parseDouble(c[1]),
                Integer.parseInt(c[2]), Integer.parseInt(c[3]), slots);
        plans.add(plan);
        outputArea.setText("Pro Plan added! (Total: " + plans.size() + ")\n\n" + plan.display());
    }

    /** Prints every plan's details to the output area. */
    private void displayAll()
    {
        if (plans.isEmpty()) { outputArea.setText("No plans yet — add one first."); return; }

        StringBuilder sb = new StringBuilder("=== All Plans (" + plans.size() + ") ===\n\n");
        for (int i = 0; i < plans.size(); i++)
            sb.append("--- Plan ").append(i + 1).append(" ---\n")
              .append(plans.get(i).display()).append("\n\n");

        outputArea.setText(sb.toString());
    }

    /** Clears all input fields and the output area back to a blank state. */
    private void clearAll()
    {
        for (JTextField f : new JTextField[]{ modelNameField, pricingField, parametersField,
                contextWindowField, promptsRemainingField, promptTextField,
                responseLengthField, purchaseAmountField, slotsField, memberNameField })
            f.setText("");
        outputArea.setText("");
    }

    /** Sends a prompt to the most recently added PersonalPlan. */
    private void enterPromptAction()
    {
        PersonalPlan plan = findLastPersonalPlan();
        if (plan == null) { showError("No Personal Plan found. Add one first."); return; }

        String prompt = promptTextField.getText().trim();
        String ls     = responseLengthField.getText().trim();
        if (prompt.isEmpty() || ls.isEmpty())
        { showError("Please fill in Prompt Text and Response Length."); return; }

        int length;
        try { length = Integer.parseInt(ls); }
        catch (NumberFormatException ex)
        { showError("Response Length must be a whole number (e.g. 512)."); return; }

        outputArea.setText("Plan: " + plan.getModelName() + "\n\n" + plan.enterPrompt(prompt, length));
    }

    /** Tops up the prompt balance on the most recently added PersonalPlan. */
    private void purchasePromptsAction()
    {
        PersonalPlan plan = findLastPersonalPlan();
        if (plan == null) { showError("No Personal Plan found. Add one first."); return; }

        String as = purchaseAmountField.getText().trim();
        if (as.isEmpty()) { showError("Please enter how many prompts to purchase."); return; }

        int amount;
        try { amount = Integer.parseInt(as); }
        catch (NumberFormatException ex)
        { showError("Purchase amount must be a whole number (e.g. 100)."); return; }

        outputArea.setText("Plan: " + plan.getModelName() + "\n\n" + plan.purchasePrompts(amount));
    }

    /** Adds a team member to the most recently added ProPlan. */
    private void addTeamMemberAction()
    {
        ProPlan plan = findLastProPlan();
        if (plan == null) { showError("No Pro Plan found. Add one first."); return; }

        String name = memberNameField.getText().trim();
        if (name.isEmpty()) { showError("Please enter the team member's name."); return; }

        outputArea.setText("Plan: " + plan.getModelName() + "\n\n" + plan.addTeamMember(name));
    }

    /** Removes a team member from the most recently added ProPlan. */
    private void removeTeamMemberAction()
    {
        ProPlan plan = findLastProPlan();
        if (plan == null) { showError("No Pro Plan found. Add one first."); return; }

        String name = memberNameField.getText().trim();
        if (name.isEmpty()) { showError("Please enter the member's name to remove."); return; }

        outputArea.setText("Plan: " + plan.getModelName() + "\n\n" + plan.removeTeamMember(name));
    }

    // ---------------------------------------------------------------
    // Entry point
    // ---------------------------------------------------------------

    /** Kicks the GUI off on the Swing event-dispatch thread — the safe way to start. */
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() -> new SubscriptionGUI());
    }
}
