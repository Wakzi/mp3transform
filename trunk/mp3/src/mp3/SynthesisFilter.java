/*
 * 11/19/04 1.0 moved to LGPL.
 * 
 * 04/01/00 Fixes for running under build 23xx Microsoft JVM. mdm.
 * 
 * 19/12/99 Performance improvements to compute_pcm_samples().  
 *			Mat McGowan. mdm@techie.com. 
 *
 * 16/02/99 Java Conversion by E.B , javalayer@javazoom.net
 *
 *  @(#) synthesis_filter.h 1.8, last edit: 6/15/94 16:52:00
 *  @(#) Copyright (C) 1993, 1994 Tobias Bading (bading@cs.tu-berlin.de)
 *  @(#) Berlin University of Technology
 *
 *-----------------------------------------------------------------------
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU Library General Public License as published
 *   by the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Library General Public License for more details.
 *
 *   You should have received a copy of the GNU Library General Public
 *   License along with this program; if not, write to the Free Software
 *   Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *----------------------------------------------------------------------
 */
package mp3;

/**
 * A class for the synthesis filter bank. This class does a fast downsampling
 * from 32, 44.1 or 48 kHz to 8 kHz, if ULAW is defined. Frequencies above 4 kHz
 * are removed by ignoring higher subbands.
 */
final class SynthesisFilter {
    private double[] v1 = new double[512];
    private double[] v2 = new double[512];
    private double[] actualV = v1; // v1 or v2
    private int actualWritePos = 15; // 0-15
    private double[] samples = new double[32]; // 32 new subband samples
    private int channel;
    private double scaleFactor;
    private double[] tmpOutBuffer = new double[32];
    // DOUBLE
    private static final double MY_PI = 3.14159265358979323846;
    private static final double cos1_64 = divCos(MY_PI / 64.0);
    private static final double cos3_64 = divCos(MY_PI * 3.0 / 64.0);
    private static final double cos5_64 = divCos(MY_PI * 5.0 / 64.0);
    private static final double cos7_64 = divCos(MY_PI * 7.0 / 64.0);
    private static final double cos9_64 = divCos(MY_PI * 9.0 / 64.0);
    private static final double cos11_64 = divCos(MY_PI * 11.0 / 64.0);
    private static final double cos13_64 = divCos(MY_PI * 13.0 / 64.0);
    private static final double cos15_64 = divCos(MY_PI * 15.0 / 64.0);
    private static final double cos17_64 = divCos(MY_PI * 17.0 / 64.0);
    private static final double cos19_64 = divCos(MY_PI * 19.0 / 64.0);
    private static final double cos21_64 = divCos(MY_PI * 21.0 / 64.0);
    private static final double cos23_64 = divCos(MY_PI * 23.0 / 64.0);
    private static final double cos25_64 = divCos(MY_PI * 25.0 / 64.0);
    private static final double cos27_64 = divCos(MY_PI * 27.0 / 64.0);
    private static final double cos29_64 = divCos(MY_PI * 29.0 / 64.0);
    private static final double cos31_64 = divCos(MY_PI * 31.0 / 64.0);
    private static final double cos1_32 = divCos(MY_PI / 32.0);
    private static final double cos3_32 = divCos(MY_PI * 3.0 / 32.0);
    private static final double cos5_32 = divCos(MY_PI * 5.0 / 32.0);
    private static final double cos7_32 = divCos(MY_PI * 7.0 / 32.0);
    private static final double cos9_32 = divCos(MY_PI * 9.0 / 32.0);
    private static final double cos11_32 = divCos(MY_PI * 11.0 / 32.0);
    private static final double cos13_32 = divCos(MY_PI * 13.0 / 32.0);
    private static final double cos15_32 = divCos(MY_PI * 15.0 / 32.0);
    private static final double cos1_16 = divCos(MY_PI / 16.0);
    private static final double cos3_16 = divCos(MY_PI * 3.0 / 16.0);
    private static final double cos5_16 = divCos(MY_PI * 5.0 / 16.0);
    private static final double cos7_16 = divCos(MY_PI * 7.0 / 16.0);
    private static final double cos1_8 = divCos(MY_PI / 8.0);
    private static final double cos3_8 = divCos(MY_PI * 3.0 / 8.0);
    private static final double cos1_4 = divCos(MY_PI / 4.0);
    private static final double[][] D16 = Constants.D16;
    /**
     * Contructor. The scalefactor scales the calculated double pcm samples to
     * short values (raw pcm samples are in [-1.0, 1.0], if no violations
     * occur).
     */
    SynthesisFilter(int channelNumber, double factor) {
        channel = channelNumber;
        scaleFactor = factor;
    }

    private static double divCos(double a) {
        return (double) (1.0 / (2.0 * Math.cos(a)));
    }

