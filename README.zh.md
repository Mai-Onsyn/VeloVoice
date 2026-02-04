# VeloVoice

[English Version](README.md)

## 📢简介

这是一个用于将txt格式小说按章节目录形式拆解为文件目录结构，或从网络上爬取小说存储为文件目录结构，并通过TTS引擎将章节文本转为mp3或wav语音文件。

- [https://www.bilibili.com/video/BV15ketemETA](https://www.bilibili.com/video/BV15ketemETA)
- [https://www.bilibili.com/video/BV159tbeqEDA](https://www.bilibili.com/video/BV159tbeqEDA)

🎐此为正式版内容，若你需要查看测试版的内容，请访问[历史标签](https://github.com/Mai-Onsyn/VeloVoice/tree/Test-v0.6.2)

## ✨特点

- **多线程TTS文本转语音**：由于微软搞事情，Edge TTS目前的最大线程数仅开放4，对于40万字的小说一个半小时就能完成。但本地的Natural TTS最多可使用64线程
- **一键加载**：指定小说源，自动构建文本结构树，并且可自定义你的文件要如何解析
- **泛用性高**：可手动操作的文本结构树，不仅是小说朗读，还可轻松用于其他文字转语音的相关需求

### 🧨支持的TTS引擎🧨

- **Microsoft Edge TTS**: 默认引擎，可使用4线程
- **Natural TTS SAPI**: 一个没有线程数量限制的本地TTS引擎，配置[NaturalVoiceSAPIAdapter](https://github.com/gexgd0419/NaturalVoiceSAPIAdapter)后可使用自然语音
- **Multi TTS API**: 一个安卓软件的TTS API，虽然只能以单线程运行，但其灵活性使得它支持几乎所有的可白嫖TTS模型
- **GPT-SoVITS TTS API**: 我要让你读我的小说！！！

### 🎫支持的文本加载源🎫

- **TXT**: 加载本地的txt文件，使用正则表达式自定义文本的解析规则
- **EPUB**: 支持加载文件夹或epub文件，可加载EPUB 2.0.1标准文件，或线性标题的EPUB 3.0标准文件
- **[轻小说文库](https://www.wenku8.net/index.php)**: 好东西

## 🐵使用

程序使用JDK22开发（使用了未命名变量），因此至少需要JDK22运行  
zip压缩包中自带运行环境，以及各种运行库，可直接使用

如果要使用本地SAPI（Natural TTS），需要在运行jar文件的jre的bin目录，或是C:/Windows/System32/中放入jacob-1.21-x64.dll，并重启软件  
如果无法运行Release的压缩包中的可执行文件，说明你的系统不支持Natural TTS。请删除"jre/bin/jacob-1.21-x64.dll"文件后，改用其他TTS引擎

如果要使用GPT-SoVITS，你需要运行API v2服务。我无法确保非v2版本的API能够正常运行

- [软件教程](https://github.com/Mai-Onsyn/VeloVoice/wiki)
- [jacob获取](https://github.com/freemansoft/jacob-project/releases/tag/Root_B-1_21)
- [GPT-SoVITS](https://github.com/RVC-Boss/GPT-SoVITS)

## ⚠注意事项⚠

- **☣禁止发布或传播任何修改了最大可调TTS线程数的源码及打包，或是分享此功能的修改教程！！！这样做只能换取一时的速度，却会害了所有使用EdgeTTS的服务或人，不仅限于本软件的使用者！！！**
- 若是遇到Bug，请尽可能提供反馈，这是我独立开发的软件，缺乏测试，Bug肯定很多。你可以在这里提Issue，或是在B站[私信作者](https://space.bilibili.com/544189344)
- 本工具的设计初衷是优化小说分卷及目录结构管理，不推荐仅用于单文件转换。若强行以非预期方式使用（如处理超大单文件导致内存溢出），开发者不会针对此类场景进行适配或优化。请合理规划文件结构，以获得最佳体验
- ⚠使用 SAPI（Natural TTS）前请务必保存项目！**该TTS引擎在单核性能较弱的 CPU 上运行时容易崩溃！**
- 软件启动时会生成一个随机的User-Agent，如果Edge TTS无法使用，请查一下自己的浏览器UA然后在设置→网络中替换

## 🛴下载

[Releases](https://github.com/Mai-Onsyn/VeloVoice/releases)

蓝奏：[https://wwpz.lanzouv.com/b0ukj384f](https://wwpz.lanzouv.com/b0ukj384f) 密码：14ix

如果你心情很好或者有钞能力也可以选择去这里下载：[https://pan.baidu.com/s/1uAGJ3xO1p4pJuM41pKftfQ?pwd=jvav](https://pan.baidu.com/s/1uAGJ3xO1p4pJuM41pKftfQ?pwd=jvav)
