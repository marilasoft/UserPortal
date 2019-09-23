package cu.marilasoft.userportal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import cu.marilasoft.selibrary.UserPortal;
import cu.marilasoft.userportal.net.Communicator;
import cu.marilasoft.userportal.net.RunTask;

public class MainActivity extends AppCompatActivity {

    String _time;
    String account;
    String block_date;
    String delete_date;
    String account_type;
    String service_type;
    String credit;
    String mail_account;

    AutoCompleteTextView actv_user_name;
    EditText et_password;
    EditText et_captcha_code;
    ImageView iv_captcha_img;
    Button bt_login_acept;
    Bitmap bitmap = null;
    ProgressDialog progressDialog;
    Intent intent;

    Map<String, String> cookies;
    Map<Object, Object> status;

    UserPortal userPortal = new UserPortal();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.actv_user_name = findViewById(R.id.actv);
        this.et_password = findViewById(R.id.et_password);
        this.et_captcha_code = findViewById(R.id.et_captchaCode);
        this.iv_captcha_img = findViewById(R.id.iv_captcha);
        this.bt_login_acept = findViewById(R.id.bt_login);

        this.progressDialog = new ProgressDialog(this);
        this.intent = new Intent(this, UserInfo.class);

        AutoCompleteAdapter adapter = new AutoCompleteAdapter(this,
                android.R.layout.simple_expandable_list_item_1);
        actv_user_name.setAdapter(adapter);

