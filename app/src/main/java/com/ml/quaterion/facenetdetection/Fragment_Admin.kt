package com.ml.quaterion.facenetdetection

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import com.ml.quaterion.facenetdetection.databinding.FragmentAdminBinding
import com.ml.quaterion.facenetdetection.model.FaceNetModel
import com.ml.quaterion.facenetdetection.model.Models
import java.io.File
import java.io.FileOutputStream
import java.io.ObjectOutputStream

class Fragment_Admin: Fragment() {
    private var isSerializedDataStored = false
    private val SERIALIZED_DATA_FILENAME = "image_data"

    private val SHARED_PREF_IS_DATA_STORED_KEY = "is_data_stored"
    private lateinit var faceNetModel : FaceNetModel
    private var _binding : FragmentAdminBinding? = null
    private val binding get() = _binding!!
    private lateinit var fileReader : FileReader
    private lateinit var frameAnalyser  : FrameAnalyser
    private val useGpu = true
    private val useXNNPack = true
    private val modelInfo = Models.FACENET
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdminBinding.inflate(inflater,container,false)
        faceNetModel = FaceNetModel( requireContext() , modelInfo , useGpu , useXNNPack )
        fileReader = FileReader( faceNetModel )

        sharedPreferences = requireActivity().getSharedPreferences( getString( R.string.app_name ) , Context.MODE_PRIVATE )
        isSerializedDataStored = sharedPreferences.getBoolean( SHARED_PREF_IS_DATA_STORED_KEY , false )
        binding.rescan.setOnClickListener {
            launchChooseDirectoryIntent()
        }
        return binding.root
    }
    private fun launchChooseDirectoryIntent() {
        val intent = Intent( Intent.ACTION_OPEN_DOCUMENT_TREE )
        // startForActivityResult is deprecated.
        // See this SO thread -> https://stackoverflow.com/questions/62671106/onactivityresult-method-is-deprecated-what-is-the-alternative
        directoryAccessLauncher.launch( intent )
    }
    private val directoryAccessLauncher = registerForActivityResult( ActivityResultContracts.StartActivityForResult() ) {
        val dirUri = it.data?.data ?: return@registerForActivityResult
        val childrenUri =
            DocumentsContract.buildChildDocumentsUriUsingTree(
                dirUri,
                DocumentsContract.getTreeDocumentId( dirUri )
            )
        val tree = DocumentFile.fromTreeUri(requireContext(), childrenUri)
        val images = ArrayList<Pair<String, Bitmap>>()
        var errorFound = false
        if ( tree!!.listFiles().isNotEmpty()) {
            for ( doc in tree.listFiles() ) {
                if ( doc.isDirectory && !errorFound ) {
                    val name = doc.name!!
                    for ( imageDocFile in doc.listFiles() ) {
                        try {
                            images.add( Pair( name , getFixedBitmap( imageDocFile.uri ) ) )
                        }
                        catch ( e : Exception ) {
                            errorFound = true
                            Logger.log( "Could not parse an image in $name directory. Make sure that the file structure is " +
                                    "as described in the README of the project and then restart the app." )
                            break
                        }
                    }
                    Logger.log( "Found ${doc.listFiles().size} images in $name directory" )
                }
                else {
                    errorFound = true
                    Logger.log( "The selected folder should contain only directories. Make sure that the file structure is " +
                            "as described in the README of the project and then restart the app." )
                }
            }
        }
        else {
            errorFound = true
            Logger.log( "The selected folder doesn't contain any directories. Make sure that the file structure is " +
                    "as described in the README of the project and then restart the app." )
        }
        if ( !errorFound ) {
            fileReader.run( images , fileReaderCallback )
            Logger.log( "Detecting faces in ${images.size} images ..." )
        }
        else {
            val alertDialog = AlertDialog.Builder( requireContext() ).apply {
                setTitle( "Error while parsing directory")
                setMessage( "There were some errors while parsing the directory. Please see the log below. Make sure that the file structure is " +
                        "as described in the README of the project and then tap RESELECT" )
                setCancelable( false )
                setPositiveButton( "RESELECT") { dialog, which ->
                    dialog.dismiss()
                    launchChooseDirectoryIntent()
                }
                setNegativeButton( "CANCEL" ){ dialog , which ->
                    dialog.dismiss()
                    finish()
                }
                create()
            }
            alertDialog.show()
        }
    }
    private fun getFixedBitmap( imageFileUri : Uri) : Bitmap {
        var imageBitmap = BitmapUtils.getBitmapFromUri( requireContext().contentResolver , imageFileUri )
        val exifInterface = ExifInterface( requireContext().contentResolver.openInputStream( imageFileUri )!! )
        imageBitmap =
            when (exifInterface.getAttributeInt( ExifInterface.TAG_ORIENTATION ,
                ExifInterface.ORIENTATION_UNDEFINED )) {
                ExifInterface.ORIENTATION_ROTATE_90 -> BitmapUtils.rotateBitmap( imageBitmap , 90f )
                ExifInterface.ORIENTATION_ROTATE_180 -> BitmapUtils.rotateBitmap( imageBitmap , 180f )
                ExifInterface.ORIENTATION_ROTATE_270 -> BitmapUtils.rotateBitmap( imageBitmap , 270f )
                else -> imageBitmap
            }
        return imageBitmap
    }
    private val fileReaderCallback = object : FileReader.ProcessCallback {
        override fun onProcessCompleted(data: ArrayList<Pair<String, FloatArray>>, numImagesWithNoFaces: Int) {
            frameAnalyser.faceList = data
            saveSerializedImageData( data )
            Logger.log( "Images parsed. Found $numImagesWithNoFaces images with no faces." )
        }
    }
    private fun finish(){
        val transaction = childFragmentManager.beginTransaction()
        transaction.remove(this).commit()
        (requireActivity() as MainActivity).activityMainBinding.bttRecog.visibility = View.VISIBLE
    }
    private fun saveSerializedImageData(data : ArrayList<Pair<String,FloatArray>> ) {
        val serializedDataFile = File( requireContext().filesDir , SERIALIZED_DATA_FILENAME )
        Log.e("hahaha",SERIALIZED_DATA_FILENAME)
        ObjectOutputStream( FileOutputStream( serializedDataFile )  ).apply {
            writeObject( data )
            flush()
            close()
        }
        sharedPreferences.edit().putBoolean( SHARED_PREF_IS_DATA_STORED_KEY , true ).apply()
    }
}