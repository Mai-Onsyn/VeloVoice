# VeloVoice

[中文版](README.zh.md)

## 📢Introduction

A tool for splitting novel text files into chapter-based directory structures, or scraping novels from the web, and converting chapter texts into mp3/wav audio files using TTS Engine.

- [https://www.bilibili.com/video/BV15ketemETA](https://www.bilibili.com/video/BV15ketemETA)
- [https://www.bilibili.com/video/BV159tbeqEDA](https://www.bilibili.com/video/BV159tbeqEDA)

This is the stable release. If you need to check the test version, please visit [Historical Tags](https://github.com/Mai-Onsyn/VeloVoice/tree/Test-v0.6.2)

## ✨Features

- **Multithreaded TTS Conversion**: Due to Microsoft's restrictions, the maximum thread count of Edge TTS is currently limited to 4, but this still allows converting a 400,000-word novel in about 1.5 hours. However, the local Natural TTS can use up to 64 threads
- **One-click Loading**: Specify novel sources to automatically build text structure trees with customizable parsing rules
- **High Versatility**: Manual text structure tree operations make it suitable not just for novel narration but also other text-to-speech needs

### 🧨Supports TTS Engines🧨

- **Microsoft Edge TTS**: The default online engine, supporting a maximum of 4 concurrent threads
- **Natural TTS SAPI**: A local TTS engine that supports up to 64 concurrent threads. You can use natural speech after configuring [NaturalVoiceSAPIAdapter](https://github.com/gexgd0419/NaturalVoiceSAPIAdapter)
- **Multi TTS API**: An Android TTS software API. Although it operates only in a single-threaded manner, it supports countless models
- **GPT-SoVITS TTS API**: Clone your voice to read my novel!!!

### 🎫Supports Text Sources🎫

- **TXT**: Load local text files with powerful and customizable parsing rules
- **EPUB**: Load local EPUB files that comply with the EPUB 2.0.1 standard or the EPUB 3.0 standard with a linear navigation document
- **[Wenku8](https://www.wenku8.net/index.php)**: A Chinese online novel library website

## 🐵Usage

The program requires JDK22 to run (uses unnamed variables)
The zip package includes runtime environment and necessary libraries for immediate use

To use local SAPI (Natural TTS), place jacob-1.21-x64.dll in either your JRE's bin directory or C:/Windows/System32/, then restart the software
If the executable file in the released zip archive fails to run, it indicates that your system does not support Natural TTS. In this case, please delete the file "jre/bin/jacob-1.21-x64.dll" and try again using alternative TTS engines

To use GPT-SoVITS, you must run the API v2 service. I cannot guarantee that the non-v2 API will function properly.

- [Tutorial](https://github.com/Mai-Onsyn/VeloVoice/wiki)
- [jacob download](https://github.com/freemansoft/jacob-project/releases/tag/Root_B-1_21)
- [GPT-SoVITS](https://github.com/RVC-Boss/GPT-SoVITS)

## ⚠Notes⚠

- **☣DO NOT distribute or share modified versions that increase the TTS thread limit, or tutorials about such modifications!!! This may provide temporary speed gains but will harm all EdgeTTS services and users beyond just this software!!!**
- Please report any bugs you encounter. As an independently developed project, thorough testing hasn't been possible. You can create Issues here or [contact the author on Bilibili](https://space.bilibili.com/544189344)
- This tool was designed for optimizing novel volume and directory structure management, not recommended for single-file conversion. Memory overflow may occur when processing overly large single files against intended use cases
- ⚠ Always save your project before using SAPI (Natural TTS)! **This TTS engine tends to crash when running on CPUs with weak single-core performance!**
- A random User-Agent is generated when the software starts. If Edge TTS is unavailable, please check your Edge’s UA and replace it in Settings → Network.

## 🛴Download

[Releases](https://github.com/Mai-Onsyn/VeloVoice/releases)

LanZou: [https://wwpz.lanzouv.com/b0ukj384f](https://wwpz.lanzouv.com/b0ukj384f) Password: 14ix

If you're feeling generous: [https://pan.baidu.com/s/1uAGJ3xO1p4pJuM41pKftfQ?pwd=jvav](https://pan.baidu.com/s/1uAGJ3xO1p4pJuM41pKftfQ?pwd=jvav)
