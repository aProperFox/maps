package com.example.mapdemo;

import android.graphics.Color;
import android.util.SparseArray;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by olsontl on 5/7/15.
 */
public class MapHandler {

  private int routeWeight = 7;

  private SparseArray<String> colorRoute() {
    SparseArray<String> map = new SparseArray<String>();
    map.put(3, "royalblue");
    map.put(5, "fuchsia");
    map.put(7, "lime");
    map.put(8, "olive");
    map.put(15, "orange");
    map.put(20, "LightSeaGreen");
    map.put(50, "red");
    return map;
  }

  private boolean verMarkers = false;

  private SparseArray<ArrayList<Polyline>> routePolyLine() {
    SparseArray<ArrayList<Polyline>> map = new SparseArray<>();
    map.put(3, new ArrayList<Polyline>(2));
    map.get(3).add(null);
    map.get(3).add(null);
    map.put(5, new ArrayList<Polyline>(2));
    map.get(5).add(null);
    map.get(5).add(null);
    map.put(7, new ArrayList<Polyline>(2));
    map.get(7).add(null);
    map.get(7).add(null);
    map.put(8, new ArrayList<Polyline>(2));
    map.get(8).add(null);
    map.get(8).add(null);
    map.put(15, new ArrayList<Polyline>(2));
    map.get(15).add(null);
    map.get(15).add(null);
    map.put(20, new ArrayList<Polyline>(2));
    map.get(20).add(null);
    map.get(20).add(null);
    map.put(50, new ArrayList<Polyline>(2));
    map.get(50).add(null);
    map.get(50).add(null);
    return map;
  }
  private SparseArray<ArrayList<ArrayList<Marker>>> markersArray() {
    SparseArray<ArrayList<ArrayList<Marker>>> map = new SparseArray<>();
    map.put(3, new ArrayList<ArrayList<Marker>>(2));
    map.get(3).add(new ArrayList<Marker>());
    map.get(3).add(new ArrayList<Marker>());
    map.put(5, new ArrayList<ArrayList<Marker>>(2));
    map.get(5).add(new ArrayList<Marker>());
    map.get(5).add(new ArrayList<Marker>());
    map.put(7, new ArrayList<ArrayList<Marker>>(2));
    map.get(7).add(new ArrayList<Marker>());
    map.get(7).add(new ArrayList<Marker>());
    map.put(8, new ArrayList<ArrayList<Marker>>(2));
    map.get(8).add(new ArrayList<Marker>());
    map.get(8).add(new ArrayList<Marker>());
    map.put(15, new ArrayList<ArrayList<Marker>>(2));
    map.get(15).add(new ArrayList<Marker>());
    map.get(15).add(new ArrayList<Marker>());
    map.put(20, new ArrayList<ArrayList<Marker>>(2));
    map.get(20).add(new ArrayList<Marker>());
    map.get(20).add(new ArrayList<Marker>());
    map.put(50, new ArrayList<ArrayList<Marker>>(2));
    map.get(50).add(new ArrayList<Marker>());
    map.get(50).add(new ArrayList<Marker>());
    return map;
  }
  private SparseArray<boolean[]> rutasMarkersSel() {
    SparseArray<boolean[]> map = new SparseArray<>();
    map.put(3, new boolean[2]);
    map.put(5, new boolean[2]);
    map.put(7, new boolean[2]);
    map.put(8, new boolean[2]);
    map.put(15, new boolean[2]);
    map.put(20, new boolean[2]);
    map.put(50, new boolean[2]);
    return map;
  }

  private SparseArray<ArrayList<Polyline>> routePolyLine;
  private SparseArray<ArrayList<ArrayList<Marker>>> markersArray;
  private SparseArray<boolean[]> rutasMarkersSel;

  private class Tuple {
    public int route;
    public int direction;
    public Tuple(int route, int direction) {
      this.route = route;
      this.direction = direction;
    }
  }

  private ArrayList<Tuple> visibleRoutes;

  public static MapResources mapResources;
  private GoogleMap map;

  public MapHandler(GoogleMap map) {
    mapResources = MapResources.getInstance();
    this.map = map;
    routePolyLine = routePolyLine();
    markersArray = markersArray();
    rutasMarkersSel = rutasMarkersSel();
    visibleRoutes = new ArrayList<>();
  }

  public ArrayList<MapResources.Stop> buscarParadas(int linea, int dir) {
    boolean isEast;
    if (dir == 0) {
      isEast = false;
    } else {
      isEast = true;
    }
    ArrayList<MapResources.Stop> tempParadas = new ArrayList<>();
    ArrayList<MapResources.Stop> paradas = mapResources.stops;
    for (int i = 0; i < paradas.size(); i++) {
      if (paradas.get(i).isEastBound == isEast && Arrays.asList(paradas.get(i).lines).contains(linea)) {
        tempParadas.add(paradas.get(i));
      }
    }
    return tempParadas;
  }

