/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db_admin;

//import com.mysql.cj.jdbc.Blob;
//import java.io.BufferedOutputStream;
import java.io.File;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.io.FileOutputStream;
import javax.swing.JFileChooser;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfEncodings;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
//import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Phrase;





/**
 *
 * @author adalavelace
 */
public class View extends javax.swing.JFrame {

    /**
     * Creates new form View
     * @throws SQLException 
     */
    private Connection getConnection() throws SQLException
    {
        String url = "jdbc:mysql://localhost:3306/leisure";
        String user = "root";
        String pass = "PrekrasnoeDaloko8519*";
        Connection con = DriverManager.getConnection(url,user,pass);
        return con;
    }

    private void setCombo(JComboBox box, String id, String table_name)
    {
        try{

            Connection con = getConnection();
            Statement st= con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM "+table_name);
            while(rs.next())
                {
                 box.addItem(rs.getString(id));
                    // Object[] row = new Object[columns.length];
                    // for (int i = 0; i < columns.length; i++){
                    //     row[i] = rs.getString(columns[i]);
                    // }
                }
                con.close();

        }
        catch(Exception e)
        {
            System.out.println("Error "+ e.getMessage());
        }
    }
    

    private void deleteSelected(JTable table, String table_name, String idname, String[] columns)
    {            
        try {
            Connection con = getConnection();
            Statement st= con.createStatement();
            int[] rows = table.getSelectedRows();
            for (int i = 0; i < rows.length; i++)
                st.executeUpdate("DELETE FROM " + table_name + " WHERE "+idname+" = "+table.getValueAt(rows[i], 0));            
            con.close();
            showTable(table, table_name, columns);
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(new JFrame(), "Нельзя удалить запись", "Диалог", JOptionPane.ERROR_MESSAGE);
            System.out.println("Error "+ e.getMessage());
        }
    }
    
    private void simpleAdd(JTextField text, JTable table, String table_name, String name,String[] columns){
        try{

            Connection con = getConnection();

            Statement st = con.createStatement();

            String field_name = text.getText();
            
            if (field_name.length()!=0)
            {
                st.executeUpdate("INSERT INTO " +table_name+"("+name+") VALUES('"+field_name+"')");
                text.setText("");
                JOptionPane.showMessageDialog(new JFrame(), "Запись успешно добавлена", "Диалог", JOptionPane.INFORMATION_MESSAGE);
            }
            else
            {
                JOptionPane.showMessageDialog(new JFrame(), "Заполните поле", "Диалог", JOptionPane.ERROR_MESSAGE);

            }
            
            con.close();

            showTable(table, table_name, columns);
        }
        catch(Exception e){
            System.out.println("Error "+ e.getMessage());
            JOptionPane.showMessageDialog(new JFrame(), e.getMessage(), "Диалог", JOptionPane.ERROR_MESSAGE);


        }
        
    }
            
    
    private void showTable(JTable table, String table_name, String[] columns){
        try {
            Connection con = getConnection();
            Statement st= con.createStatement();
            
            DefaultTableModel model = new DefaultTableModel(columns, 0);
            table.setModel(model);
            
            ResultSet rs = st.executeQuery("SELECT * FROM "+table_name);
                System.out.println("ok for " + table_name);

                while(rs.next())
                {
                    
                    Object[] row = new Object[columns.length];
                    for (int i = 0; i < columns.length; i++){
                        row[i] = rs.getString(columns[i]);
                    }
                    model.addRow(row);
                }


                con.close();
                
        }
        catch(Exception e)
        {
            System.out.println("Error "+ e.getMessage());
        }
    }
    
