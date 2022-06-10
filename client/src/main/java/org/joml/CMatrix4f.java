package org.joml;

public class CMatrix4f extends Matrix4f {
    private static final float E = 1E-6f;

    /**
     * Set this matrix to be a symmetric perspective projection frustum transformation for a
     * right-handed coordinate system using the given NDC reversed z range.
     * <p>
     * In order to apply the perspective projection transformation to an existing transformation,
     * use {@link #perspective(float, float, float, float, boolean) perspective()}.
     *
     * @see #perspective(float, float, float, float, boolean)
     *
     * @param fovy
     *            the vertical field of view in radians (must be greater than zero and less than {@link Math#PI PI})
     * @param aspect
     *            the aspect ratio (i.e. width / height; must be greater than zero)
     * @param zNear
     *            near clipping plane distance. This value must be greater than zero.
     *            If the special value {@link Float#POSITIVE_INFINITY} is used, the near clipping plane will be at positive infinity.
     *            In that case, <code>zFar</code> may not also be {@link Float#POSITIVE_INFINITY}.
     * @param zFar
     *            far clipping plane distance. This value must be greater than zero.
     *            If the special value {@link Float#POSITIVE_INFINITY} is used, the far clipping plane will be at positive infinity.
     *            In that case, <code>zNear</code> may not also be {@link Float#POSITIVE_INFINITY}.
     * @return this
     */
    public Matrix4f setReversedZPerspective(float fovy, float aspect, float zNear, float zFar) {
        MemUtil.INSTANCE.zero(this);
        float h = Math.tan(fovy * 0.5f);
        this._m00(1.0f / (h * aspect))
                ._m11(1.0f / h);

        boolean farInf = zFar > 0 && Float.isInfinite(zFar);
        boolean nearInf = zNear > 0 && Float.isInfinite(zNear);

        if (farInf) {
            // See: "Infinite Projection Matrix" (http://www.terathon.com/gdc07_lengyel.pdf)
            // See: "Depth Precision Visualized" (https://developer.nvidia.com/content/depth-precision-visualized)
            this
                    ._m22(0.0f - E)
                    ._m32((1.0f - E) * zNear);
        } else if (nearInf) {
            this
                    ._m22(E - 1.0f)
                    ._m32((E - (1.0f)) * zFar);
        } else {
            this
                    ._m22(zNear / (zFar - zNear))
                    ._m32((zNear * zFar) / (zFar  - zNear));
        }

        return this
                ._m23(-1.0f)
                ._properties(PROPERTY_PERSPECTIVE);
    }

}
