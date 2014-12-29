package game.voxel;

import org.apache.log4j.Logger;

import game.Context;
import game.Registerable;
import game.Renderable;
import game.math.Vector;
import game.proc.VertexCloud;
import game.shaders.ProgramRenderer;

public class OctTreeTerrain implements Renderable, Registerable {
  
  static private Logger logger = Logger.getLogger(OctTreeTerrain.class);

  private Context context;
  private double radius;
  private Vector center;
  private OctTree octTree;
  private VertexCloud cloud;
  private ProgramRenderer program;
  private ScaleTransform transform;

  public OctTreeTerrain(Context context) {
    super();
    this.context = context;
    this.radius = 1000;
    this.center = Vector.ONES.times(radius);
    this.octTree = new OctTree(center, radius, 5, 3);
    
    // this.octTree.add(new PerlinNoiseField(radius*2));
    this.octTree.add(new PotentialField(center, radius/5));
    this.cloud = new VertexCloud();
    this.transform = new ScaleTransform(400/radius);

    octTree.renderToVertexCloud(cloud, transform);
    cloud.computeNormals();
    cloud.freeze();

    this.program = new ProgramRenderer(context, cloud, "screen"); 
  }
  
  public void renderSphereAt(Vector center, double radius) {
    center = transform.invTransform(center);
    radius = transform.invTransform(radius);
    PotentialField p = new PotentialField(center, radius);
    octTree.add(p);

    cloud.clear();
    octTree.renderToVertexCloud(cloud, transform);
    cloud.computeNormals();
    cloud.freeze();
  }
  
  public void register() {
    context.getScene().register(this);
  }

  @Override
  public void render() {
    program.render();
  }

  
}
