package clindo.contextJ.file;

/*
 * ContextJ* 0.2
 * Copyright (c) 2007 Pascal Costanza
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

import java.util.*;

public class ContextJ {
  
  public static class Layer {}
  
  private static ThreadLocal<LinkedList<Layer>> activeLayers = 
    new ThreadLocal<LinkedList<Layer>>() {
    protected LinkedList<Layer> initialValue() {
      return new LinkedList<Layer>();
    }};
  
  public static interface Block {
    public void eval();
  }
  
  public static interface Evaluator {
    public void eval(Block b);
  }
  
  private static class WithoutEvaluator implements Evaluator {
    private Layer[] layers;
    
    private WithoutEvaluator(Layer[] layers) {this.layers = layers;}
    
    public void eval(Block b) {
      if (layers.length == 0) {b.eval(); return;}
      LinkedList<Layer> currentActiveLayers = activeLayers.get();
      if ((layers.length == 1) && (!(currentActiveLayers.contains(layers[0])))) {b.eval(); return;}
      LinkedList<Layer> clonedActiveLayers = new LinkedList<Layer>(currentActiveLayers);
      for (Layer layer: layers) currentActiveLayers.remove(layer);
      try {b.eval();} finally {activeLayers.set(clonedActiveLayers);}
    }
  }
  
  private static class WithEvaluator implements Evaluator {
    private Layer[] layers;
    
    private WithEvaluator(Layer[] layers) {this.layers = layers;}
    
    public void eval(Block b) {
      if (layers.length == 0) {b.eval(); return;}
      LinkedList<Layer> currentActiveLayers = activeLayers.get();
      if ((layers.length == 1) && 
          (currentActiveLayers.size() > 0) &&
          (currentActiveLayers.getFirst() == layers[0])) 
        {b.eval(); return;}
      LinkedList<Layer> clonedActiveLayers = new LinkedList<Layer>(currentActiveLayers);
      for (Layer layer: layers) {
        currentActiveLayers.remove(layer);
        currentActiveLayers.addFirst(layer);
      }
      try {b.eval();} finally {activeLayers.set(clonedActiveLayers);}
    }
  }
  
  public static Evaluator without(Layer... layers) {return new WithoutEvaluator(layers);}
  
  public static Evaluator with(Layer... layers) {return new WithEvaluator(layers);}
  
  public static class LayerDefinitions<Definition> {
    
    private final IdentityHashMap<Layer,Definition> layerDefinitionMap = 
      new IdentityHashMap<Layer,Definition>();
    private final IdentityHashMap<Definition,Layer> definitionLayerMap = 
      new IdentityHashMap<Definition,Layer>();
    private final Definition rootDefinition;
    
    public LayerDefinitions(Definition definition) {
      this.rootDefinition = definition;
    }
    
    public void define(Layer layer, Definition definition) {
      layerDefinitionMap.put(layer, definition); 
      definitionLayerMap.put(definition, layer);
    }
    
    public Definition select() {
      for (Layer l: activeLayers.get()) {
        Definition definition = layerDefinitionMap.get(l);
        if (definition != null) return definition;
      }
      return rootDefinition;
    }
    
    public Definition next(Layer layer) {
      LinkedList<Layer> currentActiveLayers = activeLayers.get();
      int index = currentActiveLayers.indexOf(layer);
      List<Layer> nextActiveLayers = 
        currentActiveLayers.subList(index+1, currentActiveLayers.size());
      for (Layer l: nextActiveLayers) {
        Definition definition = layerDefinitionMap.get(l);
        if (definition != null) return definition;
      }
      return rootDefinition;
    }
    
    public Definition next(Definition definition) {
      return next(definitionLayerMap.get(definition));
    }
  }
}
