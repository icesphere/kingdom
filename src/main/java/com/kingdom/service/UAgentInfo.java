/* *******************************************
// Copyright 2010-2011, Anthony Hand
//
// File version date: June 21, 2011
//		Update: 
//		- Updated detectWindowsPhone7() and detectTierRichCss(). Fixed some minor syntax issues. (Those nasty parens...)
//		- Added @return remarks to most methods for JavaDoc usage.
//
// File version date: June 04, 2011
//		Update: 
//		- Updated detectTierIphone() for a BlackBerry issue. Now it checks for both BB WebKit *and* BB Touch.
//
// File version date: May 30, 2011
//		Updates: 
//		- Fixed the method capitalization issue. Now all methods follow the Java standard of starting with lower case. 
//		- Added a global variable: isAndroidPhone. 
//		- Updated the Constructor to always call initDeviceScan().
//		   See notes if you don't need some or all of the InitDeviceScan() feature.  
//		- Added the detectIos() detection method to better reflect parity with the other OS detection methods. 
//		- Refactored the Android detection methods to better reflect parity with the iOS methods. 
//		- Note the meaning of the aetectAndroid() has changed. Now, it's ANY Android device.
//		- Note the new detectAndroidPhone() method. It detects both Android phones and multi-media players. 
//         Now, it also follows Google's best practice of any Android device that DOES have the word 'mobile' in it.
//		- Added a check for the HTC Flyer 7" tablet to the DetectAndroid and DetectAndroidPhone methods.
//			It doesn't always report itself as small Android device. 
//		- Note the detectAndroidTablet() has changed. 
//         Now, it follows Google's best practice of any Android device that does NOT have the word 'mobile' in it.
//		- Revised the BlackBerry method descriptions to clarify which include or exclude the Playbook. 
//		- Removed the detection of third-party Android WebKit browsers from detectTierIphone(). It was redundant. 
//
// File version date: March 14, 2011
//		Updates: 
//		- Added a stored variable 'isTierTablet' and getIsTierTablet() which are initialized in InitDeviceScan(). 
//		- Added a variable and the new DetectBlackBerryTablet() function. 
//		- Added a variable and the new DetectAndroidTablet() function. This is a first draft!
//		- Added the new DetectTierTablet() function. Use this to detect any of the new 
//          larger-screen HTML5 capable tablets. (The 7 inch Galaxy Tab doesn't quality right now.)
//		- Moved Windows Phone 7 from iPhone Tier to Rich CSS Tier. Sorry, Microsoft, but IE 7 isn't good enough.
//
// LICENSE INFORMATION
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//        http://www.apache.org/licenses/LICENSE-2.0
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
// either express or implied. See the License for the specific
// language governing permissions and limitations under the License. 
//
//
// ABOUT THIS PROJECT
//   Project Owner: Anthony Hand
//   Email: anthony.hand@gmail.com
//   Web Site: http://www.mobileesp.com
//   Source Files: http://code.google.com/p/mobileesp/
//
//   Versions of this code are available for:
//      PHP, JavaScript, Java, ASP.NET (C#), and Ruby
//
// *******************************************
 */
package com.kingdom.service;

/**
 * The DetectSmartPhone class encapsulates information about
 * a browser's connection to your web site.
 * You can use it to find out whether the browser asking for
 * your site's content is probably running on a mobile device.
 * The methods were written so you can be as granular as you want.
 * For example, enquiring whether it's as specific as an iPod Touch or
 * as general as a smartphone class device.
 * The object's methods return true, or false.
 */
public class UAgentInfo {
    // User-Agent and Accept HTTP request headers

    private String userAgent = "";
    private String httpAccept = "";

    // Let's store values for quickly accessing the same info multiple times.
    public boolean isIphone = false;
    public boolean isAndroidPhone = false;
    public boolean isTierTablet = false;
    public boolean isTierIphone = false;
    public boolean isTierRichCss = false;
    public boolean isTierGenericMobile = false;

    // Initialize some initial smartphone string variables.
    public static final String engineWebKit = "webkit";

    public static final String deviceIphone = "iphone";
    public static final String deviceIpod = "ipod";
    public static final String deviceIpad = "ipad";
    public static final String deviceMacPpc = "macintosh"; //Used for disambiguation

