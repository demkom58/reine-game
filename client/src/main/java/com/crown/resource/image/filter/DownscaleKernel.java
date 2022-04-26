package com.crown.resource.image.filter;

public enum DownscaleKernel implements Kernel {
    POINT(0.5) {
        @Override
        public double filter(double t) {
            if ((t >= -0.5) && (t < 0.5)) {
                return 1.0;
            }
            return 0.0;
        }
    },
    BOX(0.5) {
        @Override
        public double filter(double t) {
            if ((t >= -0.5) && (t < 0.5)) {
                return 1.0;
            }
            return 0.0;
        }
    },
    TRIANGLE(1.0) {
        @Override
        public double filter(double t) {
            if (t < 0.0) {
                t = -t;
            }
            if (t < 1.0) {
                return 1.0 - t;
            }
            return 0.0;
        }
    },
    HERMITE(1.0) {
        @Override
        public double filter(double t) {
            /* f(t) = 2|t|^3 - 3|t|^2 + 1, -1 <= t <= 1 */
            if (t < 0.0) {
                t = -t;
            }
            if (t < 1.0) {
                return (2.0 * t - 3.0) * t * t + 1.0;
            }
            return 0.0;
        }
    },
    HANNING(1.0) {
        @Override
        public double filter(double t) {
            return 0.5 + 0.5 * Math.cos(Math.PI * t);
        }
    },
    HAMMING(1.0) {
        @Override
        public double filter(double t) {
            return 0.54 + 0.46 * Math.cos(Math.PI * t);
        }
    },
    BLACKMAN(1.0) {
        @Override
        public double filter(double t) {
            return blackman(t);
        }
    },
    GAUSSIAN(1.25) {
        @Override
        public double filter(double t) {
            return Math.exp(-2.0 * t * t) * Math.sqrt(2.0 / Math.PI);
        }
    },
    QUADRATIC(1.5) {
        @Override
        public double filter(double t) {
            if (t < 0) {
                t = -t;
            }
            if (t < .5) {
                return .75 - (t * t);
            }
            if (t < 1.5) {
                t = (t - 1.5);
                return .5 * (t * t);
            }
            return 0.0;
        }
    },
    CUBIC(2.0) {
        @Override
        public double filter(double t) {
            final double tt;

            if (t < 0) {
                t = -t;
            }
            if (t < 1) {
                tt = t * t;
                return (.5 * tt * t) - tt + (2.0 / 3.0);
            } else if (t < 2) {
                t = 2 - t;
                return (1.0 / 6.0) * (t * t * t);
            }
            return 0.0;
        }
    },
    CATROM(2.0) {
        @Override
        public double filter(double t) {
            if (t < 0) {
                t = -t;
            }
            if (t < 1.0) {
                return 0.5 * (2.0 + t * t * (-5.0 + t * 3.0));
            }
            if (t < 2.0) {
                return 0.5 * (4.0 + t * (-8.0 + t * (5.0 - t)));
            }
            return 0.0;
        }
    },
    MITCHELL(2.0) {
        @Override
        public double filter(double t) {
            if (t < -2.0) {
                return 0.0;
            }
            if (t < -1.0) {
                return Q0 - t * (Q1 - t * (Q2 - t * Q3));
            }
            if (t < 0.0) {
                return P0 + t * t * (P2 - t * P3);
            }
            if (t < 1.0) {
                return P0 + t * t * (P2 + t * P3);
            }
            if (t < 2.0) {
                return Q0 + t * (Q1 + t * (Q2 + t * Q3));
            }
            return 0.0;
        }
    },
    LANCZOS(3.0) {
        @Override
        public double filter(double t) {
            if (t < 0) {
                t = -t;
            }
            if (t < 3.0) {
                return sinc(t) * sinc(t / 3.0);
            }
            return 0.0;
        }
    },
    BLACKMAN_BESSEL(3.2383) {
        @Override
        public double filter(double t) {
            return blackman(t / support()) * bessel(t);
        }
    },
    BLACKMAN_SINC(4.0) {
        @Override
        public double filter(double t) {
            return blackman(t / support()) * sinc(t);
        }
    },
    ;

    private final static double B = 1.0 / 3.0;
    private final static double C = 1.0 / 3.0;
    private final static double P0 = (6.0 - 2.0 * B) / 6.0;
    private final static double P2 = (-18.0 + 12.0 * B + 6.0 * C) / 6.0;
    private final static double P3 = (12.0 - 9.0 * B - 6.0 * C) / 6.0;
    private final static double Q0 = (8.0 * B + 24.0 * C) / 6.0;
    private final static double Q1 = (-12.0 * B - 48.0 * C) / 6.0;
    private final static double Q2 = (6.0 * B + 30.0 * C) / 6.0;
    private final static double Q3 = (-1.0 * B - 6.0 * C) / 6.0;

