/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.HashMap;

/**
 *
 * @author Administrator
 */
public class FileHelper {

    public static void serializeObject(HashMap props) {
        try {
            ObjectOutputStream ois = new ObjectOutputStream(new FileOutputStream("configure"));
            ois.writeObject(props);
            ois.flush();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static HashMap deserializeObject() {
        HashMap hmp = new HashMap();
        try {
            File f = new File("configure");
            if (f.exists()) {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream("configure"));
                Object obj = ois.readObject();
                if (obj instanceof HashMap) {
                    hmp = (HashMap) obj;
                }
                ois.close();
            } else {
                hmp = null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return hmp;
    }

    public static void setEnteredValues(Class c, HashMap hmp) {
        try {
            System.out.println("hmp "+hmp);
            Field[] arr = c.getFields();
            for (int i = 0; arr != null && i < arr.length; i++) {
                Field field = arr[i];
                System.out.println(field.getName());
                String value = StringHelper.n2s(hmp.get(field.getName()));

                if (value.length() > 0&&field.toString().indexOf("final")==-1) {
                  
                    if (field.getType() == String.class) {
                        field.set(null, value);
                    } else if (field.getType() == boolean.class) {
                        field.setBoolean(null, StringHelper.n2b(value));
                    } else if (field.getType() == int.class) {
                        field.setInt(null, StringHelper.n2i(value));
                    }
                }  

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static HashMap getClassFields(Class c) {
        HashMap hmp = new HashMap();
        try {

            Field[] arr = c.getFields();
            for (int i = 0; arr != null && i < arr.length; i++) {
                Field field = arr[i];
                String value = field.getName();
                Object o = field.get(null);
                hmp.put(field.getName(), o.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hmp;
    }
}
