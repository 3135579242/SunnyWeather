package com.along.sunnyweather.ui.place

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.along.sunnyweather.R

class PlaceFragment : Fragment() {

    private val viewModel by lazy { ViewModelProvider(this)[PlaceViewModel::class.java] }

    private lateinit var adapter: PlaceAdapter

    private lateinit var recyclerView: RecyclerView
    private lateinit var bgImageView: ImageView
    private lateinit var searchPlaceEdit: EditText

    /**
     * 加载布局文件
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_place, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 获取控件
        init(view)
        // 布局
        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        // 适配器
        val adapter = PlaceAdapter(this, viewModel.placeList)
        recyclerView.adapter = adapter

        // 点击事件
        searchPlaceEdit.addTextChangedListener { editable ->
            val content = editable.toString()
            if (content.isNotEmpty()) {
                // 网络请求
                viewModel.searchPlaces(content)
            } else {
                recyclerView.visibility = View.GONE
                bgImageView.visibility = View.VISIBLE
                viewModel.placeList.clear()
                adapter.notifyDataSetChanged()
            }
        }
        // 观察ViewModel数据
        viewModel.placeLiveData.observe(viewLifecycleOwner, Observer { result ->
            val places = result.getOrNull()
            if (places != null) {
                recyclerView.visibility = View.VISIBLE
                bgImageView.visibility = View.GONE
                viewModel.placeList.clear()
                viewModel.placeList.addAll(places)
                adapter.notifyDataSetChanged()
            } else {
                Toast.makeText(activity, "未能查询到地点", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })

    }

    fun init(view: View) {
        recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        bgImageView = view.findViewById<ImageView>(R.id.bgImageView)
        searchPlaceEdit = view.findViewById<EditText>(R.id.searchPlaceEdit)
        // val actionBarLayout = view.findViewById<Fragment>(R.id.actionBarLayout)
    }

}