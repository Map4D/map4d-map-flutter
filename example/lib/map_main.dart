import 'package:flutter/material.dart';
import 'dart:async';

import 'package:map4d_map/map4d_map.dart';
import 'page.dart';

class Map4dApp extends Map4dMapExampleAppPage {
  Map4dApp() : super(const Icon(Icons.map), 'Map4dMap SDK');

  @override
  Widget build(BuildContext context) {
    return const MyApp();
  }
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();

  const MyApp();
}

class _MyAppState extends State<MyApp> {
  bool _isMapCreated = false;
  late MFMapViewController _controller;

  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    final position =
        MFCameraPosition(target: MFLatLng(21.030041, 105.8502223), zoom: 16);
    final MFMapView map = MFMapView(
      initialCameraPosition: position,
      onMapCreated: onMapCreated,
      onCameraMoveStarted: onCameraMoveStarted,
      onCameraMove: onCameraMove,
      onCameraIdle: onCameraIdle,
      myLocationEnabled: true,
      myLocationButtonEnabled: true,
      onTap: onTap,
      onPOITap: onBaseMapPOITap,
      onBuildingTap: onBaseMapBuildingTap,
      onPlaceTap: onBaseMapPlaceTap,
      onDataSourceFeatureTap: onDataSourceFeatureTap,
    );

    return MaterialApp(
      home: Scaffold(
        body: Stack(
          children: <Widget>[
            SizedBox(
              width: 480.0,
              height: 800.0,
              child: map,
            ),
            Padding(
              padding: const EdgeInsets.all(1.0),
              child: Align(
                alignment: Alignment.topLeft,
                child: TextButton(
                  child: Text('Move Camera',
                      style: TextStyle(color: Colors.black)),
                  onPressed: moveCamera,
                ),
              ),
            ),
            Padding(
              padding: const EdgeInsets.all(1.0),
              child: Align(
                alignment: Alignment.topCenter,
                child: TextButton(
                  child: Text('Get ZoomLevel',
                      style: TextStyle(color: Colors.black)),
                  onPressed: getZoomLevel,
                ),
              ),
            ),
            Padding(
              padding: const EdgeInsets.all(1.0),
              child: Align(
                alignment: Alignment.topRight,
                child: TextButton(
                  child: Text('Animate Camera',
                      style: TextStyle(color: Colors.black)),
                  onPressed: animateCamera,
                ),
              ),
            )
          ],
        ),
        floatingActionButton: FloatingActionButton(
          onPressed: doAction,
          tooltip: "MODE",
          child: const Icon(Icons.mode_rounded),
        ),
      ),
    );
  }

  void onMapCreated(MFMapViewController controller) {
    print('onMapCreated');
    setState(() {
      _controller = controller;
      _isMapCreated = true;
    });
  }

  void onCameraMoveStarted() {
    print('camera move startedddddddddddddddddd');
  }

  void onCameraMove(MFCameraPosition position) {
    print('camera move to ${position.toString()}');
  }

  void onCameraIdle() => print('onCameraIdle');

  void moveCamera() {
    final cameraUpdate = MFCameraUpdate.newLatLng(
        MFLatLng(10.779348472547028, 106.71295166015625));
    print('moveCamera to: ' + cameraUpdate.toString());
    _controller.moveCamera(cameraUpdate);
  }

  void animateCamera() {
    final cameraUpdate =
        MFCameraUpdate.newLatLng(MFLatLng(16.088414, 108.230563));
    print('animateCamera to: ' + cameraUpdate.toString());
    _controller.animateCamera(cameraUpdate);
  }

  void getZoomLevel() {
    print("Get Zoom level");
    final zoom = _controller.getZoomLevel();
    zoom.then((value) => print('zoom level: ' + value.toString()));
  }

  Future<void> doAction() async {
    // const p = MFScreenCoordinate(x: 100, y: 200);
    // _controller.getLatLng(p).then((latlng) {
    //   print('Convert point ${p.toJson()} to latlng: ${latlng.toJson()}');
    //   _controller.getScreenCoordinate(latlng).then((value) => {
    //         print(
    //             'Convert latlng: ${latlng.toJson()} to value: ${value.toJson()}')
    //       });
    // });

    // final meters = Random().nextInt(200).toDouble();
    // final lp = await _controller.getLogicalPixelsForMeters(meters);
    // final mt = await _controller.getMetersForLogicalPixels(lp);
    // print("meters: $meters, lp: $lp, mt: $mt");
    DateTime date = DateTime.now();
    print('now: ${date.millisecondsSinceEpoch}');
    _controller.setTime(date);
  }

  void onTap(MFLatLng coordinate) {
    print('Did tap ' + coordinate.toString());
  }

  void onBaseMapPOITap(String placeId, String name, MFLatLng location) {
    print('Tap on POI $placeId, name: $name, location: $location');
  }

  void onBaseMapBuildingTap(String buildingId, String name, MFLatLng location) {
    print('Tap on Building $buildingId, name: $name, location: $location');
  }

  void onBaseMapPlaceTap(String name, MFLatLng location) {
    print('Tap on Place $name, location: $location');
  }

  void onDataSourceFeatureTap(MFDataSourceFeature feature, MFLatLng location) {
    print('Tap on Data Source Feature $feature, location: $location');
  }
}
