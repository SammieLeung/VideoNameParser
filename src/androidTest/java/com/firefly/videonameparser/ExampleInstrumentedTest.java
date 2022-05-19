package com.firefly.videonameparser;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.firefly.videonameparser.bean.Episodes;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    public static final String TAG="VideoNameTAG";
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.firefly.videonameparser", appContext.getPackageName());
    }

    @Test
    public void testNameParse() {
        debug2("[BD影视分享bd2020.co]青春变形记.Turning.Red.2022.AAC5.1.HD1080P.国粤英三语.中字.mp4");
        debug2("蜘Z侠：Y雄无归.Spitder.Man.No.Way.Home.2021.BD720P.X264.AAC.English.CHS-ENG.BDYS.mp4");
        debug2("Death.Note.TV.2006.DVDRip.Ep03.Rev2.x264.AC3-Jina");
        debug2("[dmhy][Death_Note][32].mp4");
        debug2("[Death_Note][06].mkv");
        debug2("西部世界第三季.mp4");
        debug2("新世纪福音战士：终.mp4");
        debug2("新世纪福音战士：终.mp4");
        debug2("[迅雷下载www.xiamp4.com]铁血孤儿[第05话].mp4");
        debug2("狮子王.The.Lion.King.1994.BD1080P.X264.AAC.Mandarin&English.CHS-ENG.mp4");
        debug2("Braveheart.1995.2160p.BluRay.x265.10bit.SDR.DTS-HD.MA.TrueHD.7.1.Atmos-SWTYBLZ");
        debug2("[www.domp4.com]跛豪.1991.BD1080p.国粤双语.mp4");
        debug2("/storage/emulated/0/Download/[www.domp4.com]跛豪.1991.BD1080p.国粤双语/[www.domp4.com]跛豪.1991.BD1080p.国粤双语.mp4");
        debug2("[Death_Note][32][ms32].mp4");
        debug2("Death.Note.TV.2006.DVDRip.e03.Rev2.x264.AC3-Jina");
//        debug2("Death.Note.TV.2006.DVDRip.ep03.Rev2.x264.AC3-Jina");
//        debug2("Death.Note.TV.2006.DVDRip.eP03.Rev2.x264.AC3-Jina");
//        debug2("Death.Note.TV.2006.DVDRip.E03.Rev2.x264.AC3-Jina");
        debug2("[2017][HYSUB]ONE PUNCH MAN[10][GB_MP4][720P].mp4");
//        debug2("[2017][HYSUB]ONE PUNCH MAN[10][GB_MP4][1280X720].mp4");
        debug2("2006【大兴奋!三日月岛的动物骚动!】.mp4");
        debug2("Code Geass ~Hangyaku no Lelouch R2~ 07 (BDRip 1280x720)-muxed.mp4");
        debug2("[www.BTxiaba.com]狂暴巨兽.Rampage.2018.1080p.WEB-DL.X264.AAC.CHS.ENG-BTxiaba&远鉴字幕组.mp4");
        debug2("[导火新闻线][BluRay-720P.MKV][2.67GB][国粤双语][LC-AAC.5.1].mkv");
        debug2("[dmhy][Macross Delta][21][BIG5][720P_MP4][BS11].mp4");
        debug2("[dmhy]I lov esd[82][BIG5][720P_MP4][BS11].mp4");
        debug2("The.Jungle.Book.2016.1080p.3D.BluRay.AVC.DTS-HD.MA.7.1-FGT.iso");
        debug2("The Jungle Book 2020.mp4");
        debug2("Halo.S01E01-04.2160p.WEB-DL.DDP5.1.Atmos.DV.MP4.x265-DVSUX[rartv]");
        debug2("雪中悍刀行[全38集][国语配音+中文字幕].Sword.Snow.Stride.S01.2021.2160p.WEB-DL.AAC.H265-EntWEB");
        debug2("Sword,Snow,Stride.EP01-38.2021.1080p.50FPS.HDR.WEB-DL.HEVC.AAC.10bit-HQC");
        debug2("Sword,Snow,Stride.EP01-06.2021.1080p.WEB-DL.x264.AAC-HQC");
        debug2("Penthouse.S03E01.HD1080P.X264.AAC.Korean.CHS.BDE4.mp4");
        debug2("鱿鱼游戏.Squid.Game.2021.EP01.HD1080P.X264.AAC.Korean.CHS.Mp4er.mp4");
        debug2("致命女人.Why.Women.Kill.S01.S01E01.HD1080P.X264.AAC.English.CHS-ENG.mp4");
        debug2("[悠哈璃羽字幕社&LoliHouse] 辉夜大小姐想让我告白 _ Kaguya-sama wa Kokurasetai - 01 [WebRip 1080p HEVC-10bit AAC][简繁内封字幕].mp4");
        debug2("[SBSUB][CONAN][988][1080P][AVC_AAC][CHS_JP](00B82A9E).mp4");
        debug2("[VCB-Studio] Himouto! Umaru-chan [01][Ma10p_1080p][x265_2flac].mkv");
        debug2("[LoliHouse] Tenki No Ko [BDRip 3840x2160 HEVC-10bit HDR FLAC PGS(chs,eng,jpn)].mkv");
        debug2("阳光电影ygdy8.北欧人.2022.BD.1080P.中英双字.mkv");
        debug2("www.dgy8.com[房子][2022][BD][1080P].mp4");
        debug2("www.dgy8.com[房子][第一季][BD][1080P].mp4");
        debug2("www.dgy8.com[房子][S01E03][BD][1080P].mp4");
        debug2("www.dgy8.com[房子][Ep03][BD][1080P].mp4");

        debug2("[BD影视分享bd2020.co]青春变形记.Turning.Red.2022.AAC5.1.HD1080P.国粤英三语.中字.mp4");
        debug2("[BT乐园.bt606.com]名侦探洪吉童:消失的村庄2016.HD720P.X264.AAC.韩语中字.mp4");
        debug2("www.dgy8.com.房子.2022.BD.1080P.mp4");
        debug2("[動漫國字幕組]01月新番[輝夜姬想讓人告白_天才們的戀愛頭腦戰_][01][720P][繁體][MP4]");
        debug2("[蚂蚁网www.mayi.tw]审死官CD2.mp4");
        debug2("[远鉴字幕组andOrange字幕组]金蝉脱壳2【蓝光版特效中英双字】Escape.Plan.2.Hades.2018.1080p.BluRay.x264.DTS-HDC@CHDbits.mkv");
        debug2("[Mobile Suit Gundam Seed Destiny HD REMASTER][46][Big5][720p][AVC_AAC][encoded by SEED].mp4");
        debug2("BT乐园.bt606.co.名侦探洪吉童:消失的村庄2016.HD720P.X264.AAC.韩语中字.mp4");
        debug2("[domp4]北o人.2022.HD1080p.中英双字.mp4 ");
        debug2("The.Batman.2022.2160p.MA.WEB-DL.DDP5.1.Atmos.DV.MP4.x265-DVSUX.mkv");
        debug2("The_Batman_2022_2160p_MA_WEB-DL_DDP5.1_Atmos_DV_MP4_x265-DVSUX.mkv");
        debug2("北欧人.The.north.man.2022.BD.1080P.mp4");
        debug2("北欧人.2022.BD.1080P.中英双字.mkv");
//        Episodes.parser("第1集");
//        Episodes.parser("第一百五十六集");
//        Episodes.parser("壹");
//        Episodes.parser("第叄話");
//        Episodes.parser("第二季");
//        Episodes.parser("第二部第三话");
//        Episodes.parser("E1");
//        Episodes.parser("Ep2");
//        Episodes.parser("eP3");
//        Episodes.parser("EP04");
//        Episodes.parser("EP03-04");
//        Episodes.parser("d5");
//        Episodes.parser("S0329");
//        Episodes.parser("S3E234");
    }

    public void debug2(String name) {
        VideoNameParser2 mParser2 = new VideoNameParser2();
        MovieNameInfo info2 = mParser2.parseVideoName(name);
        log("name="+name+" : "+(info2.patterns!=null?info2.patterns.toString():"===None==="));
        logw(info2.toString());
//        logw(info2.patterns!=null?info2.patterns.toString():"===None===");

    }
    public void debug(String name) {
        VideoNameParser mParser = new VideoNameParser();
        MovieNameInfo info2 = mParser.parseVideoName(name);
        log("name="+name);
        logw(info2.tags.toString());

    }
    private void log(String msg) {
        Log.i(TAG,msg);
    }

    private void logw(String msg) {
        Log.w(TAG,msg);
    }
}