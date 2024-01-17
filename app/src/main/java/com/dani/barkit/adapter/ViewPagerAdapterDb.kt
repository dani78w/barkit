package com.dani.barkit.adapter


    import android.content.Intent
    import android.graphics.Bitmap
    import android.os.Bundle
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.ImageView
    import android.widget.TextView
    import androidx.fragment.app.Fragment
    import androidx.fragment.app.FragmentActivity
    import androidx.viewpager2.adapter.FragmentStateAdapter
    import com.dani.barkit.MainActivity
    import com.dani.barkit.R
    import com.google.android.material.floatingactionbutton.FloatingActionButton

    class ViewPagerAdapterDb(fragmentActivity: FragmentActivity, private val bitmaps: List<Bitmap>) :
        FragmentStateAdapter(fragmentActivity) {

        override fun getItemCount(): Int = bitmaps.size

        override fun createFragment(position: Int): Fragment {
            val bitmap = bitmaps[position]
            return ImageFragment.newInstance(bitmap, position + 1, bitmaps.size)
        }
    }

    class ImageFragment : Fragment() {

        companion object {
            private const val ARG_BITMAP = "arg_bitmap"
            private const val POS = "arg_posicion"
            private const val MAX = "arg_posicion_max"

            fun newInstance(bitmap: Bitmap, position: Int, max: Int): ImageFragment {
                val fragment = ImageFragment()
                val args = Bundle()
                args.putParcelable(ARG_BITMAP, bitmap)
                args.putString(POS, position.toString())
                args.putString(MAX, max.toString())
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
            val textView: TextView = view.findViewById(R.id.cursor)
            textView.text = "${requireArguments().getString(POS)}/${requireArguments().getString(MAX)}"
            val bitmap = requireArguments().getParcelable<Bitmap>(ARG_BITMAP)
            val imageView: ImageView = view.findViewById(R.id.foto_grande)
            val floatingActionButton: FloatingActionButton = view.findViewById(R.id.floatingActionButton)
            floatingActionButton.setOnClickListener {
                val intent: Intent = Intent(activity, MainActivity::class.java)
                startActivity(intent)
            }
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap)
            }
            return view
        }
    }