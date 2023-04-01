package eg.gov.iti.jets.project

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.PagerAdapter
import com.squareup.picasso.Picasso
import eg.gov.iti.jets.project.databinding.PagerItemBinding
import eg.gov.iti.jets.project.model.Pager

class ViewPagerAdapter(var context:Context): PagerAdapter() {

    lateinit var binding:PagerItemBinding

    var pagerList:List<Pager> = listOf(
        Pager(context.getString(R.string.header1),context.getString(R.string.body1),R.drawable.pager1),
        Pager(context.getString(R.string.header2),context.getString(R.string.body2),R.drawable.pager2),
        Pager(context.getString(R.string.header3),context.getString(R.string.body3),R.drawable.pager3),
        Pager(context.getString(R.string.header4),context.getString(R.string.body4),R.drawable.pager4))


    override fun getCount(): Int {
        return pagerList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`as ConstraintLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var inflater:LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = PagerItemBinding.inflate(inflater,container,false)
        container.addView(binding.root)
        Picasso.get().load(pagerList[position].image).into(binding.imageView2)
        binding.pagerHeaderTxt.text = pagerList[position].title
        binding.pagerBodyTxt.text = pagerList[position].description
        return binding.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object`as ConstraintLayout)
    }
}