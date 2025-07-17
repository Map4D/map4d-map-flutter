package vn.map4d.map.map4d_map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.flutter.embedding.engine.loader.FlutterLoader;
import vn.map4d.map.annotations.MFBitmapDescriptor;
import vn.map4d.map.annotations.MFBitmapDescriptorFactory;
import vn.map4d.map.annotations.MFDashPattern;
import vn.map4d.map.annotations.MFPatternItem;
import vn.map4d.map.annotations.MFSolidPattern;
import vn.map4d.map.camera.MFCameraPosition;
import vn.map4d.map.camera.MFCameraUpdate;
import vn.map4d.map.camera.MFCameraUpdateFactory;
import vn.map4d.map.core.MFCoordinateBounds;
import vn.map4d.map.core.MFMapType;
import vn.map4d.types.MFLocationCoordinate;

/** Conversions between JSON-like values and Map4D data types. **/
class Convert {
  private static FlutterLoader flutterLoader;

  static void setFlutterLoader(FlutterLoader loader) {
    flutterLoader = loader;
  }


  // TODO : FlutterMain has been deprecated and should be replaced with FlutterLoader
  //  when it's available in Stable channel: https://github.com/flutter/flutter/issues/70923.
  @SuppressWarnings("deprecation")
  private static MFBitmapDescriptor toBitmapDescriptor(Object o) {
    final List<?> data = toList(o);
    switch (toString(data.get(0))) {
      case "defaultMarker":
        return MFBitmapDescriptorFactory.defaultMarker();
      case "fromAssetImage":
        if (data.size() == 3) {
          return MFBitmapDescriptorFactory.fromAsset(flutterLoader.getLookupKeyForAsset(toString(data.get(1))));
        } else {
          throw new IllegalArgumentException(
                  "'fromAssetImage' Expected exactly 3 arguments, got: " + data.size());
        }
      case "fromBytes":
        return getBitmapFromBytes(data);
      default:
        throw new IllegalArgumentException("Cannot interpret " + o + " as BitmapDescriptor");
    }
  }

  private static MFBitmapDescriptor getBitmapFromBytes(List<?> data) {
    if (data.size() == 2) {
      try {
        Bitmap bitmap = toBitmap(data.get(1));
        return MFBitmapDescriptorFactory.fromBitmap(bitmap);
      } catch (Exception e) {
        throw new IllegalArgumentException("Unable to interpret bytes as a valid image.", e);
      }
    } else {
      throw new IllegalArgumentException(
              "fromBytes should have exactly one argument, interpretTileOverlayOptions the bytes. Got: "
                      + data.size());
    }
  }

  private static Bitmap toBitmap(Object o) {
    byte[] bmpData = (byte[]) o;
    Bitmap bitmap = BitmapFactory.decodeByteArray(bmpData, 0, bmpData.length);
    if (bitmap == null) {
      throw new IllegalArgumentException("Unable to decode bytes as a valid bitmap.");
    } else {
      return bitmap;
    }
  }

  static Object latLngToJson(MFLocationCoordinate coordinate) {
    return Arrays.asList(coordinate.getLatitude(), coordinate.getLongitude());
  }

  private static List<?> toList(Object o) {
    return (List<?>) o;
  }

  private static String toString(Object o) {
    return (String) o;
  }

  private static double toDouble(Object o) {
    return ((Number) o).doubleValue();
  }

  private static int toInt(Object o) {
    return ((Number) o).intValue();
  }

  private static float toFloat(Object o) {
    return ((Number) o).floatValue();
  }

  private static Float toFloatWrapper(Object o) {
    return (o == null) ? null : toFloat(o);
  }

  private static boolean toBoolean(Object o) {
    return (Boolean) o;
  }

  private static Map<?, ?> toMap(Object o) {
    return (Map<?, ?>) o;
  }

  private static float toFractionalPixels(Object o, float density) {
    return toFloat(o) * density;
  }

  private static int toPixels(Object o, float density) {
    return (int) toFractionalPixels(o, density);
  }

