Java MP3 decoder / player

This project is based on JavaZOOM (http://www.javazoom.net/javalayer/javalayer.html).
It is licensed under LGPL (see LICENSE.txt).

FAQ : 
---

- How to run MP3 player?
  java -jar jmp3.jar [directory or file]

- Requirements?
  For the player: JVM 1.3. For the converter: JVM 1.1. JMF is not required.

- How to run the MP3 to WAV converter?
  java -cp jmp3.jar -convert [directory or file]

- Does it support MPEG 2.5 ?
  Yes, it works for all files generated with LAME.

- Does it support VBR ?
  Yes, It supports VBRI and XING VBR header too. 

- How to get ID3v1 or ID3v2 tags?
  The API provides a getRawID3v2() method to get an InputStream on ID3v2 frames.

- How much memory and cpu does it needs?
  Around 2 MB heap memory, for playing mp3 files around 1% cpu usage.

- How to skip frames to have a seek feature?
  See the source to learn how to skip frames.

