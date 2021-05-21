import 'package:collection/collection.dart';
import 'package:flutter/foundation.dart' show listEquals, VoidCallback;
import 'package:flutter/material.dart' show Color, Colors;
import 'package:meta/meta.dart' show immutable;

import 'annotations.dart';

/// Uniquely identifies a [MFPolygon] among [MFMapView] polygons.
///
/// This does not have to be globally unique, only unique among the list.
/// 
class MFPolygonId extends MapsObjectId<MFPolygon> {
  /// Creates an immutable identifier for a [Polygon].
  const MFPolygonId(String value) : super(value);
}

/// Draws a gon through geographical locations on the map.
@immutable
class MFPolygon implements MapsObject {
  /// Creates an immutable object representing a gon drawn through geographical locations on the map.
  const MFPolygon({
    required this.polygonId,
    this.consumeTapEvents = false,
    this.fillColor = Colors.black,
    this.points = const <MFLatLng>[],
    this.holes = const <List<MFLatLng>>[],
    this.strokeColor = Colors.black,
    this.strokeWidth = 10,
    this.visible = true,
    this.zIndex = 0,
    this.onTap,
  });

  /// Uniquely identifies a [Polygon].
  final MFPolygonId polygonId;

  @override
  MFPolygonId get mapsId => polygonId;

  /// True if the [Polygon] consumes tap events.
  ///
  /// If this is false, [onTap] callback will not be triggered.
  final bool consumeTapEvents;

  /// Fill color in ARGB format, the same format used by Color. The default value is black (0xff000000).
  final Color fillColor;

  /// The vertices of the polygon to be drawn.
  ///
  /// Line segments are drawn between consecutive points. A polygon is not closed by
  /// default; to form a closed polygon, the start and end points must be the same.
  final List<MFLatLng> points;

  /// To create an empty area within a polygon, you need to use holes.
  /// To create the hole, the coordinates defining the hole path must be inside the polygon.
  ///
  /// The vertices of the holes to be cut out of polygon.
  ///
  /// Line segments of each points of hole are drawn inside polygon between consecutive hole points.
  final List<List<MFLatLng>> holes;

  /// True if the marker is visible.
  final bool visible;

  /// Line color in ARGB format, the same format used by Color. The default value is black (0xff000000).
  final Color strokeColor;

  /// Width of the polygon, used to define the width of the line to be drawn.
  ///
  /// The width is constant and independent of the camera's zoom level.
  /// The default value is 10.
  final int strokeWidth;

  /// The z-index of the polygon, used to determine relative drawing order of
  /// map overlays.
  ///
  /// Overlays are drawn in order of z-index, so that lower values means drawn
  /// earlier, and thus appearing to be closer to the surface of the Earth.
  final int zIndex;

  /// Callbacks to receive tap events for polygon placed on this map.
  final VoidCallback? onTap;

  /// Creates a new [Polygon] object whose values are the same as this instance,
  /// unless overwritten by the specified parameters.
  MFPolygon copyWith({
    bool? consumeTapEventsParam,
    Color? fillColorParam,
    List<MFLatLng>? pointsParam,
    List<List<MFLatLng>>? holesParam,
    Color? strokeColorParam,
    int? strokeWidthParam,
    bool? visibleParam,
    int? zIndexParam,
    VoidCallback? onTapParam,
  }) {
    return MFPolygon(
      polygonId: polygonId,
      consumeTapEvents: consumeTapEventsParam ?? consumeTapEvents,
      fillColor: fillColorParam ?? fillColor,
      points: pointsParam ?? points,
      holes: holesParam ?? holes,
      strokeColor: strokeColorParam ?? strokeColor,
      strokeWidth: strokeWidthParam ?? strokeWidth,
      visible: visibleParam ?? visible,
      onTap: onTapParam ?? onTap,
      zIndex: zIndexParam ?? zIndex,
    );
  }

  /// Creates a new [MFPolygon] object whose values are the same as this
  /// instance.
  MFPolygon clone() {
    return copyWith(
      pointsParam: List<MFLatLng>.of(points),
    );
  }

  /// Converts this object to something serializable in JSON.
  /// Converts this object to something serializable in JSON.
  Object toJson() {
    final Map<String, Object> json = <String, Object>{};

    void addIfPresent(String fieldName, Object? value) {
      if (value != null) {
        json[fieldName] = value;
      }
    }

    addIfPresent('polygonId', polygonId.value);
    addIfPresent('consumeTapEvents', consumeTapEvents);
    addIfPresent('fillColor', fillColor.value);
    addIfPresent('strokeColor', strokeColor.value);
    addIfPresent('strokeWidth', strokeWidth);
    addIfPresent('visible', visible);
    addIfPresent('zIndex', zIndex);

    if (points != null) {
      json['points'] = _pointsToJson();
    }

    if (holes != null) {
      json['holes'] = _holesToJson();
    }

    return json;
  }

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) return true;
    if (other.runtimeType != runtimeType) return false;
    final MFPolygon typedOther = other as MFPolygon;
    return polygonId == typedOther.polygonId &&
        consumeTapEvents == typedOther.consumeTapEvents &&
        fillColor == typedOther.fillColor &&
        listEquals(points, typedOther.points) &&
        const DeepCollectionEquality().equals(holes, typedOther.holes) &&
        visible == typedOther.visible &&
        strokeColor == typedOther.strokeColor &&
        strokeWidth == typedOther.strokeWidth &&
        zIndex == typedOther.zIndex;
  }

  @override
  int get hashCode => polygonId.hashCode;

  Object _pointsToJson() {
    final List<Object> result = <Object>[];
    for (final MFLatLng point in points) {
      result.add(point.toJson());
    }
    return result;
  }

  List<List<Object>> _holesToJson() {
    final List<List<Object>> result = <List<Object>>[];
    for (final List<MFLatLng> hole in holes) {
      final List<Object> jsonHole = <Object>[];
      for (final MFLatLng point in hole) {
        jsonHole.add(point.toJson());
      }
      result.add(jsonHole);
    }
    return result;
  }
}
