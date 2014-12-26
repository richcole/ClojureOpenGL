package deforma;

import org.lwjgl.opengl.GL30;

public class FrameBufferGID extends GID {

    public FrameBufferGID(int gid) {
        super(gid);
        System.out.println("Allocated framebuffer id " + gid);
    }

    public FrameBufferGID() {
        super(GL30.glGenFramebuffers());
        System.out.println("Allocated framebuffer id " + gid);
    }

    @Override
    protected void release() {
        if ( gid != 0 ) {
            System.out.println("Releasing framebuffer id " + gid);
            GL30.glDeleteFramebuffers(gid);
        }
        gid = 0;
    }
}
