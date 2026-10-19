package mai_onsyn.VeloVoice.App;

import java.util.Locale;

/**
 * DeepSeek 网页版生成<br>
 * 参考<a href="https://github.com/rany2/edge-tts/blob/b2ffbdd9f43f28f03cc939b5de0f3964aa62d60e/src/edge_tts/drm.py">edge-tts</a>的提交实现翻译为Java<br>
 * 放心这段注释是人写的 :)
 */
public class SecMsGecV2 {

    private static final String TRUSTED_CLIENT_TOKEN = "6A5AA1D4EAFF4E9FB37E23D68491D6F4";
    private static final long WIN_EPOCH = 11644473600L;
    private static final double S_TO_NS = 1e9;

    // ---- SHA-256 自定义常量（全部来自 drm.py） ----
    private static final int[] SHA256_INITIAL = {
            0x6A09E667, 0xBB67AE85, 0x3C6EF372, 0xA54FF53A,
            0x510E527F, 0x9B05688C, 0x1F83D9AB, 0x5BE0CD19
    };
    private static final int[] SHA256_STATE = {
            0xFD9FBEA2, 0x91243CE7, 0xBE499094, 0xCD2E1E0A,
            0x6C006F49, 0x04BD35AB, 0xFEB0A7E4, 0x03EFFBD2
    };
    private static final int[] SHA256_SCHEDULE = {
            0xEB24BF9E, 0x1558691F, 0xA0C09EE2, 0x0CFA32BA,
            0xA622EE8C, 0xD1DD9491, 0xC0DD25A0, 0xF9E70AB6,
            0xF0C31573, 0x7058CE4E, 0x7188E606, 0x00E5AB33,
            0xD258B060, 0x72766564
    };
    private static final int[] SHA256_ROUND_CONSTANTS = {
            0x428A2F98, 0x71374491, 0xB5C0FBCF, 0xE9B5DBA5, 0x3956C25B, 0x59F111F1, 0x923F82A4, 0xAB1C5ED5,
            0xD807AA98, 0x12835B01, 0x243185BE, 0x550C7DC3, 0x72BE5D74, 0x80DEB1FE, 0x9BDC06A7, 0xC19BF174,
            0xE49B69C1, 0xEFBE4786, 0x0FC19DC6, 0x240CA1CC, 0x2DE92C6F, 0x4A7484AA, 0x5CB0A9DC, 0x76F988DA,
            0x983E5152, 0xA831C66D, 0xB00327C8, 0xBF597FC7, 0xC6E00BF3, 0xD5A79147, 0x06CA6351, 0x14292967,
            0x27B70A85, 0x2E1B2138, 0x4D2C6DFC, 0x53380D13, 0x650A7354, 0x766A0ABB, 0x81C2C92E, 0x92722C85,
            0xA2BFE8A1, 0xA81A664B, 0xC24B8B70, 0xC76C51A3, 0xD192E819, 0xD6990624, 0xF40E3585, 0x106AA070,
            0x19A4C116, 0x1E376C08, 0x2748774C, 0x34B0BCB5, 0x391C0CB3, 0x4ED8AA4A, 0x5B9CCA4F, 0x682E6FF3,
            0x748F82EE, 0x78A5636F, 0x84C87814, 0x8CC70208, 0x90BEFFFA, 0xA4506CEB, 0xBEF9A3F7, 0xC67178F2
    };
    private static final int SHA256_ADJUSTMENT = 0xC9DC06A7;
    private static final int[] SHA256_BLOCK_CONSTANTS;
    static {
        SHA256_BLOCK_CONSTANTS = SHA256_ROUND_CONSTANTS.clone();
        SHA256_BLOCK_CONSTANTS[14] = SHA256_ADJUSTMENT;
    }
    private static final int SHA256_SIGMA0_ADJUSTMENT = 0x059C0B80;
    private static final int SHA256_SIGMA1_ADJUSTMENT = 0x000B92C0;

    // ---- CS64 常量 ----
    private static final int[][] CS64_COEFFICIENTS = {
            {0xE82E91B1, 0xB32700D0}, {0x44DE24C6, 0x88DFF51D}, {0x3BEB65BC, 0x29514038},
            {0xC89C5933, 0xB328CBDF}, {0x8D1EC211, 0xD405B4D5}, {0x314EF5D5, 0x26E55F3C},
            {0xE92491B1, 0x607801C6}, {0xF0A5B9D5, 0x82B2C1FB}, {0x06064CF3, 0xFA51A3DA},
            {0xDA79FA60, 0x207CC464}, {0xBED20560, 0x3C3C74F3}, {0x89F9C9F8, 0xD2D14B61},
            {0x35FA38C6, 0xBB04A2C1}, {0xCB49E3C6, 0xC5626167}
    };
    private static final int CS64_FACTOR = 0x82893933;
    private static final int CS64_ADJUSTMENT0 = 0xF2F90B1A;
    private static final int CS64_ADJUSTMENT1 = 0x0B1A0000;

    /** 时钟偏差修正（秒），供业务层按需调整 */
    public static volatile double clockSkewSeconds = 0.0;

