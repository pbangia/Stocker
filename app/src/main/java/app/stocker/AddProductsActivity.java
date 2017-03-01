package app.stocker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import app.stocker.data.Product;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
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

        final Spinner category = (Spinner) findViewById(R.id.edit_product_category);
        List<String> list = new ArrayList<String>();
        list.add("1");
        list.add("2");
        list.add("3");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(dataAdapter);
        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View viewClicked, int pos,
                                    long id) {
                Toast.makeText(getBaseContext(),category.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });


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
