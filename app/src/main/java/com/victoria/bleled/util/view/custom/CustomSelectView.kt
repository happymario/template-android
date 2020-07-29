package com.victoria.bleled.util.view.custom

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.victoria.bleled.R

class CustomSelectView : LinearLayout {
    private var tv_title: TextView? = null
    private var tv_content: TextView? = null
    private var iv_triangle: ImageView? = null

    constructor(context: Context?) : super(context) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context,
        attrs
    ) {
        initView()
        getAttrs(attrs)
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyle: Int
    ) : super(context, attrs) {
        initView()
        getAttrs(attrs, defStyle)
    }

    private fun initView() {
        val infService = Context.LAYOUT_INFLATER_SERVICE
        val li = context.getSystemService(infService) as LayoutInflater
        val v = li.inflate(R.layout.layout_select_view, this, false)
        addView(v)
        iv_triangle = findViewById<View>(R.id.iv_triangle) as ImageView
        tv_title = findViewById<View>(R.id.tv_title) as TextView
        tv_content = findViewById<View>(R.id.tv_content) as TextView
    }

    private fun getAttrs(attrs: AttributeSet?) {
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.CustomSelectView)
        setTypeArray(typedArray)
    }

    private fun getAttrs(attrs: AttributeSet?, defStyle: Int) {
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.CustomSelectView, defStyle, 0)
        setTypeArray(typedArray)
    }

    private fun setTypeArray(typedArray: TypedArray) {
        var text_string =
            typedArray.getString(R.styleable.CustomSelectView_hintTitle)
        tv_title!!.text = text_string
        text_string = typedArray.getString(R.styleable.CustomSelectView_hintContent)
        tv_content!!.text = text_string
        val hideTitle =
            typedArray.getBoolean(R.styleable.CustomSelectView_hideTitle, false)
        tv_title!!.visibility = if (hideTitle) View.GONE else View.VISIBLE
        val hideArrow =
            typedArray.getBoolean(R.styleable.CustomSelectView_hideArrow, false)
        iv_triangle!!.visibility = if (hideArrow) View.GONE else View.VISIBLE
        typedArray.recycle()
    }

    fun setContent(text: Int) {
        tv_content!!.setText(text)
    }

    fun setTitle(title: String?) {
        tv_title!!.text = title
    }

    fun setHideArrow(hideArrow: Boolean) {
        iv_triangle!!.visibility = if (hideArrow) View.GONE else View.VISIBLE
    }

    var content: String?
        get() = tv_content!!.text.toString()
        set(text) {
            tv_content!!.text = text
        }
}