    public static final String deviceAndroid = "android";
    public static final String deviceGoogleTV = "googletv";
    public static final String deviceXoom = "xoom"; //Motorola Xoom
    public static final String deviceHtcFlyer = "htc_flyer"; //HTC Flyer

    public static final String deviceSymbian = "symbian";
    public static final String deviceS60 = "series60";
    public static final String deviceS70 = "series70";
    public static final String deviceS80 = "series80";
    public static final String deviceS90 = "series90";

    public static final String deviceWinPhone7 = "windows phone os 7";
    public static final String deviceWinMob = "windows ce";
    public static final String deviceWindows = "windows";
    public static final String deviceIeMob = "iemobile";
    public static final String devicePpc = "ppc"; //Stands for PocketPC
    public static final String enginePie = "wm5 pie"; //An old Windows Mobile

    public static final String deviceBB = "blackberry";
    public static final String vndRIM = "vnd.rim"; //Detectable when BB devices emulate IE or Firefox
    public static final String deviceBBStorm = "blackberry95";  //Storm 1 and 2
    public static final String deviceBBBold = "blackberry97";  //Bold
    public static final String deviceBBTour = "blackberry96";  //Tour
    public static final String deviceBBCurve = "blackberry89";  //Curve 2
    public static final String deviceBBTorch = "blackberry 98";  //Torch
    public static final String deviceBBPlaybook = "playbook"; //PlayBook tablet

    public static final String devicePalm = "palm";
    public static final String deviceWebOS = "webos"; //For Palm's new WebOS devices
    public static final String engineBlazer = "blazer"; //Old Palm
    public static final String engineXiino = "xiino"; //Another old Palm

    public static final String deviceKindle = "kindle";  //Amazon Kindle, eInk one.

    public static final String deviceNuvifone = "nuvifone";  //Garmin Nuvifone

    //Initialize variables for mobile-specific content.
    public static final String vndwap = "vnd.wap";
    public static final String wml = "wml";

    //Initialize variables for other random devices and mobile browsers.
    public static final String deviceBrew = "brew";
    public static final String deviceDanger = "danger";
    public static final String deviceHiptop = "hiptop";
    public static final String devicePlaystation = "playstation";
    public static final String deviceNintendoDs = "nitro";
    public static final String deviceNintendo = "nintendo";
    public static final String deviceWii = "wii";
    public static final String deviceXbox = "xbox";
    public static final String deviceArchos = "archos";

    public static final String engineOpera = "opera"; //Popular browser
    public static final String engineNetfront = "netfront"; //Common embedded OS browser
    public static final String engineUpBrowser = "up.browser"; //common on some phones
    public static final String engineOpenWeb = "openweb"; //Transcoding by OpenWave server
    public static final String deviceMidp = "midp"; //a mobile Java technology
    public static final String uplink = "up.link";
    public static final String engineTelecaQ = "teleca q"; //a modern feature phone browser
    public static final String devicePda = "pda"; //some devices report themselves as PDAs
    public static final String mini = "mini";  //Some mobile browsers put "mini" in their names.
    public static final String mobile = "mobile"; //Some mobile browsers put "mobile" in their user agent strings.
    public static final String mobi = "mobi"; //Some mobile browsers put "mobi" in their user agent strings.

    //Use Maemo, Tablet, and Linux to test for Nokia"s Internet Tablets.
    public static final String maemo = "maemo";
    public static final String maemoTablet = "tablet";
    public static final String linux = "linux";
    public static final String qtembedded = "qt embedded"; //for Sony Mylo
    public static final String mylocom2 = "com2"; //for Sony Mylo also

    //In some UserAgents, the only clue is the manufacturer.
    public static final String manuSonyEricsson = "sonyericsson";
    public static final String manuericsson = "ericsson";
    public static final String manuSamsung1 = "sec-sgh";
    public static final String manuSony = "sony";
    public static final String manuHtc = "htc"; //Popular Android and WinMo manufacturer

    //In some UserAgents, the only clue is the operator.
    public static final String svcDocomo = "docomo";
    public static final String svcKddi = "kddi";
    public static final String svcVodafone = "vodafone";

