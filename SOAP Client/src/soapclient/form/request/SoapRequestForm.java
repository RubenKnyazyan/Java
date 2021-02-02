package soapclient.form.request;

import soapclient.form.main.MainForm;
import swing_helper.syntax.xml.XmlTextPane;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SoapRequestForm extends JFrame {

    public SoapRequestForm(String name, String request) {

        super(name);


        setSize(600,700);

        XmlTextPane xmlTextPane = new XmlTextPane();
        xmlTextPane.setText(request);
        JScrollPane scrollPane = new JScrollPane(xmlTextPane);


        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Запрос");
        JMenuItem importItem = new JMenuItem("Импортировать");
        importItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                String [] params = name.split(" ");
                if (params.length > 1) {

                    MainForm.importMethod(params[0]);
                    MainForm.ImportRequest(xmlTextPane.getText());
                }
            }
        });

        menu.add(importItem);
        menuBar.add(menu);

        this.setJMenuBar(menuBar);
        this.add(scrollPane);

        setResizable(false);

        setVisible(true);
    }
}
