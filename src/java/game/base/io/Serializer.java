package game.base.io;

import game.math.Quaternion;
import game.math.Vector;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.log4j.Logger;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class Serializer {
  
  private static Logger logger = Logger.getLogger(Serializer.class);
  
  private Gson gson;
  
  public static class FieldValuePair {
    String n;
    Value  v;
    
    public FieldValuePair(String name, Value value) {
      this.n = name;
      this.v = value;
    }
  }
  
  public static class Pair {
    Value k;
    Value v;
    
    public Pair(Value key, Value value) {
      super();
      this.k = key;
      this.v = value;
    }
  }
  
  public static class Value {
    String r;
    String s;
    Double d;
    Long   l;
    Integer i;
    Short t;
    Byte b;
    Float f;
    Pair e;
    double[] v;
    double[] q;
  }
  
  public static class ObjType {
    String b;
    ObjType a;
  }
  
  public static class ObjDescription {
    String i;
    ObjType t;
    List<FieldValuePair> f;
    List<Value> l;
    
    public ObjDescription(String id, ObjType type) {
      super();
      this.i = id;
      this.t = type;
      this.f = Lists.newArrayList();
    }
  }
  
  public static class ObjSerialization {
    String i;
    Collection<ObjDescription> o;

    public ObjSerialization(String id, Collection<ObjDescription> objects) {
      this.i = id;
      this.o = objects;
    }
  }
  
  public Serializer() {
    gson = new GsonBuilder()
      .disableHtmlEscaping()
      .create();
  }

  public void toJson(List<ObjDescription> obj, Writer writer) {
    gson.toJson(obj, writer);
  }
  
  public List<ObjDescription> fromJson(Reader reader) {
    return gson.fromJson(reader, new TypeToken<List<ObjDescription>>(){}.getType());
  }
  
  public void serialize(Object obj, File outputFile) {
    logger.info("Writing to " + outputFile.getPath());
    try {
      OutputStreamWriter writer = getGzipWriter(outputFile);
      try {
        serialize(obj, writer);
      }
      finally {
        writer.close();
      }
    } catch(Exception e) {
      Throwables.propagate(e);
    }
  }

  public void serialize(Object obj, Writer writer) {
    Map<Object, ObjDescription> dMap = Maps.newHashMap();
    ensureDescription(dMap, obj);
    gson.toJson(new ObjSerialization(dMap.get(obj).i, dMap.values()), writer);
  }
  
  public <T> T deserialize(File inputFile, Class<? extends T> klass) {
    try {
      InputStreamReader reader = getGzipReader(inputFile);
      try {
        return deserialize(reader, klass);
      }
      finally {
        reader.close();
      }
    } catch(Exception e) {
      Throwables.propagate(e);
      return null;
    }
  }
  
  public <T> T deserialize(Reader reader, Class<? extends T> klass) {
    Map<String, ObjDescription> idMap = Maps.newHashMap();
    Map<String, Object> dMap = Maps.newHashMap();
    ObjSerialization objS = gson.fromJson(reader, ObjSerialization.class);
    for(ObjDescription objD: objS.o) {
      idMap.put(objD.i, objD);
      dMap.put(objD.i, newObject(objD.t, objD.l));
    }
    for(ObjDescription objD: objS.o) {
      Object obj = dMap.get(objD.i);

      if ( objD.t.a != null ) {
        if ( objD.l != null ) {
          for(int index=0; index<objD.l.size(); ++index) {
            Array.set(obj, index, getValue(dMap, objD.l.get(index)));
          }
        }
      } else if (Collection.class.isAssignableFrom(getClassFromType(objD.t)) ) {
        Collection collection = (Collection) obj;
        for(Value listElement: objD.l) {
          collection.add(getValue(dMap, listElement));
        }
      } else if (Map.class.isAssignableFrom(getClassFromType(objD.t)) ) {
        Map map = (Map) obj;
        for(Value listElement: objD.l) {
          Object key = getValue(dMap, listElement.e.k);
          Object value = getValue(dMap, listElement.e.v);
          map.put(key, value);
        }
      } else {
        for(FieldValuePair fieldValue: objD.f) {
          Field field = getField(obj, fieldValue);
          setValue(obj, field, getValue(dMap, fieldValue.v));
        }
      }
    }    
    return (T) dMap.get(objS.i);
  }

  private OutputStreamWriter getGzipWriter(File file) throws IOException, FileNotFoundException {
    return new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(file)));
  }

  private InputStreamReader getGzipReader(File file) throws IOException, FileNotFoundException {
    return new InputStreamReader(new GZIPInputStream(new FileInputStream(file)));
  }
  
  private void setValue(Object obj, Field field, Object value) {
    try {
      field.setAccessible(true);
      field.set(obj, value);
    } catch(Exception e) {
      Throwables.propagate(e);
    }
  }

  private Object getValue(Map<String, Object> dMap, Value value) {
    if ( value == null ) {
      return null;
    }
    if ( value.s != null ) {
      return value.s;
    }
    if ( value.r != null ) {
      return dMap.get(value.r);
    }
    if ( value.l != null ) {
      return value.l;
    }
    if ( value.i != null ) {
      return value.i;
    }
    if ( value.d != null ) {
      return value.d;
    }
    if ( value.f != null ) {
      return value.f;
    }
    if ( value.t != null ) {
      return value.t;
    }
    if ( value.b != null ) {
      return value.b;
    }
    if ( value.f != null ) {
      return value.f;
    }
    if ( value.v != null ) {
      return new Vector(value.v);
    }
    if ( value.q != null ) {
      return new Quaternion(value.q);
    }
    if ( value.e != null ) {
      return value.e;
    }
    throw new RuntimeException("Unable to decode fieldValue");
  }

  private Field getField(Object obj, FieldValuePair field) {
    Class<? extends Object> klass = obj.getClass();
    while(klass != null) {
      try {
        return klass.getDeclaredField(field.n);
      } catch(NoSuchFieldException e) {
        klass = klass.getSuperclass();
        if ( klass == null ) {
          Throwables.propagate(e);
        }
      }
    }
    throw new RuntimeException("Unable to locate field " + field.n + " on object of type " + obj.getClass());
  }

  private Object getFieldValue(Object obj, Field field) {
    try {
      return field.get(obj);
    } catch(Exception e) {
      Throwables.propagate(e);
      return null;
    }
  }

  private Object newObject(ObjType type, List<Value> arrayContents) {
    try {
      if ( type.b != null ) {
        Class<?> klass = getClassFromType(type);
        Constructor<?> constructor = klass.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
      } else if ( type.a != null ) {
        return Array.newInstance(getClassFromType(type.a), arrayContents.size());
      } else {
        throw new RuntimeException("Unsupported");
      }
    } catch(Exception e) {
      Throwables.propagate(e);
      return null;
    }
  }

  private Class<?> getClassFromType(ObjType type) {
    try {
      if ( type.b != null ) {
        return getClass().getClassLoader().loadClass(type.b);
      } else if ( type.a != null ) {
        return Array.class;
      } else {
        throw new RuntimeException("Couldn't decode type");
      }
    } catch(Exception e) {
      Throwables.propagate(e);
      return null;
    }
  }

  private void addDescription(Map<Object, ObjDescription> dMap, Object obj) {
    ObjDescription desc = ensureDescription(dMap, obj);

    if ( obj instanceof Collection ) {
      Collection<Object> collection = (Collection<Object>) obj;
      desc.l = Lists.newArrayList();
      for(Object listElement: collection) {
        desc.l.add(getValue(dMap, listElement));
      }
    } else if ( obj instanceof Map ) {
      Map<Object, Object> map = (Map<Object, Object>) obj;
      desc.l = Lists.newArrayList();
      for(Map.Entry<Object, Object> listElement: map.entrySet()) {
        desc.l.add(getValue(dMap, listElement.getKey(), listElement.getValue()));
      }
    } else if ( obj.getClass().isArray() ) {
      int length = Array.getLength(obj);
      desc.l = Lists.newArrayList();
      for(int i=0;i<length;++i) {
        desc.l.add(getValue(dMap, Array.get(obj, i)));
      }
    } else {
      writeFields(dMap, obj, desc);
    }
  }

  private void writeFields(Map<Object, ObjDescription> dMap, Object obj, ObjDescription desc) {
    for(Field f: getAllFields(obj)) {
      if (isStatic(f)) {
        continue;
      }
      f.setAccessible(true);
      Object value = getFieldValue(obj, f);
      if ( value != null ) {
        desc.f.add(new FieldValuePair(f.getName(), getValue(dMap, value)));
      }
    }
  }

  private List<Field> getAllFields(Object obj) {
    List<Field> fields = Lists.newArrayList();
    for(Class klass=obj.getClass(); klass != Object.class; klass=klass.getSuperclass()) {
      for(Field field: klass.getDeclaredFields()) {
        fields.add(field);
      }
    }
    return fields;
  }

  private boolean isStatic(Field f) {
    return (f.getModifiers() & Modifier.STATIC) != 0;
  }

  private Value getValue(Map<Object, ObjDescription> dMap, Object key, Object val) {
    Value value = new Value();
    value.e = new Pair(getValue(dMap, key), getValue(dMap, val));
    return value;
  }
  
  private Value getValue(Map<Object, ObjDescription> dMap, Object fieldValue) {
    Value value = new Value();
    if ( fieldValue instanceof Long ) {
      value.l = (Long) fieldValue;
      return value;
    }
    if ( fieldValue instanceof String ) {
      value.s = (String) fieldValue;
      return value;
    }
    if ( fieldValue instanceof Integer ) {
      value.i = (Integer) fieldValue;
      return value;
    }
    if ( fieldValue instanceof Byte ) {
      value.b = (Byte) fieldValue;
      return value;
    }
    if ( fieldValue instanceof Short ) {
      value.t = (Short) fieldValue;
      return value;
    }
    if ( fieldValue instanceof Double ) {
      value.d = (Double) fieldValue;
      return value;
    }
    if ( fieldValue instanceof Float ) {
      value.f = (Float) fieldValue;
      return value;
    }
    if ( fieldValue instanceof Quaternion ) {
      value.q = ((Quaternion) fieldValue).toDoubleArray();
      return value;
    }
    if ( fieldValue instanceof Vector ) {
      value.v = ((Vector) fieldValue).toDoubleArray();
      return value;
    }
    if ( fieldValue == null ) {
      return null;
    }
    if ( fieldValue.getClass() == null ) {
      throw new RuntimeException("fieldValue has no class");
    }
    ObjDescription desc = ensureDescription(dMap, fieldValue);
    value.r = desc.i;
    return value;
  }

  private ObjDescription ensureDescription(Map<Object, ObjDescription> dMap, Object obj) {
    ObjDescription desc = dMap.get(obj);
    if ( desc == null ) {
      String id = getSystemId(obj);
      desc = new ObjDescription(id, getType(obj.getClass()));
      dMap.put(obj, desc);
      addDescription(dMap, obj);
    }
    return desc;
  }

  private ObjType getType(Class<?> klass) {
    ObjType r = new ObjType();
    if ( klass.isArray() ) {
      r.a = new ObjType();
      r.a = getType(klass.getComponentType());
    } else {
      r.b = klass.getName();
    }
    return r;
  }

  private String getSystemId(Object obj) {
    return String.valueOf(System.identityHashCode(obj));
  }
}