  private static Map<String, Object> toObjectMap(Object o) {
    Map<String, Object> hashMap = new HashMap<>();
    Map<?, ?> map = (Map<?, ?>) o;
    for (Object key : map.keySet()) {
      Object object = map.get(key);
      if (object != null) {
        hashMap.put((String) key, object);
      }
    }
    return hashMap;
  }

  private static List<MFLocationCoordinate> toPoints(Object o) {
    final List<?> data = toList(o);
    final List<MFLocationCoordinate> points = new ArrayList<>(data.size());

    for (Object rawPoint : data) {
      final List<?> point = toList(rawPoint);
      points.add(new MFLocationCoordinate(toFloat(point.get(0)), toFloat(point.get(1))));
    }
    return points;
  }

  private static List<List<MFLocationCoordinate>> toHoles(Object o) {
    final List<?> data = toList(o);
    final List<List<MFLocationCoordinate>> holes = new ArrayList<>(data.size());

    for (Object rawHole : data) {
      holes.add(toPoints(rawHole));
    }
    return holes;
  }

  static MFLocationCoordinate toCoordinate(Object o) {
    final List<?> data = toList(o);
    return new MFLocationCoordinate(toDouble(data.get(0)), toDouble(data.get(1)));
  }

  static MFLocationCoordinate hashMapToCoordinate(Object o) {
    final Map<?, ?> data = toMap(o);
    final Object coordinate = data.get("latLng");
    Object lat = null;
    Object lng = null;
    if (coordinate != null) {
      lat = toList(coordinate).get(0);
      lng = toList(coordinate).get(1);
    }
    return new MFLocationCoordinate(toDouble(lat), toDouble(lng));
  }

  static Point toPoint(Object o) {
    final Map<?, ?> data = toMap(o);
    final Object coordinate = data.get("coordinate");
    Object x = null;
    Object y = null;
    if (coordinate != null) {
      x = toMap(coordinate).get("x");
      y = toMap(coordinate).get("y");
    }
    return new Point((int) x, (int) y);
  }

  static Map<String, Integer> pointToJson(Point point) {
    final Map<String, Integer> data = new HashMap<>(2);
    data.put("x", point.x);
    data.put("y", point.y);
    return data;
  }

  static MFMapType toMapType(Object o) {
    final int mapType = toInt(o);
    switch (mapType) {
      case 0:
        return MFMapType.ROADMAP;
      case 1:
        return MFMapType.SATELLITE;
      default:
        return MFMapType.HYBRID;
    }
  }

  static MFPatternItem toPolylinePattern(Object o, int width) {
    final int style = toInt(o);
    switch (style) {
      case 1:
        return new MFDashPattern(width, width);
      default:
        return new MFSolidPattern();
    }
  }

  static void interpretMap4dOptions(Object o, FMFMapViewOptionsSink sink) {
    final Map<?, ?> data = toMap(o);
    final Object minMaxZoomPreference = data.get("minMaxZoomPreference");
    if (minMaxZoomPreference != null) {
      final List<?> zoomPreferenceData = toList(minMaxZoomPreference);
      sink.setMinMaxZoomPreference( //
              toFloatWrapper(zoomPreferenceData.get(0)), //
              toFloatWrapper(zoomPreferenceData.get(1)));
    }
    final Object mapType = data.get("mapType");
    if (mapType != null) {
      sink.setMapType(toMapType(mapType));
    }
    final Object mapId = data.get("mapId");
    if (mapId != null) {
      sink.setMapId(toString(mapId));
    }
    final Object style = data.get("style");
    if (style != null) {
      sink.setMapStyle(toString(style));
    }
    final Object rotateGesturesEnabled = data.get("rotateGesturesEnabled");
    if (rotateGesturesEnabled != null) {
      sink.setRotateGesturesEnabled(toBoolean(rotateGesturesEnabled));
    }
    final Object scrollGesturesEnabled = data.get("scrollGesturesEnabled");
    if (scrollGesturesEnabled != null) {
      sink.setScrollGesturesEnabled(toBoolean(scrollGesturesEnabled));
    }
    final Object tiltGesturesEnabled = data.get("tiltGesturesEnabled");
    if (tiltGesturesEnabled != null) {
      sink.setTiltGesturesEnabled(toBoolean(tiltGesturesEnabled));
    }
    final Object zoomGesturesEnabled = data.get("zoomGesturesEnabled");
    if (zoomGesturesEnabled != null) {
      sink.setZoomGesturesEnabled(toBoolean(zoomGesturesEnabled));
    }
    final Object trackCameraPosition = data.get("trackCameraPosition");
    if (trackCameraPosition != null) {
      sink.setTrackCameraPosition(toBoolean(trackCameraPosition));
    }
    final Object myLocationEnabled = data.get("myLocationEnabled");
    if (myLocationEnabled != null) {
      sink.setMyLocationEnabled(toBoolean(myLocationEnabled));
    }
    final Object myLocationButtonEnabled = data.get("myLocationButtonEnabled");
    if (myLocationButtonEnabled != null) {
      sink.setMyLocationButtonEnabled(toBoolean(myLocationButtonEnabled));
    }
    final Object buildingsEnabled = data.get("buildingsEnabled");
    if (buildingsEnabled != null) {
      sink.setBuildingsEnabled(toBoolean(buildingsEnabled));
    }
    final Object poisEnabled = data.get("poisEnabled");
    if (poisEnabled != null) {
      sink.setPOIsEnabled(toBoolean(poisEnabled));
    }
  }

