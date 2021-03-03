/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.sbrf.sm.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;


/**
 *
 * @author petrunin1-aa
 */
public class SaveFile {
	
	public static boolean doSave (String fileName, String text){
		//!!!Запись в файл
			boolean isOkay = false;
			File file = new File(fileName);
			
			try {
				//Проверяем, если файл не существует, то создаем его
				if(!file.exists())
					file.createNewFile();
				//PrintWriter обеспечивает возможности записи в файл
				//PrintWriter out = new PrintWriter (file.getAbsoluteFile());
                                OutputStreamWriter out = new OutputStreamWriter (new FileOutputStream(file), StandardCharsets.UTF_8);
				try{
					//Записываем тект в файл
                                        out.write(text);
					//out.print(text);
				}
				finally{
					//Закрываем файл, иначе не запишется
					//isOkay = !out.checkError();
					out.close();
                                        isOkay = true;
				}
				
			} catch (IOException e){
				throw new RuntimeException (e);
			}
			return isOkay;
	}

}

