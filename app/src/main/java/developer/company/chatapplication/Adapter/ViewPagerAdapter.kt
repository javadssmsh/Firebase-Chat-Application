package developer.company.chatapplication.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class ViewPagerAdapter(manager: FragmentManager):FragmentPagerAdapter(manager) {

    var fragments:ArrayList<Fragment> = ArrayList()
    var titles:ArrayList<String> = ArrayList()

    override fun getItem(position: Int): Fragment {
       return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.count()
    }

    public fun addFragment(fragment:Fragment,title:String){
        fragments.add(fragment)
        titles.add(title)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return titles[position]
    }
}