  static MFCameraUpdate toCameraUpdate(Object o, float density) {
    final List<?> data = toList(o);
    switch (toString(data.get(0))) {
      case "newCameraPosition":
        return MFCameraUpdateFactory.newCameraPosition(toCameraPosition(data.get(1)));
      case "newLatLngBounds":
        return MFCameraUpdateFactory.newCoordinateBounds(
                toCoordinateBounds(data.get(1)), toPixels(data.get(2), density));
      case "newLatLngBoundsWithPadding":
        return MFCameraUpdateFactory.newCoordinateBounds(
                toCoordinateBounds(data.get(1)),
                toPixels(data.get(2), density),
                toPixels(data.get(3), density),
                toPixels(data.get(4), density),
                toPixels(data.get(5), density)
        );
      case "newLatLngZoom":
        return MFCameraUpdateFactory.newCoordinateZoom(toCoordinate(data.get(1)), toFloat(data.get(2)));
      case "newLatLng":
        return MFCameraUpdateFactory.newCoordinate(toCoordinate(data.get(1)));
      case "zoomIn":
        return MFCameraUpdateFactory.zoomIn();
      case "zoomOut":
        return MFCameraUpdateFactory.zoomOut();
      case "zoomTo":
        return MFCameraUpdateFactory.zoomTo(toFloat(data.get(1)));
      default:
        throw new IllegalArgumentException("Cannot interpret " + o + " as CameraUpdate");
    }
  }

  static Object cameraPositionToJson(MFCameraPosition position) {
    if (position == null) {
      return null;
    }
    final Map<String, Object> data = new HashMap<>();
    data.put("bearing", position.getBearing());
    data.put("target", latLngToJson(position.getTarget()));
    data.put("tilt", position.getTilt());
    data.put("zoom", position.getZoom());
    return data;
  }

  static MFCameraPosition toCameraPosition(Object o) {
    final Map<?, ?> data = toMap(o);
    final MFCameraPosition.Builder builder = new MFCameraPosition.Builder();
    builder.bearing(toDouble(data.get("bearing")));
    builder.target(toCoordinate(data.get("target")));
    builder.tilt(toDouble(data.get("tilt")));
    builder.zoom(toDouble(data.get("zoom")));
    return builder.build();
  }

  static MFCoordinateBounds toCoordinateBounds(Object o) {
    if (o == null) {
      return null;
    }
    final List<?> data = toList(o);
    return new MFCoordinateBounds(toCoordinate(data.get(0)), toCoordinate(data.get(1)));
  }

  static Object polylineIdToJson(String polylineId) {
    if (polylineId == null) {
      return null;
    }
    final Map<String, Object> data = new HashMap<>(1);
    data.put("polylineId", polylineId);
    return data;
  }

  static Object polygonIdToJson(String polygonId) {
    if (polygonId == null) {
      return null;
    }
    final Map<String, Object> data = new HashMap<>(1);
    data.put("polygonId", polygonId);
    return data;
  }

