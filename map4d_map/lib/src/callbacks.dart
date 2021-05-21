import 'camera.dart';
import 'location.dart';

/// Callback that receives updates to the camera position.
/// This is used in [MFMapView.onCameraMove].
typedef void MFCameraPositionCallback(MFCameraPosition position);

typedef void MFLatLngCallback(LatLng coordinate);

typedef void MFModeChangedCallback(bool is3DMode);

typedef void MFMapPOICallback(String placeId, String name, LatLng location);

typedef void MFMapBuildingCallback(String buildingId, String name, LatLng location);