    /** 入口：对应 generate_sec_ms_gec_v2 */
    public static String genSecMsGecV2(Double unixTimestamp) {
        double ticks = (unixTimestamp != null)
                ? unixTimestamp
                : (System.currentTimeMillis() / 1000.0 + clockSkewSeconds);
        ticks += WIN_EPOCH;
        ticks -= ticks % 300;
        ticks *= S_TO_NS / 100;

        // 与 Python f"{ticks:.0f}" 对齐（IEEE 754 银行家舍入）
        String ticksStr = String.format(Locale.ROOT, "%.0f", ticks);
        byte[] data = (ticksStr + TRUSTED_CLIENT_TOKEN).getBytes(java.nio.charset.StandardCharsets.US_ASCII);

        byte[] digest = sha256Digest(data);
        int[] cs64 = cs64Digest(data, digest);

        StringBuilder shaHex = new StringBuilder(64);
        for (byte b : digest) shaHex.append(String.format("%02X", b & 0xFF));

        StringBuilder cs64Hex = new StringBuilder(16);
        for (int word : cs64) {
            for (int i = 0; i < 4; i++) {
                cs64Hex.append(String.format("%02X", (word >>> (8 * i)) & 0xFF));
            }
        }
        return cs64Hex + "-" + shaHex;
    }

    // ==================== SHA-256（自定义变体） ====================

    private static int[] sha256Init(byte[] data) {
        int[] words = new int[64];
        words[14] = ((data[0] & 0xFF) << 16) | ((data[1] & 0xFF) << 8) | (data[2] & 0xFF);
        words[15] = ((data[3] & 0xFF) << 24) | ((data[4] & 0xFF) << 16)
                | ((data[5] & 0xFF) << 8)  |  (data[6] & 0xFF);

        for (int index = 16; index < 30; index++) {
            int value = SHA256_SCHEDULE[index - 16];
            if (index - 2 >= 14) {
                int s = sigma1(words[index - 2]);
                if (index == 16) s ^= SHA256_SIGMA1_ADJUSTMENT;
                value += s;
            }
            if (index - 7 >= 14) value += words[index - 7];
            if (index - 15 >= 14) {
                int s = sigma0(words[index - 15]);
                if (index == 29) s ^= SHA256_SIGMA0_ADJUSTMENT;
                value += s;
            }
            words[index] = value;
        }
        words[30] = sigma1(words[28]) + words[23] + sigma0(words[15]) + words[14]
                + SHA256_ADJUSTMENT - SHA256_ROUND_CONSTANTS[14];

        expandSchedule(words, 31);

        int[] working = sha256Rounds(SHA256_STATE, words, 14);
        int[] out = new int[8];
        for (int i = 0; i < 8; i++) out[i] = SHA256_INITIAL[i] + working[i];
        return out;
    }

    private static void expandSchedule(int[] w, int firstWord) {
        for (int i = firstWord; i < 64; i++) {
            int x = w[i - 2], y = w[i - 15];
            int s1 = (x >>> 17) ^ (x << 15) ^ (x >>> 19) ^ (x << 13) ^ (x >>> 10);
            int s0 = (y >>> 7)  ^ (y << 25) ^ (y >>> 18) ^ (y << 14) ^ (y >>> 3);
            w[i] = s1 + w[i - 7] + s0 + w[i - 16];
        }
    }

    private static int[] sha256Rounds(int[] state, int[] words, int firstRound) {
        int a = state[0], b = state[1], c = state[2], d = state[3];
        int e = state[4], f = state[5], g = state[6], h = state[7];
        int[] k = (firstRound == 14) ? SHA256_BLOCK_CONSTANTS : SHA256_ROUND_CONSTANTS;
        for (int i = firstRound; i < 64; i++) {
            int S1 = Integer.rotateRight(e, 6) ^ Integer.rotateRight(e, 11) ^ Integer.rotateRight(e, 25);
            int ch = (e & f) ^ (~e & g);
            int t1 = h + S1 + ch + k[i] + words[i];
            int S0 = Integer.rotateRight(a, 2) ^ Integer.rotateRight(a, 13) ^ Integer.rotateRight(a, 22);
            int maj = (a & b) ^ (a & c) ^ (b & c);
            int t2 = S0 + maj;
            h = g; g = f; f = e; e = d + t1;
            d = c; c = b; b = a; a = t1 + t2;
        }
        return new int[]{a, b, c, d, e, f, g, h};
    }

    private static int sigma0(int v) {
        return (v >>> 7) ^ (v << 25) ^ (v >>> 18) ^ (v << 14) ^ (v >>> 3);
    }
    private static int sigma1(int v) {
        return (v >>> 17) ^ (v << 15) ^ (v >>> 19) ^ (v << 13) ^ (v >>> 10);
    }

