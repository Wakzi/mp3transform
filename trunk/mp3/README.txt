Java MP3 Decoder / Player

This project is based on JavaZOOM (http://www.javazoom.net/javalayer/javalayer.html).
It is licensed under LGPL (see LICENSE.txt).

FAQ : 
---

- How to run MP3 player?
  java -jar mp3.jar

- Requirements?
  For the player: JVM 1.3. For the converter: JVM 1.1. JMF is not required.

- How to run the MP3 to WAV converter?
  java -cp mp3.jar mp3.wav.WavConverter -in input.mp3 -out output.wav

- Does it support MPEG 2.5?
  Yes, it works for all files generated with LAME.

- Does it support VBR?
  Yes, It supports VBRI and XING VBR header too. 

- How to get ID3v1 or ID3v2 tags?
  This is currently not implemented.

- How much memory and CPU does it needs?
  Around 15 MB memory, and 1% CPU usage.

- How to skip frames to have a seek feature?
  This is currently not implemented.