    private void createPdf(JTable table)
    {
            String path ="";
            JFileChooser j=new JFileChooser();
            j.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            int x=j.showSaveDialog(this);
            if(x==JFileChooser.APPROVE_OPTION)
            {
                path=j.getSelectedFile().getPath();
            }
            
            Document doc = new Document();
          
            try{
                PdfWriter.getInstance(doc, new FileOutputStream(path));//+"/report.pdf"
                doc.open();
                

               //float[] cellsize = {100f, 200f};
                PdfPTable tbl = new PdfPTable(table.getColumnCount());
                

                BaseFont helvetica = BaseFont.createFont("./src/db_admin/resourses/arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                Font font = new Font(helvetica, 8, Font.NORMAL);
                Font namefont = new Font(helvetica, 8, Font.BOLD);

                
                for(int c=0;c<table.getColumnCount();c++){
                    String name = table.getColumnName(c);
                    Chunk chunk = new Chunk(name, namefont);
                    Phrase p = new Phrase(chunk);
                    tbl.addCell(p);
                }
                
                for(int i=0;i<table.getRowCount();i++){
                    
                        for(int c=0;c<table.getColumnCount();c++){
                            String F1 = String.valueOf(table.getValueAt(i, c));
                                Chunk chunk = new Chunk(F1, font);
                                Phrase p = new Phrase(chunk);
                                tbl.addCell(p);
                        }

                    }
               
                doc.add(tbl);
                JOptionPane.showMessageDialog(new JFrame(), "Данные успешно сохранены", "Диалог", JOptionPane.INFORMATION_MESSAGE);

            }
            catch(Exception e)
            {
                System.out.println("Error "+ e.getMessage());
                JOptionPane.showMessageDialog(new JFrame(), "Что-то пошло не так", "Диалог", JOptionPane.ERROR_MESSAGE);

            }
            doc.close();
    }
    
    private void updateOne(JTable table, String table_name, String id, String[] column)
    {
        try{
        Connection con = getConnection();
        
        int x = -1;
        int y = -1;
        x = table.getEditingRow();
        y = table.getEditingColumn();
        if (table.isEditing()){
            table.getCellEditor().stopCellEditing();
        }
        
        System.out.println(table.getValueAt(x, y));
        if(y!=0){
            String add = String.valueOf(table.getValueAt(x, y));
            String sql = "UPDATE "+table_name+" SET "+column[y]+" = ? WHERE "+id+" = ?";

            PreparedStatement stmt = con.prepareStatement(sql);
            
            if(add.length()==0)
                add=null;
                
            stmt.setString( 1, add);
            stmt.setInt( 2, Integer.valueOf(String.valueOf(table.getValueAt(x, 0))));

            stmt.executeUpdate();
            stmt.close();
        }
        else 
        {
            JOptionPane.showMessageDialog(new JFrame(), "Вы не можете поменять поле id", "Диалог", JOptionPane.ERROR_MESSAGE);

        }
            showTable(table, table_name, column);

        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(new JFrame(), "Поставьте курсор в ячейку, которую нужно изменить", "Диалог", JOptionPane.ERROR_MESSAGE);

            System.out.println("Error: "+ e.getMessage());
        }
        
    }
            
    public View() {
        initComponents();
        
        try {
            showTable(cityTable, "city", new String[]{"idcity","city_name"});
            showTable(ownerTable, "owner", new String[]{"idowner","status","nameorg","fio","phonenum","adress","idcity"});
            showTable(type_placeTable, "type_of_place", new String[]{"id_type_of_place","type_name"});
            showTable(placeTable, "place", new String[]{"idplace","nameplace","adress","numseats","opendate","open","idowner","idcity","id_type_of_place","start","end"});
            showTable(type_eventTable, "type_of_event", new String[]{"id_type_of_event","type_name"});
            showTable(eventTable, "event", new String[]{"date","eventname","numofvisitors","idplace","id_type_of_event"});
            setCombo(ownerCityComboBox, "idcity","city");
            setCombo(placeCityComboBox, "idcity","city");
            setCombo(selectCityComboBox, "idcity","city");
            setCombo(placeOwnerComboBox, "idowner","owner");
            setCombo(placeTypeComboBox, "id_type_of_place","type_of_place");
            setCombo(eventPlaceComboBox, "idplace","place");
            setCombo(eventTypeComboBox, "id_type_of_event","type_of_event");
            setCombo(selectPlaceTypeComboBox, "id_type_of_place","type_of_place");
            setCombo(selectEventTypeComboBox, "id_type_of_event","type_of_event");




        }
        catch(Exception e)
        {
            System.out.println("Error "+ e.getMessage());
        }
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ownerbuttonGroup = new javax.swing.ButtonGroup();
        citybuttonGroup = new javax.swing.ButtonGroup();
        mainTabbedPane = new javax.swing.JTabbedPane();
        cityPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        cityTable = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        cityNameTextField = new javax.swing.JTextField();
        cityCreateButton = new javax.swing.JButton();
        cityUpdateButton = new javax.swing.JButton();
        cityDeleteButton = new javax.swing.JButton();
        cityReloadButton = new javax.swing.JButton();
        cityPdfButton = new javax.swing.JButton();
        ownerPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        ownerTable = new javax.swing.JTable();
        ownerUpdateButton = new javax.swing.JButton();
        ownerDeleteButton = new javax.swing.JButton();
        ownerStatusLabel = new javax.swing.JLabel();
        ownerNameLabel = new javax.swing.JLabel();
        ownerFioLabel = new javax.swing.JLabel();
        ownerPhoneLabel = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        ownerNameTextField = new javax.swing.JTextField();
        ownerFioTextField = new javax.swing.JTextField();
        ownerPhoneTextField = new javax.swing.JTextField();
        ownerAdressTextField = new javax.swing.JTextField();
        ownerCreateButton = new javax.swing.JButton();
        ownerReloadButton = new javax.swing.JButton();
        ownerPdfButton = new javax.swing.JButton();
        ownerCityComboBox = new javax.swing.JComboBox<>();
        ownerFisRadioButton = new javax.swing.JRadioButton();
        ownerYurRadioButton = new javax.swing.JRadioButton();
        type_placePanel = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        type_placeTable = new javax.swing.JTable();
        type_placeTypeLabel = new javax.swing.JLabel();
        type_placeTypeTextField = new javax.swing.JTextField();
        type_placeCreateButton = new javax.swing.JButton();
        type_placeUpdateButton = new javax.swing.JButton();
        type_placeDeleteButton = new javax.swing.JButton();
        type_placeReloadButton = new javax.swing.JButton();
        type_placePdfButton = new javax.swing.JButton();
        placePanel = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        placeTable = new javax.swing.JTable();
        placeNameLabel = new javax.swing.JLabel();
        placeAdressLabel = new javax.swing.JLabel();
        placeSeatsLabel = new javax.swing.JLabel();
        placeOpendateLabel = new javax.swing.JLabel();
        placeOpenLabel = new javax.swing.JLabel();
        placeOwnerLabel = new javax.swing.JLabel();
        placeCityLabel = new javax.swing.JLabel();
        placeTypeLabel = new javax.swing.JLabel();
        placeStartLabel = new javax.swing.JLabel();
        placeEndLabel = new javax.swing.JLabel();
        placeCreateButton = new javax.swing.JButton();
        placeNameTextField = new javax.swing.JTextField();
        placeSeatsTextField = new javax.swing.JTextField();
        placeAdressTextField = new javax.swing.JTextField();
        placeOpendateTextField = new javax.swing.JTextField();
        placeStartTextField = new javax.swing.JTextField();
        placeEndTextField = new javax.swing.JTextField();
        placeUpdateButton = new javax.swing.JButton();
        placeDeleteButton = new javax.swing.JButton();
        placeReloadButton = new javax.swing.JButton();
        placePdfButton = new javax.swing.JButton();
        cityYesRadioButton = new javax.swing.JRadioButton();
        cityNoRadioButton = new javax.swing.JRadioButton();
        placeOwnerComboBox = new javax.swing.JComboBox<>();
        placeCityComboBox = new javax.swing.JComboBox<>();
        placeTypeComboBox = new javax.swing.JComboBox<>();
        type_eventPanel = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        type_eventTable = new javax.swing.JTable();
        type_eventLabel = new javax.swing.JLabel();
        type_eventTextField = new javax.swing.JTextField();
        type_eventCreateButton = new javax.swing.JButton();
        type_eventUpdateButton = new javax.swing.JButton();
        type_eventDeleteButton = new javax.swing.JButton();
        type_eventReloadButton = new javax.swing.JButton();
        type_eventPdfButton = new javax.swing.JButton();
        eventPanel = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        eventTable = new javax.swing.JTable();
        eventDetaLabel = new javax.swing.JLabel();
        eventNameLabel = new javax.swing.JLabel();
        eventVisitorsLabel = new javax.swing.JLabel();
        eventPlaceLabel = new javax.swing.JLabel();
        eventTypeLabel = new javax.swing.JLabel();
        eventDateTextField = new javax.swing.JTextField();
        eventNameTextField = new javax.swing.JTextField();
        eventVisitorsTextField = new javax.swing.JTextField();
        eventCreateButton = new javax.swing.JButton();
        eventUpdateButton = new javax.swing.JButton();
        eventDeleteButton = new javax.swing.JButton();
        eventReloadButton = new javax.swing.JButton();
        eventPdfButton = new javax.swing.JButton();
        eventPlaceComboBox = new javax.swing.JComboBox<>();
        eventTypeComboBox = new javax.swing.JComboBox<>();
        selectPanel = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        selectTable = new javax.swing.JTable();
        selectButton1 = new javax.swing.JButton();
        pdfButton1 = new javax.swing.JButton();
        selectButton2 = new javax.swing.JButton();
        selectButton3 = new javax.swing.JButton();
        selectButton4 = new javax.swing.JButton();
        selectButton5 = new javax.swing.JButton();
        selectPlaceButton = new javax.swing.JButton();
        selectEventButton = new javax.swing.JButton();
        selectCityComboBox = new javax.swing.JComboBox<>();
        selectPlaceTypeComboBox = new javax.swing.JComboBox<>();
        selectEventTypeComboBox = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Места досуга горожан");
        setPreferredSize(new java.awt.Dimension(1000, 1000));
        setSize(new java.awt.Dimension(1500, 6000));

        cityTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "id", "city"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                true, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(cityTable);

        jLabel1.setText("Название города*:");

        cityNameTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cityNameTextFieldActionPerformed(evt);
            }
        });

        cityCreateButton.setText("Добавить");
        cityCreateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cityCreateButtonActionPerformed(evt);
            }
        });

        cityUpdateButton.setText("Внести изменения");
        cityUpdateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cityUpdateButtonActionPerformed(evt);
            }
        });

        cityDeleteButton.setText("Удалить");
        cityDeleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cityDeleteButtonActionPerformed(evt);
            }
        });

        cityReloadButton.setText("Обновить таблицу");
        cityReloadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cityReloadButtonActionPerformed(evt);
            }
        });

        cityPdfButton.setText("Сохранить таблицу");
        cityPdfButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cityPdfButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout cityPanelLayout = new javax.swing.GroupLayout(cityPanel);
        cityPanel.setLayout(cityPanelLayout);
        cityPanelLayout.setHorizontalGroup(
            cityPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cityPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(cityPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jScrollPane1)
                    .addGroup(cityPanelLayout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cityNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(111, 111, 111)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 327, Short.MAX_VALUE)
                .addGroup(cityPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cityUpdateButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cityDeleteButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(cityPanelLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(cityPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cityPdfButton, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cityReloadButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
            .addGroup(cityPanelLayout.createSequentialGroup()
                .addComponent(cityCreateButton)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        cityPanelLayout.setVerticalGroup(
            cityPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cityPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(cityPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(cityPanelLayout.createSequentialGroup()
                        .addComponent(cityUpdateButton)
                        .addGap(18, 18, 18)
                        .addComponent(cityDeleteButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cityReloadButton)
                        .addGap(18, 18, 18)
                        .addComponent(cityPdfButton)))
                .addGap(49, 49, 49)
                .addGroup(cityPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(cityNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(cityCreateButton)
                .addContainerGap(582, Short.MAX_VALUE))
        );

        mainTabbedPane.addTab("Города", cityPanel);

        ownerTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        // ownerTable.setMinimumSize(new java.awt.Dimension(30, 64));
        // ownerTable.setPreferredSize(new java.awt.Dimension(150, 64));
        // ownerTable.setRequestFocusEnabled(false);
        jScrollPane2.setViewportView(ownerTable);

        ownerUpdateButton.setText("Внести изменения");
        ownerUpdateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ownerUpdateButtonActionPerformed(evt);
            }
        });

        ownerDeleteButton.setText("Удалить");
        ownerDeleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ownerDeleteButtonActionPerformed(evt);
            }
        });

        ownerStatusLabel.setText("Статус*:");

        ownerNameLabel.setText("Название организации (для юридического лица):");

        ownerFioLabel.setText("ФИО (для физического лица):");

        ownerPhoneLabel.setText("Номер телефона*:");

        jLabel6.setText("Адрес*:");

        jLabel7.setText("id города*:");

        ownerNameTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ownerNameTextFieldActionPerformed(evt);
            }
        });

        ownerCreateButton.setText("Добавить");
        ownerCreateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ownerCreateButtonActionPerformed(evt);
            }
        });

        ownerReloadButton.setText("Обновить таблицу");
        ownerReloadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ownerReloadButtonActionPerformed(evt);
            }
        });

        ownerPdfButton.setText("Сохранить таблицу");
        ownerPdfButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ownerPdfButtonActionPerformed(evt);
            }
        });

        ownerCityComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ownerCityComboBoxActionPerformed(evt);
            }
        });

        ownerbuttonGroup.add(ownerFisRadioButton);
        ownerFisRadioButton.setText("физ.лицо");
        ownerFisRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ownerFisRadioButtonActionPerformed(evt);
            }
        });

        ownerbuttonGroup.add(ownerYurRadioButton);
        ownerYurRadioButton.setText("юр.лицо");
        ownerYurRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ownerYurRadioButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout ownerPanelLayout = new javax.swing.GroupLayout(ownerPanel);
        ownerPanel.setLayout(ownerPanelLayout);
        ownerPanelLayout.setHorizontalGroup(
            ownerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ownerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ownerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ownerPanelLayout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 549, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 257, Short.MAX_VALUE)
                        .addGroup(ownerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(ownerUpdateButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(ownerDeleteButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(ownerReloadButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(ownerPdfButton, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(ownerPanelLayout.createSequentialGroup()
                        .addGroup(ownerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ownerNameLabel)
                            .addComponent(ownerFioLabel)
                            .addComponent(ownerPhoneLabel)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7)
                            .addComponent(ownerStatusLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(ownerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(ownerPanelLayout.createSequentialGroup()
                                .addComponent(ownerFisRadioButton, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(30, 30, 30)
                                .addComponent(ownerYurRadioButton, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(ownerPanelLayout.createSequentialGroup()
                                .addGroup(ownerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(ownerCityComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(ownerFioTextField, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(ownerPhoneTextField, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, ownerPanelLayout.createSequentialGroup()
                                        .addComponent(ownerNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addComponent(ownerAdressTextField))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel8)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(ownerPanelLayout.createSequentialGroup()
                .addComponent(ownerCreateButton)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        ownerPanelLayout.setVerticalGroup(
            ownerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ownerPanelLayout.createSequentialGroup()
                .addGroup(ownerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(ownerPanelLayout.createSequentialGroup()
                        .addComponent(ownerUpdateButton)
                        .addGap(18, 18, 18)
                        .addComponent(ownerDeleteButton)
                        .addGap(18, 18, 18)
                        .addComponent(ownerReloadButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(ownerPdfButton)))
                .addGap(37, 37, 37)
                .addGroup(ownerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ownerStatusLabel)
                    .addComponent(ownerFisRadioButton)
                    .addComponent(ownerYurRadioButton))
                .addGap(16, 16, 16)
                .addGroup(ownerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ownerNameLabel)
                    .addComponent(jLabel8)
                    .addComponent(ownerNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(ownerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ownerFioLabel)
                    .addComponent(ownerFioTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(ownerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ownerPhoneTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ownerPhoneLabel))
                .addGap(18, 18, 18)
                .addGroup(ownerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(ownerAdressTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(ownerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(ownerCityComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ownerCreateButton)
                .addContainerGap(428, Short.MAX_VALUE))
        );

        mainTabbedPane.addTab("Владельцы", ownerPanel);

        type_placeTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane3.setViewportView(type_placeTable);

        type_placeTypeLabel.setText("Тип места*:");

        type_placeTypeTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                type_placeTypeTextFieldActionPerformed(evt);
            }
        });

        type_placeCreateButton.setText("Добавить");
        type_placeCreateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                type_placeCreateButtonActionPerformed(evt);
            }
        });

        type_placeUpdateButton.setText("Внести изменения");
        type_placeUpdateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                type_placeUpdateButtonActionPerformed(evt);
            }
        });

        type_placeDeleteButton.setText("Удалить");
        type_placeDeleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                type_placeDeleteButtonActionPerformed(evt);
            }
        });

        type_placeReloadButton.setText("Обновить таблицу");
        type_placeReloadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                type_placeReloadButtonActionPerformed(evt);
            }
        });

        type_placePdfButton.setText("Сохранить таблицу");
        type_placePdfButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                type_placePdfButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout type_placePanelLayout = new javax.swing.GroupLayout(type_placePanel);
        type_placePanel.setLayout(type_placePanelLayout);
        type_placePanelLayout.setHorizontalGroup(
            type_placePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(type_placePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(type_placeTypeLabel)
                .addGap(33, 33, 33)
                .addComponent(type_placeTypeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(type_placePanelLayout.createSequentialGroup()
                .addComponent(type_placeCreateButton)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(type_placePanelLayout.createSequentialGroup()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(type_placePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(type_placeDeleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(type_placeUpdateButton, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(type_placeReloadButton, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(type_placePdfButton, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16))
        );
        type_placePanelLayout.setVerticalGroup(
            type_placePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(type_placePanelLayout.createSequentialGroup()
                .addGroup(type_placePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(type_placePanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(type_placePanelLayout.createSequentialGroup()
                        .addComponent(type_placeUpdateButton)
                        .addGap(18, 18, 18)
                        .addComponent(type_placeDeleteButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(type_placeReloadButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(type_placePdfButton)))
                .addGap(45, 45, 45)
                .addGroup(type_placePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(type_placeTypeLabel)
                    .addComponent(type_placeTypeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(37, 37, 37)
                .addComponent(type_placeCreateButton)
                .addContainerGap(595, Short.MAX_VALUE))
        );

        mainTabbedPane.addTab("Типы мест", type_placePanel);

        placeTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        placeTable.setMinimumSize(new java.awt.Dimension(200, 100));
        jScrollPane4.setViewportView(placeTable);

        placeNameLabel.setText("Название*:");

        placeAdressLabel.setText("Адрес*:");

        placeSeatsLabel.setText("Количество мест:");

        placeOpendateLabel.setText("Дата открытия*:");

        placeOpenLabel.setText("Открыто*:");

        placeOwnerLabel.setText("id владельца*:");

        placeCityLabel.setText("id города*:");

        placeTypeLabel.setText("id типа места*:");

        placeStartLabel.setText("Начало работы:");

        placeEndLabel.setText("Конец работы:");

        placeCreateButton.setText("Добавить");
        placeCreateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                placeCreateButtonActionPerformed(evt);
            }
        });

        placeNameTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                placeNameTextFieldActionPerformed(evt);
            }
        });

        placeSeatsTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                placeSeatsTextFieldActionPerformed(evt);
            }
        });

        placeAdressTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                placeAdressTextFieldActionPerformed(evt);
            }
        });

        placeOpendateTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                placeOpendateTextFieldActionPerformed(evt);
            }
        });

        placeStartTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                placeStartTextFieldActionPerformed(evt);
            }
        });

        placeEndTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                placeEndTextFieldActionPerformed(evt);
            }
        });

        placeUpdateButton.setText("Внести изменения");
        placeUpdateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                placeUpdateButtonActionPerformed(evt);
            }
        });

        placeDeleteButton.setText("Удалить");
        placeDeleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                placeDeleteButtonActionPerformed(evt);
            }
        });

        placeReloadButton.setText("Обновить таблицу");
        placeReloadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                placeReloadButtonActionPerformed(evt);
            }
        });

        placePdfButton.setText("Сохранить таблицу");
        placePdfButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                placePdfButtonActionPerformed(evt);
            }
        });

        citybuttonGroup.add(cityYesRadioButton);
        cityYesRadioButton.setText("да");

        citybuttonGroup.add(cityNoRadioButton);
        cityNoRadioButton.setText("нет");
        cityNoRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cityNoRadioButtonActionPerformed(evt);
            }
        });

        placeOwnerComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                placeOwnerComboBoxActionPerformed(evt);
            }
        });

        placeCityComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                placeCityComboBoxActionPerformed(evt);
            }
        });

        placeTypeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                placeTypeComboBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout placePanelLayout = new javax.swing.GroupLayout(placePanel);
        placePanel.setLayout(placePanelLayout);
        placePanelLayout.setHorizontalGroup(
            placePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(placePanelLayout.createSequentialGroup()
                .addGap(108, 108, 108)
                .addGroup(placePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(placeEndTextField)
                    .addComponent(placeOwnerComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(placeCityComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(placeTypeComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(placeStartTextField))
                .addGap(685, 685, 685))
            .addGroup(placePanelLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 645, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(placePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(placePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(placeUpdateButton, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                        .addComponent(placeDeleteButton, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE))
                    .addComponent(placeReloadButton, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(placePdfButton, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(17, 17, 17))
            .addGroup(placePanelLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(placePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(placeNameLabel)
                    .addComponent(placeAdressLabel)
                    .addComponent(placeSeatsLabel)
                    .addComponent(placeOpendateLabel)
                    .addComponent(placeOwnerLabel)
                    .addComponent(placeCityLabel)
                    .addComponent(placeTypeLabel)
                    .addComponent(placeOpenLabel)
                    .addComponent(placeEndLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(placePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(placeNameTextField)
                    .addComponent(placeSeatsTextField)
                    .addComponent(placeAdressTextField)
                    .addComponent(placeOpendateTextField))
                .addGap(685, 685, 685))
            .addGroup(placePanelLayout.createSequentialGroup()
                .addGroup(placePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(placePanelLayout.createSequentialGroup()
                        .addGap(114, 114, 114)
                        .addComponent(cityYesRadioButton, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(36, 36, 36)
                        .addComponent(cityNoRadioButton, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(placePanelLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(placeStartLabel))
                    .addGroup(placePanelLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(placeCreateButton)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        placePanelLayout.setVerticalGroup(
            placePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(placePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(placePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(placePanelLayout.createSequentialGroup()
                        .addComponent(placeUpdateButton)
                        .addGap(18, 18, 18)
                        .addComponent(placeDeleteButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(placeReloadButton)
                        .addGap(18, 18, 18)
                        .addComponent(placePdfButton)))
                .addGroup(placePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(placePanelLayout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addComponent(placeNameLabel)
                        .addGap(28, 28, 28))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, placePanelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(placeNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addGroup(placePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(placeAdressLabel)
                    .addComponent(placeAdressTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(13, 13, 13)
                .addGroup(placePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(placeSeatsLabel)
                    .addComponent(placeSeatsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(placePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(placeOpendateLabel)
                    .addComponent(placeOpendateTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(placePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(placeOpenLabel)
                    .addGroup(placePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cityYesRadioButton)
                        .addComponent(cityNoRadioButton)))
                .addGap(18, 18, 18)
                .addGroup(placePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(placeOwnerLabel)
                    .addComponent(placeOwnerComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(placePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(placeCityLabel)
                    .addComponent(placeCityComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(placePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(placeTypeLabel)
                    .addComponent(placeTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(placePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(placeStartLabel)
                    .addComponent(placeStartTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(placePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(placeEndLabel)
                    .addComponent(placeEndTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addComponent(placeCreateButton)
                .addContainerGap(268, Short.MAX_VALUE))
        );

        mainTabbedPane.addTab("Место", placePanel);

        type_eventTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane5.setViewportView(type_eventTable);

        type_eventLabel.setText("Тип Мероприятия*:");

        type_eventTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                type_eventTextFieldActionPerformed(evt);
            }
        });

        type_eventCreateButton.setText("Добавить");
        type_eventCreateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                type_eventCreateButtonActionPerformed(evt);
            }
        });

        type_eventUpdateButton.setText("Внести изменения");
        type_eventUpdateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                type_eventUpdateButtonActionPerformed(evt);
            }
        });

        type_eventDeleteButton.setText("Удалить");
        type_eventDeleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                type_eventDeleteButtonActionPerformed(evt);
            }
        });

        type_eventReloadButton.setText("Обновить таблицу");
        type_eventReloadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                type_eventReloadButtonActionPerformed(evt);
            }
        });

        type_eventPdfButton.setText("Сохранить таблицу");
        type_eventPdfButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                type_eventPdfButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout type_eventPanelLayout = new javax.swing.GroupLayout(type_eventPanel);
        type_eventPanel.setLayout(type_eventPanelLayout);
        type_eventPanelLayout.setHorizontalGroup(
            type_eventPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(type_eventPanelLayout.createSequentialGroup()
                .addGroup(type_eventPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(type_eventPanelLayout.createSequentialGroup()
                        .addComponent(type_eventLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(type_eventTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(type_eventCreateButton, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(type_eventPanelLayout.createSequentialGroup()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 472, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(type_eventPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(type_eventUpdateButton, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(type_eventDeleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(type_eventReloadButton, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(type_eventPdfButton, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16))
        );
        type_eventPanelLayout.setVerticalGroup(
            type_eventPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(type_eventPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(type_eventPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(type_eventPanelLayout.createSequentialGroup()
                        .addComponent(type_eventUpdateButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(type_eventDeleteButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(type_eventReloadButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(type_eventPdfButton)))
                .addGap(58, 58, 58)
                .addGroup(type_eventPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(type_eventLabel)
                    .addComponent(type_eventTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(33, 33, 33)
                .addComponent(type_eventCreateButton)
                .addContainerGap(589, Short.MAX_VALUE))
        );

        mainTabbedPane.addTab("Типы мероприятий", type_eventPanel);

        eventTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane6.setViewportView(eventTable);

        eventDetaLabel.setText("Дата*:");

        eventNameLabel.setText("Название*:");

        eventVisitorsLabel.setText("Число посетивших*:");

        eventPlaceLabel.setText("id места проведения*:");

        eventTypeLabel.setText("id типа мероприятия*:");

        eventDateTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eventDateTextFieldActionPerformed(evt);
            }
        });

        eventVisitorsTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eventVisitorsTextFieldActionPerformed(evt);
            }
        });

        eventCreateButton.setText("Добавить");
        eventCreateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eventCreateButtonActionPerformed(evt);
            }
        });

        eventUpdateButton.setText("Внести изменения");
        eventUpdateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eventUpdateButtonActionPerformed(evt);
            }
        });

        eventDeleteButton.setText("Удалить");
        eventDeleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eventDeleteButtonActionPerformed(evt);
            }
        });

        eventReloadButton.setText("Обновить таблицу");
        eventReloadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eventReloadButtonActionPerformed(evt);
            }
        });

        eventPdfButton.setText("Сохранить таблицу");
        eventPdfButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eventPdfButtonActionPerformed(evt);
            }
        });

        eventPlaceComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eventPlaceComboBoxActionPerformed(evt);
            }
        });

        eventTypeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eventTypeComboBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout eventPanelLayout = new javax.swing.GroupLayout(eventPanel);
        eventPanel.setLayout(eventPanelLayout);
        eventPanelLayout.setHorizontalGroup(
            eventPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(eventPanelLayout.createSequentialGroup()
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 547, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 259, Short.MAX_VALUE)
                .addGroup(eventPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(eventPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, eventPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(eventUpdateButton, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(eventDeleteButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addComponent(eventReloadButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(eventPdfButton, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12))
            .addGroup(eventPanelLayout.createSequentialGroup()
                .addGroup(eventPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(eventPanelLayout.createSequentialGroup()
                        .addGroup(eventPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(eventNameLabel)
                            .addComponent(eventVisitorsLabel)
                            .addComponent(eventPlaceLabel)
                            .addComponent(eventTypeLabel)
                            .addComponent(eventDetaLabel))
                        .addGap(32, 32, 32)
                        .addGroup(eventPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(eventDateTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE)
                            .addComponent(eventNameTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE)
                            .addComponent(eventVisitorsTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE)
                            .addComponent(eventPlaceComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(eventTypeComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(eventCreateButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        eventPanelLayout.setVerticalGroup(
            eventPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(eventPanelLayout.createSequentialGroup()
                .addGroup(eventPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(eventPanelLayout.createSequentialGroup()
                        .addGroup(eventPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(eventPanelLayout.createSequentialGroup()
                                .addGap(17, 17, 17)
                                .addComponent(eventUpdateButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(eventDeleteButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(eventReloadButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(eventPdfButton)))
                        .addGap(44, 44, 44)
                        .addGroup(eventPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(eventDetaLabel)
                            .addComponent(eventDateTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(eventPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(eventNameLabel)
                            .addComponent(eventNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(eventPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(eventVisitorsLabel)
                            .addComponent(eventVisitorsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(eventPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(eventPlaceLabel)
                            .addComponent(eventPlaceComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(eventTypeLabel))
                    .addComponent(eventTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addComponent(eventCreateButton)
                .addGap(0, 445, Short.MAX_VALUE))
        );

        mainTabbedPane.addTab("Мероприятия", eventPanel);

        selectTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane7.setViewportView(selectTable);

        selectButton1.setText("Показать все места в городе:");
        selectButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectButton1ActionPerformed(evt);
            }
        });

        pdfButton1.setText("Сохранить таблицу");
        pdfButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pdfButton1ActionPerformed(evt);
            }
        });

        selectButton2.setText("Показать все прошедшие мероприятия");
        selectButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectButton2ActionPerformed(evt);
            }
        });

        selectButton3.setText("Показать запланированные мероприятия");
        selectButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectButton3ActionPerformed(evt);
            }
        });

        selectButton4.setText("Показать все открытые места");
        selectButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectButton4ActionPerformed(evt);
            }
        });

        selectButton5.setText("Показать все закрытые места");
        selectButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectButton5ActionPerformed(evt);
            }
        });

        selectPlaceButton.setText("Показать все места типа:");
        selectPlaceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectPlaceButtonActionPerformed(evt);
            }
        });

        selectEventButton.setText("Показать все мероприятия типа:");
        selectEventButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectEventButtonActionPerformed(evt);
            }
        });

        selectCityComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectCityComboBoxActionPerformed(evt);
            }
        });

        selectPlaceTypeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectPlaceTypeComboBoxActionPerformed(evt);
            }
        });

        selectEventTypeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectEventTypeComboBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout selectPanelLayout = new javax.swing.GroupLayout(selectPanel);
        selectPanel.setLayout(selectPanelLayout);
        selectPanelLayout.setHorizontalGroup(
            selectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(selectPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(selectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 763, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(selectButton2)
                    .addComponent(selectButton3)
                    .addComponent(selectButton4)
                    .addComponent(selectButton5)
                    .addGroup(selectPanelLayout.createSequentialGroup()
                        .addGroup(selectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, selectPanelLayout.createSequentialGroup()
                                .addComponent(selectEventButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(selectEventTypeComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(selectPanelLayout.createSequentialGroup()
                                .addComponent(selectPlaceButton)
                                .addGap(55, 55, 55)
                                .addComponent(selectPlaceTypeComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, selectPanelLayout.createSequentialGroup()
                                .addComponent(selectButton1)
                                .addGap(32, 32, 32)
                                .addComponent(selectCityComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(28, 28, 28)
                        .addComponent(pdfButton1)))
                .addContainerGap(219, Short.MAX_VALUE))
        );
        selectPanelLayout.setVerticalGroup(
            selectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(selectPanelLayout.createSequentialGroup()
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36)
                .addGroup(selectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(selectButton1)
                    .addComponent(selectCityComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addComponent(selectButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(selectButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(selectButton4)
                .addGap(12, 12, 12)
                .addComponent(selectButton5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(selectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(selectPlaceButton)
                    .addComponent(selectPlaceTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(selectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(selectPanelLayout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(pdfButton1))
                    .addGroup(selectPanelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(selectPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(selectEventButton)
                            .addComponent(selectEventTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(0, 245, Short.MAX_VALUE))
        );

        mainTabbedPane.addTab("Выборка", selectPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mainTabbedPane)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mainTabbedPane)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cityNameTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cityNameTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cityNameTextFieldActionPerformed

    private void cityCreateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cityCreateButtonActionPerformed
        
        simpleAdd(cityNameTextField, cityTable, "city", "city_name",new String[]{"idcity","city_name"});
        setCombo(ownerCityComboBox, "idcity","city");


    }//GEN-LAST:event_cityCreateButtonActionPerformed

    
    
    private void cityUpdateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cityUpdateButtonActionPerformed
          
        updateOne(cityTable,  "city", "idcity", new String[]{"idcity","city_name"});

    }//GEN-LAST:event_cityUpdateButtonActionPerformed

    private void cityDeleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cityDeleteButtonActionPerformed

        deleteSelected(cityTable, "city", "idcity",new String[]{"idcity","city_name"});
        
    }//GEN-LAST:event_cityDeleteButtonActionPerformed

    private void ownerUpdateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ownerUpdateButtonActionPerformed
        // TODO add your handling code here:
        updateOne(ownerTable,  "owner", "idowner", new String[]{"idowner","status","nameorg","fio","phonenum","adress","idcity"});

    }//GEN-LAST:event_ownerUpdateButtonActionPerformed

    private void ownerNameTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ownerNameTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ownerNameTextFieldActionPerformed

    private void type_placeTypeTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_type_placeTypeTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_type_placeTypeTextFieldActionPerformed

    private void type_placeCreateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_type_placeCreateButtonActionPerformed
        
        simpleAdd(type_placeTypeTextField, type_placeTable, "type_of_place", "type_name",new String[]{"id_type_of_place","type_name"});
        
    }//GEN-LAST:event_type_placeCreateButtonActionPerformed

    private void placeNameTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_placeNameTextFieldActionPerformed
        
    }//GEN-LAST:event_placeNameTextFieldActionPerformed

    private void placeSeatsTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_placeSeatsTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_placeSeatsTextFieldActionPerformed

    private void placeAdressTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_placeAdressTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_placeAdressTextFieldActionPerformed

    private void placeOpendateTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_placeOpendateTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_placeOpendateTextFieldActionPerformed

    private void placeStartTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_placeStartTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_placeStartTextFieldActionPerformed

    private void placeEndTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_placeEndTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_placeEndTextFieldActionPerformed

    private void type_eventTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_type_eventTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_type_eventTextFieldActionPerformed

    private void type_eventUpdateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_type_eventUpdateButtonActionPerformed
        updateOne(type_eventTable,  "type_of_event", "id_type_of_event", new String[]{"id_type_of_event","type_name"});
    }//GEN-LAST:event_type_eventUpdateButtonActionPerformed

    private void eventDateTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eventDateTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_eventDateTextFieldActionPerformed

    private void eventVisitorsTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eventVisitorsTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_eventVisitorsTextFieldActionPerformed

    private void ownerCreateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ownerCreateButtonActionPerformed
        try{
                Connection con = getConnection();

                String status=null;
                String nameorg = ownerNameTextField.getText();
                String fio = ownerFioTextField.getText();
                String phone = ownerPhoneTextField.getText();
                String adress = ownerAdressTextField.getText();

                if (phone.length()!=0 && adress.length()!=0 &&(ownerFisRadioButton.isSelected() || ownerYurRadioButton.isSelected())){

                    if ((ownerYurRadioButton.isSelected() && fio.length()==0 && nameorg.length()!=0) || (ownerFisRadioButton.isSelected() && fio.length()!=0 && nameorg.length()==0)){

                    if (ownerFisRadioButton.isSelected()){
                        status = ownerFisRadioButton.getActionCommand();
                        nameorg=null;
                    }
                    if (ownerYurRadioButton.isSelected()){
                        status = ownerYurRadioButton.getActionCommand();
                        fio=null;
                    }
                    
                        String query = "INSERT INTO owner(status,nameorg,fio,phonenum,adress,idcity) VALUES(?,?,?,?,?,?)";
                        PreparedStatement stmt = con.prepareStatement(query);

                        stmt.setString(1, status);
                        stmt.setString(2, nameorg); 
                        stmt.setString(3, fio); 
                        stmt.setString(4, phone); 
                        stmt.setString(5, adress); 
                        stmt.setInt(6, Integer.valueOf(String.valueOf(ownerCityComboBox.getSelectedItem())));

                        stmt.executeUpdate();

                        con.close();
                        JOptionPane.showMessageDialog(new JFrame(), "Запись успешно добавлена", "Диалог", JOptionPane.INFORMATION_MESSAGE);

                        showTable(ownerTable, "owner", new String[]{"idowner","status","nameorg","fio","phonenum","adress","idcity"});
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(new JFrame(), "Для физ.лица должно быть указано ФИО, для юр.лица должно быть указано название организации", "Диалог", JOptionPane.ERROR_MESSAGE);
                    }
                }
                else
                {
                    JOptionPane.showMessageDialog(new JFrame(), "Заполните необходимые поля", "Диалог", JOptionPane.ERROR_MESSAGE);

                }

            }
            catch(Exception e){
                
                JOptionPane.showMessageDialog(new JFrame(), "Не существующие внешние ключи", "Диалог", JOptionPane.ERROR_MESSAGE);

            }
        
    }//GEN-LAST:event_ownerCreateButtonActionPerformed

    private void ownerDeleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ownerDeleteButtonActionPerformed

        deleteSelected(ownerTable, "owner", "idowner", new String[]{"idowner","status","nameorg","fio","phonenum","adress","idcity"});

    }//GEN-LAST:event_ownerDeleteButtonActionPerformed

    private void type_placeDeleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_type_placeDeleteButtonActionPerformed
    
        deleteSelected(type_placeTable, "type_of_place", "id_type_of_place",new String[]{"id_type_of_place","type_name"});
                    
    }//GEN-LAST:event_type_placeDeleteButtonActionPerformed

    private void type_eventCreateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_type_eventCreateButtonActionPerformed
  
        simpleAdd(type_eventTextField, type_eventTable, "type_of_event", "type_name",new String[]{"id_type_of_event","type_name"});
        
    }//GEN-LAST:event_type_eventCreateButtonActionPerformed

    private void type_eventDeleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_type_eventDeleteButtonActionPerformed
        
        deleteSelected(type_eventTable, "type_of_event", "id_type_of_event",new String[]{"id_type_of_event","type_name"});
        
    }//GEN-LAST:event_type_eventDeleteButtonActionPerformed

    private void placeCreateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_placeCreateButtonActionPerformed
        try{
                Connection con = getConnection();

                String name = placeNameTextField.getText();
                String adress = placeAdressTextField.getText();
                String numseats = placeSeatsTextField.getText();
               
                String opendate = placeOpendateTextField.getText();
                // String open = placeOpenTextField.getText();
                // String idowner = placeOwnerTextField.getText();
                // String idcity = placeCityTextField.getText();
                // String id_type_of_place = placeTypeTextField.getText();
                String start = placeStartTextField.getText();
                String end = placeEndTextField.getText();

                if(name.length()!=0 && adress.length()!=0 && opendate.length()!=0){

                    if(Integer.valueOf(numseats)>0){
                        String query = "INSERT INTO place(nameplace, adress, numseats, opendate, open, idowner, idcity, id_type_of_place, start, end) VALUES(?,?,?,?,?,?,?,?,?,?)";
                        PreparedStatement stmt = con.prepareStatement(query); 

                        stmt.setString(1, name);
                        stmt.setString(2, adress); 

                        if (numseats.length()==0)
                            stmt.setNull(3, java.sql.Types.INTEGER);
                        else
                            stmt.setInt(3, Integer.valueOf(numseats));

                        stmt.setDate(4, java.sql.Date.valueOf(opendate)); 

                        // Boolean bool;
                        // if (open.equals("да")) bool = true;
                        // else bool = false;
                        stmt.setBoolean(5,true); 
                        stmt.setInt(6, Integer.valueOf("1"));
                        stmt.setInt(7, Integer.valueOf("1"));
                        stmt.setInt(8, Integer.valueOf("1"));

                        if (start.length()==0 && end.length()==0){
                            stmt.setNull(9, java.sql.Types.DATE); 
                            stmt.setNull(10, java.sql.Types.DATE); 
                        }
                        else{
                            stmt.setDate(9, java.sql.Date.valueOf(start)); 
                            stmt.setDate(10, java.sql.Date.valueOf(end)); 
                        }

                        stmt.executeUpdate();

                        con.close();

                        showTable(placeTable, "place", new String[]{"idplace","nameplace", "adress", "numseats", "opendate", "open", "idowner", "idcity", "id_type_of_place", "start", "end"});
                    }
                    else
                    {
                    JOptionPane.showMessageDialog(new JFrame(), "Количество мест должно быть больше 0, если место досуга не ограничено по количеству мест, оставьте поле пустым", "Диалог", JOptionPane.ERROR_MESSAGE);

                    }
                }
                else
                {
                    JOptionPane.showMessageDialog(new JFrame(), "Заполните необходимые поля", "Диалог", JOptionPane.ERROR_MESSAGE);

                }
            }
            catch(Exception e){
                
                JOptionPane.showMessageDialog(new JFrame(), "Не существующие внешние ключи", "Диалог", JOptionPane.ERROR_MESSAGE);
                
                System.out.println("Error "+ e.getMessage());

            }
    }//GEN-LAST:event_placeCreateButtonActionPerformed

    private void placeDeleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_placeDeleteButtonActionPerformed
        deleteSelected(placeTable, "place", "idplace",new String[]{"idplace","nameplace", "adress", "numseats", "opendate", "open", "idowner", "idcity", "id_type_of_place", "start", "end"});

    }//GEN-LAST:event_placeDeleteButtonActionPerformed

    private void eventCreateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eventCreateButtonActionPerformed
        try{
                Connection con = getConnection();

                String date = eventDateTextField.getText();
                String eventname = eventNameTextField.getText();
                String numofvisitors = eventVisitorsTextField.getText();    
                // String idplace = eventPlaceTextField.getText();
                // String id_type_of_event = eventTypeTextField.getText();
                
                if(date.length()!=0 && eventname.length()!=0 && numofvisitors.length()!=0){
                    if (Integer.valueOf(numofvisitors)>0){
                        String query = "INSERT INTO event(date, eventname, numofvisitors, idplace, id_type_of_event) VALUES(?,?,?,?,?)";
                        PreparedStatement stmt = con.prepareStatement(query); 

                        stmt.setDate(1, java.sql.Date.valueOf(date));
                        stmt.setString(2, eventname); 

                        if (numofvisitors.length()==0)
                            stmt.setNull(3, java.sql.Types.INTEGER);
                        else
                            stmt.setInt(3, Integer.valueOf(numofvisitors));

                        stmt.setInt(4, Integer.valueOf("1"));
                        stmt.setInt(5, Integer.valueOf("1"));

                        stmt.executeUpdate();

                        con.close();

                        showTable(eventTable, "event", new String[]{"date","eventname", "numofvisitors", "idplace", "id_type_of_event"});
                    }
                    else {
                    JOptionPane.showMessageDialog(new JFrame(), "Количество поситивших должно быть больше 0, если количество поситивших неизвестно, оставьте поле пустым", "Диалог", JOptionPane.ERROR_MESSAGE);

                    }
                }
                else
                {
                    JOptionPane.showMessageDialog(new JFrame(), "Заполните необходимые поля", "Диалог", JOptionPane.ERROR_MESSAGE);

                }
            }
            catch(Exception e){
                JOptionPane.showMessageDialog(new JFrame(), "Несуществующие внешние ключи или повторяющийся первичный ключ", "Диалог", JOptionPane.ERROR_MESSAGE);

                System.out.println("Error "+ e.getMessage());

            }
    }//GEN-LAST:event_eventCreateButtonActionPerformed

    private void eventDeleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eventDeleteButtonActionPerformed
        try {
            Connection con = getConnection();
            Statement st= con.createStatement();
            int[] rows = eventTable.getSelectedRows();
            for (int i = 0; i < rows.length; i++){
                st.executeUpdate("DELETE FROM event WHERE date="+"'"+eventTable.getValueAt(rows[i], 0)+"'"+ " and idplace=" +eventTable.getValueAt(rows[i], 3));            
            }
            con.close();
            
            showTable(eventTable, "event", new String[]{"date","eventname", "numofvisitors", "idplace", "id_type_of_event"});

        }
        catch(Exception e)
        {
            System.out.println("Error "+ e.getMessage());
        }

    }//GEN-LAST:event_eventDeleteButtonActionPerformed

    private void cityReloadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cityReloadButtonActionPerformed
        // TODO add your handling code here:
        showTable(cityTable, "city", new String[]{"idcity","city_name"});

    }//GEN-LAST:event_cityReloadButtonActionPerformed

    private void type_placeUpdateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_type_placeUpdateButtonActionPerformed
        updateOne(type_placeTable,  "type_of_place", "id_type_of_place", new String[]{"id_type_of_place","type_name"});

    }//GEN-LAST:event_type_placeUpdateButtonActionPerformed

    private void ownerReloadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ownerReloadButtonActionPerformed
        showTable(ownerTable, "owner", new String[]{"idowner","status","nameorg","fio","phonenum","adress","idcity"});
            
    }//GEN-LAST:event_ownerReloadButtonActionPerformed

    private void type_placeReloadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_type_placeReloadButtonActionPerformed
        // TODO add your handling code here:
        showTable(type_placeTable, "type_of_place", new String[]{"id_type_of_place","type_name"});
    }//GEN-LAST:event_type_placeReloadButtonActionPerformed

    private void placeReloadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_placeReloadButtonActionPerformed
        
        showTable(placeTable, "place", new String[]{"idplace","nameplace","adress","numseats","opendate","open","idowner","idcity","id_type_of_place","start","end"});
            
    }//GEN-LAST:event_placeReloadButtonActionPerformed

    private void type_eventReloadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_type_eventReloadButtonActionPerformed
        
        showTable(type_eventTable, "type_of_event", new String[]{"id_type_of_event","type_name"});
    }//GEN-LAST:event_type_eventReloadButtonActionPerformed

    private void eventReloadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eventReloadButtonActionPerformed
        showTable(eventTable, "event", new String[]{"date","eventname","numofvisitors","idplace","id_type_of_event"});
    }//GEN-LAST:event_eventReloadButtonActionPerformed

    private void placeUpdateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_placeUpdateButtonActionPerformed
        // TODO add your handling code here:
        updateOne(placeTable,  "place", "idplace", new String[]{"idplace","nameplace","adress","numseats","opendate","open","idowner","idcity","id_type_of_place","start","end"});

    }//GEN-LAST:event_placeUpdateButtonActionPerformed

    private void eventUpdateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eventUpdateButtonActionPerformed
        // TODO add your handling code here:
        try{
        Connection con = getConnection();
        String[] column = new String[]{"date","eventname","numofvisitors","idplace","id_type_of_event"};
        int x = -1;
        int y = -1;
        x = eventTable.getEditingRow();
        y = eventTable.getEditingColumn();
        if (eventTable.isEditing()){
            eventTable.getCellEditor().stopCellEditing();
        }
        
        System.out.println(eventTable.getValueAt(x, y));
        if(y!=0 && y!=3){
            String add = String.valueOf(eventTable.getValueAt(x, y));
            String sql = "UPDATE event SET "+column[y]+" = ? WHERE date = ? and idplace = ?";

            PreparedStatement stmt = con.prepareStatement(sql);
                          
            stmt.setString( 1, add);
            stmt.setString( 2, String.valueOf(eventTable.getValueAt(x, 0)));
            stmt.setString( 3, String.valueOf(eventTable.getValueAt(x, 3)));

            stmt.executeUpdate();
            stmt.close();
        }
        else 
        {
            JOptionPane.showMessageDialog(new JFrame(), "Вы не можете поменять дату или место, так как это ключевые поля. Если хотите всё же изменить их, удалите мероприятие и создайте его заново", "Диалог", JOptionPane.ERROR_MESSAGE);

        }
            showTable(eventTable, "event", column);

        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(new JFrame(), "Добавление прервано", "Диалог", JOptionPane.ERROR_MESSAGE);

            System.out.println("Error: "+ e.getMessage());
        }
    }//GEN-LAST:event_eventUpdateButtonActionPerformed

    private void selectButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectButton1ActionPerformed
        // TODO add your handling code here:
        try {
            Connection con = getConnection();
            Statement st= con.createStatement();
            String[] columns = new String[]{"idplace","nameplace","adress","numseats","opendate","open","idowner","idcity","id_type_of_place","start","end"};
            
            // String idcity = selectCityTextField.getText();
            
            DefaultTableModel model = new DefaultTableModel(columns, 0);
            selectTable.setModel(model);
            
            ResultSet rs = st.executeQuery("SELECT * FROM place WHERE idcity=1");
                System.out.println("ok for " + "place");

                while(rs.next())
                {
                    
                    Object[] row = new Object[columns.length];
                    for (int i = 0; i < columns.length; i++){
                        row[i] = rs.getString(columns[i]);
                    }
                    model.addRow(row);
                }


                con.close();
        }
        catch(Exception e)
        {
            System.out.println("Error "+ e.getMessage());
        }
    }//GEN-LAST:event_selectButton1ActionPerformed

    
    private void pdfButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pdfButton1ActionPerformed
        createPdf(selectTable);
