package Year2019.Day08;

import Utility.*;
import java.util.*;

class Constants {
  static Boolean showLogs = false;
}

class Day_8 {
  public static void main(String[] args) {
    ArrayList<String> filePaths = new ArrayList<String>();
    // filePaths.add("src/Year2019/Day08/Input/test_1.txt");
    filePaths.add("src/Year2019/Day08/Input/full_input.txt");

    for (String path : filePaths) {
      MyFileReader fileReader = new MyFileReader(path);
      ArrayList<String> lines = fileReader.getInputLines();

      // ImageDecoder imageDecoder = new ImageDecoder(2, 2);
      ImageDecoder imageDecoder = new ImageDecoder(25, 6);
      imageDecoder.decodeImageInput(lines.get(0));
      imageDecoder.renderImage();
    }
  }
}

class ImageDecoder {
  ArrayList<Layer> layers = new ArrayList<Layer>();
  int width;
  int height;

  public ImageDecoder(int width, int height) {
    this.width = width;
    this.height = height;
  }

  public void decodeImageInput(String imageInput) {
    int nbrPixelsPerLayer = width * height;
    int nbrLayers = imageInput.length() / nbrPixelsPerLayer;

    layers.clear();

    for (int i = 0; i < nbrLayers; i++) {
      Layer layer = new Layer(width, height);
      layers.add(layer);
    }

    for (int i = 0; i < imageInput.length(); i++) {
      int pixelVal = Integer.parseInt("" + imageInput.charAt(i));
      int layerIndex = i / nbrPixelsPerLayer;
      Layer layer = layers.get(layerIndex);
      layer.addPixelVal(pixelVal);
    }

    int minZeroCount = Integer.MAX_VALUE;
    Layer minZeroLayer = null;
    for (Layer layer : layers) {
      int layerZeroCount = layer.getPixelValCount(0);
      if (layerZeroCount < minZeroCount) {
        minZeroCount = layerZeroCount;
        minZeroLayer = layer;
      }
    }

    if (minZeroLayer != null) {
      int count1 = minZeroLayer.getPixelValCount(1);
      int count2 = minZeroLayer.getPixelValCount(2);
      System.out.println("Checksum: " + (count1 * count2));
    }
  }

  public void renderImage() {
    int nbrPixels = width * height;
    ArrayList<Integer> renderedPixels = new ArrayList<Integer>(nbrPixels);
    for (int i = 0; i < nbrPixels; i++) {
      for (Layer layer : layers) {
        int pixelVal = layer.pixels.get(i);
        if (pixelVal == 0 || pixelVal == 1) {
          renderedPixels.add(pixelVal);
          break;
        }
      }
    }

    for (int i = 0; i < renderedPixels.size(); i++) {
      if (i % width == 0) {
        System.out.print("\n");
      }
      int pixelVal = renderedPixels.get(i);
      if (pixelVal == 0) {
        System.out.print(" ");
      } else {
        System.out.print("*");
      }
    }
    System.out.print("\n\n");
  }
}

class Layer {
  int width;
  int height;
  ArrayList<Integer> pixels = new ArrayList<Integer>(width * height);

  HashMap<Integer, Integer> numberCountMap = new HashMap<Integer, Integer>();

  public Layer(int width, int height) {
    this.width = width;
    this.height = height;
  }

  public void addPixelVal(int pixelVal) {
    pixels.add(pixelVal);
    Integer pixelValCount = numberCountMap.get(pixelVal);
    if (pixelValCount == null) {
      pixelValCount = 0;
    }
    pixelValCount += 1;
    numberCountMap.put(pixelVal, pixelValCount);
  }

  public int getPixelValCount(int pixelVal) {
    Integer pixelValCount = numberCountMap.get(pixelVal);
    return pixelValCount != null ? pixelValCount : 0;
  }
}