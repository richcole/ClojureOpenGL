package game.models;

import game.Context;
import game.base.Face;
import game.base.textures.TextureTile;
import game.math.Matrix;
import game.math.Vector;
import game.models.AnimMesh.Node;

import java.nio.FloatBuffer;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import com.google.common.collect.Lists;

public class CompressedAnimMesh {
  
  List<CNode> cNodes = Lists.newArrayList();
  AnimMesh animMesh;
  Context context;
  int faceCount = 0;
  
  public CompressedAnimMesh(Context context, AnimMesh animMesh) {
    this.context = context;
    this.animMesh = animMesh;
    collecteFaces(animMesh.getRoot(), Matrix.IDENTITY);
    for(CNode cNode: cNodes) {
      cNode.freeze();
    }
  }

  private void collecteFaces(Node node, Matrix tr) {
    tr = tr.times(node.getTransform());
    cNodes.add(new CNode(node, tr));
    
    for(Node child: node.getChildren()) {
      collecteFaces(child, tr);
    }
  }
  
  public AnimMesh getAnimMesh() {
    return animMesh;
  }

  public class CNode {
    Node node;
    Matrix tr;
    
    int size;
    int vboIdPos;
    int vboIdColor;
    int vboIdNormal;
    int vboIdTexCoords;

    FloatBuffer pos;
    FloatBuffer color;
    FloatBuffer normal;
    FloatBuffer texCoords;
    
    TextureTile texture;
    
    public CNode(Node node, Matrix tr) {
      this.node = node;
      this.tr = tr;
      if ( node.getTextureName() != null ) {
        this.texture = context.getTilingTextures().getFileTexture(node.getTextureName() + ".tga");
      }
    }
  
    private int createArray(int index, FloatBuffer buf) {
      int vboId = GL15.glGenBuffers();
      GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
      GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buf, GL15.GL_STATIC_DRAW);
      return vboId;
    }
  
    public void update(AnimMeshRenderer renderer, String animName, double alpha) {
    }

    public void freeze() {
      size = node.faces.size();
  
      if ( size > 0 ) {
        allocateBuffers();
        bindArrays();
      }
    }

    private void allocateBuffers() {
      pos = BufferUtils.createFloatBuffer(size*9);
      color = BufferUtils.createFloatBuffer(size*9);
      normal = BufferUtils.createFloatBuffer(size*9);
      texCoords = BufferUtils.createFloatBuffer(size*9);
      
      for(Face face: node.getFaces()) {
        for(int i=0;i<3;++i) {
          putVertex(pos, tr.times(face.getVertices()[i]));
          putVertex(color, face.getColors()[i]);
          putVertex(normal, tr.times(face.getNormal()));
          Vector t = face.getTexturePoints()[i];
          if ( t != null && texture != null ) {
            t.set(2, texture.getTextureZ());
            putVertex(texCoords, t);
          } else {
            putVertex(texCoords, Vector.Z);
          }
        }
      }
      
      pos.flip();
      color.flip();
      normal.flip();
      texCoords.flip();
    }

    private void putVertex(FloatBuffer buf, Vector v) {
      if ( v != null ) {
        buf.put((float)v.x());
        buf.put((float)v.y());
        buf.put((float)v.z());
      }
      else {
        buf.put(0f);
        buf.put(0f);
        buf.put(0f);
      }
    }
  
    private void bindArrays() {
      vboIdPos = createArray(0, pos);
      vboIdColor = createArray(1, color);
      vboIdNormal = createArray(2, normal);
      vboIdTexCoords = createArray(3, texCoords);
    }
       
    public void render(AnimMeshRenderer renderer) {
      faceCount += size;
      if ( size > 0 ) {
        texture.bind();
        
        GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboIdPos);
        GL11.glVertexPointer(3, GL11.GL_FLOAT, 0, 0);
  
        GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboIdColor);
        GL11.glColorPointer(3, GL11.GL_FLOAT, 0, 0);
  
        GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboIdNormal);
        GL11.glNormalPointer(GL11.GL_FLOAT, 0, 0);
  
        GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboIdTexCoords);
        GL11.glTexCoordPointer(3, GL11.GL_FLOAT, 0, 0);
  
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, size*3);
        GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
        GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
        GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
        GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
      }
    }
  }

  public void render(AnimMeshRenderer renderer) {
    for(CNode node: cNodes) {
      node.render(renderer);
    }
  }

  public void update(AnimMeshRenderer renderer, String animName, double d) {
  }
}
