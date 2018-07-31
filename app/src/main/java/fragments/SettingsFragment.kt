package fragments


import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Switch
import android.widget.Toast
import com.example.champion.echo_12.R


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class SettingsFragment : Fragment() {

    var myActivity:Activity?=null
    var shakeSwitch:Switch?=null
    object Statified{
        var MY_PREFS_NAME="ShakeFeature"

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view=inflater!!.inflate(R.layout.fragment_settings, container, false)
        activity?.title="Settings"
        shakeSwitch=view?.findViewById(R.id.switchShake)

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myActivity=context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity=activity
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val prefs=myActivity?.getSharedPreferences(Statified.MY_PREFS_NAME,Context.MODE_PRIVATE)
        val isAllowed=prefs?.getBoolean("feature",false)
        shakeSwitch?.isChecked = isAllowed as Boolean

        shakeSwitch?.setOnCheckedChangeListener { CompoundButton, b ->
            if (b){
                val editior = myActivity?.getSharedPreferences(Statified.MY_PREFS_NAME, Context.MODE_PRIVATE)?.edit()
                editior?.putBoolean("feature", true )
                Toast.makeText(myActivity,"Enabled Shake Change",Toast.LENGTH_SHORT).show()
                editior?.apply()
            }else{
                val editior = myActivity?.getSharedPreferences(Statified.MY_PREFS_NAME, Context.MODE_PRIVATE)?.edit()
                editior?.putBoolean("feature", false )
                Toast.makeText(myActivity,"Disabled Shake Change",Toast.LENGTH_SHORT).show()
                editior?.apply()
            }

        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val item=menu?.findItem(R.id.action_sort)
        item?.isVisible=false


    }
}