//                createPdf(selectTable,new float[]{70f, 150f, 210f,100f,100f,100f,100f,100f,100f,100f,100f});

            
    }//GEN-LAST:event_pdfButton1ActionPerformed

    private void cityPdfButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cityPdfButtonActionPerformed
       
        createPdf(cityTable);
//                createPdf(cityTable,new float[]{100f, 210f});


    }//GEN-LAST:event_cityPdfButtonActionPerformed

    private void ownerPdfButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ownerPdfButtonActionPerformed
        createPdf(ownerTable);
//                createPdf(ownerTable,new float[]{100f, 150f, 210f,200f,100f,200f,100f});


    }//GEN-LAST:event_ownerPdfButtonActionPerformed

    private void type_placePdfButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_type_placePdfButtonActionPerformed
        createPdf(type_placeTable);
//                createPdf(type_placeTable,new float[]{100f, 210f});


    }//GEN-LAST:event_type_placePdfButtonActionPerformed

    private void placePdfButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_placePdfButtonActionPerformed
        createPdf(placeTable);
//                createPdf(placeTable,new float[]{70f, 150f, 210f,100f,100f,100f,100f,100f,100f,100f,100f});


    }//GEN-LAST:event_placePdfButtonActionPerformed

    private void type_eventPdfButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_type_eventPdfButtonActionPerformed
        createPdf(type_eventTable);
