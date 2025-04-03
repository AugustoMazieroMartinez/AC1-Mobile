package com.example.ac1;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button btnSalvar;
    EditText txtTitulo;
    EditText txtAutor;
    CheckBox cbxLido;
    Spinner spnCategoria;
    ListView listLivros;
    ArrayAdapter<CharSequence> adapterSpinner;
    ArrayAdapter<String> adapterListView;
    ArrayList<String> listaLivros;
    ArrayList<Integer> listaId;
    BancoHelper bancoHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        try{

            txtTitulo = findViewById(R.id.txtTitulo);
            txtAutor = findViewById(R.id.txtAutor);
            cbxLido = findViewById(R.id.cbxLido);
            spnCategoria = findViewById(R.id.spnCategoria);
            listLivros = findViewById(R.id.listLivros);
            btnSalvar = findViewById(R.id.btnSalvar);
            bancoHelper = new BancoHelper(this);
            adapterSpinner = ArrayAdapter.createFromResource(
                    this,
                    R.array.categorias,
                    android.R.layout.simple_spinner_item
            );
            adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnCategoria.setAdapter(adapterSpinner);
            carregarLivro();
            btnSalvar.setOnClickListener(V -> {
                String titulo = txtTitulo.getText().toString();
                String autor = txtAutor.getText().toString();
                boolean lido = cbxLido.isChecked();
                String categoria = spnCategoria.getSelectedItem().toString();

                if(!titulo.isEmpty() && !autor.isEmpty()){
                    long resultado = bancoHelper.inserirLivro(titulo, autor, lido, categoria);
                    if(resultado != 1){
                        Toast.makeText(this, "Livro Salvo!", Toast.LENGTH_SHORT).show();
                        txtTitulo.setText("");
                        txtAutor.setText("");
                        cbxLido.setChecked(false);
                        spnCategoria.setSelection(0);
                        carregarLivro();
                        btnSalvar.setText("@string/Salvar");
                    } else {
                        Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
                    }
                }

            });

            listLivros.setOnItemClickListener((parent, view, position, id) -> {
                int livroId = listaId.get(position);
                String novoTitulo = listaLivros.get(position).split(" - ")[1];
                String novoAutor = listaLivros.get(position).split(" - ")[2];
                String novaCategoria = listaLivros.get(position).split(" - ")[3];
                boolean novoLido = Boolean.parseBoolean(listaLivros.get(position).split(" - ")[4]);
                Toast.makeText(this, "Livro Selecionado!", Toast.LENGTH_SHORT).show();
                int posicao = 1;

                txtTitulo.setText(novoTitulo);
                txtAutor.setText(novoAutor);
                cbxLido.setChecked(novoLido);
                for(int i = 0; i < adapterSpinner.getCount(); i++){
                    if(adapterSpinner.getItem(i).equals(novaCategoria)) {
                        posicao = i;
                        break;
                    }
                }
                spnCategoria.setSelection(posicao);
                btnSalvar.setText("@string/Atualizar");
                btnSalvar.setOnClickListener(v ->
                {

                    if (!novoTitulo.isEmpty() && !novoAutor.isEmpty()) {
                        int resultado = bancoHelper.atualizarLivro(livroId, novoTitulo, novoAutor, novaCategoria, novoLido);
                        if (resultado > 0) {
                            Toast.makeText(this, "Livro atualizado!", Toast.LENGTH_SHORT).show();
                            carregarLivro();
                            txtTitulo.setText("");
                            txtAutor.setText("");
                            cbxLido.setChecked(false);
                            spnCategoria.setSelection(0);
                            btnSalvar.setText("@string/Salvar");
                        } else {
                            Toast.makeText(this, "Erro ao atualizar!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                listLivros.setOnItemLongClickListener((adapterView, view1, pos, I) ->
                {

                    int idLivro = listaId.get(pos);
                    int resultado = bancoHelper.excluirLivro(idLivro);
                    if (resultado > 0) {
                        Toast.makeText(this, "Livro excluído!", Toast.LENGTH_SHORT).show();
                        carregarLivro();
                    }
                    return true;
                });
            });
        }
        catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
    private void carregarLivro(){
        Cursor cursor = bancoHelper.listarLivros();
        listaLivros = new ArrayList<>();
        listaId = new ArrayList<>();

        if (cursor.moveToFirst()){
            do{
                int id = cursor.getInt(0);
                String titulo = cursor.getString(1);
                String autor = cursor.getString(2);
                boolean lido = cursor.getInt(3) == 1;
                String categoria = cursor.getString(4);
                listaLivros.add(id + " - " + titulo + " - " + autor + " - " + (lido ? "Lido" : "Não lido") + " - " + categoria);
                listaId.add(id);
            } while(cursor.moveToNext());
        }

        adapterListView = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listaLivros);
        listLivros.setAdapter(adapterListView);
    }
}