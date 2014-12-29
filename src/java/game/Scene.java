package game;

import java.util.List;

import com.google.common.collect.Lists;

public class Scene {
  
  private Context context;
  
  List<Renderable> renderables = Lists.newArrayList();

  public Scene(Context context) {
    this.context = context;
  }

  public void render() {
    context.getView().perspectiveView();
    context.getView().clear();
    context.getPlayer().render();
    
    for(Renderable r: renderables) {
      r.render();
    }

    context.getView().orthoView();
    context.getLogPanel().render();
  }

  public void register(Renderable o) {
    renderables.add(o);
  }
    
}