    //Disambiguation strings.
    public static final String disUpdate = "update"; //pda vs. update


    /**
     * Initialize the userAgent and httpAccept variables
     *
     * @param userAgent  the User-Agent header
     * @param httpAccept the Accept header
     */
    public UAgentInfo(String userAgent, String httpAccept) {
        if (userAgent != null) {
            this.userAgent = userAgent.toLowerCase();
        }
        if (httpAccept != null) {
            this.httpAccept = httpAccept.toLowerCase();
        }

        //Intialize key stored values.
        initDeviceScan();
    }

    /**
     * Return the lower case HTTP_USER_AGENT
     *
     * @return userAgent
     */
    public String getUserAgent() {
        return userAgent;
    }

    /**
     * Return the lower case HTTP_ACCEPT
     *
     * @return httpAccept
     */
    public String getHttpAccept() {
        return httpAccept;
    }

    /**
     * Return whether the device is an Iphone or iPod Touch
     *
     * @return isIphone
     */
    public boolean getIsIphone() {
        return isIphone;
    }

    /**
     * Return whether the device is in the Tablet Tier.
     *
     * @return isTierTablet
     */
    public boolean getIsTierTablet() {
        return isTierTablet;
    }

    /**
     * Return whether the device is in the Iphone Tier.
     *
     * @return isTierIphone
     */
    public boolean getIsTierIphone() {
        return isTierIphone;
    }

    /**
     * Return whether the device is in the 'Rich CSS' tier of mobile devices.
     *
     * @return isTierRichCss
     */
    public boolean getIsTierRichCss() {
        return isTierRichCss;
    }

    /**
     * Return whether the device is a generic, less-capable mobile device.
     *
     * @return isTierGenericMobile
     */
    public boolean getIsTierGenericMobile() {
        return isTierGenericMobile;
    }

    /**
     * Initialize Key Stored Values.
     */
    public void initDeviceScan() {
        this.isIphone = detectIphoneOrIpod();
        this.isAndroidPhone = detectAndroidPhone();
        this.isTierTablet = detectTierTablet();
        this.isTierIphone = detectTierIphone();
        this.isTierRichCss = detectTierRichCss();
        this.isTierGenericMobile = detectTierOtherPhones();
    }

    /**
     * Detects if the current device is an iPhone.
     *
     * @return detection of an iPhone
     */
    public boolean detectIphone() {
        // The iPad and iPod touch say they're an iPhone! So let's disambiguate.
        if (userAgent.indexOf(deviceIphone) != -1 &&
                !detectIpad() &&
                !detectIpod()) {
            return true;
        }
        return false;
    }

    /**
     * Detects if the current device is an iPod Touch.
     *
     * @return detection of an iPod Touch
     */
    public boolean detectIpod() {
        if (userAgent.indexOf(deviceIpod) != -1) {
            return true;
        }
        return false;
    }

    /**
     * Detects if the current device is an iPad tablet.
     *
     * @return detection of an iPad
     */
    public boolean detectIpad() {
        if (userAgent.indexOf(deviceIpad) != -1
                && detectWebkit()) {
            return true;
        }
        return false;
    }

    /**
     * Detects if the current device is an iPhone or iPod Touch.
     *
     * @return detection of an iPhone or iPod Touch
     */
    public boolean detectIphoneOrIpod() {
        //We repeat the searches here because some iPods may report themselves as an iPhone, which would be okay.
        if (userAgent.indexOf(deviceIphone) != -1
                || userAgent.indexOf(deviceIpod) != -1) {
            return true;
        }
        return false;
    }

    /**
     * Detects *any* iOS device: iPhone, iPod Touch, iPad.
     *
     * @return detection of an Apple iOS device
     */
    public boolean detectIos() {
        if (detectIphoneOrIpod() || detectIpad()) {
            return true;
        }
        return false;
    }


    /**
     * Detects *any* Android OS-based device: phone, tablet, and multi-media player.
     * Also detects Google TV.
     *
     * @return detection of an Android device
     */
    public boolean detectAndroid() {
        if ((userAgent.indexOf(deviceAndroid) != -1) ||
                detectGoogleTV())
            return true;
        //Special check for the HTC Flyer 7" tablet. It should report here.
        if (userAgent.indexOf(deviceHtcFlyer) != -1)
            return true;
        return false;
    }