    /**
     * Compute new values via a fast cosine transform.
     */
    private void computeNewV() {
        double nv0, nv1, nv2, nv3, nv4, nv5, nv6, nv7, nv8, nv9;
        double nv10, nv11, nv12, nv13, nv14, nv15, nv16, nv17, nv18, nv19;
        double nv20, nv21, nv22, nv23, nv24, nv25, nv26, nv27, nv28, nv29;
        double nv30, nv31;
        double[] s = samples;
        double s0 = s[0];
        double s1 = s[1];
        double s2 = s[2];
        double s3 = s[3];
        double s4 = s[4];
        double s5 = s[5];
        double s6 = s[6];
        double s7 = s[7];
        double s8 = s[8];
        double s9 = s[9];
        double s10 = s[10];
        double s11 = s[11];
        double s12 = s[12];
        double s13 = s[13];
        double s14 = s[14];
        double s15 = s[15];
        double s16 = s[16];
        double s17 = s[17];
        double s18 = s[18];
        double s19 = s[19];
        double s20 = s[20];
        double s21 = s[21];
        double s22 = s[22];
        double s23 = s[23];
        double s24 = s[24];
        double s25 = s[25];
        double s26 = s[26];
        double s27 = s[27];
        double s28 = s[28];
        double s29 = s[29];
        double s30 = s[30];
        double s31 = s[31];
        double p0 = s0 + s31;
        double p1 = s1 + s30;
        double p2 = s2 + s29;
        double p3 = s3 + s28;
        double p4 = s4 + s27;
        double p5 = s5 + s26;
        double p6 = s6 + s25;
        double p7 = s7 + s24;
        double p8 = s8 + s23;
        double p9 = s9 + s22;
        double p10 = s10 + s21;
        double p11 = s11 + s20;
        double p12 = s12 + s19;
        double p13 = s13 + s18;
        double p14 = s14 + s17;
        double p15 = s15 + s16;
        double pp0 = p0 + p15;
        double pp1 = p1 + p14;
        double pp2 = p2 + p13;
        double pp3 = p3 + p12;
        double pp4 = p4 + p11;
        double pp5 = p5 + p10;
        double pp6 = p6 + p9;
        double pp7 = p7 + p8;
        double pp8 = (p0 - p15) * cos1_32;
        double pp9 = (p1 - p14) * cos3_32;
        double pp10 = (p2 - p13) * cos5_32;
        double pp11 = (p3 - p12) * cos7_32;
        double pp12 = (p4 - p11) * cos9_32;
        double pp13 = (p5 - p10) * cos11_32;
        double pp14 = (p6 - p9) * cos13_32;
        double pp15 = (p7 - p8) * cos15_32;
        p0 = pp0 + pp7;
        p1 = pp1 + pp6;
        p2 = pp2 + pp5;
        p3 = pp3 + pp4;
        p4 = (pp0 - pp7) * cos1_16;
        p5 = (pp1 - pp6) * cos3_16;
        p6 = (pp2 - pp5) * cos5_16;
        p7 = (pp3 - pp4) * cos7_16;
        p8 = pp8 + pp15;
        p9 = pp9 + pp14;
        p10 = pp10 + pp13;
        p11 = pp11 + pp12;
        p12 = (pp8 - pp15) * cos1_16;
        p13 = (pp9 - pp14) * cos3_16;
        p14 = (pp10 - pp13) * cos5_16;
        p15 = (pp11 - pp12) * cos7_16;
        pp0 = p0 + p3;
        pp1 = p1 + p2;
        pp2 = (p0 - p3) * cos1_8;
        pp3 = (p1 - p2) * cos3_8;
        pp4 = p4 + p7;
        pp5 = p5 + p6;
        pp6 = (p4 - p7) * cos1_8;
        pp7 = (p5 - p6) * cos3_8;
        pp8 = p8 + p11;
        pp9 = p9 + p10;
        pp10 = (p8 - p11) * cos1_8;
        pp11 = (p9 - p10) * cos3_8;
        pp12 = p12 + p15;
        pp13 = p13 + p14;
        pp14 = (p12 - p15) * cos1_8;
        pp15 = (p13 - p14) * cos3_8;
        p0 = pp0 + pp1;
        p1 = (pp0 - pp1) * cos1_4;
        p2 = pp2 + pp3;
        p3 = (pp2 - pp3) * cos1_4;
        p4 = pp4 + pp5;
        p5 = (pp4 - pp5) * cos1_4;
        p6 = pp6 + pp7;
        p7 = (pp6 - pp7) * cos1_4;
        p8 = pp8 + pp9;
        p9 = (pp8 - pp9) * cos1_4;
        p10 = pp10 + pp11;
        p11 = (pp10 - pp11) * cos1_4;
        p12 = pp12 + pp13;
        p13 = (pp12 - pp13) * cos1_4;
        p14 = pp14 + pp15;
        p15 = (pp14 - pp15) * cos1_4;
        // this is pretty insane coding
        double tmp1;
        nv19/* 36-17 */= -(nv4 = (nv12 = p7) + p5) - p6;
        nv27/* 44-17 */= -p6 - p7 - p4;
        nv6 = (nv10 = (nv14 = p15) + p11) + p13;
        nv17/* 34-17 */= -(nv2 = p15 + p13 + p9) - p14;
        nv21/* 38-17 */= (tmp1 = -p14 - p15 - p10 - p11) - p13;
        nv29/* 46-17 */= -p14 - p15 - p12 - p8;
        nv25/* 42-17 */= tmp1 - p12;
        nv31/* 48-17 */= -p0;
        nv0 = p1;
        nv23/* 40-17 */= -(nv8 = p3) - p2;
        p0 = (s0 - s31) * cos1_64;
        p1 = (s1 - s30) * cos3_64;
        p2 = (s2 - s29) * cos5_64;
        p3 = (s3 - s28) * cos7_64;
        p4 = (s4 - s27) * cos9_64;
        p5 = (s5 - s26) * cos11_64;
        p6 = (s6 - s25) * cos13_64;
        p7 = (s7 - s24) * cos15_64;
        p8 = (s8 - s23) * cos17_64;
        p9 = (s9 - s22) * cos19_64;
        p10 = (s10 - s21) * cos21_64;
        p11 = (s11 - s20) * cos23_64;
        p12 = (s12 - s19) * cos25_64;
        p13 = (s13 - s18) * cos27_64;
        p14 = (s14 - s17) * cos29_64;
        p15 = (s15 - s16) * cos31_64;
        pp0 = p0 + p15;
        pp1 = p1 + p14;
        pp2 = p2 + p13;
        pp3 = p3 + p12;
        pp4 = p4 + p11;
        pp5 = p5 + p10;
        pp6 = p6 + p9;
        pp7 = p7 + p8;
        pp8 = (p0 - p15) * cos1_32;
        pp9 = (p1 - p14) * cos3_32;
        pp10 = (p2 - p13) * cos5_32;
        pp11 = (p3 - p12) * cos7_32;
        pp12 = (p4 - p11) * cos9_32;
        pp13 = (p5 - p10) * cos11_32;
        pp14 = (p6 - p9) * cos13_32;
        pp15 = (p7 - p8) * cos15_32;
        p0 = pp0 + pp7;
        p1 = pp1 + pp6;
        p2 = pp2 + pp5;
        p3 = pp3 + pp4;
        p4 = (pp0 - pp7) * cos1_16;
        p5 = (pp1 - pp6) * cos3_16;
        p6 = (pp2 - pp5) * cos5_16;
        p7 = (pp3 - pp4) * cos7_16;
        p8 = pp8 + pp15;
        p9 = pp9 + pp14;
        p10 = pp10 + pp13;
        p11 = pp11 + pp12;
        p12 = (pp8 - pp15) * cos1_16;
        p13 = (pp9 - pp14) * cos3_16;
        p14 = (pp10 - pp13) * cos5_16;
        p15 = (pp11 - pp12) * cos7_16;
        pp0 = p0 + p3;
        pp1 = p1 + p2;
        pp2 = (p0 - p3) * cos1_8;
        pp3 = (p1 - p2) * cos3_8;
        pp4 = p4 + p7;
        pp5 = p5 + p6;
        pp6 = (p4 - p7) * cos1_8;
        pp7 = (p5 - p6) * cos3_8;
        pp8 = p8 + p11;
        pp9 = p9 + p10;
        pp10 = (p8 - p11) * cos1_8;
        pp11 = (p9 - p10) * cos3_8;
        pp12 = p12 + p15;
        pp13 = p13 + p14;
        pp14 = (p12 - p15) * cos1_8;
        pp15 = (p13 - p14) * cos3_8;
        p0 = pp0 + pp1;
        p1 = (pp0 - pp1) * cos1_4;
        p2 = pp2 + pp3;
        p3 = (pp2 - pp3) * cos1_4;
        p4 = pp4 + pp5;
        p5 = (pp4 - pp5) * cos1_4;
        p6 = pp6 + pp7;
        p7 = (pp6 - pp7) * cos1_4;
        p8 = pp8 + pp9;
        p9 = (pp8 - pp9) * cos1_4;
        p10 = pp10 + pp11;
        p11 = (pp10 - pp11) * cos1_4;
        p12 = pp12 + pp13;
        p13 = (pp12 - pp13) * cos1_4;
        p14 = pp14 + pp15;
        p15 = (pp14 - pp15) * cos1_4;
        // manually doing something that a compiler should handle sucks
        // coding like this is hard to read
        double tmp2;
        nv5 = (nv11 = (nv13 = (nv15 = p15) + p7) + p11) + p5 + p13;
        nv7 = (nv9 = p15 + p11 + p3) + p13;
        nv16/* 33-17 */= -(nv1 = (tmp1 = p13 + p15 + p9) + p1) - p14;
        nv18/* 35-17 */= -(nv3 = tmp1 + p5 + p7) - p6 - p14;
        nv22/* 39-17 */= (tmp1 = -p10 - p11 - p14 - p15) - p13 - p2 - p3;
        nv20/* 37-17 */= tmp1 - p13 - p5 - p6 - p7;
        nv24/* 41-17 */= tmp1 - p12 - p2 - p3;
        nv26/* 43-17 */= tmp1 - p12 - (tmp2 = p4 + p6 + p7);
        nv30/* 47-17 */= (tmp1 = -p8 - p12 - p14 - p15) - p0;
        nv28/* 45-17 */= tmp1 - tmp2;
        // insert V[0-15] (== nv[0-15]) into actual v:
        // double[] x2 = actual_v + actual_write_pos;
        double dest[] = actualV;
        int pos = actualWritePos;
        dest[0 + pos] = nv0;
        dest[16 + pos] = nv1;
        dest[32 + pos] = nv2;
        dest[48 + pos] = nv3;
        dest[64 + pos] = nv4;
        dest[80 + pos] = nv5;
        dest[96 + pos] = nv6;
        dest[112 + pos] = nv7;
        dest[128 + pos] = nv8;
        dest[144 + pos] = nv9;
        dest[160 + pos] = nv10;
        dest[176 + pos] = nv11;
        dest[192 + pos] = nv12;
        dest[208 + pos] = nv13;
        dest[224 + pos] = nv14;
        dest[240 + pos] = nv15;
        // V[16] is always 0.0:
        dest[256 + pos] = 0.0f;
        // insert V[17-31] (== -nv[15-1]) into actual v:
        dest[272 + pos] = -nv15;
        dest[288 + pos] = -nv14;
        dest[304 + pos] = -nv13;
        dest[320 + pos] = -nv12;
        dest[336 + pos] = -nv11;
        dest[352 + pos] = -nv10;
        dest[368 + pos] = -nv9;
        dest[384 + pos] = -nv8;
        dest[400 + pos] = -nv7;
        dest[416 + pos] = -nv6;
        dest[432 + pos] = -nv5;
        dest[448 + pos] = -nv4;
        dest[464 + pos] = -nv3;
        dest[480 + pos] = -nv2;
        dest[496 + pos] = -nv1;
        // insert V[32] (== -nv[0]) into other v:
        dest = (actualV == v1) ? v2 : v1;
        dest[0 + pos] = -nv0;
        // insert V[33-48] (== nv[16-31]) into other v:
        dest[16 + pos] = nv16;
        dest[32 + pos] = nv17;
        dest[48 + pos] = nv18;
        dest[64 + pos] = nv19;
        dest[80 + pos] = nv20;
        dest[96 + pos] = nv21;
        dest[112 + pos] = nv22;
        dest[128 + pos] = nv23;
        dest[144 + pos] = nv24;
        dest[160 + pos] = nv25;
        dest[176 + pos] = nv26;
        dest[192 + pos] = nv27;
        dest[208 + pos] = nv28;
        dest[224 + pos] = nv29;
        dest[240 + pos] = nv30;
        dest[256 + pos] = nv31;
        // insert V[49-63] (== nv[30-16]) into other v:
        dest[272 + pos] = nv30;
        dest[288 + pos] = nv29;
        dest[304 + pos] = nv28;
        dest[320 + pos] = nv27;
        dest[336 + pos] = nv26;
        dest[352 + pos] = nv25;
        dest[368 + pos] = nv24;
        dest[384 + pos] = nv23;
        dest[400 + pos] = nv22;
        dest[416 + pos] = nv21;
        dest[432 + pos] = nv20;
        dest[448 + pos] = nv19;
        dest[464 + pos] = nv18;
        dest[480 + pos] = nv17;
        dest[496 + pos] = nv16;
    }

