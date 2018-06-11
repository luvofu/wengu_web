package com.wg.common.Enum.user;

/**
 * Created by Administrator on 2017/4/26 0026.
 */
public enum ReadingLvl {
    One(1, "扶我起来，我还能再读几本", "我读书少，别骗我！"),
    Tow(2, "书丛朵朵花开，我独采一枝梅", "我偏科了，班主任要谈话！"),
    Three(3, "书丛朵朵花开，看我雨露均沾", "我是多面手，潜力无限哦！"),
    Four(4, "灯火阑珊处，犹嫌书不够", "我要读书，多多益善！"),
    Five(5, "读书破万卷，心中有诗书", "我有书，有故事，你有酒吗！");
    private int leavel;
    private String quality;//质量
    private String appraise;//评价

    ReadingLvl(int leavel, String quality, String appraise) {
        this.leavel = leavel;
        this.quality = quality;
        this.appraise = appraise;
    }

    public static ReadingLvl getLvl(long bookNum, long categoryNum) {
        if (bookNum <= 10) {
            return ReadingLvl.One;
        } else if (bookNum > 10 && bookNum <= 50) {
            if (categoryNum <= 3) {
                return ReadingLvl.Tow;
            } else {
                return ReadingLvl.Three;
            }
        } else if (bookNum > 50 && bookNum <= 100) {
            return ReadingLvl.Four;
        } else {
            return ReadingLvl.Five;
        }
    }

    public int getLeavel() {
        return leavel;
    }

    public String getQuality() {
        return quality;
    }

    public String getAppraise() {
        return appraise;
    }
}
