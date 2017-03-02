package app.stocker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import app.stocker.adapters.ProductListAdapter;
import app.stocker.data.Product;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProductCategoriesActivity extends AppCompatActivity {
    private List<String> categoryList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_categories);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
/*        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });*/
        SharedPreferences prefs = getSharedPreferences("categoryList",MODE_PRIVATE);

        if (!prefs.contains("categories")) {
            SharedPreferences.Editor prefsEditor = prefs.edit();
            categoryList.add("All");
            String json = new Gson().toJson(categoryList);
            prefsEditor.putString("categories", json);
            prefsEditor.commit();
        }



    }

    public void addCategory(View view) {
        // TODO: add category activity
        Intent intent = new Intent(this, AddCategoryActivity.class);
        startActivity(intent);
    }

    private void populateCategoryList() {
        categoryList = new ArrayList<>();
        SharedPreferences prefs = getSharedPreferences("categoryList", MODE_PRIVATE);
        String json = prefs.getString("categories", "");
        categoryList = new Gson().fromJson(json, ArrayList.class);

    }

    private void populateListView() {

        ListView list = (ListView) findViewById(R.id.category_list);
       // ArrayAdapter<Product> adapter = new ProductListAdapter(DisplayProductsActivity.this, R.layout.item_view, productList);
        //list.setAdapter(adapter);
        list.setAdapter(new ArrayAdapter<String>(ProductCategoriesActivity.this, android.R.layout.simple_list_item_1 , categoryList));

    }

    private void registerClick() {
        ListView list = (ListView) findViewById(R.id.category_list);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            final Context context = getBaseContext();
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int pos,
                                    long id) {
                String category = categoryList.get(pos);
                // Toast.makeText(getBaseContext(), product.getTitle(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, DisplayProductsActivity.class);
                Log.d("Clicked category: ", category);
                intent.putExtra("clickedCategory", category);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        populateCategoryList();
        populateListView();
        registerClick();
//        Gson gson = new Gson();

//
//        String json = prefs.getString("test", "");
//        if (!json.isEmpty()){
//            Product product = gson.fromJson(json, Product.class);
//            View view = findViewById(R.id.content_display_products);
//            Snackbar.make(view, product.getTitle()+product.getQuantity(), Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show();
//        }


//        ListView lvName = (ListView) findViewById(R.id.lv_Name);
//        lvName.setAdapter(new ArrayAdapter<String>(DisplayProductsActivity.this, android.R.layout.simple_list_item_1 , productList));
//        lvName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//                                    long arg3) {
//                Toast.makeText(getBaseContext(), productList.get(arg2), Toast.LENGTH_SHORT).show();
//            }
//        });
    }

}
