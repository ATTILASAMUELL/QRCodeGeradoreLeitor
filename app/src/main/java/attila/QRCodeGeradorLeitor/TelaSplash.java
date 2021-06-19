package attila.QRCodeGeradorLeitor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class TelaSplash extends AppCompatActivity {
    int tempo = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_splash);

        trocarDeTela();
    }
    private void trocarDeTela() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent trocardeTela = new Intent(TelaSplash.this, MainActivity.class);
                startActivity(trocardeTela);
                finish();

            }
        }, tempo);
    }
}