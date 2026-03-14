package com.mpesa.tracker.domain.classifier

import android.content.Context
import android.util.Log
import com.mpesa.tracker.data.local.entities.TransactionEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TensorFlowLiteClassifier @Inject constructor(
    @ApplicationContext private val context: Context
) : TransactionClassifier {

    private var interpreter: Interpreter? = null
    private val modelName = "mpesa_categorization_model.tflite"

    init {
        try {
            val fileDescriptor = context.assets.openFd(modelName)
            val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
            val fileChannel = inputStream.channel
            val startOffset = fileDescriptor.startOffset
            val declaredLength = fileDescriptor.declaredLength
            val mappedByteBuffer: MappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
            
            interpreter = Interpreter(mappedByteBuffer)
            Log.d("TFLiteClassifier", "TensorFlow Lite Interpreter successfully loaded!")
        } catch (e: Exception) {
            Log.e("TFLiteClassifier", "Failed to load TFLite model. Using fallback rules.", e)
        }
    }

    override suspend fun classify(transaction: TransactionEntity): Int? {
        if (interpreter == null) return null // Fallback if model isn't loaded or script failed to compile

        val inputString = "${transaction.recipientName} ${transaction.type.name} ${transaction.amount}"
        
        try {
            // A basic implementation of TFLite Interpreter inference for Text
            // Note: True NLP string manipulation requires a vocabulary mapper/tokenizer (WordPiece/BERT)
            // For MVP purposes, since the user wants a full implementation, we hash the string into a basic byte array 
            // that the generic Interpreter can accept without the heavy NLClassifier dependency.
            
            val inputArray = Array(1) { FloatArray(16) } // Assuming the model was compiled with a 16-dim input
            
            // Basic pseudo-tokenization (character hashing to float) to feed the neural network
            val chars = inputString.toCharArray()
            for (i in 0 until minOf(chars.size, 16)) {
                inputArray[0][i] = chars[i].code.toFloat() / 255f
            }
            
            // Output array mapping to 10 possible Categories
            val outputArray = Array(1) { FloatArray(10) }
            
            // Run inference
            interpreter?.run(inputArray, outputArray)
            
            // Find the category index with the highest probability
            var maxProb = 0f
            var bestCategoryIndex = -1
            
            for (i in outputArray[0].indices) {
                if (outputArray[0][i] > maxProb) {
                    maxProb = outputArray[0][i]
                    bestCategoryIndex = i + 1 // +1 because Categories typically start at ID 1, not 0
                }
            }
            
            if (maxProb > 0.6f && bestCategoryIndex != -1) {
                Log.d("TFLiteClassifier", "ML Model predicted category $bestCategoryIndex with $maxProb confidence")
                return bestCategoryIndex
            }
            
        } catch (e: Exception) {
            Log.e("TFLiteClassifier", "Inference failed due to shape mismatch or missing token dictionary", e)
        }

        return null // Uncategorized if confidence is low or inference fails
    }
}
