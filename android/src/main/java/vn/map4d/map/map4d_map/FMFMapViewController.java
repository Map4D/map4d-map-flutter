package vn.map4d.map.map4d_map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.platform.PlatformView;
import vn.map4d.map.annotations.MFBuilding;
import vn.map4d.map.annotations.MFCircle;
import vn.map4d.map.annotations.MFDirectionsRenderer;
import vn.map4d.map.annotations.MFMarker;
import vn.map4d.map.annotations.MFPOI;
import vn.map4d.map.annotations.MFPolygon;
import vn.map4d.map.annotations.MFPolyline;
import vn.map4d.map.camera.MFCameraPosition;
import vn.map4d.map.camera.MFCameraUpdate;
import vn.map4d.map.camera.MFCameraUpdateFactory;
import vn.map4d.map.core.MFCoordinateBounds;
import vn.map4d.map.core.MFDataSourceFeature;
import vn.map4d.map.core.MFMapStyleOptions;
import vn.map4d.map.core.MFMapType;
import vn.map4d.map.core.MFMapView;
import vn.map4d.map.core.Map4D;
import vn.map4d.map.core.OnMapReadyCallback;
import vn.map4d.types.MFLocationCoordinate;

public final class FMFMapViewController implements
  DefaultLifecycleObserver,
  ActivityPluginBinding.OnSaveInstanceStateListener,
  PlatformView,
  OnMapReadyCallback,
  FMFMapViewOptionsSink,
  MethodChannel.MethodCallHandler,
  FMFMapViewListener {
  private static final String TAG = "FMFMapViewController";
  private final int id;
  private final MethodChannel methodChannel;
  private Map4D map4D;
  @Nullable
  private MFMapView mapView;
  private final Context context;
  private final LifecycleProvider lifecycleProvider;
  private boolean myLocationEnabled = false;
  private boolean myLocationButtonEnabled = false;
  private boolean trackCameraPosition = false;
  private boolean buildingsEnabled = true;
  private boolean rotateGesturesEnabled = true;
  private boolean zoomGesturesEnabled = true;
  private boolean tiltGesturesEnabled = true;
  private boolean scrollGesturesEnabled = true;
  private boolean poisEnabled = true;
  private boolean disposed = false;
  private final float density;
  private MethodChannel.Result mapReadyResult;

  private Float minZoomPreference = null;
  private Float maxZoomPreference = null;
  private MFMapType mapType;

  private String mapId;

  private String style;

  private MFCameraPosition initialCameraPosition;

  private final FMFCirclesController circlesController;
  private final FMFPolylinesController polylinesController;
  private final FMFPolygonsController polygonsController;
  private final FMFMarkersController markersController;
  private final FMFPOIsController poisController;
  private final FMFBuildingsController buildingsController;
  private final FMFTileOverlaysController tileOverlaysController;
  private final FMFImageOverlaysController imageOverlaysController;
  private final FMFDirectionsRenderersController directionsRenderersController;

  private List<Object> initialCircles;
  private List<Object> initialPolylines;
  private List<Object> initialPolygons;
  private List<Object> initialMarkers;
  private List<Object> initialPOIs;
  private List<Object> initialBuildings;
  private List<Object> initialDirectionsRenderers;
  private List<Map<String, ?>> initialTileOverlays;
  private List<Map<String, ?>> initialImageOverlays;

  FMFMapViewController(
    @NonNull Context context,
    int id,
    BinaryMessenger binaryMessenger,
    LifecycleProvider lifecycleProvider,
    @Nullable MFCameraPosition initialCameraPosition) {
    this.mapView = new MFMapView(context, null);
    this.id = id;
    this.context = context;
    this.density = context.getResources().getDisplayMetrics().density;
    this.methodChannel = new MethodChannel(binaryMessenger, "plugin:map4d-map-view-type_" + id);
    methodChannel.setMethodCallHandler(this);
    this.lifecycleProvider = lifecycleProvider;
    this.initialCameraPosition = initialCameraPosition;
    this.circlesController = new FMFCirclesController(methodChannel, density);
    this.polylinesController = new FMFPolylinesController(methodChannel, density);
    this.polygonsController = new FMFPolygonsController(methodChannel, density);
    this.markersController = new FMFMarkersController(context, methodChannel, density);
    this.poisController = new FMFPOIsController(context, methodChannel, density);
    this.buildingsController = new FMFBuildingsController(methodChannel, density);
    this.tileOverlaysController = new FMFTileOverlaysController(methodChannel);
    this.imageOverlaysController = new FMFImageOverlaysController(methodChannel);
    this.directionsRenderersController = new FMFDirectionsRenderersController(methodChannel, density);
  }

  void init() {
    lifecycleProvider.getLifecycle().addObserver(this);
    this.mapView.getMapAsync(this);
  }

  @Override
  public View getView() {
    return mapView;
  }

  public Map4D getMap() {
    return map4D;
  }

  public Context getContext() {
    return context;
  }

  @Override
  public void dispose() {
    if (disposed) {
      return;
    }
    disposed = true;
    methodChannel.setMethodCallHandler(null);
    setMap4dListener(null);
    destroyMapViewIfNecessary();
    Lifecycle lifecycle = lifecycleProvider.getLifecycle();
    if (lifecycle != null) {
      lifecycle.removeObserver(this);
    }
  }

  @Override
  public void onMapReady(Map4D map4D) {
    this.map4D = map4D;
    initialMapSettings();
    if (mapReadyResult != null) {
      mapReadyResult.success(null);
      mapReadyResult = null;
    }
    setMap4dListener(this);
    circlesController.setMap(map4D);
    polylinesController.setMap(map4D);
    polygonsController.setMap(map4D);
    markersController.setMap(map4D);
    poisController.setMap(map4D);
    buildingsController.setMap(map4D);
    tileOverlaysController.setMap(map4D);
    imageOverlaysController.setMap(map4D);
    directionsRenderersController.setMap(map4D);
    updateInitialCircles();
    updateInitialPolylines();
    updateInitialPolygons();
    updateInitialMarkers();
    updateInitialPOIs();
    updateInitialBuildings();
    updateInitialTileOverlays();
    updateInitialImageOverlays();
    updateInitialDirectionsRenderers();
  }

  private void initialMapSettings() {
    if (initialCameraPosition != null) {
      map4D.moveCamera(MFCameraUpdateFactory.newCameraPosition(initialCameraPosition));
    }
    if (minZoomPreference != null) {
      map4D.setMinZoomPreference(minZoomPreference);
    }
    if (maxZoomPreference != null) {
      map4D.setMaxZoomPreference(maxZoomPreference);
    }
    if (mapType != null) {
      map4D.setMapType(mapType);
    }
    if (mapId != null && !mapId.isEmpty()) {
      map4D.setMapId(mapId);
    }
    if (style != null && !style.isEmpty()) {
      map4D.setMapStyle(new MFMapStyleOptions(style));
    }
    map4D.setBuildingsEnabled(this.buildingsEnabled);
    map4D.setPOIsEnabled(this.poisEnabled);
    map4D.getUiSettings().setZoomGesturesEnabled(zoomGesturesEnabled);
    map4D.getUiSettings().setRotateGesturesEnabled(rotateGesturesEnabled);
    map4D.getUiSettings().setTiltGesturesEnabled(tiltGesturesEnabled);
    map4D.getUiSettings().setScrollGesturesEnabled(scrollGesturesEnabled);
    updateMyLocationSettings();
  }

  private void setMap4dListener(@Nullable FMFMapViewListener listener) {
    map4D.setOnCameraMoveStartedListener(listener);
    map4D.setOnCameraMoveListener(listener);
    map4D.setOnCameraIdleListener(listener);
    map4D.setOnMarkerClickListener(listener);
    map4D.setOnInfoWindowClickListener(listener);
    map4D.setOnMarkerDragListener(listener);
    map4D.setOnPolygonClickListener(listener);
    map4D.setOnPolylineClickListener(listener);
    map4D.setOnCircleClickListener(listener);
    map4D.setOnPOIClickListener(listener);
    map4D.setOnUserPOIClickListener(listener);
    map4D.setOnBuildingClickListener(listener);
    map4D.setOnUserBuildingClickListener(listener);
    map4D.setOnMapClickListener(listener);
    map4D.setOnPlaceClickListener(listener);
    map4D.setOnDirectionsClickListener(listener);
    map4D.setOnDataSourceFeatureClickListener(listener);
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
    switch (call.method) {
      case "map#waitForMap":
        if (map4D != null) {
          result.success(null);
          return;
        }
        mapReadyResult = result;
        break;
      case "camera#move": {
        final MFCameraUpdate cameraUpdate =
          Convert.toCameraUpdate(call.argument("cameraUpdate"), density);
        moveCamera(cameraUpdate);
        result.success(null);
        break;
      }
      case "camera#animate": {
        final MFCameraUpdate cameraUpdate =
          Convert.toCameraUpdate(call.argument("cameraUpdate"), density);
        animateCamera(cameraUpdate);
        result.success(null);
        break;
      }
      case "map#setTime": {
        long timeByMillisecond = call.argument("time");
        Date time = new Date(timeByMillisecond);
        if (map4D != null && time != null) {
          map4D.setTime(time);
        }
        result.success(null);
        break;
      }
      case "map#setSourceOpacity": {
        final String source = call.argument("source");
        final double opacity = call.argument("opacity");
        if (map4D != null) {
          map4D.setSourceOpacity(source, opacity);
        }
        result.success(null);
        break;
      }
      case "map#getCameraPosition": {
        if (map4D != null) {
          result.success(Convert.cameraPositionToJson(getCameraPosition()));
        }
        else {
          result.error(
            "Map4D uninitialized",
            "getCameraPosition called prior to map initialization",
            null);
        }
        break;
      }
      case "map#getBounds": {
        if (map4D != null) {
          final MFCoordinateBounds bounds = map4D.getBounds();
          if (bounds == null) {
            result.success(null);
          }
          else {
            final Map<String, Object> data = new HashMap<>();
            data.put("southwest", Convert.latLngToJson(bounds.getSouthwest()));
            data.put("northeast", Convert.latLngToJson(bounds.getNortheast()));
            result.success(data);
          }
        }
        else {
          result.error(
            "Map4D uninitialized",
            "getBounds called prior to map initialization",
            null);
        }
        break;
      }
      case "map#fitBounds": {
        if (map4D != null) {
          final MFCoordinateBounds bounds = Convert.toCoordinateBounds(call.argument("bounds"));
          final Double padding = call.argument("padding");
          final MFCameraPosition cameraPosition = map4D.getCameraPositionForBounds(bounds, padding.intValue());
          map4D.moveCamera(MFCameraUpdateFactory.newCameraPosition(cameraPosition));
          result.success(null);
        }
        else {
          result.error(
            "Map4D uninitialized",
            "fitBounds called prior to map initialization",
            null);
        }
        break;
      }
      case "map#cameraForBounds": {
        if (map4D != null) {
          final MFCoordinateBounds bounds = Convert.toCoordinateBounds(call.argument("bounds"));
          final Double padding = call.argument("padding");
          MFCameraPosition cameraPosition = map4D.getCameraPositionForBounds(bounds, padding.intValue());
          result.success(Convert.cameraPositionToJson(cameraPosition));
        } else {
          result.error(
            "Map4D uninitialized",
            "cameraForBounds called prior to map initialization",
            null);
        }
        break;
      }
      case "map#update": {
        Convert.interpretMap4dOptions(call.argument("options"), this);
        result.success(Convert.cameraPositionToJson(getCameraPosition()));
        break;
      }
      case "map#isZoomGesturesEnabled": {
        result.success(map4D.getUiSettings().isZoomGesturesEnabled());
        break;
      }
      case "map#isScrollGesturesEnabled": {
        result.success(map4D.getUiSettings().isScrollGesturesEnabled());
        break;
      }
      case "map#isTiltGesturesEnabled": {
        result.success(map4D.getUiSettings().isTiltGesturesEnabled());
        break;
      }
      case "map#isRotateGesturesEnabled": {
        result.success(map4D.getUiSettings().isRotateGesturesEnabled());
        break;
      }
      case "map#isMyLocationButtonEnabled": {
        result.success(map4D.getUiSettings().isMyLocationButtonEnabled());
        break;
      }
      case "map#isBuildingsEnabled": {
        result.success(map4D.isBuildingsEnabled());
        break;
      }
      case "map#isPOIsEnabled": {
        result.success(map4D.isPOIsEnabled());
        break;
      }
      case "map#getZoomLevel": {
        result.success(map4D.getCameraPosition().getZoom());
        break;
      }
      case "map#getScreenCoordinate": {
        if (map4D != null) {
          MFLocationCoordinate coordinate = Convert.hashMapToCoordinate(call.arguments);
          Point screenLocation = map4D.getProjection().pointForCoordinate(coordinate);
          result.success(Convert.pointToJson(screenLocation));
        }
        else {
          result.error(
            "Map4D uninitialized",
            "getScreenCoordinate called prior to map initialization",
            null);
        }
        break;
      }
      case "map#getLatLng": {
        if (map4D != null) {
          Point point = Convert.toPoint(call.arguments);
          MFLocationCoordinate coordinate = map4D.getProjection().coordinateForPoint(point);
          result.success(Convert.latLngToJson(coordinate));
        }
        else {
          result.error(
            "Map4D uninitialized",
            "getLatLng called prior to map initialization",
            null);
        }
        break;
      }
      case "circles#update": {
        List<Object> circlesToAdd = call.argument("circlesToAdd");
        circlesController.addCircles(circlesToAdd);
        List<Object> circlesToChange = call.argument("circlesToChange");
        circlesController.changeCircles(circlesToChange);
        List<Object> circleIdsToRemove = call.argument("circleIdsToRemove");
        circlesController.removeCircles(circleIdsToRemove);
        result.success(null);
        break;
      }
      case "polylines#update": {
        List<Object> polylinesToAdd = call.argument("polylinesToAdd");
        polylinesController.addPolylines(polylinesToAdd);
        List<Object> polylinesToChange = call.argument("polylinesToChange");
        polylinesController.changePolylines(polylinesToChange);
        List<Object> polylineIdsToRemove = call.argument("polylineIdsToRemove");
        polylinesController.removePolylines(polylineIdsToRemove);
        result.success(null);
        break;
      }
      case "polygons#update": {
        List<Object> polygonsToAdd = call.argument("polygonsToAdd");
        polygonsController.addPolygons(polygonsToAdd);
        List<Object> polygonsToChange = call.argument("polygonsToChange");
        polygonsController.changePolygons(polygonsToChange);
        List<Object> polygonIdsToRemove = call.argument("polygonIdsToRemove");
        polygonsController.removePolygons(polygonIdsToRemove);
        result.success(null);
        break;
      }
      case "markers#update": {
        List<Object> markersToAdd = call.argument("markersToAdd");
        markersController.addMarkers(markersToAdd);
        List<Object> markersToChange = call.argument("markersToChange");
        markersController.changeMarkers(markersToChange);
        List<Object> markerIdsToRemove = call.argument("markerIdsToRemove");
        markersController.removeMarkers(markerIdsToRemove);
        result.success(null);
        break;
      }
      case "poi#update": {
        List<Object> poisToAdd = call.argument("poisToAdd");
        poisController.addPOIs(poisToAdd);
        List<Object> poisToChange = call.argument("poisToChange");
        poisController.changePOIs(poisToChange);
        List<Object> poiIdsToRemove = call.argument("poiIdsToRemove");
        poisController.removePOIs(poiIdsToRemove);
        result.success(null);
        break;
      }
      case "building#update": {
        List<Object> buildingsToAdd = call.argument("buildingsToAdd");
        buildingsController.addBuildings(buildingsToAdd);
        List<Object> buildingsToChange = call.argument("buildingsToChange");
        buildingsController.changeBuildings(buildingsToChange);
        List<Object> buildingIdsToRemove = call.argument("buildingIdsToRemove");
        buildingsController.removeBuildings(buildingIdsToRemove);
        result.success(null);
        break;
      }
      case "tileOverlays#update": {
        List<Map<String, ?>> tileOverlaysToAdd = call.argument("tileOverlaysToAdd");
        tileOverlaysController.addTileOverlays(tileOverlaysToAdd);
        List<Map<String, ?>> tileOverlaysToChange = call.argument("tileOverlaysToChange");
        tileOverlaysController.changeTileOverlays(tileOverlaysToChange);
        List<String> tileOverlaysToRemove = call.argument("tileOverlayIdsToRemove");
        tileOverlaysController.removeTileOverlays(tileOverlaysToRemove);
        result.success(null);
        break;
      }
      case "tileOverlays#clearTileCache": {
        String tileOverlayId = call.argument("tileOverlayId");
        tileOverlaysController.clearTileCache(tileOverlayId);
        result.success(null);
        break;
      }
      case "imageOverlays#update": {
        List<Map<String, ?>> imageOverlaysToAdd = call.argument("imageOverlaysToAdd");
        imageOverlaysController.addImageOverlays(imageOverlaysToAdd);
        List<Map<String, ?>> imageOverlaysToChange = call.argument("imageOverlaysToChange");
        imageOverlaysController.changeImageOverlays(imageOverlaysToChange);
        List<String> imageOverlaysToRemove = call.argument("imageOverlayIdsToRemove");
        imageOverlaysController.removeImageOverlays(imageOverlaysToRemove);
        result.success(null);
        break;
      }
      case "directionsRenderers#update": {
        List<Object> directionsRenderersToAdd = call.argument("directionsRenderersToAdd");
        directionsRenderersController.addDirectionsRenderers(directionsRenderersToAdd);
        List<Object> directionsRenderersToChange = call.argument("directionsRenderersToChange");
        directionsRenderersController.changeDirectionsRenderers(directionsRenderersToChange);
        List<Object> directionsRendererIdsToRemove = call.argument("directionsRendererIdsToRemove");
        directionsRenderersController.removeDirectionsRenderers(directionsRendererIdsToRemove);
        result.success(null);
        break;
      }
      case "map#getMetersForLP": {
        double lp = call.argument("lp");
        result.success(map4D.getMeterFromDp(lp));
        break;
      }
      case "map#getLPForMeters": {
        double meters = call.argument("meters");
        result.success(map4D.getDpFromMeter(meters));
        break;
      }
      default:
        result.notImplemented();
        break;
    }
  }

  private void moveCamera(MFCameraUpdate cameraUpdate) {
    map4D.moveCamera(cameraUpdate);
  }

  private void animateCamera(MFCameraUpdate cameraUpdate) {
    map4D.animateCamera(cameraUpdate);
  }

  private MFCameraPosition getCameraPosition() {
    if (map4D == null) {
      return null;
    }
    return map4D.getCameraPosition();
  }

  private void destroyMapViewIfNecessary() {
    if (mapView == null) {
      return;
    }
    mapView.onDestroy();
    mapView = null;
  }

  @SuppressLint("MissingPermission")
  private void updateMyLocationSettings() {
    if (hasLocationPermission()) {
      // The plugin doesn't add the location permission by default so that apps that don't need
      // the feature won't require the permission.
      // Gradle is doing a static check for missing permission and in some configurations will
      // fail the build if the permission is missing. The following disables the Gradle lint.
      // noinspection ResourceType
      map4D.setMyLocationEnabled(myLocationEnabled);
      map4D.getUiSettings().setMyLocationButtonEnabled(myLocationButtonEnabled);
    } else {
      Log.e(TAG, "Cannot enable MyLocation layer as location permissions are not granted");
    }
  }

  private boolean hasLocationPermission() {
    return checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
      == PackageManager.PERMISSION_GRANTED
      || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
      == PackageManager.PERMISSION_GRANTED;
  }

  private int checkSelfPermission(String permission) {
    if (permission == null) {
      throw new IllegalArgumentException("permission is null");
    }
    return context.checkPermission(
      permission, android.os.Process.myPid(), android.os.Process.myUid());
  }

  private void updateInitialCircles() {
    circlesController.addCircles(initialCircles);
  }

  @Override
  public void setMapType(MFMapType mapType) {
    this.mapType = mapType;
    if (map4D == null) {
      return;
    }
    map4D.setMapType(mapType);
  }

  @Override
  public void setMapId(String mapId) {
    this.mapId = mapId;
    if (map4D == null) {
      return;
    }
    map4D.setMapId(mapId);
  }

  @Override
  public void setMapStyle(String style) {
    this.style = style;
    if (map4D == null) {
      return;
    }
    map4D.setMapStyle(new MFMapStyleOptions(style));
  }

  @Override
  public void setMinMaxZoomPreference(Float min, Float max) {
    this.minZoomPreference = min;
    this.maxZoomPreference = max;
    if (map4D == null) {
      return;
    }
    if (min != null) {
      map4D.setMinZoomPreference(min);
    }
    if (max != null) {
      map4D.setMaxZoomPreference(max);
    }
  }

  @Override
  public void setRotateGesturesEnabled(boolean rotateGesturesEnabled) {
    this.rotateGesturesEnabled = rotateGesturesEnabled;
    if (map4D == null) {
      return;
    }
    map4D.getUiSettings().setRotateGesturesEnabled(rotateGesturesEnabled);
  }

  @Override
  public void setScrollGesturesEnabled(boolean scrollGesturesEnabled) {
    this.scrollGesturesEnabled = scrollGesturesEnabled;
    if (map4D == null) {
      return;
    }
    map4D.getUiSettings().setScrollGesturesEnabled(scrollGesturesEnabled);
  }

  @Override
  public void setTiltGesturesEnabled(boolean tiltGesturesEnabled) {
    this.tiltGesturesEnabled = tiltGesturesEnabled;
    if (map4D == null) {
      return;
    }
    map4D.getUiSettings().setTiltGesturesEnabled(tiltGesturesEnabled);
  }

  @Override
  public void setZoomGesturesEnabled(boolean zoomGesturesEnabled) {
    this.zoomGesturesEnabled = zoomGesturesEnabled;
    if (map4D == null) {
      return;
    }
    map4D.getUiSettings().setZoomGesturesEnabled(zoomGesturesEnabled);
  }

  @Override
  public void setTrackCameraPosition(boolean trackCameraPosition) {
    this.trackCameraPosition = trackCameraPosition;
  }

  @Override
  public void setMyLocationEnabled(boolean myLocationEnabled) {
    if (this.myLocationEnabled == myLocationEnabled) {
      return;
    }
    this.myLocationEnabled = myLocationEnabled;
    if (map4D != null) {
      updateMyLocationSettings();
    }
  }

  @Override
  public void setMyLocationButtonEnabled(boolean myLocationButtonEnabled) {
    if (this.myLocationButtonEnabled == myLocationButtonEnabled) {
      return;
    }
    this.myLocationButtonEnabled = myLocationButtonEnabled;
    if (map4D != null) {
      updateMyLocationSettings();
    }
  }

  @Override
  public void setBuildingsEnabled(boolean buildingsEnabled) {
    this.buildingsEnabled = buildingsEnabled;
    if (map4D == null) {
      return;
    }
    map4D.setBuildingsEnabled(buildingsEnabled);
  }

  @Override
  public void setPOIsEnabled(boolean poisEnabled) {
    this.poisEnabled = poisEnabled;
    if (map4D == null) {
      return;
    }
    map4D.setPOIsEnabled(poisEnabled);
  }

  @Override
  public void setInitialCircles(Object initialCircles) {
    ArrayList<?> circles = (ArrayList<?>) initialCircles;
    this.initialCircles = circles != null ? new ArrayList<>(circles) : null;
    if (map4D != null) {
      updateInitialCircles();
    }
  }

  @Override
  public void setInitialPolylines(Object initialPolylines) {
    ArrayList<?> polylines = (ArrayList<?>) initialPolylines;
    this.initialPolylines = polylines != null ? new ArrayList<>(polylines) : null;
    if (map4D != null) {
      updateInitialPolylines();
    }
  }

  private void updateInitialPolylines() {
    polylinesController.addPolylines(initialPolylines);
  }

  @Override
  public void setInitialPolygons(Object initialPolygons) {
    ArrayList<?> polygons = (ArrayList<?>) initialPolygons;
    this.initialPolygons = polygons != null ? new ArrayList<>(polygons) : null;
    if (map4D != null) {
      updateInitialPolygons();
    }
  }

  private void updateInitialPolygons() {
    polygonsController.addPolygons(initialPolygons);
  }


  @Override
  public void setInitialMarkers(Object initialMarkers) {
    ArrayList<?> markers = (ArrayList<?>) initialMarkers;
    this.initialMarkers = markers != null ? new ArrayList<>(markers) : null;
    if (map4D != null) {
      updateInitialMarkers();
    }
  }

  private void updateInitialMarkers() {
    markersController.addMarkers(initialMarkers);
  }

  @Override
  public void setInitialPOIs(Object initialPOIs) {
    ArrayList<?> pois = (ArrayList<?>) initialPOIs;
    this.initialPOIs = pois != null ? new ArrayList<>(pois) : null;
    if (map4D != null) {
      updateInitialPOIs();
    }
  }

  private void updateInitialPOIs() {
    poisController.addPOIs(initialPOIs);
  }

  @Override
  public void setInitialBuildings(Object initialBuildings) {
    ArrayList<?> buildings = (ArrayList<?>) initialBuildings;
    this.initialBuildings = buildings != null ? new ArrayList<>(buildings) : null;
    if (map4D != null) {
      updateInitialBuildings();
    }
  }

  private void updateInitialBuildings() {
    buildingsController.addBuildings(initialBuildings);
  }

  @Override
  public void setInitialDirectionsRenderers(Object initialDirectionsRenderers) {
    ArrayList<?> directionsRenderers = (ArrayList<?>) initialDirectionsRenderers;
    this.initialDirectionsRenderers = directionsRenderers != null ? new ArrayList<>(directionsRenderers) : null;
    if (map4D != null) {
      updateInitialDirectionsRenderers();
    }
  }

  private void updateInitialDirectionsRenderers() {
    directionsRenderersController.addDirectionsRenderers(initialDirectionsRenderers);
  }

  @Override
  public void setInitialTileOverlays(List<Map<String, ?>> initialTileOverlays) {
    this.initialTileOverlays = initialTileOverlays;
    if (map4D != null) {
      updateInitialTileOverlays();
    }
  }

  private void updateInitialTileOverlays() {
    tileOverlaysController.addTileOverlays(initialTileOverlays);
  }

  @Override
  public void setInitialImageOverlays(List<Map<String, ?>> initialImageOverlays) {
    this.initialImageOverlays = initialImageOverlays;
    if (map4D != null) {
      updateInitialImageOverlays();
    }
  }

  private void updateInitialImageOverlays() {
    imageOverlaysController.addImageOverlays(initialImageOverlays);
  }

  @Override
  public void onCameraIdle() {
    methodChannel.invokeMethod("camera#onIdle", Collections.singletonMap("map", id));
  }

  @Override
  public void onCameraMove() {
    if (!trackCameraPosition) {
      return;
    }
    final Map<String, Object> arguments = new HashMap<>(2);
    arguments.put("position", Convert.cameraPositionToJson(map4D.getCameraPosition()));
    methodChannel.invokeMethod("camera#onMove", arguments);
  }

  @Override
  public void onCameraMoveStarted(int reason) {
    final Map<String, Object> arguments = new HashMap<>(2);
    boolean isGesture = reason == Map4D.OnCameraMoveStartedListener.REASON_GESTURE;
    arguments.put("isGesture", isGesture);
    methodChannel.invokeMethod("camera#onMoveStarted", arguments);
  }

  @Override
  public void onCircleClick(MFCircle mfCircle) {
    circlesController.onCircleTap(mfCircle.getId());
  }

  @Override
  public void onInfoWindowClick(@NonNull MFMarker mfMarker) {
    markersController.onInfoWindowTap(mfMarker.getId());
  }

  @Override
  public void onMapClick(MFLocationCoordinate mfLocationCoordinate) {
    final Map<String, Object> arguments = new HashMap<>(2);
    arguments.put("coordinate", Convert.latLngToJson(mfLocationCoordinate));
    methodChannel.invokeMethod("map#didTapAtCoordinate", arguments);
  }

  @Override
  public boolean onMarkerClick(MFMarker mfMarker) {
    final long markerId = mfMarker.getId();
    markersController.onMarkerTap(markerId);
    return false;
  }

  @Override
  public void onMarkerDrag(MFMarker mfMarker) {

  }

  @Override
  public void onMarkerDragEnd(MFMarker mfMarker) {
    markersController.onMarkerDragEnd(mfMarker.getId(), mfMarker.getPosition());
  }

  @Override
  public void onMarkerDragStart(MFMarker mfMarker) {

  }

  @Override
  public void onPolygonClick(MFPolygon mfPolygon) {
    polygonsController.onPolygonTap(mfPolygon.getId());
  }

  @Override
  public void onPolylineClick(MFPolyline mfPolyline) {
    polylinesController.onPolylineTap(mfPolyline.getId());
  }

  @Override
  public void onUserBuildingClick(MFBuilding mfBuilding) {
    buildingsController.onBuildingTap(mfBuilding.getId());
  }

  @Override
  public void onDirectionsClick(MFDirectionsRenderer mfDirectionsRenderer, int index) {
    directionsRenderersController.onDirectionsRendererTap(mfDirectionsRenderer.getId(), index);
  }

  @Override
  public void onUserPOIClick(MFPOI mfpoi) {
    poisController.onPOITap(mfpoi.getId());
  }

  @Override
  public void onBuildingClick(String buildingId, String name, MFLocationCoordinate location) {
    final Map<String, Object> arguments = new HashMap<>(2);
    arguments.put("buildingId", buildingId);
    arguments.put("name", name);
    arguments.put("location", Convert.latLngToJson(location));
    methodChannel.invokeMethod("map#onTapBuilding", arguments);
  }

  @Override
  public void onPOIClick(String placeId, String name, MFLocationCoordinate location) {
    final Map<String, Object> arguments = new HashMap<>(2);
    arguments.put("placeId", placeId);
    arguments.put("name", name);
    arguments.put("location", Convert.latLngToJson(location));
    methodChannel.invokeMethod("map#onTapPOI", arguments);
  }

  @Override
  public void onPlaceClick(@NonNull String name, @NonNull MFLocationCoordinate location) {
    final Map<String, Object> arguments = new HashMap<>(2);
    arguments.put("name", name);
    arguments.put("location", Convert.latLngToJson(location));
    methodChannel.invokeMethod("map#onTapPlace", arguments);
  }

  @Override
  public void onDataSourceFeatureClick(@NonNull MFDataSourceFeature dataSourceFeature, @NonNull MFLocationCoordinate location) {
    final Map<String, Object> feature = new HashMap<>(6);
    feature.put("id", dataSourceFeature.getId());
    feature.put("name", dataSourceFeature.getName());
    feature.put("source", dataSourceFeature.getSource());
    feature.put("sourceLayer", dataSourceFeature.getSourceLayer());
    feature.put("layerType", dataSourceFeature.getLayerType());
    feature.put("properties", dataSourceFeature.getProperties());
    final Map<String, Object> arguments = new HashMap<>(2);
    arguments.put("feature", feature);
    arguments.put("location", Convert.latLngToJson(location));
    methodChannel.invokeMethod("map#onTapDataSourceFeature", arguments);
  }

  // DefaultLifecycleObserver
  @Override
  public void onCreate(@NonNull LifecycleOwner owner) {
    if (disposed) {
      return;
    }
    mapView.onCreate(null);
  }

  @Override
  public void onStart(@NonNull LifecycleOwner owner) {
    if (disposed) {
      return;
    }
    mapView.onStart();
  }

  @Override
  public void onResume(@NonNull LifecycleOwner owner) {
  }

  @Override
  public void onPause(@NonNull LifecycleOwner owner) {
  }

  @Override
  public void onStop(@NonNull LifecycleOwner owner) {
    if (disposed) {
      return;
    }
    mapView.onStop();
  }

  @Override
  public void onDestroy(@NonNull LifecycleOwner owner) {
    owner.getLifecycle().removeObserver(this);
    if (disposed) {
      return;
    }
    destroyMapViewIfNecessary();
  }

  @Override
  public void onSaveInstanceState(@NonNull Bundle bundle) {
    if (disposed) {
      return;
    }
    mapView.onSaveInstanceState(bundle);
  }

  @Override
  public void onRestoreInstanceState(@Nullable Bundle bundle) {
    if (disposed) {
      return;
    }
    mapView.onCreate(bundle);
  }
}
