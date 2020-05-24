package com.example.restopass.main.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.RelativeLayout
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.example.restopass.R
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.fragment_map.*


class MapFragment : Fragment(), OnMapReadyCallback{

    private lateinit var mapViewModel: MapViewModel
    private lateinit var mMap: GoogleMap
    private val fineLocation = Manifest.permission.ACCESS_FINE_LOCATION
    private val coarseLocation = Manifest.permission.ACCESS_COARSE_LOCATION
    private val permissions = arrayOf(fineLocation, coarseLocation)
    private val permissionCode = 1234
    private var locationGranted = false
    private var location: LatLng? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mapViewModel =
            ViewModelProviders.of(this).get(MapViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_map, container, false)
        initializeLocation()
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        search_here_button.visibility = View.GONE
        search_here_button.setOnClickListener {
            mMap.cameraPosition.target
        }
        val mapFragment =  childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        map_search.setEndIconOnClickListener {
            view.findNavController().navigate(R.id.filterFragment)
        }
        map_search_edit.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                search()
                return@OnEditorActionListener true
            }
            false
        })
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.isMyLocationEnabled = true
        mMap.setOnCameraMoveListener { search_here_button.visibility = View.VISIBLE }
        positionMyLocationOnBottomRight()
    }

    private fun search() {
        Toast.makeText(this.context, "search action", Toast.LENGTH_SHORT).show()
        search_here_button.visibility = View.GONE
    }

    private fun positionMyLocationOnBottomRight() {
        val locationButton= this.activity?.let { (it.findViewById<View>(Integer.parseInt("1")).parent as View).findViewById<View>(Integer.parseInt("2")) }
        val rlp = locationButton?.layoutParams as (RelativeLayout.LayoutParams)
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP,0)
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.TRUE)
        rlp.setMargins(0,0,30,30);
    }

    private fun initializeLocation() {
        locationGranted = getLocationPermissions()
        if (!locationGranted)
            requestLocationPermission()
        else
            getLocation()
    }

    private fun getLocation() {
        val fuseLoc = this.context?.let { LocationServices.getFusedLocationProviderClient(it) }
        if (locationGranted) {
            fuseLoc?.lastLocation?.addOnSuccessListener  {lastLocation : Location? ->
                lastLocation?.let {
                    val latLng = LatLng(it.latitude, it.longitude)
                    location?:apply { moveCamera(latLng, 15f) }
                    location = latLng
                }
            }
        }
    }

    private fun moveCamera(loc: LatLng, zoom: Float) = mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, zoom))

    private fun getLocationPermissions() = permissions.all { perm -> this.context?.let { ContextCompat.checkSelfPermission(it, perm) } == PackageManager.PERMISSION_GRANTED }

    private fun requestLocationPermission() = this.activity?.let { ActivityCompat.requestPermissions(it, permissions, permissionCode) }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            permissionCode -> if (grantResults.isNotEmpty() && grantResults.all { it ==  PackageManager.PERMISSION_GRANTED }) {
                locationGranted = true
                getLocation()
            }
        }
    }

}
