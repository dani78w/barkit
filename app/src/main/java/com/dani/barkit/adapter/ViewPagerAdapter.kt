import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.Glide
import com.dani.barkit.MainActivity
import com.dani.barkit.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ViewPagerAdapter(fragmentActivity: FragmentActivity, private val imageUrls: List<String>) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = imageUrls.size

    override fun createFragment(position: Int): Fragment {
        val imageUrl = imageUrls[position]
        return ImageFragment.newInstance(imageUrl,position,getItemCount())
    }
}
class ImageFragment : Fragment() {

    companion object {
        private const val ARG_IMAGE_URL = "arg_image_url"
        private const val POS = "arg_posicion"
        private const val MAX = "arg_posicion_max"
        fun newInstance(imageUrl: String,position: Int,max :Int): ImageFragment {
            val fragment = ImageFragment()
            val args = Bundle()
            args.putString(ARG_IMAGE_URL, imageUrl)
            args.putString(POS,(position+1).toString())
            args.putString(MAX,max.toString())
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_image, container, false)
        val textView :TextView = view.findViewById(R.id.cursor)
        textView.setText(requireArguments().getString(POS)+"/"+requireArguments().getString(MAX))
        val imageUrl = requireArguments().getString(ARG_IMAGE_URL)
        var imageView:ImageView = view.findViewById(R.id.foto_grande)
        var floatingActionButton : FloatingActionButton = view.findViewById(R.id.floatingActionButton)
        floatingActionButton.setOnClickListener{
            var intent: Intent = Intent(activity,MainActivity::class.java)
            startActivity(intent)
        }
        Glide.with(this)
            .load(imageUrl)
            .into(imageView)
        return view

    }


}