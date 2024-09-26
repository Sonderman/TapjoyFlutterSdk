import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

enum TJSegment { vip, payer, nonPayer, unknown }

class TapjoyService {
  final MethodChannel _channel = const MethodChannel('TapjoySDK');
  static bool didSdkConnected = false;
  bool isAnyContentReadyToShow = false;
  final void Function(String result)? _onConnectionResult;
  final void Function(String currencyName, int amount) _onEarnedCurrency;
  final void Function(String placementName)? _onclicked;
  final void Function(String placementName)? _onRequestSuccess;
  final void Function(String placementName, String error)? _onRequestFailure;
  final void Function(String placementName)? _onContentReady;
  final void Function(String placementName)? _onContentShow;
  final void Function(String placementName)? _onContentDismiss;
  final void Function(String currencyName, int balance)
      _onGetCurrencyBalanceResponse;
  final void Function(String error)? _onGetCurrencyBalanceResponseFailure;
  final void Function(String currencyName, int balance)
      _onSpendCurrencyResponse;
  final void Function(String error)? _onSpendCurrencyResponseFailure;
  final void Function(String currencyName, int balance)
      _onAwardCurrencyResponse;
  final void Function(String error)? _onAwardCurrencyResponseFailure;

  TapjoyService(
      {void Function(String result)? onConnectionResult,
      required void Function(String currencyName, int amount) onEarnedCurrency,
      void Function(String placementName)? onclicked,
      void Function(String placementName)? onRequestSuccess,
      void Function(String placementName, String error)? onRequestFailure,
      void Function(String placementName)? onContentShow,
      void Function(String placementName)? onContentReady,
      void Function(String placementName)? onContentDismiss,
      required void Function(String currencyName, int balance)
          onGetCurrencyBalanceResponse,
      void Function(String)? onGetCurrencyBalanceResponseFailure,
      required void Function(String currencyName, int balance)
          onSpendCurrencyResponse,
      void Function(String error)? onSpendCurrencyResponseFailure,
      required void Function(String currencyName, int balance)
          onAwardCurrencyResponse,
      void Function(String error)? onAwardCurrencyResponseFailure})
      : _onAwardCurrencyResponseFailure = onAwardCurrencyResponseFailure,
        _onAwardCurrencyResponse = onAwardCurrencyResponse,
        _onSpendCurrencyResponseFailure = onSpendCurrencyResponseFailure,
        _onSpendCurrencyResponse = onSpendCurrencyResponse,
        _onGetCurrencyBalanceResponseFailure =
            onGetCurrencyBalanceResponseFailure,
        _onGetCurrencyBalanceResponse = onGetCurrencyBalanceResponse,
        _onContentDismiss = onContentDismiss,
        _onContentShow = onContentShow,
        _onContentReady = onContentReady,
        _onRequestFailure = onRequestFailure,
        _onRequestSuccess = onRequestSuccess,
        _onclicked = onclicked,
        _onEarnedCurrency = onEarnedCurrency,
        _onConnectionResult = onConnectionResult {
    _channel.setMethodCallHandler(
      (call) async {
        switch (call.method) {
          case 'connectionResult':
            String data = call.arguments as String;
            if (kDebugMode) {
              print('Connection Result: $data');
            }
            if (data.contains("Connected")) {
              didSdkConnected = true;
            }
            _onConnectionResult?.call(data);
            break;
          case "onEarnedCurrency":
            if (kDebugMode) {
              print(call.arguments);
            }
            _onEarnedCurrency.call(call.arguments["currencyName"] as String,
                call.arguments["amount"] as int);
            break;
          case "onClicked":
            _onclicked?.call(call.arguments["placementName"] as String);
            break;
          case "requestSuccess":
            _onRequestSuccess?.call(call.arguments["placementName"] as String);
            break;
          case "requestFail":
            _onRequestFailure?.call(call.arguments["placementName"] as String,
                call.arguments["error"] as String);
            break;
          case "contentReady":
            isAnyContentReadyToShow = true;
            _onContentReady?.call(call.arguments["placementName"] as String);
            break;
          case "contentDidAppear":
            _onContentShow?.call(call.arguments["placementName"] as String);
            break;
          case "contentDidDisAppear":
            isAnyContentReadyToShow = false;
            _onContentDismiss?.call(call.arguments["placementName"] as String);
            break;
          case "onGetCurrencyBalanceResponse":
            _onGetCurrencyBalanceResponse.call(
                call.arguments["currencyName"] as String,
                call.arguments["balance"] as int);
            break;
          case "onGetCurrencyBalanceResponseFailure":
            _onGetCurrencyBalanceResponseFailure
                ?.call(call.arguments["error"] as String);
            break;
          case "onSpendCurrencyResponse":
            _onSpendCurrencyResponse.call(
                call.arguments["currencyName"] as String,
                call.arguments["balance"] as int);
            break;
          case "onSpendCurrencyResponseFailure":
            _onSpendCurrencyResponseFailure
                ?.call(call.arguments["error"] as String);
            break;
          case "onAwardCurrencyResponse":
            _onAwardCurrencyResponse.call(
                call.arguments["currencyName"] as String,
                call.arguments["balance"] as int);
            break;
          case "onAwardCurrencyResponseFailure":
            _onAwardCurrencyResponseFailure
                ?.call(call.arguments["error"] as String);
            break;
          default:
        }
      },
    );
  }

  Future<void> setUserID(String userID) async {
    await _channel.invokeMethod("setUserID", {"userID": userID});
  }

  Future<void> setUserLevel(int level) async {
    await _channel.invokeMethod("setUserLevel", {"level": level});
  }

  Future<void> connectSDK(
      {required String userID, required String sdkKey}) async {
    try {
      if (didSdkConnected) return;
      await _channel.invokeMethod('initTapjoySDK',
          {"userID": userID, "sdkKey": sdkKey, "isDebug": kDebugMode});
    } catch (e) {
      if (kDebugMode) {
        print(e);
      }
    }
  }

  Future<void> setUserSegment(TJSegment segment) async {
    await _channel.invokeMethod('setUserSegment', {"segment": segment.name});
  }

  Future<void> setMaxLevel(int level) async {
    await _channel.invokeMethod('setMaxLevel', {"maxlevel": level});
  }

  Future<void> createPlacement(String placementName) async {
    await _channel
        .invokeMethod("createPlacement", {"placementName": placementName});
  }

  Future<void> requestContent(String placementName) async {
    await _channel
        .invokeMethod("requestContent", {"placementName": placementName});
  }

  Future<void> showPlacement(String placementName) async {
    await _channel
        .invokeMethod("showPlacement", {"placementName": placementName});
  }

  Future<void> getBalance() async {
    await _channel.invokeMethod("getCurrencyBalance");
  }

  Future<void> spendCurrency(int amount) async {
    await _channel.invokeMethod("onSpendCurrency", {"amount": amount});
  }

  Future<void> awardCurrency(int amount) async {
    await _channel.invokeMethod("awardCurrency", {"amount": amount});
  }
}
