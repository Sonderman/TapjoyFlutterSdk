import 'package:flutter/material.dart';
import 'package:tapjoy/tapjoyService.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.deepPurple),
        useMaterial3: true,
      ),
      home: const TestPage(),
    );
  }
}

class TestPage extends StatelessWidget {
  const TestPage({super.key});

  @override
  Widget build(BuildContext context) {
    String sdkKey =
        "9Ttb9fWiRpiq_H-a4M296wECjEjI3fSudIZwoEFujD1PwlphIYoJJu1U94L2";
    String placementName = "easygiftofferwall";
    TapjoyService service = TapjoyService(
      onConnectionResult: (result) {},
      onEarnedCurrency: (currencyName, amount) {},
      onclicked: (placementName) => print("Clicked: $placementName"),
      onGetCurrencyBalanceResponse: (currencyName, balance) {
        print("Currency:$currencyName \n Balance:$balance");
      },
      onSpendCurrencyResponse: (currencyName, balance) {
        print("Currency:$currencyName \n Balance:$balance");
      },
      onAwardCurrencyResponse: (currencyName, balance) {
        print("Currency:$currencyName \n Balance:$balance");
      },
      onContentDismiss: (placementName) {
        print("Content Dismissed!");
      },
    );
    return Scaffold(
      body: Center(
        child: Column(
          children: [
            const SizedBox(
              height: 100,
            ),
            MaterialButton(
              onPressed: () {
                service.connectSDK(userID: "ajsdmamslahw", sdkKey: sdkKey);
              },
              child: const Text("Init sdk"),
            ),
            MaterialButton(
              onPressed: () {
                service.createPlacement(placementName);
              },
              child: const Text("createPlacement"),
            ),
            MaterialButton(
              onPressed: () {
                service.requestContent(placementName);
              },
              child: const Text("requestContent"),
            ),
            MaterialButton(
              onPressed: () {
                service.showPlacement(placementName);
              },
              child: const Text("showPlacement"),
            ),
            MaterialButton(
              onPressed: () {
                service.getBalance();
              },
              child: const Text("getbalance"),
            ),
            MaterialButton(
              onPressed: () {
                service.spendCurrency(1);
              },
              child: const Text("Spend Balance"),
            ),
            MaterialButton(
              onPressed: () {
                service.awardCurrency(1);
              },
              child: const Text("Award Currency"),
            ),
          ],
        ),
      ),
    );
  }
}
