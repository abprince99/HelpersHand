package com.exnovation.helpershand.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.exnovation.helpershand.Models.CartListModel;
import com.exnovation.helpershand.Models.CartListModel_new;
import com.exnovation.helpershand.Models.CartListModel_sub_new;
import com.exnovation.helpershand.Models.CartListSubModel;
import com.exnovation.helpershand.Models.PlaceOrderModel;
import com.exnovation.helpershand.Network.ApiManager;
import com.exnovation.helpershand.R;
import com.exnovation.helpershand.Utilities.App;
import com.exnovation.helpershand.Utilities.Loader;
import com.exnovation.helpershand.Utilities.Nav;
import com.exnovation.helpershand.Utilities.Prefs;
import com.exnovation.helpershand.Utilities.QtyUtilities;
import com.google.android.gms.dynamic.IFragmentWrapper;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityCart extends AppCompatActivity implements QtyUtilities.cartUpdate, PaymentResultListener {
    RecyclerView cartRv;
    List<CartListModel_new> cartList = new ArrayList<>();
    ApiManager manager = new ApiManager();
    Loader loader = new Loader();
    Prefs prefs;
    Button btnPlaceOrder;
    TextView tvCartItems, tvSubTotal, tvCartVal, tvDiscount, tvDelChrg, tvGrandTotal;
    CartAdapter adapter;
    QtyUtilities qtyUtilities = new QtyUtilities();
    LinearLayout emptyLay, cartLay;

    LinearLayout addressLay, addChngLay;
    CheckBox chkBox;
    TextView tvAdd, tvSetAdd, tvChange;
    String ShippingId = "";
    String ShippingAdd = "";
    Dialog dialog;
    String finalAmount = "";

    private Checkout chackout;
    private String razorpayKey;
    String transId="";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        qtyUtilities.cartUpdate = this;
        prefs = new Prefs(ActivityCart.this);
        cartRv = findViewById(R.id.cart_rv);
        btnPlaceOrder = findViewById(R.id.btn_place_order);
        tvCartItems = findViewById(R.id.tv_tot_items);
        tvSubTotal = findViewById(R.id.tv_sub_tot);
        tvCartVal = findViewById(R.id.tv_cart_val);
        tvDiscount = findViewById(R.id.tv_dis);
        tvDelChrg = findViewById(R.id.tv_del_chrg);
        tvGrandTotal = findViewById(R.id.tv_grand_tot);
        emptyLay = findViewById(R.id.empty_ll);
        cartLay = findViewById(R.id.cart_ll);
        addressLay = findViewById(R.id.add_lay);
        addChngLay = findViewById(R.id.add_chng_lay);
        chkBox = findViewById(R.id.chk_box);
        tvAdd = findViewById(R.id.tv_add);
        tvSetAdd = findViewById(R.id.tv_address);
        tvChange = findViewById(R.id.tv_chng);

        ShippingId = prefs.getData("addID");
        makeAPopup();
       /* if (prefs.getData("Address").equals("") || prefs.getData("Address") == null){
            addressLay.setVisibility(View.VISIBLE);
            addChngLay.setVisibility(View.GONE);
        }else {
            ShippingAdd = prefs.getData("Address");
            tvSetAdd.setText(ShippingAdd);
            addressLay.setVisibility(View.GONE);
            addChngLay.setVisibility(View.VISIBLE);
        }*/

        //Nav.nav(this,HomeActivity.class, new QtyUtilities().init(this,R.id.ic_back));
        ((ImageView) findViewById(R.id.ic_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getCartData();

        if (prefs.getData("Old_acc").equals("1")){
            addressLay.setVisibility(View.GONE);
            addChngLay.setVisibility(View.VISIBLE);
            if (prefs.getData("Address") == null || prefs.getData("Address").equals("")){
                ShippingAdd = "";
            }else{
                ShippingAdd = prefs.getData("Address");
                tvSetAdd.setText(ShippingAdd);
            }
        }else {
            addressLay.setVisibility(View.VISIBLE);
            addChngLay.setVisibility(View.GONE);

        }

        if (App.isAddressSelected == true){
            App.isAddressSelected = false;
            addressLay.setVisibility(View.GONE);
            addChngLay.setVisibility(View.VISIBLE);
            tvSetAdd.setText(ShippingAdd);
        }

        tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ActivityCart.this, AddAddress.class);
                App.isFromCart = true;
                startActivity(i);
                finish();
            }
        });
        tvChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ActivityCart.this, AddAddress.class);
                App.isFromCart = true;
                startActivity(i);
                finish();
            }
        });
        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ShippingId.equals("") || ShippingId == null){

                }else {
                    if(addressLay.getVisibility() == View.VISIBLE){
                        Toast.makeText(ActivityCart.this, "Please select your address.", Toast.LENGTH_SHORT).show();
                    }else{
                        dialog.show();
                    }

                }

            }
        });
    }

    private void makeAPopup() {
        dialog = new Dialog(ActivityCart.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.popup_payment_lay);
        dialog.setCancelable(false);
        final TextView tvCash = (TextView) dialog.findViewById(R.id.tv_cash);
        final TextView tvOnline = (TextView) dialog.findViewById(R.id.tv_online);
        final LinearLayout ivClose = (LinearLayout) dialog.findViewById(R.id.iv_close);

        ivClose.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
            }
        });

        tvCash.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                callForOrder(ShippingId, ShippingId, "flateRate", "Cash on Delivery", "");
                dialog.dismiss();
            }
        });

        tvOnline.setOnClickListener(
        new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String convertedAmount=String.valueOf(Double.parseDouble(finalAmount)*100);
                rezorpayCall(convertedAmount);
                dialog.dismiss();

            }
        });

    }

    private void rezorpayCall(String convertedAmount)
    {
        //razorpayKey="rzp_test_Iih2OPw6MYfvKt"; //Generate your razorpay key from Settings-> API Keys-> copy Key Id
        razorpayKey="rzp_live_2TmRtiyMBIxkOB";
        chackout = new Checkout();
        chackout.setKeyID(razorpayKey);

        try {
            JSONObject options = new JSONObject();
            //options.put("name", name);
            //options.put("description", "Razorpay Payment Test");
            options.put("currency", "INR");
            options.put("amount", convertedAmount);

            JSONObject preFill = new JSONObject();
            // preFill.put("email", email);
            // preFill.put("contact", phNo);
            options.put("prefill", preFill);

            chackout.open(ActivityCart.this, options);
        }
        catch (Exception e)
        {
            Toast.makeText(ActivityCart.this, "Error in payment: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }

    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {

        Toast.makeText(ActivityCart.this, "Transaction Successful: " + razorpayPaymentID, Toast.LENGTH_LONG).show();
        transId = razorpayPaymentID;
       // Log.d("AAA", " " + finalCartItem);
        //makeOrder(uid, finalCartItem, delAddressId, billAddressId, time, transId);
        callForOrder(ShippingId, ShippingId, "flateRate", "online", transId);

    }

    @Override
    public void onPaymentError(int i, String error) {

        Toast.makeText(ActivityCart.this, "Transaction unsuccessful: "+ error , Toast.LENGTH_LONG).show();

    }

    private void callForOrder(String billingId, String shippingId, String shippingMethod,
                              String paymentMethod, String tId) {

        loader.showDialog(ActivityCart.this);
        manager.service.placeOrder(billingId, shippingId, shippingMethod, paymentMethod, tId)
                .enqueue(new Callback<PlaceOrderModel>() {
            @Override
            public void onResponse(Call<PlaceOrderModel> call, Response<PlaceOrderModel> response) {
                if (response.isSuccessful()){
                    loader.hideDialog();
                    if (response.body().success){
                       // prefs.saveData("Old_acc", "1");
                        App.cartTotal=0;
                        Toast.makeText(ActivityCart.this, response.body().message, Toast.LENGTH_SHORT).show();
                        finish();
                    }else{
                        Toast.makeText(ActivityCart.this, response.body().message, Toast.LENGTH_SHORT).show();
                    }
                }else{
                   loader.hideDialog();
                   Toast.makeText(ActivityCart.this, "Server is busy! Please try again!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PlaceOrderModel> call, Throwable t) {

                loader.hideDialog();
                Toast.makeText(ActivityCart.this, "Please check your internet connection and try again!", Toast.LENGTH_SHORT).show();

            }
        });

    }

    public void getCartData() {
        loader.showDialog(ActivityCart.this);
        manager.service.getCartItems().enqueue(new Callback<CartListModel_new>() {
            @Override
            public void onResponse(Call<CartListModel_new> call, Response<CartListModel_new> response) {

                if (response.isSuccessful()) {
                    loader.hideDialog();
                    if (response.body().success = true) {
                        CartListModel clm = new CartListModel();
                        //cartList = response.body().data;

                        //cartList = clm.data;
                        tvCartVal.setText(response.body().sub_total);
                        tvDiscount.setText(response.body().discount);
                        tvDelChrg.setText(response.body().dlv_fee);
                        tvGrandTotal.setText(response.body().final_total);
                        finalAmount = response.body().final_total;
                        tvCartItems.setText(response.body().cart_count);
                        tvSubTotal.setText(response.body().sub_total);
                        if (adapter != null) adapter.notifyDataSetChanged();
                        if (response.body().data.size() > 0) {
                            cartLay.setVisibility(View.VISIBLE);
                            emptyLay.setVisibility(View.GONE);
                            adapter = new CartAdapter(ActivityCart.this, response.body().data);
                            cartRv.setLayoutManager(new LinearLayoutManager(ActivityCart.this));
                            cartRv.setAdapter(adapter);
                            //adapter.notifyDataSetChanged();
                        } else {
                            loader.hideDialog();
                            cartLay.setVisibility(View.GONE);
                            emptyLay.setVisibility(View.VISIBLE);
                            //finish();
                        }

                    } else {
                        cartLay.setVisibility(View.GONE);
                        emptyLay.setVisibility(View.VISIBLE);
                        Toast.makeText(ActivityCart.this, "Something went wrong! Try again!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    loader.hideDialog();
                    cartLay.setVisibility(View.GONE);
                    emptyLay.setVisibility(View.VISIBLE);
                    //finish();
                    //Toast.makeText(ActivityCart.this, "No response from the server! Try again!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CartListModel_new> call, Throwable t) {
                loader.hideDialog();
                Toast.makeText(ActivityCart.this, "Please check your internet connection and try again!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void cartUpdate(Boolean bool) {
        if (bool) getCartData();
    }


    public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
        Context context;
        List<CartListModel_sub_new> list;
        AlertDialog.Builder builder;
        CartListModel_sub_new clsm;

        public CartAdapter(ActivityCart activityCart, List<CartListModel_sub_new> cartList) {
            this.context = activityCart;
            this.list = cartList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View listItem = layoutInflater.inflate(R.layout.cart_items, parent, false);
            ViewHolder viewHolder = new ViewHolder(listItem);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

            clsm = list.get(position);
            builder = new AlertDialog.Builder(ActivityCart.this);

            holder.textView.setText(list.get(position).products_name);
           // holder.textView.setText(clsm.);

            Glide.with(context).load(list.get(position).products_image)
                    .skipMemoryCache(true).into(holder.imageView);
            /*if (list.get(position).products_weight_unit == null) {
                list.get(position).products_weight_unit = "";
            }*/
            holder.tvWeight.setText(clsm.products_options_name + " - " + clsm.products_options_values_name);
            holder.tvNewPrice.setText(list.get(position).products_price);
            if (list.get(position).old_price == null) {
                list.get(position).old_price = "";
            }
            holder.tvOldPrice.setText(list.get(position).old_price);
            Log.d("pppdataCCC",list.get(position).cart_quantity+"A");
            holder.tvCount.setText(list.get(position).cart_quantity);
            holder.ivMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String pId = list.get(position).products_id;
                    int qnt = Integer.valueOf(holder.tvCount.getText().toString());
                    if (qnt == 1) {
                        qtyUtilities.removeCartFromCartPage(ActivityCart.this, pId, list, adapter, holder.getAdapterPosition(),list.get(position).customers_basket_id);
                        //getCartData();
                    } else {
                        qtyUtilities.decrProductFromCart(context, pId, qnt, holder.tvCount, holder.qntLay, null,list.get(position).customers_basket_id);
                        //getCartData();
                    }
                }

            });
            holder.ivPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String pId = list.get(position).products_id;;
                    int qnt = Integer.valueOf(holder.tvCount.getText().toString());
                    qtyUtilities.incrProductToCart(context, pId, qnt, holder.tvCount, null,list.get(position).customers_basket_id);
                    //getCartData();
                }
            });
           /* holder.btn_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String pId = clsm.products_id;
                    qtyUtilities.addProductToCart(context,pId,holder.btn_add,holder.qntLay,holder.tvCount);
                }
            });*/
            holder.ivRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    builder.setTitle("Remove from cart");

                    //Setting message manually and performing action on button click
                    builder.setMessage("Do you want to remove this product ?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    String pId = list.get(position).products_id;;

                                    /*if (list.size()==1){

                                    }
                                    qtyUtilities.removeCartFromCartPage(ActivityCart.this, pId, list, adapter, holder.getAdapterPosition());
                                    if (pId.equals("1"))
                                    getCartData();
*/
                                    if(list.size()==1){
                                        qtyUtilities.removeCartFromCartPage(ActivityCart.this, pId, list, adapter, holder.getAdapterPosition(),list.get(position).customers_basket_id);
                                       // qtyUtilities.removeCartFromCartPage(ActivityCart.this, pId, list, adapter, holder.getAdapterPosition(),list.get(position).customers_basket_id);
                                        //getCartData();
                                    }else{
                                        qtyUtilities.removeCartFromCartPage(ActivityCart.this, pId, list, adapter, holder.getAdapterPosition(),list.get(position).customers_basket_id);
                                        //qtyUtilities.removeCartFromCartPage(ActivityCart.this, pId, list, adapter, holder.getAdapterPosition(),list.get(position).customers_basket_id);
                                    }

                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    //Setting the title manually
                    alert.show();

                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView, ivRemove, ivPlus, ivMinus;
            public TextView textView, tvWeight, tvOldPrice, tvNewPrice, tvCount;
            public LinearLayout qntLay;
            public RelativeLayout relativeLayout;

            //public Button btn_add;
            public ViewHolder(View itemView) {
                super(itemView);
                this.imageView = (ImageView) itemView.findViewById(R.id.imageView);
                this.ivRemove = (ImageView) itemView.findViewById(R.id.iv_remove);
                this.ivPlus = (ImageView) itemView.findViewById(R.id.iv_plus);
                this.ivMinus = (ImageView) itemView.findViewById(R.id.iv_minus);
                this.textView = (TextView) itemView.findViewById(R.id.textView);
                this.tvWeight = (TextView) itemView.findViewById(R.id.tv_weight);
                this.tvOldPrice = (TextView) itemView.findViewById(R.id.tv_old_price);
                this.tvNewPrice = (TextView) itemView.findViewById(R.id.tv_new_price);
                this.tvCount = (TextView) itemView.findViewById(R.id.tv_count);
                this.qntLay = itemView.findViewById(R.id.qntLay);
                // this.btn_add =  itemView.findViewById(R.id.btn_add);
                relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relativeLayout);
            }
        }
    }

}