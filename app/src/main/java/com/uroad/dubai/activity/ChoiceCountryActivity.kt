package com.uroad.dubai.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import com.uroad.dubai.R
import com.uroad.dubai.adapter.SortCityAdapter
import com.uroad.library.common.BaseActivity
import com.uroad.dubai.model.ContactMDL
import com.uroad.dubai.utils.PinyinComparator
import com.uroad.dubai.widget.MyLetterSortView
import com.uroad.dubai.widget.TitleItemDecoration
import kotlinx.android.synthetic.main.activity_choice_address.*
import java.util.*

class ChoiceCountryActivity : BaseActivity() {

    private lateinit var dataList : MutableList<ContactMDL>
    private lateinit var adapter : SortCityAdapter
    private lateinit var manager : LinearLayoutManager

    override fun setUp(savedInstanceState: Bundle?) {
        setBaseContentView(R.layout.activity_choice_address)
        withTitle("Country/Area")

        initView()
    }

    private fun initView() {
        dataList = ArrayList()
        val comparator = PinyinComparator()
        rightLetter.setTextView(tvMidLetter)

        dataList.addAll(filledData(simulationData()))

        // TODO 根据a-z进行排序源数据
        Collections.sort(dataList, comparator)

        manager = LinearLayoutManager(this@ChoiceCountryActivity)
        rvAddressList.layoutManager = manager
        adapter = SortCityAdapter(this@ChoiceCountryActivity, dataList)
        rvAddressList.adapter = adapter
        adapter.setOnItemClickListener(itemClickListener)
        val titleItemDecoration = TitleItemDecoration(this@ChoiceCountryActivity, dataList)
        rvAddressList.addItemDecoration(titleItemDecoration)
        rvAddressList.addItemDecoration( DividerItemDecoration(this@ChoiceCountryActivity, DividerItemDecoration.VERTICAL))

        // 设置右侧触摸监听
        rightLetter.setOnTouchingLetterChangedListener(object : MyLetterSortView.OnTouchingLetterChangedListener{
            override fun onTouchingLetterChanged( s : String) {
                // 该字母首次出现的位置
                val position = adapter.getPositionForSection(s[0].toInt())
                if(position != -1){
                    manager.scrollToPositionWithOffset(position, 0)
                }
            }
        })
    }

    private var itemClickListener : SortCityAdapter.OnItemClickListener = SortCityAdapter.OnItemClickListener { _, position ->
        val mdl = dataList[position]
        val intent = Intent()
        val bundle = Bundle()
        bundle.putString("name",mdl.name)
        bundle.putString("phone",mdl.phone)
        intent.putExtras(bundle)
        setResult(100,intent)
        finish()
    }

    private fun filledData(list: List<ContactMDL>): ArrayList<ContactMDL> {
        val mSortList = ArrayList<ContactMDL>()

        for ( value in list){

            if (value.name.isEmpty()){
                return mSortList
            }
            val name = value.name.substring(0, 1)
            if (name.matches(Regex("[A-Z]"))){
                value.letters = name.toUpperCase()
            }else{
                value.letters = "#"
            }
            mSortList.add(value)
        }
        return mSortList
    }


    //模拟数据
    private fun simulationData(): List<ContactMDL> {
        val country = arrayOf("America", "Canada", "Egypt", "South Sudan", "Morocco", "Algeria", "Libya",
                "Gambia", "China", "Greece", "Netherlands", "Belgium", "France", "Spain","Singapore",
                "Thailand","Japan","Korea","Vietnam","Hong Kong","Macao","Taiwan Province","India","Turkey",
                "Pakistan","United Arab Emirates")
        val code = arrayOf("1", "1", "20", "211", "212", "213", "218", "220", "86", "30", "31", "32",
                "33", "34","65","66","81","82","84","852","853","886","91","90","92","971")
        val mSortList = ArrayList<ContactMDL>()
        for (i in country.indices){
            val mdl = ContactMDL()
            mdl.name = country[i]
            mdl.phone = code[i]
            mSortList.add(mdl)
        }
        return mSortList
    }

}