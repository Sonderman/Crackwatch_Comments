import 'package:crackwatch_comments/WebView.dart';
import 'package:crackwatch_comments/admob.dart';
import 'package:flutter/material.dart';

void main() {
  WidgetsFlutterBinding.ensureInitialized();
  Admob.admobInitializer();
  runApp(MaterialApp(
    debugShowCheckedModeBanner: false,
    debugShowMaterialGrid: false,
    home: WebViewPage(),
  ));
}
