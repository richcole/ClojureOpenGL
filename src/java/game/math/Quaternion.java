package game.math;

public class Quaternion extends Vector {
  
  public final static Quaternion ZERO = new Quaternion(0, 0, 0, 1);

  public Quaternion() {
    super();
  }

  public Quaternion(double x1, double x2, double x3, double x4) {
    super(x1, x2, x3, x4);
  }

  public Quaternion(Vector o) {
    super(o);
  }
  
  public Quaternion(double[] v) {
    this.v = v;
  }

  public Matrix toMatrix() {
    double qx = v[0];
    double qy = v[1];
    double qz = v[2];
    double qw = v[3];
    
    return new Matrix(
      1.0f - 2.0f*qy*qy - 2.0f*qz*qz, 2.0f*qx*qy - 2.0f*qz*qw, 2.0f*qx*qz + 2.0f*qy*qw, 0.0f,
      2.0f*qx*qy + 2.0f*qz*qw, 1.0f - 2.0f*qx*qx - 2.0f*qz*qz, 2.0f*qy*qz - 2.0f*qx*qw, 0.0f,
      2.0f*qx*qz - 2.0f*qy*qw, 2.0f*qy*qz + 2.0f*qx*qw, 1.0f - 2.0f*qx*qx - 2.0f*qy*qy, 0.0f,
      0.0f, 0.0f, 0.0f, 1.0f
    );
  }

  public Quaternion times(double s) {
    return new Quaternion(v[0]*s, v[1]*s, v[2]*s, v[3]*s);
  }

  public Quaternion plus(Quaternion o) {
    return new Quaternion(v[0] + o.v[0], v[1] + o.v[1], v[2] + o.v[2], v[3] + o.v[3]);
  }

  public Quaternion minus(Quaternion o) {
    return new Quaternion(v[0] - o.v[0], v[1] - o.v[1], v[2] - o.v[2], v[3] - o.v[3]);
  }
  
  public Vector times(Vector u) {
    double vx = u.x(), vy = u.y(), vz = u.z();
    double x = v[0], y = v[1], z = v[2], w = v[3];
    Vector r = new Vector();
    r.v[0] = w * w * vx + 2 * y * w * vz - 2 * z * w * vy + x * x
            * vx + 2 * y * x * vy + 2 * z * x * vz - z * z * vx - y
            * y * vx;
    r.v[1] = 2 * x * y * vx + y * y * vy + 2 * z * y * vz + 2 * w
            * z * vx - z * z * vy + w * w * vy - 2 * x * w * vz - x
            * x * vy;
    r.v[2] = 2 * x * z * vx + 2 * y * z * vy + z * z * vz - 2 * w
            * y * vx - y * y * vz + 2 * w * x * vy - x * x * vz + w
            * w * vz;
    r.v[3] = 1.0;
    return r;
  }

  public Quaternion conjugate() {
    double l = length();
    Quaternion r = new Quaternion(-v[0] / l, -v[1] / l, -v[2] / l, v[3] / l);
    return r;
  }
  
  public double theta() {
    return Math.acos(v[3]);
  }
  
  public Quaternion power(double p) {
    double theta = theta();
    double ctheta = Math.cos(p*theta);
    double stheta = Math.sin(p*theta);
    return new Quaternion(v[0]*stheta, v[1]*stheta, v[2]*stheta, ctheta);
  }
  
  public Quaternion times(Quaternion o) {
    double w1 = v[3];
    double x1 = v[0];
    double y1 = v[1];
    double z1 = v[2];
    double w2 = o.v[3];
    double x2 = o.v[0];
    double y2 = o.v[1];
    double z2 = o.v[2];
    Quaternion r = new Quaternion(
      w1*x2 + x1*w2 + y1*z2 - z1*y2,
      w1*y2 - x1*z2 + y1*w2 + z1*x2,
      w1*z2 + x1*y2 - y1*x2 + z1*w2,
      w1*w2 - x1*x2 - y1*y2 - z1*z2
    );
    return r;
  }
  
  public double length() {
    return Math.sqrt(lengthSquared());
  }
  
  public double lengthSquared() {
    return v[0]*v[0] + v[1]*v[1] + v[2]*v[2] + v[3]*v[3];
  }

  public double dot(Quaternion o) {
    return v[0]*o.v[0] + v[1]*o.v[1] + v[2]*o.v[2] + v[3]*o.v[3];
  }
  
  public Quaternion scaleTo(double length) {
    double l = length;
    return new Quaternion(v[0]/l, v[1]/l, v[2]/l, v[3]);
  }
  
  public Quaternion fromAngles(double xAngle, double yAngle, double zAngle) {
    Quaternion n = new Quaternion();

    double angle;
    double sinY, sinZ, sinX, cosY, cosZ, cosX;
    angle = zAngle * 0.5f;
    sinZ = Math.sin(angle);
    cosZ = Math.cos(angle);
    angle = yAngle * 0.5f;
    sinY = Math.sin(angle);
    cosY = Math.cos(angle);
    angle = xAngle * 0.5f;
    sinX = Math.sin(angle);
    cosX = Math.cos(angle);

    // variables used to reduce multiplication calls.
    double cosYXcosZ = cosY * cosZ;
    double sinYXsinZ = sinY * sinZ;
    double cosYXsinZ = cosY * sinZ;
    double sinYXcosZ = sinY * cosZ;

    n.v[0] = (cosYXcosZ * sinX + sinYXsinZ * cosX);
    n.v[1] = (sinYXcosZ * cosX + cosYXsinZ * sinX);
    n.v[2] = (cosYXsinZ * cosX - sinYXcosZ * sinX);

    n.v[3] = (cosYXcosZ * cosX - sinYXsinZ * sinX);
    
    return n.scaleTo(1.0);
  }
}