    private void computePcmSamples0() {
        final double[] vp = actualV;
        final double[] tmpOut = tmpOutBuffer;
        int dvp = 0;
        for (int i = 0; i < 32; i++) {
            final double[] dp = D16[i];
            double pcmSample = (double) (((vp[0 + dvp] * dp[0])
                    + (vp[15 + dvp] * dp[1]) + (vp[14 + dvp] * dp[2])
                    + (vp[13 + dvp] * dp[3]) + (vp[12 + dvp] * dp[4])
                    + (vp[11 + dvp] * dp[5]) + (vp[10 + dvp] * dp[6])
                    + (vp[9 + dvp] * dp[7]) + (vp[8 + dvp] * dp[8])
                    + (vp[7 + dvp] * dp[9]) + (vp[6 + dvp] * dp[10])
                    + (vp[5 + dvp] * dp[11]) + (vp[4 + dvp] * dp[12])
                    + (vp[3 + dvp] * dp[13]) + (vp[2 + dvp] * dp[14]) + (vp[1 + dvp] * dp[15])) * scaleFactor);
            tmpOut[i] = pcmSample;
            dvp += 16;
        }
    }

    private void computePcmSamples1() {
        final double[] vp = actualV;
        final double[] tmpOut = tmpOutBuffer;
        int dvp = 0;
        for (int i = 0; i < 32; i++) {
            final double[] dp = D16[i];
            double pcmSample = (double) (((vp[1 + dvp] * dp[0])
                    + (vp[0 + dvp] * dp[1]) + (vp[15 + dvp] * dp[2])
                    + (vp[14 + dvp] * dp[3]) + (vp[13 + dvp] * dp[4])
                    + (vp[12 + dvp] * dp[5]) + (vp[11 + dvp] * dp[6])
                    + (vp[10 + dvp] * dp[7]) + (vp[9 + dvp] * dp[8])
                    + (vp[8 + dvp] * dp[9]) + (vp[7 + dvp] * dp[10])
                    + (vp[6 + dvp] * dp[11]) + (vp[5 + dvp] * dp[12])
                    + (vp[4 + dvp] * dp[13]) + (vp[3 + dvp] * dp[14]) + (vp[2 + dvp] * dp[15])) * scaleFactor);
            tmpOut[i] = pcmSample;
            dvp += 16;
        }
    }

