package app.stocker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import app.stocker.data.Product;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ProductCategoriesActivity extends AppCompatActivity {
    private List<String> categoryList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private String listItem;
    private Object mActionMode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_categories);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        SharedPreferences prefs = getSharedPreferences("categoryList",MODE_PRIVATE);
        if (!prefs.contains("categories")) {
            SharedPreferences.Editor prefsEditor = prefs.edit();
            categoryList.add("All");
            String json = new Gson().toJson(categoryList);
            prefsEditor.putString("categories", json);
            prefsEditor.commit();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        populateCategoryList();
        populateListView();
        registerClick();
    }

    private void populateCategoryList() {
        categoryList = new ArrayList<>();
        SharedPreferences prefs = getSharedPreferences("categoryList", MODE_PRIVATE);
        String json = prefs.getString("categories", "");
        categoryList = new Gson().fromJson(json, ArrayList.class);

    }

    private void populateListView() {

        ListView list = (ListView) findViewById(R.id.category_list);
        adapter = new ArrayAdapter<String>(ProductCategoriesActivity.this, android.R.layout.simple_list_item_1 , categoryList);
        list.setAdapter(adapter);
        list.setEmptyView(findViewById(R.id.empty));
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                listItem = categoryList.get(position);
                if (mActionMode==null) {
                    mActionMode = ProductCategoriesActivity.this.startActionMode(mActionModeCallback);
                }
                return true;
            }
        });
    }

    private void registerClick() {
        ListView list = (ListView) findViewById(R.id.category_list);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            final Context context = getBaseContext();
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int pos,
                                    long id) {
                String category = categoryList.get(pos);
                Intent intent = new Intent(context, DisplayProductsActivity.class);
                Log.d("Clicked category: ", category);
                intent.putExtra("clickedCategory", category);
                startActivity(intent);
            }
        });
    }

    private ActionMode.Callback mActionModeCallback = new android.view.ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.context_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete2:
                    ProductCategoriesActivity.this.deleteCategoryOption(listItem);
                    mode.finish();
                    return true;
                case R.id.action_edit:
                    ProductCategoriesActivity.this.addCategory(null);
                    mode.finish();
                    return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            listItem = null;

        }
    };

    public void addCategory(View view) {
        Intent intent = new Intent(this, AddCategoryActivity.class);
        if (listItem!=null){
            intent.putExtra("selectedCategory", listItem);
        }
        startActivity(intent);
    }

    private void deleteCategory(boolean withContents, String category) {
        SharedPreferences prefs = getSharedPreferences("categoryList",MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        Gson gson = new Gson();
        String json = prefs.getString("categories","");
        List categories =gson.fromJson(json, ArrayList.class);
        categories.remove(category);
        json = gson.toJson(categories);
        prefsEditor.putString("categories", json);

        Log.d("Deleting category", category);
        prefsEditor.commit();
        adapter.remove(listItem);
        adapter.notifyDataSetChanged();
        Toast.makeText(getBaseContext(), category+" deleted.", Toast.LENGTH_SHORT).show();

        if (withContents){
            prefs = getSharedPreferences("products", MODE_PRIVATE);
            prefsEditor = prefs.edit();
            Map<String,?> keys = prefs.getAll();
            for(Map.Entry<String,?> entry : keys.entrySet()){
                json = prefs.getString(entry.getKey(), "");
                if (!json.isEmpty()){
                    Product product = gson.fromJson(json, Product.class);
                    if (category.equals(product.getCategory())) {
                        Log.d("Deleting product",entry.getKey() + ": " + entry.getValue().toString());
                        prefsEditor.remove(product.getTitle());
                    }
                }
            }
            prefsEditor.commit();
        }
    }

    private void deleteCategoryOption(final String category ){

        if (category.equals("All")) {
            new AlertDialog.Builder(this)
                    .setMessage("Can not delete default category.")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, null).show();
            return;

        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete "+category)
                //.setMessage("Do you want to delete this category?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setItems(new CharSequence[]
                        {"Delete category including contents", "Delete only category", "Cancel"},
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        switch (which) {
                            case 0:
                                ProductCategoriesActivity.this.deleteCategory(true, category);
                                break;
                            case 1:
                                ProductCategoriesActivity.this.deleteCategory(false, category);
                                break;
                            case 2:
                                break;
                        }
                    }
                })
            .create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.sort_high_price).setVisible(false);
        menu.findItem(R.id.sort_low_price).setVisible(false);
        MenuItem item = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String search) {
                adapter.getFilter().filter(search);
                return false;
            }
        });


        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.sort_az: sortAZ(); break;
            case R.id.sort_za: sortZA(); break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sortAZ(){
        adapter.sort(new Comparator<String>() {
            public int compare(String arg0, String arg1) {
                return arg0.compareTo(arg1);
            }
        });
        adapter.notifyDataSetChanged();
    }

    private void sortZA(){
        adapter.sort(new Comparator<String>() {
            public int compare(String arg0, String arg1) {
                return arg1.compareTo(arg0);
            }
        });
        adapter.notifyDataSetChanged();
    }

}
