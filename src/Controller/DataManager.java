/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Model.Record;
import Model.User;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import service.DBManager;

/**
 *
 * @author Bugy
 */
public class DataManager {

    private DBManager DbManager;
    SimpleDateFormat Formater = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    
    public DataManager(DBManager dbManager) {
        DbManager = dbManager;
    }

    public String getFunctionName(int id) {
        String result = null;
        ResultSet rs = DbManager.querySQL("SELECT"
                + "  popis"
                + " FROM"
                + " Funkcia"
                + " WHERE id_funkcie like " + id
        );
        try {
            if (rs != null) {

                rs.next();
                //Retrieve by column name
                result = rs.getString("popis");
                rs.close();

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public int getFunctionId(String name) {
        int result = -1;
        ResultSet rs = DbManager.querySQL("SELECT"
                + "  id_funkcie"
                + " FROM"
                + " Funkcia"
                + " WHERE popis like " + addApostrofs(name)
        );
        try {
            if (rs != null) {

                rs.next();
                //Retrieve by column name
                result = rs.getInt("id_funkcie");
                rs.close();

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public ArrayList<String> getStationNames() {
        ArrayList<String> result = new ArrayList<>();
        ResultSet rs = DbManager.querySQL("SELECT"
                + " nazov"
                + " FROM"
                + " Stanica"
        );
        try {
            if (rs != null) {
                while (rs.next()) {
                    result.add(rs.getString("nazov"));
                }
                rs.close();

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public ArrayList<String> getFunctionNames() {
        ArrayList<String> result = new ArrayList<>();
        ResultSet rs = DbManager.querySQL("SELECT"
                + " popis"
                + " FROM"
                + " Funkcia"
        );
        try {
            if (rs != null) {
                while (rs.next()) {
                    result.add(rs.getString("popis"));
                }
                rs.close();

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public int getStationId(String name) {
        int result = -1;
        String query = "SELECT"
                + "  id_stanice"
                + " FROM"
                + " Stanica"
                + " WHERE nazov like " + addApostrofs(name);

        ResultSet rs = DbManager.querySQL(query);
        try {
            if (rs != null) {

                rs.next();
                //Retrieve by column name
                result = rs.getInt("id_stanice");
                rs.close();

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }
    
    public String getStationName(int id) {
        String result = null;
        String query = "SELECT"
                + " nazov"
                + " FROM"
                + " Stanica"
                + " WHERE id_stanice like " + id;

        ResultSet rs = DbManager.querySQL(query);
        try {
            if (rs != null) {

                rs.next();
                //Retrieve by column name
                result = rs.getString("nazov");
                rs.close();

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public ArrayList<String> getWagonTypes() {
        ArrayList<String> result = new ArrayList<>();
        ResultSet rs = DbManager.querySQL("SELECT"
                + " nazov"
                + " FROM"
                + " Typ_vozna"
        );
        try {
            if (rs != null) {
                while (rs.next()) {
                    result.add(rs.getString("nazov"));
                }
                rs.close();

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public ArrayList<String> getCompanyNames() {
        ArrayList<String> result = new ArrayList<>();
        ResultSet rs = DbManager.querySQL("SELECT"
                + " nazov"
                + " FROM"
                + " Spolocnost"
        );
        try {
            if (rs != null) {
                while (rs.next()) {
                    result.add(rs.getString("nazov"));
                }
                rs.close();

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public ArrayList<String> getTrainIds() {
        ArrayList<String> result = new ArrayList<>();
        ResultSet rs = DbManager.querySQL("SELECT"
                + " id_vlaku,"
                + " nazov"
                + " FROM"
                + " Vlak"
                + " ORDER BY id_vlaku asc"
        );
        try {
            if (rs != null) {
                while (rs.next()) {
                    result.add(rs.getString("id_vlaku") + "-" + rs.getString("nazov"));
                }
                rs.close();

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public ArrayList<String> getTrainTypes() {
        ArrayList<String> result = new ArrayList<>();
        ResultSet rs = DbManager.querySQL("SELECT"
                + " nazov"
                + " FROM"
                + " Typ_vlaku"
        );
        try {
            if (rs != null) {
                while (rs.next()) {
                    result.add(rs.getString("nazov"));
                }
                rs.close();

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public boolean insertUser(User user) {
        boolean result = false;
        String query
                = "INSERT INTO UZIVATEL VALUES (" + addApostrofs(user.getLogin()) + ","
                + addApostrofs(user.getHash()) + ","
                + addApostrofs(user.getRc()) + ","
                + user.getIdFunction();

        byte[] foto = null;
        try {
            foto = (user.getFoto() == null) ? null : user.getFoto().getBytes(1, (int) user.getFoto().length());
        } catch (SQLException ex) {
            Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (foto == null) {
            query += "," + null + ")";
            result = DbManager.insertSql(query);
        } else {
            query += ",?)";
            result = DbManager.insertWithBlobSql(query, foto);
        }
        return result;
    }

    public void insertPerson(User user) {
        String query
                = "INSERT INTO OSOBA VALUES (" + addApostrofs(user.getRc()) + ","
                + addApostrofs(user.getName()) + ","
                + addApostrofs(user.getLastName()) + ")";

        DbManager.insertSql(query);
    }

    public void insertRecord(Record record) {
        String query
                = "INSERT INTO Zaznamy VALUES (" + null + ","
                + "TO_DATE(" + addApostrofs(Formater.format(record.getDate())) + ", 'DD.MM.YYYY HH24:MI:SS')" + ","
                        + addApostrofs(record.getDescription()) + ","
                        + addApostrofs(record.getLogIn()) + ")";

        DbManager.insertSql(query);
    }

    public boolean uniqueLogIn(String login) {
        login = addApostrofs(login);
        boolean result = false;
        ResultSet rs = DbManager.querySQL("SELECT"
                + " count(*) as count"
                + " FROM"
                + " Uzivatel"
                + " WHERE login like " + login
        );
        try {
            if (rs != null) {

                rs.next();
                //Retrieve by column name
                int count = rs.getInt("count");
                if (count == 0) {
                    result = true;
                }
                rs.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public boolean uniqueRC(String rc) {
        rc = addApostrofs(rc);
        boolean result = false;
        ResultSet rs = DbManager.querySQL("SELECT"
                + " count(*) as count"
                + " FROM"
                + " Osoba"
                + " WHERE rod_cislo like " + rc
        );
        try {
            if (rs != null) {

                rs.next();
                //Retrieve by column name
                int count = rs.getInt("count");
                if (count == 0) {
                    result = true;
                }
                rs.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    private String addApostrofs(String name) {
        return "'" + name + "'";
    }
}
