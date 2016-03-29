package com.lixue.aibei.universalimageloader;

/**
 * 变量
 * Created by Administrator on 2016/3/29.
 */
public class Constants {
    public static final String[] IMAGES = new String[] {
            // Heavy images
            "http://c.hiphotos.baidu.com/image/h%3D200/sign=696a25bb29dda3cc14e4bf2031e93905/32fa828ba61ea8d3df43c820900a304e251f58b4.jpg",
            "http://img.xiami.net/images/artistlogo/23/14397974642923.jpg",
            "http://imge.gmw.cn/attachement/jpg/site2/20151116/bc305baebecd17b381a422.jpg",
            "http://np29.yule.com.cn/html/UploadPic/2009-6/200961514323957477.jpg",
            "http://images.china.cn/news/attachement/jpg/site3/20101216/8757717829627102875.jpg",
            "http://a1.att.hudong.com/28/09/300001062059132011090698816_950.jpg",
            "http://www.zj.xinhuanet.com/2016-03/09/1118279034_14575057779211n.jpg",
            "http://www.uyooo.com/d/file/news/ba/2016-03-10/4631908a75dc7ccc0d24716809c7481f.jpg",
            "http://www.lznews.cn/uploadfile/2011/0330/20110330033830207.jpg",
            "http://www.52tq.net/uploads/allimg/160324/1R5161431-0.png",
            "http://news.k618.cn/kx/201602/W020160207832592839256.jpg",
            "http://news.youth.cn/jy/201602/W020160208243166822047.jpg",
            "http://pic1.nipic.com/2008-11-14/2008111412202678_2.jpg",
            "http://gb.cri.cn/mmsource/images/2010/03/30/ex20100330505.jpg",
            "http://a1.att.hudong.com/06/12/01300000201583122752126343660.jpg",
            "http://news.k618.cn/kx/201602/W020160207832589811834.jpg",

            // Light images
            "http://tabletpcssource.com/wp-content/uploads/2011/05/android-logo.png",
            "http://simpozia.com/pages/images/stories/windows-icon.png",
            "http://radiotray.sourceforge.net/radio.png",
            "http://www.bandwidthblog.com/wp-content/uploads/2011/11/twitter-logo.png",
            "http://weloveicons.s3.amazonaws.com/icons/100907_itunes1.png",
            "http://weloveicons.s3.amazonaws.com/icons/100929_applications.png",
            "http://www.idyllicmusic.com/index_files/get_apple-iphone.png",
            "http://www.frenchrevolutionfood.com/wp-content/uploads/2009/04/Twitter-Bird.png",
            "http://3.bp.blogspot.com/-ka5MiRGJ_S4/TdD9OoF6bmI/AAAAAAAAE8k/7ydKtptUtSg/s1600/Google_Sky%2BMaps_Android.png",
            "http://www.desiredsoft.com/images/icon_webhosting.png",
            "http://goodereader.com/apps/wp-content/uploads/downloads/thumbnails/2012/01/hi-256-0-99dda8c730196ab93c67f0659d5b8489abdeb977.png",
            "http://1.bp.blogspot.com/-mlaJ4p_3rBU/TdD9OWxN8II/AAAAAAAAE8U/xyynWwr3_4Q/s1600/antivitus_free.png",
            "http://cdn3.iconfinder.com/data/icons/transformers/computer.png",
            "http://cdn.geekwire.com/wp-content/uploads/2011/04/firefox.png?7794fe",
            "https://ssl.gstatic.com/android/market/com.rovio.angrybirdsseasons/hi-256-9-347dae230614238a639d21508ae492302340b2ba",
            "http://androidblaze.com/wp-content/uploads/2011/12/tablet-pc-256x256.jpg",
            "http://www.theblaze.com/wp-content/uploads/2011/08/Apple.png",
            "http://1.bp.blogspot.com/-y-HQwQ4Kuu0/TdD9_iKIY7I/AAAAAAAAE88/3G4xiclDZD0/s1600/Twitter_Android.png",
            "http://3.bp.blogspot.com/-nAf4IMJGpc8/TdD9OGNUHHI/AAAAAAAAE8E/VM9yU_lIgZ4/s1600/Adobe%2BReader_Android.png",
            "http://cdn.geekwire.com/wp-content/uploads/2011/05/oovoo-android.png?7794fe",
            "http://icons.iconarchive.com/icons/kocco/ndroid/128/android-market-2-icon.png",
            "http://thecustomizewindows.com/wp-content/uploads/2011/11/Nicest-Android-Live-Wallpapers.png",
            "http://c.wrzuta.pl/wm16596/a32f1a47002ab3a949afeb4f",
            "http://macprovid.vo.llnwd.net/o43/hub/media/1090/6882/01_headline_Muse.jpg",
            // Special cases
            "http://cdn.urbanislandz.com/wp-content/uploads/2011/10/MMSposter-large.jpg", // Very large image
            "http://www.ioncannon.net/wp-content/uploads/2011/06/test9.webp", // WebP image
            "http://4.bp.blogspot.com/-LEvwF87bbyU/Uicaskm-g6I/AAAAAAAAZ2c/V-WZZAvFg5I/s800/Pesto+Guacamole+500w+0268.jpg", // Image with "Mark has been invalidated" problem
            "file:///sdcard/Universal Image Loader @#&=+-_.,!()~'%20.png", // Image from SD card with encoded symbols
            "assets://Living Things @#&=+-_.,!()~'%20.jpg", // Image from assets
            "drawable://" + R.drawable.ic_launcher, // Image from drawables
            "http://upload.wikimedia.org/wikipedia/ru/b/b6/Как_кот_с_мышами_воевал.png", // Link with UTF-8
            "https://www.eff.org/sites/default/files/chrome150_0.jpg", // Image from HTTPS
            "http://bit.ly/soBiXr", // Redirect link
            "http://img001.us.expono.com/100001/100001-1bc30-2d736f_m.jpg", // EXIF
            "", // Empty link
            "http://wrong.site.com/corruptedLink", // Wrong link
    };

    private Constants() {
    }

    public static class Config {
        public static final boolean DEVELOPER_MODE = false;
    }

    public static class Extra {
        public static final String FRAGMENT_INDEX = "com.lixue.aibei.universalimageloader.FRAGMENT_INDEX";
        public static final String IMAGE_POSITION = "com.lixue.aibei.universalimageloader.IMAGE_POSITION";
    }
}
