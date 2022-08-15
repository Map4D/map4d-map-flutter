package vn.map4d.map.map4d_map;

import vn.map4d.map.annotations.MFBitmapDescriptor;
import vn.map4d.map.core.MFCoordinateBounds;
import vn.map4d.map.overlays.MFImageOverlay;

class FMFImageOverlayController implements FMFImageOverlaySink {

  private final MFImageOverlay imageOverlay;

  FMFImageOverlayController(MFImageOverlay imageOverlay) {
    this.imageOverlay = imageOverlay;
  }

  void remove() {
    imageOverlay.remove();
  }

  @Override
  public void setImage(MFBitmapDescriptor image) {
    // You can not change image after creation
  }

  @Override
  public void setBounds(MFCoordinateBounds bounds) {
    // You can not change bounds after creation
  }

  @Override
  public void setZIndex(float zIndex) {
    // You can not change zIndex after creation
  }

  @Override
  public void setVisible(boolean visible) {
    imageOverlay.setVisible(visible);
  }

  @Override
  public void setOpacity(float opacity) {
    imageOverlay.setOpacity(opacity);
  }
}
