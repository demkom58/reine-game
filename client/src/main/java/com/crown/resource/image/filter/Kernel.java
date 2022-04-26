package com.crown.resource.image.filter;

public interface Kernel {
    double support();

    double filter(double t);
}