    private void computePcmSamples2() {
        final double[] vp = actualV;
        final double[] tmpOut = tmpOutBuffer;
        int dvp = 0;
        for (int i = 0; i < 32; i++) {
            final double[] dp = D16[i];
            double pcmSample = (double) (((vp[2 + dvp] * dp[0])
                    + (vp[1 + dvp] * dp[1]) + (vp[0 + dvp] * dp[2])
                    + (vp[15 + dvp] * dp[3]) + (vp[14 + dvp] * dp[4])
                    + (vp[13 + dvp] * dp[5]) + (vp[12 + dvp] * dp[6])
                    + (vp[11 + dvp] * dp[7]) + (vp[10 + dvp] * dp[8])
                    + (vp[9 + dvp] * dp[9]) + (vp[8 + dvp] * dp[10])
                    + (vp[7 + dvp] * dp[11]) + (vp[6 + dvp] * dp[12])
                    + (vp[5 + dvp] * dp[13]) + (vp[4 + dvp] * dp[14]) + (vp[3 + dvp] * dp[15])) * scaleFactor);
            tmpOut[i] = pcmSample;
            dvp += 16;
        }
    }

    private void computePcmSamples3() {
        final double[] vp = actualV;
        final double[] tmpOut = tmpOutBuffer;
        int dvp = 0;
        for (int i = 0; i < 32; i++) {
            final double[] dp = D16[i];
            double pcmSample = (double) (((vp[3 + dvp] * dp[0])
                    + (vp[2 + dvp] * dp[1]) + (vp[1 + dvp] * dp[2])
                    + (vp[0 + dvp] * dp[3]) + (vp[15 + dvp] * dp[4])
                    + (vp[14 + dvp] * dp[5]) + (vp[13 + dvp] * dp[6])
                    + (vp[12 + dvp] * dp[7]) + (vp[11 + dvp] * dp[8])
                    + (vp[10 + dvp] * dp[9]) + (vp[9 + dvp] * dp[10])
                    + (vp[8 + dvp] * dp[11]) + (vp[7 + dvp] * dp[12])
                    + (vp[6 + dvp] * dp[13]) + (vp[5 + dvp] * dp[14]) + (vp[4 + dvp] * dp[15])) * scaleFactor);
            tmpOut[i] = pcmSample;
            dvp += 16;
        }
    }

