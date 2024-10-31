package org.example.sound;

public class Main {

  public static void main(String[] args) {
    AudioFile audioFile = new AudioFile("src/main/resources/inputAudio.wav");
    audioFile.play();

    AudioData data = audioFile.extractAudioData();
    AudioFile outputAudioFile = new AudioFile("src/main/resources/outputAudio.wav");
    outputAudioFile.writeAudioData(data);
    outputAudioFile.play();
  }

}
