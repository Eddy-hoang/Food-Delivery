package com.example.fooddeliveryapp.core.data.repoImpl

import android.net.Uri
import androidx.core.net.toUri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.fooddeliveryapp.core.data.domain.CustomerRepository
import com.example.fooddeliveryapp.core.data.models.Cart
import com.example.fooddeliveryapp.core.data.models.Country
import com.example.fooddeliveryapp.core.data.models.Customer
import com.example.fooddeliveryapp.core.data.models.PhoneNumber
import com.example.fooddeliveryapp.feature.util.RequestState
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume

private const val CUSTOMER_COLLECTION = "customer"
private const val CART_SUBCOLLECTION = "cart"
private const val FAVOURITE_SUBCOLLECTION = "favourite"


class CustomerRepoImpl : CustomerRepository {
    override fun getCurrentUserId(): String? =
        FirebaseAuth.getInstance().currentUser?.uid

    override suspend fun createCustomer(
        user: FirebaseUser,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val customerCollection = Firebase.firestore.collection("customer")
            val docRef = customerCollection.document(user.uid)
            val snapshot = docRef.get().await()

            if (!snapshot.exists()) {
                val names = user.displayName?.split(" ")
                val customer = Customer(
                    id = user.uid,
                    firstName = names?.firstOrNull() ?: "Unknown",
                    lastName = if ((names?.size ?: 0) > 1) names?.lastOrNull() ?: "" else "",
                    email = user.email ?: "Unknown",
                    profilePictureUrl = user.photoUrl?.toString()
                )
                docRef.set(customer).await()
            }
            onSuccess()
        } catch (e: Exception) {
            onError("Failed to create customer: ${e.message}")
        }
    }

    override fun readCustomerFlow(): Flow<RequestState<Customer>> = channelFlow {
        try {
            val userId = getCurrentUserId()
            if (userId != null) {
                val dataBase = Firebase.firestore
                dataBase.collection(CUSTOMER_COLLECTION)
                    .document(userId)
                    .snapshots()
                    .collectLatest { documentSnapshot ->
                        if (documentSnapshot.exists()) {
                            val postalCode = (documentSnapshot.get("postalCode") as? Long)?.toInt()
                            val phoneNumberMap = documentSnapshot.get("phoneNumber") as? Map<*, *>
                            val phoneNumber = phoneNumberMap?.let {
                                val dialCode = (it["CountryCode"] as? Long)?.toInt()
                                val number = it["number"] as? String

                                if (dialCode != null && number != null) {
                                    PhoneNumber(
                                        dialCode = dialCode,
                                        number = number
                                    )
                                } else {
                                    null
                                }
                            }

                            val countryMap = documentSnapshot.get("country") as? Map<*, *>
                            val country = countryMap?.let { map ->
                                val name = map["name"] as? String
                                val code = map["code"] as? String
                                val dialCode = (map["diaCode"] as? Long)?.toInt()
                                val flagUrl = map["flagUrl"] as? String
                                if (name != null && code != null && dialCode != null && flagUrl != null)
                                    Country(
                                        name = name,
                                        code = code,
                                        dialCode = dialCode,
                                        flagUrl = flagUrl
                                    )
                                else null
                            }

                            val customer = Customer(
                                id = documentSnapshot.id,
                                firstName = documentSnapshot.get("firstName") as String,
                                lastName = documentSnapshot.get("lastName") as String,
                                email = documentSnapshot.get("email") as String,
                                city = documentSnapshot.get("city") as String?,
                                postalCode = postalCode,
                                phoneNumber = phoneNumber,
                                address = documentSnapshot.get("address") as String?,
                                country = country,
                                profilePictureUrl = documentSnapshot.get("photoUrl") as String?,
                                isAdmin = documentSnapshot.getBoolean("admin") ?: false
                            )
                            send(RequestState.Success(data = customer))
                        } else {
                            send(RequestState.Error("Queried customer document does not exist."))
                        }
                    }
            } else {
                send(RequestState.Error("User is not available."))
            }
        } catch (e: Exception) {
            send(RequestState.Error("Error while reading customer information: ${e.message}"))
        }
    }

    override suspend fun updateCustomer(
        customer: Customer,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val userId = getCurrentUserId()
            if (userId != null) {
                val firestore = Firebase.firestore
                val customerCollection = firestore.collection("customer")
                val existingCustomer = customerCollection
                    .document(userId)
                    .get().await()
                if (existingCustomer.exists()) {
                    val phoneNumberMap = customer.phoneNumber?.let {
                        mapOf(
                            "CountryCode" to it.dialCode,
                            "number" to it.number,
                        )
                    }

                    val countryMap = customer.country?.let {
                        mapOf(
                            "name" to it.name,
                            "code" to it.code,
                            "diaCode" to it.dialCode,
                            "flagUrl" to it.flagUrl,
                        )
                    }

                    customerCollection
                        .document(userId)
                        .update(
                            mapOf(
                                "firstName" to customer.firstName,
                                "lastName" to customer.lastName,
                                "city" to customer.city,
                                "postalCode" to customer.postalCode,
                                "address" to customer.address,
                                "phoneNumber" to phoneNumberMap,
                                "country" to countryMap,
                            )
                        ).await()
                    onSuccess()
                } else {
                    onError("Customer document not found.")
                }
            } else {
                onError("User is not available.")
            }
        } catch (e: Exception) {
            onError("Error while updating customer information: ${e.message}")
        }
    }

    override suspend fun updateProfilePictureUrl(url: String): RequestState<Unit> = try {
        val uid = getCurrentUserId() ?: return RequestState.Error("User is not available.")
        Firebase.firestore.collection("customer")
            .document(uid)
            .update("profilePictureUrl", url) // Consistent field name
            .await()
        try {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                val request = userProfileChangeRequest { photoUri = url.toUri() }
                user.updateProfile(request).await()
                user.reload().await()
            }
        } catch (_: Exception) {
            // Ignore auth profile update failure
        }
        RequestState.Success(Unit)
    } catch (e: Exception) {
        RequestState.Error("Error while updating profile picture URL: ${e.message}")
    }

    override suspend fun uploadProfilePhoto(
        localUrl: Uri,
        onProcess: (Float) -> Unit
    ): RequestState<String> = suspendCancellableCoroutine { continuation ->
        val uid = getCurrentUserId() ?: run {
            if (continuation.isActive) continuation.resume(RequestState.Error("User is not available."))
            return@suspendCancellableCoroutine
        }

        try {
            MediaManager.get().upload(localUrl)
                .unsigned("NghiaxEddy")
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String?) {
                        onProcess(0.0f)
                    }

                    override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
                        val progress =
                            if (totalBytes > 0) bytes.toFloat() / totalBytes.toFloat() else 0f
                        onProcess(progress)
                    }

                    override fun onSuccess(requestId: String?, resultData: Map<*, *>) {
                        val secureUrl = resultData["secure_url"].toString()

                        android.util.Log.d("KiemTraAnh", "Link Cloudinary: $secureUrl")

                        Firebase.firestore.collection("customer")
                            .document(uid)
                            .update("profilePictureUrl", secureUrl)
                            .addOnSuccessListener {
                                FirebaseAuth.getInstance().currentUser?.let { user ->
                                    val request =
                                        userProfileChangeRequest { photoUri = secureUrl.toUri() }
                                    user.updateProfile(request)
                                }
                                if (continuation.isActive) {
                                    continuation.resume(RequestState.Success(secureUrl))
                                }
                            }
                            .addOnFailureListener { e ->
                                if (continuation.isActive) {
                                    continuation.resume(RequestState.Error("Failed to save URL: ${e.message}"))
                                }
                            }
                    }

                    override fun onError(requestId: String?, error: ErrorInfo) {
                        if (continuation.isActive) {
                            continuation.resume(RequestState.Error("Upload failed: ${error.description}"))
                        }
                    }

                    override fun onReschedule(requestId: String?, error: ErrorInfo) {
                    }
                })
                .dispatch()
        } catch (e: Exception) {
            if (continuation.isActive) {
                continuation.resume(RequestState.Error("Cloudinary Error: ${e.message}. Kiểm tra xem MediaManager đã init chưa."))
            }
        }
    }

    override suspend fun signOut(): RequestState<Unit> {
        return try {
            Firebase.auth.signOut()
            RequestState.Success(Unit)
        } catch (e: Exception) {
            RequestState.Error("Error while signing out ${e.message}")
        }
    }

    override suspend fun addToCart(
        productId: String,
        productTitle: String,
        quantityToAdd: Int
    ): RequestState<Unit> {
        return try {
            val uid = getCurrentUserId() ?: return RequestState.Error("User not available.")
            if (productId.isBlank()) return RequestState.Error("Invalid product id.")
            if (quantityToAdd <= 0) return RequestState.Error("Quantity must be at least 1.")

            val cartDoc = Firebase.firestore
                .collection(CUSTOMER_COLLECTION)
                .document(uid)
                .collection(CART_SUBCOLLECTION)
                .document(productId)

            Firebase.firestore.runTransaction { trx ->
                val snap = trx.get(cartDoc)
                if (snap.exists()) {
                    trx.update(
                        cartDoc,
                        mapOf(
                            "quantity" to FieldValue.increment(quantityToAdd.toLong()),
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                    )
                } else {
                    trx.set(
                        cartDoc,
                        mapOf(
                            "productId" to productId,
                            "quantity" to quantityToAdd,
                            "title" to productTitle,
                            "createdAt" to FieldValue.serverTimestamp(),
                            "updatedAd" to FieldValue.serverTimestamp()
                        )
                    )
                }
                Unit
            }.await()
            RequestState.Success(Unit)
        } catch (e: Exception) {
            RequestState.Error("Failed to add item to cart: ${e.message}")
        }
    }

    override suspend fun removeFromCart(
        productId: String,
        quantityToRemove: Int
    ): RequestState<Unit> {
        return try {
            val uid = getCurrentUserId() ?: return RequestState.Error("User not available.")
            if (productId.isBlank()) return RequestState.Error("Invalid product id.")
            if (quantityToRemove <= 0) return RequestState.Error("Quantity must be at least 1.")

            val cartDoc = Firebase.firestore
                .collection(CUSTOMER_COLLECTION)
                .document(uid)
                .collection(CART_SUBCOLLECTION)
                .document(productId)

            Firebase.firestore.runTransaction { trx ->
                val snap = trx.get(cartDoc)
                if (!snap.exists()) return@runTransaction

                val currentQty = (snap.getLong("quantity") ?: 0L).toInt()
                val newQty = currentQty -quantityToRemove
                if (newQty <= 0) {
                    trx.delete(cartDoc)
                } else {
                    trx.update(
                        cartDoc,
                        mapOf(
                            "quantity" to quantityToRemove,
                            "createdAt" to FieldValue.serverTimestamp(),
                        )
                    )
                }
                Unit
            }.await()
            RequestState.Success(Unit)
        } catch (e: Exception) {
            RequestState.Error("Failed to remove item from cart: ${e.message}")
        }
    }


    override suspend fun toggleFavourite(productId: String): RequestState<Boolean> {
        return try {
            val uid = getCurrentUserId() ?: return RequestState.Error("User not available.")
            if (productId.isBlank()) return RequestState.Error("Invalid product id.")

            val favDoc = Firebase.firestore
                .collection(CUSTOMER_COLLECTION)
                .document(uid)
                .collection(FAVOURITE_SUBCOLLECTION)
                .document(productId)

            val isFavouriteToggle = Firebase.firestore.runTransaction { trx ->
                val snap = trx.get(favDoc)
                if (snap.exists()) {
                    trx.delete(favDoc)
                    false
                }  else {
                    trx.set(
                        favDoc,
                        mapOf(
                            "productId" to productId,
                            "createdAt" to FieldValue.serverTimestamp(),
                        )
                    )
                    true
                }
            }.await()
            RequestState.Success(isFavouriteToggle)
        } catch (e: Exception){
            RequestState.Error("Failed to toggle favourite: ${e.message}")
        }
    }

    override suspend fun isFavourite(productId: String): RequestState<Boolean> {
        return try {
            val uid = getCurrentUserId() ?: return RequestState.Error("User not available.")
            if (productId.isBlank()) return RequestState.Error("Invalid product id.")

            val isFavDoc = Firebase.firestore
                .collection(CUSTOMER_COLLECTION)
                .document(uid)
                .collection(FAVOURITE_SUBCOLLECTION)
                .document(productId)
                .get()
                .await()


            RequestState.Success(isFavDoc.exists())
        } catch (e: Exception) {
            RequestState.Error("Failed to read favourite state: ${e.message}")
        }
    }
    override fun readBadgeCountFlow(): Flow<RequestState<Int>> = channelFlow {
        try {
            val uid = getCurrentUserId()
            if (uid.isNullOrBlank()){
                send(RequestState.Error("User not available."))
                return@channelFlow
            }
            send(RequestState.Loading)
            Firebase.firestore
                .collection(CUSTOMER_COLLECTION)
                .document(uid)
                .collection(CART_SUBCOLLECTION)
                .snapshots()
                .collectLatest { snapshots ->
                    send(RequestState.Success(snapshots.size()))
                }
        } catch (e: Exception){
            RequestState.Error("Failed to read size items in the cart.: ${e.message}")
        }
    }

    override fun readCartFlow(): Flow<RequestState<List<Cart>>> = callbackFlow {
        val uid = getCurrentUserId()
        if (uid == null) {
            trySend(RequestState.Error("User not available."))
            close()
            return@callbackFlow
        }

        trySend(RequestState.Loading)
        val listener = Firebase.firestore
            .collection(CUSTOMER_COLLECTION)
            .document(uid)
            .collection(CART_SUBCOLLECTION)
            .addSnapshotListener { snapshot, error ->
                if (error != null){
                    trySend(RequestState.Error(error.message ?: "Failed to read cart."))
                    return@addSnapshotListener
                }
                val docs = snapshot?.documents.orEmpty()
                val cart = docs.mapNotNull { documentSnapshot ->
                    val productId = documentSnapshot.getString("productId") ?: documentSnapshot.id
                    val quantity = (documentSnapshot.getLong("quantity") ?: 0L).toInt()
                    if (quantity <= 0) null else Cart(productId = productId, quantity = quantity)
                }
                trySend(RequestState.Success(cart))
            }
        awaitClose { listener.remove() }
    }

    override suspend fun deleteCartItem(productId: String): RequestState<Unit> {
        return try {
            val uid = getCurrentUserId() ?: return RequestState.Error("User not available.")
            if (productId.isBlank()) return RequestState.Error("Invalid product id.")

            Firebase.firestore
                .collection(CUSTOMER_COLLECTION)
                .document(uid)
                .collection(CART_SUBCOLLECTION)
                .document(productId)
                .delete()
                .await()
            RequestState.Success(Unit)
        } catch (e: Exception) {
            RequestState.Error("Failed to delete cart item: ${e.message}")
        }
    }

    override suspend fun setCartQuantity(
        productId: String,
        newQuantity: Int
    ): RequestState<Unit> {
        return try {
            val uid = getCurrentUserId() ?: return RequestState.Error("User not available.")
            if (productId.isBlank()) return RequestState.Error("Invalid product id.")

            val cartQuantity = newQuantity.coerceIn(0, 99)
            val cartDoc = Firebase.firestore
                .collection(CUSTOMER_COLLECTION)
                .document(uid)
                .collection(CART_SUBCOLLECTION)
                .document(productId)

            if (cartQuantity == 0){
                cartDoc.delete()
            } else {
                cartDoc.set(
                    mapOf(
                        "productId" to productId,
                        "quantity" to cartQuantity,
                        "updatedAt" to FieldValue.serverTimestamp()
                    ),
                    SetOptions.merge()
                ).await()
            }
            RequestState.Success(Unit)
        } catch (e: Exception) {
            RequestState.Error("Failed to update cart quantity: ${e.message}")
        }
    }


    override fun readFavouriteIdFlow(): Flow<RequestState<Set<String>>> = channelFlow {
        try {
            val uid = getCurrentUserId()
            if (uid.isNullOrBlank()) {
                send(RequestState.Error("User not available."))
                return@channelFlow
            }
            send(RequestState.Loading)
            Firebase.firestore
                .collection(CUSTOMER_COLLECTION)
                .document(uid)
                .collection(FAVOURITE_SUBCOLLECTION)
                .snapshots()
                .collectLatest { snapshots ->
                    val ids = snapshots.documents.map { it.id }.toSet()
                    send(RequestState.Success(ids))
                }
        } catch (e: Exception) {
            RequestState.Error("Failed to read favourites: ${e.message}")
        }
    }
}