    private static byte[] sha256Digest(byte[] data) {
        if (data.length < 7) throw new IllegalArgumentException("Token salt must contain at least seven bytes");
        int length = 57 + data.length;
        int padLen = (55 - length) % 64;
        if (padLen < 0) padLen += 64;

        int tailLen = data.length - 7;
        byte[] padded = new byte[tailLen + 1 + padLen + 8];
        System.arraycopy(data, 7, padded, 0, tailLen);
        padded[tailLen] = (byte) 0x80;

        long bitLen = (long) length * 8;
        for (int i = 0; i < 8; i++) padded[padded.length - 1 - i] = (byte) (bitLen >>> (8 * i));

        int[] state = sha256Init(data);
        for (int off = 0; off < padded.length; off += 64) {
            int[] w = new int[64];
            for (int i = 0; i < 16; i++) {
                int p = off + i * 4;
                w[i] = ((padded[p] & 0xFF) << 24) | ((padded[p+1] & 0xFF) << 16)
                        | ((padded[p+2] & 0xFF) << 8) | (padded[p+3] & 0xFF);
            }
            expandSchedule(w, 16);
            int[] working = sha256Rounds(state, w, 0);
            for (int i = 0; i < 8; i++) state[i] += working[i];
        }

        byte[] digest = new byte[32];
        for (int i = 0; i < 8; i++) {
            digest[i*4]   = (byte)(state[i] >>> 24);
            digest[i*4+1] = (byte)(state[i] >>> 16);
            digest[i*4+2] = (byte)(state[i] >>> 8);
            digest[i*4+3] = (byte) state[i];
        }
        return digest;
    }

    // ==================== CS64 ====================

    private static int[] wordsLe4Even(byte[] data) {
        int padLen = (4 - data.length % 4) % 4;
        byte[] padded = new byte[data.length + padLen];
        System.arraycopy(data, 0, padded, 0, data.length);
        int count = padded.length / 4;
        int[] out = new int[count + (count % 2)];
        for (int i = 0; i < count; i++) {
            int p = i * 4;
            out[i] = (padded[p] & 0xFF) | ((padded[p+1] & 0xFF) << 8)
                    | ((padded[p+2] & 0xFF) << 16) | ((padded[p+3] & 0xFF) << 24);
        }
        return out;
    }

    private static int[] cs64Digest(byte[] data, byte[] digest) {
        int keyA = (digest[0] & 0xFF) | ((digest[1] & 0xFF) << 8) | ((digest[2] & 0xFF) << 16) | ((digest[3] & 0xFF) << 24);
        int keyB = (digest[4] & 0xFF) | ((digest[5] & 0xFF) << 8) | ((digest[6] & 0xFF) << 16) | ((digest[7] & 0xFF) << 24);
        keyA |= 1;
        keyB |= 1;
        int[] factors = { keyA * CS64_FACTOR, keyB * CS64_FACTOR };

        byte[] prefixed = new byte[data.length + 1];
        System.arraycopy(data, 0, prefixed, 1, data.length);
        int[] words = wordsLe4Even(prefixed);

        int nCoef = CS64_COEFFICIENTS.length;
        int total = nCoef + words.length;
        int[] terms   = new int[total];
        int[] offsets = new int[total];

        for (int i = 0; i < nCoef; i++) {
            terms[i]   = CS64_COEFFICIENTS[i][0] * factors[i % 2];
            offsets[i] = CS64_COEFFICIENTS[i][1];
        }
        for (int i = 0; i < words.length; i++) {
            int word = words[i];
            int term = word * (i % 2 == 0 ? keyA : keyB);
            int offset = Integer.rotateLeft(word, 16) * (i % 2 == 0 ? 0xEF0569FB : 0xC31713DB);
            if (i == 0) {
                term   += CS64_ADJUSTMENT0 * factors[0];
                offset += CS64_ADJUSTMENT1;
            }
            terms[nCoef + i]   = term;
            offsets[nCoef + i] = offset;
        }

        int swapAcc = 0, swapLast = 0, state = 0, acc = 0;
        for (int i = 0; i < total; i += 2) {
            int x = terms[i], y = terms[i + 1];

            int t = Integer.rotateLeft(offsets[i] + x, 16);
            int q = t * 0x76F50F34;
            swapAcc += Integer.rotateLeft(q, 16) * 0xC3EFEA97 + q;

            t = Integer.rotateLeft(offsets[i + 1] + y, 16);
            q = t * 0x1881CE3C;
            swapLast = Integer.rotateLeft(q, 16) * 0x35BD1EC9 + q;
            swapAcc += swapLast;

            state = state * keyA + x;
            state = Integer.rotateLeft(state, 16) * 0xCF98B111;
            state = Integer.rotateLeft(state, 16) * 0x87085B9F;
            state = Integer.rotateLeft(state, 16) * 0x12CEB96D;
            state = Integer.rotateLeft(state, 16) * 0x257E1D83;
            acc += state;

            t = state * keyB + y;
            t = Integer.rotateLeft(t, 16) * 0xA27416F5;
            t = Integer.rotateLeft(t, 16) * 0xD38396FF;
            state = Integer.rotateLeft(Integer.rotateLeft(t, 16) * 0x7C932B89, 16) * 0xBFA49F69;
            acc += state;
        }

        return new int[]{ swapLast ^ state, swapAcc ^ acc };
    }
}