    /**
     * Detects if the current device is a (small-ish) Android OS-based device
     * used for calling and/or multi-media (like a Samsung Galaxy Player).
     * Google says these devices will have 'Android' AND 'mobile' in user agent.
     * Ignores tablets (Honeycomb and later).
     *
     * @return detection of an Android phone
     */
    public boolean detectAndroidPhone() {
        if (detectAndroid() && (userAgent.indexOf(mobile) != -1))
            return true;
        //Special check for the HTC Flyer 7" tablet. It should report here.
        if (userAgent.indexOf(deviceHtcFlyer) != -1)
            return true;
        return false;
    }

    /**
     * Detects if the current device is a (self-reported) Android tablet.
     * Google says these devices will have 'Android' and NOT 'mobile' in their user agent.
     *
     * @return detection of an Android tablet
     */
    public boolean detectAndroidTablet() {
        //Special check for the HTC Flyer 7" tablet. It should NOT report here.
        if (userAgent.indexOf(deviceHtcFlyer) != -1)
            return false;

        if (detectAndroid() && !(userAgent.indexOf(mobile) != -1)) {
            return true;
        }
        return false;
    }

    /**
     * Detects if the current device is an Android OS-based device and
     * the browser is based on WebKit.
     *
     * @return detection of an Android WebKit browser
     */
    public boolean detectAndroidWebKit() {
        if (detectAndroid() && detectWebkit()) {
            return true;
        }
        return false;
    }

    /**
     * Detects if the current device is a GoogleTV.
     *
     * @return detection of GoogleTV
     */
    public boolean detectGoogleTV() {
        if (userAgent.indexOf(deviceGoogleTV) != -1) {
            return true;
        }
        return false;
    }

    /**
     * Detects if the current browser is based on WebKit.
     *
     * @return detection of a WebKit browser
     */
    public boolean detectWebkit() {
        if (userAgent.indexOf(engineWebKit) != -1) {
            return true;
        }
        return false;
    }

    /**
     * Detects if the current browser is the Symbian S60 Open Source Browser.
     *
     * @return detection of Symbian S60 Browser
     */
    public boolean detectS60OssBrowser() {
        //First, test for WebKit, then make sure it's either Symbian or S60.
        if (detectWebkit()
                && (userAgent.indexOf(deviceSymbian) != -1
                || userAgent.indexOf(deviceS60) != -1)) {
            return true;
        }
        return false;
    }

    /**
     * Detects if the current device is any Symbian OS-based device,
     * including older S60, Series 70, Series 80, Series 90, and UIQ,
     * or other browsers running on these devices.
     *
     * @return detection of SymbianOS
     */
    public boolean detectSymbianOS() {
        if (userAgent.indexOf(deviceSymbian) != -1
                || userAgent.indexOf(deviceS60) != -1
                || userAgent.indexOf(deviceS70) != -1
                || userAgent.indexOf(deviceS80) != -1
                || userAgent.indexOf(deviceS90) != -1) {
            return true;
        }
        return false;
    }

    /**
     * Detects if the current browser is a
     * Windows Phone 7 device.
     *
     * @return detection of WindowsPhone7
     */
    public boolean detectWindowsPhone7() {
        if (userAgent.indexOf(deviceWinPhone7) != -1) {
            return true;
        }
        return false;
    }

    /**
     * Detects if the current browser is a Windows Mobile device.
     * Excludes Windows Phone 7 devices.
     * Focuses on Windows Mobile 6.xx and earlier.
     *
     * @return detection of Windows Mobile
     */
    public boolean detectWindowsMobile() {
        //Exclude new Windows Phone 7.
        if (detectWindowsPhone7()) {
            return false;
        }
        //Most devices use 'Windows CE', but some report 'iemobile'
        //  and some older ones report as 'PIE' for Pocket IE.
        //  We also look for instances of HTC and Windows for many of their WinMo devices.
        if (userAgent.indexOf(deviceWinMob) != -1
                || userAgent.indexOf(deviceWinMob) != -1
                || userAgent.indexOf(deviceIeMob) != -1
                || userAgent.indexOf(enginePie) != -1
                || (userAgent.indexOf(manuHtc) != -1 && userAgent.indexOf(deviceWindows) != -1)
                || (detectWapWml() && userAgent.indexOf(deviceWindows) != -1)) {
            return true;
        }

        //Test for Windows Mobile PPC but not old Macintosh PowerPC.
        if (userAgent.indexOf(devicePpc) != -1 &&
                !(userAgent.indexOf(deviceMacPpc) != -1))
            return true;

        return false;
    }

