package com.example.gias.Location;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gias.FindStudent.AdapterShowStudent;
import com.example.gias.Helper;
import com.example.gias.Main.Login.FragmentLogin;
import com.example.gias.Main.MainActivity;
import com.example.gias.Object.Student;
import com.example.gias.Object.Teacher;
import com.example.gias.R;
import com.example.gias.User.UserActivity;
import com.example.gias.User.profile.FragmentUser;
import com.example.gias.databinding.FragmentMapBinding;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.here.android.mpa.common.ApplicationContext;
import com.here.android.mpa.common.GeoBoundingBox;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.GeoPosition;
import com.here.android.mpa.common.Image;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.PositioningManager;
import com.here.android.mpa.common.ViewObject;
import com.here.android.mpa.guidance.NavigationManager;
import com.here.android.mpa.mapping.AndroidXMapFragment;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapGesture;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.MapObject;
import com.here.android.mpa.mapping.MapRoute;
import com.here.android.mpa.routing.CoreRouter;
import com.here.android.mpa.routing.Maneuver;
import com.here.android.mpa.routing.Route;
import com.here.android.mpa.routing.RouteOptions;
import com.here.android.mpa.routing.RoutePlan;
import com.here.android.mpa.routing.RouteResult;
import com.here.android.mpa.routing.RouteWaypoint;
import com.here.android.mpa.routing.Router;
import com.here.android.mpa.routing.RoutingError;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

import static androidx.core.app.ActivityCompat.finishAffinity;

public class MapFragment extends Fragment {
    public static String TAG = "MapFragment";
    private FragmentMapBinding binding;
    private AndroidXMapFragment mapFragment;
    private Map map;
    private PositioningManager positioningManager = null;
    private PositioningManager.OnPositionChangedListener positionListener;
    private Route m_route;
    private List < String> detailsRoute;
    private int lengthRoute;
    private GeoBoundingBox m_geoBoundingBox;
    private GPSLocation gpsLocation;
    private Student student;
    private Teacher teacher;

    private GeoCoordinate finishPosition = null;
    private GeoCoordinate startPosition = null;

    private Bundle data;

    public static MapFragment newInstance(Bundle args) {
        MapFragment fragment = new MapFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false);
        if (!checkRequiredPermissions()) checkRequiredPermissions();
        gpsLocation = new GPSLocation(getContext());
        detailsRoute = new ArrayList<>();
        data = getArguments();
        if(data != null){
            student = (Student) data.getSerializable(Helper.STUDENT);
            teacher = (Teacher) data.getSerializable(Helper.TEACHER);
            if(student != null){
                finishPosition = new GeoCoordinate(student.getLatitude(), student.getLongitude());
            }
            else finishPosition = new GeoCoordinate(teacher.getLatitude(), teacher.getLongitude());
            inItMap();
            Search();
        }

