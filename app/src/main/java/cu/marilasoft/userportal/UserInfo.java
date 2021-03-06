package cu.marilasoft.userportal;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cu.marilasoft.selibrary.UserPortal;
import cu.marilasoft.userportal.net.Communicator;
import cu.marilasoft.userportal.net.RunTask;

public class UserInfo extends AppCompatActivity {

    String userName;
    String password;

    String _time;
    String block_date;
    String delete_date;
    String account_type;
    String service_type;
    String credit;
    String mail_account;
    String recharge_code;

    String mont;
    String accountToTransfer;

    Map<String, String> cookies = new HashMap<>();

    TextView tv_time;
    TextView tv_account;
    TextView tv_block_date;
    TextView tv_delete_date;
    TextView tv_account_type;
    TextView tv_service_type;
    TextView tv_credit;
    TextView tv_mail_account;

    ProgressDialog progressDialog;

    EditText et_recharge_code;
    EditText et_mont;
    AutoCompleteTextView actv_accountToTransfer;
    Button bt_recharge;

    UserPortal userPortal = new UserPortal();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        this.tv_time = findViewById(R.id.tv_info_time);
        this.tv_account = findViewById(R.id.tv_info_user);
        this.tv_block_date = findViewById(R.id.tv_block_date);
        this.tv_delete_date = findViewById(R.id.tv_delete_date);
        this.tv_account_type = findViewById(R.id.tv_account_type);
        this.tv_service_type = findViewById(R.id.tv_service_type);
        this.tv_credit = findViewById(R.id.tv_account_credit);
        this.tv_mail_account = findViewById(R.id.tv_mail_account);

        Bundle datos = getIntent().getExtras();
        this.userName = datos.getString("userName");
        this.password = datos.getString("password");
        this.block_date = datos.getString("blockDate");
        this.delete_date = datos.getString("delDate");
        this.account_type = datos.getString("accountType");
        this.service_type = datos.getString("serviceType");
        this.credit = datos.getString("credit");
        this._time = datos.getString("time");
        this.mail_account = datos.getString("mailAccount");

        this.cookies.put("session", datos.getString("session"));
        this.cookies.put("nauta_lang", datos.getString("nauta_lang"));

        this.tv_time.setText(_time);
        this.tv_account.setText(userName);
        this.tv_block_date.setText(block_date);
        this.tv_delete_date.setText(delete_date);
        this.tv_account_type.setText(account_type);
        this.tv_service_type.setText(service_type);
        this.tv_credit.setText(credit);
        this.tv_mail_account.setText(mail_account);

        this.et_recharge_code = findViewById(R.id.et_recharge_code);
        this.bt_recharge = findViewById(R.id.bt_recharge);
        this.et_mont = findViewById(R.id.et_mont);
        this.actv_accountToTransfer = findViewById(R.id.actv_accountToTransfer);

        this.progressDialog = new ProgressDialog(this);

        AutoCompleteAdapter adapter = new AutoCompleteAdapter(this,
                android.R.layout.simple_expandable_list_item_1);
        actv_accountToTransfer.setAdapter(adapter);
    }

    public void recharge(View v) {
        if (et_recharge_code.getText().toString().length() != 12 &&
                et_recharge_code.getText().toString().length() != 16) {
            et_recharge_code.setError("El código de recarga debe ser de 12 o 16 dígitos.");
        } else {
            progressDialog.setTitle("UserPortal");
            progressDialog.setMessage("Recargando...");
            progressDialog.show();
            recharge_code = et_recharge_code.getText().toString();
            new RunTask(new Communicator() {
                @Override
                public void Communicate() {

                }

                @Override
                public void Communicate(UserPortal userPortal) {
                    try {
                        userPortal.recharge(UserInfo.this.recharge_code,
                                UserInfo.this.cookies);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void communicate() {
                    UserInfo.this.progressDialog.dismiss();
                    if (userPortal.status().get("status").equals("error")) {
                        List<String> errors = (List<String>) userPortal.status().get("msg");
                        UserInfo.this.et_recharge_code.setError(errors.get(0));
                        AlertDialog.Builder builder = new AlertDialog.Builder(UserInfo.this);
                        builder.setTitle("UserPortal").setMessage("Cuenta recargada!");
                        builder.setPositiveButton("OK", null);
                        AlertDialog success = builder.create();
                        success.setCancelable(false);
                        success.show();
                    }
                }
            }, userPortal).execute();
        }
    }

    public void transfer(View v) {
        int validate = 0;
        if (et_mont.getText().toString().length() == 0) {
            et_mont.setError("Introduzca un monto válido.");
            validate = 1;
        }
        if (actv_accountToTransfer.getText().toString().equals("")) {
            actv_accountToTransfer.setError("Introdusca un nombre de usuario.");
            validate = 1;
        } else if (!actv_accountToTransfer.getText().toString().endsWith("@nauta.com.cu") &&
                !actv_accountToTransfer.getText().toString().endsWith("@nauta.co.cu")) {
            actv_accountToTransfer.setError("Introdusca un nombre de usuario válido.");
            validate = 1;
        }
        if (validate != 1) {
            if (credit.equals("$0,00 CUC")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UserInfo.this);
                builder.setTitle("UserPortal").setMessage("Su saldo no es suficiente!");
                builder.setPositiveButton("OK", null);
                AlertDialog success = builder.create();
                success.setCancelable(false);
                success.show();
            } else {
                progressDialog.setTitle("UserPortal");
                progressDialog.setMessage("Transfiriendo...");
                progressDialog.show();
                mont = et_mont.getText().toString();
                accountToTransfer = actv_accountToTransfer.getText().toString();
                new RunTask(new Communicator() {
                    @Override
                    public void Communicate() {

                    }

                    @Override
                    public void Communicate(UserPortal userPortal) {
                        try {
                            userPortal.transfer(UserInfo.this.mont,
                                    UserInfo.this.password,
                                    UserInfo.this.accountToTransfer,
                                    UserInfo.this.cookies);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void communicate() {
                        UserInfo.this.progressDialog.dismiss();
                        if (userPortal.status().get("status").equals("error")) {
                            List<String> errors = (List<String>) userPortal.status().get("msg");
                            String errors_ = "Se encontraron los siguientes errores al intentar" +
                                    " transferir el saldo:";
                            for (String error : errors) {
                                errors_ = errors_ + "\n" + error;
                            }
                            AlertDialog.Builder builder = new AlertDialog.Builder(UserInfo.this);
                            builder.setTitle("UserPortal").setMessage(errors_);
                            AlertDialog success = builder.create();
                            success.show();
                        }
                    }
                }, userPortal).execute();
            }
        }
    }
}
