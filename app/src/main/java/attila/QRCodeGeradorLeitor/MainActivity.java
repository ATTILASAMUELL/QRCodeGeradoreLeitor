package attila.QRCodeGeradorLeitor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    EditText editQrCode;
    Button btnGerarQqrCode;
    ImageView imgQrCode;
    TextView txttitulolido;
    Button btnUrls;
    Button btnlercode;
    TextView txtConteudoUrl;
    Button btnCompartilharQrCode;

    String[] permissaoRequiridas = {Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public static final int APP_PERMISSOES_ID = 2021;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iniciarComponentes();
        btnGerarQqrCodeAcoes();

        btnLendoUrl();
        btnCompartilharQr();

        final Activity activity = this;
        //solicitarPermissaoParaObterLocalizacao();
        btnLerQrcode();
    }


    private void iniciarComponentes() {
        editQrCode = findViewById(R.id.editQrCode);
        btnGerarQqrCode = findViewById(R.id.btnGerarQqrCode);
        imgQrCode = findViewById(R.id.imgQrCode);
        btnlercode = findViewById(R.id.btnlercode);
        txttitulolido = findViewById(R.id.txttitulolido);
        btnCompartilharQrCode = findViewById(R.id.btnCompartilharQrCode);
        btnUrls = findViewById(R.id.btnUrls);
        txtConteudoUrl = findViewById(R.id.txtConteudoUrl);


    }

    private void btnGerarQqrCodeAcoes() {
        btnGerarQqrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(editQrCode.getText().toString())) {
                    editQrCode.setError("Por favor digite algo, como url,frase ou palavra...");
                    editQrCode.requestFocus();
                } else {
                    gerarQrCode(editQrCode.getText().toString());
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    if (imm.isActive()) {
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    }
                }
            }
        });
    }

    private void gerarQrCode(String conteudoDoQrCode) {

        // Usando a biblioteca zxing-android-embedded
        QRCodeWriter qrCode = new QRCodeWriter();

        try {
            BitMatrix bitMatrix = qrCode.encode(conteudoDoQrCode, BarcodeFormat.QR_CODE, 292, 324);
            int tamanho = bitMatrix.getWidth();
            int altura = bitMatrix.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(tamanho, altura, Bitmap.Config.RGB_565);

            for (int x = 0; x < tamanho; x++) {
                for (int y = 0; y < altura; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);

                }
            }
            imgQrCode.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();

        }

    }


    private boolean solicitarPermissaoParaCamera() {
        List<String> permissoesNegadas = new ArrayList<>();

        int permissaoNegada;

        for (String permissao : this.permissaoRequiridas) {
            permissaoNegada = ContextCompat.checkSelfPermission(MainActivity.this, permissao);
            if (permissaoNegada != PackageManager.PERMISSION_GRANTED) {
                permissoesNegadas.add(permissao);
            }

        }
        //se não for vazio pemissões negadas...
        if (!permissoesNegadas.isEmpty()) {

            //janelinha de permissão
            ActivityCompat.requestPermissions(MainActivity.this, permissoesNegadas.toArray(new String[permissoesNegadas.size()]),
                    APP_PERMISSOES_ID);

            return false;

        } else {
            return true;


        }

    }

    private void btnLerQrcode() {
        final Activity activity = this;

        btnlercode.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                if (solicitarPermissaoParaCamera()) {
                    IntentIntegrator integrator = new IntentIntegrator(activity);
                    integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                    integrator.setPrompt("Leitor QR Code Scan");
                    integrator.setCameraId(0);
                    integrator.initiateScan();
                }else{
                    Toast.makeText(getBaseContext(), "Solicitações de permisões não aceitas...", Toast.LENGTH_LONG).show();

                }


            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                alert(result.getContents());
            } else {
                Toast.makeText(this, "Não encontrado", Toast.LENGTH_LONG).show();

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);

        }

    }

    private void alert(String msg) {
        txttitulolido.setText("Conteúdo lido do QR Code:");
        txtConteudoUrl.setText("" + msg);

    }

    private void btnLendoUrl() {
        btnUrls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {


                    if (TextUtils.isEmpty(txtConteudoUrl.getText().toString())) {
                        Toast.makeText(getBaseContext(), "Não leu nenhum QR Code...", Toast.LENGTH_LONG).show();


                    } else {
                        String conteudo = txtConteudoUrl.getText().toString();
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(conteudo)));
                        //Intent defaultBrowser = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_BROWSER);
                        // defaultBrowser.setData(Uri.parse(data));
                        //startActivity(defaultBrowser);
                    }
                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), "Qr Code lido não é uma URL ou  a URL lida não é valida...", Toast.LENGTH_LONG).show();

                }
            }

        });
    }

    private void btnCompartilharQr() {
        btnCompartilharQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compartilhar();
            }
        });
    }

    private void compartilhar() {

        if (imgQrCode.getDrawable() != null) {
            Intent intent = new Intent((Intent.ACTION_SEND));
            intent.setType(("image/jpg"));

            BitmapDrawable drawable = (BitmapDrawable) imgQrCode.getDrawable();
            Bitmap bitmap = drawable.getBitmap();

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

            String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "QR CODE IMAGENS", null);
            Uri uri = Uri.parse(path);

            intent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(intent, "Compartilhar QR Code"));
        } else {
            Toast.makeText(getBaseContext(), "Não Possui QR CODE para ser compartilhado...", Toast.LENGTH_LONG).show();

        }
    }
}