        return binding.getRoot();
    }

    private void Search(){
        binding.btnSearchAdress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.hideKeyBoard(getActivity());
                checkAdress();
            }
        });
    }

    private void checkAdress(){
        String newAdress = binding.edtAdress.getText().toString();
        if(newAdress.isEmpty())
            Toast.makeText(getContext(), "Bạn chưa nhập địa điểm", Toast.LENGTH_SHORT).show();
        else{
            LatLng lng= gpsLocation.getLocationFromAddress(getContext(), newAdress);
            if(lng == null)
                Toast.makeText(getContext(), getResources().getString(R.string.Error_Adress), Toast.LENGTH_SHORT).show();
            else{
                positioningManager.stop();
                map.removeAllMapObjects();
                createMarkerFinishPosition();
                startPosition = new GeoCoordinate(lng.latitude, lng.longitude);
                MapMarker marker = new MapMarker(new GeoCoordinate(lng.latitude, lng.longitude));
                marker.setDraggable(true);
                map.addMapObject(marker);
                map.setCenter(new GeoCoordinate(lng.latitude, lng.longitude), Map.Animation.NONE);
                map.getPositionIndicator().setVisible(false);
            }
        }
    }

    private void inItMap(){
        String diskCacheRoot = getActivity().getFilesDir().getPath()
                + File.separator + ".isolated-here-maps";

        boolean success = com.here.android.mpa.common.MapSettings.setIsolatedDiskCacheRootPath(diskCacheRoot);
        if(!success)
            Toast.makeText(getActivity().getApplicationContext(), "Unable to set isolated disk cache path.", Toast.LENGTH_SHORT).show();
        else{
            mapFragment = new AndroidXMapFragment();
            getFragmentManager().beginTransaction().add(R.id.MapDirection, mapFragment, "MAP_TAG").commit();
            mapFragment.init(new ApplicationContext(getContext()), new OnEngineInitListener() {
                @Override
                public void onEngineInitializationCompleted(
                        final OnEngineInitListener.Error error) {
                    if (error == OnEngineInitListener.Error.NONE){
                        map = mapFragment.getMap();
                        map.setZoomLevel(13);
                        map.setTilt((map.getMinTilt() + map.getMaxTilt())/2);
                        map.setProjectionMode(Map.Projection.MERCATOR);
                        Image marker_img_current_position = new Image();
                        try {
                            marker_img_current_position.setImageResource(R.drawable.ic_current_position);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        map.getPositionIndicator().setMarker(marker_img_current_position);
                        map.getPositionIndicator().setVisible(true);

                        GPSLocation location = new GPSLocation(getContext());
                        GeoCoordinate currentPosition =  new GeoCoordinate(location.getLatitude(), location.getLongitude());
                        startPosition = currentPosition;
                        map.setCenter(currentPosition, Map.Animation.NONE);

                        createMarkerFinishPosition();
                        clickButtonCurrentPosition();
                        updateCurrentPosition();
                        direction();
                        createRoute();
                    }
                    else{
                        System.out.println("ERROR: Cannot initialize Map Fragment");
                        new AlertDialog.Builder(getActivity()).setMessage(
                                "Error : " + error.name() + "\n\n" + error.getDetails())
                                .setTitle(R.string.engine_init_error)
                                .setNegativeButton(android.R.string.cancel,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                                finishAffinity(getActivity());
                                            }
                                        }).create().show();
                    }
                }
            });
        }
    }

    private void createMarkerFinishPosition(){
        Bitmap bitmap = null;
        if(student != null) {
            bitmap = student.getAvatarStudent();
        }
        else {
            bitmap = teacher.getAvatarTeacher();
        }

        Image marker_img = new Image();
        marker_img.setBitmap(new Helper(getContext()).createAvatarUser(bitmap));
        MapMarker marker = new MapMarker(finishPosition, marker_img);
        marker.setDraggable(true);
        map.addMapObject(marker);
    }

    private void direction(){
        binding.btnDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(m_route != null){
                    m_route = null;
                }
                createRoute();
            }
        });
    }

    private void clickButtonCurrentPosition(){
        binding.btnCurrentPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                map.getPositionIndicator().setVisible(true);
                positioningManager.start(PositioningManager.LocationMethod.GPS_NETWORK);
                GPSLocation location = new GPSLocation(getContext());
                map.setCenter(new GeoCoordinate(location.getLatitude(), location.getLongitude()), Map.Animation.BOW);
            }
        });
    }

    private void updateCurrentPosition(){
        positioningManager = PositioningManager.getInstance();
        positionListener = new PositioningManager.OnPositionChangedListener() {
            @Override
            public void onPositionUpdated(PositioningManager.LocationMethod locationMethod, GeoPosition geoPosition, boolean b) {
                map.setCenter(geoPosition.getCoordinate(), Map.Animation.BOW);
            }
            @Override
            public void onPositionFixChanged(PositioningManager.LocationMethod locationMethod, PositioningManager.LocationStatus locationStatus) { }};
        try {
            positioningManager.addListener(new WeakReference<>(positionListener));
            if(!positioningManager.start(PositioningManager.LocationMethod.GPS_NETWORK)) {
                Log.e("HERE", "PositioningManager.start: Failed to start...");
            }
        } catch (Exception e) {
            Log.e("HERE", "Caught: " + e.getMessage());
        }
        map.getPositionIndicator().setVisible(true);
    }

    private void createRoute() {
        detailsRoute.clear();
        lengthRoute = 0;
        CoreRouter coreRouter = new CoreRouter();
        RoutePlan routePlan = new RoutePlan();
        RouteOptions routeOptions = new RouteOptions();
        routeOptions.setTransportMode(RouteOptions.TransportMode.SCOOTER);
        routeOptions.setHighwaysAllowed(false);
        routeOptions.setRouteType(RouteOptions.Type.FASTEST);
        routeOptions.setRouteCount(1);
        routePlan.setRouteOptions(routeOptions);

        RouteWaypoint startPoint = new RouteWaypoint(startPosition);
        RouteWaypoint destination = new RouteWaypoint(finishPosition);

        routePlan.addWaypoint(startPoint);
        routePlan.addWaypoint(destination);

        coreRouter.calculateRoute(routePlan,
                new Router.Listener<List<RouteResult>, RoutingError>() {
                    @Override
                    public void onProgress(int i) {
                        /* The calculation progress can be retrieved in this callback. */
                    }
                    @Override
                    public void onCalculateRouteFinished(List<RouteResult> routeResults,
                                                         RoutingError routingError) {
                        if (routingError == RoutingError.NONE) {
                            if (routeResults.get(0).getRoute() != null) {
                                m_route = routeResults.get(0).getRoute();

                                //getDetails route
                                lengthRoute = m_route.getLength();
                                List <Maneuver> maneuvers = m_route.getManeuvers();
                                for (Maneuver maneuver: maneuvers) {
                                    if(!maneuver.getNextRoadName().isEmpty())
                                        detailsRoute.add(maneuver.getNextRoadName());
                                }

                                //setBottom sheet
                                setBottomSheet();
                                binding.btnDitails.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        setBottomSheet();
                                    }
                                });

                                MapRoute mapRoute = new MapRoute(routeResults.get(0).getRoute());
                                mapRoute.setManeuverNumberVisible(true);
                                map.addMapObject(mapRoute);
                                m_geoBoundingBox = routeResults.get(0).getRoute().getBoundingBox();
                                map.zoomTo(m_geoBoundingBox, Map.Animation.NONE,
                                        Map.MOVE_PRESERVE_ORIENTATION);

                                //startNavigation();
                            } else {
                                Toast.makeText(getContext(), "Error:route results returned is not valid", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "Error:route calculation returned error code: " + routingError, Toast.LENGTH_LONG).show();

                        }
                    }
                });
    }

    private void setBottomSheet(){
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext(), R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(getContext())
                .inflate(R.layout.layout_bottom_sheet_road,
                        (LinearLayout)getActivity().findViewById(R.id.bottomSheetRoad));

        TextView tvLengRoad = bottomSheetView.findViewById(R.id.lengRoad);
        int km = lengthRoute/1000;
        int m = lengthRoute % 1000;
        String length = km + "," + m + " km";
        tvLengRoad.setText(length);

        RecyclerView rvRoadName = bottomSheetView.findViewById(R.id.rvRoadName);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        rvRoadName.setLayoutManager(layoutManager);
        AdapterRoadname adapterRoadname = new AdapterRoadname(getContext(), detailsRoute);
        rvRoadName.setAdapter(adapterRoadname);

        TextView tvTitle = bottomSheetView.findViewById(R.id.tvTitle);
        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    private boolean checkRequiredPermissions() {
        String[] perms = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION,};
        if (!EasyPermissions.hasPermissions(getContext(), perms)) {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, getString(R.string.message_request_permission_read_phone_state),
                    20000, perms);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

}