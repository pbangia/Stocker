package app.stocker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import app.stocker.adapters.ProductListAdapter;
import app.stocker.data.Product;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DisplayProductsActivity extends AppCompatActivity {
    private List<Product> productList = new ArrayList<Product>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_products);
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


    }

    public void addProducts(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, AddProductsActivity.class);
        String category = getIntent().getStringExtra("clickedCategory");
        intent.putExtra("clickedCategory",category);
        startActivity(intent);
    }

    private void populateProductList() {
        SharedPreferences prefs = getSharedPreferences("products", MODE_PRIVATE);
        String category = getIntent().getStringExtra("clickedCategory");
        setTitle(category);
        Map<String,?> keys = prefs.getAll();
        Gson gson = new Gson();
        productList = new ArrayList<>();
        for(Map.Entry<String,?> entry : keys.entrySet()){
            Log.d("map values",entry.getKey() + ": " + entry.getValue().toString());

            String json = prefs.getString(entry.getKey(), "");
            if (!json.isEmpty()){
                Product product = gson.fromJson(json, Product.class);
                if (category.equals("All") || category.equals(product.getCategory())) {
                    productList.add(product);
                }
            }
        }
    }

    private void populateListView() {

        ListView list = (ListView) findViewById(R.id.product_list);
        ArrayAdapter<Product> adapter = new ProductListAdapter(DisplayProductsActivity.this, R.layout.item_view, productList);
        list.setAdapter(adapter);
        list.setEmptyView(findViewById(R.id.empty));
        //list.setAdapter(new ArrayAdapter<String>(DisplayProductsActivity.this, android.R.layout.simple_list_item_1 , productList));

    }

    private void registerClick() {
        ListView list = (ListView) findViewById(R.id.product_list);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        final Context context = getBaseContext();
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int pos,
                                    long id) {
                Product product = productList.get(pos);
               // Toast.makeText(getBaseContext(), product.getTitle(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, AddProductsActivity.class);
                String json = new Gson().toJson(product);
                Log.d("Clicked product: ", json);
                intent.putExtra("clickedProduct", json);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        populateProductList();
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
