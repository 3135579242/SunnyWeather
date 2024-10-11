package com.along.sunnyweather.ui.weather

import android.content.Context
import android.graphics.Color
import android.hardware.input.InputManager
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.along.sunnyweather.R
import com.along.sunnyweather.logic.model.Weather
import com.along.sunnyweather.logic.model.getSky
import java.text.SimpleDateFormat
import java.util.Locale

class WeatherActivity : AppCompatActivity() {

    val viewModel by lazy { ViewModelProvider(this)[WeatherViewModel::class.java] }

    // now.xml
    private lateinit var nowLayout: RelativeLayout
    private lateinit var placeName: TextView
    private lateinit var currentTemp: TextView
    private lateinit var currentSky: TextView
    private lateinit var currentAQI: TextView
    private lateinit var navBtn: Button

    // forecast.xml
    private lateinit var forecastLayout: LinearLayout

    // life_index
    private lateinit var coldRiskText: TextView
    private lateinit var dressingText: TextView
    private lateinit var ultravioletText: TextView
    private lateinit var carWashingText: TextView

    // activity_weather.xml
    private lateinit var weatherLayout: ScrollView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    public lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val decorView = window.decorView
        decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.statusBarColor = Color.TRANSPARENT
        setContentView(R.layout.activity_weather)

        // 初始化控件
        init()

        // 不是空就赋值给 ViewModel
        if (viewModel.locationLng.isEmpty()) {
            viewModel.locationLng = intent.getStringExtra("location_lng") ?: ""
        }
        if (viewModel.locationLat.isEmpty()) {
            viewModel.locationLat = intent.getStringExtra("location_lat") ?: ""
        }
        if (viewModel.placeName.isEmpty()) {
            viewModel.placeName = intent.getStringExtra("place_name") ?: ""
        }
        // 观察数据
        viewModel.weatherLiveData.observe(this, Observer { result ->
            val weather = result.getOrNull()
            if (weather != null) {
                showWeatherInfo(weather)
            } else {
                Toast.makeText(this, "无法成功获取天气信息", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
            // 刷新结束可见
            swipeRefresh.isRefreshing = false
        })
        // 下拉进度条颜色
        swipeRefresh.setColorSchemeResources(com.google.android.material.R.color.design_default_color_primary)
        // 隐藏左侧菜单
        navBtn.setOnClickListener {
            // 左侧打开菜单
            drawerLayout.openDrawer(GravityCompat.START)
        }
        drawerLayout.addDrawerListener(object: DrawerLayout.DrawerListener{
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}
            override fun onDrawerOpened(drawerView: View) {}
            override fun onDrawerClosed(drawerView: View) {
                val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(drawerLayout.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }
            override fun onDrawerStateChanged(newState: Int) {}
        })
        // 刷新
        refreshWeather()
        // 监听下拉刷新
        swipeRefresh.setOnRefreshListener {
            refreshWeather()
        }

    }

    fun refreshWeather() {
        // 执行网络请求
        viewModel.refreshWeather(viewModel.locationLng, viewModel.locationLat)
        // 刷新没结束不可见
        swipeRefresh.isRefreshing = true
    }

    private fun showWeatherInfo(weather: Weather) {
        placeName.text = viewModel.placeName
        val realtime = weather.realtime
        val daily = weather.daily
        // 写now.xml数据
        val currentTempText = "${realtime.temperature.toInt()} ℃"
        currentTemp.text = currentTempText
        currentSky.text = getSky(realtime.skycon).info
        val currentPM25Text = "空气指数 ${realtime.airQuality.aqi.chn.toInt()}"
        currentAQI.text = currentPM25Text
        nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)
        // 写forecast.xml
        forecastLayout.removeAllViews()
        // 获取未来天气总数
        val days = daily.skycon.size
        for (i in 0 until days) {
            val skycon = daily.skycon[i]
            val temperature = daily.temperature[i]
            // 加载布局
            val view =
                LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false)
            // 初始化布局控件
            val dateInfo = view.findViewById(R.id.dateInfo) as TextView
            val skyIcon = view.findViewById(R.id.skyIcon) as ImageView
            val skyInfo = view.findViewById(R.id.skyInfo) as TextView
            val temperatureInfo = view.findViewById(R.id.temperatureInfo) as TextView
            // 格式化时间
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            // 填充数据
            dateInfo.text = simpleDateFormat.format(skycon.date)
            val sky = getSky(skycon.value)
            skyIcon.setImageResource(sky.icon)
            skyInfo.text = sky.info
            val tempText = "${temperature.min.toInt()} ~ ${temperature.max.toInt()}"
            temperatureInfo.text = tempText
            // 把子视图添加到父布局下
            forecastLayout.addView(view)
        }
        // 写life_index.xml
        val lifeIndex = daily.lifeIndex
        coldRiskText.text = lifeIndex.coldRisk[0].desc
        dressingText.text = lifeIndex.dressing[0].desc
        ultravioletText.text = lifeIndex.ultraviolet[0].desc
        carWashingText.text = lifeIndex.carWashing[0].desc
        weatherLayout.visibility = View.VISIBLE
    }

    fun init() {
        // now.xml
        nowLayout = findViewById<RelativeLayout>(R.id.nowLayout)
        placeName = findViewById<TextView>(R.id.placeName)
        currentTemp = findViewById<TextView>(R.id.currentTemp)
        currentSky = findViewById<TextView>(R.id.currentSky)
        currentAQI = findViewById<TextView>(R.id.currentAQI)
        navBtn = findViewById<Button>(R.id.navBtn)
        // forecast.xml
        forecastLayout = findViewById<LinearLayout>(R.id.forecastLayout)
        // life_index.xml
        coldRiskText = findViewById<TextView>(R.id.coldRiskText)
        dressingText = findViewById<TextView>(R.id.dressingText)
        ultravioletText = findViewById<TextView>(R.id.ultravioletText)
        carWashingText = findViewById<TextView>(R.id.carWashingText)
        // activity_weather.xml
        weatherLayout = findViewById<ScrollView>(R.id.weatherLayout)
        swipeRefresh = findViewById<SwipeRefreshLayout>(R.id.swipeRefresh)
        drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
    }

}