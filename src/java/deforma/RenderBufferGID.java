package deforma;

import org.lwjgl.opengl.GL30;

public class RenderBufferGID extends GID {

    public RenderBufferGID(int gid) {
        super(gid);
        System.out.println("Allocated renderbuffer id " + gid);
    }

    public RenderBufferGID() {
        super(GL30.glGenRenderbuffers());
        System.out.println("Allocated renderbuffer id " + gid);
    }

    @Override
    protected void release() {
        if ( gid != 0 ) {
            System.out.println("Releasing renderbuffer id " + gid);
            GL30.glDeleteRenderbuffers(gid);
        }
        gid = 0;
    }
}
