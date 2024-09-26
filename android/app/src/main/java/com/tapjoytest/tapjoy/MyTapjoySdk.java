package com.tapjoytest.tapjoy;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.tapjoy.TJActionRequest;
import com.tapjoy.TJAwardCurrencyListener;
import com.tapjoy.TJConnectListener;
import com.tapjoy.TJEarnedCurrencyListener;
import com.tapjoy.TJError;
import com.tapjoy.TJGetCurrencyBalanceListener;
import com.tapjoy.TJPlacement;
import com.tapjoy.TJPlacementListener;
import com.tapjoy.TJSegment;
import com.tapjoy.TJSetUserIDListener;
import com.tapjoy.TJSpendCurrencyListener;
import com.tapjoy.Tapjoy;
import com.tapjoy.TapjoyConnectFlag;
import java.util.Hashtable;


import io.flutter.Log;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.embedding.engine.FlutterEngine;


public class MyTapjoySdk implements ActivityAware {
    MethodChannel channel;
    private Hashtable<String, TJPlacement> placements = new Hashtable<>();
    private Activity activity;

    public MyTapjoySdk(Activity activity,FlutterEngine flutterEngine,Context applicationContext,String userID,String sdkKey,boolean isDebugMod){
        this.activity=activity;
        Hashtable<String, Object> connectFlags = new Hashtable<String, Object>();
        connectFlags.put(TapjoyConnectFlag.ENABLE_LOGGING, isDebugMod? "true":"false"); // Disable this in production builds
        connectFlags.put(TapjoyConnectFlag.USER_ID, userID); // Important for self-managed currency
        channel = new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(),
                "TapjoySDK");
        Tapjoy.connect(applicationContext, sdkKey, connectFlags, new TJConnectListener() {
            @Override
            public void onConnectSuccess() {
                channel.invokeMethod("connectionResult","Connected");
            }
            @Override
            public void onConnectWarning(int code, String message) {
                channel.invokeMethod("connectionResult","Warning");
            }
            @Override
            public void onConnectFailure(int code, String message) {
                channel.invokeMethod("connectionResult","Failure");
            }
        });
        Tapjoy.setEarnedCurrencyListener(new TJEarnedCurrencyListener() {
            @Override
            public void onEarnedCurrency(String currencyName, int amount) {
                Hashtable<String, Object> getCurrencyResponse = new Hashtable<>();
                getCurrencyResponse.put("currencyName", currencyName);
                getCurrencyResponse.put("earnedAmount", amount);
                invokeMethod("onEarnedCurrency", getCurrencyResponse);
            }
            });
    }

    public boolean setUserSegment(String value){
        TJSegment segment = TJSegment.valueOf(value.toUpperCase());
        Tapjoy.setUserSegment(segment);
        return  true;
    }
    public boolean setMaxLevel(int value){
        Tapjoy.setMaxLevel(value);
        return  true;
    }

    public boolean setUserLevel(int value){
        Tapjoy.setUserLevel(value);
        return  true;
    }

    public void createPlacement(String placementName){
        TJPlacementListener placementListener = new TJPlacementListener() {

            @Override
            public void onRequestSuccess(TJPlacement tjPlacement) {
                final Hashtable<String, Object> myMap = new Hashtable<>();
                myMap.put("placementName", tjPlacement.getName());
            invokeMethod("requestSuccess",myMap);
            }

            @Override
            public void onRequestFailure(TJPlacement tjPlacement, TJError tjError) {
                final Hashtable<String, Object> myMap = new Hashtable<>();
                myMap.put("placementName", tjPlacement.getName());
                myMap.put("error", tjError.message);
                invokeMethod("requestFail", myMap);
            }

            @Override
            public void onContentReady(TJPlacement tjPlacement) {
                final Hashtable<String, Object> myMap = new Hashtable<>();
                myMap.put("placementName", tjPlacement.getName());
                invokeMethod("contentReady", myMap);
            }

            @Override
            public void onContentShow(TJPlacement tjPlacement) {
                final Hashtable<String, Object> myMap = new Hashtable<>();
                myMap.put("placementName", tjPlacement.getName());
               invokeMethod("contentDidAppear", myMap);
            }

            @Override
            public void onContentDismiss(TJPlacement tjPlacement) {
                final Hashtable<String, Object> myMap = new Hashtable<>();
                myMap.put("placementName", tjPlacement.getName());
                invokeMethod("contentDidDisAppear", myMap);
            }

            @Override
            public void onPurchaseRequest(TJPlacement tjPlacement, TJActionRequest tjActionRequest, String s) {

            }

            @Override
            public void onRewardRequest(TJPlacement tjPlacement, TJActionRequest tjActionRequest, String s, int i) {

            }

            @Override
            public void onClick(TJPlacement tjPlacement) {
                final Hashtable<String, Object> myMap = new Hashtable<>();
                myMap.put("placementName", tjPlacement.getName());
                invokeMethod("clicked", myMap);
            }
        };
        TJPlacement p = Tapjoy.getPlacement(placementName, placementListener);
        placements.put(placementName, p);
    }
    public boolean setUserID(String userID){
        Tapjoy.setUserID(userID,new TJSetUserIDListener(){});
        return true;
    }
    public void requestContent(String placementName){
        final TJPlacement tjPlacementRequest = placements.get(placementName);
        if (tjPlacementRequest != null) {
            tjPlacementRequest.requestContent();
        } else {
            final Hashtable<String, Object> myMap = new Hashtable<>();
            myMap.put("placementName", placementName);
            myMap.put("error", "Placement Not Found, Please Add placement first");
            channel.invokeMethod("requestFail", myMap);
        }
    }
    public void showPlacement(String placementName){
        final TJPlacement tjPlacementShow = placements.get(placementName);
        if(tjPlacementShow != null) {
            tjPlacementShow.showContent();
        }else {
            Log.e("FlutterTapjoy", "Error: Placement Not Found, Please Add placement first" );
        }
    }
    public  void  getCurrencyBalance(){
        Tapjoy.getCurrencyBalance(new TJGetCurrencyBalanceListener() {
            @Override
            public void onGetCurrencyBalanceResponse(String currencyName, int balance) {
                Hashtable<String, Object> getCurrencyResponse = new Hashtable<>();
                getCurrencyResponse.put("currencyName", currencyName);
                getCurrencyResponse.put("balance", balance);
                channel.invokeMethod("onGetCurrencyBalanceResponse", getCurrencyResponse);
            }

            @Override
            public void onGetCurrencyBalanceResponseFailure(String error) {
                Hashtable<String, Object> getCurrencyResponse = new Hashtable<>();
                getCurrencyResponse.put("error", error);
                channel.invokeMethod("onGetCurrencyBalanceResponseFailure", getCurrencyResponse);
            }
        });
    }
    public  void onSpendCurrency(int amount){
        Tapjoy.spendCurrency(amount, new TJSpendCurrencyListener() {
            @Override
            public void onSpendCurrencyResponse(String currencyName, int balance) {
                Hashtable<String, Object> spendCurrencyResponse = new Hashtable<>();
                spendCurrencyResponse.put("currencyName", currencyName);
                spendCurrencyResponse.put("balance", balance);
                channel.invokeMethod("onSpendCurrencyResponse", spendCurrencyResponse);
            }

            @Override
            public void onSpendCurrencyResponseFailure(String error) {
                Hashtable<String, Object> spendCurrencyResponse = new Hashtable<>();
                spendCurrencyResponse.put("error", error);
                channel.invokeMethod("onSpendCurrencyResponseFailure", spendCurrencyResponse);
            }
        });
    }
    public  void  awardCurrency(int amount){
        Tapjoy.awardCurrency(amount, new TJAwardCurrencyListener() {
            @Override
            public void onAwardCurrencyResponse(String currencyName, int balance) {
                Hashtable<String, Object> awardCurrencyResponse = new Hashtable<>();
                awardCurrencyResponse.put("currencyName", currencyName);
                awardCurrencyResponse.put("balance", balance);
                invokeMethod("onAwardCurrencyResponse", awardCurrencyResponse);
            }

            @Override
            public void onAwardCurrencyResponseFailure(String error) {
                Hashtable<String, Object> awardCurrencyResponse = new Hashtable<>();
                awardCurrencyResponse.put("error", error);
                invokeMethod("onAwardCurrencyResponseFailure", awardCurrencyResponse);
            }
        });
    }

    /////////////////////////////////
    private void invokeMethod(@NonNull final String methodName, final Hashtable<String, Object> data) {
        try {
            activity.runOnUiThread(() -> channel.invokeMethod(methodName, data));
        } catch (final Exception e) {
            Log.e("FlutterTapjoy", "Error " + e.toString());
        }
    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        activity = binding.getActivity();
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {

    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
        activity = binding.getActivity();
    }

    @Override
    public void onDetachedFromActivity() {

    }
}
