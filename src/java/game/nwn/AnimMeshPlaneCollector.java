package game.nwn;

import game.Context;
import game.base.Face;
import game.math.Vector;
import game.models.AnimMesh;
import game.nwn.NwnMesh.Visitor;
import game.nwn.readers.MdlFace;
import game.nwn.readers.MdlMeshHeader;
import game.nwn.readers.MdlNodeHeader;

import java.util.Stack;

import org.apache.log4j.Logger;

public class AnimMeshPlaneCollector implements Visitor {
  
  private static Logger logger = Logger.getLogger(AnimMeshPlaneCollector.class);
  
  Context context;
  AnimMesh animMesh;
  Stack<AnimMesh.Node> nodes = new Stack<AnimMesh.Node>();
  AnimMesh.Node node;
  String animName;
  
  public AnimMeshPlaneCollector(Context context, AnimMesh animMesh, String animName) {
    this.context = context;
    this.animMesh = animMesh;
    this.animName = animName;
  }

  @Override
  public void preVisit(MdlNodeHeader mdlNode, MdlNodeHeader animNode, double alpha) {
    if ( nodes.empty() ) {
      node = animMesh.ensureRoot(mdlNode.getName());
    } else {
      node = nodes.peek().getChild(mdlNode.getName());
    }
    nodes.push(node);

    if ( mdlNode.getPosition() != null && mdlNode.getPosition().length > 0 ) {
      node.setPosition(mdlNode.getPosition()[0]);
    }
    if ( mdlNode.getOrientation() != null && mdlNode.getOrientation().length > 0 ) {
      node.setRotation(mdlNode.getOrientation()[0]);
    }
    
    if ( animNode != null ) {
      if ( animNode.getPosition() != null ) {
        node.addAnimPositions(animName, animNode.getPositionTimings(), animNode.getPosition());
      }
      if ( animNode.getOrientation() != null ) {
        node.addAnimRotations(animName, animNode.getOrientationTimings(), animNode.getOrientation());
      }
    }

    if ( animNode == null ) {
      MdlMeshHeader meshHeader = mdlNode.getMeshHeader();
      if ( meshHeader != null ) {
        node.setTextureName(meshHeader.getTextures()[0]);
        node.setDiffuse(meshHeader.getDiffuse());
        node.setSpecular(meshHeader.getSpecular());
        if ( meshHeader.getRender() == 1 ) {
          Vector[] vertexes = meshHeader.getVertices();
          Vector[] colors = meshHeader.getColors();
          for(MdlFace mdlFace: meshHeader.getFaces()) {
             int[] vertex = mdlFace.getVertex();
             Vector[][] texturePoints = meshHeader.getTexturePoints();
             Vector[] vs = new Vector[3];
             Vector[] tps = new Vector[3];
             for(int i=0;i<3;++i) {
               Vector vx = vertexes[vertex[i]];
               vs[i] = vx;
               tps[i] = texturePoints[0][vertex[i]];
             }
             node.addFace(new Face(vs, colors, mdlFace.getPlaneNormal(), tps));
          }
        }
      }
    }
  }

  @Override
  public void postVisit(MdlNodeHeader mdlNode, MdlNodeHeader animNode, double alpha) {
    nodes.pop();
  }

}
