package com.crown.resource.image.filter;

import com.crown.resource.image.GenericImageData;
import com.crown.util.PixelFormat;

import java.util.ArrayList;
import java.util.List;

public final class ImageSampler {
    static class Weight {
        int pixel;
        double weight;

        public Weight(int pixel, double weight) {
            this.pixel = pixel;
            this.weight = weight;
        }
    }

    static class WeightList {
        int n;
        Weight[] p;
    }

    public static GenericImageData sample(Kernel kernel, GenericImageData data, int newWidth, int newHeight) {
        final PixelFormat format = data.format();
        final GenericImageData output = GenericImageData.alloc(newWidth, newHeight, format);

        sample(kernel, data, output);

        return output;
    }

    public static void sample(Kernel kernel, GenericImageData input, GenericImageData output) {
        try (GenericImageData work = GenericImageData.alloc(1, input.height(), input.format())) {
            final PixelFormat format = input.format();

            final int inWidth = input.width(), inHeight = input.height();
            final int outWidth = output.width(), outHeight = output.height();

            final double xScale = (double) outWidth / (double) inWidth;
            final double yScale = (double) outHeight / (double) inHeight;

            final WeightList[] contributorsY = new WeightList[outHeight];
            for (int i = 0; i < contributorsY.length; i++) {
                contributorsY[i] = new WeightList();
            }

            final double support = kernel.support();
            if (yScale < 1.0) {
                double width = support / yScale;
                double fScale = 1.0 / yScale;

                if (width <= .5) {
                    width = .5 + 1.0e-6;
                    fScale = 1.0;
                }

                for (int i = 0; i < outHeight; i++) {
                    contributorsY[i].p = new Weight[(int) (width * 2.0 + 1 + 0.5)];

                    final double center = (double) i / yScale;
                    final int start = (int) Math.ceil(center - width);
                    final int end = (int) Math.floor(center + width);

                    double density = 0.0;
                    for (int y = start; y <= end; y++) {
                        double weight = center - (double) y;
                        weight = kernel.filter(weight / fScale) / fScale;

                        int n;
                        if (y < 0) {
                            n = -y;
                        } else if (y >= inHeight) {
                            n = (inHeight - y) + inHeight - 1;
                        } else {
                            n = y;
                        }

                        if (n >= inHeight) {
                            n = n % inHeight;
                        } else if (n < 0) {
                            n = inHeight - 1;
                        }

                        int k = contributorsY[i].n++;
                        contributorsY[i].p[k] = new Weight(n, weight);
                        density += weight;
                    }

                    // Normalize
                    if ((density != 0.0) && (density != 1.0)) {
                        density = 1.0 / density;
                        for (int k = 0; k < contributorsY[i].n; k++) {
                            contributorsY[i].p[k].weight *= density;
                        }
                    }
                }
            } else {
                for (int i = 0; i < outHeight; ++i) {
                    contributorsY[i].p = new Weight[(int) (support * 2 + 1 + 0.5)];

                    final double center = (double) i / yScale;
                    final double start = Math.ceil(center - support);
                    final double end = Math.floor(center + support);

                    for (int y = (int) start; y <= end; ++y) {
                        double weight = center - (double) y;
                        weight = kernel.filter(weight);

                        int n;
                        if (y < 0) {
                            n = -y;
                        } else if (y >= inHeight) {
                            n = (inHeight - y) + inHeight - 1;
                        } else {
                            n = y;
                        }

                        if (n >= inHeight) {
                            n = n % inHeight;
                        } else if (n < 0) {
                            n = inHeight - 1;
                        }

                        int k = contributorsY[i].n++;
                        contributorsY[i].p[k] = new Weight(n, weight);
                    }
                }
            }

            for (int xx = 0; xx < outWidth; xx++) {
                List<Weight> contribX = calcXContrib(xScale, support, inWidth, kernel, xx);

                // Apply horizontal filter to make dst column in tmp.
                for (int y = 0; y < inHeight; y++) {
                    for (int channel = 0; channel < format.channels; channel++) {
                        double weight = 0.0;
                        boolean bPelDelta = false;

                        double pel = getChannel(input, contribX.get(0).pixel, y, channel);
                        for (int x = 0; x < contribX.size(); x++) {
                            double pel2 = x == 0 ? pel : getChannel(input, contribX.get(x).pixel, y, channel);
                            if (pel2 != pel) {
                                bPelDelta = true;
                            }
                            weight += pel2 * contribX.get(x).weight;
                        }
                        weight = bPelDelta ? round(weight) : pel;

                        if (weight < 0) {
                            weight = 0;
                        } else if (weight > format.channelMax) {
                            weight = format.channelMax;
                        }

                        setChannel(work, 0, y, channel, (int) weight);
                    }
                }

                // The temp column has been built. Now stretch it vertically into dst column.
                for (int y = 0; y < outHeight; y++) {
                    for (int channel = 0; channel < format.channels; channel++) {

                        double weight = 0.0;
                        boolean bPelDelta = false;
                        double pel = getChannel(work, 0, contributorsY[y].p[0].pixel, channel);

                        for (int j = 0; j < contributorsY[y].n; j++) {
                            double pel2 = j == 0 ? pel : getChannel(work, 0, contributorsY[y].p[j].pixel, channel);
                            if (pel2 != pel) {
                                bPelDelta = true;
                            }
                            weight += pel2 * contributorsY[y].p[j].weight;
                        }
                        weight = bPelDelta ? round(weight) : pel;
                        if (weight < 0) {
                            weight = 0;
                        } else if (weight > format.channelMax) {
                            weight = format.channelMax;
                        }

                        setChannel(output, xx, y, channel, (int) weight);
                    }
                }
            }
        }
    }