//                createPdf(type_eventTable,new float[]{100f, 210f});


    }//GEN-LAST:event_type_eventPdfButtonActionPerformed

    private void eventPdfButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eventPdfButtonActionPerformed
         createPdf(eventTable);
//                  createPdf(eventTable,new float[]{200f, 250f, 100f,100f,100f});


    }//GEN-LAST:event_eventPdfButtonActionPerformed

    private void selectButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectButton2ActionPerformed
        // TODO add your handling code here:
        try {
            Connection con = getConnection();
            Statement st= con.createStatement();
            String[] columns = new String[]{"date","eventname","numofvisitors","idplace","id_type_of_event"};

            
            DefaultTableModel model = new DefaultTableModel(columns, 0);
            selectTable.setModel(model);
            
            ResultSet rs = st.executeQuery("SELECT * FROM event WHERE date<current_date()");

                while(rs.next())
                {
                    
                    Object[] row = new Object[columns.length];
                    for (int i = 0; i < columns.length; i++){
                        row[i] = rs.getString(columns[i]);
                    }
                    model.addRow(row);
                }


                con.close();
        }
        catch(Exception e)
        {
            System.out.println("Error "+ e.getMessage());
        }
    }//GEN-LAST:event_selectButton2ActionPerformed

    private void selectButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectButton3ActionPerformed
        // TODO add your handling code here:
        try {
            Connection con = getConnection();
            Statement st= con.createStatement();
            String[] columns = new String[]{"date","eventname","numofvisitors","idplace","id_type_of_event"};

            
            DefaultTableModel model = new DefaultTableModel(columns, 0);
            selectTable.setModel(model);
            
            ResultSet rs = st.executeQuery("SELECT * FROM event WHERE date>current_date()");

                while(rs.next())
                {
                    
                    Object[] row = new Object[columns.length];
                    for (int i = 0; i < columns.length; i++){
                        row[i] = rs.getString(columns[i]);
                    }
                    model.addRow(row);
                }


                con.close();
        }
        catch(Exception e)
        {
            System.out.println("Error "+ e.getMessage());
        }
    }//GEN-LAST:event_selectButton3ActionPerformed

    private void selectButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectButton4ActionPerformed
        // TODO add your handling code here:
        try {
            Connection con = getConnection();
            Statement st= con.createStatement();
            String[] columns = new String[]{"idplace","nameplace","adress","numseats","opendate","open","idowner","idcity","id_type_of_place","start","end"};
                        
            DefaultTableModel model = new DefaultTableModel(columns, 0);
            selectTable.setModel(model);
            
            ResultSet rs = st.executeQuery("SELECT * FROM place WHERE open=1");
                System.out.println("ok for " + "place");

                while(rs.next())
                {
                    
                    Object[] row = new Object[columns.length];
                    for (int i = 0; i < columns.length; i++){
                        row[i] = rs.getString(columns[i]);
                    }
                    model.addRow(row);
                }


                con.close();
        }
        catch(Exception e)
        {
            System.out.println("Error "+ e.getMessage());
        }
    }//GEN-LAST:event_selectButton4ActionPerformed

    private void selectButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectButton5ActionPerformed
        // TODO add your handling code here:
        try {
            Connection con = getConnection();
            Statement st= con.createStatement();
            String[] columns = new String[]{"idplace","nameplace","adress","numseats","opendate","open","idowner","idcity","id_type_of_place","start","end"};
                        
            DefaultTableModel model = new DefaultTableModel(columns, 0);
            selectTable.setModel(model);
            
            ResultSet rs = st.executeQuery("SELECT * FROM place WHERE open=0");
                System.out.println("ok for " + "place");

                while(rs.next())
                {
                    
                    Object[] row = new Object[columns.length];
                    for (int i = 0; i < columns.length; i++){
                        row[i] = rs.getString(columns[i]);
                    }
                    model.addRow(row);
                }


                con.close();
        }
        catch(Exception e)
        {
            System.out.println("Error "+ e.getMessage());
        }
    }//GEN-LAST:event_selectButton5ActionPerformed

    private void selectPlaceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectPlaceButtonActionPerformed
        // TODO add your handling code here:
        try {
            Connection con = getConnection();
            Statement st= con.createStatement();
            String[] columns = new String[]{"idplace","nameplace","adress","numseats","opendate","open","idowner","idcity","id_type_of_place","start","end"};
                        
            DefaultTableModel model = new DefaultTableModel(columns, 0);
            selectTable.setModel(model);
            // String id = selectPlaceTextField.getText();
            ResultSet rs = st.executeQuery("SELECT * FROM place WHERE id_type_of_place=1");
                System.out.println("ok for " + "place");

                while(rs.next())
                {
                    
                    Object[] row = new Object[columns.length];
                    for (int i = 0; i < columns.length; i++){
                        row[i] = rs.getString(columns[i]);
                    }
                    model.addRow(row);
                }


                con.close();
        }
        catch(Exception e)
        {
            System.out.println("Error "+ e.getMessage());
        }
    }//GEN-LAST:event_selectPlaceButtonActionPerformed

    private void selectEventButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectEventButtonActionPerformed
        // TODO add your handling code here:
        try {
            Connection con = getConnection();
            Statement st= con.createStatement();
            String[] columns = new String[]{"date","eventname","numofvisitors","idplace","id_type_of_event"};

            
            DefaultTableModel model = new DefaultTableModel(columns, 0);
            selectTable.setModel(model);
            // String id = selectEventTextField.getText();
            ResultSet rs = st.executeQuery("SELECT * FROM event WHERE id_type_of_event=1");

                while(rs.next())
                {
                    
                    Object[] row = new Object[columns.length];
                    for (int i = 0; i < columns.length; i++){
                        row[i] = rs.getString(columns[i]);
                    }
                    model.addRow(row);
                }


                con.close();
        }
        catch(Exception e)
        {
            System.out.println("Error "+ e.getMessage());
        }
    }//GEN-LAST:event_selectEventButtonActionPerformed

    private void cityNoRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cityNoRadioButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cityNoRadioButtonActionPerformed

    private void ownerFisRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ownerFisRadioButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ownerFisRadioButtonActionPerformed

    private void ownerYurRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ownerYurRadioButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ownerYurRadioButtonActionPerformed

    private void ownerCityComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ownerCityComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ownerCityComboBoxActionPerformed

    private void placeOwnerComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_placeOwnerComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_placeOwnerComboBoxActionPerformed

    private void placeCityComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_placeCityComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_placeCityComboBoxActionPerformed

    private void placeTypeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_placeTypeComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_placeTypeComboBoxActionPerformed

    private void eventPlaceComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eventPlaceComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_eventPlaceComboBoxActionPerformed

    private void eventTypeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eventTypeComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_eventTypeComboBoxActionPerformed

    private void selectCityComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectCityComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_selectCityComboBoxActionPerformed

    private void selectPlaceTypeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectPlaceTypeComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_selectPlaceTypeComboBoxActionPerformed

    private void selectEventTypeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectEventTypeComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_selectEventTypeComboBoxActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(View.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(View.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(View.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(View.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new View().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cityCreateButton;
    private javax.swing.JButton cityDeleteButton;
    private javax.swing.JTextField cityNameTextField;
    private javax.swing.JRadioButton cityNoRadioButton;
    private javax.swing.JPanel cityPanel;
    private javax.swing.JButton cityPdfButton;
    private javax.swing.JButton cityReloadButton;
    private javax.swing.JTable cityTable;
    private javax.swing.JButton cityUpdateButton;
    private javax.swing.JRadioButton cityYesRadioButton;
    private javax.swing.ButtonGroup citybuttonGroup;
    private javax.swing.JButton eventCreateButton;
    private javax.swing.JTextField eventDateTextField;
    private javax.swing.JButton eventDeleteButton;
    private javax.swing.JLabel eventDetaLabel;
    private javax.swing.JLabel eventNameLabel;
    private javax.swing.JTextField eventNameTextField;
    private javax.swing.JPanel eventPanel;
    private javax.swing.JButton eventPdfButton;
    private javax.swing.JComboBox<String> eventPlaceComboBox;
    private javax.swing.JLabel eventPlaceLabel;
    private javax.swing.JButton eventReloadButton;
    private javax.swing.JTable eventTable;
    private javax.swing.JComboBox<String> eventTypeComboBox;
    private javax.swing.JLabel eventTypeLabel;
    private javax.swing.JButton eventUpdateButton;
    private javax.swing.JLabel eventVisitorsLabel;
    private javax.swing.JTextField eventVisitorsTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JTabbedPane mainTabbedPane;
    private javax.swing.JTextField ownerAdressTextField;
    private javax.swing.JComboBox<String> ownerCityComboBox;
    private javax.swing.JButton ownerCreateButton;
    private javax.swing.JButton ownerDeleteButton;
    private javax.swing.JLabel ownerFioLabel;
    private javax.swing.JTextField ownerFioTextField;
    private javax.swing.JRadioButton ownerFisRadioButton;
    private javax.swing.JLabel ownerNameLabel;
    private javax.swing.JTextField ownerNameTextField;
    private javax.swing.JPanel ownerPanel;
    private javax.swing.JButton ownerPdfButton;
    private javax.swing.JLabel ownerPhoneLabel;
    private javax.swing.JTextField ownerPhoneTextField;
    private javax.swing.JButton ownerReloadButton;
    private javax.swing.JLabel ownerStatusLabel;
    private javax.swing.JTable ownerTable;
    private javax.swing.JButton ownerUpdateButton;
    private javax.swing.JRadioButton ownerYurRadioButton;
    private javax.swing.ButtonGroup ownerbuttonGroup;
    private javax.swing.JButton pdfButton1;
    private javax.swing.JLabel placeAdressLabel;
    private javax.swing.JTextField placeAdressTextField;
    private javax.swing.JComboBox<String> placeCityComboBox;
    private javax.swing.JLabel placeCityLabel;
    private javax.swing.JButton placeCreateButton;
    private javax.swing.JButton placeDeleteButton;
    private javax.swing.JLabel placeEndLabel;
    private javax.swing.JTextField placeEndTextField;
    private javax.swing.JLabel placeNameLabel;
    private javax.swing.JTextField placeNameTextField;
    private javax.swing.JLabel placeOpenLabel;
    private javax.swing.JLabel placeOpendateLabel;
    private javax.swing.JTextField placeOpendateTextField;
    private javax.swing.JComboBox<String> placeOwnerComboBox;
    private javax.swing.JLabel placeOwnerLabel;
    private javax.swing.JPanel placePanel;
    private javax.swing.JButton placePdfButton;
    private javax.swing.JButton placeReloadButton;
    private javax.swing.JLabel placeSeatsLabel;
    private javax.swing.JTextField placeSeatsTextField;
    private javax.swing.JLabel placeStartLabel;
    private javax.swing.JTextField placeStartTextField;
    private javax.swing.JTable placeTable;
    private javax.swing.JComboBox<String> placeTypeComboBox;
    private javax.swing.JLabel placeTypeLabel;
    private javax.swing.JButton placeUpdateButton;
    private javax.swing.JButton selectButton1;
    private javax.swing.JButton selectButton2;
    private javax.swing.JButton selectButton3;
    private javax.swing.JButton selectButton4;
    private javax.swing.JButton selectButton5;
    private javax.swing.JComboBox<String> selectCityComboBox;
    private javax.swing.JButton selectEventButton;
    private javax.swing.JComboBox<String> selectEventTypeComboBox;
    private javax.swing.JPanel selectPanel;
    private javax.swing.JButton selectPlaceButton;
    private javax.swing.JComboBox<String> selectPlaceTypeComboBox;
    private javax.swing.JTable selectTable;
    private javax.swing.JButton type_eventCreateButton;
    private javax.swing.JButton type_eventDeleteButton;
    private javax.swing.JLabel type_eventLabel;
    private javax.swing.JPanel type_eventPanel;
    private javax.swing.JButton type_eventPdfButton;
    private javax.swing.JButton type_eventReloadButton;
    private javax.swing.JTable type_eventTable;
    private javax.swing.JTextField type_eventTextField;
    private javax.swing.JButton type_eventUpdateButton;
    private javax.swing.JButton type_placeCreateButton;
    private javax.swing.JButton type_placeDeleteButton;
    private javax.swing.JPanel type_placePanel;
    private javax.swing.JButton type_placePdfButton;
    private javax.swing.JButton type_placeReloadButton;
    private javax.swing.JTable type_placeTable;
    private javax.swing.JLabel type_placeTypeLabel;
    private javax.swing.JTextField type_placeTypeTextField;
    private javax.swing.JButton type_placeUpdateButton;
    // End of variables declaration//GEN-END:variables
}
