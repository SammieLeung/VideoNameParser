package com.firefly.videonameparser;

import android.text.TextUtils;
import android.util.Log;

import com.firefly.videonameparser.bean.AudioCodec;
import com.firefly.videonameparser.bean.Country;
import com.firefly.videonameparser.bean.Episodes;
import com.firefly.videonameparser.bean.FileSize;
import com.firefly.videonameparser.bean.OtherItem;
import com.firefly.videonameparser.bean.Resolution;
import com.firefly.videonameparser.bean.Source;
import com.firefly.videonameparser.bean.SubTitle;
import com.firefly.videonameparser.bean.VideoCodec;
import com.firefly.videonameparser.bean.Year;
import com.firefly.videonameparser.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class VideoNameParser2 {
    private static final String TAG = "VideoNameParser2";
    /*
     * 视频名称解析依据
     * * http://wiki.xbmc.org/index.php?title=Adding_videos_to_the_library/Naming_files/TV_shows
     * * http://wiki.xbmc.org/index.php?title=Advancedsettings.xml#.3Ctvshowmatching.3E
     */
    private final static int maxSegments = 2;//最多识别2级菜单 电影名文件夹 > xx篇 > 电影文件| 电视剧文件夹 > 第x季 > 电视剧文件
    private final static String[] extensions = {
            "avi", "wmv", "mp4", "rmvb", "kkv", "3gp", "ts", "mpeg", "mpg", "mkv", "m3u8", "mov",
            "m2ts", "flv", "m2t", "mts", "vob", /*"dat",*/ "m4v", "asf", "f4v", "3g2", "m1v", "m2v", "tp", "trp", "m2p", "rm",
            "avc", "dv", "divx", "mjpg", "mjpeg", "mpe", "mp2p", "mp2t", "mpg2", "mpeg2", "m4p", "mp4ps", "ogm", "hdmov",
            "qt", "iso", "webm", "swf", "ram", "oog", "ogv", "mt2s", "amv", "svi", "mxf", "roq", "f4p", "f4a", "f4b"
    };
    private final static String[] movieKeywords = {
            "2160p",
            "1080p",
            "720p",
            "480p",
            "blurayrip",
            "brrip",
            "divx",
            "dvdrip",
            "hdrip",
            "hdtv",
            "tvrip",
            "xvid",
            "camrip",
            "hd",
            "4k",
            "2k",
            "bd"
    };
    private static final String[] SEASON_WORDS = {
            "s",
            "season",
            "saison",
            "seizoen",
            "serie",
            "seasons",
            "saisons",
            "series",
            "tem",
            "temp",
            "temporada",
            "temporadas",
            "stagione"
    };

    private static final String[] EPISODE_WORDS = {
            "e",
            "episode",
            "episodes",
            "eps",
            "ep",
            "episodio",
            "episodios",
            "capitulo",
            "capitulos"
    };

    /**
     * 第1集
     * 共1集
     * 全1432集
     * 第一百五十六集
     * 第贰话
     * 全叁季
     * E1
     * Ep1
     * S0329
     * S3E2
     * S3E301-02
     * EP01-02
     */
    private static final String PATTERN_SEASON_OR_EPISODE_OR_YEAR =
            "(" + build_or_pattern(SEASON_WORDS, EPISODE_WORDS, new String[]{"第", "共", "全"}) +
                    "?(?:[0-9]{1,4}|[一二三四五六七八九十零壹贰叁肆伍陆柒捌玖拾百千佰仟]{1,})"
                    + build_or_pattern(EPISODE_WORDS, new String[]{"季", "话", "話", "集", "部", "-"}) + "?(?:[0-9]{0,4}-?[0-9]{0,4}))";
    /**
     * 0.
     * (.*[\.| |_]){2,}
     * [sample]
     * 北欧人.2022.mp4
     */
    private static final String PATTERN_A = "(.*[\\.| |_]){2,}";


    /* * 1.name.0000.xxx.xx.xxx.xxx.extension
     * ^(?:[A-Za-z0-9\u4e00-\u9fa5-:!?_ ]+[\.| |_])+(?:19[0-9][0-9]|20[0-9][0-9])[\.| |_](?:[A-Za-z0-9\u4e00-\u9fa5-:!?_ ]*[\.| |_])+
     * [exclude]
     * 网页website.中文名.2022.BD.1080P.中英双字.mkv
     * www.website.com.中文名.2022.BD.1080P.mp4
     **/
    private static final String PATTERN_B = "^((?:[A-Za-z0-9\\u4e00-\\u9fa5-:!?_ ]+[\\.| |_])+)(19[0-9][0-9]|20[0-9][0-9])[\\.| |_]+";

    /* *
     * 2. [series title][20] 匹配剧集/动画 title+集数/年份
     * \[([A-Za-z0-9\u4E00-\u9FA5 _]*)+\]\[[0-9]{2,4}\]
     * [sample]
     * [Mobile Suit Gundam Seed Destiny HD REMASTER][46][Big5][720p][AVC_AAC][encoded by SEED].mp4
     * [動漫國字幕組]01月新番[輝夜姬想讓人告白_天才們的戀愛頭腦戰_][01][720P][繁體][MP4]
     * www.dgy8.com[房子][2022][BD][1080P].mp4
     * [exclude]
     * 排除年份干扰
     * [title][year]
     * www.dgy8.com[房子][2022][BD][1080P].mp4
     */
//    private static final String PATTERN_C = "\\[([A-Za-z0-9\\u4E00-\\u9FA5 _]*)+\\]\\[[0-9]{2,4}\\]";
    private static final String PATTERN_C = "\\[([\\u4e00-\\u9fa5\\w\\d,!?: _-]+)\\]\\[" + PATTERN_SEASON_OR_EPISODE_OR_YEAR + "\\]";

    private static final String PATTERN_D = "\\]?([\\u4e00-\\u9fa5\\w\\d,!?: _-]+)\\[" + PATTERN_SEASON_OR_EPISODE_OR_YEAR + "\\]";

    private static final String PATTERN_C_PREFIX = "^\\[[\\u4e00-\\u9fa5\\w\\d,!?: _-]+\\]\\[" + PATTERN_SEASON_OR_EPISODE_OR_YEAR + "\\]";


    /* 3.[translate group|website|etc.]Movie Title[99|2099] 名字没有被[]包裹
     * (?:\[.*\])+([A-Za-z0-9\u4E00-\u9FA5]+\s?)+\[([0-9]+)\]
     * [sample]
     * [2017][HYSUB]ONE PUNCH MAN[10][GB_MP4][1280X720].mp4
     * [exclude]
     * 排除年份干扰
     * [title][year]
     * [HYSUB]ONE PUNCH MAN[2017][GB_MP4][1280X720].mp4
     */
//    private static final String PATTERN_D = "(?:\\[.*\\])+([A-Za-z0-9\\u4E00-\\u9FA5]+\\s?)+\\[([0-9]+)\\]";
//    private static final String PATTERN_D = "(?:\\[.*\\])+([A-Za-z0-9\\u4E00-\\u9FA5]+\\s?)+\\[" + PATTERN_SEASON_OR_EPISODE_OR_YEAR + "\\]";


    /**
     * 4.www.website.co 匹配网页/中文网站名+pinyin简写/中文网站名+网址，需要去掉的内容
     * ^(?:[0-9a-z_!~*'()-]+\.*[0-9a-z][0-9a-z-]{0,61}?[0-9a-z]\.[a-z]{2,6})[\.| |_]?|^[A-Z]*([\u4E00-\u9FA5][\.| |_]?)+([0-9a-z]+[\.| |_])+
     * [sample]
     * 阳光电影www.ygdy8.com
     * 阳光电影www.ygdy8.com
     * 阳光电影_www.ygdy8.com
     * 阳光电影-www.ygdy8.com
     * 阳光电影.www.ygdy8.com
     * 阳光电影|www.ygdy8.com
     * BT乐园.bt606.co.
     * 阳光电影ygdy8.
     */
    private static final String PATTERN_E = "^(?:[0-9a-z_!~*'()-]+\\.*[0-9a-z][0-9a-z-]{0,61}?[0-9a-z]\\.[a-z]{2,6})[\\.| |_]?|^[A-Z]*([\\u4E00-\\u9FA5][\\.| |_]?)+([0-9a-z]+[\\.| |_])+";

    /* 5.开头[]里的内容，内容中无空格，有空格可以视为名字，需要去掉
     * ^\[[A-Za-z0-9\u4E00-\u9FA5\.]*\]
     * [sample]
     * [BD影视分享bd2020.co]青春变形记.Turning.Red.2022.AAC5.1.HD1080P.国粤英三语.中字.mp4
     * [BT乐园.bt606.com]名侦探洪吉童:消失的村庄2016.HD720P.X264.AAC.韩语中字.mp4
     * [動漫國字幕組]01月新番[輝夜姬想讓人告白_天才們的戀愛頭腦戰_][01][720P][繁體][MP4]
     * [蚂蚁网www.mayi.tw]审死官CD2.mp4
     * [远鉴字幕组andOrange字幕组]金蝉脱壳2【蓝光版特效中英双字】Escape.Plan.2.Hades.2018.1080p.BluRay.x264.DTS-HDC@CHDbits.mkv
     * [2017][HYSUB]ONE PUNCH MAN[10][GB_MP4][1280X720].mp4
     * [domp4]北o人.2022.HD1080p.中英双字.mp4
     */
    private static final String PATTERN_F = "^\\[[A-Za-z0-9\\u4E00-\\u9FA5\\.-]*\\]";

    private static final String PATTERN_G = "^\\w*[\\u4e00-\\u9fa5]+[a-z0-9\\.]{4,}([\\u4e00-\\u9fa5]+)";

    /**
     * name.xxxx.xxx.xxxx.20201031
     * [sample]
     * Beijing.2022.Olympic.Winter.Games.Opening.Ceremony.20220204.4320p.CCTV-8K.UHDTV.AVS3.10bit.HDR.MPEG-FLTTH.ts
     */
    private static final String PATTERN_H = "^((?:[A-Za-z0-9\\u4e00-\\u9fa5-:!?_ ]+[\\.| |_])+)((?:19[0-9][0-9]|20[0-9][0-9])\\d{4})[\\.| |_]+(.*)";

    private final static String SEGMENTS_SPLIT = "\\.| |-|;|_";


    private String simplifyName(String name) {
        if (name == null || name.length() == 0) return "";
        name = name
                .trim()
                .replaceAll("\\(|\\{", "\\[")
                .replaceAll("\\)|\\}", "\\]")
                .replaceAll("/\\([^\\(]+\\)$/", "") // remove brackets at end
                .replaceAll("/&/g", "and")
                .replaceAll("(\\D)[257]\\.[01]", "$1")
                .replaceAll("([HhXx]).(26[45])", "$1$2");

        return name;
        //.split(" ").filter(function(r){return r}).join(" ")
    }

    private MovieNameInfo mInfo;
    private ArrayList<String> mTrushWord = new ArrayList<>();

    public MovieNameInfo parseVideoName(String filePath) {
        if (StringUtils.checkChina(filePath)) {
            filePath = StringUtils.ChineseToEnglish(filePath);
        }
        filePath = filePath.replaceAll("[Ss][Mm][Bb]://.*?/", "")
                .replaceAll("[Hh][Tt]{2}[Pp]://.*?/", "");

        mInfo = new MovieNameInfo();
        String[] segments = slice(reverse(filePath
                .replace("\\", "/") // Support Windows slashes, lol
                .replace("\\(", "\\[")
                .split("/")), 0, maxSegments);//like String[]{"Movie","folder1","xxx.mp4"}
        String[] firstNameSplit = segments[0].split("\\.| |_");//
        /**
         * extension
         */
        for (String part : reverse(firstNameSplit)) {
            if (mInfo.getExtension() == null && contain(extensions, part.toLowerCase())) {
                mInfo.setExtension(part);
                break;
            }
        }
        String firstName = segments[0].replaceAll("\\.| |_", "");//like [BD影视分享bd2020co]青春变形记TurningRed2022AAC51HD1080P国粤英三语中字mp4

        for (String seg : segments) {
            if (mInfo.hasName() && ((mInfo.saneSeason() && mInfo.saneEpisode()) || mInfo.hasAired() || mInfo.getYear() != 0))
                break;

            seg = simplifyName(seg);
            if (StringUtils.matchFindStrictMode(PATTERN_G, seg)) {
                seg = seg.replaceAll(PATTERN_G, "$1");
            }
            //预筛选分类
            while (StringUtils.matchFindStrictMode(PATTERN_F, seg)) {
                mTrushWord.add(StringUtils.matcher(PATTERN_F, seg)[0]);
                seg = seg.replaceAll(PATTERN_F, "");
                Log.e("VideoNameTAG", "F=>" + seg);
                if (StringUtils.matchFindStrictMode(PATTERN_C_PREFIX, seg))
                    break;
                if (StringUtils.matchFindStrictMode(PATTERN_D, seg))
                    break;
            }
            if (StringUtils.matchFindStrictMode(PATTERN_B, seg)) {
                if (StringUtils.matchFindStrictMode(PATTERN_H, seg)) {
                    mInfo.pushPattern("H");
                } else {
                    mInfo.pushPattern("B");
                }
            } else if (StringUtils.matchFindStrictMode(PATTERN_H, seg)) {
                mInfo.pushPattern("H");
            } else if (StringUtils.matchFindStrictMode(PATTERN_E, seg)) {
                if (!StringUtils.matchFindStrictMode(PATTERN_SEASON_OR_EPISODE_OR_YEAR, seg)) {
                    String tmpSeg = seg.replaceAll(PATTERN_E, "");
                    if (!TextUtils.isEmpty(tmpSeg))
                        seg = tmpSeg;
                    Log.e("VideoNameTAG", "E=>" + seg);
                }
            }

            if (StringUtils.matchFind(PATTERN_A, seg))
                mInfo.pushPattern("A");
            if (StringUtils.matchFind(PATTERN_C, seg))
                mInfo.pushPattern("C");
            if (StringUtils.matchFind(PATTERN_D, seg))
                mInfo.pushPattern("D");
//            if (StringUtils.matchFind(PATTERN_D, seg))
//                mInfo.pushPattern("D");

//        先处理C
            if (mInfo.containPattern("C")) {
                processPatternCorD(PATTERN_C, seg);
            }

            if (mInfo.containPattern("D")) {
                processPatternCorD(PATTERN_D, seg);
            }

            if (mInfo.containPatterns("B", "A")) {
                processPatternA(seg);
            }

            if (mInfo.containPatterns("H", "A")) {
                processPatternHA(seg);
            }

            if (mInfo.onlyContainPattern("A")) {

                processPatternA(seg);
            }

            if (mInfo.patterns == null) {
                parserYear(seg);
                parserEpisode(seg);
                processPatternA(seg);
            }
        }

        String mayBeName = "";

        if (TextUtils.isEmpty(mInfo.name)) {
            if (mTrushWord.size() > 0)
                mayBeName = mTrushWord.get(0).replaceAll("\\[|\\]", "");
            else
                mayBeName = simplifyName(segments[0].substring(0, segments[0].lastIndexOf("."))).replaceAll("\\[|\\]", "");
            mInfo.autoSetName(mayBeName);
        }

        boolean canBeMovie = mInfo.getYear() != 0
                || mInfo.getDiskNumber() != 0
                || checkMovieKeywords(join("/", segments));
        if (mInfo.hasName()) {
            if (mInfo.hasAired()) {
                mInfo.setType(MovieNameInfo.TYPE_SERIES);
            }

            if (mInfo.saneSeason() &&(mInfo.saneEpisode()||mInfo.hasAired())) {
                mInfo.setType(MovieNameInfo.TYPE_SERIES);
            } else if (canBeMovie) {
                mInfo.setType(MovieNameInfo.TYPE_MOVIE);
            } else if (mInfo.getType() != null && mInfo.getType().equals(MovieNameInfo.TYPE_MOVIE) && mInfo.saneSeason())// Must be deprioritized compared to mapsList
            {
                mInfo.setType(MovieNameInfo.TYPE_EXTRAS);
            } else {
                mInfo.setType(MovieNameInfo.TYPE_OTHER);
            }

            if (mInfo.getType().equals(MovieNameInfo.TYPE_SERIES) || mInfo.getType().equals(MovieNameInfo.TYPE_EXTRAS)) {
                String cn_name = mInfo.getCName();
                cn_name = cn_name.replaceFirst("(.*):.*", "$1");
                mInfo.setCName(cn_name);
                String en_name = mInfo.getEName();
                en_name = en_name.replaceFirst("(.*):.*", "$1");
                mInfo.setEName(en_name);
            }
        } else {
            mInfo.setType(MovieNameInfo.TYPE_OTHER);
        }
        return mInfo;
    }


    private void processPatternCorD(String PATTERN_C, String seg) {
        String[] results = matcher(PATTERN_C, seg);
        String name = "";
        String year_or_episode = "";
        if (results.length == 3) {
            name = results[1];
            year_or_episode = results[2];
        }
        //名字
        if (!mInfo.hasName())
            mInfo.autoSetName(name.trim());
        //year
        int year = Year.parser(year_or_episode);
        if (year > 0) {
            mInfo.setYear(year);
        }
        //episode
        Episodes episodes = Episodes.parser(year_or_episode);
        if (episodes != null) {
            if (episodes.season != -1)
                mInfo.setSeason(episodes.season);
            if (mInfo.getYear() != episodes.episode && episodes.episode != -1) {
                mInfo.setEpisode(episodes.episode);
                if (episodes.toEpisode >= 0) {
                    mInfo.setEpisode(episodes.toEpisode);
                }
            }
        }

        if (StringUtils.matchFind(PATTERN_C, seg)) {
            String[] matches = StringUtils.matcher(PATTERN_C, seg);
            if (seg.startsWith(matches[0]))
                seg = seg.replace(matches[0], "");
        }


        if (seg.lastIndexOf(".") > -1) {
            seg = seg.substring(0, seg.lastIndexOf("."));//remove "."
        }

        String[] sourcePrefix = matcher("\\[(.*?)\\]", seg);
        if (sourcePrefix != null && sourcePrefix.length > 0) {
            for (int i = 0; i < (sourcePrefix.length) / 2; i++) {

                String value = sourcePrefix[i * 2 + 1];

                if (StringUtils.hasHttpUrl(value)) {
                    continue;
                }

                Country country = Country.parser(value);
                if (country != null) {
                    mInfo.setCountry(country.code);
                    continue;
                }

                year = Year.parser(value);
                if (year > 0) {
                    mInfo.setYear(year);
                    continue;
                }

                Source source = Source.parser(value);
                if (source != null) {
                    if (source.name.equals(Source.BD_NAME) || source.name.equals(Source.DVD_NAME))
                        mInfo.setVideoSource(source.name);
                    continue;
                }

                Resolution resolution = Resolution.parser(value);
                if (resolution != null) {
                    mInfo.setResolution(resolution.tag);
                    continue;
                }


                VideoCodec videoCodec = VideoCodec.parser(value);
                if (videoCodec != null) {
                    mInfo.setVideoCodec(videoCodec.codec);
                    continue;
                }

                AudioCodec audioCodec = AudioCodec.parser(value);
                if (audioCodec != null) {
                    mInfo.setAudioCodec(audioCodec.codec);
                    continue;
                }

                FileSize fileSize = FileSize.parser(value);
                if (fileSize != null) {
                    mInfo.setFileSize(fileSize.size);
                    continue;
                }

                if (SubTitle.parser(value)) {
                    continue;
                }

                OtherItem otherItem = OtherItem.parser(value);
                if (otherItem != null) {
                    continue;
                }
            }
        }
        processPatternA(seg);
    }

    private void processPatternHA(String seg) {
        String[] results = matcher(PATTERN_H, seg);
        String name = "";
        String aired = "";
        if (results.length == 4) {
            name = results[1];
            if (name.length() - 1 == name.lastIndexOf(".")) {
                name = name.substring(0, name.length() - 1);
            }
            name = name.replaceAll(SEGMENTS_SPLIT, " ");
            aired = results[2];
            seg=results[3];
        }
        //名字
        if (!mInfo.hasName())
            mInfo.autoSetName(name.trim());

       if(!mInfo.hasAired())
           mInfo.setAired(aired);

        if (StringUtils.matchFind(PATTERN_B, seg)) {
            String[] matches = StringUtils.matcher(PATTERN_B, seg);
            if (seg.startsWith(matches[0]))
                seg = seg.replace(matches[0], "");
        }

        if (seg.lastIndexOf(".") > -1) {
            seg = seg.substring(0, seg.lastIndexOf("."));//remove "."
        }


        seg = seg.replaceAll("[\\[\\]]", " ").trim();
        String[] keywords = seg.split(SEGMENTS_SPLIT);
        for (String keyword : keywords) {
            if (StringUtils.hasHttpUrl(keyword)) {
                continue;
            }

            Country country = Country.parser(keyword);
            if (country != null) {
                mInfo.setCountry(country.code);
                continue;
            }

            Source source = Source.parser(keyword);
            if (source != null) {
                if (source.name.equals(Source.BD_NAME) || source.name.equals(Source.DVD_NAME))
                    mInfo.setVideoSource(source.name);
                continue;
            }

            Resolution resolution = Resolution.parser(keyword);
            if (resolution != null) {
                //Log.v("sjfq", "resolution removeWords:"+key);
                mInfo.setResolution(resolution.tag);
                continue;
            }


            VideoCodec videoCodec = VideoCodec.parser(keyword);
            if (videoCodec != null) {
                mInfo.setVideoCodec(videoCodec.codec);
                continue;
            }

            AudioCodec audioCodec = AudioCodec.parser(keyword);
            if (audioCodec != null) {
                mInfo.setAudioCodec(audioCodec.codec);
                continue;
            }

            FileSize fileSize = FileSize.parser(keyword);
            if (fileSize != null) {
                mInfo.setFileSize(fileSize.size);
                continue;
            }

        }


    }

    private void processPatternBA(String seg) {
        String[] results = matcher(PATTERN_B, seg);
        String name = "";
        String yearStr = "";
        if (results.length == 3) {
            name = results[1];
            if (name.length() - 1 == name.lastIndexOf(".")) {
                name = name.substring(0, name.length() - 1);
            }
            name = name.replaceAll(SEGMENTS_SPLIT, " ");
            String[] httpPreTests = matcher(PATTERN_E, seg);
            yearStr = results[2];
        }
        //名字
        if (!mInfo.hasName())
            mInfo.autoSetName(name.trim());
        //year
        int year = Year.parser(yearStr);
        if (year > 0) {
            mInfo.setYear(year);
        }

        if (StringUtils.matchFind(PATTERN_B, seg)) {
            String[] matches = StringUtils.matcher(PATTERN_B, seg);
            if (seg.startsWith(matches[0]))
                seg = seg.replace(matches[0], "");
        }

        if (seg.lastIndexOf(".") > -1) {
            seg = seg.substring(0, seg.lastIndexOf("."));//remove "."
        }


        seg = seg.replaceAll("[\\[\\]]", " ").trim();
        String[] keywords = seg.split(SEGMENTS_SPLIT);
        for (String keyword : keywords) {
            if (StringUtils.hasHttpUrl(keyword)) {
                continue;
            }

            Country country = Country.parser(keyword);
            if (country != null) {
                mInfo.setCountry(country.code);
                continue;
            }

            Episodes episodes = Episodes.parser(keyword);
            if (episodes != null) {
                if (episodes.season != -1)
                    mInfo.setSeason(episodes.season);
                if (mInfo.getYear() != episodes.episode && episodes.episode != -1) {
                    mInfo.setEpisode(episodes.episode);
                    if (episodes.toEpisode >= 0) {
                        mInfo.setEpisode(episodes.toEpisode);
                    }
                }
                if (episodes.isMatch()) {
                    continue;
                } else if (episodes.saneEpisodes()) {
                    continue;
                }
            }

            Source source = Source.parser(keyword);
            if (source != null) {
                if (source.name.equals(Source.BD_NAME) || source.name.equals(Source.DVD_NAME))
                    mInfo.setVideoSource(source.name);
                continue;
            }

            Resolution resolution = Resolution.parser(keyword);
            if (resolution != null) {
                //Log.v("sjfq", "resolution removeWords:"+key);
                mInfo.setResolution(resolution.tag);
                continue;
            }


            VideoCodec videoCodec = VideoCodec.parser(keyword);
            if (videoCodec != null) {
                mInfo.setVideoCodec(videoCodec.codec);
                continue;
            }

            AudioCodec audioCodec = AudioCodec.parser(keyword);
            if (audioCodec != null) {
                mInfo.setAudioCodec(audioCodec.codec);
                continue;
            }

            FileSize fileSize = FileSize.parser(keyword);
            if (fileSize != null) {
                mInfo.setFileSize(fileSize.size);
                continue;
            }

            if (SubTitle.parser(keyword)) {
                //Log.v("sjfq", "SubTitle removeWords:"+key);
                continue;
            }

            OtherItem otherItem = OtherItem.parser(keyword);
            if (otherItem != null) {
                continue;
            }

        }


    }

    private void processPatternA(String seg) {
        /* Remove extension */
        if (seg.lastIndexOf(".") > -1) {
            seg = seg.substring(0, seg.lastIndexOf("."));//remove "."
        }

        String[] sourcePrefix = reverse(matcher("\\[(.*?)\\]", seg));

        ArrayList<String> removeWords = new ArrayList<>();
        ArrayList<String> tmpNameParts = new ArrayList<>();
        if (sourcePrefix != null && sourcePrefix.length > 0) {
            for (int i = 0; i < (sourcePrefix.length) / 2; i++) {

                String key = sourcePrefix[i * 2 + 1];
                String value = sourcePrefix[i * 2];
                String[] words = value.split("\\.| ");
                //Log.v("sjfq", "key:"+key);
                if (words.length > 1) {
                    for (int j = 0; j < words.length; j++) {
                        String word = words[j];
                        if (StringUtils.hasHttpUrl(word)) {
                            removeWords.add(word);
                            continue;
                        }

                        Country country = Country.parser(word);
                        if (country != null) {
                            //Log.v("sjfq", "setCountry removeWords:"+key);
                            mInfo.setCountry(country.code);
                            removeWords.add(word);
                            continue;
                        }

                        int year = Year.parser(word);
                        if (year > 0) {
                            //Log.v("sjfq", "Year removeWords:"+key);
                            mInfo.setYear(year);
                            removeWords.add(word);
                            continue;
                        }

                        Episodes episodes = Episodes.parser(word);
                        if (episodes != null) {
                            if (episodes.season != -1)
                                mInfo.setSeason(episodes.season);
                            if (mInfo.getYear() != episodes.episode && episodes.episode != -1) {
                                mInfo.setEpisode(episodes.episode);
                                if (episodes.toEpisode >= 0) {
                                    mInfo.setEpisode(episodes.toEpisode);
                                }
                            }
                            if (episodes.isMatch()) {
                                removeWords.addAll(episodes.getMatchList());
                                continue;
                            }
                        }

                        Source source = Source.parser(word);
                        if (source != null) {
                            if (source.name.equals(Source.BD_NAME) || source.name.equals(Source.DVD_NAME)) {
                                mInfo.setVideoSource(source.name);
                                removeWords.add(word);
                            }
                            continue;
                        }

                        Resolution resolution = Resolution.parser(word);
                        if (resolution != null) {
                            //Log.v("sjfq", "resolution removeWords:"+key);
                            mInfo.setResolution(resolution.tag);
                            removeWords.add(word);
                            continue;
                        }


                        VideoCodec videoCodec = VideoCodec.parser(word);
                        if (videoCodec != null) {
                            mInfo.setVideoCodec(videoCodec.codec);
                            removeWords.add(word);
                            continue;
                        }

                        AudioCodec audioCodec = AudioCodec.parser(word);
                        if (audioCodec != null) {
                            mInfo.setAudioCodec(audioCodec.codec);
                            removeWords.add(word);
                            continue;
                        }

                        FileSize fileSize = FileSize.parser(word);
                        if (fileSize != null) {
                            mInfo.setFileSize(fileSize.size);
                            removeWords.add(word);
                            continue;
                        }

                        if (SubTitle.parser(word)) {
                            //Log.v("sjfq", "SubTitle removeWords:"+key);
                            removeWords.add(word);
                            continue;
                        }

                        OtherItem otherItem = OtherItem.parser(word);
                        if (otherItem != null) {
                            removeWords.add(word);
                            continue;
                        }
                    }
                } else {
                    if (StringUtils.hasHttpUrl(value)) {
                        //Log.v("sjfq", "hasHttpUrl removeWords:"+key);
                        removeWords.add(key);
                        continue;
                    }

                    Country country = Country.parser(value);
                    if (country != null) {
                        //Log.v("sjfq", "setCountry removeWords:"+key);
                        mInfo.setCountry(country.code);
                        removeWords.add(key);
                        continue;
                    }

                    int year = Year.parser(value);
                    if (year > 0) {
                        //Log.v("sjfq", "Year removeWords:"+key);
                        mInfo.setYear(year);
                        removeWords.add(String.valueOf(year));
                        continue;
                    }

                    Episodes episodes = Episodes.parser(value);
                    if (episodes != null) {
                        if (episodes.season != -1)
                            mInfo.setSeason(episodes.season);
                        if (mInfo.getYear() != episodes.episode && episodes.episode != -1) {
                            mInfo.setEpisode(episodes.episode);
                            if (episodes.toEpisode >= 0) {
                                mInfo.setEpisode(episodes.toEpisode);
                            }
                        }
                        if (episodes.isMatch()) {
                            removeWords.addAll(episodes.getMatchList());
                            continue;
                        }
                    }

                    Source source = Source.parser(value);
                    if (source != null) {
                        if (source.name.equals(Source.BD_NAME) || source.name.equals(Source.DVD_NAME)) {
                            mInfo.setVideoSource(source.name);
                            removeWords.add(key);
                            continue;
                        }
                    }

                    Resolution resolution = Resolution.parser(value);
                    if (resolution != null) {
                        //Log.v("sjfq", "resolution removeWords:"+key);
                        mInfo.setResolution(resolution.tag);
                        removeWords.add(key);
                        continue;
                    }


                    VideoCodec videoCodec = VideoCodec.parser(value);
                    if (videoCodec != null) {
                        mInfo.setVideoCodec(videoCodec.codec);
                        removeWords.add(key);
                        continue;
                    }

                    AudioCodec audioCodec = AudioCodec.parser(value);
                    if (audioCodec != null) {
                        mInfo.setAudioCodec(audioCodec.codec);
                        removeWords.add(key);
                        continue;
                    }

                    FileSize fileSize = FileSize.parser(value);
                    if (fileSize != null) {
                        mInfo.setFileSize(fileSize.size);
                        removeWords.add(key);
                        continue;
                    }

                    if (SubTitle.parser(value)) {
                        //Log.v("sjfq", "SubTitle removeWords:"+key);
                        removeWords.add(key);
                        continue;
                    }

                    OtherItem otherItem = OtherItem.parser(value);
                    if (otherItem != null) {
                        removeWords.add(key);
                        continue;
                    }

                    removeWords.add(key);
                }
            }
            if(removeWords.size()>0){
                seg = seg.replaceAll("\\[(.*?)\\]", "");
            }else {
                seg = seg.replaceAll("\\.\\[", "\\[")
                        .replaceAll("\\[", "\\.\\[");
            }
            tmpNameParts.clear();
            removeWords.clear();
        }

        String[] segSplit = reverse(seg.split("\\.|-|;|_"));//空格不分开

        for (int i = 0; i < segSplit.length; i++) {

            String value = segSplit[i];
            //Log.v("sjfq", "key:"+key);

            if (StringUtils.hasHttpUrl(value)) {
                //Log.v("sjfq", "hasHttpUrl removeWords:"+key);
                removeWords.add(value);
                removeWords.addAll(tmpNameParts);
                continue;
            }


            Country country = Country.parser(value);
            if (country != null) {
                //Log.v("sjfq", "setCountry removeWords:"+key);
                mInfo.setCountry(country.code);
                removeWords.add(value);
                removeWords.addAll(tmpNameParts);
                tmpNameParts.clear();
                continue;
            }

            String aired = parserAired(value);
            if (!TextUtils.isEmpty(aired)) {
                removeWords.add(aired);
                removeWords.addAll(tmpNameParts);
                value = value.replace(aired, "");
                tmpNameParts.clear();
            }

            if(mInfo.getYear()==0) {
                int year = Year.parser(value);
                if (year > 0) {
                    //Log.v("sjfq", "Year removeWords:"+key);
                    mInfo.setYear(year);
                    removeWords.add(String.valueOf(year));
                    removeWords.addAll(tmpNameParts);
                    tmpNameParts.clear();
                    value = value.replaceAll(String.valueOf(year), "");
                }
            }

            Episodes episodes = Episodes.parser(value);
            if (episodes != null) {
                if (episodes.season != -1)
                    mInfo.setSeason(episodes.season);
                if (mInfo.getYear() != episodes.episode && episodes.episode != -1) {
                    mInfo.setEpisode(episodes.episode);
                    if (episodes.toEpisode >= 0) {
                        mInfo.setEpisode(episodes.toEpisode);
                    }
                }
                if (episodes.isMatch()) {
                    removeWords.addAll(episodes.getMatchList());
                    removeWords.addAll(tmpNameParts);
                    tmpNameParts.clear();
                    continue;
                }
            }

            Source source = Source.parser(value);
            if (source != null) {
                if (source.name.equals(Source.BD_NAME) || source.name.equals(Source.DVD_NAME)) {
                    mInfo.setVideoSource(source.name);
                    removeWords.add(value);
                    removeWords.addAll(tmpNameParts);
                    tmpNameParts.clear();
                    continue;
                }
            }

            Resolution resolution = Resolution.parser(value);
            if (resolution != null) {
                //Log.v("sjfq", "resolution removeWords:"+key);
                mInfo.setResolution(resolution.tag);
                removeWords.add(value);
                removeWords.addAll(tmpNameParts);
                tmpNameParts.clear();
                continue;
            }


            VideoCodec videoCodec = VideoCodec.parser(value);
            if (videoCodec != null) {
                mInfo.setVideoCodec(videoCodec.codec);
                removeWords.add(value);
                removeWords.addAll(tmpNameParts);
                tmpNameParts.clear();
                continue;
            }

            AudioCodec audioCodec = AudioCodec.parser(value);
            if (audioCodec != null) {
                mInfo.setAudioCodec(audioCodec.codec);
                removeWords.add(value);
                removeWords.addAll(tmpNameParts);
                tmpNameParts.clear();
                continue;
            }

            FileSize fileSize = FileSize.parser(value);
            if (fileSize != null) {
                mInfo.setFileSize(fileSize.size);
                removeWords.add(value);
                removeWords.addAll(tmpNameParts);
                tmpNameParts.clear();
                continue;
            }

            if (SubTitle.parser(value)) {
                //Log.v("sjfq", "SubTitle removeWords:"+key);
                removeWords.add(value);
                removeWords.addAll(tmpNameParts);
                tmpNameParts.clear();
                continue;
            }

            OtherItem otherItem = OtherItem.parser(value);
            if (otherItem != null) {
                removeWords.add(value);
                continue;
            }

            tmpNameParts.add(value);
        }
        String removeResult = StringUtils.removeAll(seg, removeWords).trim();
        if (!TextUtils.isEmpty(removeResult)) {
            seg = removeResult;
        } else if (removeWords.size() > 0) {
            seg = removeWords.remove(removeWords.size() - 1);
        }

        seg = seg.replaceAll("\\.|-|;|_", " ")
                .replaceAll("([a-z])([A-Z])", "$1 $2");

        segSplit = seg.split(SEGMENTS_SPLIT);

        /* No need to go further;  */
        if (!TextUtils.isEmpty(mInfo.name))
            return;

        ArrayList<String> nameParts = new ArrayList<String>();
        int lastIndex = -1;
        for (int i = 0; i < segSplit.length; i++) {

            String word = segSplit[i];
            //Log.v(TAG, "word:"+word);
            lastIndex = i;
            /* words with basic punctuation and two-digit numbers; or numbers in the first position */
            String[] x = {"ep", "episode", "season"};
            if (!(isChinese(word)
                    || word.matches("^[a-zA-Z,?!'&]*$")
                    || (!isNaN(word) && word.length() <= 2)
                    || (!isNaN(word) && i == 0)
                    || (Year.parser(word)>0&&mInfo.getYear()>0))
                    //                || contain(excluded,word.toLowerCase())
                    || ((indexOf(x, word.toLowerCase()) > -1) && !isNaN(segSplit[i + 1]))
                    || indexOf(movieKeywords, word.toLowerCase()) > -1) // TODO: more than that, match for stamp too
                continue;
            nameParts.add(word);
        }
        //Log.v(TAG, "nameParts.size():"+nameParts.size());
        if (nameParts.size() == 0)
            nameParts.add(seg);

        ArrayList<String> parts = new ArrayList<String>();
        for (String part : nameParts) {
            //Log.v(TAG, "part:"+part);
            if (part != null && part.length() > 0) {
                parts.add(part.substring(0, 1).toUpperCase() + part.substring(1).toLowerCase());
            }
        }
        String name = join(" ", parts.toArray(new String[0]));

        if (!mInfo.hasName())
            mInfo.autoSetName(name.trim());
    }

    private boolean hasEnoughInfo() {
        if (mInfo == null)
            return false;
        return mInfo.hasName() && mInfo.saneSeason() && mInfo.saneEpisode();
    }

    /*
     * Test for a year in the name
     * */
    private final static String MATCH_YEAR_REGEX = "[^\\dA-Za-z](19[0-9][0-9]|20[0-9][0-9])[^\\dA-Za-z]";//匹配４位数字,范围为1900-2099

    private String parserYear(String seg) {
        String[] numbers = matcher(MATCH_YEAR_REGEX, seg);
        if (numbers != null && numbers.length > 1) {
            //Log.v(TAG, "year:"+numbers[1]);
            mInfo.setYear(Integer.parseInt(numbers[1]));
            return numbers[1];
        }
        return null;
    }

    /*
     * Test for "aired" stamp; if aired stamp is there, we have a series
     */
    private final static String MATCH_AIRED_REGEX = "(19[0-9][0-9]|20[0-9][0-9])(\\.|-| )?(\\d\\d)(\\.|-| )?(\\d\\d)";//匹配４位数字,范围为1900-2099

    private String parserAired(String seg) {
        String[] aired = matcher(MATCH_AIRED_REGEX, Pattern.CASE_INSENSITIVE, seg);
        if (aired != null && aired.length > 0) {
            //Log.v(TAG, "aired:"+aired[0]);
            //			String year = aired[1];
            //			String month = aired[3];
            //			String day = aired[5];
            mInfo.setAired(aired[0]);
            return aired[0];
        }
        return null;
    }

    /*
     * A typical pattern - "s05e12", "S01E01", etc. ; can be only "E01"
     * Those are matched only in the file name
     *
     * TODO: this stamp may be in another segment (e.g. directory name)
     *
     * season,episode,
     * */
    private void parserEpisode(String seg) {
        String[] splits = seg.split("\\.| |_");
        for (String split : splits) {
            //Log.v(TAG, "split:"+split);
            String season_regex = "^" + build_or_pattern(SEASON_WORDS) + "(\\d{1,2})";//"S(\\d{1,2})"
            String[] seasonMatch = matcher(season_regex, Pattern.CASE_INSENSITIVE, split);
            if (seasonMatch != null && seasonMatch.length > 1) {
                mInfo.setSeason(Integer.parseInt(seasonMatch[1]));
            }
            String episode_regex = build_or_pattern(EPISODE_WORDS) + "(\\d{1,2})(?:-(\\d{1,2}))?";//"E(\\d{1,2})"
            String[] episodeMatch = matcher(episode_regex, Pattern.CASE_INSENSITIVE, split);
            if (episodeMatch != null && episodeMatch.length > 1) {
                ArrayList<Integer> episode = new ArrayList<Integer>();
                if (episodeMatch[0].contains("-")) {
                    if (episodeMatch.length == 3) {
                        for (int i = Integer.parseInt(episodeMatch[1]); i <= Integer.parseInt(episodeMatch[2]); i++) {
                            episode.add(i);
                        }
                    }
                } else {
                    for (int i = 1; i < episodeMatch.length; i++) {
                        episode.add(Integer.parseInt(episodeMatch[1]));
                    }
                }
                mInfo.setEpisode(episode);
            }

            String[] xStampMatch = matcher("(\\d\\d?)x(\\d\\d?)", Pattern.CASE_INSENSITIVE, split);

        }
        String[] fullMatch = matcher("^([a-zA-Z0-9,-?!'& ]*) S(\\d{1,2})E(\\d{2})", seg.replace("\\.| |;|_", " "));
        //if (TextUtils.isEmpty(mInfo.getName()) && meta.season && meta.episode && fullMatch && fullMatch[1]) meta.name = fullMatch[1];
    }

    private String[] matcher(String regex, String input) {
        return matcher(regex, Pattern.CASE_INSENSITIVE, input);
    }

    private String[] matcher(String regex, int flag, String input) {
        Pattern pattern = Pattern.compile(regex, flag);
        Matcher matcher = pattern.matcher(input);
        List<String> list = new ArrayList<String>();
        while (matcher.find()) {
            for (int i = 0; i <= matcher.groupCount(); i++) {
                list.add(matcher.group(i));
            }
        }

        return list.toArray(new String[0]);
    }

    private static String[] reverse(String[] Array) {
        String[] new_array = new String[Array.length];
        for (int i = 0; i < Array.length; i++) {
            // 反转后数组的第一个元素等于源数组的最后一个元素：
            new_array[i] = Array[Array.length - i - 1];
        }
        return new_array;
    }

    private static String[] slice(String[] Array, int start, int end) {
        if (end >= Array.length - 1) end = Array.length - 1;
        if (start < 0) start = 0;
        int length = end - start + 1;
        String[] new_array = new String[length];
        for (int i = 0; i < length; i++) {
            // 反转后数组的第一个元素等于源数组的最后一个元素：
            new_array[i] = Array[i]; //????
        }
        return new_array;
    }


    public static String join(String join, String[] strAry) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < strAry.length; i++) {
            if (i == (strAry.length - 1)) {
                sb.append(strAry[i]);
            } else {
                sb.append(strAry[i]).append(join);
            }
        }
        return new String(sb);
    }

    public static boolean isNaN(String num) {
        if (!TextUtils.isEmpty(num) && TextUtils.isDigitsOnly(num)) {
            return false;
        }
        return true;
    }

    public static boolean isChinese(String word) {
        if (word == null)
            return false;
        for (char c : word.toCharArray()) {
            if (c >= 0x4E00 && c <= 0x9FA5)    // 根据字节码判断
                return true;// 有一个中文字符就返回
        }
        return false;
    }

    public static boolean isOnlyChinese(String word) {
        if (word == null)
            return false;
        for (char c : word.toCharArray()) {
            if (c >= 0x4E00 && c <= 0x9FA5)    // 根据字节码判断
                continue;
            else
                return false;
        }
        return true;
    }

    public static boolean contain(String[] array, String str) {
        if (array == null || array.length == 0) return false;
        List<String> list = Arrays.asList(array);
        return list.contains(str);
    }

    public static int indexOf(String[] array, String str) {
        if (array == null || array.length == 0) return -1;
        List<String> list = Arrays.asList(array);
        return list.indexOf(str);
    }

    public static boolean checkMovieKeywords(String str) {
        if (str == null || str.length() == 0) return false;
        for (String keyWord : movieKeywords) {
            if (str.toLowerCase().indexOf(keyWord) > -1) {
                return true;
            }
        }
        return false;
    }

    private static String build_or_pattern(String[]... patterns) {
        StringBuilder result = new StringBuilder();
        for (String[] strings : patterns) {
            for (String string : strings) {
                if (result.length() == 0) {
                    result.append("(?");
                    result.append(":");
                } else {
                    result.append("|");
                }
                result.append(StringUtils.escapeExprSpecialWord(string));
//					result.append(string);
                //result.append(String.format("(?:%s)",string));
            }

        }
        result.append(")");
        if (result.length() == 0) return null;
        return result.toString();
    }
}
