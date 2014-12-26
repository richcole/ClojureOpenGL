package deforma;

import org.lwjgl.opengl.GL15;

public class BufferGID extends GID {

    public BufferGID(int gid) {
        super(gid);
        System.out.println("Allocated buffer id " + gid);
    }

    public BufferGID() {
        super(GL15.glGenBuffers());
        System.out.println("Allocated buffer id " + gid);
    }

    @Override
    protected void release() {
        if ( gid != 0 ) {
            System.out.println("Releasing buffer id " + gid);
            GL15.glDeleteBuffers(gid);
        }
        gid = 0;
    }
}