    /**
     * Detects if the current browser is any BlackBerry.
     * Includes the PlayBook.
     *
     * @return detection of Blackberry
     */
    public boolean detectBlackBerry() {
        if (userAgent.indexOf(deviceBB) != -1 ||
                httpAccept.indexOf(vndRIM) != -1) {
            return true;
        }
        return false;
    }

    /**
     * Detects if the current browser is on a BlackBerry tablet device.
     * Example: PlayBook
     *
     * @return detection of a Blackberry Tablet
     */
    public boolean detectBlackBerryTablet() {
        if (userAgent.indexOf(deviceBBPlaybook) != -1) {
            return true;
        }
        return false;
    }

    /**
     * Detects if the current browser is a BlackBerry device AND uses a
     * WebKit-based browser. These are signatures for the new BlackBerry OS 6.
     * Examples: Torch. Includes the Playbook.
     *
     * @return detection of a Blackberry device with WebKit browser
     */
    public boolean detectBlackBerryWebKit() {
        if (detectBlackBerry() &&
                userAgent.indexOf(engineWebKit) != -1) {
            return true;
        }
        return false;
    }

    /**
     * Detects if the current browser is a BlackBerry Touch
     * device, such as the Storm or Torch. Excludes the Playbook.
     *
     * @return detection of a Blackberry touchscreen device
     */
    public boolean detectBlackBerryTouch() {
        if (detectBlackBerry() &&
                (userAgent.indexOf(deviceBBStorm) != -1 ||
                        userAgent.indexOf(deviceBBTorch) != -1)) {
            return true;
        }
        return false;
    }

