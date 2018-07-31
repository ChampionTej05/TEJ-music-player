package fragments


import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.graphics.drawable.RoundedBitmapDrawable
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.view.*
import android.widget.ImageView
import com.example.champion.echo_12.R
import kotlinx.android.synthetic.main.fragment_about_us.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class AboutUsFragment : Fragment() {

    var imageRound:ImageView?=null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

       var view=inflater!!.inflate(R.layout.fragment_about_us, container, false)
        activity?.setTitle("About Us")
        setHasOptionsMenu(true)
        return view
    }




    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val item: MenuItem?=menu?.findItem(R.id.action_redirect)
        item?.isVisible=true
        val sortitem: MenuItem?=menu?.findItem(R.id.action_sort)
        sortitem?.isVisible=false


    }
}
