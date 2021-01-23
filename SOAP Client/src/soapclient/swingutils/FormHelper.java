package soapclient.swingutils;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.*;
import java.io.File;
import java.lang.reflect.Field;

public  class FormHelper {

    public static JFileChooser createFileChooser() {

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        return fileChooser;
    }

    public static void setDefaultCSS(HTMLEditorKit kit) {

        StyleSheet styleSheet = kit.getStyleSheet();
        styleSheet.addRule("body {color:#000; font-family:times; margin: 4px; }");
        styleSheet.addRule("h1 {color: blue;}");
        styleSheet.addRule("h2 {color: #ff0000;}");
        styleSheet.addRule("pre {font : 10px monaco; color : black; background-color : #fafafa; }");
    }
    @SuppressWarnings("unchecked")
    static int count = 0;
    public static Component findElementByName (Component component,String parentType, String name){

        count++;
        Component[] chields = ((Container) component).getComponents();
        for (Component element : chields) {
            Component result = null;

//            if (element.getClass().getName().replace("javax.swing.","").equalsIgnoreCase(parentType)) {
//
//                switch (parentType) {
//                    case "JMenu":
//                        result = findMenuItem((JMenu) element, name);
//                        break;
//                }
//            }

            System.out.println(count + " " +  element.getClass().getName() + " " + result);
             return findElementByName(element,parentType,name);
//            if (result != null)
//                return result;
//            else
//                return findElementByName(element,parentType,name);
        }
        return null;
    }
    private static JMenuItem findMenuItem(JMenu menu, String name) {

        if (menu.getItemCount() > 0) {
            for (int i = 0; i < menu.getItemCount(); ++i) {
                JMenuItem menuItem = menu.getItem(i);
                if (menuItem.getName().equals(name))
                    return menuItem;
            }
        }
        return null;
    }
}
