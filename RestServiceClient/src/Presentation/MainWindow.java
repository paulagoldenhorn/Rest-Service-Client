package Presentation;

import Entity.Header;
import Entity.Method;
import Entity.QueryString;
import Entity.Request;
import Service.FavRequestsManager;
import Service.HttpClientManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.LinkedList;
import java.util.List;

public class MainWindow extends JFrame {
    private final FavRequestsManager favRequestsManager = new FavRequestsManager();
    private final HttpClientManager httpClientManager = new HttpClientManager();
    private final int FONT_SIZE = 14;
    private final String FONT_NAME = "Dialog";

    private JMenuBar menuBar;
    private JMenu favsMenu;
    private JPanel northPanel;
    private JPanel centerPanel;
    private JPanel southPanel;
    private JTextField nameField;
    private JTextField urlField;
    private JTextArea bodyArea;
    private JTextArea responseArea;
    private JPanel responseImagePanel;
    private JTabbedPane responseTabbedPane;
    private JScrollPane responseScrollPane;
    private JPanel requestButtonsPanel;
    private JPanel favsButtonPanel;
    private JButton sendBtn;
    private JButton addToFavsBtn;
    private JComboBox<String> methodComboBox;
    private DefaultTableModel headerTableModel;
    private DefaultTableModel queryStringsTableModel;
    private JButton addQueryStringBtn;
    private JButton deleteBtn;
    private JButton updateBtn;
    private JMenu newRequestMenu;

    public MainWindow()
    {
        setTitle("Cliente Rest");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 UnsupportedLookAndFeelException e) {
            throw new RuntimeException(e);
        }

        northPanel = new JPanel(new BorderLayout());
        centerPanel = new JPanel(new BorderLayout());
        southPanel = new JPanel(new BorderLayout());

        buildNorthPanel();
        buildCenterPanel();
        buildSouthPanel();

        buildMenuBar();

        add(northPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);

        // Adds an empty row when button is clicked
        addQueryStringBtn.addActionListener(e -> {
            queryStringsTableModel.addRow( new Object[]{"", ""} );
        });

        // Activate buttons action listeners
        activateListeners();

