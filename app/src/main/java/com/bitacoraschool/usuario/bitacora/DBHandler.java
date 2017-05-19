package com.bitacoraschool.usuario.bitacora;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Octagono on 24/04/2017.
 */

public class DBHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "ListasEscolaresDB";

    // Tables name
    private static final String TABLE_RUTAS = "Ruta";

    // Ruta Table Columns names
    private static final String RUTA_ID = "id";
    private static final String RUTA_CUENTAID = "CuentaID";
    private static final String RUTA_LOCALIDADID = "LocalidadID";
    private static final String RUTA_CHOFERID = "ChoferID";
    private static final String RUTA_DESCRIPCION = "descripcion";
    private static final String RUTA_TANDA = "tanda";


    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_RUTA_TABLE = "CREATE TABLE " + TABLE_RUTAS + "("
        + RUTA_ID + " INTEGER PRIMARY KEY," + RUTA_CUENTAID + " TEXT,"
        + RUTA_LOCALIDADID + " TEXT," + RUTA_CHOFERID + " TEXT," + RUTA_DESCRIPCION + " TEXT,"
        + RUTA_TANDA + " TEXT)";
        db.execSQL(CREATE_RUTA_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RUTAS);
        // Creating tables again
        onCreate(db);
    }


    /* |||||||||||||||||||||||||||||||||||||||||| Operaciones DB |||||||||||||||||||||||||||||||||||||||||||||||||||||| */

    public void addRuta(rutas_model ruta) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(RUTA_ID, ruta.getId());
        values.put(RUTA_CUENTAID, ruta.getCuentaId());
        values.put(RUTA_LOCALIDADID, ruta.getLocalidad());
        values.put(RUTA_CHOFERID, ruta.getChoferId());
        values.put(RUTA_DESCRIPCION, ruta.getDescripcion());
        values.put(RUTA_TANDA, ruta.getTanda());
        db.insert(TABLE_RUTAS, null, values);
        db.close(); // Closing database connection
    }
    public rutas_model getRuta(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_RUTAS, new String[] {
                        RUTA_ID,
                        RUTA_CUENTAID,
                        RUTA_LOCALIDADID,
                        RUTA_CHOFERID,
                        RUTA_DESCRIPCION,
                        RUTA_TANDA
        }, RUTA_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        rutas_model ruta = new rutas_model(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getString(5));
        return ruta;
    }
    public List<rutas_model> getAllRutas() {
        List<rutas_model> Rutas = new ArrayList<rutas_model>();
        String selectQuery = "SELECT * FROM " + TABLE_RUTAS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                rutas_model ruta = new rutas_model();
                ruta.setId(Integer.parseInt(cursor.getString(0)));
                ruta.setCuentaId(cursor.getString(1));
                ruta.setLocalidad(cursor.getString(2));
                ruta.setChoferId(cursor.getString(3));
                ruta.setDescripcion(cursor.getString(4));
                ruta.setTanda(cursor.getString(5));
                Rutas.add(ruta);
            } while (cursor.moveToNext());
        }
        return Rutas;
    }
    public int updateRuta(rutas_model ruta) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(RUTA_ID, ruta.getId());
        values.put(RUTA_CUENTAID, ruta.getCuentaId());
        values.put(RUTA_LOCALIDADID, ruta.getLocalidad());
        values.put(RUTA_CHOFERID, ruta.getChoferId());
        values.put(RUTA_DESCRIPCION, ruta.getDescripcion());
        values.put(RUTA_TANDA, ruta.getTanda());
        return db.update(TABLE_RUTAS, values, RUTA_ID + " = ?",
                new String[]{String.valueOf(ruta.getId())});
    }

}
