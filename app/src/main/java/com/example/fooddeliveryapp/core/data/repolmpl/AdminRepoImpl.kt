package com.example.fooddeliveryapp.core.data.repolmpl

import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.fooddeliveryapp.core.data.domain.AdminRepository
import com.example.fooddeliveryapp.core.data.models.Product
import com.example.fooddeliveryapp.feature.util.RequestState
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.security.MessageDigest
import kotlin.coroutines.resume

class AdminRepoImpl() : AdminRepository {

    private fun DocumentSnapshot.toProduct(): Product? {
        return try {
            Product(
                id = id,
                title = getString("title").orEmpty(),
                description = getString("description").orEmpty(),
                category = getString("category").orEmpty(),
                allergyAdvice = getString("allergyAdvice").orEmpty(),
                energyValue = getLong("energyValue")?.toInt(),
                ingredients = getString("ingredients").orEmpty(),
                price = (get("price") as? Number)?.toDouble() ?: 0.0,
                productImage = getString("productImage").orEmpty(),
                isNew = getBoolean("new") ?: false,
                isPopular = getBoolean("popular") ?: false,
                isDiscounted = getBoolean("discounted") ?: false
                )
        } catch (e: Exception) {
            null
        }
    }

    override fun getCurrentUserId() = Firebase.auth.currentUser?.uid

    override suspend fun uploadProductImage(imageUri: Uri): Result<String> =
        suspendCancellableCoroutine { continuation ->
            try {
                MediaManager.get().upload(imageUri).unsigned("NghiaxEddy")
                    .callback(object : UploadCallback {
                        override fun onStart(requestId: String?) {}
                        override fun onProgress(
                            requestId: String?, bytes: Long, totalBytes: Long
                        ) {
                        }

                        override fun onSuccess(requestId: String?, resultData: Map<*, *>) {
                            val secureUrl = resultData["secure_url"]?.toString()
                            if (secureUrl != null) {
                                if (continuation.isActive) {
                                    continuation.resume(Result.success(secureUrl))
                                }
                            } else {
                                if (continuation.isActive) {
                                    continuation.resume(Result.failure(Exception("URL không hợp lệ")))
                                }
                            }
                        }

                        override fun onError(requestId: String?, error: ErrorInfo) {
                            if (continuation.isActive) {
                                continuation.resume(Result.failure(Exception(error.description)))
                            }
                        }

                        override fun onReschedule(requestId: String?, error: ErrorInfo) {}
                    }).dispatch()
            } catch (e: Exception) {
                if (continuation.isActive) {
                    continuation.resume(Result.failure(e))
                }
            }
        }

