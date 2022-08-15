package vn.map4d.map.map4d_map;

import vn.map4d.map.annotations.MFBitmapDescriptor;
import vn.map4d.map.core.MFCoordinateBounds;
import vn.map4d.map.overlays.MFImageOverlayOptions;

class FMFImageOverlayBuilder implements FMFImageOverlaySink {

  private final MFImageOverlayOptions imageOverlayOptions;

  FMFImageOverlayBuilder() {
    this.imageOverlayOptions = new MFImageOverlayOptions();
  }

  MFImageOverlayOptions build() {
    return imageOverlayOptions;
  }

  @Override
  public void setImage(MFBitmapDescriptor image) {
    imageOverlayOptions.image(image);
  }

  @Override
  public void setBounds(MFCoordinateBounds bounds) {
    imageOverlayOptions.bounds(bounds);
  }

  @Override
  public void setZIndex(float zIndex) {
    imageOverlayOptions.zIndex(zIndex);
  }

  @Override
  public void setVisible(boolean visible) {
    imageOverlayOptions.visible(visible);
  }

  @Override
  public void setOpacity(float opacity) {
    imageOverlayOptions.opacity(opacity);
  }
}