    private static double sinc(double x) {
        x *= Math.PI;
        if (x != 0.0) {
            return Math.sin(x) / x;
        }
        return 1.0;
    }

    private static double j1(final double t) {
        final double[] pOne = {
                0.581199354001606143928050809e+21,
                -0.6672106568924916298020941484e+20,
                0.2316433580634002297931815435e+19,
                -0.3588817569910106050743641413e+17,
                0.2908795263834775409737601689e+15,
                -0.1322983480332126453125473247e+13,
                0.3413234182301700539091292655e+10,
                -0.4695753530642995859767162166e+7,
                0.270112271089232341485679099e+4
        };
        final double[] qOne = {
                0.11623987080032122878585294e+22,
                0.1185770712190320999837113348e+20,
                0.6092061398917521746105196863e+17,
                0.2081661221307607351240184229e+15,
                0.5243710262167649715406728642e+12,
                0.1013863514358673989967045588e+10,
                0.1501793594998585505921097578e+7,
                0.1606931573481487801970916749e+4,
                0.1e+1
        };

        double p = pOne[8];
        double q = qOne[8];
        for (int i = 7; i >= 0; i--) {
            p = p * t * t + pOne[i];
            q = q * t * t + qOne[i];
        }
        return p / q;
    }

    private static double p1(final double t) {
        final double[] pOne = {
                0.352246649133679798341724373e+5,
                0.62758845247161281269005675e+5,
                0.313539631109159574238669888e+5,
                0.49854832060594338434500455e+4,
                0.2111529182853962382105718e+3,
                0.12571716929145341558495e+1
        };
        final double[] qOne = {
                0.352246649133679798068390431e+5,
                0.626943469593560511888833731e+5,
                0.312404063819041039923015703e+5,
                0.4930396490181088979386097e+4,
                0.2030775189134759322293574e+3,
                0.1e+1
        };

        double p = pOne[5];
        double q = qOne[5];
        for (int i = 4; i >= 0; i--) {
            p = p * (8.0 / t) * (8.0 / t) + pOne[i];
            q = q * (8.0 / t) * (8.0 / t) + qOne[i];
        }
        return p / q;
    }

    private static double q1(final double t) {
        final double[] pOne = {
                0.3511751914303552822533318e+3,
                0.7210391804904475039280863e+3,
                0.4259873011654442389886993e+3,
                0.831898957673850827325226e+2,
                0.45681716295512267064405e+1,
                0.3532840052740123642735e-1
        };
        final double[] qOne = {
                0.74917374171809127714519505e+4,
                0.154141773392650970499848051e+5,
                0.91522317015169922705904727e+4,
                0.18111867005523513506724158e+4,
                0.1038187585462133728776636e+3,
                0.1e+1
        };

        double p = pOne[5];
        double q = qOne[5];
        for (int i = 4; i >= 0; i--) {
            p = p * (8.0 / t) * (8.0 / t) + pOne[i];
            q = q * (8.0 / t) * (8.0 / t) + qOne[i];
        }
        return p / q;
    }

    static double besselOrderOne(double t) {
        double p, q;

        if (t == 0.0) {
            return 0.0;
        }
        p = t;
        if (t < 0.0) {
            t = -t;
        }
        if (t < 8.0) {
            return p * j1(t);
        }
        q = Math.sqrt(2.0 / (Math.PI * t)) * (p1(t) * (1.0 / Math.sqrt(2.0) * (Math.sin(t) - Math.cos(t))) - 8.0 / t * q1(t) *
                (-1.0 / Math.sqrt(2.0) * (Math.sin(t) + Math.cos(t))));
        if (p < 0.0) {
            q = -q;
        }
        return q;
    }

    private static double bessel(final double t) {
        if (t == 0.0) {
            return Math.PI / 4.0;
        }
        return besselOrderOne(Math.PI * t) / (2.0 * t);
    }

    private static double blackman(final double t) {
        return 0.42 + 0.50 * Math.cos(Math.PI * t) + 0.08 * Math.cos(2.0 * Math.PI * t);
    }

    private final double minSize;

    DownscaleKernel(double minSize) {
        this.minSize = minSize;
    }

    @Override
    public double support() {
        return minSize;
    }

    @Override
    public double filter(double t) {
        throw new UnsupportedOperationException();
    }

}
