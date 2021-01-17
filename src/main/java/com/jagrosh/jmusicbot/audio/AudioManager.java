package com.jagrosh.jmusicbot.audio;

import com.jagrosh.jmusicbot.queue.FairQueue;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.JDA;

public interface AudioManager {

    /**
     * add the track to the end of the queue. play it immediatly if no
     * tracks are in the queue
     *
     * @param track track to enqueue
     * @return position the track got inserted in the queue
     */
    int addTrack(QueuedTrack track);

    /**
     * add the track to the front of the queue. play it immediatly if no
     * tracks are in the queue
     *
     * @param track track to enqueue
     * @return 0 if there is a track playing, -1 if immediatly played
     */
    int addTrackToFront(QueuedTrack track);

    /**
     * play the first track from the default playlist
     *
     * @return if a track has been found to play
     */
    boolean playFromDefault();

    /**
     * stops the current track end empties the queue
     */
    default void stopAndClear() {
        stop();
        clear();
    }

    /**
     * stop the current track
     */
    void stop();

    /**
     * clear the queue
     */
    void clear();

    /**
     * Returns if music is playing for the jda.
     *
     * @param jda
     * @return
     */
    boolean isMusicPlaying(JDA jda);

    /**
     * @return user ID who requested the currently playing song. is 0 if no song is playing or
     * information is not available
     */
    long getRequester();

    /**
     * @return current queue of tracks to play
     */
    FairQueue<QueuedTrack> getQueue();

    AudioPlayer getPlayer();

}