    override suspend fun deleteProductImageFromStorage(downloadUrl: String): Result<Unit> =
        suspendCancellableCoroutine { continuation ->
            try {
                val publicId = extractPublicId(downloadUrl)
                val apiKey = "283875926324232"
                val apiSecret = "p8dUf8ld6oNnxIxj1fLfTDbwSn0"
                val timestamp = System.currentTimeMillis() / 1000

                val signature: String = "public_id=$publicId&timestamp=$timestamp$apiSecret"
                val sha1Signature = sha1(signature)

                val client = OkHttpClient()
                val request =
                    Request.Builder().url("https://api.cloudinary.com/v1_1/dmjrv1vzw/image/destroy")
                        .post(
                            FormBody.Builder().add("public_id", publicId).add("api_key", apiKey)
                                .add("timestamp", timestamp.toString())
                                .add("signature", sha1Signature).build()
                        ).build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: java.io.IOException) {
                        if (continuation.isActive) {
                            continuation.resume(Result.failure(e))
                        }
                    }

                    override fun onResponse(call: Call, response: Response) {
                        if (response.isSuccessful) {
                            if (continuation.isActive) {
                                continuation.resume(Result.success(Unit))
                            }
                        } else {
                            if (continuation.isActive) {
                                continuation.resume(Result.failure(Exception("Xóa thất bại")))
                            }
                        }
                    }
                })
            } catch (e: Exception) {
                if (continuation.isActive) {
                    continuation.resume(Result.failure(e))
                }
            }
        }

    override suspend fun createNewProduct(product: Product) {
        val productMap = hashMapOf(
            "id" to product.id,
            "title" to product.title,
            "description" to product.description,
            "category" to product.category,
            "allergyAdvice" to product.allergyAdvice,
            "energyValue" to product.energyValue,
            "ingredients" to product.ingredients,
            "price" to product.price,
            "productImage" to product.productImage,
            "createdAt" to com.google.firebase.Timestamp.now(), // Thống nhất dùng createdAt
            "new" to product.isNew,
            "popular" to product.isPopular,
            "discounted" to product.isDiscounted
        )
        Firebase.firestore.collection("products").document(product.id).set(productMap).await()
    }

    override suspend fun updateProductThumbnail(
        productId: String, downloadUrl: String
    ): Result<Unit> {
        return try {
            val database = Firebase.firestore
            val productCollection = database.collection("products")
            val docRef = productCollection.document(productId)
            docRef.update("productImage", downloadUrl).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(
                IllegalStateException("Error while updating product thumbnail: ${e.message}")
            )
        }
    }

    override fun readLastTenProducts(): Flow<RequestState<List<Product>>> = channelFlow {
        send(RequestState.Loading)
        try {
            Firebase.firestore.collection("products")
                .orderBy("createdAt", Query.Direction.DESCENDING) // Dùng createdAt
                .limit(10)
                .snapshots()
                .collectLatest { snapshot ->
                    val products = snapshot.documents.mapNotNull { it.toProduct() }
                    send(RequestState.Success(products))
                }
        } catch (e: Exception) {
            send(RequestState.Error("Error reading products: ${e.message}"))
        }
    }

    override suspend fun readProductById(id: String): RequestState<Product> {
        return try {
            val productDocRef = Firebase.firestore.collection("products").document(id).get().await()
            if (productDocRef.exists()) {
                val product = productDocRef.toProduct()
                if (product != null) {
                    RequestState.Success(product)
                } else {
                    RequestState.Error("Lỗi chuyển đổi dữ liệu sản phẩm")
                }
            } else {
                RequestState.Error("Không tìm thấy sản phẩm")
            }
        } catch (e: Exception) {
            RequestState.Error("Lỗi khi đọc sản phẩm: ${e.message}")
        }
    }

    override suspend fun updateProduct(product: Product): Result<Unit> {
        return try {
            val database = Firebase.firestore
            val productCollection = database.collection("products")
            val docRef = productCollection.document(product.id)
            
            // Sử dụng update để tránh ghi đè làm mất trường createdAt
            val updates = hashMapOf<String, Any?>(
                "title" to product.title,
                "description" to product.description,
                "category" to product.category,
                "allergyAdvice" to product.allergyAdvice,
                "energyValue" to product.energyValue,
                "ingredients" to product.ingredients,
                "price" to product.price,
                "productImage" to product.productImage,
                "new" to product.isNew,
                "popular" to product.isPopular,
                "discounted" to product.isDiscounted
            )
            
            docRef.update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(
                IllegalStateException("Error while updating product: ${e.message}")
            )
        }
    }

    override suspend fun deleteProduct(productId: String): Result<Unit> {
        return try {
            val database = Firebase.firestore
            val productCollection = database.collection("products")
            val docRef = productCollection.document(productId)
            docRef.delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(
                IllegalStateException("Error while updating product: ${e.message}")
            )
        }
    }

    override fun searchProductByTitle(
        searchQuery: String
    ): Flow<RequestState<List<Product>>> = channelFlow {
        try {
            val collectionRef = Firebase.firestore.collection("products")
            if (searchQuery.isBlank()) {
                send(RequestState.Success(emptyList()))
                return@channelFlow
            }
            collectionRef
                .orderBy("title", Query.Direction.ASCENDING)
                .startAt(searchQuery)
                .endAt(searchQuery + "\uf8ff")
                .limit(10)
                .snapshots()
                .collectLatest { queryDocumentSnapshots ->
                    val products = queryDocumentSnapshots.documents.mapNotNull { it.toProduct() }
                    send(RequestState.Success(products))
                }
        } catch (e: Exception) {
            send(
                RequestState.Error("Error while searching products: ${e.message}")
            )
        }
    }

    private fun extractPublicId(url: String): String {
        val parts = url.split("/")
        val fileNameWithExtension = parts.last()
        return fileNameWithExtension.substringBeforeLast(".")
    }

    private fun sha1(input: String): String {
        val bytes = input.toByteArray(Charsets.UTF_8)
        val md = MessageDigest.getInstance("SHA-1")
        val digest = md.digest(bytes)

        return digest.joinToString("") { "%02x".format(it) }
    }
}
