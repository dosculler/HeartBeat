package com.android.deskclock3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.security.GeneralSecurityException;

public class MyFile {
	
	private static String filenameTemp;
	private FileReader fr;
	private BufferedReader br;
    
    public void creatPath(String name) {
    	File myFolderPath = new File(name);   
    	try {   
    	    if (!myFolderPath.exists()) {   
    	       myFolderPath.mkdir();   
    	    }   
    	}   
    	catch (Exception e) {   
    	    System.out.println("error create new dir");   
    	    e.printStackTrace();   
    	} 
    }


    public boolean creatTxtFile(String name) {
        boolean flag = false;
        File filename = new File(name);
        filenameTemp = name;
        if (!filename.exists()) {
            try {
				filename.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				flag = false;
			}
            flag = true;
        }
        return flag;
    }
    public void deleteFile(String name) {
        boolean flag = false;
        File filename = new File(name);
        if (filename.exists()) {
				filename.delete();
        }

    }

    public boolean writeTxtFile(String newStr) {
        boolean flag = false;
        String filein = newStr + "\r\n";
        String temp = "";

        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        FileOutputStream fos = null;
        PrintWriter pw = null;
        try {
            File file = new File(filenameTemp);
            fis = new FileInputStream(file);
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);
            StringBuffer buf = new StringBuffer();
            
            for (int j = 1; (temp = br.readLine()) != null; j++) {
                buf = buf.append(temp);
                // System.getProperty("line.separator")
                buf = buf.append(System.getProperty("line.separator"));
            }
            buf.append(filein);

            fos = new FileOutputStream(file);
            pw = new PrintWriter(fos);
            pw.write(buf.toString().toCharArray());
            pw.flush();
            flag = true;
        } catch (IOException e1) {

        } finally {
            if (pw != null) {
                pw.close();
            }
            if (fos != null) {
                try {
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            if (br != null) {
                try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            if (isr != null) {
                try {
					isr.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            if (fis != null) {
                try {
					fis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        }
        return flag;
    }
   
   
    public void AppendToFile (String fileName, String content) {
        try {
            FileWriter writer = new FileWriter(fileName, true);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    public void readData1() {
        try {
            FileReader read = new FileReader(filenameTemp);
            BufferedReader br = new BufferedReader(read);
            String row;
            while ((row = br.readLine()) != null) {
                System.out.println(row);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readDate() {
        String strs = "";
        try {
            FileReader read = new FileReader(new File(filenameTemp));
            StringBuffer sb = new StringBuffer();
            char ch[] = new char[1024];
            int d = read.read(ch);
            while (d != -1) {
                String str = new String(ch, 0, d);
                sb.append(str);
                d = read.read(ch);
            }
            System.out.print(sb.toString());
            String a = sb.toString().replaceAll("@@@@@", ",");
            strs = a.substring(0, a.length() - 1);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return strs;
    }
    
    public void openFile(String name) {
        filenameTemp = name;

		try {
			fr = new FileReader(name);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
        br = new BufferedReader(fr);   
    }
    public String readDateLine() {
    	String str2 = null;
		try {
			str2 = br.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    	return str2;
    }
    public void closeFile() {
    	  try {
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
    	  try {
			fr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    }

    
}
