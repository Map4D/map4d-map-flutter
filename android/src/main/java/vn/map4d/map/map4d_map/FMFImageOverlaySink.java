package vn.map4d.map.map4d_map;

import vn.map4d.map.annotations.MFBitmapDescriptor;
import vn.map4d.map.core.MFCoordinateBounds;

/** Receiver of ImageOverlayOptions configuration. */
interface FMFImageOverlaySink {

  void setImage(MFBitmapDescriptor image);

  void setBounds(MFCoordinateBounds bounds);

  void setZIndex(float zIndex);

  void setVisible(boolean visible);

  void setOpacity(float opacity);
}