    private void computePcmSamples4() {
        final double[] vp = actualV;
        final double[] tmpOut = tmpOutBuffer;
        int dvp = 0;
        for (int i = 0; i < 32; i++) {
            final double[] dp = D16[i];
            double pcmSample = (double) (((vp[4 + dvp] * dp[0])
                    + (vp[3 + dvp] * dp[1]) + (vp[2 + dvp] * dp[2])
                    + (vp[1 + dvp] * dp[3]) + (vp[0 + dvp] * dp[4])
                    + (vp[15 + dvp] * dp[5]) + (vp[14 + dvp] * dp[6])
                    + (vp[13 + dvp] * dp[7]) + (vp[12 + dvp] * dp[8])
                    + (vp[11 + dvp] * dp[9]) + (vp[10 + dvp] * dp[10])
                    + (vp[9 + dvp] * dp[11]) + (vp[8 + dvp] * dp[12])
                    + (vp[7 + dvp] * dp[13]) + (vp[6 + dvp] * dp[14]) + (vp[5 + dvp] * dp[15])) * scaleFactor);
            tmpOut[i] = pcmSample;
            dvp += 16;
        }
    }

    private void computePcmSamples5() {
        final double[] vp = actualV;
        final double[] tmpOut = tmpOutBuffer;
        int dvp = 0;
        for (int i = 0; i < 32; i++) {
            final double[] dp = D16[i];
            double pcmSample = (double) (((vp[5 + dvp] * dp[0])
                    + (vp[4 + dvp] * dp[1]) + (vp[3 + dvp] * dp[2])
                    + (vp[2 + dvp] * dp[3]) + (vp[1 + dvp] * dp[4])
                    + (vp[0 + dvp] * dp[5]) + (vp[15 + dvp] * dp[6])
                    + (vp[14 + dvp] * dp[7]) + (vp[13 + dvp] * dp[8])
                    + (vp[12 + dvp] * dp[9]) + (vp[11 + dvp] * dp[10])
                    + (vp[10 + dvp] * dp[11]) + (vp[9 + dvp] * dp[12])
                    + (vp[8 + dvp] * dp[13]) + (vp[7 + dvp] * dp[14]) + (vp[6 + dvp] * dp[15])) * scaleFactor);
            tmpOut[i] = pcmSample;
            dvp += 16;
        }
    }

