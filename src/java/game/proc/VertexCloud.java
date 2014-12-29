package game.proc;

import game.Renderable;
import game.math.Vector;
import game.voxel.Transform;

import java.nio.DoubleBuffer;
import java.util.List;

import org.apache.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

public class VertexCloud implements Renderable {
  
  static private Logger logger = Logger.getLogger(VertexCloud.class);
  
  int vId = -1;
  int pIndex = 0, nIndex = 1, tIndex = 2, oIndex = 3, dxIndex = 4, dyIndex = 5;
  
  List<Vector>[]     vectors;
  int[]              ids;
  
  int pos;
  int tail;
  
  public VertexCloud() {
    vectors = (List<Vector>[])new List[6];
    for(int i=0;i<vectors.length; ++i) {
      vectors[i] = Lists.newArrayList();
    }
    ids = new int[6];
    for(int i=0; i<ids.length; ++i) {
    	ids[i] = -1;
    }
    pos = 0;
    tail = 0;
  }
  
  public int pos() {
    return pos;
  }
  
  public void seek(int pos) {
    this.pos = pos;
  }
  
  public int seekEnd() {
    this.pos = tail;
    return tail;
  }
  
  public void clear() {
    this.pos = 0;
    this.tail = 0;
    for(int i=0;i<vectors.length; ++i) {
      vectors[i] = Lists.newArrayList();
    }
  }
  
  public void addVertex(Vector p, Vector n, Vector t) {
    Vector z = Vector.Z;
    addVertex(p, n, t, z, z, z);
  }
  
  public void addVertex(Vector p, Vector n, Vector t, Vector o, Vector dx, Vector dy) {
    Preconditions.checkNotNull(p);
    Preconditions.checkNotNull(n);
    Preconditions.checkNotNull(t);
    
    if ( pos >= vectors[pIndex].size() ) {
      vectors[pIndex].add(p);
      vectors[nIndex].add(n);
      vectors[tIndex].add(t);
      vectors[oIndex].add(o);
      vectors[dxIndex].add(dx);
      vectors[dyIndex].add(dy);
    } else {
      vectors[pIndex].set(pos, p);
      vectors[nIndex].add(pos, n);
      vectors[tIndex].add(pos, t);
      vectors[oIndex].add(pos, o);
      vectors[dxIndex].add(pos, dx);
      vectors[dyIndex].add(pos, dy);
    }
    pos += 1;
    tail += 1;
  }
  
  public void freeze() {
    if ( vId == -1 ) {
      vId = GL30.glGenVertexArrays();
    }
    GL30.glBindVertexArray(vId);
    
    for(int i=0;i<vectors.length; ++i) {
      ids[i] = setVertexData(i, ids[i], vectors[i], tail); 
    }
    
    GL30.glBindVertexArray(0);
    
    logger.info("Number of points: " + tail);
  }

  private int setVertexData(int index, int id, List<Vector> vs, int expectedLen) {
    if ( vs.size() != expectedLen ) {
      throw new RuntimeException("expected different length");
    }
    if ( id == -1 ) {
    	id = GL15.glGenBuffers();
    }
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, id);
    GL15.glBufferData(GL15.GL_ARRAY_BUFFER, createBuf(vs), GL15.GL_STATIC_DRAW);
    GL20.glVertexAttribPointer(index, 3, GL11.GL_DOUBLE, false, 0, 0);
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    return id;
  }

  private DoubleBuffer createBuf(List<Vector> vs) {
    DoubleBuffer buf = BufferUtils.createDoubleBuffer(vs.size()*3);
    for(Vector v: vs) {
      buf.put(v.x());
      buf.put(v.y());
      buf.put(v.z());
    }
    buf.flip();
    return buf;
  }
  
  public void render() {
    GL30.glBindVertexArray(vId);
    for(int i=0;i<vectors.length; ++i) {
      GL20.glEnableVertexAttribArray(i);
    }
    
    GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, tail);

    for(int i=0;i<vectors.length; ++i) {
      GL20.glDisableVertexAttribArray(i);
    }
  }

  public void computeNormals() {
    for(int i=0;i<tail; i+=3) {
      Vector p1 = vectors[pIndex].get(i);
      Vector p2 = vectors[pIndex].get(i+1);
      Vector p3 = vectors[pIndex].get(i+2);
      Vector n = p2.minus(p1).cross(p3.minus(p1)).normalize();
      vectors[nIndex].set(i, n);
      vectors[nIndex].set(i+1, n);
      vectors[nIndex].set(i+2, n);
    }
    
  }

  public void free(int pos, int end) {
    for(int i=pos; i<end; ++i) {
      swap(i, tail);
      tail -= 1;
    }
  }

  private void swap(int i, int j) {
    for(int k=0;i<vectors.length; ++i) {
      Vector tmp = vectors[k].get(i);
      vectors[k].set(i, vectors[k].get(j));
      vectors[k].set(j, tmp);
    }
  }

  public void addTriangle(Vector p1, Vector p2, Vector p3, Transform tr) {
    Vector n = p2.minus(p1).cross(p3.minus(p1)).normalize();
    addVertex(tr.transform(p1), tr.transformNormal(p1, n), Vector.Z);
    addVertex(tr.transform(p2), tr.transformNormal(p2, n), Vector.Z);
    addVertex(tr.transform(p3), tr.transformNormal(p3, n), Vector.Z);
  }
}
