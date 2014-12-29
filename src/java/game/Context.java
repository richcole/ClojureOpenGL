package game;

import game.base.io.Serializer;
import game.base.textures.TextureTile;
import game.base.textures.Textures;
import game.base.textures.TilingTextures;
import game.enums.Model;
import game.main.Main;
import game.math.Matrix;
import game.math.Vector;
import game.models.BigCube;
import game.models.Creature;
import game.models.Grid;
import game.models.Light;
import game.models.Models;
import game.models.ResFiles;
import game.models.SkyBox;
import game.models.TerrainTile;
import game.models.TileSetDescriptions;
import game.nwn.readers.KeyReader;
import game.proc.GrassBox;
import game.proc.GrassSquare;
import game.proc.HeightMap;
import game.proc.Perlin2;
import game.proc.VertexCloud;
import game.shaders.ProgramRenderer;
import game.shaders.ShaderArray;
import game.shaders.ShaderMatrix;
import game.tools.DeformTerrainTool;
import game.tools.MovePlayerTool;
import game.tools.OctTreeTerrainTool;
import game.voxel.DeformableTerrain;
import game.voxel.OctTreeTerrain;

import java.io.File;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Context {
  
  Logger logger = Logger.getLogger(Context.class);

  View view;
  Scene scene;
  InputDevice inputDevice;
  Simulator simulator;
  BigCube bigCube;
  LogPanel  logPanel;
  Main      main;
  TextureTile stoneTexture;
  TextureTile grassTexture;
  Player player;
  SkyBox skyBox;
  Light light;
  int lightNumber;
  Material material;
  Colors colors;
  Creature creature;
  Grid terrain;
  File root;
  ResFiles resFiles;
  TilingTextures tilingTextures;
  
  Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
  KeyReader keyReader;
  Textures textures;
  SelectionRay selectionRay;
  Models models;
  Serializer serializer;
  TileSetDescriptions tileSetDescriptions;
  HeightMap heightMap;
  ProgramRenderer box;

  List<File> roots = Lists.newArrayList(
    new File("C:/Users/Richard/nwn"),
    new File("/home/local/ANT/richcole/clients/other/nwn-stuff/nwn/"),
    new File("/mnt/nwn/")
  );

  GrassSquare grassSquare;
  GrassBox grassBox;
  DeformableTerrain deformableTerrain;
  MovePlayerTool movePlayerTool;
  DeformTerrainTool deformTerrainTool;
  OctTreeTerrain octTreeTerrain;
  OctTreeTerrainTool octTreeTerrainTool;

  public Context() {
    configureLoghing();
  }
  
  public ProgramRenderer getBox() {
    if ( box == null ) {
      VertexCloud vCloud = new VertexCloud();
      getHeightMap().copyTo(vCloud);
      vCloud.freeze();
      Perlin2 perlin = new Perlin2(8);
      box = new ProgramRenderer(this, vCloud, "screen");
      box.withShaderVariable(new ShaderMatrix("tr", Matrix.rot(Math.PI/4, Vector.U3)));
      box.withShaderVariable(new ShaderArray("noise", perlin.toFloatArray(16, 16, 3)));
    }
    return box;
  }
  
  public GrassBox getGrassBox() {
    if ( grassBox == null ) {
      grassBox = new GrassBox(this);
    }
    return grassBox;
  }

  public Scene getScene() {
    if ( scene == null ) {
      scene = new Scene(this);
    }
    return scene;
  }

  public InputDevice getInputDevice() {
    if ( inputDevice == null ) {
      inputDevice = new InputDevice(this);
    }
    return inputDevice;
  }

  public Simulator getSimulator() {
    if ( simulator == null ) {
      simulator = new Simulator(this);
    }
    return simulator;
  }

  public View getView() {
    if ( view == null ) {
      view = new View(this);
    }
    return view;
  }

  public Player getPlayer() {
    if ( player == null ) {
      player = new Player(this);
    }
    return player;
  }

  public Main getMain() {
    if ( main == null ) {
      main = new Main(this);
    }
    return main;
  }

  public TextureTile getStoneTexture() {
    if ( stoneTexture == null ) {
      stoneTexture = getTilingTextures().getFileTexture("image.jpg");
    }
    return stoneTexture;
  }

  public TextureTile getGrassTexture() {
    if ( grassTexture == null ) {
      grassTexture = getTilingTextures().getFileTexture("grass.jpg");
    }
    return grassTexture;
  }

  public BigCube getBigCube() {
    if ( bigCube == null ) {
      bigCube = new BigCube(this);
    }
    return bigCube;
  }

  public Creature newCreature() {
    return new Creature(this, Model.Wererat);
  }

  public TerrainTile newTile() {
    return new TerrainTile(this, new Vector(-20, -20, 0, 1), Model.Tcn01_r10_01);
  }

  public SkyBox getSkyBox() {
    if ( skyBox == null ) {
      skyBox = new SkyBox(this);
    }
    return skyBox;
  }

  public LogPanel getLogPanel() {
    if ( logPanel == null ) {
      logPanel = new LogPanel(this);
    }
    return logPanel;
  }

  public int getNextLightNumber() {
    if ( lightNumber == 0 ) {
      lightNumber = 0;
    } else {
      lightNumber += 1;
    }
    return lightNumber;
  }

  public Material getMaterial() {
    if ( material == null ) {
      material = new Material(this);
    }
    return material;
  }
  
  public Colors getColors() {
    if ( colors == null ) {
      colors = new Colors();
    }
    return colors;
  }

  public Gson getGson() {
    return gson;
  }
  
  public File getNwnRoot() {
    if ( root == null ) {
      for(File cand: roots) {
        logger.info("Checking " + cand);
        if ( cand.exists() ) {
          root = cand;
        }
      }
    }
    if ( root == null ) {
      throw new RuntimeException("Unable to locate nwn root directory");
    }
    return root;
  }
  
  public KeyReader getKeyReader() {
    if ( keyReader == null ) {
      keyReader = new KeyReader(this);
    }
    return keyReader;
  }

  public Textures getTextures() {
    if ( textures == null ) {
      textures = new Textures(this);
    }
    return textures;
  }
  
  public Grid getTerrain() {
    if ( terrain == null ) {
      terrain = new Grid(this, 80, 80);
    }
    return terrain;
  }

  public void configureLoghing() {
    BasicConfigurator.configure();
  }
  
  public SelectionRay getSelectionRay() {
    if ( selectionRay == null ) {
      selectionRay = new SelectionRay(this);
    }
    return selectionRay;
  }

  public Models getModels() {
    if ( models == null ) {
      models = new Models(this);
    }
    return models;
  }

  public double getScale() {
    return 10;
  }
  
  public double getGridScale() {
    return 10;
  }

  public double getTileScale() {
    return 100;
  }

  public ResFiles getResFiles() {
    if ( resFiles == null ) {
      resFiles = new ResFiles();
    }
    return resFiles;
  }

  public Serializer getSerializer() {
    if ( serializer == null ) {
      serializer = new Serializer();
    }
    return serializer;
  }

  public TileSetDescriptions getTileSetDescriptions() {
    if ( tileSetDescriptions == null ) {
      tileSetDescriptions = new TileSetDescriptions(this);
    }
    return tileSetDescriptions;
  }
  
  public TilingTextures getTilingTextures() {
    if ( tilingTextures == null ) {
      tilingTextures = new TilingTextures(this);
    }
    return tilingTextures;
  }

  public HeightMap getHeightMap() {
    if ( heightMap == null ) {
      heightMap = new HeightMap(this, 128, 128);
      new Perlin2(12).generate(heightMap, heightMap.getWidth());
      heightMap.calculateNormals();
    }
    return heightMap;
  }

  public GrassSquare getGrassSquare() {
    if ( grassSquare == null ) {
      grassSquare = new GrassSquare(this);
    }
    return grassSquare;
  }

  public DeformableTerrain getDeformableTerrain() {
    if ( deformableTerrain == null ) {
      deformableTerrain = new DeformableTerrain(this);
    }
    return deformableTerrain;
  }

  public MovePlayerTool getMovePlayerTool() {
    if ( movePlayerTool == null ) {
      movePlayerTool = new MovePlayerTool(this);
    }
    return movePlayerTool;
  }

  public DeformTerrainTool getDeformTerrainTool() {
    if ( deformTerrainTool == null ) {
      deformTerrainTool = new DeformTerrainTool(this);
    }
    return deformTerrainTool;
  }
  
  public  OctTreeTerrain getOctTreeTerrain() {
    if ( octTreeTerrain == null ) {
      octTreeTerrain = new OctTreeTerrain(this);
    }
    return octTreeTerrain;
  }
  
  
  public  OctTreeTerrainTool getOctTreeTerrainTool() {
    if ( octTreeTerrainTool == null ) {
      octTreeTerrainTool = new OctTreeTerrainTool(this);
    }
    return octTreeTerrainTool;
  }

}