    private void computePcmSamples6() {
        final double[] vp = actualV;
        final double[] tmpOut = tmpOutBuffer;
        int dvp = 0;
        for (int i = 0; i < 32; i++) {
            final double[] dp = D16[i];
            double pcmSample = (double) (((vp[6 + dvp] * dp[0])
                    + (vp[5 + dvp] * dp[1]) + (vp[4 + dvp] * dp[2])
                    + (vp[3 + dvp] * dp[3]) + (vp[2 + dvp] * dp[4])
                    + (vp[1 + dvp] * dp[5]) + (vp[0 + dvp] * dp[6])
                    + (vp[15 + dvp] * dp[7]) + (vp[14 + dvp] * dp[8])
                    + (vp[13 + dvp] * dp[9]) + (vp[12 + dvp] * dp[10])
                    + (vp[11 + dvp] * dp[11]) + (vp[10 + dvp] * dp[12])
                    + (vp[9 + dvp] * dp[13]) + (vp[8 + dvp] * dp[14]) + (vp[7 + dvp] * dp[15])) * scaleFactor);
            tmpOut[i] = pcmSample;
            dvp += 16;
        }
    }

    private void computePcmSamples7() {
        final double[] vp = actualV;
        final double[] tmpOut = tmpOutBuffer;
        int dvp = 0;
        for (int i = 0; i < 32; i++) {
            final double[] dp = D16[i];
            double pcmSample = (double) (((vp[7 + dvp] * dp[0])
                    + (vp[6 + dvp] * dp[1]) + (vp[5 + dvp] * dp[2])
                    + (vp[4 + dvp] * dp[3]) + (vp[3 + dvp] * dp[4])
                    + (vp[2 + dvp] * dp[5]) + (vp[1 + dvp] * dp[6])
                    + (vp[0 + dvp] * dp[7]) + (vp[15 + dvp] * dp[8])
                    + (vp[14 + dvp] * dp[9]) + (vp[13 + dvp] * dp[10])
                    + (vp[12 + dvp] * dp[11]) + (vp[11 + dvp] * dp[12])
                    + (vp[10 + dvp] * dp[13]) + (vp[9 + dvp] * dp[14]) + (vp[8 + dvp] * dp[15])) * scaleFactor);
            tmpOut[i] = pcmSample;
            dvp += 16;
        }
    }

    private void computePcmSamples8() {
        final double[] vp = actualV;
        final double[] tmpOut = tmpOutBuffer;
        int dvp = 0;
        for (int i = 0; i < 32; i++) {
            final double[] dp = D16[i];
            double pcmSample = (double) (((vp[8 + dvp] * dp[0])
                    + (vp[7 + dvp] * dp[1]) + (vp[6 + dvp] * dp[2])
                    + (vp[5 + dvp] * dp[3]) + (vp[4 + dvp] * dp[4])
                    + (vp[3 + dvp] * dp[5]) + (vp[2 + dvp] * dp[6])
                    + (vp[1 + dvp] * dp[7]) + (vp[0 + dvp] * dp[8])
                    + (vp[15 + dvp] * dp[9]) + (vp[14 + dvp] * dp[10])
                    + (vp[13 + dvp] * dp[11]) + (vp[12 + dvp] * dp[12])
                    + (vp[11 + dvp] * dp[13]) + (vp[10 + dvp] * dp[14]) + (vp[9 + dvp] * dp[15])) * scaleFactor);
            tmpOut[i] = pcmSample;
            dvp += 16;
        }
    }