  static Object markerIdToJson(String markerId) {
    if (markerId == null) {
      return null;
    }
    final Map<String, Object> data = new HashMap<>(1);
    data.put("markerId", markerId);
    return data;
  }

  static Object circleIdToJson(String circleId) {
    if (circleId == null) {
      return null;
    }
    final Map<String, Object> data = new HashMap<>(1);
    data.put("circleId", circleId);
    return data;
  }

  static Object poiIdToJson(String poiId) {
    if (poiId == null) {
      return null;
    }
    final Map<String, Object> data = new HashMap<>(1);
    data.put("poiId", poiId);
    return data;
  }

  static Object buildingIdToJson(String buildingId) {
    if (buildingId == null) {
      return null;
    }
    final Map<String, Object> data = new HashMap<>(1);
    data.put("buildingId", buildingId);
    return data;
  }

  static Object directionsRendererIdAndActiveIndexToJson(String directionsRendererId, int index) {
    if (directionsRendererId == null) {
      return null;
    }
    final Map<String, Object> data = new HashMap<>(2);
    data.put("rendererId", directionsRendererId);
    data.put("routeIndex", index);
    return data;
  }

  static String interpretDirectionsRendererOptions(Object o, FMFDirectionsRendererOptionsSink sink) {
    final Map<?, ?> data = toMap(o);
    final Object originPOIOptions = data.get("originPOIOptions");
    if (originPOIOptions != null) {
      final Map<?, ?> originPOIData = toMap(originPOIOptions);
      final Object position = originPOIData.get("position");
      if (position != null) {
        sink.setStartLocation(toCoordinate(position));
      }
      final Object icon = originPOIData.get("icon");
      if (icon != null) {
        sink.setStartIcon(toBitmapDescriptor(icon));
      }
      final Object title = originPOIData.get("title");
      if (title != null) {
        sink.setStartLabel(toString(title));
      }
      final Object titleColor = originPOIData.get("titleColor");
      if (titleColor != null) {
        sink.setTitleColor(toInt(titleColor));
      }
      final Object visible = originPOIData.get("visible");
      if (visible != null) {
        sink.setOriginPOIVisible(toBoolean(visible));
      }
    }
    final Object destinationPOIOptions = data.get("destinationPOIOptions");
    if (destinationPOIOptions != null) {
      final Map<?, ?> destinationPOIData = toMap(destinationPOIOptions);
      final Object position = destinationPOIData.get("position");
      if (position != null) {
        sink.setEndLocation(toCoordinate(position));
      }
      final Object icon = destinationPOIData.get("icon");
      if (icon != null) {
        sink.setEndIcon(toBitmapDescriptor(icon));
      }
      final Object title = destinationPOIData.get("title");
      if (title != null) {
        sink.setEndLabel(toString(title));
      }
      final Object titleColor = destinationPOIData.get("titleColor");
      if (titleColor != null) {
        // Update late when update Map SDK
      }
      final Object visible = destinationPOIData.get("visible");
      if (visible != null) {
        sink.setDestinationPOIVisible(toBoolean(visible));
      }
    }
    final Object consumeTapEvents = data.get("consumeTapEvents");
    if (consumeTapEvents != null) {
      sink.setConsumeTapEvents(toBoolean(consumeTapEvents));
    }
    final Object directions = data.get("directions");
    if (directions != null) {
      final String jsonData = toString(directions);
      if (!jsonData.isEmpty()) {
        sink.setJsonData(jsonData);
      }
    }
    final Object activedIndex = data.get("activedIndex");
    if (activedIndex != null) {
      sink.setActivedIndex(toInt(activedIndex));
    }
    final Object activeStrokeColor = data.get("activeStrokeColor");
    if (activeStrokeColor != null) {
      sink.setActiveStrokeColor(toInt(activeStrokeColor));
    }
    final Object activeOutlineColor = data.get("activeOutlineColor");
    if (activeOutlineColor != null) {
      sink.setActiveOutlineColor(toInt(activeOutlineColor));
    }
    final Object inactiveStrokeColor = data.get("inactiveStrokeColor");
    if (inactiveStrokeColor != null) {
      sink.setInactiveStrokeColor(toInt(inactiveStrokeColor));
    }
    final Object inactiveOutlineColor = data.get("inactiveOutlineColor");
    if (inactiveOutlineColor != null) {
      sink.setInactiveOutlineColor(toInt(inactiveOutlineColor));
    }
    final Object width = data.get("activeStrokeWidth");
    if (width != null) {
      sink.setWidth(toInt(width));
    }
    final Object paths = data.get("routes");
    if (paths != null) {
      sink.setPaths(toHoles(paths));
    }
    final String directionsRendererId = (String) data.get("rendererId");
    if (directionsRendererId == null) {
      throw new IllegalArgumentException("directionsRendererId was null");
    } else {
      return directionsRendererId;
    }
  }

