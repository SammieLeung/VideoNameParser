package com.firefly.videonameparser.bean;

import android.text.TextUtils;

import com.firefly.videonameparser.utils.StringUtils;

import java.util.ArrayList;

public class Episodes {
    public int season = -1;
    public int episode = -1;
    public int toEpisode = -1;
    private ArrayList<String> mMatchList =new ArrayList<>();

    private static final int SEASON_MAX_RANGE = 100;
    private static final int EPISODE_MAX_RANGE = 100;

    private static final String[] SEASON_MARKERS = {"s"};
    private static final String[] SEASON_EP_MARKERS = {"x"};
    private static final String[] DISC_MARKERS = {"d"};

    private static final String[] EPISODE_MARKERS = {"xe", "ex", "ep", "e"};

    private static final String[] RANGE_SEPARATORS = {"_", "-", "~", "to", "a"};
    private static final String[] DISCRETE_SEPARATORS = {"+", "&", "and", "et"};
    private static final String[] SEASON_WORDS = {
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
            "episode",
            "episodes",
            "eps",
            "ep",
            "episodio",
            "episodios",
            "capitulo",
            "capitulos"
    };


    //	        	? +2x5
//				? +2X5
//				? +02x05
//				? +2X05
//				? +02x5
//				? S02E05
//				? s02e05
//				? s02e5
//				? s2e05
//				? s02ep05
//				? s2EP5
//				? -s03e05
//				? -s02e06
//				? -3x05
//				? -2x06
//				: season: 2
//				  episode: 5
//
//				? "+0102"
//				? "+102"
//				: season: 1
//				  episode: 2
//
//				? "0102 S03E04"
//				? "S03E04 102"
//				: season: 3
//				  episode: 4
    public Episodes(int season, int episode) {
        super();
        this.season = season;
        this.episode = episode;
    }

