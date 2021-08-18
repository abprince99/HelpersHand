package com.exnovation.helpershand.Utilities;

import android.content.Context;

public class CartFunction {
    Context context;
    public CartFunction(Context context) {
        this.context = context;
    };
    public static int setIncrement (int count){
        count = count + 1;
        return count;
    }
    public static int setDecrement (int count){
        count = count - 1;
        return count;
    }
}
