package game.nwn.readers.set;

import game.containers.CacheMap;
import game.containers.Containers;
import game.containers.Factory;
import game.containers.TriMap;
import game.nwn.readers.BinaryFileReader;
import game.nwn.readers.Resource;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.LineReader;

public class SetReader {

  private static Logger logger = Logger.getLogger(SetReader.class);
  
  Resource resource;
  TriMap<String, String, String> trimap = Containers.newTriMap();
  CacheMap<Class<? extends Object>, Map<String, FieldDescription>> typeMap = Containers.newCacheMap();
  
  public static class TileSetDescription {
    General general;
    Grass grass;
    List<TerrainType> terrainTypes = Lists.newArrayList();
    List<CrosserType> crosserTypes = Lists.newArrayList();
    List<PrimaryRule> primaryRules = Lists.newArrayList();
    List<PrimaryRule> secondaryRules = Lists.newArrayList();
    private List<Tile> tiles = Lists.newArrayList();
    List<Group> groups = Lists.newArrayList();
    public List<Tile> getTiles() {
      return tiles;
    }
    public void setTiles(List<Tile> tiles) {
      this.tiles = tiles;
    }
  }

  public SetReader() {
  }
  
  public TileSetDescription read(Resource resource) {
    BinaryFileReader inp = resource.getReader().getInp();
    TileSetDescription tileSet = new TileSetDescription();
    try {
      inp.seek(resource.getOffset());
      byte[] bytes = inp.readBytes(resource.getLength());
      LineReader lineReader = new LineReader(new InputStreamReader(new ByteArrayInputStream(bytes)));
      String line;
      String header = null;
      String key = null;
      String value = null;
      Object target = null;
      Class<?> targetType = null;
      Object parentTarget = tileSet;
      int expectedCount = 0;
      while((line = lineReader.readLine()) != null) {
        if ( line.matches("\\s*") ) {
          // nothing
        } else if ( line.startsWith(";") ) {
          // nothing
        } else if ( line.startsWith("[") ) {
          header = line.substring(1, line.length()-1);
          String shortClassName;
          String className;
          boolean haveTypeHeader = false;

          if ( targetType == Tile.class && target != null ) {
            targetType = Door.class;
            parentTarget = target;
            expectedCount = ((Tile) target).numDoors;
          }
          
          if ( targetType == Door.class ) {
            if ( expectedCount == 0 ) {
              targetType = Tile.class;
              parentTarget = tileSet;
            } else {
              --expectedCount;
            }
          } 
          
          shortClassName = getClassName(header);
          if ( shortClassName.equals("TerrainTypes") ) {
            targetType = TerrainType.class;
            haveTypeHeader = true;
          } else if ( shortClassName.equals("CrosserTypes") ) {
            targetType = CrosserType.class;
            haveTypeHeader = true;
          } else if ( shortClassName.equals("PrimaryRules") ) {
            targetType = PrimaryRule.class;
            haveTypeHeader = true;
          } else if ( shortClassName.equals("SecondaryRules") ) {
            targetType = SecondaryRule.class;
            haveTypeHeader = true;
          } else if ( shortClassName.equals("Tiles") ) {
            targetType = Tile.class;
            haveTypeHeader = true;
          } else if ( shortClassName.equals("Groups") ) {
            targetType = Group.class;
            haveTypeHeader = true;
          }
          if ( haveTypeHeader ) {
            target = null;
          } else if (targetType == null) {
            className = General.class.getPackage().getName() + "." + shortClassName;
            target = getClass().getClassLoader().loadClass(className).newInstance();
          } else {
            className = targetType.getName();
            shortClassName = targetType.getSimpleName();
            target = targetType.newInstance();
          }
          if ( target != null ) {
            String fieldName = uncapitalize(shortClassName);
            if ( targetType != null ) {
              Field field = parentTarget.getClass().getDeclaredField(fieldName + "s");
              field.setAccessible(true);
              List list = (List) field.get(parentTarget);
              list.add(target);
            } else {
              Field field = parentTarget.getClass().getDeclaredField(fieldName);
              field.setAccessible(true);
              field.set(parentTarget, target);
            }
          }   
        } else {
          if ( target != null ) {
            int index = line.indexOf('=');
            key = line.substring(0, index);
            value = line.substring(index+1, line.length());
            addKeyValuePair(target, key, value);
          }
        }
      }
    } catch(Exception e) {
      Throwables.propagate(e);
    }
    return tileSet;
  }