  public void drawPoliLyne(float[][] coordPL, int numRoutePL, int dir) {
    /*var lineSymbol = {
            path: google.maps.SymbolPath.FORWARD_CLOSED_ARROW
    };*/

    if(routePolyLine.get(numRoutePL).get(dir) != null) {
      routePolyLine.get(numRoutePL).get(dir).setVisible(true);
    } else {
      LatLng[] coordTrans = new LatLng[coordPL.length];
      for (int i = 0; i < coordPL.length; i++) {
        LatLng coord = new LatLng(coordPL[i][0], coordPL[i][1]);
        coordTrans[i] = coord;
      }
      Polyline line = map.addPolyline(new PolylineOptions()
          .add(coordTrans)
          .geodesic(true)
          .width(routeWeight)
          .color(mapResources.routeColors.get(numRoutePL)));
      routePolyLine.get(numRoutePL).set(dir, line);
    }
  }

  public void cargarMarkers(ArrayList<MapResources.Stop> markersRuta, int index, int direction) {

    for (int i = 0; i < markersRuta.size(); i++) {
      if (verMarkers) {
        if (markersArray.get(index).get(direction).size() > i) {
          markersArray.get(index).get(direction).get(i).setVisible(true);
        } else {
          Marker marker = map.addMarker(new MarkerOptions().visible(true)
                  .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_stop))
                  .title(markersRuta.get(i).name)
                  .position(new LatLng(markersRuta.get(i).latitude, markersRuta.get(i).longitude))
                  .snippet(mapResources.findStop(markersRuta.get(i)) + "")
          );
          markersArray.get(index).get(direction).add(marker);
        }
      }
    }
  }

  public void quitarMarkers(ArrayList<Marker> markers) {
    for (int j = 0; j < markers.size(); j++) {
      markers.get(j).setVisible(false);
    }
  }

  public Integer getColorByIndex(int index) {
    if (index == 3) {
      return Color.parseColor("#994169e1");
    } else if (index == 5) {
      return Color.parseColor("#99dd69f5");
    } else if (index == 7) {
      return Color.parseColor("#9950dc59");
    } else if (index == 8) {
      return Color.parseColor("#99808000");
    } else if (index == 15) {
      return Color.parseColor("#99ff9900");
    } else if (index == 20) {
      return Color.parseColor("#9920b2aa");
    } else if (index == 50) {
      return Color.parseColor("#99e53030");
    }
    return Color.parseColor("#00000000");
  }

  public void showRoutes(String route, int direction, boolean isChecked) {
    int index = Integer.parseInt(route.replaceAll("\\D+", ""));
    if (isChecked) {
      int color = getColorByIndex(index);
      //$(this).parent().parent().parent().css('background-color', color);
      if(direction == 1) {
        drawPoliLyne(mapResources.routes.get(index).east, index, direction);
      } else {
        drawPoliLyne(mapResources.routes.get(index).west, index, direction);
      }
      ArrayList<MapResources.Stop> paradasArray = buscarParadas(index, direction);
      cargarMarkers(paradasArray, index, direction);
      rutasMarkersSel.get(index)[direction] = true;
      visibleRoutes.add(new Tuple(index, direction));
    } else {
      //if (!$(this).parent().siblings('label').children('input').prop("checked")) {
      //  $(this).parent().parent().parent().css('background-color', 'rgba(205,205,205,0.65)');
      //}
      routePolyLine.get(index).get(direction).setVisible(false);
      quitarMarkers(markersArray.get(index).get(direction));
      rutasMarkersSel.get(index)[direction] = false;
      for (int i = 0; i < visibleRoutes.size(); i++) {
        if (visibleRoutes.get(i).route == index) {
          visibleRoutes.remove(i);
          break;
        }
      }
    }
  }

  public void toggleMarkers(boolean shouldShow) {
    if (shouldShow) {
      verMarkers = true;
      for (int i = 0; i < visibleRoutes.size(); i++) {
        int index = visibleRoutes.get(i).route;
        int direction = visibleRoutes.get(i).direction;
        ArrayList<MapResources.Stop> paradasArray = buscarParadas(index, direction);
        cargarMarkers(paradasArray, index, direction);
      }
    } else {
      verMarkers = false;
      for (int i = 0; i < visibleRoutes.size(); i++) {
        quitarMarkers(markersArray.get(visibleRoutes.get(i).route).get(visibleRoutes.get(i).direction));
      }
    }
  }

}
