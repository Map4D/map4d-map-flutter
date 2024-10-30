import 'camera.dart';
import 'location.dart';
import 'data_source_feature.dart';

/// Callback that receives updates to the camera position.
/// This is used in [MFMapView.onCameraMove].
typedef void MFCameraPositionCallback(MFCameraPosition position);

typedef void MFLatLngCallback(MFLatLng coordinate);

typedef void MFMapPOICallback(String placeId, String name, MFLatLng location);

typedef void MFMapBuildingCallback(
    String buildingId, String name, MFLatLng location);

typedef void MFMapPlaceCallback(String name, MFLatLng location);

typedef void MFDirectionsCallback(int routeIndex);

typedef void MFDataSourceFeatureCallback(
    MFDataSourceFeature feature, MFLatLng? location);
