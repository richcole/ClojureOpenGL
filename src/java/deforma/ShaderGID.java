package deforma;

import org.lwjgl.opengl.GL20;

public class ShaderGID extends GID {

    public ShaderGID(int gid) {
        super(gid);
        System.out.println("Allocated shader id " + gid);
    }

    public ShaderGID(ProgramGID program) {
        super(GL20.glCreateShader(program.gid));
        System.out.println("Allocated shader id " + gid);
    }


    @Override
    protected void release() {
        if ( gid != 0 ) {
            System.out.println("Releasing shader id " + gid);
            GL20.glDeleteShader(gid);
        }
        gid = 0;
    }
}
