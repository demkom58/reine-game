package com.crown.graphic.gl;

public enum OglVersion {
    V2_0(2, 0, 110),
    V2_1(2, 1, 120),
    V3_0(3, 0, 130),
    V3_1(3, 1, 140),
    V3_2(3, 2, 150),
    V3_3(3, 3, 330),
    V4_0(4, 0, 400),
    V4_1(4, 1, 410),
    V4_2(4, 2, 420),
    V4_3(4, 3, 430),
    V4_4(4, 4, 440),
    V4_5(4, 5, 450),
    V4_6(4, 6, 460),
    ;

    private final int major;
    private final int minor;
    private final int glslVersion;

    OglVersion(int major, int minor, int glslVersion) {
        this.major = major;
        this.minor = minor;
        this.glslVersion = glslVersion;
    }

    public int major() {
        return major;
    }

    public int minor() {
        return minor;
    }

    public int glslVersion() {
        return glslVersion;
    }
}