  static String interpretPolylineOptions(Object o, FMFPolylineOptionsSink sink) {
    final Map<?, ?> data = toMap(o);
    final Object consumeTapEvents = data.get("consumeTapEvents");
    if (consumeTapEvents != null) {
      sink.setConsumeTapEvents(toBoolean(consumeTapEvents));
    }
    final Object color = data.get("color");
    if (color != null) {
      sink.setColor(toInt(color));
    }
    final Object visible = data.get("visible");
    if (visible != null) {
      sink.setVisible(toBoolean(visible));
    }
    final Object widthObject = data.get("width");
    Integer width = null;
    if (widthObject != null) {
      width = toInt(widthObject);
      sink.setWidth(width.floatValue());
    }
    final Object style = data.get("style");
    if (style != null) {
      sink.setPattern(toPolylinePattern(style, width != null ? width.intValue() : 10 /** 10 is default width **/));
    }
    final Object zIndex = data.get("zIndex");
    if (zIndex != null) {
      sink.setZIndex(toFloat(zIndex));
    }
    final Object points = data.get("points");
    if (points != null) {
      sink.setPoints(toPoints(points));
    }
    final String polylineId = (String) data.get("polylineId");
    if (polylineId == null) {
      throw new IllegalArgumentException("polylineId was null");
    } else {
      return polylineId;
    }
  }

  static String interpretPolygonOptions(Object o, FMFPolygonOptionsSink sink) {
    final Map<?, ?> data = toMap(o);
    final Object consumeTapEvents = data.get("consumeTapEvents");
    if (consumeTapEvents != null) {
      sink.setConsumeTapEvents(toBoolean(consumeTapEvents));
    }
    final Object visible = data.get("visible");
    if (visible != null) {
      sink.setVisible(toBoolean(visible));
    }
    final Object zIndex = data.get("zIndex");
    if (zIndex != null) {
      sink.setZIndex(toFloat(zIndex));
    }
    final Object fillColor = data.get("fillColor");
    if (fillColor != null) {
      sink.setFillColor(toInt(fillColor));
    }
    final Object strokeColor = data.get("strokeColor");
    if (strokeColor != null) {
      sink.setStrokeColor(toInt(strokeColor));
    }
    final Object strokeWidth = data.get("strokeWidth");
    if (strokeWidth != null) {
      sink.setStrokeWidth(toFloat(strokeWidth));
    }
    final Object points = data.get("points");
    if (points != null) {
      sink.setPoints(toPoints(points));
    }
    final Object holes = data.get("holes");
    if (holes != null) {
      sink.setHoles(toHoles(holes));
    }
    final String polygonId = (String) data.get("polygonId");
    if (polygonId == null) {
      throw new IllegalArgumentException("polygonId was null");
    } else {
      return polygonId;
    }
  }