        preLoad();
    }

    public void preLoad() {
        new RunTask(new Communicator() {
            @Override
            public void Communicate() {

            }

            @Override
            public void Communicate(UserPortal userPortal) {
                try {
                    userPortal.preLogin();
                    Map<String, String> cookies = userPortal.cookies();
                    userPortal.loadCAPTCHA(cookies);
                    MainActivity.this.cookies = cookies;
                    MainActivity.this.userPortal = userPortal;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void communicate() {
                if (userPortal.captchaImg != null) {
                    MainActivity.this.bitmap = BitmapFactory.decodeStream(
                            new ByteArrayInputStream(userPortal.captchaImg)
                    );
                    MainActivity.this.iv_captcha_img.setImageBitmap(MainActivity.this.bitmap);
                } else {
                    Toast.makeText(MainActivity.this,
                            "No se pudo cargar la imagen CAPTCHA", Toast.LENGTH_LONG).show();
                }
            }
        }, userPortal).execute();
    }

    public void reLoadCaptcha(View v) {
        reLoadCaptcha();
    }

    public void reLoadCaptcha() {
        new RunTask(new Communicator() {
            @Override
            public void Communicate() {

            }

            @Override
            public void Communicate(UserPortal userPortal) {
                try {
                    if (MainActivity.this.cookies == null) {
                        userPortal.preLogin();
                        MainActivity.this.cookies = userPortal.cookies();
                        MainActivity.this.userPortal = userPortal;
                    }
                    userPortal.loadCAPTCHA(MainActivity.this.cookies);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void communicate() {
                if (userPortal.captchaImg != null) {
                    MainActivity.this.bitmap = BitmapFactory.decodeStream(
                            new ByteArrayInputStream(userPortal.captchaImg)
                    );
                    MainActivity.this.iv_captcha_img.setImageBitmap(MainActivity.this.bitmap);
                } else {
                    Toast.makeText(MainActivity.this,
                            "No se pudo cargar la imagen CAPTCHA", Toast.LENGTH_LONG).show();
                }
            }
        }, userPortal).execute();
    }

    public void login(View v) {
        progressDialog.setTitle("UserPortal");
        progressDialog.setMessage("Iniciado Session...");
        progressDialog.setCancelable(false);
        int validate = 0;
        if (actv_user_name.getText().toString().equals("")) {
            actv_user_name.setError("Introdusca un nombre de usuario.");
            validate = 1;
        } else if (!actv_user_name.getText().toString().endsWith("@nauta.com.cu") &&
                !actv_user_name.getText().toString().endsWith("@nauta.co.cu")) {
            actv_user_name.setError("Introdusca un nombre de usuario válido.");
            validate = 1;
        }
        if (et_password.getText().toString().equals("")) {
            et_password.setError("Introdusca una contraseña.");
            validate = 1;
        }
        if (et_captcha_code.getText().toString().equals("")) {
            et_captcha_code.setError("Introdusca el codigo captcha.");
            validate = 1;
        }
        if (validate != 1) {
            progressDialog.show();
            new RunTask(new Communicator() {
                @Override
                public void Communicate() {

                }

                @Override
                public void Communicate(UserPortal userPortal) {
                    try {
                        userPortal.login(MainActivity.this.actv_user_name.getText().toString(),
                                MainActivity.this.et_password.getText().toString(),
                                MainActivity.this.et_captcha_code.getText().toString(),
                                MainActivity.this.cookies);
                        MainActivity.this.status = userPortal.status();
                        if (userPortal.status().get("status").equals("success")) {
                            MainActivity.this.account = userPortal.userName();
                            MainActivity.this._time = userPortal.time();
                            MainActivity.this.block_date = userPortal.blockDate();
                            MainActivity.this.delete_date = userPortal.delDate();
                            MainActivity.this.account_type = userPortal.accountType();
                            MainActivity.this.service_type = userPortal.serviceType();
                            MainActivity.this.credit = userPortal.credit();
                            MainActivity.this.mail_account = userPortal.mailAccount();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        MainActivity.this.progressDialog.dismiss();
                    }
                }

                @Override
                public void communicate() {
                    MainActivity.this.progressDialog.dismiss();
                    if (MainActivity.this.status.get("status").equals("success")) {
                        MainActivity.this.intent.putExtra("userName",
                                MainActivity.this.account);
                        MainActivity.this.intent.putExtra("password",
                                MainActivity.this.et_password.getText().toString());
                        MainActivity.this.intent.putExtra("blockDate",
                                MainActivity.this.block_date);
                        MainActivity.this.intent.putExtra("delDate",
                                MainActivity.this.delete_date);
                        MainActivity.this.intent.putExtra("accountType",
                                MainActivity.this.account_type);
                        MainActivity.this.intent.putExtra("serviceType",
                                MainActivity.this.service_type);
                        MainActivity.this.intent.putExtra("credit",
                                MainActivity.this.credit);
                        MainActivity.this.intent.putExtra("time",
                                MainActivity.this._time);
                        MainActivity.this.intent.putExtra("mailAccount",
                                MainActivity.this.mail_account);
                        MainActivity.this.intent.putExtra("session",
                                MainActivity.this.cookies.get("session"));
                        MainActivity.this.intent.putExtra("nauta_lang",
                                MainActivity.this.cookies.get("nauta_lang"));
                        MainActivity.this.transition();
                    } else if (MainActivity.this.status.get("status").equals("error")) {
                        String errors = "";
                        for (String error : (List<String>) MainActivity.this.status.get("msg")) {
                            errors += "\n" + error;
                            if (error.contains("Usuario")) {
                                MainActivity.this.actv_user_name.setError("Puede que el " +
                                        "nombre de usuario sea incorrecto.");
                            }
                            if (error.contains("contraseña")) {
                                MainActivity.this.et_password.setError("Puede que la " +
                                        "contraseña sea incorrecta.");
                            }
                            if (error.contains("Captcha")) {
                                MainActivity.this.et_captcha_code.setError(error);
                            }
                        }
                        Toast.makeText(MainActivity.this, "No se pudo iniciar session" +
                                "por los siguiente errores:" + errors, Toast.LENGTH_LONG).show();
                        MainActivity.this.reLoadCaptcha();
                    }
                }
            }, userPortal).execute();
        }
    }

    public void transition() {
        startActivity(intent);
    }
}
