package com.itsp20032018.coffeeshop.coffeeshopapp.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.itsp20032018.coffeeshop.coffeeshopapp.R;
import com.itsp20032018.coffeeshop.coffeeshopapp.model.StockItem;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ItemAdapter extends FirestoreRecyclerAdapter {

    // string to identify the item type of the list
    String type;

    // item click listener
    OnItemClickListener listener;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options      FireStore query
     */
    public ItemAdapter(@NonNull FirestoreRecyclerOptions options, String type) {
        super(options);

        //assign type
        this.type = type;
    }

    /* ViewHolder classes TODO: add other item view holder classes
    * * * * * * * * * * * *
    * * * * * * * * * * * *
    **/
    // stock holder
    class StockHolder extends RecyclerView.ViewHolder {

        // get views
        CircleImageView stockItemImage;
        TextView stockName;
        TextView stockQty;

        public StockHolder(View itemView) {
            super(itemView);

            // assign views
            stockItemImage = (CircleImageView) itemView.findViewById(R.id.stockImageView);
            stockName = (TextView) itemView.findViewById(R.id.stockNameDetailTextView);
            stockQty = (TextView) itemView.findViewById(R.id.stockQuantityTextView);

            // set click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // get item position in adapter
                    int position = getAdapterPosition();

                    // check if position is null or listener is nukk
                    if(position != RecyclerView.NO_POSITION && listener != null) {
                        DocumentSnapshot qs = (DocumentSnapshot) getSnapshots().getSnapshot(position);
                        listener.onItemClick(qs, position);
                    }
                }
            });
        }

    }


    /**
     * @param holder    object containing general view item
     * @param position  position of item in list
     * @param model    the model object containing the data that should be used to populate the view.
     * @see #onBindViewHolder(RecyclerView.ViewHolder, int)
     */
    @Override
    protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull Object model) {
        switch(type){
            case "stock":
                // cast model to stock item
                StockItem stockItem = (StockItem) model;

                // download image using image url and set to stock image
                if(!stockItem.getImage().equals("")) Picasso.get().load(stockItem.getImage()).into(((StockHolder) holder).stockItemImage);

                // set stock item name
                ((StockHolder) holder).stockName.setText(stockItem.getName());

                // set stock item quantity
                ((StockHolder) holder).stockQty.setText(String.valueOf(stockItem.getQuantity()));

        }

    }

    /**
     * @param parent   The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create view to hold item
        View view = null;

        // check type of item
        switch (type){
            case "stock":
                // inflate view using stock item xml
                view = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.stockitem_list_content, parent, false);
                // return new StockHolder view holder object
                return new StockHolder(view);
                // TODO: add other list items
        }

        return null;
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }



}
