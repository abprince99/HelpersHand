package com.exnovation.helpershand.Utilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.exnovation.helpershand.Activity.ActivityCart;
import com.exnovation.helpershand.Models.AddToCartModel;
import com.exnovation.helpershand.Models.AddToCartModelRemove;
import com.exnovation.helpershand.Models.AddToCartModel_new;
import com.exnovation.helpershand.Models.CartListModel;
import com.exnovation.helpershand.Models.CartListModel_sub_new;
import com.exnovation.helpershand.Models.CartListSubModel;
import com.exnovation.helpershand.Models.FabModel;
import com.exnovation.helpershand.Network.ApiManager;
import com.exnovation.helpershand.R;
import com.varunest.sparkbutton.SparkButton;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QtyUtilities extends AppCompatActivity {
    static Loader loader = new Loader();
    ApiManager manager = new ApiManager();
    public cartUpdate cartUpdate=null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
   /* public void addProductToCart(Context context, final String pId, final Button btnAdd, final LinearLayout qntLay, final TextView tvQty, final TextView totalCartCount, String products_attributes_id) {
        String rawData="{\n" +
                "\"products_id\": \""+pId+"\",\n" +
                "\"products_attributes_id\": "+products_attributes_id+"\n" +
                "}";
        RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), rawData);
        //Toast.makeText(context, ""+pId, Toast.LENGTH_SHORT).show();
        Log.d("eeeeeeeeABCD",pId+"\n"+products_attributes_id);
        loader.showDialog(context);
        manager.service.addToCart(bodyRequest).enqueue(new Callback<AddToCartModel_new>() {
            @Override
            public void onResponse(Call<AddToCartModel_new> call, Response<AddToCartModel_new> response) {
                loader.hideDialog();
                if (response.isSuccessful()){
                    loader.hideDialog();
                    try {
                        App.cartTotal = Integer.parseInt(response.body().cart_count);
                        if (totalCartCount!=null)setCartTotal(totalCartCount);
                    //    App.basket_id.put(pId,response.body().data.get(0).products.get(0).products.get(0).customers_basket_id);
                   //     App.basket_id_2.put(pId,response.body().data.get(0).products.get(0).products.get(0).customers_basket_id);

                    }catch (Exception e){}
                    try {
                        cartUpdate.cartUpdate(true);
                    }catch (Exception e){}
                    if (response.body().success == true){
                        btnAdd.setVisibility(View.GONE);
                        qntLay.setVisibility(View.VISIBLE);
                        tvQty.setText("1");
                        }
                }
            }

            @Override
            public void onFailure(Call<AddToCartModel_new> call, Throwable t) {
                loader.hideDialog();
                Log.d("eeeeeeee",""+t.getMessage());
            }
        });

    }*/
    public void addProductToCart(Context context, final String pId, final Button btnAdd, final LinearLayout qntLay, final TextView tvQty, final TextView totalCartCount, String products_attributes_id) {
        String rawData="{\n" +
                "\"products_id\": \""+pId+"\",\n" +
                "\"products_attributes_id\": "+products_attributes_id+"\n" +
                "}";
        RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), rawData);
        //Toast.makeText(context, ""+pId, Toast.LENGTH_SHORT).show();
        Log.d("eeeeeeeeABCD",pId+"\n"+products_attributes_id);

        loader.showDialog(context);
        manager.service.addToCart(bodyRequest).enqueue(new Callback<AddToCartModel_new>() {
            @Override
            public void onResponse(Call<AddToCartModel_new> call, Response<AddToCartModel_new> response) {
                loader.hideDialog();
                if (response.isSuccessful()){
                    loader.hideDialog();
                    try {
                        App.cartTotal = Integer.parseInt(response.body().cart_count);
                        if (totalCartCount!=null)setCartTotal(totalCartCount);
                        Log.d("eeeeeeeeABCD","DATA CHK  : "+response.body().data+"");
                        App.basket_id.put(pId,response.body().data.get(0).customers_basket_id);
                        App.basket_id_2.put(pId,response.body().data.get(0).customers_basket_id);
                        Log.d("eeeeeeeeABCD","ADD QTY : "+pId+"\n"+response.body().data.get(0).customers_basket_id);

                    }catch (Exception e){
                        Log.d("eeeeeeeeABCD","Error "+e.getMessage());
                    }
                    try {
                        cartUpdate.cartUpdate(true);
                    }catch (Exception e){}
                    if (response.body().success == true){
                        btnAdd.setVisibility(View.GONE);
                        qntLay.setVisibility(View.VISIBLE);
                        tvQty.setText("1");
                        }
                }
            }

            @Override
            public void onFailure(Call<AddToCartModel_new> call, Throwable t) {
                loader.hideDialog();
                Log.d("eeeeeeee",""+t.getMessage());
            }
        });

    }

    public void incrProductToCart(Context context,String pId, final int qnt, final TextView tvQty, final TextView totalCartCount,  String products_attributes_id) {

        String rawData="{\n" +
                "\"products_id\": \""+pId+"\",\n" +
                "\"customers_basket_id\": "+products_attributes_id+"\n" +
                "}";
        loader.hideDialog();
        loader.showDialog(context);
        RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), rawData);
        Log.d("pppDataKol",rawData);
        manager.service.incrToCart(bodyRequest).enqueue(new Callback<AddToCartModel>() {
            @Override
            public void onResponse(Call<AddToCartModel> call, Response<AddToCartModel> response) {
                if (response.isSuccessful()){
                    loader.hideDialog();
                    try {
                        App.cartTotal = Integer.parseInt(response.body().cart_count);
                        if (totalCartCount!=null)setCartTotal(totalCartCount);

                    }catch (Exception e){}
                    if (response.body().success == true){
                        tvQty.setText(String.valueOf(CartFunction.setIncrement(qnt)));
                        try {
                            cartUpdate.cartUpdate(true);
                        }catch (Exception e){}
                        }

                }
            }

            @Override
            public void onFailure(Call<AddToCartModel> call, Throwable t) {
                loader.hideDialog();

            }
        });

    }
    public void decrProductToCart(Context context,String pId, final int qnt, final TextView tvQty, final LinearLayout qntLay, final Button btnAdd, final TextView totalCartCount,  String products_attributes_id) {
        loader.hideDialog();
        loader.showDialog(context);
        Callback<AddToCartModel> callback=new Callback<AddToCartModel>() {
            @Override
            public void onResponse(Call<AddToCartModel> call, Response<AddToCartModel> response) {
                if (response.isSuccessful()){
                    loader.hideDialog();
                    if (response.body().success == true){
                        try {
                            App.cartTotal = Integer.parseInt(response.body().cart_count);
                            if (totalCartCount!=null)setCartTotal(totalCartCount);

                        }catch (Exception e){}
                        if (qnt == 1){
                            qntLay.setVisibility(View.GONE);
                            btnAdd.setVisibility(View.VISIBLE);
                        }else {
                            tvQty.setText(String.valueOf(CartFunction.setDecrement(qnt)));
                        }
                        try {
                            cartUpdate.cartUpdate(true);
                        }catch (Exception e){}
                    }

                }
            }

            @Override
            public void onFailure(Call<AddToCartModel> call, Throwable t) {
                loader.hideDialog();
               }
        };
        manager.service.decrToCart(pId,products_attributes_id).enqueue(callback);
    }
    public void decrProductFromCart(Context context,String pId, final int qnt, final TextView tvQty, final LinearLayout qntLay, final TextView totalCartCount,  String products_attributes_id) {
        loader.hideDialog();
        loader.showDialog(context);
        Callback<AddToCartModel> callback=new Callback<AddToCartModel>() {
            @Override
            public void onResponse(Call<AddToCartModel> call, Response<AddToCartModel> response) {
                if (response.isSuccessful()){
                    loader.hideDialog();
                    try {
                        App.cartTotal = Integer.parseInt(response.body().cart_count);
                        if (totalCartCount!=null)setCartTotal(totalCartCount);

                    }catch (Exception e){}
                    if (response.body().success == true){
                        tvQty.setText(String.valueOf(CartFunction.setDecrement(qnt)));
                        try {
                            cartUpdate.cartUpdate(true);
                        }catch (Exception e){}
                    }

                }
            }

            @Override
            public void onFailure(Call<AddToCartModel> call, Throwable t) {
                loader.hideDialog();
            }
        };
        manager.service.decrToCart(pId,products_attributes_id).enqueue(callback);
    }
    public void removeCart(Context context,String pId, final int qnt, final TextView tvQty, final LinearLayout qntLay, final Button btnAdd, final TextView totalCartCount,  String products_attributes_id) {
        loader.showDialog(context);
        String rawData="{\n" +
                "\t\"customers_basket_id\": "+products_attributes_id+"\n" +
                "}";
        Log.d("removeDatar","B  : "+rawData);
        RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), rawData);
        Callback<AddToCartModelRemove> callback=new Callback<AddToCartModelRemove>() {
            @Override
            public void onResponse(Call<AddToCartModelRemove> call, Response<AddToCartModelRemove> response) {
                if (response.isSuccessful()){
                    loader.hideDialog();
                    try {
                        App.cartTotal = Integer.parseInt(response.body().cart_count);
                        if (totalCartCount!=null)setCartTotal(totalCartCount);

                    }catch (Exception e){}
                    Log.d("removeDatar","QTY  : "+qnt);
                    if (response.body().success == true){

                       if (qnt == 1){
                            qntLay.setVisibility(View.GONE);
                            btnAdd.setVisibility(View.VISIBLE);
                        }else {
                            tvQty.setText(String.valueOf(CartFunction.setDecrement(qnt)));
                        }

                    }

                }
            }

            @Override
            public void onFailure(Call<AddToCartModelRemove> call, Throwable t) {
                loader.hideDialog();
            }
        };
        manager.service.removeFromCart(bodyRequest).enqueue(callback);
    }
    public void removeCartFromCartPage(final Context context, String pId, final List<CartListModel_sub_new> list, final ActivityCart.CartAdapter adapter, final int position, String products_attributes_id) {
        loader.hideDialog();
        loader.showDialog(context);
        String rawData="{\n" +
                "\t\"customers_basket_id\": "+products_attributes_id+"\n" +
                "}";
        Log.d("removeDatar","A  : "+rawData);
        RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), rawData);
        Callback<AddToCartModelRemove> callback=new Callback<AddToCartModelRemove>() {
            @Override
            public void onResponse(Call<AddToCartModelRemove> call, Response<AddToCartModelRemove> response) {
                loader.hideDialog();
                if (response.isSuccessful()){
                    //loader.hideDialog();
                    try {
                        App.cartTotal = Integer.parseInt(response.body().cart_count);
                        if (response.body().success == true){
                            list.remove(position);
                            adapter.notifyItemRemoved(position);
                            adapter.notifyItemRangeChanged(position,list.size());
                            try {
                                cartUpdate.cartUpdate(true);
                            }catch (Exception e){}

                        }
                    }catch (Exception e){}


                }
                else {
                   // loader.hideDialog();
                }
            }

            @Override
            public void onFailure(Call<AddToCartModelRemove> call, Throwable t) {
                loader.hideDialog();
            }
        };
        manager.service.removeFromCart(bodyRequest).enqueue(callback);
    }
    public  ImageView init(Activity ctx, final int id)
    {

            ImageView imageView=ctx.findViewById(id);
            return imageView;

    }
public interface cartUpdate{
public  void cartUpdate(Boolean bool);
}
void setCartTotal(final TextView textView){
    textView.postDelayed(new Runnable() {
        @Override
        public void run() {
            textView.setText(App.cartTotal+"");
            Log.d("cartABC","A "+App.cartTotal);
        }
    },100);
}
}
