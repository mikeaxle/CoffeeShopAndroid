package com.itsp20032018.coffeeshop.coffeeshopapp.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.itsp20032018.coffeeshop.coffeeshopapp.R;
import com.itsp20032018.coffeeshop.coffeeshopapp.model.StockItem;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomAdapter extends ArrayAdapter {

    private int resource;
    private String currentActivity;

    public CustomAdapter(@NonNull Context context, @LayoutRes int resource, String currentActivity) {
        super(context, resource);

        this.resource = resource;
        this.currentActivity = currentActivity;
    }

    /**
     * getView -  create stock item view
     *
     * @param position    - position of current item
     * @param convertView - current view
     * @param parent      - parent view
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // view object
        View v = null;

        try {
            // check if object should be recycled
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                v = inflater.inflate(resource, null);

            } else {
                v = convertView;
            }

            // check calling activity
            switch (currentActivity) {
                case "stock":
                    // get stock item
                    final StockItem stockItem = (StockItem) getItem(position);

                    // get views
                    CircleImageView stockItemImage = (CircleImageView) v.findViewById(R.id.stockImageView);
                    TextView stockName = (TextView) v.findViewById(R.id.stockNameTextView);
                    TextView stockQty = (TextView) v.findViewById(R.id.stockQuantityTextView);


                    // download image from url and set image view
                    Picasso.get().load(stockItem.image).into(stockItemImage);

                    // set text views
                    stockName.setText(stockItem.name);
                    stockQty.setText(stockItem.quantity + "");

                    break;

                default:
                    break;
            }

        } catch (Exception e) {
            Log.e("Error occurred: ", e.getMessage());
        }

        // return view
        return v;
    }

}
