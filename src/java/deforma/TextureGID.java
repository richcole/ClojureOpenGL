package deforma;

import org.lwjgl.opengl.GL11;

public class TextureGID extends GID {

    public TextureGID(int gid) {
        super(gid);
        System.out.println("Allocated texture id " + gid);
    }

    public TextureGID() {
        super(GL11.glGenTextures());
        System.out.println("Allocated texture id " + gid);
    }

    @Override
    protected void release() {
        if ( gid != 0 ) {
            System.out.println("Releasing texture id " + gid);
            GL11.glDeleteTextures(gid);
        }
        gid = 0;
    }
}
