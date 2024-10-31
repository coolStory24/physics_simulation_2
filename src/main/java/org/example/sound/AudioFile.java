package org.example.sound;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/*
Only supports mono WAV files with 8 or 16 bit PCM encoding
 */
public class AudioFile {
  private final File file;

  public AudioFile(String path) {
    this.file = new File(path);
  }

  public void play() {
    try {
      AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);

      Clip clip = AudioSystem.getClip();
      clip.open(audioStream);

      clip.start();
      Thread.sleep(clip.getMicrosecondLength() / 1000);

      audioStream.close();
      clip.close();
    } catch (Exception e) {
      throw new AudioException("Failed to play the file", e);
    }
  }

  public AudioData extractAudioData() {
    try {
      AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
      int bytesPerSample = audioStream.getFormat().getFrameSize();
      boolean isBigEndian = audioStream.getFormat().isBigEndian();

      byte[] audioBytes = audioStream.readAllBytes();
      int numSamples = audioBytes.length / bytesPerSample;

      double[] samples = new double[numSamples];
      for (int i = 0; i < numSamples; i++) {
        int sampleIndex = i * bytesPerSample;
        double sampleValue = 0;

        sampleValue += bytesToSample(audioBytes, sampleIndex, bytesPerSample, isBigEndian);

        samples[sampleIndex] = sampleValue;
      }

      return new AudioData(samples, audioStream.getFormat().getSampleRate(), bytesPerSample);
    } catch (Exception e) {
      throw new AudioException("Failed to extract the audio file", e);
    }
  }

  private double bytesToSample(byte[] audioBytes, int start, int bytesPerSample, boolean isBigEndian) {
    if (bytesPerSample == 1) {
      return (audioBytes[start] & 0xFF) / 127.5 - 1.0;
    } else if (bytesPerSample == 2) {
      ByteBuffer bb = ByteBuffer.wrap(audioBytes, start, bytesPerSample);
      bb.order(isBigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
      return bb.getShort() / 32768.0;
    } else {
      throw new UnsupportedOperationException("Unsupported bytes per sample: " + bytesPerSample);
    }
  }

  public void writeAudioData(AudioData audioData) {
    try (FileOutputStream fos = new FileOutputStream(file.getPath())) {
      int sampleRate = (int) audioData.sampleRate();
      int byteDepth = audioData.byteDepth();
      double[] samples = audioData.data();

      int byteRate = sampleRate * byteDepth;
      int dataSize = samples.length * byteDepth;
      int fileSize = 36 + dataSize;

      fos.write("RIFF".getBytes());
      fos.write(intToLittleEndianBytes(fileSize, 4));
      fos.write("WAVE".getBytes());
      fos.write("fmt ".getBytes());
      fos.write(intToLittleEndianBytes(16, 4));
      fos.write(shortToLittleEndianBytes((short) 1, 2));
      fos.write(shortToLittleEndianBytes((short) 1, 2));
      fos.write(intToLittleEndianBytes(sampleRate, 4));
      fos.write(intToLittleEndianBytes(byteRate, 4));
      fos.write(shortToLittleEndianBytes((short) byteDepth, 2));
      fos.write(shortToLittleEndianBytes((short) (byteDepth * 8), 2));
      fos.write("data".getBytes());
      fos.write(intToLittleEndianBytes(dataSize, 4));

      for (double sample : samples) {
        fos.write(sampleToBytes(sample, byteDepth));
      }

    } catch (IOException e) {
      throw new AudioException("Failed to write the audio file", e);
    }
  }

  private byte[] sampleToBytes(double sample, int bytesPerSample) {
    if (bytesPerSample == 1) {
      int sampleByte = (int) ((sample + 1.0) * 127.5);
      return new byte[]{(byte) sampleByte};
    } else if (bytesPerSample == 2) {
      int sampleShort = (int) (sample * 32767.0);
      return ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort((short) sampleShort).array();
    } else {
      throw new UnsupportedOperationException("Unsupported bit depth: " + bytesPerSample);
    }
  }



  private byte[] intToLittleEndianBytes(int value, int byteCount) {
    ByteBuffer buffer = ByteBuffer.allocate(byteCount).order(ByteOrder.LITTLE_ENDIAN);
    return buffer.putInt(value).array();
  }

  private byte[] shortToLittleEndianBytes(short value, int byteCount) {
    ByteBuffer buffer = ByteBuffer.allocate(byteCount).order(ByteOrder.LITTLE_ENDIAN);
    return buffer.putShort(value).array();
  }
}
