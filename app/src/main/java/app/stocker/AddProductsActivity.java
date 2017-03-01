package app.stocker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import app.stocker.data.Product;
import com.google.gson.Gson;

import java.util.Locale;

public class AddProductsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_products);

        String json = getIntent().getStringExtra("clickedProduct");
        if (json != null){
            Product product = new Gson().fromJson(json, Product.class);
            TextView title = (TextView) findViewById(R.id.edit_product_title);
            TextView qty = (TextView) findViewById(R.id.edit_product_quantity);
            TextView price = (TextView) findViewById(R.id.edit_product_price);
            TextView notes = (TextView) findViewById(R.id.edit_product_notes);

            title.setText(product.getTitle());
            qty.setText(Integer.toString(product.getQuantity()));
            price.setText(String.format(Locale.getDefault(),"%.2f", product.getPrice()));
            notes.setText(product.getNotes());
        }


    }

    public void saveProduct(View view) {
        // Do something in response to button
        EditText titleTxt = (EditText) findViewById(R.id.edit_product_title);
        String title = titleTxt.getText().toString();

        EditText qtyTxt = (EditText) findViewById(R.id.edit_product_quantity);
        int qty = Integer.parseInt(qtyTxt.getText().toString());

        EditText notesTxt = (EditText) findViewById(R.id.edit_product_notes);
        String notes = notesTxt.getText().toString();

        EditText priceTxt = (EditText) findViewById(R.id.edit_product_price);
        double price = Double.parseDouble(priceTxt.getText().toString());

        if (title.isEmpty()) {
            Snackbar.make(view, "Please fill title.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return;
        }

        Product product = new Product(title, qty, notes, price);
        SharedPreferences prefs = getSharedPreferences("products",MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(product);
        prefsEditor.putString(title, json);
        prefsEditor.commit();
        finish();
        //intent.putExtra(EXTRA_MESSAGE, message);
       // Intent intent = new Intent(this, DisplayProductsActivity.class);
       // startActivity(intent);
    }


}
