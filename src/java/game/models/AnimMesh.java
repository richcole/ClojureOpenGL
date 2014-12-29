package game.models;

import game.base.Face;
import game.math.Matrix;
import game.math.Quaternion;
import game.math.Vector;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class AnimMesh {
  
  Node root;
  Map<String, Animation> animations = Maps.newHashMap();
  
  public static class Timing {
    double timing;
  }
  
  static public class RotationTiming extends Timing {
    Quaternion rotation;

    public RotationTiming() {
    }
    
    public RotationTiming(Quaternion rotation, float timing) {
      this.rotation = rotation;
      this.timing = timing;
    }
  }
  
  static public class PositionTiming extends Timing {
    Vector position;

    public PositionTiming() {
    }

    public PositionTiming(Vector position, float timing) {
      this.position = position;
      this.timing = timing;
    }
  }
  
  static public class Animation {
    double timing;

    Animation() {
    }
    
    public void setTiming(double timing) {
      this.timing = timing;
    }

    public double getTiming() {
      return timing;
    }
  }
  
  static public class AnimationNode {
    List<RotationTiming> rotations;
    List<PositionTiming> positions;
    
    AnimationNode() {
      rotations = Lists.newArrayList();
      positions = Lists.newArrayList();
    }

    public List<RotationTiming> getRotations() {
      return rotations;
    }

    public void setRotations(List<RotationTiming> rotations) {
      this.rotations = rotations;
    }

    public List<PositionTiming> getPositions() {
      return positions;
    }

    public void setPositions(List<PositionTiming> positions) {
      this.positions = positions;
    }
  }

  static public class Node {
    String name;
    Quaternion rotation;
    Vector position;

    List<Face> faces = Lists.newArrayList();
    Map<String, AnimationNode> animations = Maps.newHashMap();
    List<Node> children = Lists.newArrayList();
    String textureName;
    Vector diffuse;
    Vector specular;
    
    public Node() {
    }

    public Node(String name) {
      this.name = name;
    }

    public void setPosition(Vector position) {
      this.position = position;
    }

    public Quaternion getRotation() {
      return rotation;
    }

    public void setRotation(Quaternion rotation) {
      this.rotation = rotation;
    }

    public Vector getPosition() {
      return position;
    }

    public void addAnimPositions(String name, float[] positionTimings, Vector[] positions) {
      AnimationNode anim = ensureAnimation(name);
      for(int i=0;i<positions.length;++i) {
        anim.positions.add(new PositionTiming(positions[i], positionTimings[i]));
      }
    }

    public void addAnimRotations(String name, float[] rotationTimings, Quaternion[] rotations) {
      AnimationNode anim = ensureAnimation(name);
      for(int i=0;i<rotations.length;++i) {
        anim.rotations.add(new RotationTiming(rotations[i], rotationTimings[i]));
      }
    }

    private AnimationNode ensureAnimation(String name) {
      AnimationNode anim = animations.get(name);
      if ( anim == null ) {
        anim = new AnimationNode();
        animations.put(name, anim);
      }
      return anim;
    }

    public void addFace(Face face) {
      faces.add(face);
    }

    public Node getChild(String name) {
      for(Node child: children) {
        if ( child.name.equals(name) ) {
          return child;
        }
      }
      Node newChild = new Node(name);
      children.add(newChild);
      return newChild;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public List<Face> getFaces() {
      return faces;
    }

    public void setFaces(List<Face> faces) {
      this.faces = faces;
    }

    public Map<String, AnimationNode> getAnimations() {
      return animations;
    }

    public void setAnimations(Map<String, AnimationNode> animations) {
      this.animations = animations;
    }

    public List<Node> getChildren() {
      return children;
    }

    public void setChildren(List<Node> children) {
      this.children = children;
    }

    public void setTextureName(String textureName) {
      if ( textureName != null && textureName.length() > 0 && ! textureName.equals("NULL") ) {
        this.textureName = textureName;
      } else {
        this.textureName = null;
      }
    }

    public String getTextureName() {
      return textureName;
    }

    public void setDiffuse(Vector diffuse) {
      this.diffuse = diffuse;
    }

    public void setSpecular(Vector specular) {
      this.specular = specular;
    }

    public Vector getDiffuse() {
      return diffuse;
    }

    public Vector getSpecular() {
      return specular;
    }

    public Matrix getTransform() {
      Matrix tr = Matrix.IDENTITY;
      if ( position != null ) {
        tr = tr.times(Matrix.translate(position));
      }
      if ( rotation != null ) {
        tr = tr.times(rotation.toMatrix());
      }
      return tr;
    }

  }
  
  public Node ensureRoot(String name) {
    if ( root == null ) {
      root = new Node(name);
    }
    return root;
  }
 
  public void setAnimationTiming(String name, double timing) {
    Animation animation = ensureAnimation(name);
    animation.setTiming(timing);
  }

  private Animation ensureAnimation(String name) {
    Animation animation = animations.get(name);
    if ( animation == null ) {
      animation = new Animation();
      animations.put(name, animation);
    }
    return animation;
  }

  public Node getRoot() {
    return root;
  }

  public void setRoot(Node root) {
    this.root = root;
  }

  public Map<String, Animation> getAnimations() {
    return animations;
  }

  public void setAnimations(Map<String, Animation> animations) {
    this.animations = animations;
  }
  
  public Set<String> getTextures() {
    Set<String> textures = Sets.newHashSet();
    getTextures(root, textures);
    return textures;
  }

  private void getTextures(Node node, Set<String> textures) {
    String textureName = node.getTextureName();
    if ( textureName != null ) {
      textures.add(textureName);
    }
    for(Node child: node.getChildren()) {
      getTextures(child, textures);
    }
  }
  
}
