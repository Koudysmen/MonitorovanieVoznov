/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Model.WagonInTrain;
import Model.WagonOnStation;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import service.DBManager;

/**
 *
 * @author Bugy
 */
public class Reports {

    private DBManager DbManager;
    SimpleDateFormat Formater = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    public Reports(DBManager dbManager) {
        DbManager = dbManager;
    }

    public List<WagonOnStation> getWagonsOnStation(int idStation, String wagonType, String inService, Date dateFrom, Date datesTo, String company) {
        String datFrom = Formater.format(dateFrom);
        String datTO = Formater.format(datesTo);

        if (!isNullOrEmpty(wagonType)) {
            wagonType = " AND Typ_vozna.nazov like" + addApostrofs(wagonType);
        }
        if (!isNullOrEmpty(inService)) {
            inService = " AND v_prevadzke like" + addApostrofs(inService);
        }
        if (!isNullOrEmpty(company)) {
            company = " AND Spolocnost.nazov like" + addApostrofs(company);
        }
        List<WagonOnStation> result = new ArrayList<>();
        ResultSet rs = DbManager.querySQL("SELECT"
                + " id_vozna,"
                + " Spolocnost.nazov as spolocnostNazov,"
                + " Typ_vozna.nazov as typ_vozna_nazov,"
                + " v_prevadzke"
                + " FROM Typ_vozna"
                + " join Vozen using(id_typu)"
                + " join Vozen_spolocnost using(id_vozna)"
                + " join Spolocnost using(id_spolocnosti)"
                + " join Snimanie using(id_vozna)"
                + " join Snimac using(id_snimacu)"
                + " join Kolajovy_usek using(id_snimacu)"
                + " where id_stanice like " + idStation
                + " AND TO_DATE (TO_CHAR (cas_od, 'DD.MM.YYYY HH24:MI:SS'), 'DD.MM.YYYY HH24:MI:SS') BETWEEN to_date('" + datFrom + "','DD.MM.YYYY HH24:MI:SS') AND to_date('" + datTO + "','DD.MM.YYYY HH24:MI:SS')"
                + wagonType + inService + company
        );

        try {
            if (rs != null) {
                while (rs.next()) {
                    String idWagon = rs.getString("id_vozna");
                    String spolocnostNazov = rs.getString("spolocnostNazov");   
                    String inServiceDb = rs.getString("v_prevadzke");
                    String wagonTypeDb = rs.getString("typ_vozna_nazov");
                    WagonOnStation wagonOnStation = new WagonOnStation(idWagon, inServiceDb, wagonTypeDb, spolocnostNazov);
                    result.add(wagonOnStation);
                }
                rs.close();

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public List<WagonInTrain> getWagonsInTrain(int idTrain, String trainType, String wagonType, Date date, String company) {
        String dateString = addApostrofs(Formater.format(date));
        if (!isNullOrEmpty(wagonType)) {
            wagonType = " AND Typ_vozna.nazov like" + addApostrofs(wagonType);
        }
        if (!isNullOrEmpty(trainType)) {
            trainType = " AND Typ_vlaku.nazov like" + addApostrofs(trainType);
        }
        if (!isNullOrEmpty(company)) {
            company = " AND Spolocnost.nazov like" + addApostrofs(company);
        }
        List<WagonInTrain> result = new ArrayList<>();
        ResultSet rs = DbManager.querySQL("SELECT"
                + " id_vlaku,"
                + " Vlak.nazov as vlak_nazov,"
                + " id_vozna,"
                + " Spolocnost.nazov as spolocnostNazov,"
                + " Typ_vlaku.nazov as typ_vlaku_nazov,"
                + " Typ_vozna.nazov as typ_vozna_nazov,"
                + " Sprava_voznov.datum_od as datumOd,"
                + " Sprava_voznov.datum_do as datumDo"
                + " FROM typ_vozna"
                + " join Vozen using(id_typu)"
                + " join Vozen_spolocnost using(id_vozna)"
                + " join Spolocnost using(id_spolocnosti)"
                + " join Sprava_voznov using(id_vozna)"
                + " join Vlak using(id_vlaku)"
                + " join Typ_vlaku using(id_typ)"
                + " where id_vlaku like " + idTrain
                + " and Sprava_voznov.datum_od <= to_date(" + dateString + ",'DD.MM.YYYY HH24:MI:SS')"
                + " and (Sprava_voznov.datum_do >= to_date(" + dateString + ",'DD.MM.YYYY HH24:MI:SS') or Sprava_voznov.datum_do is null)"
                + wagonType + trainType + company
        );

        try {
            if (rs != null) {
                while (rs.next()) {
                    String idWagon = rs.getString("id_vozna");
                    String idTrainDb = rs.getString("id_vlaku"); 
                    String trainName = rs.getString("vlak_nazov");
                    String spolocnostNazov = rs.getString("spolocnostNazov");                  
                    String wagonTypeDb = rs.getString("typ_vozna_nazov");
                    String trainTypeDb = rs.getString("typ_vlaku_nazov");
                    Date dateFrom = rs.getDate("datumOd");
                    Date dateTo = rs.getDate("datumDo");
                    WagonInTrain wagonInTrain = new WagonInTrain(idWagon, idTrainDb, trainName, trainTypeDb, dateFrom, dateTo, spolocnostNazov, wagonTypeDb);
                    result.add(wagonInTrain);
                }
                rs.close();

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public boolean isNullOrEmpty(String term) {
        return term == null || term.equals("");
    }

    private String addApostrofs(String name) {
        return "'" + name + "'";
    }
}