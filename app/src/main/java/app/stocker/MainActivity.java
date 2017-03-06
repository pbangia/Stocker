package app.stocker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Map;

import app.stocker.DisplayProductsActivity;
import app.stocker.R;
import app.stocker.data.Product;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void showCategories(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, ProductCategoriesActivity.class);
        startActivity(intent);
    }

    public void scanBarcode(View view) {
        // Do something in response to button
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scan");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if (result!=null){
            if (result.getContents()!=null){
                String barcode = result.getContents();
                Log.d("Barcode", "scanned "+ barcode);
                SharedPreferences prefs = getSharedPreferences("products", MODE_PRIVATE);
                Gson gson = new Gson();
                String productJson = "";
                Map<String,?> keys = prefs.getAll();

                for(Map.Entry<String,?> entry : keys.entrySet()){
                    String json = prefs.getString(entry.getKey(), "");
                    if (!json.isEmpty()){
                        Product product = gson.fromJson(json, Product.class);
                        if (barcode.equals(Long.toString(product.getBarcode()))) {
                            Log.d("Found scanned", entry.getKey() + ": " + entry.getValue().toString());
                            productJson = gson.toJson(product, Product.class);
                            Intent intent = new Intent(this, AddProductsActivity.class);
                            intent.putExtra("clickedProduct", productJson);
                            startActivity(intent);
                            return;
                        }
                    }

                }

                Toast.makeText(MainActivity.this, "Could not find barcode", Toast.LENGTH_SHORT);
            }
        } else {
            super.onActivityResult(requestCode,resultCode,data);
        }
    }
}

