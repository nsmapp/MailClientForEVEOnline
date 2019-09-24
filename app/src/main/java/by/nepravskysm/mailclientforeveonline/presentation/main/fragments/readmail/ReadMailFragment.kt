package by.nepravskysm.mailclientforeveonline.presentation.main.fragments.readmail

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import by.nepravskysm.domain.entity.InPutMail
import by.nepravskysm.domain.utils.*
import by.nepravskysm.mailclientforeveonline.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_read_mail.*
import kotlinx.android.synthetic.main.fragment_read_mail.view.*
import org.koin.android.viewmodel.ext.android.viewModel

class ReadMailFragment : Fragment(){

    val fViewModel: ReadMailViewModel by viewModel()


    private val mailObserver = Observer<InPutMail>{ mail ->

        pastHtmlTextToMailBody(body, mail.body)
        pastFromPhoto(fromPhoto, mail.from)

    }

    private val progresBarObserver = Observer<Boolean>{
        if(it){showProgresBar()}
        else{hideProgresBar()}
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {


        val fView = inflater.inflate(R.layout.fragment_read_mail, container, false)
        fView.body.movementMethod = ScrollingMovementMethod()
        fView.rootView.progressBar.visibility = View.GONE

        try {
            pastHtmlTextToMailBody(fView.body, fViewModel.mailBody)
            pastFromPhoto(fView.fromPhoto, fViewModel.fromId)
            fViewModel.subject = arguments?.getString(SUBJECT)!!
            fViewModel.from = arguments?.getString(FROM)!!
            fViewModel.inPutMail.observe(this, mailObserver)
        }catch (E: Exception){
            //TODO obrabotat'
        }finally {
            fView.subject.text = fViewModel.subject
            fView.from.text = fViewModel.from
        }


        fViewModel.getMail(arguments!!.getLong(MAIL_ID))
        fViewModel.isVisibilityProgressBar.observe(this, progresBarObserver)


        val navController = NavHostFragment.findNavController(this)

        fView.rootView.replayMail
            .setOnClickListener {
                navController.navigate(R.id.newMailFragment, createBundle(REPLAY))
            }

        fView.rootView.forwardMail
            .setOnClickListener {
                navController.navigate(R.id.newMailFragment, createBundle(FORWARD))
            }

        return fView
    }


    private fun pastFromPhoto(imageView: ImageView, fromId: Long){
        Picasso.get()
            .load("https://imageserver.eveonline.com/Character/${fromId}_128.jpg")
            .into(imageView.fromPhoto)
    }

    private fun pastHtmlTextToMailBody(view: TextView, htmlText: String){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            view.text = Html.fromHtml(htmlText, Html.FROM_HTML_MODE_COMPACT)
        }else{
            view.text = Html.fromHtml(htmlText)
        }
    }

    private fun createBundle(bundleType: String) : Bundle{

        val bundle = Bundle()
        bundle.putString(BUNDLE_TYPE, bundleType)
        bundle.putString(FROM, fViewModel.from)
        bundle.putString(SUBJECT, fViewModel.subject)
        bundle.putString(MAIL_BODY, fViewModel.mailBody)

        return bundle
    }

    private fun showProgresBar(){
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgresBar(){
        progressBar.visibility = View.GONE
    }



}