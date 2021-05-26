package vn.map4d.map.map4d_map;

import android.content.Context;

import java.util.List;
import java.util.Map;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.StandardMessageCodec;
import io.flutter.plugin.platform.PlatformView;
import io.flutter.plugin.platform.PlatformViewFactory;
import vn.map4d.map.camera.MFCameraPosition;

public class FMFMapViewFactory extends PlatformViewFactory {

  private final BinaryMessenger binaryMessenger;

  public FMFMapViewFactory(BinaryMessenger binaryMessenger) {
    super(StandardMessageCodec.INSTANCE);
    this.binaryMessenger = binaryMessenger;
  }

  @Override
  public PlatformView create(Context context, int viewId, Object args) {
    final Map<String, Object> creationParams = (Map<String, Object>) args;
    final FMFMapViewBuilder builder = new FMFMapViewBuilder();
    Convert.interpretMap4dOptions(creationParams.get("options"), builder);
    if (creationParams.containsKey("initialCameraPosition")
      && creationParams.get("initialCameraPosition") != null) {
      MFCameraPosition initialCameraPosition = Convert.toCameraPosition(creationParams.get("initialCameraPosition"));
      builder.setInitialCameraPosition(initialCameraPosition);
    }
    if (creationParams.containsKey("circlesToAdd")) {
      builder.setInitialCircles(creationParams.get("circlesToAdd"));
    }
    if (creationParams.containsKey("polylinesToAdd")) {
      builder.setInitialPolylines(creationParams.get("polylinesToAdd"));
    }
    if (creationParams.containsKey("polygonsToAdd")) {
      builder.setInitialPolygons(creationParams.get("polygonsToAdd"));
    }
    if (creationParams.containsKey("markersToAdd")) {
      builder.setInitialMarkers(creationParams.get("markersToAdd"));
    }
    if (creationParams.containsKey("poisToAdd")) {
      builder.setInitialPOIs(creationParams.get("poisToAdd"));
    }
    if (creationParams.containsKey("buildingsToAdd")) {
      builder.setInitialBuildings(creationParams.get("buildingsToAdd"));
    }
    if (creationParams.containsKey("tileOverlaysToAdd")) {
      builder.setInitialTileOverlays((List<Map<String, ?>>) creationParams.get("tileOverlaysToAdd"));
    }

    return builder.build(viewId, context, binaryMessenger);
  }
}
