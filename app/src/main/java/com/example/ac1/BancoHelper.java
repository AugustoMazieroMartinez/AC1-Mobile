package com.example.ac1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BancoHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "meubanco.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "Livros";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITULO = "Titulo";
    private static final String COLUMN_AUTOR = "Autor";
    private static final String COLUMN_LIDO = "Lido";
    private static final String COLUMN_CATEGORIA = "Categoria";

    public BancoHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_TITULO + " TEXT, "
                + COLUMN_AUTOR + " TEXT, "
                + COLUMN_LIDO + " bit, "
                + COLUMN_CATEGORIA + " TEXT)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long inserirLivro(String titulo, String autor,  boolean lido, String categoria) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITULO, titulo);
        values.put(COLUMN_AUTOR, autor);
        values.put(COLUMN_LIDO, lido);
        values.put(COLUMN_CATEGORIA, categoria);
        return db.insert(TABLE_NAME, null, values);
    }

    public Cursor listarLivros() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    public int atualizarLivro(int id, String novoTitulo, String novoAutor, String novaCategoria, boolean novoLido) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITULO, novoTitulo);
        values.put(COLUMN_AUTOR, novoAutor);
        values.put(COLUMN_CATEGORIA, novaCategoria);
        values.put(COLUMN_LIDO, novoLido);
        return db.update(TABLE_NAME, values, COLUMN_ID + "=?", new String[]{String.valueOf(id)
        });
    }

    public int excluirLivro(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
    }

}
