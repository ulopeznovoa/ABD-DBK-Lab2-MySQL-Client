import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.mysql.cj.jdbc.result.ResultSetMetaData;

import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JTextArea;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.awt.event.ActionEvent;
import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.JPasswordField;

public class MainFrame extends JFrame {

    private JPanel contentPane;
    private JTextField txtServerAddress;
    private JTextField txtPort;
    private JTextPane txtSqlSentence;
    private JButton btnQuery;
    private JButton btnExecute;
    private JTable myJTable;
    private JTextField txtNotificationArea;

    private Data myData;
    private DefaultTableModel myTableModel = new DefaultTableModel();
    private JTextField txtStatus;
    private JTextField txtCause;
    private JTextPane txtMissingPermissions;
    private JPasswordField passwordField;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    MainFrame frame = new MainFrame();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public MainFrame() {

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 542, 600);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        txtServerAddress = new JTextField();
        txtServerAddress.setBounds(22, 12, 193, 22);
        txtServerAddress.setText("Server address");
        contentPane.add(txtServerAddress);
        txtServerAddress.setColumns(10);

        txtPort = new JTextField();
        txtPort.setBounds(227, 15, 48, 19);
        txtPort.setText("Port");
        contentPane.add(txtPort);
        txtPort.setColumns(10);

        JButton btnLogin = new JButton("Login");
        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                // Get username
                String username = JOptionPane.showInputDialog("Insert username:", "");

                // Get password
                String password = JOptionPane.showInputDialog("Insert password:", "");

