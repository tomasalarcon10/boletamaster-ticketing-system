package gui;

import javax.sound.sampled.*;
import java.io.File;

public class MusicPlayer {
    private Clip clip;
    private FloatControl volumeControl;
    private boolean isStoppedManually = false;

    public void play(String filepath) {
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(filepath));
            clip = AudioSystem.getClip();
            clip.open(audioStream);

            volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            volumeControl.setValue(-15.0f);

            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP && !isStoppedManually) {
                    clip.setFramePosition(0);
                    clip.start();
                }
            });

            isStoppedManually = false;
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (clip != null && clip.isRunning()) {
            isStoppedManually = true; 
            clip.stop();
        }
    }

    public void resume() {
        if (clip != null) {
            isStoppedManually = false; 
            clip.start();
        }
    }

    public boolean isPlaying() {
        return clip != null && clip.isRunning();
    }
}