    /**
     * Calculates the filter weights for a single target column.
     */
    private static List<Weight> calcXContrib(double xscale, double fwidth, int srcwidth, Kernel pFilter, int i) {
        double width;
        double fscale;
        double center;
        double weight;

        List<Weight> contribX;

        if (xscale < 1.0) {
            // Shrinking image
            width = fwidth / xscale;
            fscale = 1.0 / xscale;

            if (width <= .5) {
                // Reduce to point sampling.
                width = .5 + 1.0e-6;
                fscale = 1.0;
            }

            contribX = new ArrayList<>((int) (width * 2.0 + 1.0 + 0.5));

            center = (double) i / xscale;
            int left = (int) Math.ceil(center - width); // Note: Assumes width <= .5
            int right = (int) Math.floor(center + width);

            double density = 0.0;

            for (int j = left; j <= right; j++) {
                weight = center - (double) j;
                weight = pFilter.filter(weight / fscale) / fscale;
                int n;
                if (j < 0) {
                    n = -j;
                } else if (j >= srcwidth) {
                    n = (srcwidth - j) + srcwidth - 1;
                } else {
                    n = j;
                }

                if (n >= srcwidth) {
                    n = n % srcwidth;
                } else if (n < 0) {
                    n = srcwidth - 1;
                }

                contribX.add(new Weight(n, weight));
                density += weight;

            }

            if ((density != 0.0) && (density != 1.0)) {
                // Normalize.
                density = 1.0 / density;
                for (Weight x : contribX) {
                    x.weight *= density;
                }
            }
        } else {
            // Expanding image
            contribX = new ArrayList<>((int) (fwidth * 2.0 + 1.0 + 0.5));

            center = (double) i / xscale;
            int left = (int) Math.ceil(center - fwidth);
            int right = (int) Math.floor(center + fwidth);

            for (int j = left; j <= right; j++) {
                weight = center - (double) j;
                weight = pFilter.filter(weight);

                int n;
                if (j < 0) {
                    n = -j;
                } else if (j >= srcwidth) {
                    n = (srcwidth - j) + srcwidth - 1;
                } else {
                    n = j;
                }

                if (n >= srcwidth) {
                    n = n % srcwidth;
                } else if (n < 0) {
                    n = srcwidth - 1;
                }

                contribX.add(new Weight(n, weight));
            }
        }

        return contribX;
    }

    static int getChannel(GenericImageData in, int x, int y, int channel) {
        PixelFormat format = in.format();
        return (in.getRGBA(x, y) >> (format.channelBits * channel)) & format.channelMax;
    }

    static void setChannel(GenericImageData in, int x, int y, int channel, int value) {
        PixelFormat format = in.format();
        int offset = format.channelBits * channel;

        value <<= offset;
        int cleaner = ~((format.channelMax) << offset);

        in.setRGBA(x, y, (in.getRGBA(x, y) & cleaner) | value);
    }

    static int round(double d) {
        // NOTE: This code seems to be faster than Math.round(double)...
        int n = (int) d;
        double diff = d - (double) n;
        if (diff < 0) {
            diff = -diff;
        }
        if (diff >= 0.5) {
            if (d < 0) {
                n--;
            } else {
                n++;
            }
        }
        return n;
    }
}
