## Next

## 3.0.1

* [Android] Support AGP v8

## 3.0.0

Changed the approach to 3D mode.
Instead of requiring the 3D map type to render 3D objects, developers can now manually enable or disable 3D buildings via the `buildingsEnabled` property.
This feature is supported on both `roadmap` and `hybrid` map types, allowing full customization of the map style even when 3D rendering is enabled.

* Support set map style by `style` property of the `MFMapView` widget
* Add `hybrid` map type
* Support update camera with custom padding
* Bugs fixed

### Breaking changes

* The map type `raster`, `map3D` has been removed
* The `enable3DMode` method has been removed from `MFMapViewController`. 3D buildings can now be shown using the `buildingsEnabled` property of the `MFMapView` widget
* The `onModeChange` callback has been removed from the `MFMapView` widget

## 2.6.0

* Add setTime method for MFMapViewController
* [iOS] Remove bitcode

## 2.5.3

* Replace hashValues, hashList by Object.hash, Object.hashAll

## 2.5.2

* Ensure thread safety when cleaning map view instances
* Upgrade Map4dMap SDK dependencies to 2.7.2+ for iOS

## 2.5.1

* Fixed bug map instance was not released properly on iOS

## 2.5.0

* Allow set mapId for `MFMapView`
* Add `onDataSourceFeatureTap` callback for `MFMapView`
* Upgrade Map4dMap SDK dependencies to 2.6+

## 2.4.6

* Fixed bug method `MFMapViewController.getCameraPosition()` return null on Android

## 2.4.5

* Support convert between logical pixels and meters
* Upgrade Map4dMap SDK dependencies to 2.5+
* Fixed bugs

## 2.4.4

* Add gesture recognizers for MapView to move Map inside a Scroll View

## 2.4.3

* Revert lib from Map4dTypesV2 to Map4dTypes for Android platform

## 2.4.2

* Change lib from Map4dTypes to Map4dTypesV2 for Android platform

## 2.4.1

* Remove jcenter() from repositories in build.gradle for Android platform

## 2.4.0

* Add MFImageOverlay to display image layer
* Support transparency for tile overlay
* Fixed bugs

## 2.3.0

* Upgrade Map4dMap SDK dependencies to 2.4.+
* Fixed bugs

## 2.2.2

* Override onPause() and onResume() for lifecycle

## 2.2.1

* Update lifecycle for Android

## 2.2.0

* Upgrade Map4dMap SDK dependencies to 2.1.+
* Add 2 map type: satellite, map 3D
* Deprecated MFMapViewController.enable3DMode and MFMapView.onModeChange
* Fixed bugs & improve performance

## 2.1.3

* Wait for map ready on android

## 2.1.2

* Update Map4dMap SDK dependencies to 2.0.9 and Map4dTypes to 1.1.0

## 2.1.1

* Fixed bug onMapCreated event not working properly on android

## 2.1.0

* Support render directions with MFDirectionsRenderer

## 2.0.1

* Upgrade Map4dMap SDK dependencies to 2.0.x

## 2.0.0

* Upgrade Map4dMap SDK dependencies to 2.0.0
* Render map with vector tiles
* Support setMapType, getBounds for Map View
* Support onPlaceTap event
* Fixed bugs & improve performance

## 1.1.0

* Upgrade Map4dMap SDK dependencies to 1.6.0

## 1.0.0

* Initial release
