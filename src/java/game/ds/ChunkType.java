package game.ds;

public enum ChunkType {
  
  ROOT(0x4D4D, null, 0, true),
  VERSION(0x0002, ROOT, 4, false),

  EDIT3DS(0x3D3D, ROOT, 0, true),
  ONE_UNIT(0x0100, EDIT3DS, 4, false),
  BACKGROUND_BITMAP(0x1100, EDIT3DS, -1, false),
  MESH_VERSION(0x3D3E, EDIT3DS, 4, false),

  KEYF3DS(0xB000, ROOT, 0, false),
  MATERIAL(0xAFFF, EDIT3DS, 0, true),
  MATERIAL_NAME(0xA000, MATERIAL, -1, false),
  MATERIAL_AMBIENT_COLOR(0xA010, MATERIAL, 0, true),
  MATERIAL_DIFFUSE_COLOR(0xA020, MATERIAL, 0, true),
  MATERIAL_SPECULAR_COLOR(0xA030, MATERIAL, 0, true),
  MATERIAL_SHININESS_PERCENT(0xA040, MATERIAL, 0, true),
  MATERIAL_SHININESS_STENGTH_PERCENT(0xA041, MATERIAL, 0, true),
  MATERIAL_TRANSPARENCY_PCT(0xA050, MATERIAL, 0, true),
  MATERIAL_TRANSPARENCY_FALLOFF(0xA052, MATERIAL, 0, true),
  MATERIAL_REFLECTION_BLUR(0xA053, MATERIAL, 0, true),
  MATERIAL_SELF_ILLUMINATION(0xA084, MATERIAL, 0, true),
  MATERIAL_IN_TRAC(0xA08A, MATERIAL, 0, true),
  MATERIAL_WIRE_THICKNESS(0xA087, MATERIAL, 0, false),
  MATERIAL_TEXTURE_MAP1(0xA200, MATERIAL, 0, true),
  MATERIAL_BUMP_MAP(0xA230, MATERIAL, 0, true),
  
  RENDER_TYPE(0xA100, MATERIAL, 0, false),
  MAPPING_FILENAME(0xA300, null, 0, false),
  MAPPING_PARAMETERS(0xA351, null, 0, false),
  BLUR_PERCENT(0xA353, null, 0, false),
  VSCALE(0xA354, null, 4, false),
  USCALE(0xA355, null, 4, false),
  VOFFSET(0xA35A, null, 4, false),
  UOFFSET(0xA358, null, 4, false),
  ROTATION_ANGLE(-1, null, 4, false),
  BUMP_MAP_PRESENT(0xA252, null, 0, false),

  RGB_FLOAT(0x0011, null, 12, false),
  RGB(0x0011, null, 3, false),
  RGB_GAMMA(0x0012, null, 3, false),
  RGB_FLOAT_GAMMA(0x0013, null, 3, false),
  PERCENT(0x0030, null, 2, false),
  PERCENT_FLOAT(0x0031, null, 2, false),
  
  SELF_ILLUMINATION(0x0084, null, 3, false),
  TWO_SIDED(0xa081, null, 0, false),
  
  OBJECT_BLOCK(0x4000, null, -1, true),
  
  NO_MATCH(0, null, 0, false);
  
  int code;
  ChunkType parentType;
  int dataSize;
  boolean hasChildren;

  ChunkType(int code, ChunkType parentType, int dataSize, boolean hasChildren) {
    this.code = code;
    this.parentType = parentType;
    this.dataSize = dataSize;
    this.hasChildren = hasChildren;
  }
  
  static ChunkType getChunkType(int code, ChunkType parentType) {
    for(ChunkType type: values()) {
      if ( type.code == code && (type.parentType == parentType || type.parentType == null) ) {
        return type;
      }
    }
    return NO_MATCH;
  }
}
