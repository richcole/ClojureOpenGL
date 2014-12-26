package deforma;

public abstract class GID {

    int gid;

    public GID(Integer gid) {
        this.gid = gid;
    }

    public int getGid() {
        return gid;
    }

    abstract protected void release();

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        release();
    }
}