        favsMenu.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                handleLoadAllFavs();
            }

            @Override
            public void menuDeselected(MenuEvent e) {

            }

            @Override
            public void menuCanceled(MenuEvent e) {

            }
        });

        newRequestMenu.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                cleanFields();
                replaceRequestButtons();
                activateListeners();
            }

            @Override
            public void menuDeselected(MenuEvent e) {

            }

            @Override
            public void menuCanceled(MenuEvent e) {

            }
        });

        setVisible(true);

    }

    private void activateListeners() {
        sendBtn.addActionListener(e ->  {
            Header header = getHeaderFromTable();
            if (header!=null)
            {
                String key = header.getHeader_key();
                String value = header.getHeader_value();
                if ( key.equals("Content-type")&&value.startsWith("image") )
                    setImageResponseArea( handleSendImageRequest() );
            }
            setResponseArea( handleSendRequest() );
        });

        addToFavsBtn.addActionListener(e -> {
            handleAddToFavs();
        });

    }
    private void setResponseArea(HttpResponse<String> response) {
        responseArea.setText(
                "CÓDIGO DE ESTADO: " + response.statusCode() +"\n"
                        + response.body()
        );
    }

    private void setImageResponseArea(HttpResponse<byte[]> response) {
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(response.body()));
            JLabel imageLabel = new JLabel(new ImageIcon(image));
            responseImagePanel.removeAll();
            responseImagePanel.add(imageLabel);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void buildNorthPanel() {

        // Right side of the panel
        sendBtn = new JButton("Enviar");
        sendBtn.setBackground(new Color(122, 110, 60));
        sendBtn.setForeground(Color.WHITE);
        addToFavsBtn = new JButton("Agregar a favoritos");
        addToFavsBtn.setBackground(new Color(66, 50, 114));
        addToFavsBtn.setForeground(Color.WHITE);
        methodComboBox = new JComboBox<>();
        for ( Method method : favRequestsManager.loadAllMethods() )
            methodComboBox.addItem(method.getType());

        requestButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        requestButtonsPanel.add(methodComboBox);
        requestButtonsPanel.add(sendBtn);
        requestButtonsPanel.add(addToFavsBtn);

        // Left side of the panel
        nameField = new JTextField();
        nameField.setFont(new Font(FONT_NAME, Font.PLAIN, FONT_SIZE));
        JPanel namePanel = new JPanel(new BorderLayout());
        namePanel.add(new JLabel("Nombre: "), BorderLayout.WEST);
        namePanel.add(nameField, BorderLayout.CENTER);

        urlField = new JTextField();
        urlField.setFont(new Font(FONT_NAME, Font.PLAIN, FONT_SIZE));
        JPanel urlPanel = new JPanel(new BorderLayout());
        urlPanel.add(new JLabel("URL: "), BorderLayout.WEST);
        urlPanel.add(urlField, BorderLayout.CENTER);

        JPanel textFieldsPanel = new JPanel(new BorderLayout());
        textFieldsPanel.add(namePanel, BorderLayout.NORTH);
        textFieldsPanel.add(urlPanel, BorderLayout.SOUTH) ;

        northPanel.add(textFieldsPanel, BorderLayout.CENTER);
        northPanel.add(requestButtonsPanel, BorderLayout.EAST);
    }

    private void buildCenterPanel(){

        String[] columnNames = {"Clave", "Valor"};

        // Table for header
        headerTableModel = new DefaultTableModel(columnNames, 1);
        JTable headerTable = new JTable(headerTableModel);
        headerTable.setFont(new Font(FONT_NAME, Font.PLAIN, FONT_SIZE));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(new JLabel("Encabezado"), BorderLayout.NORTH);
        headerPanel.add(new JScrollPane(headerTable), BorderLayout.CENTER);

        // Table for query strings
        queryStringsTableModel = new DefaultTableModel(columnNames, 1);
        JTable queryStringsTable = new JTable(queryStringsTableModel);
        queryStringsTable.setFont(new Font(FONT_NAME, Font.PLAIN,FONT_SIZE));
        addQueryStringBtn = new JButton("Agregar parámetro");
        addQueryStringBtn.setBackground(new Color(140, 156, 187));

        JPanel queryStringsPanel = new JPanel(new BorderLayout());
        queryStringsPanel.add(new JLabel("Parámetros"), BorderLayout.NORTH);
        queryStringsPanel.add(new JScrollPane(queryStringsTable), BorderLayout.CENTER);
        queryStringsPanel.add(addQueryStringBtn, BorderLayout.SOUTH);

        // Split panel to divide headers and query strings tables
        JSplitPane centralSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, headerPanel, queryStringsPanel);
        centralSplitPane.setResizeWeight(0.2); // Divides space (20/80)% between tables

        centerPanel.add(centralSplitPane, BorderLayout.CENTER);
    }

    private void buildSouthPanel() {

        // Body area
        bodyArea = new JTextArea(10, 1); // Default visible rows (then scroll down)
        bodyArea.setFont(new Font("Monospaced", Font.PLAIN, FONT_SIZE));
        JScrollPane bodyScrollPane = new JScrollPane(bodyArea);

        JPanel bodyPanel = new JPanel(new BorderLayout());
        bodyPanel.add(new JLabel("Cuerpo"), BorderLayout.NORTH);
        bodyPanel.add(bodyScrollPane, BorderLayout.CENTER);

        // Response area
        responseArea = new JTextArea(10, 1);
        responseArea.setFont(new Font("Monospaced", Font.PLAIN, FONT_SIZE));
        responseScrollPane = new JScrollPane(responseArea);

        responseImagePanel = new JPanel(new BorderLayout());
        JScrollPane responseImageScrollPane = new JScrollPane(responseImagePanel);

        responseTabbedPane = new JTabbedPane();
        responseTabbedPane.add("Respuesta", responseScrollPane);
        responseTabbedPane.add("Visualizar imagen", responseImageScrollPane);

        // Split panel to divide body and response area
        JSplitPane southSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, bodyPanel, responseTabbedPane);
        southSplitPane.setResizeWeight(0.5);

        southPanel.add(southSplitPane, BorderLayout.CENTER);
    }

    private void buildMenuBar() {
        menuBar = new JMenuBar();
        favsMenu = new JMenu("Favoritos");
        newRequestMenu = new JMenu("Nueva solicitud");
        menuBar.add(favsMenu);
        menuBar.add(newRequestMenu);

        handleLoadAllFavs();

        this.setJMenuBar(menuBar);
    }

    private Request buildRequestObject() {
        Request request = new Request();

        if ( nameField.getText().isEmpty() || nameField.getText() == null )
            request.setName("Nueva solicitud");
        else request.setName(nameField.getText());

        request.setAddress(urlField.getText());

        if ( bodyArea.getText().isEmpty() || bodyArea.getText() == null )
            request.setBody(null);
        else request.setBody(bodyArea.getText());

        request.setMethod( getMethodFromComboBox() );
        request.setHeader( getHeaderFromTable() );
        request.setQueryStrings( getQueryStringsFromTable() );

        return request;
    }

    private Method getMethodFromComboBox() {
        String selectedItem = (String) methodComboBox.getSelectedItem();
        return favRequestsManager.loadMethodByType(selectedItem);
    }

    private Header getHeaderFromTable() {
        String key = (String) headerTableModel.getValueAt(0, 0);
        String value = (String) headerTableModel.getValueAt(0, 1);

        if ( key != null && !key.equals("") && value != null && !value.equals("") )
            return new Header(key, value);

        else return null;
    }

    private List<QueryString> getQueryStringsFromTable() {

        List<QueryString> queryStringList = new LinkedList<>();

        for ( int i=0; i<queryStringsTableModel.getRowCount(); i++ ) {

            String key = (String) queryStringsTableModel.getValueAt(i, 0);
            String value = (String) queryStringsTableModel.getValueAt(i, 1);

            if ( key != null && !key.equals("") && value != null && !value.equals("") )
                queryStringList.add( new QueryString(key, value) );

        }

        if ( queryStringList.size() > 0 )
            return queryStringList;

        else return null;
    }

    public void handleAddToFavs() {
        if ( urlField.getText().isEmpty() )
            JOptionPane.showMessageDialog(centerPanel, "El campo URL no puede estar vacío", "Cliente HTTP", JOptionPane.WARNING_MESSAGE);
        else {
            favRequestsManager.addToFavs(buildRequestObject());
            JOptionPane.showMessageDialog(centerPanel, "Solicitud agregada a favoritos!", "Cliente HTTP", JOptionPane.INFORMATION_MESSAGE);
        }

    }
    public HttpResponse<String> handleSendRequest() {
        return httpClientManager.executeRequest(buildRequestObject());
    }
    public HttpResponse<byte[]> handleSendImageRequest() {
        return httpClientManager.executeImageRequest(buildRequestObject());
    }

    public void handleLoadAllFavs() {

        // Clean menu
        favsMenu.removeAll();

        // Re-load items
        for ( Request request : favRequestsManager.loadAllFavs() ) {

            JMenuItem favItem = new JMenuItem(request.getName());
            favsMenu.add(favItem);

            // Load fav when item is selected
            favItem.addActionListener(e -> {
                cleanFields();
                handleLoadFav(request.getId());
                replaceFavsButtons();
                activateListeners();

                deleteBtn.addActionListener(d -> {
                    handleDeleteFav(request);
                    cleanFields();
                });

                updateBtn.addActionListener(u -> {
                    handleModifyFav(request.getId());
                });

            });
        }

        validate();
        repaint();

    }

    private void handleLoadFav(Integer id) {

        Request r = favRequestsManager.loadFav(id);
        nameField.setText(r.getName());
        urlField.setText(r.getAddress());
        methodComboBox.setSelectedItem(r.getMethod().getType());
        bodyArea.setText(r.getBody());

        headerTableModel.setValueAt(r.getHeader().getHeader_key(), 0, 0);
        headerTableModel.setValueAt(r.getHeader().getHeader_value(), 0, 1);

        if (! r.getQueryStrings().isEmpty() ) {
            queryStringsTableModel.setRowCount(0); // Cleans query strings table
            for (QueryString qs : r.getQueryStrings()) {
                queryStringsTableModel.addRow( new Object[]{ qs.getQuery_key(), qs.getQuery_value() } ); // Adds query strings
            }
        }

    }

    private void handleModifyFav(Integer id) {

        if ( urlField.getText().isEmpty() )
            JOptionPane.showMessageDialog(centerPanel, "El campo URL no puede estar vacío", "Cliente HTTP", JOptionPane.WARNING_MESSAGE);

        else {
            Request r = buildRequestObject();
            r.setId(id);
            favRequestsManager.modifyFav(r);
            JOptionPane.showMessageDialog(centerPanel, "Solicitud modificada correctamente", "Cliente HTTP", JOptionPane.INFORMATION_MESSAGE);
        }

    }

    private void handleDeleteFav(Request r) {
        favRequestsManager.deleteFav(r);
        JOptionPane.showMessageDialog(centerPanel, "Solicitud eliminada de favoritos", "Cliente HTTP", JOptionPane.INFORMATION_MESSAGE);
    }

    private void replaceFavsButtons() {

        sendBtn = new JButton("Enviar");
        sendBtn.setBackground(new Color(122, 110, 60));
        sendBtn.setForeground(Color.WHITE);

        updateBtn = new JButton("Guardar cambios");
        updateBtn.setBackground(new Color(63, 114, 50));
        updateBtn.setForeground(Color.WHITE);

        deleteBtn = new JButton("Eliminar");
        deleteBtn.setBackground(new Color(114, 50, 50));
        deleteBtn.setForeground(Color.WHITE);

        // Replace requestButtonsPanel with favsButtonPanel
        favsButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        favsButtonPanel.add(methodComboBox);
        favsButtonPanel.add(sendBtn);
        favsButtonPanel.add(updateBtn);
        favsButtonPanel.add(deleteBtn);

        northPanel.remove(1); // Remove right panel
        northPanel.add(favsButtonPanel, BorderLayout.EAST); // Add new right panel

        validate();
        repaint();
    }

    private void replaceRequestButtons() {

        sendBtn = new JButton("Enviar");
        sendBtn.setBackground(new Color(122, 110, 60));
        sendBtn.setForeground(Color.WHITE);
        addToFavsBtn = new JButton("Agregar a favoritos");
        addToFavsBtn.setBackground(new Color(66, 50, 114));
        addToFavsBtn.setForeground(Color.WHITE);

        // Replace favsButtonPanel with requestButtonsPanel
        requestButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        requestButtonsPanel.add(methodComboBox);
        requestButtonsPanel.add(sendBtn);
        requestButtonsPanel.add(addToFavsBtn);

        northPanel.remove(1);
        northPanel.add(requestButtonsPanel, BorderLayout.EAST);

        validate();
        repaint();
    }

    private void cleanFields() {

        nameField.setText("");
        urlField.setText("");
        methodComboBox.setSelectedItem("GET");
        bodyArea.setText("");
        responseArea.setText("");
        responseImagePanel.removeAll();

        // Clean tables
        headerTableModel.setRowCount(0);
        queryStringsTableModel.setRowCount(0);

        // Add empty rows
        headerTableModel.addRow( new Object[]{"", ""} );
        queryStringsTableModel.addRow( new Object[]{"", ""} );
    }

}
