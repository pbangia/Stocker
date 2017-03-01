package app.stocker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import app.stocker.R;
import app.stocker.data.Product;

import java.util.List;
import java.util.Locale;


public class ProductListAdapter extends ArrayAdapter<Product> {
    private Context context;
    private List<Product> productList;
    public ProductListAdapter(Context context, int resourceId, List<Product> productList){
        super(context, resourceId, productList);
        this.context = context;
        this.productList = productList;
    }

    @Override
    public View getView (int pos, View convertView, ViewGroup parent) {
        View itemView = convertView;
        if (itemView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.item_view, parent, false);
        }

        Product product = productList.get(pos);

        TextView itemTitle = (TextView) itemView.findViewById(R.id.item_title);
        itemTitle.setText(product.getTitle());

        TextView itemQuantity = (TextView) itemView.findViewById(R.id.item_qty);
        itemQuantity.setText(String.format(Locale.getDefault(),"Qty: %d", product.getQuantity()));

        TextView itemPrice = (TextView) itemView.findViewById(R.id.item_price);
        itemPrice.setText(String.format(Locale.getDefault(),"$%.2f", product.getPrice()));


        return itemView;
    }
}