    private void computePcmSamples9() {
        final double[] vp = actualV;
        final double[] tmpOut = tmpOutBuffer;
        int dvp = 0;
        for (int i = 0; i < 32; i++) {
            final double[] dp = D16[i];
            double pcmSample = (double) (((vp[9 + dvp] * dp[0])
                    + (vp[8 + dvp] * dp[1]) + (vp[7 + dvp] * dp[2])
                    + (vp[6 + dvp] * dp[3]) + (vp[5 + dvp] * dp[4])
                    + (vp[4 + dvp] * dp[5]) + (vp[3 + dvp] * dp[6])
                    + (vp[2 + dvp] * dp[7]) + (vp[1 + dvp] * dp[8])
                    + (vp[0 + dvp] * dp[9]) + (vp[15 + dvp] * dp[10])
                    + (vp[14 + dvp] * dp[11]) + (vp[13 + dvp] * dp[12])
                    + (vp[12 + dvp] * dp[13]) + (vp[11 + dvp] * dp[14]) + (vp[10 + dvp] * dp[15])) * scaleFactor);
            tmpOut[i] = pcmSample;
            dvp += 16;
        }
    }

    private void computePcmSamples10() {
        final double[] vp = actualV;
        final double[] tmpOut = tmpOutBuffer;
        int dvp = 0;
        for (int i = 0; i < 32; i++) {
            final double[] dp = D16[i];
            double pcmSample = (double) (((vp[10 + dvp] * dp[0])
                    + (vp[9 + dvp] * dp[1]) + (vp[8 + dvp] * dp[2])
                    + (vp[7 + dvp] * dp[3]) + (vp[6 + dvp] * dp[4])
                    + (vp[5 + dvp] * dp[5]) + (vp[4 + dvp] * dp[6])
                    + (vp[3 + dvp] * dp[7]) + (vp[2 + dvp] * dp[8])
                    + (vp[1 + dvp] * dp[9]) + (vp[0 + dvp] * dp[10])
                    + (vp[15 + dvp] * dp[11]) + (vp[14 + dvp] * dp[12])
                    + (vp[13 + dvp] * dp[13]) + (vp[12 + dvp] * dp[14]) + (vp[11 + dvp] * dp[15])) * scaleFactor);
            tmpOut[i] = pcmSample;
            dvp += 16;
        }
    }

    private void computePcmSamples11() {
        final double[] vp = actualV;
        final double[] tmpOut = tmpOutBuffer;
        int dvp = 0;
        for (int i = 0; i < 32; i++) {
            final double[] dp = D16[i];
            double pcmSample = (double) (((vp[11 + dvp] * dp[0])
                    + (vp[10 + dvp] * dp[1]) + (vp[9 + dvp] * dp[2])
                    + (vp[8 + dvp] * dp[3]) + (vp[7 + dvp] * dp[4])
                    + (vp[6 + dvp] * dp[5]) + (vp[5 + dvp] * dp[6])
                    + (vp[4 + dvp] * dp[7]) + (vp[3 + dvp] * dp[8])
                    + (vp[2 + dvp] * dp[9]) + (vp[1 + dvp] * dp[10])
                    + (vp[0 + dvp] * dp[11]) + (vp[15 + dvp] * dp[12])
                    + (vp[14 + dvp] * dp[13]) + (vp[13 + dvp] * dp[14]) + (vp[12 + dvp] * dp[15])) * scaleFactor);
            tmpOut[i] = pcmSample;
            dvp += 16;
        }
    }

    private void computePcmSamples12() {
        final double[] vp = actualV;
        final double[] tmpOut = tmpOutBuffer;
        int dvp = 0;
        for (int i = 0; i < 32; i++) {
            final double[] dp = D16[i];
            double pcmSample = (double) (((vp[12 + dvp] * dp[0])
                    + (vp[11 + dvp] * dp[1]) + (vp[10 + dvp] * dp[2])
                    + (vp[9 + dvp] * dp[3]) + (vp[8 + dvp] * dp[4])
                    + (vp[7 + dvp] * dp[5]) + (vp[6 + dvp] * dp[6])
                    + (vp[5 + dvp] * dp[7]) + (vp[4 + dvp] * dp[8])
                    + (vp[3 + dvp] * dp[9]) + (vp[2 + dvp] * dp[10])
                    + (vp[1 + dvp] * dp[11]) + (vp[0 + dvp] * dp[12])
                    + (vp[15 + dvp] * dp[13]) + (vp[14 + dvp] * dp[14]) + (vp[13 + dvp] * dp[15])) * scaleFactor);
            tmpOut[i] = pcmSample;
            dvp += 16;
        }
    }

