package mai_onsyn.VeloVoice.Audio;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.WaveformSimilarityBasedOverlapAdd;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
import be.tarsos.dsp.resample.RateTransposer;
import javazoom.jl.decoder.*;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AudioEncodeUtils {

    public enum AudioFormat {
        MP3_24KHZ_16BIT("mp3"),
        WAV_24KHZ_16BIT("wav"),
        WAV_22KHZ_16BIT("wav");

        private final String format;

        AudioFormat(String format) {
            this.format = format;
        }

        public String getFormat() {
            return format;
        }
    }

    public static byte[] speed_rateShift(byte[] mp3Data, double speedFactor, double rateFactor) {
        byte[] pcmData = decodeMp3ToPcm(mp3Data);
        InputStream rawPcmInputStream = new ByteArrayInputStream(pcmData);
        TarsosDSPAudioFormat format = new TarsosDSPAudioFormat(24000,16,1,true,false);
        AudioInputStream inputStream = new AudioInputStream(rawPcmInputStream, JVMAudioInputStream.toAudioFormat(format), AudioSystem.NOT_SPECIFIED);
        JVMAudioInputStream stream = new JVMAudioInputStream(inputStream);

        WaveformSimilarityBasedOverlapAdd w = new WaveformSimilarityBasedOverlapAdd(WaveformSimilarityBasedOverlapAdd.Parameters.speechDefaults(speedFactor, 24000));
        int inputBufferSize = w.getInputBufferSize();
        int overlap = w.getOverlap();
        AudioDispatcher dispatcher = new AudioDispatcher(stream, inputBufferSize ,overlap);
        w.setDispatcher(dispatcher);

        AudioOutputToByteArray out = new AudioOutputToByteArray();

        dispatcher.addAudioProcessor(w);
        dispatcher.addAudioProcessor(new RateTransposer(rateFactor));
        dispatcher.addAudioProcessor(out);
        dispatcher.run();

        return out.getData();
    }

    public static byte[] decodeMp3ToPcm(byte[] mp3Data) {
        // 准备输入流和输出流
        ByteArrayOutputStream pcmOutputStream = new ByteArrayOutputStream();
        InputStream inputStream = new ByteArrayInputStream(mp3Data);

        try {
            // 创建 MP3 解码器相关对象
            Bitstream bitstream = new Bitstream(inputStream);
            Decoder decoder = new Decoder();

            // 逐帧解码
            while (true) {
                Header frameHeader = bitstream.readFrame();
                if (frameHeader == null) break; // 无更多帧时退出

                // 解码帧并获取 PCM 样本
                SampleBuffer sampleBuffer = (SampleBuffer) decoder.decodeFrame(frameHeader, bitstream);
                byte[] pcmFrame = convertSamplesToBytes(sampleBuffer.getBuffer(), sampleBuffer.getBufferLength());
                pcmOutputStream.write(pcmFrame);

                bitstream.closeFrame(); // 处理下一帧前关闭当前帧
            }

            // 清理资源
            bitstream.close();
            inputStream.close();
        } catch (DecoderException | IOException | BitstreamException e) {
            throw new RuntimeException(e);
        }

        return pcmOutputStream.toByteArray();
    }

    public static byte[] wavRedecode(WavInfo wav, int targetRate, int targetBPS, int targetChannels) throws IOException {

        // 创建原始音频格式
        javax.sound.sampled.AudioFormat originalFormat = new javax.sound.sampled.AudioFormat(wav.sampleRate, wav.bitsPerSample, wav.channels, true, false);

        // 创建目标音频格式
        javax.sound.sampled.AudioFormat targetFormat = new javax.sound.sampled.AudioFormat(targetRate, targetBPS, targetChannels, true, false);

        // 将字节数组转换为音频输入流
        ByteArrayInputStream bais = new ByteArrayInputStream(wav.audioData);
        AudioInputStream originalStream = new AudioInputStream(bais, originalFormat,
                wav.audioData.length / (targetBPS / 8));

        // 使用AudioSystem进行采样率转换
        AudioInputStream convertedStream = AudioSystem.getAudioInputStream(targetFormat, originalStream);

        // 将转换后的流读回字节数组
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int bytesRead;

        while ((bytesRead = convertedStream.read(buffer)) != -1) {
            baos.write(buffer, 0, bytesRead);
        }

        // 关闭流
        originalStream.close();
        convertedStream.close();
        baos.close();

        return baos.toByteArray();
    }

    // 将 int 型样本数组转为小端序 byte[]
    private static byte[] convertSamplesToBytes(short[] samples, int len) {
        byte[] byteBuffer = new byte[len * 2]; // 每个样本占 2 字节 (16-bit)
        int byteIndex = 0;

        for (int i = 0; i < len; i++) {
            short sample = samples[i];
            // 小端序：低位在前
            byteBuffer[byteIndex++] = (byte) (sample & 0xFF);        // 低字节
            byteBuffer[byteIndex++] = (byte) ((sample >> 8) & 0xFF); // 高字节
        }
        return byteBuffer;
    }

    public static byte[] genWavHeader(int pcmLength, int sampleRate, int channels) {
        ByteArrayOutputStream wavOut = new ByteArrayOutputStream();
        try {
            // RIFF 头
            wavOut.write("RIFF".getBytes());
            writeInt(wavOut, 36 + pcmLength); // 总长度 = 头部长度 + PCM 数据长度
            wavOut.write("WAVE".getBytes());

            // fmt 子块
            wavOut.write("fmt ".getBytes());
            writeInt(wavOut, 16);               // fmt 块长度
            writeShort(wavOut, (short) 1);       // 格式 (PCM=1)
            writeShort(wavOut, (short) channels); // 声道数
            writeInt(wavOut, sampleRate);         // 采样率
            writeInt(wavOut, sampleRate * channels * 2); // 字节率
            writeShort(wavOut, (short) (channels * 2));  // 块对齐
            writeShort(wavOut, (short) 16);        // 位深 (16-bit)

            // data 子块
            wavOut.write("data".getBytes());
            writeInt(wavOut, pcmLength);     // PCM 数据长度
            //wavOut.write(pcmData);                // 写入 PCM 数据

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return wavOut.toByteArray();
    }

    // 辅助方法：写入小端序整数
    private static void writeInt(ByteArrayOutputStream out, int value) {
        out.write(value);
        out.write(value >> 8);
        out.write(value >> 16);
        out.write(value >> 24);
    }

    private static void writeShort(ByteArrayOutputStream out, short value) {
        out.write(value);
        out.write(value >> 8);
    }


    private static class AudioOutputToByteArray implements AudioProcessor {
        private boolean isDone = false;
        private byte[] out = null;
        private ByteArrayOutputStream bos;

        public AudioOutputToByteArray() {
            bos = new ByteArrayOutputStream();
        }

        public byte[] getData() {
            while (!isDone && out == null) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ignored) {}
            }

            return out;
        }

        @Override
        public boolean process(AudioEvent audioEvent) {
            bos.write(audioEvent.getByteBuffer(),0,audioEvent.getByteBuffer().length);
            return true;
        }

        @Override
        public void processingFinished() {
            out = bos.toByteArray().clone();
            bos = null;
            isDone = true;
        }
    }

    public static class WavInfo {
        public int sampleRate;
        public int bitsPerSample;
        public int channels;
        public byte[] audioData;

        public WavInfo(int sampleRate, int bitsPerSample, int channels, byte[] audioData) {
            this.sampleRate = sampleRate;
            this.bitsPerSample = bitsPerSample;
            this.channels = channels;
            this.audioData = audioData;
        }

        public WavInfo() {}

        @Override
        public String toString() {
            return "WavInfo{" +"sampleRate=" + sampleRate + ", bitsPerSample=" + bitsPerSample + ", channels=" + channels + ", audioData=" + audioData.length + '}';
        }
    }

    public static WavInfo parseWav(byte[] wavData) {
        WavInfo info = new WavInfo();

        // 基本验证
        if (wavData.length < 44 ||
                !"RIFF".equals(new String(wavData, 0, 4)) ||
                !"WAVE".equals(new String(wavData, 8, 4))) {
            throw new IllegalArgumentException("无效的WAV文件");
        }

        int fmtChunkOffset = 12;
        int dataChunkOffset = -1;

        // 查找fmt和data块
        int pos = 12;
        while (pos < wavData.length - 8) {
            String chunkId = new String(wavData, pos, 4);
            int chunkSize = byteArrayToInt(wavData, pos + 4);

            if ("fmt ".equals(chunkId)) {
                // 解析音频格式信息
                info.sampleRate = byteArrayToInt(wavData, pos + 12);
                info.bitsPerSample = byteArrayToShort(wavData, pos + 22);
                info.channels = byteArrayToShort(wavData, pos + 10);
            } else if ("data".equals(chunkId)) {
                dataChunkOffset = pos + 8;
                break;
            }

            pos += 8 + chunkSize;
        }

        if (dataChunkOffset == -1) {
            throw new IllegalArgumentException("未找到数据块");
        }

        // 提取音频数据
        info.audioData = new byte[wavData.length - dataChunkOffset];
        System.arraycopy(wavData, dataChunkOffset, info.audioData, 0, info.audioData.length);

        return info;
    }

    // 辅助方法：将4字节转换为int
    private static int byteArrayToInt(byte[] bytes, int offset) {
        return ((bytes[offset] & 0xFF) |
                ((bytes[offset + 1] & 0xFF) << 8) |
                ((bytes[offset + 2] & 0xFF) << 16) |
                ((bytes[offset + 3] & 0xFF) << 24));
    }

    // 辅助方法：将2字节转换为short
    private static short byteArrayToShort(byte[] bytes, int offset) {
        return (short)((bytes[offset] & 0xFF) |
                ((bytes[offset + 1] & 0xFF) << 8));
    }

}
