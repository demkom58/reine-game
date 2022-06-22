package com.crown.graphic.gl;

import com.crown.graphic.util.Destroyable;

public abstract class GlObject implements Destroyable {
    private static final int INVALID_HANDLE = Integer.MIN_VALUE;

    private int handle = INVALID_HANDLE;

    public final int getHandle() {
        return handle;
    }

    protected void invalidateHandle() {
        this.handle = INVALID_HANDLE;
    }

    protected final void setHandle(int handle) {
        this.handle = handle;
    }

    protected boolean isHandleInvalid() {
        return handle == INVALID_HANDLE;
    }

    protected void checkHandle() {
        if (handle == INVALID_HANDLE) {
            throw new OpenGlDeviceException("Invalid object handle");
        }
    }

}
