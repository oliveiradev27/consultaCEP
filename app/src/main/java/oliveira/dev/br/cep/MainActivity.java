package oliveira.dev.br.cep;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Policy;
import java.util.Scanner;

import static android.app.ProgressDialog.STYLE_HORIZONTAL;
import static android.app.ProgressDialog.STYLE_SPINNER;

public class MainActivity extends AppCompatActivity {
    public ProgressDialog dialog;
    private EditText etCep;
    private Button btnPesquisar;
    private TextView tvLogradouro,
                     tvCep,
                     tvCidade,
                     tvIbge,
                     tvBairro,
                     tvUf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().build());
        //consultar();

        etCep = (EditText) findViewById(R.id.etCep);
        btnPesquisar = (Button) findViewById(R.id.btnPesquisar);
        tvCep = (TextView) findViewById(R.id.tvCep);
        tvLogradouro = (TextView) findViewById(R.id.tvLogradouro);
        tvCidade = (TextView) findViewById(R.id.tvCidade);
        tvIbge = (TextView) findViewById(R.id.tvIbge);
        tvBairro = (TextView) findViewById(R.id.tvBairro);
        tvUf = (TextView) findViewById(R.id.tvUf);
    }

    public void getView (View view)
    {
        if(view.getId() == btnPesquisar.getId())
        {
            String cep = etCep.getText().toString();
            if(!cep.isEmpty()){
                ConsultaAsyncTask consulta = new ConsultaAsyncTask();
                consulta.execute(cep);
            }
        }
    }
    public void consultar(){
        try {
            URL location = new URL("https://viacep.com.br/ws/01001000/json/");
            Scanner scanner = new Scanner(location.openStream());
            StringBuilder builder = new StringBuilder();
            HttpURLConnection connection = (HttpURLConnection) location.openConnection();
            if(connection.getResponseCode() == 200) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(),"utf-8"));
                String line;
                while((line = bufferedReader.readLine()) != null)
                    builder.append(line);
                String dados = builder.toString();

                /*while (scanner.hasNextLine())
                    builder.append(scanner.nextLine());
                String dados = builder.toString();*/
                Toast.makeText(this, dados, Toast.LENGTH_LONG).show();
                connection.disconnect();
            }
        }catch (MalformedURLException e){
            Toast.makeText(this, "Erro na formação da URL", Toast.LENGTH_SHORT).show();
            //Log.e("Erro", "");
        } catch (IOException e){
            Toast.makeText(this, "Erro ao conectar com URL", Toast.LENGTH_SHORT).show();
        }

    }
    private class ConsultaAsyncTask extends AsyncTask<String, Integer, String>
    {
        @Override
        protected void onPreExecute(){
            /*dialog = new ProgressDialog(MainActivity.this);
            dialog.setTitle("Aguardando resposta do servidor.");
            dialog.setMessage("Carregando...");
            dialog.setIndeterminate(false);
            dialog.setCancelable(false);
            dialog.setProgressStyle(STYLE_SPINNER);
            dialog.show();*/

            dialog = ProgressDialog.show(MainActivity.this, "Aguarde por favor", "Carregando...", false, true);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }

        @Override
        protected String doInBackground(String... strings) {
            String dados = "";
            try {
                URL location = new URL("https://viacep.com.br/ws/"+strings[0]+"/json/");
                Scanner scanner = new Scanner(location.openStream());
                StringBuilder builder = new StringBuilder();
                HttpURLConnection connection = (HttpURLConnection) location.openConnection();
                if(connection.getResponseCode() == 200) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(),"utf-8"));
                    String line;
                    while((line = bufferedReader.readLine()) != null)
                        builder.append(line);
                    dados = builder.toString();
                    Log.i("INFORMACOES", dados);
                }
            }catch (MalformedURLException e){
                Toast.makeText(MainActivity.this, "Erro na formação da URL", Toast.LENGTH_SHORT).show();
                //Log.e("Erro", "");
            } catch (IOException e){
                Toast.makeText(MainActivity.this, "Erro ao conectar com URL", Toast.LENGTH_SHORT).show();
            }

            return dados;
        }

        @Override
        protected void onPostExecute(String string)
        {
            try {
                Toast.makeText(MainActivity.this, string, Toast.LENGTH_LONG).show();
                JSONObject job = new JSONObject(string);
                tvLogradouro.setText("Logradouro: "+job.getString("logradouro"));
                tvCidade.setText("Cidade: "+job.getString("localidade"));
                tvIbge.setText("CÓD IBGE: "+job.getString("ibge"));
                tvCep.setText("CEP: "+job.getString("cep"));
                tvBairro.setText("Bairro: "+job.getString("bairro"));
                tvUf.setText("UF: "+job.getString("uf"));
                dialog.dismiss();

            } catch (JSONException e) {
                e.printStackTrace();
                Log.i("Erro de JSON", e.getMessage());
            }
        }
    }

}