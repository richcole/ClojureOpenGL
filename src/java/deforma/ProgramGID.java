package deforma;

import org.lwjgl.opengl.GL20;

public class ProgramGID extends GID {

    public ProgramGID(int gid) {
        super(gid);
        System.out.println("Allocated program id " + gid);
    }

    public ProgramGID() {
        super(GL20.glCreateProgram());
        System.out.println("Allocated program id " + gid);
    }

    @Override
    protected void release() {
        if ( gid != 0 ) {
            System.out.println("Releasing program id " + gid);
            GL20.glDeleteProgram(gid);
        }
        gid = 0;
    }
}
