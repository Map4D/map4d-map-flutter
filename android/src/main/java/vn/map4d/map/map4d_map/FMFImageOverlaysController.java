package vn.map4d.map.map4d_map;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.flutter.plugin.common.MethodChannel;
import vn.map4d.map.core.Map4D;
import vn.map4d.map.overlays.MFImageOverlay;
import vn.map4d.map.overlays.MFImageOverlayOptions;

class FMFImageOverlaysController {

  private final Map<String, FMFImageOverlayController> imageOverlayIdToController;
  private final MethodChannel methodChannel;
  private Map4D map4D;

  FMFImageOverlaysController(MethodChannel methodChannel) {
    this.imageOverlayIdToController = new HashMap<>();
    this.methodChannel = methodChannel;
  }

  void setMap(Map4D map4D) {
    this.map4D = map4D;
  }

  void addImageOverlays(List<Map<String, ?>> imageOverlaysToAdd) {
    if (imageOverlaysToAdd == null) {
      return;
    }
    for (Map<String, ?> imageOverlayToAdd : imageOverlaysToAdd) {
      addImageOverlay(imageOverlayToAdd);
    }
  }

  void changeImageOverlays(List<Map<String, ?>> imageOverlaysToChange) {
    if (imageOverlaysToChange == null) {
      return;
    }
    for (Map<String, ?> imageOverlayToChange : imageOverlaysToChange) {
      changeImageOverlay(imageOverlayToChange);
    }
  }

  void removeImageOverlays(List<String> imageOverlayIdsToRemove) {
    if (imageOverlayIdsToRemove == null) {
      return;
    }
    for (String imageOverlayId : imageOverlayIdsToRemove) {
      if (imageOverlayId == null) {
        continue;
      }
      removeImageOverlay(imageOverlayId);
    }
  }

  private void addImageOverlay(Map<String,?> imageOverlayOptions) {
    if (imageOverlayOptions == null) {
      return;
    }
    FMFImageOverlayBuilder imageOverlayBuilder = new FMFImageOverlayBuilder();
    String imageOverlayId = Convert.interpretImageOverlayOptions(imageOverlayOptions, imageOverlayBuilder);
    MFImageOverlayOptions options = imageOverlayBuilder.build();
    MFImageOverlay imageOverlay = map4D.addImageOverlay(options);
    FMFImageOverlayController imageOverlayController = new FMFImageOverlayController(imageOverlay);
    imageOverlayIdToController.put(imageOverlayId, imageOverlayController);
  }

  private void changeImageOverlay(Map<String,?> imageOverlayOptions) {
    if (imageOverlayOptions == null) {
      return;
    }
    String imageOverlayId = getImageOverlayId(imageOverlayOptions);
    FMFImageOverlayController imageOverlayController = imageOverlayIdToController.get(imageOverlayId);
    if (imageOverlayController != null) {
      Convert.interpretImageOverlayOptions(imageOverlayOptions, imageOverlayController);
    }
  }

  private void removeImageOverlay(String imageOverlayId) {
    FMFImageOverlayController imageOverlayController = imageOverlayIdToController.get(imageOverlayId);
    if (imageOverlayController != null) {
      imageOverlayController.remove();
      imageOverlayIdToController.remove(imageOverlayId);
    }
  }

  @SuppressWarnings("unchecked")
  private static String getImageOverlayId(Map<String, ?> tileOverlay) {
    return (String) tileOverlay.get("imageOverlayId");
  }
}