                // Login
                try {
                    myData = new Data(txtServerAddress.getText(),txtPort.getText(),username,password);

                    // Set notification area
                    txtNotificationArea.setText("Login successful");

                    // Set status area
                    txtStatus.setText("Ok");

                } catch (SQLException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();

                    // Set notification area
                    txtNotificationArea.setText("Login denied");

                    // Set status area
                    txtStatus.setText("Failure");

                }
            }
        });
        btnLogin.setBounds(287, 12, 110, 25);
        contentPane.add(btnLogin);

        JButton btnLogout = new JButton("Logout");
        btnLogout.setBounds(409, 12, 110, 25);
        btnLogout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    myData.conn.close();

                    // Set notification area
                    txtNotificationArea.setText("Logout successful");

                    // Set status area
                    txtStatus.setText("Ok");

                } catch (SQLException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                    txtNotificationArea.setText("Logout denied");

                    // Set status area
                    txtStatus.setText("Failure");

                }
            }
        });
        contentPane.add(btnLogout);

        txtSqlSentence = new JTextPane();
        txtSqlSentence.setBounds(22, 57, 275, 93);
        txtSqlSentence.setText("SQL sentence");
        contentPane.add(txtSqlSentence);

        btnQuery = new JButton("Query");
        btnQuery.setBounds(359, 65, 110, 25);
        btnQuery.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                ResultSetMetaData myMetaData;
                try {

                    // Query execution
                    myData.rs = myData.st.executeQuery(txtSqlSentence.getText());

                    // Reset TableModel
                    myTableModel = new DefaultTableModel();

                    // Set column labels
                    myMetaData = (ResultSetMetaData) myData.rs.getMetaData();
                    int columnNumber = myMetaData.getColumnCount();
                    Object[] labelsArray = new Object[columnNumber];
                    for (int i = 0; i < columnNumber; i++)
                    {
                        labelsArray[i] = myMetaData.getColumnLabel(i + 1);
                        System.out.println(myMetaData.getColumnLabel(i + 1));
                    }
                    myTableModel.setColumnIdentifiers(labelsArray);

                    // Set column values
                    int rowNumber = 0;
                    while (myData.rs.next())
                    {
                        Object[] myRow = new Object[columnNumber];
                        for (int i=0;i<columnNumber;i++)
                            myRow[i] = myData.rs.getObject(i+1);
                        System.out.println(myRow.toString());
                        myTableModel.addRow(myRow);
                        rowNumber++;
                    }

                    // Update TableModel
                    myJTable.setModel(myTableModel);
                    myTableModel.fireTableDataChanged();

                    // Set notification area
                    if (rowNumber == 0)
                        txtNotificationArea.setText("Empty set");
                    else
                    if (rowNumber == 1)
                        txtNotificationArea.setText("1 row in set");
                    else
                        txtNotificationArea.setText(Integer.toString(rowNumber) + " rows in set");

                    // Set status area
                    txtStatus.setText("Ok");
                    txtCause.setText("");
                    txtMissingPermissions.setText("");

                } catch (SQLException ex) {
                    // TODO Auto-generated catch block
                    ex.printStackTrace();

                    // Reset TableModel
                    myTableModel = new DefaultTableModel();

                    // Update TableModel
                    myJTable.setModel(myTableModel);
                    myTableModel.fireTableDataChanged();

                    // Set notification area
                    txtNotificationArea.setText("");

                    // Set status area
                    txtStatus.setText("Failure");
                    String myMessage = ex.getMessage();
                    String[] messageWords = myMessage.split(" ");
                    if (messageWords[1].equals("command") && messageWords[2].equals("denied"))
                    {
                        txtCause.setText("Denied");
                        txtMissingPermissions.setText(messageWords[0] + " for " + messageWords[7] + messageWords[8] + "\nto " + messageWords[5]);
                    }
                    else
                    {
                        txtCause.setText("Syntax error");
                        txtMissingPermissions.setText("");
                    }
                }
            }
        });
        contentPane.add(btnQuery);

        btnExecute = new JButton("Execute");
        btnExecute.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                try {

                    // Query execution
                    int rowsAffected = myData.st.executeUpdate(txtSqlSentence.getText());

                    // Reset TableModel
                    myTableModel = new DefaultTableModel();

                    // Update TableModel
                    myJTable.setModel(myTableModel);
                    myTableModel.fireTableDataChanged();

                    // Set notification area
                    txtNotificationArea.setText(rowsAffected + " row(s) affected");

                    // Set status area
                    txtStatus.setText("Ok");
                    txtCause.setText("");
                    txtMissingPermissions.setText("");

                } catch (SQLException ex) {
                    // TODO Auto-generated catch block
                    ex.printStackTrace();

                    // Reset TableModel
                    myTableModel = new DefaultTableModel();

                    // Update TableModel
                    myJTable.setModel(myTableModel);
                    myTableModel.fireTableDataChanged();

                    // Set notification area
                    txtNotificationArea.setText("");

                    // Set status area
                    txtStatus.setText("Failure");
                    String myMessage = ex.getMessage();
                    String[] messageWords = myMessage.split(" ");
                    if (messageWords[1].equals("command") && messageWords[2].equals("denied"))
                    {
                        txtCause.setText("Denied");
                        txtMissingPermissions.setText(messageWords[0] + " for " + messageWords[7] + messageWords[8] + "\nto " + messageWords[5]);
                    }
                    else
                    {
                        txtCause.setText("Syntax error");
                        txtMissingPermissions.setText("");
                    }
                }
            }
        });
        btnExecute.setBounds(359, 102, 110, 25);
        contentPane.add(btnExecute);

        DefaultTableModel myTableModel = new DefaultTableModel();

        myJTable = new JTable(myTableModel);

        JScrollPane myJScrollPane = new JScrollPane(myJTable);
        myJScrollPane.setBounds(22, 190, 497, 170);
        contentPane.add(myJScrollPane);

        txtNotificationArea = new JTextField();
        txtNotificationArea.setBounds(22, 391, 497, 49);
        txtNotificationArea.setText("Notification area");
        contentPane.add(txtNotificationArea);
        txtNotificationArea.setColumns(10);

        JLabel lblStatus = new JLabel("Status: ");
        lblStatus.setBounds(22, 468, 56, 15);
        contentPane.add(lblStatus);

        JLabel lblCause = new JLabel("Cause: ");
        lblCause.setBounds(191, 468, 56, 15);
        contentPane.add(lblCause);

        JLabel lblMissingPermissions = new JLabel("Missing permissions: ");
        lblMissingPermissions.setBounds(191, 495, 328, 15);
        contentPane.add(lblMissingPermissions);

        txtStatus = new JTextField();
        txtStatus.setText("Status");
        txtStatus.setBounds(86, 466, 74, 19);
        contentPane.add(txtStatus);
        txtStatus.setColumns(10);

        txtCause = new JTextField();
        txtCause.setText("");
        txtCause.setBounds(336, 464, 183, 19);
        contentPane.add(txtCause);
        txtCause.setColumns(10);

        txtMissingPermissions = new JTextPane();
        txtMissingPermissions.setText("");
        txtMissingPermissions.setBounds(336, 493, 183, 49);
        contentPane.add(txtMissingPermissions);

    }
}