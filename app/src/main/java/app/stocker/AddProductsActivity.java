package app.stocker;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import app.stocker.data.Product;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AddProductsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_products);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        Spinner categorySpinner = (Spinner) findViewById(R.id.edit_product_category);
        SharedPreferences prefs = getSharedPreferences("categoryList", MODE_PRIVATE);
        String json = prefs.getString("categories", "");
        List list = new Gson().fromJson(json, ArrayList.class);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(dataAdapter);

        String category = getIntent().getStringExtra("clickedCategory");
        ArrayAdapter arrayAdapter = (ArrayAdapter) categorySpinner.getAdapter();
        categorySpinner.setSelection(arrayAdapter.getPosition(category));

//        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View viewClicked, int pos,
//                                       long id) {
//                Toast.makeText(getBaseContext(),category.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> arg0) {
//                // TODO Auto-generated method stub
//            }
//        });


        json = getIntent().getStringExtra("clickedProduct");
        if (json != null){
            Product product = new Gson().fromJson(json, Product.class);
            TextView title = (TextView) findViewById(R.id.edit_product_title);
            TextView qty = (TextView) findViewById(R.id.edit_product_quantity);
            TextView price = (TextView) findViewById(R.id.edit_product_price);
            TextView notes = (TextView) findViewById(R.id.edit_product_notes);
            TextView barcode = (TextView) findViewById(R.id.edit_product_barcode);

            title.setText(product.getTitle());
            qty.setText(Integer.toString(product.getQuantity()));
            price.setText(String.format(Locale.getDefault(),"%.2f", product.getPrice()));
            notes.setText(product.getNotes());
            categorySpinner.setSelection(arrayAdapter.getPosition(product.getCategory()));
            barcode.setText(Long.toString(product.getBarcode()));

            setTitle(product.getTitle());

        }

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
                Log.d("Barcode", "scanned "+result.getContents());
                TextView title = (TextView) findViewById(R.id.edit_product_barcode);
                title.setText(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode,resultCode,data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                deleteProduct();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
    private void deleteProduct(){
        new AlertDialog.Builder(this)
                .setTitle("Delete Product")
                .setMessage("Do you want to delete this product?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        EditText titleTxt = (EditText) findViewById(R.id.edit_product_title);
                        String title = titleTxt.getText().toString();
                        SharedPreferences.Editor prefsEditor = getSharedPreferences("products",MODE_PRIVATE).edit();
                        prefsEditor.remove(title);
                        prefsEditor.commit();

                        Toast.makeText(getBaseContext(), title+" deleted.", Toast.LENGTH_SHORT).show();
                        finish();
                    }})
                .setNegativeButton(android.R.string.no, null).show();
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


        Spinner categorySpinner = (Spinner) findViewById(R.id.edit_product_category);
        String selected = categorySpinner.getSelectedItem().toString();

        EditText barcodeTxt = (EditText) findViewById(R.id.edit_product_barcode);
        long barcode = Long.parseLong(barcodeTxt.getText().toString());

        if (title.isEmpty()) {
            Snackbar.make(view, "Please fill title.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return;
        }

        Product product = new Product(title, qty, notes, price, selected, barcode);
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
