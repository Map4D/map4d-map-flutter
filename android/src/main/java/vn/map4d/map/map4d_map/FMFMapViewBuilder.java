package vn.map4d.map.map4d_map;

import android.content.Context;

import java.util.List;
import java.util.Map;

import io.flutter.plugin.common.BinaryMessenger;
import vn.map4d.map.camera.MFCameraPosition;
import vn.map4d.map.core.MFMapType;

class FMFMapViewBuilder implements FMFMapViewOptionsSink {
  private Float minZoomPreference = 2.f;
  private Float maxZoomPreference = 22.f;
  private boolean trackCameraPosition = false;
  private boolean myLocationEnabled = false;
  private boolean myLocationButtonEnabled = false;
  private boolean rotateGesturesEnabled = true;
  private boolean scrollGesturesEnabled = true;
  private boolean tiltGesturesEnabled = true;
  private boolean zoomGesturesEnabled = true;
  private boolean buildingsEnabled = true;
  private boolean poisEnabled = true;
  private MFMapType mapType;
  MFCameraPosition initialCameraPosition;
  private Object initialCircles;
  private Object initialPolylines;
  private Object initialPolygons;
  private Object initialMarkers;
  private Object initialPOIs;
  private Object initialBuildings;
  private Object initialDirectionsRenderers;
  private List<Map<String, ?>> initialTileOverlays;
  private List<Map<String, ?>> initialImageOverlays;

  private String mapId;

  private String style;

  FMFMapViewController build(
    int id,
    Context context,
    BinaryMessenger binaryMessenger,
    LifecycleProvider lifecycleProvider) {
    final FMFMapViewController controller = new FMFMapViewController(context, id, binaryMessenger, lifecycleProvider, initialCameraPosition);
    controller.init();
    controller.setMinMaxZoomPreference(minZoomPreference, maxZoomPreference);
    controller.setMyLocationEnabled(myLocationEnabled);
    controller.setMyLocationButtonEnabled(myLocationButtonEnabled);
    controller.setRotateGesturesEnabled(rotateGesturesEnabled);
    controller.setMapType(mapType);
    controller.setTiltGesturesEnabled(tiltGesturesEnabled);
    controller.setZoomGesturesEnabled(zoomGesturesEnabled);
    controller.setScrollGesturesEnabled(scrollGesturesEnabled);
    controller.setBuildingsEnabled(buildingsEnabled);
    controller.setPOIsEnabled(poisEnabled);
    controller.setInitialCircles(initialCircles);
    controller.setInitialPolylines(initialPolylines);
    controller.setInitialPolygons(initialPolygons);
    controller.setInitialMarkers(initialMarkers);
    controller.setInitialPOIs(initialPOIs);
    controller.setInitialBuildings(initialBuildings);
    controller.setInitialTileOverlays(initialTileOverlays);
    controller.setInitialImageOverlays(initialImageOverlays);
    controller.setInitialDirectionsRenderers(initialDirectionsRenderers);
    controller.setTrackCameraPosition(trackCameraPosition);
    if (mapId != null && !mapId.isEmpty()) {
      controller.setMapId(mapId);
    }
    if (style != null && !style.isEmpty()) {
      controller.setMapStyle(style);
    }
    return controller;
  }

  void setInitialCameraPosition(MFCameraPosition position) {
    this.initialCameraPosition = position;
  }

  @Override
  public void setMapType(MFMapType mapType) {
    this.mapType = mapType;
  }

  @Override
  public void setMapId(String mapId) {
    this.mapId = mapId;
  }

  @Override
  public void setMapStyle(String style) {
    this.style = style;
  }

  @Override
  public void setMinMaxZoomPreference(Float min, Float max) {
    this.minZoomPreference = min;
    this.maxZoomPreference = max;
  }

  @Override
  public void setRotateGesturesEnabled(boolean rotateGesturesEnabled) {
    this.rotateGesturesEnabled = rotateGesturesEnabled;
  }

  @Override
  public void setScrollGesturesEnabled(boolean scrollGesturesEnabled) {
    this.scrollGesturesEnabled = scrollGesturesEnabled;
  }

  @Override
  public void setTiltGesturesEnabled(boolean tiltGesturesEnabled) {
    this.tiltGesturesEnabled = tiltGesturesEnabled;
  }

  @Override
  public void setZoomGesturesEnabled(boolean zoomGesturesEnabled) {
    this.zoomGesturesEnabled = zoomGesturesEnabled;
  }

  @Override
  public void setTrackCameraPosition(boolean trackCameraPosition) {
    this.trackCameraPosition = trackCameraPosition;
  }

  @Override
  public void setMyLocationEnabled(boolean myLocationEnabled) {
    this.myLocationEnabled = myLocationEnabled;
  }

  @Override
  public void setMyLocationButtonEnabled(boolean myLocationButtonEnabled) {
    this.myLocationButtonEnabled = myLocationButtonEnabled;
  }

  @Override
  public void setBuildingsEnabled(boolean buildingsEnabled) {
    this.buildingsEnabled = buildingsEnabled;
  }

  @Override
  public void setPOIsEnabled(boolean poisEnabled) {
    this.poisEnabled = poisEnabled;
  }

  @Override
  public void setInitialCircles(Object initialCircles) {
    this.initialCircles = initialCircles;
  }

  @Override
  public void setInitialPolylines(Object initialPolylines) {
    this.initialPolylines = initialPolylines;
  }

  @Override
  public void setInitialPolygons(Object initialPolygons) {
    this.initialPolygons = initialPolygons;
  }
  
  @Override
  public void setInitialMarkers(Object initialMarkers) {
    this.initialMarkers = initialMarkers;
  }

  @Override
  public void setInitialPOIs(Object initialPOIs) {
    this.initialPOIs = initialPOIs;
  }

  @Override
  public void setInitialBuildings(Object initialBuildings) {
    this.initialBuildings = initialBuildings;
  }

  @Override
  public void setInitialTileOverlays(List<Map<String, ?>> initialTileOverlays) {
    this.initialTileOverlays = initialTileOverlays;
  }

  @Override
  public void setInitialImageOverlays(List<Map<String, ?>> initialImageOverlays) {
    this.initialImageOverlays = initialImageOverlays;
  }

  @Override
  public void setInitialDirectionsRenderers(Object initialDirectionsRenderers) {
    this.initialDirectionsRenderers = initialDirectionsRenderers;
  }
}