    public static Episodes parser(String input) {
        if (TextUtils.isEmpty(input)) return null;
        input=input.trim();
        Episodes episodes = new Episodes(-1, -1);

        String regex = build_or_pattern(SEASON_MARKERS, SEASON_WORDS) + "(\\d+)?" +
                build_or_pattern(EPISODE_MARKERS, EPISODE_WORDS, DISC_MARKERS) + "(\\d+)";
        String[] dotStampMatch = StringUtils.matcher(regex, input);
        StringUtils.debug(dotStampMatch);
        if (dotStampMatch != null && dotStampMatch.length == 3&&dotStampMatch[1]!=null&&dotStampMatch[2]!=null) {
            episodes.mMatchList.add(dotStampMatch[0]);
            episodes.season = Integer.parseInt(dotStampMatch[1]);
            episodes.episode = Integer.parseInt(dotStampMatch[2]);
        }

        if(episodes.season==-1){
            regex = "^" + build_or_pattern(SEASON_MARKERS, SEASON_WORDS) + "(\\d+)$";
            dotStampMatch = StringUtils.matcher(regex, input);
            if (dotStampMatch != null) {
                if (dotStampMatch.length == 2&&dotStampMatch[1]!=null) {
                    episodes.season = Integer.parseInt(dotStampMatch[1]);
                }
            }
        }

        if (episodes.episode == -1 && !input.contains("x265") &&!input.contains("x264")) {
            regex = "^" + build_or_pattern(EPISODE_MARKERS, EPISODE_WORDS, DISC_MARKERS) + "(\\d+)";
            dotStampMatch = StringUtils.matcher(regex, input);
            StringUtils.debug(dotStampMatch);
            if (dotStampMatch != null) {
                if (dotStampMatch.length == 2&&dotStampMatch[1]!=null) {
                     episodes.mMatchList.add(dotStampMatch[0]);
                    episodes.episode = Integer.parseInt(dotStampMatch[1]);
                }
                if (dotStampMatch.length == 4) {
                     episodes.mMatchList.add(dotStampMatch[0]);
                    episodes.episode = Integer.parseInt(dotStampMatch[1]);
                    episodes.toEpisode = Integer.parseInt(dotStampMatch[3]);
                }
            }
        }


        regex = "\\D(\\d{1,3})@?" +
                build_or_pattern(SEASON_EP_MARKERS) +
                "@?(\\d{1,3})\\D";
        dotStampMatch = StringUtils.matcher(regex, input);
        StringUtils.debug(dotStampMatch);
        if (dotStampMatch != null && dotStampMatch.length == 3&&dotStampMatch[1]!=null&&dotStampMatch[2]!=null) {
             episodes.mMatchList.add(dotStampMatch[0]);
            episodes.season = Integer.parseInt(dotStampMatch[1]);
            episodes.episode = Integer.parseInt(dotStampMatch[2]);
        }

        regex = build_or_pattern(EPISODE_MARKERS, EPISODE_WORDS, DISC_MARKERS) + "(\\d+)" + build_or_pattern(RANGE_SEPARATORS) + "(\\d+)";
        dotStampMatch = StringUtils.matcher(regex, input);
        StringUtils.debug(dotStampMatch);
        if (dotStampMatch != null && dotStampMatch.length == 3) {
             episodes.mMatchList.add(dotStampMatch[0]);
            episodes.episode = Integer.parseInt(dotStampMatch[1]);
            episodes.toEpisode = Integer.parseInt(dotStampMatch[2]);
        }


        regex = "[全共]([0-9]{1,4}|[一二三四五六七八九十零壹贰叁肆伍陆柒捌玖拾百千佰仟]{1,})[集话話章]";
        dotStampMatch = StringUtils.matcher(regex, input);
        StringUtils.debug(dotStampMatch);
        if (dotStampMatch != null && dotStampMatch.length == 2) {
             episodes.mMatchList.add(dotStampMatch[0]);
            String numStr = dotStampMatch[1];
            if (StringUtils.checkChina(numStr)) {
                episodes.episode = 1;
                episodes.toEpisode = (int) StringUtils.ch2Num(numStr);
            } else {
                episodes.episode = 1;
                episodes.toEpisode = Integer.parseInt(dotStampMatch[1]);
            }
        }

        regex = "第([0-9]{1,4}|[一二三四五六七八九十零壹贰叁肆伍陆柒捌玖拾百千佰仟]{1,})[集话話章]";
        dotStampMatch = StringUtils.matcher(regex, input);
        StringUtils.debug(dotStampMatch);
        if (dotStampMatch != null && dotStampMatch.length == 2) {
             episodes.mMatchList.add(dotStampMatch[0]);
            String numStr = dotStampMatch[1];
            if (StringUtils.checkChina(numStr)) {
                episodes.episode = (int) StringUtils.ch2Num(numStr);
            } else {
                episodes.episode = Integer.parseInt(dotStampMatch[1]);
            }
        }

        regex = "第([0-9]{1,4}|[一二三四五六七八九十零壹贰叁肆伍陆柒捌玖拾百千佰仟]{1,})[部季]";
        dotStampMatch = StringUtils.matcher(regex, input);
        StringUtils.debug(dotStampMatch);
        if (dotStampMatch != null && dotStampMatch.length == 2) {
             episodes.mMatchList.add(dotStampMatch[0]);
            String numStr = dotStampMatch[1];
            if (StringUtils.checkChina(numStr)) {
                episodes.season = (int) StringUtils.ch2Num(numStr);
            } else {
                episodes.season = Integer.parseInt(dotStampMatch[1]);
            }
        }

        if (episodes.episode == 0) {
            regex = "^\\d{2,}$";
            dotStampMatch = StringUtils.matcher(regex, input);
            StringUtils.debug(dotStampMatch);
            if (dotStampMatch != null && dotStampMatch.length == 1) {
                 episodes.mMatchList.add(dotStampMatch[0]);
                episodes.episode = Integer.parseInt(dotStampMatch[0]);
            }
        }

        regex="[上中下]$";
        dotStampMatch = StringUtils.matcher(regex, input);
        StringUtils.debug(dotStampMatch);
        if (dotStampMatch != null && dotStampMatch.length == 1) {
             episodes.mMatchList.add(dotStampMatch[0]);
        }


        if (episodes.episode != -1 && episodes.season == -1)
            episodes.season = 1;
        return episodes;
    }


//		def build_or_pattern(patterns, name=None, escape=False):
//		    or_pattern = []
//		    for pattern in patterns:
//		        if not or_pattern:
//		            or_pattern.append('(?')
//		            if name:
//		                or_pattern.append('P<' + name + '>')
//		            else:
//		                or_pattern.append(':')
//		        else:
//		            or_pattern.append('|')
//		        or_pattern.append('(?:%s)' % re.escape(pattern) if escape else pattern)
//		    or_pattern.append(')')
//		    return ''.join(or_pattern)	

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

    public boolean saneEpisodes(){
        if(episode!=-1||season!=-1||toEpisode!=-1)
            return true;
        return false;
    }

    public boolean isMatch(){
        return mMatchList.size()>0;
    }

    public ArrayList<String> getMatchList() {
        return mMatchList;
    }

    @Override
    public String toString() {
        return "Episodes{" +
                "season=" + season +
                ", episode=" + episode +
                ", toEpisode=" + toEpisode +
                '}';
    }
}
