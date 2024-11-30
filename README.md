# This software is developed for Chinese users!

## 简介

这是一个用于将txt格式小说按章节目录形式拆解为文件目录结构，或从网络上爬取小说存储为文件目录结构，并通过Edge TTS（Edge浏览器的大声朗读功能）将章节文本转为mp3语音文件。
- <https://www.bilibili.com/video/BV15ketemETA>
- <https://www.bilibili.com/video/BV159tbeqEDA>

## 特点

- **多线程TTS文本转语音**：64线程下40万字的文本转换仅需5分钟。（由于微软搞事情 我在最新版把最大线程限制到了8 但速度还是够用的）
- **一键加载**：指定小说源，自动构建文本结构树。
- **泛用性高**：可手动操作的文本结构树，不仅是小说朗读，还可轻松用于其他文字转语音的相关需求。

### api服务器部属
直接看专栏
- <https://www.bilibili.com/read/cv39961289/>

## 使用

程序使用JDK22开发（使用了未命名变量），因此至少需要JDK22运行。

1. 运行jar文件（首次启动会生成一个`settings.json`存储软件配置）。
2. 在左侧选择一种加载模式，然后在下方的输入框输入小说源（本地模式需要的是txt文件所在目录路径，会读取目录下所有`.txt`后缀的文件），点击加载，即可在"ROOT"下看到小说的结构。
    - 注意：加载模式要选对，否则可能会无反应或者加载出空目录！
    - 当前支持的小说来源：
      - **本地（文件）**：直接加载txt文件，不进行解析。
      - **本地（全集）**：需要整个系列的小说txt文件，格式需要与轻小说文库的全本下载一致。
      - **本地（分卷）**：需要一本书的体量的txt文件，格式需要与轻小说文库的分卷下载一致。
      - **轻小说文库**（<https://www.wenku8.net/>）：需要小说主页或目录页的地址。
      - **轻之文库** (<https://www.linovel.net/>)：需要小说主页或目录页的地址。
4. 点击文本图标的项目，即可在中间的文本区域查看或修改其内容。
5. 可在右侧的操作面板修改TTS相关配置。

若一切准备就绪，点击"开始"即可开始TTS转换，下方会有进度提示。

## 注意事项

- 线程不要设置过多，更多的线程不仅不会更快，反而会增加重连频率，降低转换速度。64线程基本就达到饱和了。
- 若是遇到Bug，请尽可能提供反馈，这是我独立开发的软件，缺乏测试，Bug肯定满天飞。
- 使用**轻之文库**的在线加载时，重试次数建议拉满，这个网站访问非常慢。
- 请结合网络小说的名称仔细考虑是否开启**添加额外章节/卷名**。

## 设置详解

### 图形

- **主题** - 切换亮色与暗色模式，亮色模式下调整背景亮度是越大越亮，暗色模式则是越大越暗 —— 重启生效
- **主题色** - 影响整个软件的颜色 —— 重启生效
- **Windows UI** - 启用透明与模糊的窗口，(不稳定，但还算好看) —— 重启生效
- **Blur Mode** - 背景的模糊样式 —— 立即生效(有bug，具体看release) —— 立即生效
- **背景图片** - 设置背景图片，可输入本地图片或网络图片，网络图片地址尽量复制粘贴，不然每次输入文字都会很卡 —— 立即生效
- **背景模糊度** 与 **背景亮/暗度** - 背景图片的设置 —— 立即生效

### 网络

- **连接线程数** - 转换时建立多少个TTS线程 —— 立即生效
- **重试次数** - 在TTS连接出现问题时重试多少次，若是超出这个次数将会放弃某个句子的转换（或直接报错 - 一般不会）—— 立即生效
- **超时时间** - 一个TTS请求的最大等待时间，超时会触发重连 —— 立即生效

### 文本

- **切片最大长度** - 文本切片的大小，也就是一次向TTS发送的文本最大字符数量，越大越快，但也越容易出问题 —— 立即生效
- **切片首选符号** - 文本切片的判断符号，优先会使用换行符切割文本，若是切片大小还是大于**切片最大长度**，就会按这些符号优先从左到右查找尝试切割文本，若都没有，将会强制切割（强制断句）。注意：不要设置逗号“，” 因为这样会导致生成的语音衔接极其不自然！ —— 立即生效
- **章节名添加卷名** - 仅针对从网络加载的小说生效，在每个章节的名字前添加卷名，如果你使用MP3，这将会很有用。本地小说不支持是因为本地txt的章节名自带卷名 —— 立即生效
- **文件名添加序号** - 若是一个目录下的文件数量大于1，就添加序号，将其保持与文本结构树的顺序一致。因为小说的名字在大多数排序方式下都是混乱的，所以不管是用什么设备听，这个选项都很有必要 —— 立即生效
- **添加额外章节名** - 为从网络加载的小说的章节名前额外添加形如**第1章**，**第2章**这样的名称 —— 立即生效
- **添加额外章节名** - 为从网络加载的小说的卷名前额外添加形如**第1卷**，**第2卷**这样的名称 —— 立即生效

### 音频
- **试听文本** - 试听时使用的文本 —— 立即生效
- **分段保存音频** - 按**分段时长**设置的时间拆分一个音频文件 —— 立即生效
- **分段添加名称** - 为每个分段的音频（不包括第一个分段）添加第一个分段音频中的第一句话（单个File的第一个文本切片，一般为软件中文本文件的第一行，转换小说时可以理解为该章节的标题） —— 立即生效
- **分段时长** - 每个音频分段的最大时长（单位：分钟）

## 下载

都来这了，Releases页面不正是个很好的地方吗？\
不过蓝奏盘也是推荐的：<https://wwpz.lanzouv.com/b0ukj384f> 密码：14ix\
如果你心情很好或者有钞能力也可以选择去这里下载：<https://pan.baidu.com/s/1uAGJ3xO1p4pJuM41pKftfQ?pwd=jvav>
- 注：Test-v0.2及之后的版本需要运行此软件的java的bin目录下有项目lib文件夹里的javafxblur.dll，否则Windows 10 April 2018 Update (1803, build 17134)或更高的版本可能会出错，要是嫌麻烦还请直接下带jre运行库的zip压缩包。
