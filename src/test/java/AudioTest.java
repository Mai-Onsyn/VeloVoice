import be.tarsos.dsp.*;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
import be.tarsos.dsp.resample.RateTransposer;
import javazoom.jl.decoder.*;

import javax.sound.sampled.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class AudioTest {
    public static byte[] speed_rateShift(final InputStream rawPcmInputStream, double speedFactor, double rateFactor) {
        TarsosDSPAudioFormat format = new TarsosDSPAudioFormat(24000,16,1,true,false);
        AudioInputStream inputStream = new AudioInputStream(rawPcmInputStream, JVMAudioInputStream.toAudioFormat(format),AudioSystem.NOT_SPECIFIED);
        JVMAudioInputStream stream = new JVMAudioInputStream(inputStream);

        WaveformSimilarityBasedOverlapAdd w = new WaveformSimilarityBasedOverlapAdd(WaveformSimilarityBasedOverlapAdd.Parameters.speechDefaults(speedFactor, 16000));
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

    private static byte[] convertMp3ToPcm(byte[] mp3Data) throws IOException, BitstreamException, DecoderException {
        // 准备输入流和输出流
        ByteArrayOutputStream pcmOutputStream = new ByteArrayOutputStream();
        InputStream inputStream = new ByteArrayInputStream(mp3Data);

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

        return pcmOutputStream.toByteArray();
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

    public static byte[] addWavHeader(byte[] pcmData, int sampleRate, int channels) {
        ByteArrayOutputStream wavOut = new ByteArrayOutputStream();
        try {
            // RIFF 头
            wavOut.write("RIFF".getBytes());
            writeInt(wavOut, 36 + pcmData.length); // 总长度 = 头部长度 + PCM 数据长度
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
            writeInt(wavOut, pcmData.length);     // PCM 数据长度
            wavOut.write(pcmData);                // 写入 PCM 数据

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

    public static void main(String[] args) throws IOException, DecoderException, BitstreamException {

        byte[] sourceData = Files.readAllBytes(Paths.get("D:\\Users\\Desktop\\Files\\Test\\folder2\\file4.mp3"));
        byte[] pcmData = convertMp3ToPcm(sourceData);
        byte[] result = speed_rateShift(new ByteArrayInputStream(pcmData), 4.0, 1.0);

        Files.write(Paths.get("D:\\Users\\Desktop\\Files\\Test\\folder2\\file5.wav"), addWavHeader(result, 24000, 1));

    }
}

class AudioOutputToByteArray implements AudioProcessor {
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