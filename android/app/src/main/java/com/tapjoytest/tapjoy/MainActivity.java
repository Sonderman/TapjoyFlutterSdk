package com.tapjoytest.tapjoy;
import androidx.annotation.NonNull;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public class MainActivity extends FlutterActivity {
    FlutterEngine flutterEngine;
    MyTapjoySdk tapjoySdk;

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);
        this.flutterEngine = flutterEngine;
         new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), "TapjoySDK").setMethodCallHandler(
                this::onMethodCall
        );
    }

    private void onMethodCall(MethodCall call, MethodChannel.Result result) {
        switch (call.method) {
            case "initTapjoySDK":
                this.tapjoySdk = new MyTapjoySdk(this.getActivity(),flutterEngine,getApplicationContext(),call.argument("userID"),call.argument("sdkKey"), Boolean.TRUE.equals(call.argument("isDebug")));
                result.success(true);
                break;
            case "setUserSegment":
                result.success(tapjoySdk.setUserSegment(call.argument("segment")));
                break;
            case "setMaxLevel":
                result.success(tapjoySdk.setMaxLevel(call.argument("maxlevel")));
                break;
            case "setUserLevel":
                result.success(tapjoySdk.setUserLevel(call.argument("level")));
                break;
            case "createPlacement":
                tapjoySdk.createPlacement(call.argument("placementName"));
                result.success(true);
                break;
            case "requestContent":
                tapjoySdk.requestContent(call.argument("placementName"));
                result.success(true);
                break;
            case "showPlacement":
                tapjoySdk.showPlacement(call.argument("placementName"));
                result.success(true);
                break;
            case "setUserID":
                result.success(tapjoySdk.setUserID(call.argument("userID")));
                break;
            case "getCurrencyBalance" :
                tapjoySdk.getCurrencyBalance();
                result.success(true);
                break;
            case "onSpendCurrency" :
                tapjoySdk.onSpendCurrency(call.argument("amount"));
                result.success(true);
                break;
            case "awardCurrency":
                tapjoySdk.awardCurrency(call.argument("amount"));
                break;
            default:
                result.notImplemented();
                break;
        }
    }
}
