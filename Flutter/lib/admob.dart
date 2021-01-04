import 'package:firebase_admob/firebase_admob.dart';

class Admob {
  static final String appID = "ca-app-pub-8398366046575199~5855719570";
  static final String testID = FirebaseAdMob.testAppId;
  static final String bannerTopID = "ca-app-pub-8398366046575199/6226528312";

  static final MobileAdTargetingInfo targetingInfo = MobileAdTargetingInfo(
    keywords: <String>['crackwatch', 'crackwatchcomments', 'comments'],
    contentUrl: 'https://crackwatch.com/',
    childDirected: false,
    testDevices: <String>[], // Android emulators are considered test devices
  );

  static admobInitializer() {
    FirebaseAdMob.instance.initialize(appId: appID);
  }

  static BannerAd buildBannerAd() => BannerAd(
        adUnitId: bannerTopID, // BannerAd.testAdUnitId
        size: AdSize.smartBanner,
        targetingInfo: targetingInfo,
        listener: (MobileAdEvent event) {
          if (event == MobileAdEvent.loaded) {
            print("Banner Yüklendi");
          } else {
            print("Banner Yüklenemedi");
          }
        },
      );
}
