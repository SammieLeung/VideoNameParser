package com.firefly.videonameparser;

import android.text.TextUtils;

import com.firefly.videonameparser.bean.Resolution;
import com.firefly.videonameparser.bean.Source;
import com.firefly.videonameparser.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class MovieNameInfo {
    public static String TYPE_SERIES = "series";
    public static String TYPE_MOVIE = "movie";
    public static String TYPE_EXTRAS = "extras";
    public static String TYPE_OTHER = "other";

    //file extension
    String extension;
    // parsed name, in lower case, allowed numbers/letters, no special symbols
    String name;
    //
    String name_cn;
    // can be movie, series or other - inferred from keywords / key phrases
    String type;
    // additional tags inferred from the name, e.g. 1080p
    ArrayList<String> tags;
    // - number of the season
    int season = -1;
    //- array of episode numbers, returned for episodes
    ArrayList<Integer> episodes;
    int diskNumber = 0;
    int year = 0;

    String aired;

    String country;

    String videoCodec;

    String audioCodec;

    String fileSize;

    ArrayList<String> patterns;

    String resolution;
    String video_source;

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getAudioCodec() {
        return audioCodec;
    }

    public void setAudioCodec(String audioCodec) {
        this.audioCodec = audioCodec;
    }

    public String getVideoCodec() {
        return videoCodec;
    }

    public void setVideoCodec(String videoCodec) {
        this.videoCodec = videoCodec;
    }

    public void setEpisode(ArrayList<Integer> episodes) {
        this.episodes = episodes;
    }

    public void setEpisode(int episode) {
        if (episodes == null) episodes = new ArrayList<Integer>();
        if (!episodes.contains(episode)) {
            if (episodes.size() <= 1)
                episodes.add(episode);
            else {
                int ep = episodes.get(1);
                if (episode > ep)
                    episodes.set(1, episode);
            }

        }
        Collections.sort(episodes);
    }

    public ArrayList<Integer> getEpisode() {
        return episodes;
    }

    public boolean saneEpisode() {
        return (episodes != null) && (episodes.size() > 0);
    }

    public int toEpisode() {
        if (saneEpisode()) {
            return episodes.get(0);
        }
        return -1;
    }

    public String getName() {
        if (Locale.getDefault().getLanguage().equals(Locale.SIMPLIFIED_CHINESE.getLanguage()))
            return name_cn;
        else
            return name;
    }

    public String getCName() {
        return name_cn;
    }

    public String getEName() {
        return name;
    }

    public void setEName(String name) {
        if (TextUtils.isEmpty(name))
            return;
        this.name = name;
    }

    public void setCName(String name) {
        if (TextUtils.isEmpty(name))
            return;
        this.name_cn = name;
    }

    public void autoSetName(String name) {
        if (TextUtils.isEmpty(name))
            return;
        String[] nameParts = name.split(" ");
        StringBuffer sb_cn = new StringBuffer();
        if (StringUtils.matchGB2312(name)) {
            for (String namePart : nameParts) {
                if (StringUtils.matchGB2312(namePart)) {
                    sb_cn.append(StringUtils.getOnlyGB2312AndInt(namePart));
                }
            }
            this.name_cn = sb_cn.toString();
            if (nameParts.length == 1) {
                this.name = this.name_cn;
            } else if (name.length() - 1 == name.indexOf(sb_cn.charAt(sb_cn.length() - 1))) {
                this.name = name_cn;
            } else {
                int i = sb_cn.length() - 1;
                char s = sb_cn.charAt(i);
                int k = name.indexOf(s);
                this.name = name.substring(name.indexOf(sb_cn.charAt(sb_cn.length() - 1)) + 1).trim();
            }
        } else {
            this.name = name;
            this.name_cn = name;
        }

        if (this.name != null)
            this.name = this.name.trim();
        if (this.name_cn != null)
            this.name_cn = this.name_cn.trim();
    }

    public String getAired() {
        return aired;
    }

    public void setAired(String aired) {
        this.aired = aired;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getSeason() {
        return season;
    }

    public void setSeason(int season) {
        this.season = season;
    }

    public boolean saneSeason() {
        return season != -1;
    }

    public int getDiskNumber() {
        return diskNumber;
    }

    public void setDiskNumber(int diskNumber) {
        this.diskNumber = diskNumber;
    }

    public boolean hasName() {
        return name != null && name.length() > 0;
    }

    public boolean hasAired() {
        return aired != null && aired.length() > 0;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getVideoSource() {
        return video_source;
    }

    public void setVideoSource(String video_source) {
        this.video_source = video_source;
    }

    public void pushTag(String tag) {
        if (tags == null) tags = new ArrayList<String>();
        if(!tags.contains(tag))
            tags.add(tag);
    }

    public List<String> getTags(){
        return tags;
    }

    public void pushPattern(String pattern) {
        if (patterns == null) patterns = new ArrayList<String>();
        patterns.add(pattern);
    }

    public boolean containPattern(String pattern) {
        if (patterns != null)
            return patterns.contains(pattern);
        return false;
    }

    public boolean onlyContainPattern(String pattern) {
        if (patterns != null && patterns.size() == 1)
            return patterns.contains(pattern);
        return false;
    }

    public boolean containPatterns(String... patterns) {
        if (this.patterns == null)
            return false;
        for (String pattern : patterns) {
            if (!this.patterns.contains(pattern))
                return false;
        }
        return true;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return "MovieNameInfo{" +
                "extension='" + extension + '\'' +
                ", name='" + name + '\'' +
                ", name_cn='" + name_cn + '\'' +
                ", type='" + type + '\'' +
                ", tags=" + tags +
                ", season=" + season +
                ", episodes=" + episodes +
                ", diskNumber=" + diskNumber +
                ", year=" + year +
                ", aired='" + aired + '\'' +
                ", country='" + country + '\'' +
                ", videoCodec='" + videoCodec + '\'' +
                ", audioCodec='" + audioCodec + '\'' +
                ", fileSize='" + fileSize + '\'' +
                '}';
    }




}