    /**
     * Detects if the current browser is a BlackBerry device AND
     * has a more capable recent browser. Excludes the Playbook.
     * Examples, Storm, Bold, Tour, Curve2
     * Excludes the new BlackBerry OS 6 browser!!
     *
     * @return detection of a Blackberry device with a better browser
     */
    public boolean detectBlackBerryHigh() {
        //Disambiguate for BlackBerry OS 6 (WebKit) browser
        if (detectBlackBerryWebKit())
            return false;
        if (detectBlackBerry()) {
            if (detectBlackBerryTouch()
                    || userAgent.indexOf(deviceBBBold) != -1
                    || userAgent.indexOf(deviceBBTour) != -1
                    || userAgent.indexOf(deviceBBCurve) != -1) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Detects if the current browser is a BlackBerry device AND
     * has an older, less capable browser.
     * Examples: Pearl, 8800, Curve1
     *
     * @return detection of a Blackberry device with a poorer browser
     */
    public boolean detectBlackBerryLow() {
        if (detectBlackBerry()) {
            //Assume that if it's not in the High tier, then it's Low
            if (detectBlackBerryHigh()
                    || detectBlackBerryWebKit()) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    /**
     * Detects if the current browser is on a PalmOS device.
     *
     * @return detection of a PalmOS device
     */
    public boolean detectPalmOS() {
        //Most devices nowadays report as 'Palm', but some older ones reported as Blazer or Xiino.
        if (userAgent.indexOf(devicePalm) != -1
                || userAgent.indexOf(engineBlazer) != -1
                || userAgent.indexOf(engineXiino) != -1) {
            //Make sure it's not WebOS first
            if (detectPalmWebOS()) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * Detects if the current browser is on a Palm device
     * running the new WebOS.
     *
     * @return detection of a Palm WebOS device
     */
    public boolean detectPalmWebOS() {
        if (userAgent.indexOf(deviceWebOS) != -1) {
            return true;
        }
        return false;
    }

    /**
     * Detects if the current browser is a
     * Garmin Nuvifone.
     *
     * @return detection of a Garmin Nuvifone
     */
    public boolean detectGarminNuvifone() {
        return userAgent.contains(deviceNuvifone);
    }

    /**
     * Check to see whether the device is any device
     * in the 'smartphone' category.
     *
     * @return detection of a general smartphone device
     */
    public boolean detectSmartphone() {
        return (isIphone
                || isAndroidPhone
                || isTierIphone
                || detectS60OssBrowser()
                || detectSymbianOS()
                || detectWindowsMobile()
                || detectWindowsPhone7()
                || detectBlackBerry()
                || detectPalmWebOS()
                || detectPalmOS()
                || detectGarminNuvifone());
    }

    /**
     * Detects whether the device is a Brew-powered device.
     *
     * @return detection of a Brew device
     */
    public boolean detectBrewDevice() {
        return userAgent.contains(deviceBrew);
    }

    /**
     * Detects the Danger Hiptop device.
     *
     * @return detection of a Danger Hiptop
     */
    public boolean detectDangerHiptop() {
        if (userAgent.indexOf(deviceDanger) != -1
                || userAgent.indexOf(deviceHiptop) != -1) {
            return true;
        }
        return false;
    }

    /**
     * Detects Opera Mobile or Opera Mini.
     *
     * @return detection of an Opera browser for a mobile device
     */
    public boolean detectOperaMobile() {
        if (userAgent.indexOf(engineOpera) != -1
                && (userAgent.indexOf(mini) != -1
                || userAgent.indexOf(mobi) != -1)) {
            return true;
        }
        return false;
    }

    /**
     * Detects whether the device supports WAP or WML.
     *
     * @return detection of a WAP- or WML-capable device
     */
    public boolean detectWapWml() {
        return httpAccept.contains(vndwap)
                || httpAccept.contains(wml);
    }

    /**
     * Detects if the current device is an Amazon Kindle.
     *
     * @return detection of a Kindle
     */
    public boolean detectKindle() {
        return userAgent.contains(deviceKindle);
    }

    /**
     * Detects if the current device is a mobile device.
     * This method catches most of the popular modern devices.
     * Excludes Apple iPads and other modern tablets.
     *
     * @return detection of any mobile device using the quicker method
     */
    public boolean detectMobileQuick() {
        //Let's exclude tablets
        if (isTierTablet) {
            return false;
        }
        //Most mobile browsing is done on smartphones
        return detectSmartphone() || detectWapWml() || detectBrewDevice() || detectOperaMobile()
                || (userAgent.contains(engineNetfront)) || (userAgent.contains(engineUpBrowser))
                || (userAgent.contains(engineOpenWeb)) || detectDangerHiptop() || detectMidpCapable()
                || detectMaemoTablet() || detectArchos() || (userAgent.contains(devicePda)) && (!userAgent.contains(disUpdate)) || userAgent.contains(mobile);

    }

    /**
     * Detects if the current device is a Sony Playstation.
     *
     * @return detection of Sony Playstation
     */
    public boolean detectSonyPlaystation() {
        return userAgent.contains(devicePlaystation);
    }

    /**
     * Detects if the current device is a Nintendo game device.
     *
     * @return detection of Nintendo
     */
    public boolean detectNintendo() {
        return userAgent.contains(deviceNintendo)
                || userAgent.contains(deviceWii)
                || userAgent.contains(deviceNintendoDs);
    }

    /**
     * Detects if the current device is a Microsoft Xbox.
     *
     * @return detection of Xbox
     */
    public boolean detectXbox() {
        return userAgent.contains(deviceXbox);
    }

    /**
     * Detects if the current device is an Internet-capable game console.
     *
     * @return detection of any Game Console
     */
    public boolean detectGameConsole() {
        return detectSonyPlaystation()
                || detectNintendo()
                || detectXbox();
    }

    /**
     * Detects if the current device supports MIDP, a mobile Java technology.
     *
     * @return detection of a MIDP mobile Java-capable device
     */
    public boolean detectMidpCapable() {
        return userAgent.contains(deviceMidp)
                || httpAccept.contains(deviceMidp);
    }

    /**
     * Detects if the current device is on one of the Maemo-based Nokia Internet Tablets.
     *
     * @return detection of a Maemo OS tablet
     */
    public boolean detectMaemoTablet() {
        if (userAgent.contains(maemo)) {
            return true;
        } else if (userAgent.contains(maemoTablet)
                && userAgent.contains(linux)) {
            return true;
        }
        return false;
    }

    /**
     * Detects if the current device is an Archos media player/Internet tablet.
     *
     * @return detection of an Archos media player
     */
    public boolean detectArchos() {
        return userAgent.contains(deviceArchos);
    }

    /**
     * Detects if the current browser is a Sony Mylo device.
     *
     * @return detection of a Sony Mylo device
     */
    public boolean detectSonyMylo() {
        return userAgent.contains(manuSony)
                && (userAgent.contains(qtembedded)
                || userAgent.contains(mylocom2));
    }

    /**
     * The longer and more thorough way to detect for a mobile device.
     * Will probably detect most feature phones,
     * smartphone-class devices, Internet Tablets,
     * Internet-enabled game consoles, etc.
     * This ought to catch a lot of the more obscure and older devices, also --
     * but no promises on thoroughness!
     *
     * @return detection of any mobile device using the more thorough method
     */
    public boolean detectMobileLong() {
        if (detectMobileQuick()
                || detectGameConsole()
                || detectSonyMylo()) {
            return true;
        }

        //detect older phones from certain manufacturers and operators.
        return userAgent.contains(uplink) || userAgent.contains(manuSonyEricsson) || userAgent.contains(manuericsson) || userAgent.contains(manuSamsung1) || userAgent.contains(svcDocomo) || userAgent.contains(svcKddi) || userAgent.contains(svcVodafone);

    }

    //*****************************
    // For Mobile Web Site Design
    //*****************************

    /**
     * The quick way to detect for a tier of devices.
     * This method detects for the new generation of
     * HTML 5 capable, larger screen tablets.
     * Includes iPad, Android (e.g., Xoom), BB Playbook, etc.
     *
     * @return detection of any device in the Tablet Tier
     */
    public boolean detectTierTablet() {
        return detectIpad()
                || detectAndroidTablet()
                || detectBlackBerryTablet();
    }

    /**
     * The quick way to detect for a tier of devices.
     * This method detects for devices which can
     * display iPhone-optimized web content.
     * Includes iPhone, iPod Touch, Android, Palm WebOS, etc.
     *
     * @return detection of any device in the iPhone/Android/WebOS Tier
     */
    public boolean detectTierIphone() {
        return isIphone
                || isAndroidPhone
                || (detectBlackBerryWebKit()
                && detectBlackBerryTouch())
                || detectPalmWebOS()
                || detectGarminNuvifone()
                || detectMaemoTablet();
    }

    /**
     * The quick way to detect for a tier of devices.
     * This method detects for devices which are likely to be capable
     * of viewing CSS content optimized for the iPhone,
     * but may not necessarily support JavaScript.
     * Excludes all iPhone Tier devices.
     *
     * @return detection of any device in the 'Rich CSS' Tier
     */
    public boolean detectTierRichCss() {
        //The following devices are explicitly ok.
        //Note: 'High' BlackBerry devices ONLY
        if (detectMobileQuick()) {

            if (detectTierIphone()) {
                return false;
            }

            //The following devices are explicitly ok.
            //Note: 'High' BlackBerry devices ONLY
            //WP7's IE-7-based browser isn't good enough for iPhone Tier.
            if (detectWebkit()
                    || detectS60OssBrowser()
                    || detectBlackBerryHigh()
                    || detectWindowsPhone7()
                    || detectWindowsMobile()
                    || userAgent.contains(engineTelecaQ)) {
                return true;
            }
        }
        return false;
    }

    /**
     * The quick way to detect for a tier of devices.
     * This method detects for all other types of phones,
     * but excludes the iPhone and RichCSS Tier devices.
     *
     * @return detection of a mobile device in the less capable tier
     */
    public boolean detectTierOtherPhones() {
        //Exclude devices in the other 2 categories
        return detectMobileLong()
                && !detectTierIphone()
                && !detectTierRichCss();
    }
}