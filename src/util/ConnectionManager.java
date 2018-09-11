package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.swing.JOptionPane;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;

public class ConnectionManager {

    public static final String db_driver = "com.mysql.jdbc.Driver";
    public static final String db_user = "root";
    public static final String db_pwd = "";
    public static final String db_url = "jdbc:mysql://localhost/serverdb";

    private static Connection getDBConnection() {

        Connection conn = null;
        try {
            Class.forName(db_driver);
            conn = DriverManager.getConnection(db_url, db_user, db_pwd);
            System.out.println("Got Connection");
        } catch (Exception ex) {
            ex.printStackTrace();

            JOptionPane.showMessageDialog(null,
                    "Please start the mysql Service from XAMPP Console.");

        }

        return conn;
    }

    private static void closeConnection(Connection conn) {
        try {
            conn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static List isIMEIExists(String imei) {
        String query = "SELECT * FROM users where imei = '" + imei + "'";
        List list = getMapList(query);
        return list;
    }

    public static List checkUser(String loginid, String password) {
        String query = "SELECT * FROM users where loginid = '" + loginid + "' and `password`='" + password + "'";
        List list = getMapList(query);
        return list;
    }

    public static List getActivityLog() {
        String query = "SELECT `activityid`, `description`,date_format( `udate`, '%d-%b-%y %a %r') as udate FROM activitylog  ";
        List list = getMapList(query);
        return list;
    }

    public static List getEmegencyContacts() {
        String query = "SELECT * FROM emegencycontacts  ";
        List list = getMapList(query);
        return list;

    }

    public static String getEmegencyContactsPhone() {
        List list = ConnectionManager.getEmegencyContacts();
        StringBuffer sb = new StringBuffer();
        int i = 1;
        for (Iterator it = list.iterator(); it.hasNext();) {
            HashMap record = (HashMap) it.next();
            String details = StringHelper.n2s(record.get("details"));
            String phoneno = StringHelper.n2s(record.get("phoneno"));
            String address = StringHelper.n2s(record.get("address"));
            sb.append(details + "," + phoneno + "," + address + "\n");

        }
        return sb.toString();

    }

    public static void saveActivityLog(String desc) {
        String query = "insert into activitylog (description) values('" + desc + "')";
        int i = executeUpdate(query, new Object[]{});

    }

    public static void saveUsers(String login, String pass, String imei) {
        String query = "insert into users  (loginid, `password`, imei) values ('" + login + "','" + pass + "','" + imei + "')";
        int i = executeUpdate(query, new Object[]{});

    }

    public static void saveEmergency(String name, String phone, String address) {
        String query = "insert into emegencycontacts  ( details, phoneno, address) " +
                "   values ('" + name + "','" + phone + "','" + address + "')";
        int i = executeUpdate(query, new Object[]{});

    }

    public static List getMapList(String query) {
        Connection conn = null;
        List beans = null;
        try {
            conn = getDBConnection();

            QueryRunner qRunner = new QueryRunner();
            beans = (List) qRunner.query(conn, query, new MapListHandler());

        } catch (SQLException e) {
            // handle the exception
            e.printStackTrace();
        } finally {
            closeConnection(conn);
        }
        return beans;
    }

    public static List getParameterizedList(String query, Object... param) {
        Connection conn = null;
        List beans = null;
        try {
            conn = getDBConnection();

            QueryRunner qRunner = new QueryRunner();
            beans = (List) qRunner.query(conn, query, new MapListHandler(),
                    param);

        } catch (SQLException e) {
            // handle the exception
            e.printStackTrace();
        } finally {
            closeConnection(conn);
        }
        return beans;
    }

    public static int executeUpdate(String query, Object... param) {
        Connection conn = null;
        int beans = 0;
        try {
            conn = getDBConnection();
            QueryRunner qRunner = new QueryRunner();
            if (param != null) {
                beans = qRunner.update(conn, query, param);
            } else {
                beans = qRunner.update(conn, query);
            }
        } catch (SQLException e) {

            e.printStackTrace();
        } finally {
            closeConnection(conn);
        }
        return beans;
    }
}
