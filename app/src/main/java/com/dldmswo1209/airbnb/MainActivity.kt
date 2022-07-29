package com.dldmswo1209.airbnb

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.dldmswo1209.airbnb.databinding.ActivityMainBinding
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var naverMap: NaverMap
    private lateinit var binding: ActivityMainBinding
    private lateinit var locationSource: FusedLocationSource
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.mapView.onCreate(savedInstanceState)

        binding.mapView.getMapAsync(this)
    }
    override fun onMapReady(map: NaverMap) {
        naverMap = map

        // 확대/축소 범위 설정
        naverMap.maxZoom = 18.0
        naverMap.minZoom = 10.0

        // 지도 초기 위치
        val cameraUpdate = CameraUpdate.scrollTo(LatLng(37.497885,127.02751))
        naverMap.moveCamera(cameraUpdate)

        // 현재 위치를 찾는 버튼 활성화
        val uiSetting = naverMap.uiSettings
        uiSetting.isLocationButtonEnabled = true


        // 위치를 반환하는 FusedLocationSource 생성
        locationSource = FusedLocationSource(this@MainActivity, LOCATION_PERMISSION_REQUEST_CODE)
        // 위치소스 지정
        naverMap.locationSource = locationSource

        getHouseListFromAPI()
    }

    private fun getHouseListFromAPI(){
        val retrofit = Retrofit.Builder()
            .baseUrl("https://run.mocky.io")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(HouseService::class.java).also {
            it.getHouseList()
                .enqueue(object: Callback<HouseDto>{
                    override fun onResponse(call: Call<HouseDto>, response: Response<HouseDto>) {
                        if(!response.isSuccessful){
                            // 실패 처리
                            return
                        }
                        response.body()?.let { dto ->
                            updateMarker(dto.items)
                        }
                    }

                    override fun onFailure(call: Call<HouseDto>, t: Throwable) {
                        // 실패 처리

                    }
                })
        }
    }

    private fun updateMarker(houses: List<HouseModel>){
        houses.forEach { house ->
            val marker = Marker()
            marker.position = LatLng(house.lat, house.lng)
            // todo 마커 클릭 리스너
            marker.map = naverMap
            marker.tag = house.id
            marker.icon = MarkerIcons.BLACK
            marker.iconTintColor = Color.RED

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // requestCode 확인
        if(requestCode != LOCATION_PERMISSION_REQUEST_CODE)
            return

        // 권한 팝업을 쉽게 구현하기 위해서 google 에서 제공하는 라이브러리를 사용
        if(locationSource.onRequestPermissionsResult(requestCode,permissions,grantResults)){
            if(!locationSource.isActivated){
                naverMap.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }

    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }
    companion object{
        private const val LOCATION_PERMISSION_REQUEST_CODE = 10000
    }

}