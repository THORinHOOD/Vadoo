package catwithbowtie.chatchat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import catwithbowtie.chatchat.Fragments.Items.Msg;
import okhttp3.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;

public class LoginActivity extends Activity {
    private EditText edtTextName;
    private EditText edtTextPassword;

    private final OkHttpClient client = new OkHttpClient();
    public static final String ip = "http://92.63.105.60";
    public static final int port = 8086;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtTextName = (EditText) findViewById(R.id.editText_name_login);
        edtTextPassword = (EditText) findViewById(R.id.editText_password_login);
        final Activity act = this;

        Button btnLogin = (Button) findViewById(R.id.button_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    login(act, edtTextName.getText().toString(), edtTextPassword.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Button btnRegistration = (Button) findViewById(R.id.button_registration);
        btnRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    register(act, edtTextName.getText().toString(), edtTextPassword.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void login(final Activity act, final String login, final String password) throws Exception {
        Request request = new Request.Builder()
                .url(ip +":" + port + "/authorization?" +"name=" + login + "&login=" + login + "&password=" + password)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(act, "Неполучилось соединиться, попробуйте снова", Toast.LENGTH_SHORT).show();
                    }
                });
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                try {
                    final String res = response.body().string();
                    if (checkOnSuccess(act, res)) {
                        succesEnter(act, login, password);
                    } else {


                        act.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Toast.makeText(act, getError(res), Toast.LENGTH_SHORT).show();
                                } catch(Exception e) {

                                }
                                    //Toast.makeText(act, "Неверный логин или пароль, попробуйте снова", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private String getError(String response) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject obj = (JSONObject) parser.parse(response);
        String error = (String) obj.get("error");
        return error;
    }

    public void register(final Activity act, final String login, final String password) throws Exception {
        Request request = new Request.Builder()
                .url(ip + ":" + port + "/registration?" +"name=" + login + "&login=" + login + "&password=" + password)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                act.runOnUiThread(new Runnable() {
                                      @Override
                                      public void run() {
                                          Toast.makeText(act, "Неполучилось соединиться, попробуйте снова", Toast.LENGTH_SHORT).show();
                                      }
                                  });
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                try {
                    String responseStr = response.body().string();
                    if (checkOnSuccess(act, responseStr)) {
                        succesEnter(act, login, password);
                    } else {
                        act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                             Toast.makeText(act, "Не получилось зарегисрироваться, попробуйте снова", Toast.LENGTH_SHORT).show();
                                }
                        });
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        } );
    }

    private boolean checkOnSuccess(final Activity act, String response) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject obj = (JSONObject) parser.parse(response);
        Boolean status = (Boolean) obj.get("status");
        final String message = (String) obj.get("message");
        return status;
    }

    private String makeParam(String nameParam, String value) {
        return "&" + value;
    }

    private void succesEnter(final Activity act,final String login,final String password) {
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("login", login);
                intent.putExtra("password", password);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }
}
