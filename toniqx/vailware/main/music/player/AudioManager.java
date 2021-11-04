package toniqx.vailware.main.music.player;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
  
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
  
public class AudioManager 
{
  
    // to store current position
    static Long currentFrame;
    static Clip clip;
      
    static // current status of clip
    String status;
      
    static AudioInputStream audioInputStream;
    static String filePath;
  
    // constructor to initialize streams and clip
    public AudioManager()
        throws UnsupportedAudioFileException,
        IOException, LineUnavailableException 
    {
        // create AudioInputStream object
        audioInputStream = 
                AudioSystem.getAudioInputStream(new File(filePath).getAbsoluteFile());
          
        // create clip reference
        clip = AudioSystem.getClip();
          
        // open audioInputStream to the clip
        clip.open(audioInputStream);
          
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }
  
    public static void main(String[] args) 
    {
        try
        {
            filePath = "Your path for the file";
            AudioManager audioPlayer = 
                            new AudioManager();
              
            audioPlayer.play();
            Scanner sc = new Scanner(System.in);
              
            while (true)
            {
                System.out.println("1. pause");
                System.out.println("2. resume");
                System.out.println("3. restart");
                System.out.println("4. stop");
                System.out.println("5. Jump to specific time");
                int c = sc.nextInt();
                audioPlayer.gotoChoice(c);
                if (c == 4)
                break;
            }
            sc.close();
        } 
          
        catch (Exception ex) 
        {
            System.out.println("Error with playing sound.");
            ex.printStackTrace();
          
          }
    }
      
    // Work as the user enters his choice
      
    private static void gotoChoice(int c)
            throws IOException, LineUnavailableException, UnsupportedAudioFileException 
    {
        switch (c) 
        {
            case 1:
                pause();
                break;
            case 2:
                resumeAudio();
                break;
            case 3:
                restart();
                break;
            case 4:
                stop();
                break;
            case 5:
                System.out.println("Enter time (" + 0 + 
                ", " + clip.getMicrosecondLength() + ")");
                Scanner sc = new Scanner(System.in);
                long c1 = sc.nextLong();
                jump(c1);
                break;
      
        }
      
    }
      
    // Method to play the audio
    public static void play() 
    {
        //start the clip
        clip.start();
          
        status = "play";
    }
      
    // Method to pause the audio
    public static void pause() 
    {
        if (status.equals("paused")) 
        {
            System.out.println("audio is already paused");
            return;
        }
        currentFrame = 
        clip.getMicrosecondPosition();
        clip.stop();
        status = "paused";
    }
      
    // Method to resume the audio
    public static void resumeAudio() throws UnsupportedAudioFileException,
                                IOException, LineUnavailableException 
    {
        if (status.equals("play")) 
        {
            System.out.println("Audio is already "+
            "being played");
            return;
        }
        clip.close();
        resetAudioStream();
        clip.setMicrosecondPosition(currentFrame);
        play();
    }
      
    // Method to restart the audio
    public static void restart() throws IOException, LineUnavailableException,
                                            UnsupportedAudioFileException 
    {
        clip.stop();
        clip.close();
        resetAudioStream();
        currentFrame = 0L;
        clip.setMicrosecondPosition(0);
        play();
    }
      
    // Method to stop the audio
    public static void stop() throws UnsupportedAudioFileException,
    IOException, LineUnavailableException 
    {
        currentFrame = 0L;
        clip.stop();
        clip.close();
    }
      
    // Method to jump over a specific part
    public static void jump(long c) throws UnsupportedAudioFileException, IOException,
                                                        LineUnavailableException 
    {
        if (c > 0 && c < clip.getMicrosecondLength()) 
        {
            clip.stop();
            clip.close();
            resetAudioStream();
            currentFrame = c;
            clip.setMicrosecondPosition(c);
            play();
        }
    }
      
    // Method to reset audio stream
    public static void resetAudioStream() throws UnsupportedAudioFileException, IOException,
                                            LineUnavailableException 
    {
        audioInputStream = AudioSystem.getAudioInputStream(
        new File(filePath).getAbsoluteFile());
        clip.open(audioInputStream);
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }
  
}