  private String getClassName(String header) {
    String s1 = header.toLowerCase();
    String s4 = capitalize(s1);
    while( s4.contains(" ") ) {
      int index = s4.indexOf(" ");
      s4 = s4.substring(0, index) + capitalize(s4.substring(index+1, s4.length()));
    }
    return s4;
  }

  private String capitalize(String s1) {
    String s2 = s1.substring(0, 1);
    String s3 = s1.substring(1, s1.length());
    String s4 = s2.toUpperCase() + s3;
    return s4;
  }
  
  private String uncapitalize(String s1) {
    String s2 = s1.substring(0, 1);
    String s3 = s1.substring(1, s1.length());
    String s4 = s2.toLowerCase() + s3;
    return s4;
  }

  private void addKeyValuePair(Object target, String key, String value) {
    key = uncapitalize(key);
    try {
      Class<? extends Object> targetClass = target.getClass();
      Map<String, FieldDescription> targetFieldMap = typeMap.ensure(targetClass, createTargetFieldMap(targetClass));
      targetFieldMap.get(key).set(target, value);
    } catch(Exception e) {
      logger.error("Cannot set " + key + " on " + target.getClass() + " to " + value);
      Throwables.propagate(e);
    }
  }
  
  static interface TypeConverter {
    Object convert(Object object);
  }
  
  static class StringToInteger implements TypeConverter {
    @Override
    public Object convert(Object object) {
      return Integer.valueOf((String)object);
    }
  }
  
  static class StringToDouble implements TypeConverter {
    @Override
    public Object convert(Object object) {
      return Double.valueOf((String)object);
    }
  }

  static class StringToFloat implements TypeConverter {
    @Override
    public Object convert(Object object) {
      return Float.valueOf((String)object);
    }
  }

  static class FieldDescription {
    Field field;
    TypeConverter typeConverter;
    
    public FieldDescription(Field field) {
      this.field = field;
    }

    public void set(Object target, Object value) {
      try {
        field.setAccessible(true);
        field.set(target, typeConverter != null ? typeConverter.convert(value) : value);
      } catch(Exception e) {
        Throwables.propagate(e);
      }
    }
  }
  
  static class FieldMapFactory implements Factory<Map<String, FieldDescription>> {
    private Class<?> targetClass;

    FieldMapFactory(Class<?> targetClass) {
      this.targetClass = targetClass;
    }
    
    @Override
    public Map<String, FieldDescription> create() {
      Map<String, FieldDescription> fieldMap = Maps.newHashMap();
      for(Field field: targetClass.getDeclaredFields()) {
        FieldDescription fd = new FieldDescription(field);
        if ( field.getType().equals(Integer.class) ) {
          fd.typeConverter = new StringToInteger();
        } else if ( field.getType().equals(Float.class) ) {
          fd.typeConverter = new StringToFloat();
        } else if ( field.getType().equals(Double.class) ) {
          fd.typeConverter = new StringToDouble();
        }
        Name name = field.getAnnotation(Name.class);
        if ( name != null ) {
          fieldMap.put(name.value(), fd);
        } else {
          fieldMap.put(field.getName(), fd);
        }
      }
      return fieldMap;
    }
  }

  private Factory<Map<String, FieldDescription>> createTargetFieldMap(Class<?> targetClass) {
    return new FieldMapFactory(targetClass);
  }
}
