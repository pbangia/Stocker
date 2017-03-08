package app.stocker;

import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import app.stocker.data.Product;

public class AddCategoryActivity extends AppCompatActivity {
    String selectedCategory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        selectedCategory = getIntent().getStringExtra("selectedCategory");
        if (selectedCategory != null){
            TextView title = (TextView) findViewById(R.id.edit_category_title);
            title.setText(selectedCategory);
        }


    }

    public void saveCategory(View view) {
        // Do something in response to button
        EditText titleTxt = (EditText) findViewById(R.id.edit_category_title);
        String title = titleTxt.getText().toString();

        if (title.isEmpty()) {
            Snackbar.make(view, "Please fill title.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return;
        }

        if (selectedCategory !=null && selectedCategory.equals(title)){
            finish();
            return;
        }

        Gson gson = new Gson();
        SharedPreferences prefs = getSharedPreferences("categoryList",MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        String previousList = prefs.getString("categories", "");
        List categories = gson.fromJson(previousList, ArrayList.class);

        if (selectedCategory!=null) {
            categories.remove(selectedCategory);

        }

        categories.add(title);

        String newList = gson.toJson(categories);
        prefsEditor.putString("categories", newList);
        prefsEditor.commit();

        if (selectedCategory!=null && !selectedCategory.equals(title)){
            updateProductCategories(title);
        }
        finish();
        //intent.putExtra(EXTRA_MESSAGE, message);
        // Intent intent = new Intent(this, DisplayProductsActivity.class);
        // startActivity(intent);
    }

    private void updateProductCategories(String title) {
        SharedPreferences prefs = getSharedPreferences("products", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        Map<String,?> keys = prefs.getAll();
        String json;
        Gson gson = new Gson();

        for(Map.Entry<String,?> entry : keys.entrySet()){
            json = prefs.getString(entry.getKey(), "");
            if (!json.isEmpty()){
                Product product = gson.fromJson(json, Product.class);
                if (selectedCategory.equals(product.getCategory())) {
                    product.setCategory(title);
                    json = gson.toJson(product, Product.class);
                    prefsEditor.putString(entry.getKey(), json);
                }

            }
        }

        prefsEditor.commit();
    }


}