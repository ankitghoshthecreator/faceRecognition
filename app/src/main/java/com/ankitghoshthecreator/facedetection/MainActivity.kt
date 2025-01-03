package com.ankitghoshthecreator.facedetection

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        var buttonCamera=findViewById<Button>(R.id.btnCamera)

        buttonCamera.setOnClickListener {

            val intent=Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            if (intent.resolveActivity(packageManager)!=null){
                startActivityForResult(intent, 123)

            }else{
                Toast.makeText(this, "something went wrong", Toast.LENGTH_SHORT).show()
            }
        }



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (123==requestCode && RESULT_OK==resultCode){
            val extras=data?.extras
            val bitmap= extras?.get("data") as? Bitmap // according to documentation in site certain data types are to be used
            if (bitmap!=null) {
                detectFace(bitmap)
            }
        }
    }

    private fun detectFace(bitmap: Bitmap?) {
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()


        val detector= FaceDetection.getClient(options)
        val image = InputImage.fromBitmap(bitmap!!, 0)



        val result = detector.process(image)
            .addOnSuccessListener { faces ->
                // Task completed successfully, face successfully detected
                var resultText= " "

                var i=1

                for(face in faces){
                    resultText= "Face Number: $i" +
                            "Smile: ${face.smilingProbability?.times(100)}%"
                    i++
                }
                if(faces.isEmpty()){
                    Toast.makeText(this, "no face", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this, resultText, Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { e ->
                // Task failed with an exception, face not successfully detected
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
            }

    }
}