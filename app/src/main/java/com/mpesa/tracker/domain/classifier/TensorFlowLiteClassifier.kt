package com.mpesa.tracker.domain.classifier

import android.content.Context
import android.util.Log
import com.mpesa.tracker.data.local.entities.TransactionEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import org.tensorflow.lite.Interpreter
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStream
import java.io.InputStreamReader
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
    private val vocabMap = mutableMapOf<String, Int>()
    private var sequenceLength = 16 // The sequence length expected by the model

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

            // Load vocabulary
            loadVocabulary()
        } catch (e: Exception) {
            Log.e("TFLiteClassifier", "Failed to load TFLite model. Using fallback rules.", e)
            loadFallbackVocabulary() // Ensure fallback is loaded even if model file itself fails to open
        }
    }

    private fun loadVocabulary() {
        try {
            val vocabStream: InputStream = context.assets.open("vocab.txt")
            BufferedReader(InputStreamReader(vocabStream)).use { reader ->
                var index = 0
                var line: String? = reader.readLine()
                while (line != null) {
                    val word = line.trim()
                    if (word.isNotEmpty()) {
                        vocabMap[word] = index
                    }
                    index++
                    line = reader.readLine()
                }
            }
            Log.d("TFLiteClassifier", "Loaded ${vocabMap.size} words from assets vocabulary.")
        } catch (e: Exception) {
            Log.w("TFLiteClassifier", "vocab.txt not found in assets. Using fallback vocabulary.")
            loadFallbackVocabulary()
        }
    }

    private fun loadFallbackVocabulary() {
        // Fallback vocabulary in case assets loading fails or model is dummy
        // These words correspond to target merchants and types in the training script
        val fallbackWords = listOf(
            "<pad>", "<unk>", "naivas", "supermarket", "paybill", "quickmart", 
            "groceries", "kfc", "restaurant", "kplc", "prepaid", "nairobi", 
            "water", "uber", "kenya", "bolt", "rides", "safaricom", "home", 
            "zuku", "fibre", "john", "doe", "send_money", "mary", "jane", 
            "peter", "pan"
        )
        fallbackWords.forEachIndexed { index, word ->
            vocabMap[word] = index
        }
        Log.d("TFLiteClassifier", "Loaded ${vocabMap.size} fallback vocabulary words.")
    }

    override suspend fun classify(transaction: TransactionEntity): Int? {
        val currentInterpreter = interpreter ?: return null
        
        val inputString = "${transaction.recipientName} ${transaction.type.name} ${transaction.amount}"
        
        try {
            // Proper tokenization matching AverageWordVec text classification models
            // 1. Lowercase and split into alphanumeric words
            val words = inputString.lowercase().split("\\W+".toRegex()).filter { it.isNotEmpty() }
            
            // 2. Map words to vocabulary indices. Unrecognized words are mapped to <unk> (index 1)
            val tokens = IntArray(sequenceLength) { 0 } // index 0 is <pad>
            
            for (i in 0 until minOf(words.size, sequenceLength)) {
                val word = words[i]
                val token = vocabMap[word] ?: vocabMap["<unk>"] ?: 1
                tokens[i] = token
            }
            
            // 3. Prepare input buffer of shape [1, sequenceLength] as Int32 array
            val inputArray = Array(1) { IntArray(sequenceLength) }
            inputArray[0] = tokens
            
            // Output array mapping to 10 possible Categories (FLOAT32 probabilities)
            val outputArray = Array(1) { FloatArray(10) }
            
            // Run inference
            currentInterpreter.run(inputArray, outputArray)
            
            // Find the category index with the highest probability
            var maxProb = 0f
            var bestCategoryIndex = -1
            
            for (i in outputArray[0].indices) {
                if (outputArray[0][i] > maxProb) {
                    maxProb = outputArray[0][i]
                    bestCategoryIndex = i + 1 // Categories start at ID 1
                }
            }
            
            if (maxProb > 0.6f && bestCategoryIndex != -1) {
                Log.d("TFLiteClassifier", "ML Model predicted category $bestCategoryIndex with $maxProb confidence")
                return bestCategoryIndex
            }
            
        } catch (e: Exception) {
            Log.e("TFLiteClassifier", "Inference failed due to shape mismatch or tokenizer error", e)
        }

        return null
    }
}
