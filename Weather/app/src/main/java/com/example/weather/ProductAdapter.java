package com.example.weather;


import android.annotation.SuppressLint;
import android.content.Context;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;
import java.util.List;


public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context mCtx;
    private List<City> productList; //Adapter listája

    private List<Integer> pos = new LinkedList<>();


    public ProductAdapter(Context mCtx, List<City> productList) {
        this.mCtx = mCtx;
        this.productList = productList;
    }

    //ViewHolder segítségevel tudom megjeleníteni az adatokat a képernyőn.
    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) { //Ezzel a függvénnyel initzializálom a ViewHoldereketet. Ez a függvény rögtön az adapter létrejötte után hívódik meg.
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.list_item, null); //A list.item.xml layout fileban megvannak adva a View-k az egy városhoz tartozó időjárásokhoz
        Log.i("asd","tef");
        return new ProductViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final ProductViewHolder holder, final int position) {//Ezzel a függvénnyel kötöm össze a ViewHoldereket az adapterrel. Itt tudom hozzáadni az adatokat a ViewHolderhez.

        //A ViewHolder pozicióját használja hogy milegyen a tartalom a lista alapján

        City curCity = productList.get(position);

        //Volt egy olyan problémám, hogy ha rákattintottam egy városra akkor a listában másik 2 városhoz is kiírt plussz adatot
        //Erre találtam egy megoldást, még hozzá csináltam egy listát amiben eltárolom az aktuálisan látható poziciókat
        //Ha rákattintok egy item-re akkor ehhez a listához hozzáadja az aktuális poziciót
        //Viszont olyan poiciók amik nem láthatóak azokat takarja el
        if(pos.contains(position))
        {
           holderItemsOn(holder,position); //láthatóvá teszi
        }else {
            holderItemsOff(holder); //láthatatlanná teszi
        }

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //item kattintásra jelenjenek meg a plussz információk
                pos.add(position);
                holderItemsOn(holder,position);
            }
        });

        holder.txt_city.setText(curCity.getName()); //Beállítom a város nevét és a hőmérsékletet
        holder.txt_temp.setText(curCity.getTemp());
    }

    @Override
    public int getItemCount() { //Az adapter listájának méretét kérem le
        return productList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder{ //A ViewHolder osztály

        //A város adataihoz tartozó View-k

        TextView txt_city;
        TextView txt_temp;
        TextView txt_temp_min;
        TextView txt_temp_max;
        TextView txt_wind;
        TextView txt_cloud;
        ImageView img_wind;
        ImageView img_cloud;
        ImageView img_min;
        ImageView img_max;
        CardView card;

        public ProductViewHolder(View itemView) {
            super(itemView);
            txt_city = itemView.findViewById(R.id.txt_city);
            txt_temp = itemView.findViewById(R.id.txt_temp);
            txt_temp_min = itemView.findViewById(R.id.txt_temp_min);
            txt_temp_max = itemView.findViewById(R.id.txt_temp_max);
            txt_wind = itemView.findViewById(R.id.txt_wind);
            txt_cloud = itemView.findViewById(R.id.txt_cloud);
            img_wind = itemView.findViewById(R.id.img_wind);
            img_cloud = itemView.findViewById(R.id.img_cloud);
            img_min = itemView.findViewById(R.id.img_min);
            img_max = itemView.findViewById(R.id.img_max);
            card = itemView.findViewById(R.id.card);
        }

    }

    public void setList(List<City> cities){ //Amikor keresek egy várost akkor a Mainactivty meghívja ezt a függvényt egy listával benne azokkal a városokkal amikbe benne van a keresett szöveg
        pos.clear();
        productList = cities; //Az adapter új listája a kapott listalesz
        notifyDataSetChanged(); //Értesítem az adatokat a változásról
    }

    private void holderItemsOn(ProductViewHolder holder,int position){ //Láthatóvá teszi a VIEW-kat az egy városhoz tartozó plussz információkhoz
        holder.txt_temp_min.setVisibility(View.VISIBLE);
        holder.txt_temp_max.setVisibility(View.VISIBLE);
        holder.txt_wind.setVisibility(View.VISIBLE);
        holder.txt_cloud.setVisibility(View.VISIBLE);
        holder.img_wind.setVisibility(View.VISIBLE);
        holder.img_cloud.setVisibility(View.VISIBLE);
        holder.img_min.setVisibility(View.VISIBLE);
        holder.img_max.setVisibility(View.VISIBLE);
        holder.txt_temp_min.setText(productList.get(position).getTemp_min());
        holder.txt_temp_max.setText(productList.get(position).getTemp_max());
        holder.txt_wind.setText(productList.get(position).getWind());
        holder.txt_cloud.setText(productList.get(position).getCloud());
    }

    private void holderItemsOff(ProductViewHolder holder){ //Láthatatlanná teszi a VIEW-kat az egy városhoz tartozó plussz információkhoz
        holder.txt_temp_min.setVisibility(View.GONE);
        holder.txt_temp_max.setVisibility(View.GONE);
        holder.txt_wind.setVisibility(View.GONE);
        holder.txt_cloud.setVisibility(View.GONE);
        holder.img_wind.setVisibility(View.GONE);
        holder.img_cloud.setVisibility(View.GONE);
        holder.img_min.setVisibility(View.GONE);
        holder.img_max.setVisibility(View.GONE);
        holder.txt_temp_min.setText("");
        holder.txt_temp_max.setText("");
        holder.txt_wind.setText("");
        holder.txt_cloud.setText("");
    }


}