  static String interpretCircleOptions(Object o, FMFCircleOptionsSink sink) {
    final Map<?, ?> data = toMap(o);
    final Object consumeTapEvents = data.get("consumeTapEvents");
    if (consumeTapEvents != null) {
      sink.setConsumeTapEvents(toBoolean(consumeTapEvents));
    }
    final Object fillColor = data.get("fillColor");
    if (fillColor != null) {
      sink.setFillColor(toInt(fillColor));
    }
    final Object strokeColor = data.get("strokeColor");
    if (strokeColor != null) {
      sink.setStrokeColor(toInt(strokeColor));
    }
    final Object visible = data.get("visible");
    if (visible != null) {
      sink.setVisible(toBoolean(visible));
    }
    final Object strokeWidth = data.get("strokeWidth");
    if (strokeWidth != null) {
      sink.setStrokeWidth(toInt(strokeWidth));
    }
    final Object zIndex = data.get("zIndex");
    if (zIndex != null) {
      sink.setZIndex(toFloat(zIndex));
    }
    final Object center = data.get("center");
    if (center != null) {
      sink.setCenter(toCoordinate(center));
    }
    final Object radius = data.get("radius");
    if (radius != null) {
      sink.setRadius(toDouble(radius));
    }
    final String circleId = String.valueOf(data.get("circleId"));
    if (circleId == null) {
      throw new IllegalArgumentException("circleId was null");
    } else {
      return circleId;
    }
  }

  static String interpretMarkerOptions(Object o, FMFMarkerOptionsSink sink) {
    final Map<?, ?> data = toMap(o);
    final Object consumeTapEvents = data.get("consumeTapEvents");
    if (consumeTapEvents != null) {
      sink.setConsumeTapEvents(toBoolean(consumeTapEvents));
    }
    final Object position = data.get("position");
    if (position != null) {
      sink.setPosition(toCoordinate(position));
    }
    final Object elevation = data.get("elevation");
    if (elevation != null) {
      sink.setElevation(toDouble(elevation));
    }
    final Object rotation = data.get("rotation");
    if (rotation != null) {
      sink.setRotation(toDouble(rotation));
    }
    final Object anchor = data.get("anchor");
    if (anchor != null) {
      final List<?> anchorData = toList(anchor);
      sink.setAnchor(toFloat(anchorData.get(0)), toFloat(anchorData.get(1)));
    }
    final Object icon = data.get("icon");
    if (icon != null) {
      sink.setIcon(toBitmapDescriptor(icon));
    }
    final Object draggable = data.get("draggable");
    if (draggable != null) {
      sink.setDraggable(toBoolean(draggable));
    }
    final Object visible = data.get("visible");
    if (visible != null) {
      sink.setVisible(toBoolean(visible));
    }
    final Object zIndex = data.get("zIndex");
    if (zIndex != null) {
      sink.setZIndex(toFloat(zIndex));
    }
    final Object infoWindow = data.get("infoWindow");
    if (infoWindow != null) {
      interpretInfoWindowOptions(sink, toObjectMap(infoWindow));
    }
    final String markerId = (String) data.get("markerId");
    if (markerId == null) {
      throw new IllegalArgumentException("markerId was null");
    } else {
      return markerId;
    }
  }

  private static void interpretInfoWindowOptions(
          FMFMarkerOptionsSink sink, Map<String, Object> infoWindow) {
    String title = (String) infoWindow.get("title");
    String snippet = (String) infoWindow.get("snippet");
    if (title != null) {
      sink.setTitle(title);
    }
    if (snippet != null) {
      sink.setSnippet(snippet);
    }

    Object infoWindowAnchor = infoWindow.get("anchor");
    if (infoWindowAnchor != null) {
      final List<?> anchorData = toList(infoWindowAnchor);
      sink.setWindowAnchor(toFloat(anchorData.get(0)), toFloat(anchorData.get(1)));
    }
  }

  static String interpretPOIOptions(Object o, FMFPOIOptionsSink sink) {
    final Map<?, ?> data = toMap(o);
    final Object consumeTapEvents = data.get("consumeTapEvents");
    if (consumeTapEvents != null) {
      sink.setConsumeTapEvents(toBoolean(consumeTapEvents));
    }
    final Object visible = data.get("visible");
    if (visible != null) {
      sink.setVisible(toBoolean(visible));
    }
    final Object zIndex = data.get("zIndex");
    if (zIndex != null) {
      sink.setZIndex(toFloat(zIndex));
    }
    final Object type = data.get("type");
    if (type != null) {
      sink.setType(toString(type));
    }
    final Object title = data.get("title");
    if (title != null) {
      sink.setTitle(toString(title));
    }
    final Object titleColor = data.get("titleColor");
    if (titleColor != null) {
      sink.setTitleColor(toInt(titleColor));
    }
    final Object position = data.get("position");
    if (position != null) {
      sink.setPosition(toCoordinate(position));
    }
    final Object icon = data.get("icon");
    if (icon != null) {
      sink.setIcon(toBitmapDescriptor(icon));
    }
    final String poiId = (String) data.get("poiId");
    if (poiId == null) {
      throw new IllegalArgumentException("poiId was null");
    } else {
      return poiId;
    }
  }

