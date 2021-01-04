import 'package:crackwatch_comments/admob.dart';
import 'package:firebase_admob/firebase_admob.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class WebViewPage extends StatefulWidget {
  WebViewPage({Key key}) : super(key: key);

  @override
  _WebViewState createState() => _WebViewState();
}

class _WebViewState extends State<WebViewPage> {
  WebViewController controller;
  BannerAd bannerAd = Admob.buildBannerAd();
  @override
  void initState() {
    bannerAd.load();
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    bannerAd.show(anchorType: AnchorType.bottom);
    Future<bool> askForQuit() => showDialog(
        context: context,
        builder: (context) => AlertDialog(
              title: Text("Do you want to exit?"),
              actions: <Widget>[
                FlatButton(
                    onPressed: () {
                      Navigator.pop(context);
                    },
                    child: Text("No")),
                FlatButton(
                    onPressed: () {
                      SystemNavigator.pop();
                    },
                    child: Text("Yes"))
              ],
            ));

    Future<bool> goback() async {
      if (await controller.canGoBack()) {
        print("Going Back");
        controller.goBack();
        return Future.value(false);
      } else {
        return askForQuit();
      }
    }

    return WillPopScope(
      onWillPop: () => goback(),
      child: Scaffold(
        body: SafeArea(
          child: WebView(
            onWebViewCreated: (c) {
              controller = c;
            },
            initialUrl: "https://crackwatch.com/best-comments?period=day",
            javascriptMode: JavascriptMode.unrestricted,
            //gestureNavigationEnabled: false,
            navigationDelegate: (NavigationRequest request) {
              print("Current Request:" + request.url);
              if (!request.url
                      .startsWith('https://crackwatch.com/best-comments') &&
                  !request.url.startsWith('https://b2.crackwatch.com/file')) {
                print('blocking navigation to $request}');
                return NavigationDecision.prevent;
              } else
                return NavigationDecision.navigate;
            },
          ),
        ),
      ),
    );
  }
}
