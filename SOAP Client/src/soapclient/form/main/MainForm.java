package soapclient.form.main;

import soapclient.host.HostList;
import soapclient.parser.SchemeField;
import soapclient.parser.SchemeParser;
import soapclient.project.SaveProject;
import soapclient.soap.Auth;
import soapclient.soap.SOAPRequest;
import soapclient.soap.SoapApacheHttpClient;
import soapclient.swingutils.FormHelper;
import soapclient.syntac.xml.XmlTextPane;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class MainForm extends JFrame {

    private int FIELD_PANEL_WIDTH = 600;
    private JFileChooser fileChooser;
    private File inputFile;
    private SchemeParser parser;
    private Auth autorization;
    private SoapApacheHttpClient soapClientHTTP;
    private JPanel mainGUIPanel;
    private JPanel schemePanel;
    private XmlTextPane xmlTextPane;
    private SOAPRequest request;
    private String method;
    private HostList hostList;
    private JMenu status;
    private SaveProject saveProject;

    public MainForm() {
        super("SOAP Client");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        //во весь экран устройства
//        setSize(Toolkit.getDefaultToolkit().getScreenSize());
        setSize(1300,600);
        //класс авторизации
        this.autorization = new Auth();
        //класс для выполнения запросов
        this.soapClientHTTP = new SoapApacheHttpClient();
        //класс парса схемы
        this.parser = new SchemeParser();
        //класс сохранения запросов
        this.saveProject = new SaveProject();
        //класс для генерации сообщений
        this.request = new SOAPRequest();
        //вычитываем хосты из файла
        this.hostList= new HostList();
        //file opener inicialization
        this.fileChooser = FormHelper.createFileChooser();
        //создаем еще panel - разобьем на 2 части
        this.mainGUIPanel = new JPanel(new GridLayout(1,2,10,10));
        //будем использовать абсолютное позиционирование - временное решение
        this.schemePanel = new JPanel();
        //не будем задавать layout
        this.schemePanel.setLayout(null);
        //панель с методами и полями
        this.mainGUIPanel.add(schemePanel);
        //request / response view
        this.mainGUIPanel.add(createResponseWindow());
        //создаем меню
        createMenu();
        //устанавливаем главный panel
        getContentPane().add(this.mainGUIPanel);
        //запрещаем менять размер
        setResizable(false);
        //задаем видимость
        setVisible(true);
    }

    private void createMenu() {

        JMenuBar menuBar = new JMenuBar();
        //добавляем элементы меню - верхний уровень
        menuBar.add(createProjectMenu());
        menuBar.add(createSchemeMenu());
        menuBar.add(createAuthMenu());
        //добавим меню на фрейм
        this.setJMenuBar(menuBar);
    }
    
    private JMenu createSchemeMenu() {

        JMenu myMenu = new JMenu("Схема WSDL");
        //создаем подпункт
        JMenuItem itemScheme = new JMenuItem("Указать файл");
        itemScheme.setName("scheme");
        //вешаем слушателя действия
        itemScheme.addActionListener(new MainFormAction(itemScheme));
        //создаем подпункт
        JMenuItem itemSchemeURL = new JMenuItem("Указать адрес схемы");
        itemSchemeURL.setName("scheme_url");
        //вешаем слушателя действия
        itemSchemeURL.addActionListener(new MainFormAction(itemSchemeURL));
        //добавляем к меню верхнего уровня
        myMenu.add(itemScheme);
        myMenu.add(itemSchemeURL);
        //возвращаем
        return myMenu;
    }
    private JMenu createAuthMenu() {

        JMenu myMenu = new JMenu("Отправка запроса");
        //создаем подпункт
        JMenuItem itemAuth = new JMenuItem("Ввести логин и пароль");
        itemAuth.setName("auth");
        JMenuItem itemGenerateMessage = new JMenuItem("Сгенерировать сообщение");
        itemGenerateMessage.setName("request");

        JMenuItem itemSendMessage = new JMenuItem("Отправить запрос");
        itemSendMessage.setName("send");

        itemAuth.addActionListener(new MainFormAction(itemAuth));
        itemGenerateMessage.addActionListener(new MainFormAction(itemGenerateMessage));
        itemSendMessage.addActionListener(new MainFormAction(itemSendMessage));
        myMenu.add(itemAuth);
        myMenu.add(itemGenerateMessage);
        myMenu.add(itemSendMessage);
        //возвращаем
        return myMenu;
    }

    private JMenu createProjectMenu() {

        JMenu myMenu = new JMenu("Проект");

        JMenuItem itemSaveRequest = new JMenuItem("Сохранить запрос");
        itemSaveRequest.setName("save_request");
        itemSaveRequest.addActionListener(new MainFormAction(itemSaveRequest));

        myMenu.add(itemSaveRequest);

        return myMenu;
    }
    private JPanel setMethods() {

        JPanel method = new JPanel(new GridLayout(2,2,10,10));
        JLabel label_methods = new JLabel("Метод");

//        JPanel host = new JPanel(new GridLayout(1,2,10,10));
        JLabel label_host = new JLabel("Хост");

        JComboBox list_methods = new JComboBox(this.parser.getMethods().toArray());
        list_methods.setName("methods");
       if (!list_methods.getItemAt(0).toString().equals("") && list_methods.getItemAt(0) != null) {

           this.method = list_methods.getItemAt(0).toString();
       }

        this.method = list_methods.getSelectedItem().toString();
        list_methods.addActionListener(new MainFormAction(list_methods));

        JComboBox list_hosts = new JComboBox(this.hostList.getHostNameList().toArray());
        list_hosts.setName("host");

        if (list_hosts.getItemAt(0) != null && !list_hosts.getItemAt(0).toString().equals("") ) {

            if (this.autorization != null) {
                this.autorization.setHost(this.hostList.getHostValue(list_hosts.getItemAt(0).toString()));
            }
        }

        list_hosts.addActionListener(new MainFormAction(list_hosts));

        method.add(label_host);
        method.add(list_hosts);

        method.add(label_methods);
        method.add(list_methods);

        return method;
    }

    private void redrawPanel (JPanel panel) {

        panel.validate();
        panel.repaint();
    }
    private void setFieldsPanel() {

        ArrayList<SchemeField> fieldList = this.parser.getFields();

        JPanel panelFields = new JPanel(new GridLayout(fieldList.size(),2,10,10));
        JScrollPane scrollPane = new JScrollPane(panelFields);
        //задаем padding, чтобы внутренние элементы были не в плотную
        panelFields.setBorder(new EmptyBorder(10, 10, 10, 10));
        for (int index=0; index < fieldList.size(); index++) {

            SchemeField field = fieldList.get(index);

            //добавляем метку с именем поля
            JLabel fieldName = (field.isKey()) ? new JLabel("Key : " + field.getName()) : new JLabel(field.getName());
            panelFields.add(fieldName);
            // в зависимости от типа поля будем добавлять разный Input
            switch (field.getType()) {

                case "BooleanType":
                    JCheckBox checkBox = new JCheckBox();
                    panelFields.add(checkBox);
                    //добавляем к объекту поля input, чтобы потом получать значение
                    field.setInputField(checkBox);
                    break;
                case "DurationType":
                    //проверить как записывается значение
                    JSpinner timeSpinnerDuration = new JSpinner(new SpinnerDateModel());
                    JSpinner.DateEditor timeEditorDuration = new JSpinner.DateEditor(timeSpinnerDuration, "HH:mm:ss");
                    timeSpinnerDuration.setEditor(timeEditorDuration);
                    timeSpinnerDuration.setValue(new Date());
                    //добавляем в общий блок полей
                    panelFields.add(timeSpinnerDuration);
                    //добавляем к объекту поля input, чтобы потом получать значение
                    field.setInputField(timeSpinnerDuration);
                    break;
                case "DateTimeType":
                    JSpinner timeSpinner = new JSpinner(new SpinnerDateModel());
                    JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "dd.MM.yyyy HH:mm:ss");
                    timeSpinner.setEditor(timeEditor);
                    timeSpinner.setValue(new Date());
                    //добавляем в общий блок полей
                    panelFields.add(timeSpinner);
                    //добавляем к объекту поля input, чтобы потом получать значение
                    field.setInputField(timeSpinner);
                    break;
                case "DecimalType":
                    JTextField numberField = new JTextField();
                    panelFields.add(numberField);
                    //добавляем к объекту поля input, чтобы потом получать значение
                    field.setInputField(numberField);
                    break;
                case "StringType":
                    JTextField inputField = new JTextField();
                    panelFields.add(inputField);
                    //добавляем к объекту поля input, чтобы потом получать значение
                    field.setInputField(inputField);
                    break;
            }
        }
        this.request.setFieldList(fieldList);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBounds(20,80,this.FIELD_PANEL_WIDTH,460);
        this.schemePanel.add(scrollPane);
        JPanel methods = this.setMethods();
        methods.setBounds(20,10, this.FIELD_PANEL_WIDTH,60);
        this.schemePanel.add(methods);
    }
    private JScrollPane createResponseWindow() {

        // create jeditorpane
        this.xmlTextPane = new XmlTextPane();
        // make it read-only
        this.xmlTextPane.setEditable(false);
        // create a scrollpane; modify its attributes as desired
        JScrollPane scrollPane = new JScrollPane(xmlTextPane);
        // add some styles to the html - for test
        // add an html editor kit
        HTMLEditorKit kit = new HTMLEditorKit();

        FormHelper.setDefaultCSS(kit);
//        this.xmlTextPane.setEditorKit(kit);

        // create some simple html as a string
        String htmlString = "<html>\n"
                + "<body>\n"
                + "<h1>Здравствуйте!</h1>\n"
                + "<h2>Здесь будут сообщения</h2>\n"
                + "<p>Данное окно предназначено для отображения сгенерированного сообщения-запроса и ответа на него</p>\n"
                + "<p>Для этого требуется вначале разрузить WSDL схему (выберите пункт в меню \"Схема WSDL\")</p>\n"
                + "<p>После требуется в меню \"Сгенерировать сообщение\" выбрать вначале сгенерировать сообщение, а после отправить запрос</p>\n"
                + "<p>Указать логин, пароль, хост можно в любой момент, но до отправки запроса</p>\n"
                + "</body>\n";

        xmlTextPane.setText(htmlString);

        xmlTextPane.setBounds(20,40,this.FIELD_PANEL_WIDTH,500);
        return scrollPane;
    }
    private void showAuthWindow() {

        JTextField username = new JTextField();
        JTextField password = new JPasswordField();
        Object[] message = {
                "Логин :", username,
                "Пароль :", password
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Авторизация", JOptionPane.OK_CANCEL_OPTION,JOptionPane.INFORMATION_MESSAGE);
        if (option == JOptionPane.OK_OPTION) {

            this.autorization.setLogin(username.getText());
            this.autorization.setPassword(password.getText());
            this.soapClientHTTP.setAutorization(this.autorization);

        } else {
            System.out.println("Login canceled");
        }
    }
    private void saveRequest() {

        try {

            FileNameExtensionFilter filter = new FileNameExtensionFilter("*.xml","*.xml");
            fileChooser.setFileFilter(filter);

            if ( fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION ) {

                File saveFile = fileChooser.getSelectedFile();
                if (this.parser != null) {
                    if (this.parser.getFields().size() > 0) {

                        if (this.saveProject.initSave(this.parser.getFields(),this.parser.getActions(),saveFile.getPath()+".xml")) {
                            
                            if (this.saveProject.run()) {
                                JOptionPane.showMessageDialog(null, "Запрос сохранен", "Сохранение", JOptionPane.INFORMATION_MESSAGE);
                            } else
                                throw new Exception("Ошибка при сохранении запроса");
                        }
                    }
                    else
                        throw new Exception("Ошибка при сохранении. Не удалось найти поля запроса");
                }
                else
                    throw new Exception("Ошибка при сохранении запроса. Скорее всего вы не указали схему WSDL, по которой строится и сохраняется запрос");
            }
        }
        catch(Exception ex) {

            if (!ex.getMessage().equals("")) {

                JOptionPane.showMessageDialog(null,  ex.getMessage(),"Упс", JOptionPane.ERROR_MESSAGE);
//                System.out.println(ex.getMessage());
            }
        }
    }
    private void showURLDialog() {

        JTextField schemeURL = new JTextField();
        Object[] message = {
                "Адрес схемы :", schemeURL
        };
        int option = JOptionPane.showConfirmDialog(null, message, "Ссылка на схему", JOptionPane.OK_CANCEL_OPTION,JOptionPane.INFORMATION_MESSAGE);
        if (option == JOptionPane.OK_OPTION) {

            try {
                String url = (schemeURL.getText().indexOf("http://") > -1) ? schemeURL.getText() : "http://" + schemeURL.getText();
                if (this.parser.initParser(url)) {

                    if (this.parser.startParse()) {
                        //clear main form GUI panel
                        this.schemePanel.removeAll();
                        //cоздает список полей
                        this.setFieldsPanel();
                        //redraw GUI main Panel
                        this.redrawPanel(this.schemePanel);
                    } else {
                       throw new Exception("Ошибка при парсе - проверьте схему");
                    }
                }
                else
                    throw new Exception("Ошибка при получении схемы - проверьте адрес" );
            }
            catch (Exception ex) {
                if (!ex.getMessage().equals("")) {

                    JOptionPane.showMessageDialog(null,  ex.getMessage(),"Упс", JOptionPane.ERROR_MESSAGE);
//                System.out.println(ex.getMessage());
                }
            }

        } else {
            System.out.println("Scheme address canceled");
        }
    }
    private void showFileChooser() {

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {

            try {
                inputFile = fileChooser.getSelectedFile();
                if (this.parser.initParser(inputFile) ) {
                    if (this.parser.startParse()) {
                        //clear main form GUI panel
                        this.schemePanel.removeAll();
                        //cоздает список полей
                        this.setFieldsPanel();
                        //redraw GUI main Panel
                        this.redrawPanel(this.schemePanel);
                    }
                    else
                        throw new Exception("Ошибка при разборе файла - не удалось распарсить");
                }
                else
                    throw new Exception("Ошибка при получении чтении файла");
            }
            catch (Exception ex) {
                if (!ex.getMessage().equals("")) {

                    JOptionPane.showMessageDialog(null,  ex.getMessage(),"Упс", JOptionPane.ERROR_MESSAGE);
//                System.out.println(ex.getMessage());
                }
            }
        }
    }

    private void selectedMethod(JComboBox item) {

        this.method = item.getSelectedItem().toString();
    }
    private void selectedHost(JComboBox item) {

        if (this.autorization != null) {
            this.autorization.setHost(this.hostList.getHostValue(item.getSelectedItem().toString()));
        }
        else
            JOptionPane.showMessageDialog(null,  "Необходимо вначале указать логин и пароль","Упс", JOptionPane.ERROR_MESSAGE);
    }
    private void createMessage() {

        try {
            if (this.parser.getFields().size() > 0) {
                if (this.method != null && !this.method.equals("")) {
                    // create jeditorpane
                    this.xmlTextPane = new XmlTextPane();
                    this.xmlTextPane.setEditable(true);
                    String message = this.request.generateMessage(this.method);
                    if (message != null && !message.equals("")) {

                        this.xmlTextPane.setText(message);
                        this.redrawPanel(this.mainGUIPanel);
                    } else
                        throw new Exception("Не удалось сгенерировать сообщение - попробуйте еще раз");
                } else
                    throw new Exception("Не выбран метод для оправки запроса");
            }
            else
                throw new Exception("Нет полей для генерации сообщения");
        }
        catch (Exception ex) {

            if (!ex.getMessage().equals("")) {

                JOptionPane.showMessageDialog(null,  ex.getMessage(),"Упс", JOptionPane.ERROR_MESSAGE);
//                System.out.println(ex.getMessage());
            }
        }
    }

    private void sendRequest() {

        try {
           this.xmlTextPane.setText( String.valueOf((this.soapClientHTTP.sendPOST(this.xmlTextPane.getText(), this.parser.getActionByMethod(this.method)))));
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    /**
     * для обработки нажатий пунктов меню
     */
    public class MainFormAction implements ActionListener {

        private JMenuItem menuItem;
        private JComboBox jComboBox;

        public MainFormAction(JMenuItem item) {

            this.menuItem = item;
        }
        public MainFormAction(JComboBox item) {

            this.jComboBox = item;
        }
        @Override
        public void actionPerformed(ActionEvent actionEvent) {

            if (this.menuItem != null) {

                switch(this.menuItem.getName().toLowerCase()) {

                    case "scheme":
                        showFileChooser();
                        break;
                    case "scheme_url":
                        showURLDialog();
                        break;
                    case "auth":
                        showAuthWindow();
                        break;
                    case "request":
                        createMessage();
                        break;
                    case "send":
                        sendRequest();
                        break;
                    case "save_request":
                        saveRequest();
                        break;
                }
            }

            if (this.jComboBox != null) {

                switch (this.jComboBox.getName()) {

                    case "methods":
                        selectedMethod(this.jComboBox);
                        break;
                    case "host":
                        selectedHost(this.jComboBox);
                        break;
                }
            }
        }
    }

}