  static String interpretBuildingOptions(Object o, FMFBuildingOptionsSink sink) {
    final Map<?, ?> data = toMap(o);
    final Object consumeTapEvents = data.get("consumeTapEvents");
    if (consumeTapEvents != null) {
      sink.setConsumeTapEvents(toBoolean(consumeTapEvents));
    }
    final Object location = data.get("position");
    if (location != null) {
      sink.setLocation(toCoordinate(location));
    }
    final Object coordinates = data.get("coordinates");
    if (coordinates != null) {
      List<MFLocationCoordinate> points = toPoints(coordinates);
      if (points.size() > 0) {
        sink.setModel(toPoints(coordinates));
      }
    }
    final Object name = data.get("name");
    if (name != null) {
      sink.setName(toString(name));
    }
    final Object modelUrl = data.get("modelUrl");
    if (modelUrl != null) {
      sink.setModel(toString(modelUrl));
    }
    final Object textureUrl = data.get("textureUrl");
    if (textureUrl != null) {
      sink.setTexture(toString(textureUrl));
    }
    final Object height = data.get("height");
    if (height != null) {
      sink.setHeight(toDouble(height));
    }
    final Object scale = data.get("scale");
    if (scale != null) {
      sink.setScale(toDouble(scale));
    }
    final Object bearing = data.get("bearing");
    if (bearing != null) {
      sink.setBearing(toDouble(bearing));
    }
    final Object elevation = data.get("elevation");
    if (elevation != null) {
      sink.setElevation(toFloat(elevation));
    }
    final Object visible = data.get("visible");
    if (visible != null) {
      sink.setVisible(toBoolean(visible));
    }
    final Object selected = data.get("selected");
    if (selected != null) {
      sink.setSelected(toBoolean(selected));
    }
    final String buildingId = (String) data.get("buildingId");
    if (buildingId == null) {
      throw new IllegalArgumentException("buildingId was null");
    } else {
      return buildingId;
    }
  }

  static String interpretTileOverlayOptions(Map<String, ?> data, FMFTileOverlaySink sink) {
    final Object zIndex = data.get("zIndex");
    if (zIndex != null) {
      sink.setZIndex(toFloat(zIndex));
    }
    final Object visible = data.get("visible");
    if (visible != null) {
      sink.setVisible(toBoolean(visible));
    }
    final Object transparency = data.get("transparency");
    if (transparency != null) {
      sink.setOpacity(1.f - toFloat(transparency));
    }
    final Object urlPattern = data.get("urlPattern");
    if (urlPattern != null) {
      sink.setUrlPattern(toString(urlPattern));
    }
    final String tileOverlayId = (String) data.get("tileOverlayId");
    if (tileOverlayId == null) {
      throw new IllegalArgumentException("tileOverlayId was null");
    } else {
      return tileOverlayId;
    }
  }

  static String interpretImageOverlayOptions(Map<String, ?> data, FMFImageOverlaySink sink) {
    final Object bounds = data.get("bounds");
    if (bounds != null) {
      sink.setBounds(toCoordinateBounds(bounds));
    }
    final Object image = data.get("image");
    if (image != null) {
      sink.setImage(toBitmapDescriptor(image));
    }
    final Object zIndex = data.get("zIndex");
    if (zIndex != null) {
      sink.setZIndex(toFloat(zIndex));
    }
    final Object visible = data.get("visible");
    if (visible != null) {
      sink.setVisible(toBoolean(visible));
    }
    final Object transparency = data.get("transparency");
    if (transparency != null) {
      sink.setOpacity(1.f - toFloat(transparency));
    }
    final String imageOverlayId = (String) data.get("imageOverlayId");
    if (imageOverlayId == null) {
      throw new IllegalArgumentException("imageOverlayId was null");
    } else {
      return imageOverlayId;
    }
  }
}
