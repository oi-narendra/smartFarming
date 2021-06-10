package com.pranav.smartfarming.ui.main.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pranav.smartfarming.BASE_URL
import com.pranav.smartfarming.dataClasses.DiseaseResult
import com.pranav.smartfarming.databinding.FragmentDiseasePredictionBinding
import com.pranav.smartfarming.utils.errorToast
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.support.v4.runOnUiThread
import timber.log.Timber
import java.io.File


class DiseasePredictionFragment : Fragment() {

    lateinit var binding: FragmentDiseasePredictionBinding

    private var imagePath = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDiseasePredictionBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.removeImage.setOnClickListener {
            binding.selectImage.visibility = VISIBLE
            binding.predict.visibility = GONE
            binding.removeImage.visibility = GONE
            binding.image.visibility = INVISIBLE
            binding.resultContainer.visibility = GONE
            binding.image.setImageURI(null)
        }

        binding.predict.setOnClickListener {
            if (imagePath.isNotEmpty()) {
                binding.predict.isEnabled = false
                upload(File(imagePath))
            }
        }

        binding.selectImage.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(
                    1080,
                    1080
                )
                .start()

        }
    }


    private fun upload(file: File) {
        try {
            doAsync {
                val client = OkHttpClient()
                val formBody: RequestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(
                        "file", file.name,
                        file.asRequestBody("image/*".toMediaTypeOrNull())
                    )
                    .build()
                val request: Request =
                    Request.Builder().url(BASE_URL + "upload").post(formBody).build()
                val response: Response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val result = Gson().fromJson<DiseaseResult>(
                        response.body?.string(),
                        object : TypeToken<DiseaseResult>() {}.type
                    )

                    runOnUiThread {
                        binding.resultContainer.visibility = VISIBLE
                        binding.accuracy.text = result.accuracy.toString().trim() + "%"
                        binding.diseaseName.text = result.name.trim()
                        binding.solution.text = result.soln.trim()
                        Timber.d(result.toString())
                    }

                } else {

                    runOnUiThread {
                        binding.predict.visibility = VISIBLE
                        requireContext().errorToast(response.body?.string().toString())
                    }

                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                imagePath = data?.data?.path.toString()
                binding.image.setImageURI(Uri.fromFile(File(imagePath)))
                binding.image.visibility = VISIBLE
                binding.removeImage.visibility = VISIBLE
                binding.predict.visibility = VISIBLE
                binding.selectImage.visibility = GONE
                Timber.d(imagePath)
            }
            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT)
                    .show()
            }
            else -> {
                Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }


}