class MFDataSourceFeature {
  const MFDataSourceFeature(
    this.source,
    this.sourceLayer,
    this.layerType,
    this.properties,
  );

  final String source;
  final String sourceLayer;
  final String layerType;
  final Map<String, dynamic> properties;

  static MFDataSourceFeature? fromJson(Object? json) {
    if (json == null || !(json is Map<dynamic, dynamic>)) {
      return null;
    }

    return MFDataSourceFeature(
      json['source'],
      json['sourceLayer'],
      json['layerType'],
      Map<String, dynamic>.from(json['properties']),
    );
  }
}
