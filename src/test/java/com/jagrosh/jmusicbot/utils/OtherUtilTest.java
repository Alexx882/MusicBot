package com.jagrosh.jmusicbot.utils;

import net.dv8tion.jda.api.entities.Activity;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class OtherUtilTest {

    public OtherUtil otherUtil;

    @Before
    public void before(){
        otherUtil = new OtherUtil();
    }

    @Test
    public void test_parseGame_playingText(){
        Activity res = OtherUtil.parseGame("playing CoD");

        Assert.assertEquals("CoD", res.getName());
        Assert.assertEquals(Activity.ActivityType.DEFAULT, res.getType());
    }

    @Test
    public void test_parseGame_listeningToText(){
        Activity res = OtherUtil.parseGame("listening to angry NoiSes");

        Assert.assertEquals("angry NoiSes", res.getName());
        Assert.assertEquals(Activity.ActivityType.LISTENING, res.getType());
    }

    @Test
    public void test_parseGame_listeningText(){
        Activity res = OtherUtil.parseGame("listening rap");

        Assert.assertEquals("rap", res.getName());
        Assert.assertEquals(Activity.ActivityType.LISTENING, res.getType());
    }

    @Test
    public void test_parseGame_watchingText(){
        Activity res = OtherUtil.parseGame("watching videos");

        Assert.assertEquals("videos", res.getName());
        Assert.assertEquals(Activity.ActivityType.WATCHING, res.getType());
    }

    @Test
    public void test_parseGame_streamingText_resultsInPlaying(){
        Activity res = OtherUtil.parseGame("streaming livecontent");

        Assert.assertEquals(Activity.ActivityType.DEFAULT, res.getType());
        Assert.assertEquals("streaming livecontent", res.getName());
    }

    @Test
    public void test_parseGame_streamingTextWithUrl_resultsInStreaming(){
        Activity res = OtherUtil.parseGame("streaming mrherry pokemon");

        Assert.assertEquals(Activity.ActivityType.STREAMING, res.getType());
        Assert.assertEquals("pokemon", res.getName());
        Assert.assertEquals("https://twitch.tv/mrherry", res.getUrl());
    }
}
