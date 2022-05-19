package com.viajero.spring.lesson3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class MusicPlayer {
    /*
    Or trough field (maybe private)
    @Autowired
    private Music music;
     */

    /*
    Constructor or setter:
    @Autowired
    public MusicPlayer(Music music) {
        this.music = music;
    }

    @Autowired
    public void setMusicAnyName(Music music) {
        this.music = music;
    }
     */

    private final ClassicalMusic classicalMusic;
    private final RockMusic rockMusic;

    @Autowired
    public MusicPlayer(@Qualifier("classicalMusic") ClassicalMusic classicalMusic,
                       @Qualifier("rockMusic") RockMusic rockMusic) {
        this.classicalMusic = classicalMusic;
        this.rockMusic = rockMusic;
    }

    public void playMusic() {
        System.out.println("Playing: " + classicalMusic.getSong());
        System.out.println("Playing: " + rockMusic.getSong());
    }
}
