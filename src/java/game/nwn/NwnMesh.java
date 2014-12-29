
package game.nwn;

import game.Context;
import game.models.AnimMesh;
import game.nwn.readers.MdlAnimation;
import game.nwn.readers.MdlGeometryHeader;
import game.nwn.readers.MdlModel;
import game.nwn.readers.MdlNodeHeader;

import java.util.Set;

import org.apache.log4j.Logger;

import com.google.common.collect.Sets;

public class NwnMesh {
  
  static private Logger logger = Logger.getLogger(NwnMesh.class);
  
  Context context;
  MdlModel mdl;
    
  interface Visitor {
    void preVisit(MdlNodeHeader node, MdlNodeHeader fromNode, double alpha);
    void postVisit(MdlNodeHeader node, MdlNodeHeader fromNode, double alpha);
  }

  public NwnMesh(Context context, MdlModel mdl, int x) {
    this.context = context;
    this.mdl = mdl;
  }

  private void visit(MdlNodeHeader geometry, MdlNodeHeader fromGeom, double alpha, Visitor visitor) {
    visitor.preVisit(geometry, fromGeom, alpha);
    if ( fromGeom != null ) {
      MdlNodeHeader[] c1 = geometry.getChildren();
      MdlNodeHeader[] c2 = fromGeom.getChildren();
      for(int i=0;i<c1.length;++i) {
        boolean found = false;
        for(int j=0;j<c2.length;++j) {
          if (c1[i].getName().equals(c2[j].getName())) {
            visit(c1[i], c2[j], alpha, visitor);
            found = true;
            break;
          }
        }
        if ( ! found ) {
          visit(c1[i], null, alpha, visitor);
        }
      }
    }
    else {
      MdlNodeHeader[] c1 = geometry.getChildren();
      for(int i=0;i<c1.length;++i) {
        visit(c1[i], null, alpha, visitor);
      }
    }
    visitor.postVisit(geometry, fromGeom, alpha);
  }

  public AnimMesh getAnimMesh() {
    AnimMesh animMesh = new AnimMesh();
    double alpha = 0.0;
    AnimMeshPlaneCollector planeCollector = new AnimMeshPlaneCollector(context, animMesh, null);
    visit(mdl.getGeometryHeader().getGeometry(), null, alpha, planeCollector);
    for(MdlAnimation anim: mdl.getAnimMap().values()) {
      String animName = anim.getGeometryHeader().getName();
      animMesh.setAnimationTiming(animName, anim.getLength());
      planeCollector = new AnimMeshPlaneCollector(context, animMesh, animName);
      visit(mdl.getGeometryHeader().getGeometry(), anim.getGeometryHeader().getGeometry(), alpha, planeCollector);
    }
    return animMesh;
  }

  public Set<Integer> getNumberOfFrames(String name) {
    MdlAnimation anim = mdl.getAnimMap().get(name);
    Set<Integer> numberOfFrames = Sets.newHashSet();
    getNumberOfFrames(anim.getGeometryHeader(), numberOfFrames);
    return numberOfFrames;
  }

  private void getNumberOfFrames(MdlGeometryHeader header, Set<Integer> frames) {
    if ( header.getGeometry() != null && header.getGeometry().getPosition() != null ) {
      frames.add(header.getGeometry().getPosition().length);
    }
    if ( header.getGeometry() != null && header.getGeometry().getOrientation() != null ) {
      frames.add(header.getGeometry().getOrientation().length);
    }
    for(MdlNodeHeader child: header.getGeometry().getChildren()) {
      if ( child.getOrientation() != null ) {
        frames.add(child.getOrientation().length);
      }
      if ( child.getPosition() != null ) {
        frames.add(child.getPosition().length);
      }
      if ( child.getGeomemtryHeader() != null ) {
        getNumberOfFrames(child.getGeomemtryHeader(), frames);
      }
    }
  }
}
