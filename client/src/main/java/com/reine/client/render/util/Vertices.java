package com.reine.client.render.util;

import com.reine.util.WorldSide;
import org.joml.Vector3i;

public final class Vertices {
    public static void quad(WorldSide side, Vector3i str, Vector3i end, float[] out) {
        switch (side) {
            case WEST -> {
                out[0] = str.x;
                out[1] = str.y;
                out[2] = str.z;

                out[3] = str.x;
                out[4] = str.y;
                out[5] = end.z;

                out[6] = str.x;
                out[7] = end.y;
                out[8] = str.z;

                out[9] = str.x;
                out[10] = end.y;
                out[11] = str.z;

                out[12] = str.x;
                out[13] = str.y;
                out[14] = end.z;

                out[15] = str.x;
                out[16] = end.y;
                out[17] = end.z;
            }
            case EAST -> {
                out[0] = str.x;
                out[1] = str.y;
                out[2] = str.z;

                out[3] = str.x;
                out[4] = end.y;
                out[5] = str.z;

                out[6] = str.x;
                out[7] = str.y;
                out[8] = end.z;

                out[9]  = str.x;
                out[10] = str.y;
                out[11] = end.z;

                out[12] = str.x;
                out[13] = end.y;
                out[14] = str.z;

                out[15] = str.x;
                out[16] = end.y;
                out[17] = end.z;
            }
            case DOWN -> {
                out[0] = str.x;
                out[1] = str.y;
                out[2] = str.z;

                out[3] = end.x;
                out[4] = str.y;
                out[5] = str.z;

                out[6] = str.x;
                out[7] = str.y;
                out[8] = end.z;

                out[9]  = str.x;
                out[10] = str.y;
                out[11] = end.z;

                out[12] = end.x;
                out[13] = str.y;
                out[14] = str.z;

                out[15] = end.x;
                out[16] = str.y;
                out[17] = end.z;
            }
            case UP -> {
                out[0] = str.x;
                out[1] = str.y;
                out[2] = str.z;

                out[3] = str.x;
                out[4] = str.y;
                out[5] = end.z;

                out[6] = end.x;
                out[7] = str.y;
                out[8] = str.z;

                out[9]  = end.x;
                out[10] = str.y;
                out[11] = str.z;

                out[12] = str.x;
                out[13] = str.y;
                out[14] = end.z;

                out[15] = end.x;
                out[16] = str.y;
                out[17] = end.z;
            }
            case NORTH -> {
                out[0] = end.x;
                out[1] = str.y;
                out[2] = str.z;

                out[3] = str.x;
                out[4] = str.y;
                out[5] = str.z;

                out[6] = str.x;
                out[7] = end.y;
                out[8] = str.z;

                out[9]  = end.x;
                out[10] = str.y;
                out[11] = str.z;

                out[12] = str.x;
                out[13] = end.y;
                out[14] = str.z;

                out[15] = end.x;
                out[16] = end.y;
                out[17] = str.z;
            }
            case SOUTH -> {
                out[0] = str.x;
                out[1] = str.y;
                out[2] = str.z;

                out[3] = end.x;
                out[4] = str.y;
                out[5] = str.z;

                out[6] = str.x;
                out[7] = end.y;
                out[8] = str.z;

                out[9]  = str.x;
                out[10] = end.y;
                out[11] = str.z;

                out[12] = end.x;
                out[13] = str.y;
                out[14] = str.z;

                out[15] = end.x;
                out[16] = end.y;
                out[17] = str.z;
            }
        }
    }
}
