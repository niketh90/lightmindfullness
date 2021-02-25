package com.seraphic.lightapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.android.billingclient.api.Purchase;
import com.seraphic.lightapp.home_module.views.HomeBaseActivity;
import com.seraphic.lightapp.menuprofile.InappFragment;

import java.util.List;

public class BaseActivity extends AppCompatActivity {

    private BaseActivity context;
    private BillingClientLifecycle billingClientLifecycle;
    public boolean startRegister=false;
    private Purchase purchase;

    public static List<Purchase> purchasesData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this;




    }


   /* public void inBilling()
    {
        billingClientLifecycle = getBillingClientLifecycle();
        getLifecycle().addObserver(billingClientLifecycle);
        billingClientLifecycle.purchaseUpdateEvent.observe(context, new Observer<List<Purchase>>() {


            @Override
            public void onChanged(List<Purchase> purchases) {
                if (purchases != null) {
                    purchasesData=purchases;

                  //  registerPurchases(purchases);
                    if(!startRegister) {
                        registerPurchases(purchases);
                        startRegister=true;
                    }



                }
            }

        });

    }

    public void inBillingNew()
    {
        billingClientLifecycle = getBillingClientLifecycle();
        getLifecycle().addObserver(billingClientLifecycle);
        billingClientLifecycle.purchaseUpdateEvent.observe(context, new Observer<List<Purchase>>() {
            @Override
            public void onChanged(List<Purchase> purchases) {
                if (purchases != null) {

                        registerPurchasesNew(purchases);
                      //  startRegister=true;



                }
            }

        });

    }*/

   /* public void registerPurchases(List<Purchase> purchaseList) {
        if(purchaseList.size()>0)
        {
            for (Purchase purchase : purchaseList) {
                this.purchase=purchase;
                *//*if (purchase.isAutoRenewing()) {
                    Intent intent = new Intent(context, HomeBaseActivity.class);
                    startActivity(intent);
                    finish();
                } else {

                    Intent intent = new Intent(context, InappFragment.class);
                    startActivity(intent);
                    finish();
                }*//*

            }

            if (purchase.isAutoRenewing()) {
                Intent intent = new Intent(context, HomeBaseActivity.class);
                startActivity(intent);
                finish();
            } else {

                Intent intent = new Intent(context, InappFragment.class);
                startActivity(intent);
                finish();
            }

        }
        else
        {
            Intent intent = new Intent(context, InappFragment.class);
            startActivity(intent);
            finish();


        }




    }*/

    public void registerPurchasesNew(List<Purchase> purchaseList) {
        if(purchaseList.size()>0)
        {
            for (Purchase purchase : purchaseList) {
                if (!purchase.isAutoRenewing()) {
                    Intent intent = new Intent(context, InappFragment.class);
                    startActivity(intent);
                   // finish();
                }
            }
        }
        else
        {
            Intent intent = new Intent(context, InappFragment.class);
            startActivity(intent);
           // finish();


        }




    }

    /*public BillingClientLifecycle getBillingClientLifecycle() {
        return BillingClientLifecycle.getInstance(this);
    }*/
}
