import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:map4d_map/map4d_map.dart';

void main() {
  const MethodChannel channel = MethodChannel('map4d_map');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await Map4dMap.platformVersion, '42');
  });
}
