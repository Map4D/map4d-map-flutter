# Sample Usage

```dart
import 'dart:async';
import 'package:flutter/material.dart';
import 'package:map4d_map/map4d_map.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Map4D Map',
      home: Map4dSample(),
    );
  }
}

class Map4dSample extends StatefulWidget {
  @override
  _Map4dSampleState createState() => _Map4dSampleState();
}

class _Map4dSampleState extends State<Map4dSample> {
  
  Completer<MFMapViewController> _controller = Completer();
  MFMapType _mapType = MFMapType.roadmap;

  static final MFLatLng _kLandmark81 = MFLatLng(10.794630856464138, 106.72229460050636);
  static final MFCameraPosition _kInitialCameraPosition = MFCameraPosition(target: _kLandmark81, zoom: 16);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: MFMapView(
        mapType: _mapType,
        initialCameraPosition: _kInitialCameraPosition,
        onMapCreated: (MFMapViewController controller) {
          _controller.complete(controller);
        },
        onPOITap: _onPOITap,
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: _mapTypeToggler,
        tooltip: 'Map Type',
        child: Icon(Icons.map_outlined),
      ),
    );
  }

  void _onPOITap(String placeId, String name, MFLatLng location) {
    print('Tap on place: $placeId, name: $name, location: $location');
  }

  void _mapTypeToggler() async {    
    var newType = MFMapType.roadmap;
    switch (_mapType) {
      case MFMapType.roadmap:
        newType = MFMapType.satellite;
        break;
      case MFMapType.satellite:
        newType = MFMapType.hybrid;
        break;
      case MFMapType.hybrid:
        newType = MFMapType.roadmap;
        break;
    }

    setState(() {
      _mapType = newType;
    });
  }
}
```