    private void computePcmSamples13() {
        final double[] vp = actualV;
        final double[] tmpOut = tmpOutBuffer;
        int dvp = 0;
        for (int i = 0; i < 32; i++) {
            final double[] dp = D16[i];
            double pcmSample = (double) (((vp[13 + dvp] * dp[0])
                    + (vp[12 + dvp] * dp[1]) + (vp[11 + dvp] * dp[2])
                    + (vp[10 + dvp] * dp[3]) + (vp[9 + dvp] * dp[4])
                    + (vp[8 + dvp] * dp[5]) + (vp[7 + dvp] * dp[6])
                    + (vp[6 + dvp] * dp[7]) + (vp[5 + dvp] * dp[8])
                    + (vp[4 + dvp] * dp[9]) + (vp[3 + dvp] * dp[10])
                    + (vp[2 + dvp] * dp[11]) + (vp[1 + dvp] * dp[12])
                    + (vp[0 + dvp] * dp[13]) + (vp[15 + dvp] * dp[14]) + (vp[14 + dvp] * dp[15])) * scaleFactor);
            tmpOut[i] = pcmSample;
            dvp += 16;
        }
    }

    private void computePcmSamples14() {
        final double[] vp = actualV;
        final double[] tmpOut = tmpOutBuffer;
        int dvp = 0;
        for (int i = 0; i < 32; i++) {
            final double[] dp = D16[i];
            double pcmSample = (double) (((vp[14 + dvp] * dp[0])
                    + (vp[13 + dvp] * dp[1]) + (vp[12 + dvp] * dp[2])
                    + (vp[11 + dvp] * dp[3]) + (vp[10 + dvp] * dp[4])
                    + (vp[9 + dvp] * dp[5]) + (vp[8 + dvp] * dp[6])
                    + (vp[7 + dvp] * dp[7]) + (vp[6 + dvp] * dp[8])
                    + (vp[5 + dvp] * dp[9]) + (vp[4 + dvp] * dp[10])
                    + (vp[3 + dvp] * dp[11]) + (vp[2 + dvp] * dp[12])
                    + (vp[1 + dvp] * dp[13]) + (vp[0 + dvp] * dp[14]) + (vp[15 + dvp] * dp[15])) * scaleFactor);
            tmpOut[i] = pcmSample;
            dvp += 16;
        }
    }

    private void computePcmSamples15() {
        final double[] vp = actualV;
        final double[] tmpOut = tmpOutBuffer;
        int dvp = 0;
        for (int i = 0; i < 32; i++) {
            final double dp[] = D16[i];
            double pcmSample = (double) (((vp[15 + dvp] * dp[0])
                    + (vp[14 + dvp] * dp[1]) + (vp[13 + dvp] * dp[2])
                    + (vp[12 + dvp] * dp[3]) + (vp[11 + dvp] * dp[4])
                    + (vp[10 + dvp] * dp[5]) + (vp[9 + dvp] * dp[6])
                    + (vp[8 + dvp] * dp[7]) + (vp[7 + dvp] * dp[8])
                    + (vp[6 + dvp] * dp[9]) + (vp[5 + dvp] * dp[10])
                    + (vp[4 + dvp] * dp[11]) + (vp[3 + dvp] * dp[12])
                    + (vp[2 + dvp] * dp[13]) + (vp[1 + dvp] * dp[14]) + (vp[0 + dvp] * dp[15])) * scaleFactor);
            tmpOut[i] = pcmSample;
            dvp += 16;
        }
    }

    private void computePcmSamples() {
        switch (actualWritePos) {
        case 0:
            computePcmSamples0();
            break;
        case 1:
            computePcmSamples1();
            break;
        case 2:
            computePcmSamples2();
            break;
        case 3:
            computePcmSamples3();
            break;
        case 4:
            computePcmSamples4();
            break;
        case 5:
            computePcmSamples5();
            break;
        case 6:
            computePcmSamples6();
            break;
        case 7:
            computePcmSamples7();
            break;
        case 8:
            computePcmSamples8();
            break;
        case 9:
            computePcmSamples9();
            break;
        case 10:
            computePcmSamples10();
            break;
        case 11:
            computePcmSamples11();
            break;
        case 12:
            computePcmSamples12();
            break;
        case 13:
            computePcmSamples13();
            break;
        case 14:
            computePcmSamples14();
            break;
        case 15:
            computePcmSamples15();
            break;
        }
    }

    /**
     * Calculate 32 PCM samples and write them
     */
    void calculatePcmSamples(double[] s, Decoder player) {
        for (int i = 0; i < 32; i++) {
            samples[i] = s[i];
        }
        computeNewV();
        computePcmSamples();
        player.appendSamples(channel, tmpOutBuffer);
        actualWritePos = (actualWritePos + 1) & 0xf;
        actualV = (actualV == v1) ? v2 : v1;
    }

}
