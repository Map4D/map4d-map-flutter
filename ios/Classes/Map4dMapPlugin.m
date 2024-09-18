#import "Map4dMapPlugin.h"
#import "FMFMapView.h"

#pragma mark - FMFMapViewFactory Extension

@interface FMFMapViewFactory (Private)
- (void)cleanFlutterMapViewReferences;
@end

#pragma mark - Map4dMapPlugin

static Map4dMapPlugin *sharedInstance = nil;

@interface Map4dMapPlugin()
@property(nonatomic, strong, nonnull) NSPointerArray *mapFactories;// List reference of FMFMapViewFactory
@end

@implementation Map4dMapPlugin

+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  FMFMapViewFactory* mapFactory = [[FMFMapViewFactory alloc] initWithRegistrar:registrar];
  
  [registrar registerViewFactory:mapFactory
                          withId:@"plugin:map4d-map-view-type"
gestureRecognizersBlockingPolicy:
   FlutterPlatformViewGestureRecognizersBlockingPolicyWaitUntilTouchesEnded];
  
  [Map4dMapPlugin.instance.mapFactories addPointer:(__bridge void * _Nullable)(mapFactory)];
}

+ (Map4dMapPlugin *)instance {
  if (sharedInstance == nil) {
    sharedInstance = [[super allocWithZone:NULL] init];
  }
  return sharedInstance;
}

+ (id)allocWithZone:(NSZone *)zone {
    return [Map4dMapPlugin instance];
}

- (instancetype)init {
  if (self = [super init]) {
    _mapFactories = [NSPointerArray weakObjectsPointerArray];
  }
  return self;
}

- (id<FlutterPlatformView>)getFlutterMapViewById:(int64_t)viewId {
  for (FMFMapViewFactory* mapFactory in _mapFactories) {
    if (mapFactory == nil) {
      continue;
    }

    id<FlutterPlatformView> view = [mapFactory getFlutterMapViewById:viewId];
    if (view != nil) {
      return view;
    }
  }
  return nil;
}

- (void)cleanFlutterMapViewReferences {
  NSInteger index = 0;
  while (index < _mapFactories.count) {
    FMFMapViewFactory* mapFactory = [_mapFactories pointerAtIndex:index];
    if (mapFactory == nil) {
      [_mapFactories removePointerAtIndex:index];
    }
    else {
      [mapFactory cleanFlutterMapViewReferences];
      ++index;
    }
  }
}

@end
