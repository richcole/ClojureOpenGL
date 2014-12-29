package game.voxel;

import game.math.Vector;
import game.proc.VertexCloud;

import org.apache.log4j.Logger;

public class OctTree {
  
  private static Logger logger = Logger.getLogger(OctTree.class);
  
  final static Vector[] OFFSETS = ShapeRenderer.CUBE_VERTEXES;
  
  public static class Node {

    double[] density;
    boolean isBoundary;
    Node[] children;

    public Node() {
    }
  }
    
  Node root;
  Vector center;
  double radius;
  double minRadius;
  double maxDepth;
  double minDepth;
  double epsilon;
  int    numberOfNodes;
  
  ShapeRenderer renderer;
  
  public OctTree(Vector center, double radius, double maxDepth, double minDepth) {
    this.center = center;
    this.radius = radius;
    this.maxDepth = maxDepth;
    this.minDepth = minDepth;
    this.minRadius = radius / Math.pow(2, maxDepth);
    this.epsilon = radius / Math.pow(2, maxDepth+1);
    this.renderer = new ShapeRenderer();
    this.numberOfNodes = 0;
  }

  public void add(DensityFunction densityFunction) {
    root = generate(null, root, densityFunction, center, radius, 0);
    logger.info("Number of nodes " + numberOfNodes);
  }
  
  private Node generate(Node parent, Node node, DensityFunction densityFunction, Vector center, double radius, double depth) {
    if ( node == null ) {
      numberOfNodes += 1;
      node = new Node();
      node.density = new double[8];
    }
    if ( ! renderer.isActive(center, radius, densityFunction) ) {
      return node;
    } else {
      renderer.updateDensity(center, radius, node.density, densityFunction);
    }
    node.isBoundary = renderer.isBoundaryCube(center, radius, node.density);
    if ( depth < maxDepth ) {
      if ( node.children != null || depth < minDepth || node.isBoundary ) {
        if ( node.children == null ) {
          node.children = new Node[OFFSETS.length];
        }
        for(int i=0; i<OFFSETS.length; ++i) {
          node.children[i] = generate(node, node.children[i], densityFunction, getChildCenter(center, radius, i), radius/2, depth+1);
        }
      }
    }
    return node;
  }
  
  void renderToVertexCloud(VertexCloud cloud, Transform transform) {
    renderToVertexCloud(cloud, root, center, radius, transform);
  }
  
  public void renderToVertexCloud(VertexCloud cloud, Node node, Vector center, double radius, Transform tr) {
    if ( node.children == null ) {
      Vector[] ps = new Vector[8];
      Vector[] vs = new Vector[12];
      int cubeIndex = renderer.getCubeIndex(center, radius, ps, node.density);
      renderer.renderTriangles(cubeIndex, ps, node.density, vs, tr, cloud, minRadius, this.center, this.radius);
    }
    else {
      for(int i=0; i<OFFSETS.length; ++i) {
        Node child = node.children[i];
        Vector childCenter = getChildCenter(center, radius, i);
        renderToVertexCloud(cloud, child, childCenter, radius/2, tr);
      }
    }
  }

  private Vector getChildCenter(Vector center, double radius, int i) {
    return center.plus(OFFSETS[i].times(radius/2));
  }

  
  int getNumberOfNodes() {
    return numberOfNodes;
  }
}
