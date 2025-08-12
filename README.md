# VeloVoice
[中文版](README.zh.md)

## Introduction

A tool for splitting novel text files into chapter-based directory structures, or scraping novels from the web, and converting chapter texts into mp3/wav audio files using TTS Engine.
- <https://www.bilibili.com/video/BV15ketemETA>
- <https://www.bilibili.com/video/BV159tbeqEDA>

This is the stable release. If you need to check the test version, please visit [Historical Tags](<https://github.com/Mai-Onsyn/VeloVoice/tree/Test-v0.6.2>)

## Features

- **Multithreaded TTS Conversion**: Due to Microsoft's restrictions, the maximum thread count of Edge TTS is currently limited to 4, but this still allows converting a 400,000-word novel in about 1.5 hours. However, the local Natural TTS can use up to 64 threads
- **One-click Loading**: Specify novel sources to automatically build text structure trees with customizable parsing rules
- **High Versatility**: Manual text structure tree operations make it suitable not just for novel narration but also other text-to-speech needs

## Usage

The program requires JDK22 to run (uses unnamed variables)  
The zip package includes runtime environment and necessary libraries for immediate use

To use local SAPI (Natural TTS), place jacob-1.21-x64.dll in either your JRE's bin directory or C:/Windows/System32/, then restart the software
- [Tutorial](https://github.com/Mai-Onsyn/VeloVoice/wiki)
- [jacob download](https://github.com/freemansoft/jacob-project/releases/tag/Root_B-1_21)

## Notes

- **DO NOT distribute or share modified versions that increase the TTS thread limit, or tutorials about such modifications!!! This may provide temporary speed gains but will harm all EdgeTTS services and users beyond just this software!!!**
- Please report any bugs you encounter. As an independently developed project, thorough testing hasn't been possible. You can create Issues here or [contact the author on Bilibili](https://space.bilibili.com/544189344)
- This tool was designed for optimizing novel volume and directory structure management, not recommended for single-file conversion. Memory overflow may occur when processing overly large single files against intended use cases
- ⚠ Always save your project before using SAPI (Natural TTS)! **This TTS engine tends to crash when running on CPUs with weak single-core performance!**

## Download

[Releases](https://github.com/Mai-Onsyn/VeloVoice/releases)  
LanZou: <https://wwpz.lanzouv.com/b0ukj384f> Password: 14ix  
If you're feeling generous: <https://pan.baidu.com/s/1uAGJ3xO1p4pJuM41pKftfQ?pwd=jvav>